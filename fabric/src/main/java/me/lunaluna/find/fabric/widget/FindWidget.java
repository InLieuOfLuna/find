package me.lunaluna.find.fabric.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nullable;

public class FindWidget extends TextFieldWidget {

    private static String search = "";

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
}