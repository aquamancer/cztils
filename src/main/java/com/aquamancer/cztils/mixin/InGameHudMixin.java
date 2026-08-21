package com.aquamancer.cztils.mixin;

import com.aquamancer.cztils.Cztils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.BEFORE))
    private void renderBehindChat(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (Cztils.config.renderOverChat) return;
        if (!Cztils.config.hudEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof HandledScreen<?>) return;

        if (client.options.playerListKey.isPressed()) {
            Cztils.hud.render(context, Cztils.config.tabPressedRenderMode);
        } else {
            Cztils.hud.render(context, Cztils.config.inGameRenderMode);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/scoreboard/Scoreboard;getObjectiveForSlot(Lnet/minecraft/scoreboard/ScoreboardDisplaySlot;)Lnet/minecraft/scoreboard/ScoreboardObjective;", shift = At.Shift.BEFORE),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V")))
    private void renderOverChat(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (!Cztils.config.renderOverChat) return;
        if (!Cztils.config.hudEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof HandledScreen<?>) return;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(0f, 0f, 200f);
        if (client.options.playerListKey.isPressed()) {
            Cztils.hud.render(context, Cztils.config.tabPressedRenderMode);
        } else {
            Cztils.hud.render(context, Cztils.config.inGameRenderMode);
        }
        matrices.pop();
    }
}
