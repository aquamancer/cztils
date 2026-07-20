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
    @Override
    public void validatePostLoad() {
        this.textures.updateFlatMap();
    }

    @ConfigEntry.Category("textures")
    @ConfigEntry.Gui.TransitiveObject
    public Textures textures = new Textures();

    private static class Textures {
        public Textures() {}

        @AbilityIconMap
        private EnumMap<Actives.Combo, AbilityIcon.Type> combo = new EnumMap<>(Actives.Combo.class);
        @AbilityIconMap
        private EnumMap<Actives.Right, AbilityIcon.Type> right = new EnumMap<>(Actives.Right.class);
        @AbilityIconMap
        private EnumMap<Actives.LeftShift, AbilityIcon.Type> leftShift = new EnumMap<>(Actives.LeftShift.class);
        @AbilityIconMap
        private EnumMap<Actives.RightShift, AbilityIcon.Type> rightShift = new EnumMap<>(Actives.RightShift.class);
        @AbilityIconMap
        private EnumMap<Actives.Wildcard, AbilityIcon.Type> wildcard = new EnumMap<>(Actives.Wildcard.class);
        @AbilityIconMap
        private EnumMap<Actives.Swap, AbilityIcon.Type> swap = new EnumMap<>(Actives.Swap.class);
        @AbilityIconMap
        private EnumMap<Actives.Lifeline, AbilityIcon.Type> lifeline = new EnumMap<>(Actives.Lifeline.class);
        @AbilityIconMap
        private EnumMap<Passives, AbilityIcon.Type> passives = new EnumMap<>(Passives.class);
        @AbilityIconMap
        private EnumMap<Gifts, AbilityIcon.Type> gifts = new EnumMap<>(Gifts.class);
        @AbilityIconMap
        private EnumMap<Curse, AbilityIcon.Type> curses = new EnumMap<>(Curse.class);

        @ConfigEntry.Gui.Excluded
        public transient Map<Enum<?>, AbilityIcon.Type> flatMap = new HashMap<>();
        private void updateFlatMap() {
            flatMap.putAll(combo);
            flatMap.putAll(right);
            flatMap.putAll(leftShift);
            flatMap.putAll(rightShift);
            flatMap.putAll(wildcard);
            flatMap.putAll(swap);
            flatMap.putAll(lifeline);
            flatMap.putAll(passives);
            flatMap.putAll(gifts);
            flatMap.putAll(curses);
        }
    }

    public void updateFlatMap() {
        textures.updateFlatMap();
    }

    public AbilityIcon.Type getIconType(Enum<?> ability) {
        AbilityIcon.Type result = textures.flatMap.get(ability);
        return (result == null) ? AbilityIcon.Type.VANILLA : result;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface AbilityIconMap {}
}
