package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpecConfig {
    public enum ActiveSorters {
        SPEC, SPEC_REVERSED,
        SLOT, SLOT_REVERSED,
        RARITY, RARITY_ASCENDING,
        DISABLED
    }
    public enum PassiveSorters {
        SPEC, SPEC_REVERSED,
        RARITY, RARITY_ASCENDING,
        DISABLED
    }

    public enum IconListMode {
        ALLOWLIST, BLOCKLIST
    }

    public String name;
    public Map<Spec, Integer> teammatePriority = new EnumMap<>(Spec.class);
    public Map<AbilitySpec, Integer> specPriority = new EnumMap<>(AbilitySpec.class);
    public Map<ActiveSlot, Integer> slotPriority = new EnumMap<>(ActiveSlot.class);
    public List<ActiveSorters> activeSortOrder = new ArrayList<>();
    public List<PassiveSorters> passiveSortOrder = new ArrayList<>();
    // using lists for autoconfig compatibility
    public List<String> alwaysShow = new ArrayList<>();
    public List<String> showIfHasSpec = new ArrayList<>();

    public IconListMode activeListMode;
    public IconListMode passiveListMode;
    public IconListMode giftListMode;
    public IconListMode curseListMode;
    public List<String> activeList;
    public List<String> passiveList;
    public List<String> giftList;
    public List<String> curseList;
    // set view of the above, actually used in code
    public transient Set<Enum<?>> alwaysShowSet = new HashSet<>();
    public transient Set<Enum<?>> showIfHasSpecSet = new HashSet<>();
    public transient Set<Actives> activeSet;
    public transient Set<Passives> passiveSet;
    public transient Set<Gifts> giftSet;
    public transient Set<Curse> curseSet;

    public SpecConfig() {}

    @Nullable
    public Comparator<Active> getSorter(ActiveSorters type) {
        Comparator<? super Active> comparator = switch (type) {
            case SPEC -> new AbilitySpec.SpecComparator(this.specPriority);
            case SPEC_REVERSED -> new AbilitySpec.SpecComparator(this.specPriority).reversed();
            case SLOT -> new Actives.ActiveSlotComparator2(this.slotPriority);
            case SLOT_REVERSED -> new Actives.ActiveSlotComparator2(this.slotPriority).reversed();
            case RARITY -> new Rarity.RarityComparator().reversed();
            case RARITY_ASCENDING -> new Rarity.RarityComparator();
            case DISABLED -> null;
        };
        return comparator == null ? null : comparator::compare;
    }

    @Nullable
    public Comparator<Passive> getSorter(PassiveSorters type) {
        Comparator<? super Passive> comparator = switch (type) {
            case SPEC -> new AbilitySpec.SpecComparator(this.specPriority);
            case SPEC_REVERSED -> new AbilitySpec.SpecComparator(this.specPriority).reversed();
            case RARITY -> new Rarity.RarityComparator().reversed();
            case RARITY_ASCENDING -> new Rarity.RarityComparator();
            case DISABLED -> null;
        };
        return comparator == null ? null : comparator::compare;
    }
}
