package me.lunaluna.find.fabric.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.Strings;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class FindWidget extends TextFieldWidget {

    public static String search = "";

    public FindWidget(int x, int y) {
        super(MinecraftClient.getInstance().textRenderer, x, y, 174, 18, Text.empty());
        setText(search);
        setChangedListener(string -> search = string);
    }

    public boolean matches(@Nullable ItemStack item) {
        String text = getText();
        if (Strings.isBlank(text)) return true;
        if(item == null) return false;
        return item.getName().getString().toLowerCase().contains(text.toLowerCase());
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (visible()) super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean visible() {
        return isFocused() || Strings.isNotBlank(search);
    }
}