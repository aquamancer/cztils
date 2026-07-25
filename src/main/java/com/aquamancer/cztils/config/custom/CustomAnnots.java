package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.Spec;
import com.aquamancer.cztils.config.ConfigDefaults;
import com.aquamancer.cztils.config.ModConfig;
import com.aquamancer.cztils.hud.AbilityIcon;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
                            }

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

                            List<AbstractConfigListEntry> specs = new ArrayList<>();
                            for (Map.Entry<Spec, SpecConfig> pair : values.entrySet()) {
                                Spec spec = pair.getKey();
                                if (spec == null) continue;
                                SpecConfig specConfig = pair.getValue();

                                List<AbstractConfigListEntry> children = new ArrayList<>();
                                children.add(builder.startStrField(Text.literal("Display name"), specConfig.name).setSaveConsumer(s -> specConfig.name = s).build());
                                children.add(CustomEntries.teammatePriorityEntry("Teammate spec order", specConfig));

                                children.add(CustomEntries.abilitySpecPriorityEntry("Ability spec order", specConfig));
                                children.add(CustomEntries.activeSlotPriorityEntry("Active slot order", specConfig));
                                children.add(CustomEntries.activeSortEntry("Active sort order", specConfig));
                                children.add(CustomEntries.passiveSortEntry("Passive sort order", specConfig));
                                children.add(CustomEntries.enumListEntry("Always shown icons", specConfig.alwaysShow, specConfig::updateEnumSets, new ArrayList<>(ConfigDefaults.alwaysShow.get(spec))));
                                children.add(CustomEntries.enumListEntry("Always shown icons if has spec", specConfig.showIfHasSpec, specConfig::updateEnumSets, new ArrayList<>(ConfigDefaults.showIfHasSpec.get(spec))));
                                children.add(CustomEntries.iconListDropdownEntry("Icons to show", specConfig, spec));

                                specs.add(builder.startSubCategory(Text.literal(spec.name()), children).build());
                            }
                            return specs;
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
}
