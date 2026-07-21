package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.aquamancer.czlib.api.abils.AbilitySpec.STEEL;

public class SpecConfig {
    public enum ActiveSorters { SPEC, SLOT, RARITY, DISABLED }
    public enum PassiveSorters { SPEC, RARITY, DISABLED }

    public Map<Spec, Integer> teammatePriority = new EnumMap<>(Spec.class);
    public Map<AbilitySpec, Integer> specPriority = new EnumMap<>(AbilitySpec.class);
    public Map<ActiveSlot, Integer> slotPriority = new EnumMap<>(ActiveSlot.class);
    public List<ActiveSorters> activeSortOrder = new ArrayList<>();
    public List<PassiveSorters> passiveSortOrder = new ArrayList<>();
    // using lists for autoconfig compatibility
    public List<Enum<?>> alwaysShow = new ArrayList<>();
    public List<Enum<?>> showIfHas = new ArrayList<>();

    public SpecConfig() {}

    @Nullable
    public Comparator<? super Active> getSorter(ActiveSorters type) {
        return switch (type) {
            case SPEC -> new AbilitySpec.SpecComparator(this.specPriority);
            case SLOT -> new Actives.ActiveSlotComparator2(this.slotPriority);
            case RARITY -> new Rarity.RarityComparator();
            case DISABLED -> null;
        };
    }

    @Nullable
    public Comparator<? super Passive> getSorter(PassiveSorters type) {
        return switch (type) {
            case SPEC -> new AbilitySpec.SpecComparator(this.specPriority);
            case RARITY -> new Rarity.RarityComparator();
            case DISABLED -> null;
        };
    }

    public static void fillDefaults(Spec spec, SpecConfig config) {
        config.teammatePriority.putIfAbsent(Spec.STEEL, 0);
        config.teammatePriority.putIfAbsent(Spec.SHADOW, 0);
        config.teammatePriority.putIfAbsent(Spec.FLAME, 1);
        config.teammatePriority.putIfAbsent(Spec.FROST, 1);
        config.teammatePriority.putIfAbsent(Spec.WIND, 2);
        config.teammatePriority.putIfAbsent(Spec.EARTH, 3);
        config.teammatePriority.putIfAbsent(Spec.DAWN, 5);

        switch (spec) {
            case STEEL:
                config.specPriority.putIfAbsent(AbilitySpec.STEEL, 0);
                config.specPriority.putIfAbsent(AbilitySpec.WIND, 1);
                config.specPriority.putIfAbsent(AbilitySpec.EARTH, 2);
                config.specPriority.putIfAbsent(AbilitySpec.DAWN, 3);
                config.specPriority.putIfAbsent(AbilitySpec.SHADOW, 4);
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 7);
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 8);
                config.specPriority.putIfAbsent(AbilitySpec.PRISMATIC, 9);
                break;
            case SHADOW:
                config.specPriority.putIfAbsent(AbilitySpec.SHADOW, 0);
                config.specPriority.putIfAbsent(AbilitySpec.WIND, 1);
                config.specPriority.putIfAbsent(AbilitySpec.EARTH, 2);
                config.specPriority.putIfAbsent(AbilitySpec.STEEL, 5);
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 6);
                config.specPriority.putIfAbsent(AbilitySpec.DAWN, 7);
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 8);
                config.specPriority.putIfAbsent(AbilitySpec.PRISMATIC, 9);
                break;
            case FLAME:
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 0);
                config.specPriority.putIfAbsent(AbilitySpec.WIND, 1);
                config.specPriority.putIfAbsent(AbilitySpec.EARTH, 2);
                config.specPriority.putIfAbsent(AbilitySpec.DAWN, 3);
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 6);
                config.specPriority.putIfAbsent(AbilitySpec.STEEL, 7);
                config.specPriority.putIfAbsent(AbilitySpec.SHADOW, 8);
                config.specPriority.putIfAbsent(AbilitySpec.PRISMATIC, 9);
                break;
            case FROST:
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 0);
                config.specPriority.putIfAbsent(AbilitySpec.WIND, 1);
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 2);
                config.specPriority.putIfAbsent(AbilitySpec.EARTH, 3);
                config.specPriority.putIfAbsent(AbilitySpec.STEEL, 5);
                config.specPriority.putIfAbsent(AbilitySpec.DAWN, 6);
                config.specPriority.putIfAbsent(AbilitySpec.SHADOW, 8);
                config.specPriority.putIfAbsent(AbilitySpec.PRISMATIC, 9);
                break;
            case WIND:
                config.specPriority.putIfAbsent(AbilitySpec.WIND, 0);
                config.specPriority.putIfAbsent(AbilitySpec.EARTH, 1);
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 9);
                config.specPriority.putIfAbsent(AbilitySpec.DAWN, 9);
                config.specPriority.putIfAbsent(AbilitySpec.STEEL, 9);
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 9);
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 9);
                config.specPriority.putIfAbsent(AbilitySpec.PRISMATIC, 9);
                break;
            case DAWN:
                config.specPriority.putIfAbsent(AbilitySpec.DAWN, 0);
                config.specPriority.putIfAbsent(AbilitySpec.WIND, 1);
                config.specPriority.putIfAbsent(AbilitySpec.EARTH, 2);
                config.specPriority.putIfAbsent(AbilitySpec.STEEL, 3);
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 6);
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 7);
                config.specPriority.putIfAbsent(AbilitySpec.SHADOW, 9);
                config.specPriority.putIfAbsent(AbilitySpec.PRISMATIC, 9);
                break;
        }

        config.slotPriority.putIfAbsent(ActiveSlot.COMBO, 0);
        config.slotPriority.putIfAbsent(ActiveSlot.RIGHT, 1);
        config.slotPriority.putIfAbsent(ActiveSlot.LEFT_SHIFT, 2);
        config.slotPriority.putIfAbsent(ActiveSlot.RIGHT_SHIFT, 3);
        config.slotPriority.putIfAbsent(ActiveSlot.SWAP, 4);
        config.slotPriority.putIfAbsent(ActiveSlot.WILDCARD, 5);
        config.slotPriority.putIfAbsent(ActiveSlot.BOW, 6);
        config.slotPriority.putIfAbsent(ActiveSlot.LIFELINE, 7);

        // must add as many elements as the enum size otherwise the gui will break
        config.activeSortOrder.add(ActiveSorters.SLOT);
        config.activeSortOrder.add(ActiveSorters.SPEC);
        config.activeSortOrder.add(ActiveSorters.DISABLED);
        config.activeSortOrder.add(ActiveSorters.DISABLED);

        config.passiveSortOrder.add(PassiveSorters.SPEC);
        config.activeSortOrder.add(ActiveSorters.DISABLED);
        config.activeSortOrder.add(ActiveSorters.DISABLED);
    }
}
