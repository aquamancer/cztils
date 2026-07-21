package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.AbilitySpec;
import com.aquamancer.czlib.api.abils.ActiveSlot;
import com.aquamancer.czlib.api.abils.Spec;
import com.aquamancer.cztils.hud.AbilityIcon;
import com.aquamancer.cztils.hud.TextureInfo;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.*;

public class CustomEntries {
    public static AbstractConfigListEntry textureEntry(String name, TextureInfo info) {
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        List<AbstractConfigListEntry> entries = List.of(
                builder.startStrField(Text.literal("Identifier"), info.getIdentifier())
                        .setSaveConsumer(id -> info.setIdentifier(id)).build(),
                builder.startIntField(Text.literal("u"), info.getU())
                        .setSaveConsumer(v -> info.setU(v)).build(),
                builder.startIntField(Text.literal("v"), info.getV())
                        .setSaveConsumer(v -> info.setV(v)).build(),
                builder.startIntField(Text.literal("uw"), info.getUw())
                        .setSaveConsumer(v -> info.setUw(v)).build(),
                builder.startIntField(Text.literal("uh"), info.getUh())
                        .setSaveConsumer(v -> info.setUh(v)).build(),
                builder.startIntField(Text.literal("sourceWidth"), info.getSourceWidth())
                        .setSaveConsumer(v -> info.setSourceWidth(v)).build(),
                builder.startIntField(Text.literal("sourceHeight"), info.getSourceHeight())
                        .setSaveConsumer(v -> info.setSourceHeight(v)).build()
        );
        return builder.startSubCategory(Text.literal(name), entries).build();
    }

    public static AbstractConfigListEntry textureEntry(Enum<?> key, Map<Enum<?>, AbilityIcon.Type> config) {
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        AbstractConfigListEntry entry = builder.startEnumSelector(
                        Text.literal(key.name()),
                        AbilityIcon.Type.class,
                        config.get(key)
                )
                .setSaveConsumer(v -> config.put(key, v))
                .build();

        return entry;
    }

    public static AbstractConfigListEntry teammatePriorityEntry(String dropdownName, SpecConfig config) {
        List<AbstractConfigListEntry> inputs = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (Spec spec : Spec.values()) {
            AbstractConfigListEntry input = builder
                    .startIntField(Text.literal(spec.name()), config.teammatePriority.get(spec))
                    .setSaveConsumer(v -> config.teammatePriority.put(spec, v))
                    .build();
            inputs.add(input);
        }
        return builder.startSubCategory(Text.literal(dropdownName), inputs).build();
    }

    public static AbstractConfigListEntry abilitySpecPriorityEntry(String dropdownName, SpecConfig config) {
        List<AbstractConfigListEntry> inputs = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (AbilitySpec spec : AbilitySpec.values()) {
            AbstractConfigListEntry input = builder
                    .startIntField(Text.literal(spec.name()), config.specPriority.get(spec))
                    .setSaveConsumer(v -> config.specPriority.put(spec, v))
                    .build();
            inputs.add(input);
        }
        return builder.startSubCategory(Text.literal(dropdownName), inputs).build();
    }

    public static AbstractConfigListEntry activeSlotPriorityEntry(String dropdownName, SpecConfig config) {
        List<AbstractConfigListEntry> inputs = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        for (ActiveSlot slot : ActiveSlot.values()) {
            AbstractConfigListEntry input = builder
                    .startIntField(Text.literal(slot.name()), config.slotPriority.get(slot))
                    .setSaveConsumer(v -> config.slotPriority.put(slot, v))
                    .build();
            inputs.add(input);
        }
        return builder.startSubCategory(Text.literal(dropdownName), inputs).build();
    }

    public static AbstractConfigListEntry activeSortEntry(String dropdownName, SpecConfig config) {
        List<AbstractConfigListEntry> sortOrder = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        // don't create a line for "disabled"
        for (int i = 0; i < SpecConfig.ActiveSorters.values().length - 1; i++) {
            int finalI = i;
            AbstractConfigListEntry entry = builder.startEnumSelector(Text.literal("Sort by #"+i+1), SpecConfig.ActiveSorters.class, config.activeSortOrder.get(i))
                    .setSaveConsumer(e -> config.activeSortOrder.add(finalI, e))
                    .build();
            sortOrder.add(entry);
        }
        return builder.startSubCategory(Text.literal(dropdownName), sortOrder).build();
    }

    public static AbstractConfigListEntry passiveSortEntry(String dropdownName, SpecConfig config) {
        List<AbstractConfigListEntry> sortOrder = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();
        // don't create a line for "disabled"
        for (int i = 0; i < SpecConfig.PassiveSorters.values().length - 1; i++) {
            int finalI = i;
            AbstractConfigListEntry entry = builder.startEnumSelector(Text.literal("Sort by #"+i+1), SpecConfig.PassiveSorters.class, config.passiveSortOrder.get(i))
                    .setSaveConsumer(e -> config.passiveSortOrder.add(finalI, e))
                    .build();
            sortOrder.add(entry);
        }
        return builder.startSubCategory(Text.literal(dropdownName), sortOrder).build();
    }

    public static AbstractConfigListEntry enumListEntry(String dropdownName, String listName, List<String> list, Set<Enum<?>> enumBacked) {
        List<AbstractConfigListEntry> sortOrder = new ArrayList<>();
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        builder.startStrList(Text.literal(listName), list).setSaveConsumer(l -> {
            list.clear();
            list.addAll(l);
            enumBacked.clear();

        })
        return builder.startSubCategory(Text.literal(dropdownName), sortOrder).build();
    }
}
