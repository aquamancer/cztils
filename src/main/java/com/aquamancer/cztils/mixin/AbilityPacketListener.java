package com.aquamancer.cztils.mixin;

import com.aquamancer.cztils.lifeline.AbilityPacketHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.payload.RetainedPayload;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
class AbilityPacketListener {
    @Unique
    private static final Identifier CHANNEL_ID = new Identifier("monumenta", "client_channel_v1");

    @Inject(method="onCustomPayload(Lnet/minecraft/network/packet/s2c/common/CustomPayloadS2CPacket;)V", at=@At("HEAD"))
    private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (packet == null) return;
        // fabric api wraps the payloads into RetainedPayload
        if (packet.payload() instanceof RetainedPayload payload) {
            if (payload.id() == null || payload.buf() == null) return;
            if (!payload.id().equals(CHANNEL_ID)) return;
            AbilityPacketHandler.onAbilityPacketReceived(PacketByteBufs.copy(payload.buf()));  // copy to not consume the buffer
        }
    }
}
