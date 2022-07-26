package me.lunaluna.find.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Startup implements ClientModInitializer {
    public static KeyBinding toggle;

    @Override
    public void onInitializeClient() {
        toggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.find.toggle",
                GLFW.GLFW_KEY_F,
                "category.find"
        ));
    }
}
