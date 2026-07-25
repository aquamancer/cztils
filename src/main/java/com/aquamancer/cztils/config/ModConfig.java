package com.aquamancer.cztils.config;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.AbilityIcon;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

@Config(name = Cztils.MOD_ID)
public class ModConfig implements ConfigData {
    public ModConfig() {}

    @ConfigEntry.Category("icons")
    public int iconSize = ConfigDefaults.grayedOutColor;

    @ConfigEntry.Category("icons")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int grayedOut = ConfigDefaults.grayedOutColor;

    @ConfigEntry.Category("icons")
    public int borderWidth = ConfigDefaults.borderWidth;

    public boolean showSelf = ConfigDefaults.showSelf;

    @ConfigEntry.Category("specs")
    @SpecConfigs
    public Map<Spec, SpecConfig> specConfigs = ConfigDefaults.createDefaultSpecConfigs();

    @ConfigEntry.Category("textures")
    @ConfigEntry.Gui.TransitiveObject
    public Textures textures = ConfigDefaults.createDefaultTextures();

    public static class Textures {
        public Textures() {}

        @AbilityIconMap
        public Map<Actives, AbilityIcon.Type> actives;
        @AbilityIconMap
        public Map<Passives, AbilityIcon.Type> passives;
        @AbilityIconMap
        public Map<Curse, AbilityIcon.Type> curses;
        @AbilityIconMap
        public Map<Gifts, AbilityIcon.Type> gifts;

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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface AbilityIconMap {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SpecConfigs {}
}
