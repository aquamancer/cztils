package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.Spec;
import com.aquamancer.cztils.config.ConfigDefaults;
import com.aquamancer.cztils.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CustomAnnots {
    public static void init() {
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
                            for (Map.Entry<Spec, SpecConfig> pair : values.entrySet().stream().sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator
                                    .naturalOrder()))).toList()) {
                                Spec spec = pair.getKey();
                                if (spec == null) continue;
                                SpecConfig specConfig = pair.getValue();

                                List<AbstractConfigListEntry> children = new ArrayList<>();
                                children.add(builder
                                        .startColorField(Text.translatable("text.autoconfig.cztils.option.specConfig.nameColor"), specConfig.nameColor)
                                        .setDefaultValue(ConfigDefaults.nameColors.get(spec))
                                        .setSaveConsumer(c -> specConfig.nameColor = c)
                                        .build()
                                );
                                children.add(builder
                                        .startStrField(Text.translatable("text.autoconfig.cztils.option.specConfig.name"), specConfig.name)
                                        .setDefaultValue(ConfigDefaults.names.get(spec))
                                        .setSaveConsumer(s -> specConfig.name = s)
                                        .build()
                                );
                                children.add(builder.startColorField(Text.translatable("text.autoconfig.cztils.option.specConfig.specColor"), specConfig.specColor)
                                        .setSaveConsumer(c -> specConfig.specColor = c)
                                        .setDefaultValue(ConfigDefaults.specColors.get(spec))
                                        .build()
                                );
                                children.add(CustomEntries.teammatePriorityEntry(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.teammatePriority"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.teammatePriority.@Tooltip"),
                                        specConfig
                                        )
                                );
                                children.add(CustomEntries.abilitySpecPriorityEntry(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.abilitySpecPriority"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.abilitySpecPriority.@Tooltip"),
                                        specConfig
                                        )
                                );
                                children.add(CustomEntries.slotPriority(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.slotPriority"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.slotPriority.@Tooltip"),
                                        specConfig
                                        )
                                );
                                children.add(CustomEntries.activeSortEntry(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.activeSort"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.activeSort.@Tooltip"),
                                        specConfig
                                        )
                                );
                                children.add(CustomEntries.passiveSortEntry(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.passiveSort"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.passiveSort.@Tooltip"),
                                        specConfig
                                        )
                                );
                                children.add(CustomEntries.enumListEntry(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.alwaysShow"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.alwaysShow.@Tooltip"),
                                        specConfig.alwaysShow,
                                        specConfig::updateEnumSets,
                                        new ArrayList<>(ConfigDefaults.alwaysShow.get(spec))
                                        )
                                );
                                children.add(CustomEntries.enumListEntry(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.showIfSpec"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.showIfSpec.@Tooltip"),
                                        specConfig.showIfHasSpec,
                                        specConfig::updateEnumSets,
                                        new ArrayList<>(ConfigDefaults.showIfHasSpec.get(spec))
                                        )
                                );
                                children.add(CustomEntries.iconListDropdownEntry(
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.iconsShown"),
                                        Text.translatable("text.autoconfig.cztils.option.specConfig.iconsShown.@Tooltip"),
                                        specConfig,
                                        spec
                                        )
                                );

                                specs.add(builder.startSubCategory(Text.literal(spec.getDisplayName()), children).build());
                            }
                            return specs;
                        },
                        ModConfig.SpecConfigs.class
                );
    }
}
