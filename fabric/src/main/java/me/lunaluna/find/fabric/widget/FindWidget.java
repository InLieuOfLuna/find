package me.lunaluna.find.fabric.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.potion.Potion;
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
            if (! string.contains(token))
                return false;
        return true;
    }

    public boolean matches(@Nullable ItemStack stack) {
        try {
            String text = getText();
            if (Strings.isBlank(text)) return true;
            if (stack == null)
                return false;

            Item item = stack.getItem();
            if (matchString(item.getName().getString()))
                return true;

            NbtCompound nbt = stack.getNbt();
            if (nbt == null)
                return false;

            for (String key : nbt.getKeys()) {
                switch (key) {
                    case "StoredEnchantments":
                    case "Enchantments":
                        NbtList enchantmentTag = stack.getNbt().getList(key, 10);
                        for (int i = 0; i < enchantmentTag.size(); i++) {
                            NbtCompound nbtTag = enchantmentTag.getCompound(i);
                            String enchantmentName = nbtTag.getString("id");

                            // Compare the enchantment name to the search text
                            if (matchString(enchantmentName)) {
                                return true;
                            }
                        }
                        break;
                    case "Potion":
                        NbtElement potionTag = nbt.get(key);
                        if (potionTag == null)
                            break;
                        if (matchString(potionTag.asString()))
                            return true;

                        Potion potion = Potion.byId(potionTag.asString());
                        for (StatusEffectInstance effectInstance : potion.getEffects()) {
                            if (matchString(effectInstance.getEffectType().getName().getString()))
                                return true;
                        }

                        break;
                    case "BlockEntityTag":
                        NbtCompound blockEntityTag = stack.getSubNbt("BlockEntityTag");
                        if (blockEntityTag != null && blockEntityTag.contains("Items", 9)) {
                            NbtList itemList = blockEntityTag.getList("Items", 10);
                            if (itemList == null)
                                break;

                            for (int i = 0, len = itemList.size(); i < len; ++i) {
                                ItemStack s = ItemStack.fromNbt(itemList.getCompound(i));
                                if (matches(s))
                                    return true;
                            }

                        }
                        break;
                    default:
                        // System.out.println(key + ": " + nbt.get(key).toString());
                }

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible() || !isVisible()) return false;  // Disables mouse clicking when not visible
        boolean inTextBox = mouseX >= (double)this.getX() && mouseX < (double)(this.getX() + this.width) && mouseY >= (double)this.getY() && mouseY < (double)(this.getY() + this.height);
        if (inTextBox && button == GLFW.GLFW_MOUSE_BUTTON_2) {
            setText("");  // Clears text on right click
            return super.mouseClicked(mouseX, mouseY, GLFW.GLFW_MOUSE_BUTTON_1);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        if (visible()) super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean visible() {
        return isFocused() || Strings.isNotBlank(search);
    }
}
