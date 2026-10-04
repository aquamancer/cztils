package com.aquamancer.cztils.mixin;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.lifeline.LifelineManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InGameHud.class)
public class HealthBarMixin {
    @ModifyVariable(
            method="Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V",
            at=@At("STORE"),
            ordinal=1
    )
    private boolean modifyHardcore(boolean originalValue) {
        PartyMember self = ZenithApi.getInstance().getSelf().orElse(null);
        ModConfig.HardcoreHeartsEntry config = Cztils.config.hardcoreHearts;
        if (
                self != null
                && config.enabled
                && self.getGraveTimer() <= config.hardcoreGraveThreshold
                && (config.showIfLifelineUp || !LifelineManager.isLifelineUp())
        ) {
            return true;
        } else {
            return originalValue;
        }
    }

    @Inject(
            method="Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V",
            at=@At(
                    value="INVOKE",
                    target="Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIZZZ)V",
                    ordinal=3,  // on rendering of the player's current health (red hearts)
                    shift=At.Shift.AFTER
            ),
            locals=LocalCapture.CAPTURE_FAILHARD
    )
    private void renderLifelineHearts(
            DrawContext context,
            PlayerEntity player,
            int x,
            int y,
            int rowHeight,
            int regeneratingHeartIndex,
            float maxHealth,
            int currentHealth,
            int prevHealth,
            int absorption,
            boolean blinking,
            CallbackInfo ci,

            @Coerce Object heartType,
            boolean hardcore,
            int heartContainers,
            int absorptionContainers,
            int halfHeartContainers,
            int heartIndex,
            int row,
            int col,
            int heartX,
            int heartY,
            int heartHealth,
            boolean isAbsorptionHeart,
            boolean isHalfHeart
    ){
        if (!Cztils.config.lifelineIndicatorEnabled) return;
        if (blinking) return;
        int lifelineHealthThreshold = LifelineManager.getHealthThreshold(maxHealth);
        if (lifelineHealthThreshold <= heartHealth) return;
        boolean isLifelineHalfHeart = isHalfHeart || heartHealth + 1 == lifelineHealthThreshold;
        Identifier heartTexture = LifelineManager.getHeartOverlayTexture(isLifelineHalfHeart);
        if (heartTexture == null) return;
        context.drawGuiTexture(heartTexture, heartX, heartY, 9, 9);
    }
}
