package com.aquamancer.cztils.lifeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

/**
 * Adapted from <a href="https://github.com/Njol/UnofficialMonumentaMod">Unofficial Monumenta Mod by Njol</a>
 */
public class AbilityPacketHandler {
    private static final Gson gson = new GsonBuilder().create();

    /** Sent when the player's class is updated or silence expires */
    public static class ClassUpdatePacket {
        String _type = "ClassUpdatePacket";
        AbilityInfo[] abilities;

        public static class AbilityInfo {
            public String name;
            public String className;

            int remainingCooldown;
            int initialCooldown;

            int remainingCharges;
            int maxCharges;

            @Nullable String mode;
            @Nullable Integer remainingDuration;
            @Nullable Integer initialDuration;
        }
    }

    /** Sent when an ability is used or comes off cooldown */
    public static class AbilityUpdatePacket {
        String _type = "AbilityUpdatePacket";
        public String name;
        int remainingCooldown;
        int remainingCharges;

        @Nullable String mode;
        @Nullable Integer remainingDuration;
        @Nullable Integer initialDuration;
    }

    /** Sent when the player is silenced */
    public static class PlayerStatusPacket {
        String _type = "PlayerStatusPacket";
        int silenceDuration;
    }

    public static void onAbilityPacketReceived(PacketByteBuf buf) {
        String message = buf.readCharSequence(buf.readableBytes(), StandardCharsets.UTF_8).toString();
        JsonElement json = JsonParser.parseString(message);

        MinecraftClient.getInstance().execute(() -> {
            String packetType = json.getAsJsonObject().getAsJsonPrimitive("_type").getAsString();
            switch (packetType) {
                case "ClassUpdatePacket" -> {
                    ClassUpdatePacket packet = gson.fromJson(json, ClassUpdatePacket.class);
                    LifelineManager.onClassUpdatePacket(packet.abilities);
                }
                case "AbilityUpdatePacket" -> {
                    AbilityUpdatePacket packet = gson.fromJson(json, AbilityUpdatePacket.class);
                    LifelineManager.onAbilityUpdatePacket(packet);
                }
                case "PlayerStatusPacket" -> {
                    PlayerStatusPacket packet = gson.fromJson(json, PlayerStatusPacket.class);
                    LifelineManager.onSilenced(packet);
                }
            }
        });
    }
}
