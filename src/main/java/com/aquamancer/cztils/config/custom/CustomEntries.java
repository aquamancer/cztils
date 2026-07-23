package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.AbilitySpec;
import com.aquamancer.czlib.api.abils.AbilityUtils;
import com.aquamancer.czlib.api.abils.ActiveSlot;
import com.aquamancer.czlib.api.abils.Spec;
import com.aquamancer.cztils.config.ConfigDefaults;
import com.aquamancer.cztils.hud.AbilityIcon;
import com.aquamancer.cztils.hud.TextureInfo;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

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
        for (int i = 0; i < SpecConfig.ActiveSorters.values().length - 1; i++) {
            int finalI = i;
            String description = (i == 0) ? "Sort by" : "then by";
            AbstractConfigListEntry entry = builder.startEnumSelector(Text.literal(description), SpecConfig.ActiveSorters.class, config.activeSortOrder.get(i))
                    .setSaveConsumer(e -> config.activeSortOrder.set(finalI, e))
                    .build();
            sortOrder.add(entry);
        }
        return builder.startSubCategory(Text.literal(dropdownName), sortOrder).build();
    }

    public static AbstractConfigListEntry passiveSortEntry(String dropdownName, SpecConfig config) {
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
        return builder.startSubCategory(Text.literal(dropdownName), sortOrder).build();
    }

    public enum EnumListType { ALWAYS_SHOW, SHOW_IF_SPEC, SHOW_IF_HAS }
    public static AbstractConfigListEntry enumListEntry(EnumListType type, @NotNull Spec spec, List<String> list, Set<Enum<?>> enumBacked) {
        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        String listName;
        List<String> defaultValues;
        switch (type) {
            default:
            case ALWAYS_SHOW:
                listName = "Always show icons";
                defaultValues = ConfigDefaults.alwaysShow.get(spec);
                break;
            case SHOW_IF_SPEC:
                listName = "Always show icon if has spec";
                defaultValues = ConfigDefaults.alwaysShowIfHasSpec.get(spec);
                break;
            case SHOW_IF_HAS:
                listName = "Show icon if has";
                defaultValues = ConfigDefaults.showIfHas.get(spec);
                break;
        }

        return builder.startStrList(Text.literal(listName), list).setSaveConsumer(updated -> {
            list.clear();
            enumBacked.clear();
            updated.forEach(e -> {
                Optional<Enum<?>> ability = AbilityUtils.fromString(e);
                if (ability.isPresent()) {
                    list.add(e);
                    enumBacked.add(ability.get());
                }
            });
        }).setDefaultValue(defaultValues).build();
    }
}
