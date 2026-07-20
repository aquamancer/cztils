package com.aquamancer.cztils.config;

import com.aquamancer.czlib.api.abils.Actives;
import com.aquamancer.czlib.api.abils.Curse;
import com.aquamancer.czlib.api.abils.Gifts;
import com.aquamancer.czlib.api.abils.Passives;
import com.aquamancer.cztils.Cztils;
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Config(name = Cztils.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Category("icons")
    @ConfigEntry.ColorPicker
    public int grayedOut = 0x80000000;

    @ConfigEntry.Category("textures")
    @ConfigEntry.Gui.TransitiveObject
    public Textures textures = new Textures();

    private static class Textures {
        public Textures() {}

        @AbilityIconMap
        private EnumMap<Actives, AbilityIcon.Type> actives = new EnumMap<>(Actives.class);
        @AbilityIconMap
        private EnumMap<Passives, AbilityIcon.Type> passives = new EnumMap<>(Passives.class);
        @AbilityIconMap
        private EnumMap<Curse, AbilityIcon.Type> curses = new EnumMap<>(Curse.class);
        @AbilityIconMap
        private EnumMap<Gifts, AbilityIcon.Type> gifts = new EnumMap<>(Gifts.class);

        private AbilityIcon.Type getIconType(Enum<?> ability) {
            if (ability instanceof Actives) {
                return actives.get(ability);
            } else if (ability instanceof Passives) {
                return passives.get(ability);
            } else if (ability instanceof Curse) {
                return curses.get(ability);
            } else if (ability instanceof Gifts) {
                return gifts.get(ability);
            }
            return null;
        }
    }

    public AbilityIcon.Type getIconType(Enum<?> ability) {
        AbilityIcon.Type result = textures.getIconType(ability);
        return (result == null) ? AbilityIcon.Type.VANILLA : result;
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
}
