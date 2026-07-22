package com.aquamancer.cztils.config;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.AbilityIcon;
import com.aquamancer.cztils.hud.TextureInfo;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.util.ActionResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

@Config(name = Cztils.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category("icons")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int grayedOut = 0x80000000;

    @ConfigEntry.Category("specs")
    @SpecConfigs
    public Map<Spec, SpecConfig> specConfigs = new HashMap<>();

    @ConfigEntry.Category("textures")
    @ConfigEntry.Gui.TransitiveObject
    public Textures textures = new Textures();

    public static class Textures {
        public Textures() {}

        @AbilityIconMap
        public Map<Actives, AbilityIcon.Type> actives = new EnumMap<>(Actives.class);
        @AbilityIconMap
        public Map<Passives, AbilityIcon.Type> passives = new EnumMap<>(Passives.class);
        @AbilityIconMap
        public Map<Curse, AbilityIcon.Type> curses = new EnumMap<>(Curse.class);
        @AbilityIconMap
        public Map<Gifts, AbilityIcon.Type> gifts = new EnumMap<>(Gifts.class);

        public AbilityIcon.Type getIconType(Enum<?> ability) {
            if (ability instanceof Actives) {
                return actives.get(ability);
            } else if (ability instanceof Passives) {
                return passives.get(ability);
            } else if (ability instanceof Curse) {
                return curses.get(ability);
            } else if (ability instanceof Gifts) {
                return gifts.get(ability);
            }
            return AbilityIcon.Type.VANILLA;
        }
    }

    public int getBackgroundFill() {
        return 0xFF000000;
    }

    public int getBorderWidth() {
        return 1;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface AbilityIconMap {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SpecConfigs {}
}
