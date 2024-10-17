package me.lunaluna.find.fabric.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class FindWidget extends TextFieldWidget {

    public static String search = "";

    public FindWidget(int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x, y, 174, 18, Text.empty());
        setText(search);
        setChangedListener(string -> search = string);
    }

    private boolean matchString(String string) {
        String text = getText().toLowerCase();
        string = string.toLowerCase();

        for (String token : text.split(" "))
            if (!string.contains(token))
                return false;
        return true;
    }

    public boolean matches(@Nullable ItemStack stack) {
        String text = getText();
        if (Strings.isBlank(text))
            return true;
        if (stack == null)
            return false;

        Item item = stack.getItem();
        if (matchString(item.getName().getString()))
            return true;

        ComponentMap components = stack.getComponents();
        if (components == null)
            return false;

        if (components.contains(DataComponentTypes.ENCHANTMENTS)) {
            ItemEnchantmentsComponent enchantments = components.get(DataComponentTypes.ENCHANTMENTS);
            for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                String enchantmentName = Enchantment.getName(enchantment, enchantments.getLevel(enchantment))
                        .getString();

                if (matchString(enchantmentName)) {
                    return true;
                }
            }
        }
        if (components.contains(DataComponentTypes.POTION_CONTENTS)) {
            PotionContentsComponent potionContents = components.get(DataComponentTypes.POTION_CONTENTS);
            for (StatusEffectInstance effect : potionContents.getEffects()) {
                if (matchString(effect.getEffectType().value().getName().getString()))
                    return true;
            }
        }
        if (components.contains(DataComponentTypes.CONTAINER)) {
            ContainerComponent container = components.get(DataComponentTypes.CONTAINER);
            for (ItemStack containedItem : container.iterateNonEmpty()) {
                if (matches(containedItem))
                    return true;
            }

        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible() || !isVisible())
            return false; // Disables mouse clicking when not visible
        boolean inTextBox = mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width)
                && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
        if (inTextBox && button == GLFW.GLFW_MOUSE_BUTTON_2) {
            setText(""); // Clears text on right click
            return super.mouseClicked(mouseX, mouseY, GLFW.GLFW_MOUSE_BUTTON_1);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        if (visible())
            super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean visible() {
        return isFocused() || Strings.isNotBlank(search);
    }
}
