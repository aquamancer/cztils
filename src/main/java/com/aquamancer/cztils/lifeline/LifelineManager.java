package com.aquamancer.cztils.lifeline;

import com.aquamancer.czlib.api.abils.Actives;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class LifelineManager {
    // maps lifeline to hp activation threshold
    private static final Map<Actives, Double> lifelines = new EnumMap<>(Map.of(
            Actives.APOCALYPSE, 0.25,
            Actives.CRYOBOX, 0.25,
            Actives.ESCAPE_ARTIST, 0.30,
            Actives.ETERNAL_SAVIOR, 0.20,
            Actives.LAST_BREATH, 0.40,
            Actives.STEEL_STALLION, 0.25
    ));

    private static final Map<Actives, Identifier> fullHearts = new EnumMap<>(Map.of(
            Actives.APOCALYPSE, new Identifier("cztils", "hud/heart/apocalypse"),
            Actives.CRYOBOX, new Identifier("cztils", "hud/heart/cryobox"),
            Actives.ESCAPE_ARTIST, new Identifier("cztils", "hud/heart/escape_artist"),
            Actives.ETERNAL_SAVIOR, new Identifier("cztils", "hud/heart/eternal_savior"),
            Actives.LAST_BREATH, new Identifier("cztils", "hud/heart/last_breath"),
            Actives.STEEL_STALLION, new Identifier("cztils", "hud/heart/steel_stallion")
    ));

    private static final Map<Actives, Identifier> halfHearts = new EnumMap<>(Map.of(
            Actives.APOCALYPSE, new Identifier("cztils", "hud/heart/apocalypse_half"),
            Actives.CRYOBOX, new Identifier("cztils", "hud/heart/cryobox_half"),
            Actives.ESCAPE_ARTIST, new Identifier("cztils", "hud/heart/escape_artist_half"),
            Actives.ETERNAL_SAVIOR, new Identifier("cztils", "hud/heart/eternal_savior_half"),
            Actives.LAST_BREATH, new Identifier("cztils", "hud/heart/last_breath_half"),
            Actives.STEEL_STALLION, new Identifier("cztils", "hud/heart/steel_stallion_half")
    ));

    private static @Nullable Actives readyLifeline = null;
    private static boolean silenced = false;

    public static void onClassUpdatePacket(AbilityPacketHandler.ClassUpdatePacket.AbilityInfo[] abilities) {
        silenced = false;  // sent when silence from stasis ends
        readyLifeline = null;
        for (AbilityPacketHandler.ClassUpdatePacket.AbilityInfo ability : abilities) {
            updateLifeline(ability.name, ability.remainingCooldown);
        }
    }

    public static void onAbilityUpdatePacket(AbilityPacketHandler.AbilityUpdatePacket packet) {
        updateLifeline(packet.name, packet.remainingCooldown);
    }

    public static void onSilenced(AbilityPacketHandler.PlayerStatusPacket packet) {
        // silenceDuration = 0 is sent when non-stasis silence ends
        // the server has a bug: if a non-stasis silence B occurs while silenced by a long non-stasis silence A,
        // the server will send a packet silenceDuration = 0 when B expires despite still being silenced by A.
        // this doesn't handle the bug for now
        silenced = packet.silenceDuration > 0;
    }

    private static void updateLifeline(String ability, int remainingCooldown) {
        Actives active = Actives.fromString(ability).orElse(null);
        if (active == null) return;
        if (!lifelines.containsKey(active)) return;

        if (remainingCooldown <= 0) {
            readyLifeline = active;
        } else {
            readyLifeline = null;
        }
    }

    public static boolean isLifelineUp() {
        return readyLifeline != null && !silenced;
    }

    public static int getHealthThreshold(float maxHealth) {
        if (readyLifeline == null) return -1;
        return MathHelper.ceil(maxHealth * lifelines.get(readyLifeline));
    }

    @Nullable
    public static Identifier getHeartOverlayTexture(boolean half) {
        if (readyLifeline == null) return null;
        return half ? halfHearts.get(readyLifeline) : fullHearts.get(readyLifeline);
    }
}
