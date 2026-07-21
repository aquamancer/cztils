package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.Actives;
import com.aquamancer.czlib.api.abils.Spec;
import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.hud.AbilityIcon;
import com.aquamancer.cztils.hud.TextureInfo;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CustomAnnots {
    public static void init() {
        AutoConfig.getGuiRegistry(ModConfig.class)
                .registerAnnotationProvider(
                        (fieldName, fieldAccessor, currentValue, defaultValue, reg) -> {
                            fieldAccessor.setAccessible(true);
                            Map<Enum<?>, AbilityIcon.Type> values;
                            try {
                                values = (Map<Enum<?>, AbilityIcon.Type>) fieldAccessor.get(currentValue);
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }

                            Class<? extends Enum<?>> keyType = getMapKeyType(fieldAccessor);
                            String dropdownName = "null";
                            if (keyType != null) {
                                dropdownName = keyType.getSimpleName();
                                fillDefaults(values, keyType);
                            }

                            ConfigEntryBuilder builder = ConfigEntryBuilder.create();

                            List<AbstractConfigListEntry> result = values.entrySet().stream().map(entry -> CustomEntries.textureEntry(entry.getKey(), values)).toList();
                            return List.of(ConfigEntryBuilder.create().startSubCategory(Text.literal(dropdownName), result).build());
                        },
                        ModConfig.AbilityIconMap.class
                );

        AutoConfig.getGuiRegistry(ModConfig.class)
                .registerAnnotationProvider(
                        (fieldName, fieldAccessor, currentValue, defaultValue, reg) -> {
                            fieldAccessor.setAccessible(true);
                            Map<Spec, SpecConfig> values;
                            try {
                                values = (Map<Spec, SpecConfig>) fieldAccessor.get(currentValue);
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }

                            ConfigEntryBuilder builder = ConfigEntryBuilder.create();
                            for (Map.Entry<Spec, SpecConfig> pair : values.entrySet()) {
                                Spec spec = pair.getKey();
                                SpecConfig specConfig = pair.getValue();

                                SpecConfig.fillDefaults(spec, specConfig);

                                List<AbstractConfigListEntry> result = new ArrayList<>();
                                result.add(CustomEntries.teammatePriorityEntry("Teammate Spec Order", specConfig));
                                result.add(CustomEntries.abilitySpecPriorityEntry("Ability Spec Order", specConfig));
                                result.add(CustomEntries.activeSlotPriorityEntry("Active Slot Order", specConfig));
                                result.add(CustomEntries.activeSortEntry("Active Sort Order", specConfig));
                                result.add(CustomEntries.passiveSortEntry("Passive Sort Order", specConfig));
                                result.add(builder.startStrList("Always Show Icons", specConfig.alwaysShow.stream().toList()))





                            }


                            List<AbstractConfigListEntry> result = values.entrySet().stream().map(entry -> CustomEntries.textureEntry(entry.getKey(), values)).toList();
                            return List.of(ConfigEntryBuilder.create().startSubCategory(Text.literal(dropdownName), result).build());
                        },
                        ModConfig.SpecConfigs.class
                );
    }

    private static Class<? extends Enum<?>> getMapKeyType(Field field) {
        Type type = field.getGenericType();

        if (type instanceof ParameterizedType pt) {
            Type keyType = pt.getActualTypeArguments()[0];

            if (keyType instanceof Class<?> clazz && clazz.isEnum()) {
                return (Class<? extends Enum<?>>) clazz.asSubclass(Enum.class);
            }
        }

        return null;
    }

    private static void fillDefaults(Map<Enum<?>, AbilityIcon.Type> map, Class<? extends Enum<?>> enumClass) {
        for (Enum<?> key : enumClass.getEnumConstants()) {
            map.putIfAbsent(key, AbilityIcon.Type.VANILLA);
        }
    }
}
