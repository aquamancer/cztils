package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.config.ConfigDefaults;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.*;

public class CustomEntries {
    public static AbstractConfigListEntry teammatePriorityEntry(Text name, Text tooltip, SpecConfig config) {
        List<AbstractConfigListEntry> inputs = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (Spec spec : Spec.values()) {
            AbstractConfigListEntry input = builder
                    .startIntField(Text.literal(spec.getDisplayName()), config.teammatePriority.get(spec))
                    .setSaveConsumer(v -> config.teammatePriority.put(spec, v))
                    .build();
            inputs.add(input);
        }
        return builder.startSubCategory(name, inputs).setTooltip(tooltip).build();
    }

    public static AbstractConfigListEntry abilitySpecPriorityEntry(Text name, Text tooltip, SpecConfig config) {
        List<AbstractConfigListEntry> inputs = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (AbilitySpec spec : AbilitySpec.values()) {
            AbstractConfigListEntry input = builder
                    .startIntField(Text.literal(spec.name()), config.specPriority.get(spec))
                    .setSaveConsumer(v -> config.specPriority.put(spec, v))
                    .build();
            inputs.add(input);
        }
        return builder.startSubCategory(name, inputs).setTooltip(tooltip).build();
    }

    public static AbstractConfigListEntry slotPriority(Text name, Text tooltip, SpecConfig config) {
        List<AbstractConfigListEntry> inputs = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (ActiveSlot slot : ActiveSlot.values()) {
            AbstractConfigListEntry input = builder
                    .startIntField(Text.literal(slot.name()), config.slotPriority.get(slot))
                    .setSaveConsumer(v -> config.slotPriority.put(slot, v))
                    .build();
            inputs.add(input);
        }
        return builder.startSubCategory(name, inputs).setTooltip(tooltip).build();
    }

    public static AbstractConfigListEntry activeSortEntry(Text name, Text tooltip, SpecConfig config) {
        List<AbstractConfigListEntry> sortOrder = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (int i = 0; i < SpecConfig.ActiveSorters.values().length - 1; i++) {
            int finalI = i;
            String description = (i == 0) ? "Sort by" : "then by";
            AbstractConfigListEntry entry = builder.startEnumSelector(Text.literal(description), SpecConfig.ActiveSorters.class, config.activeSortOrder.get(i))
                    .setSaveConsumer(e -> config.activeSortOrder.set(finalI, e))
                    .build();
            sortOrder.add(entry);
        }
        return builder.startSubCategory(name, sortOrder).setTooltip(tooltip).build();
    }

    public static AbstractConfigListEntry passiveSortEntry(Text name, Text tooltip, SpecConfig config) {
        List<AbstractConfigListEntry> sortOrder = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (int i = 0; i < SpecConfig.PassiveSorters.values().length - 1; i++) {
            int finalI = i;
            String description = (i == 0) ? "Sort by" : "then by";
            AbstractConfigListEntry entry = builder.startEnumSelector(Text.literal(description), SpecConfig.PassiveSorters.class, config.passiveSortOrder.get(i))
                    .setSaveConsumer(e -> config.passiveSortOrder.set(finalI, e))
                    .build();
            sortOrder.add(entry);
        }
        return builder.startSubCategory(name, sortOrder).setTooltip(tooltip).build();
    }

    public static AbstractConfigListEntry enumListEntry(Text name, Text tooltip, List<String> configRef, Runnable setUpdateCallback, List<String> defaultValue) {
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        return builder.startStrList(name, configRef)
                .setSaveConsumer(updated -> {
                    configRef.clear();
                    updated.forEach(e -> {
                        Optional<Enum<?>> ability = AbilityUtils.fromString(e);
                        if (ability.isPresent()) {
                            configRef.add(e);
                        }
                    });
                    setUpdateCallback.run();
                })
                .setDefaultValue(defaultValue)
                .setTooltip(tooltip)
                .build();
    }

    public static AbstractConfigListEntry iconListDropdownEntry(Text name, Text tooltip, SpecConfig configRef, Spec spec) {
        List<AbstractConfigListEntry> children = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        children.add(builder.startEnumSelector(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.activeMode"), SpecConfig.IconListMode.class, configRef.activeListMode)
                .setDefaultValue(ConfigDefaults.activeListMode.get(spec))
                .setSaveConsumer(mode -> {
                    configRef.activeListMode = mode;
                    configRef.updateEnumSets();
                }).build());
        children.add(enumListEntry(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.actives"), Text.empty(), configRef.activeList, configRef::updateEnumSets, new ArrayList<>(ConfigDefaults.activeList.get(spec))));

        children.add(builder.startEnumSelector(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.passiveMode"), SpecConfig.IconListMode.class, configRef.passiveListMode)
                .setDefaultValue(ConfigDefaults.passiveListMode.get(spec))
                .setSaveConsumer(mode -> {
                    configRef.passiveListMode = mode;
                    configRef.updateEnumSets();
                }).build());
        children.add(enumListEntry(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.passives"), Text.empty(), configRef.passiveList, configRef::updateEnumSets, new ArrayList<>(ConfigDefaults.passiveList.get(spec))));

        children.add(builder.startEnumSelector(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.giftMode"), SpecConfig.IconListMode.class, configRef.giftListMode)
                .setDefaultValue(ConfigDefaults.giftListMode.get(spec))
                .setSaveConsumer(mode -> {
                    configRef.giftListMode = mode;
                    configRef.updateEnumSets();
                }).build());
        children.add(enumListEntry(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.gifts"), Text.empty(), configRef.giftList, configRef::updateEnumSets, new ArrayList<>(ConfigDefaults.giftList.get(spec))));

        children.add(builder.startEnumSelector(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.curseMode"), SpecConfig.IconListMode.class, configRef.curseListMode)
                .setDefaultValue(ConfigDefaults.curseListMode.get(spec))
                .setSaveConsumer(mode -> {
                    configRef.curseListMode = mode;
                    configRef.updateEnumSets();
                }).build());
        children.add(enumListEntry(Text.translatable("text.autoconfig.cztils.option.specConfig.iconList.curses"), Text.empty(), configRef.curseList, configRef::updateEnumSets, new ArrayList<>(ConfigDefaults.curseList.get(spec))));

        return builder.startSubCategory(name, children).setTooltip(tooltip).build();
    }
}
