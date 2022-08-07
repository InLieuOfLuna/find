package me.lunaluna.find.fabric.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import me.lunaluna.find.fabric.Startup;
import me.lunaluna.find.fabric.config.Config;
import me.lunaluna.find.fabric.widget.FindWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin extends Screen {
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundHeight;

    protected HandledScreenMixin(Text title) { super(title); }  // Never called just to avoid compile errors
    private static final boolean HAS_BORDER = true;
    private FindWidget widget;

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CallbackInfo ci) {
        widget = new FindWidget(x + 1, y + backgroundHeight + 4);
        addSelectableChild(widget);
    }

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    private void disableFurtherProcessing(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (widget.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                focusOn(null);
                widget.changeFocus(true);
            }
            widget.keyPressed(keyCode, scanCode, modifiers);
            cir.setReturnValue(true);
        } else if (Startup.toggle.matchesKey(keyCode, scanCode) && modifiers == GLFW.GLFW_MOD_CONTROL) {
            focusOn(widget);
            widget.changeFocus(true);
            widget.setSelectionStart(0);
            widget.setSelectionEnd(widget.getText().length());
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("TAIL"), method = "drawSlot")
    private void darkenNonMatching(MatrixStack matrices, Slot slot, CallbackInfo ci) {
        if (!widget.matches(slot.getStack())) {
            darkenSlot(matrices, slot.x, slot.y);
        }
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void renderSearch(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        widget.render(matrices, mouseX, mouseY, delta);
    }
    private void darkenSlot(MatrixStack matrices, int x, int y) {
        var color = Config.INSTANCE.color().getRGB();
        var border = HAS_BORDER ? 1 : 0;

        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(matrices, x - border, y - border, x + 16 + border, y + 16 + border, color, color, getZOffset());
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }
}