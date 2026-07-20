package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.*;

import java.util.*;

public class SpecConfig {
    public enum ActiveSorters { SPEC, SLOT, RARITY }
    public enum PassiveSorters { SPEC, RARITY }

    private Map<AbilitySpec, Integer> specPriority = new EnumMap<>(AbilitySpec.class);
    private Map<ActiveSlot, Integer> slotPriority = new EnumMap<>(ActiveSlot.class);
    private List<ActiveSorters> activeSortOrder = new ArrayList<>();
    private List<PassiveSorters> passiveSortOrder = new ArrayList<>();
    private Set<Enum<?>> alwaysShow = new HashSet<>();
    private Set<Enum<?>> showIfHas = new HashSet<>();
    private Set<Enum<?>> neverShow = new HashSet<>();

    public SpecConfig() {}

    public Comparator<Active> getActiveSorter(ActiveSorters type) {
        return switch (type) {
            case SPEC -> new AbilitySpec.SpecComparator(this.specPriority);
            case SLOT -> new Actives.ActiveSlotComparator2(this.slotPriority);
            case RARITY -> new Rarity.RarityComparator();
        };
    }
}
