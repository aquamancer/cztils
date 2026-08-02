package com.aquamancer.cztils.mixin;

import com.aquamancer.cztils.Cztils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class ScreenMixin {
    @Inject(at = @At("TAIL"), method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V")
    private void onRender(DrawContext context, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
        Cztils.hud.render(context, Cztils.config.inventoryRenderMode);
    }
}
