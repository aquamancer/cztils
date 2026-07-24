package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

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
    public Map<Spec, Integer> teammatePriority;
    public Map<AbilitySpec, Integer> specPriority;
    public Map<ActiveSlot, Integer> slotPriority;
    public List<ActiveSorters> activeSortOrder;
    public List<PassiveSorters> passiveSortOrder;
    // using lists for autoconfig compatibility
    public List<String> alwaysShow;
    public List<String> showIfHasSpec;

    public IconListMode activeListMode;
    public IconListMode passiveListMode;
    public IconListMode giftListMode;
    public IconListMode curseListMode;
    public List<String> activeList;
    public List<String> passiveList;
    public List<String> giftList;
    public List<String> curseList;
    // set view of the above, actually used in code
    public transient Set<Enum<?>> alwaysShowSet;
    public transient Set<Enum<?>> showIfHasSpecSet;
    public transient Set<Actives> activeSet;
    public transient Set<Passives> passiveSet;
    public transient Set<Gifts> giftSet;
    public transient Set<Curse> curseSet;

    public SpecConfig() {}

    public void updateEnumSets() {
        this.alwaysShowSet = fillAbilitySet(this.alwaysShow, e -> (e instanceof Curse || e instanceof Gifts));
        this.showIfHasSpecSet = fillAbilitySet(this.showIfHasSpec, e -> (e instanceof Curse || e instanceof Gifts));
        this.activeSet = fillAbilitySet(this.activeList, Actives.class, activeListMode == IconListMode.BLOCKLIST);
        this.passiveSet = fillAbilitySet(this.passiveList, Passives.class, passiveListMode == IconListMode.BLOCKLIST);
        this.giftSet = fillAbilitySet(this.giftList, Gifts.class, giftListMode == IconListMode.BLOCKLIST);
        this.curseSet = fillAbilitySet(this.curseList, Curse.class, curseListMode == IconListMode.BLOCKLIST);
    }

    private static Set<Enum<?>> fillAbilitySet(List<String> names, Function<Enum<?>, Boolean> excludeIf) {
        Set<Enum<?>> set = new HashSet<>();
        for (String name : names) {
            Optional<Enum<?>> ability = AbilityUtils.fromString(name);
            if (ability.isEmpty()) continue;
            if (!excludeIf.apply(ability.get())) {
                set.add(ability.get());
            }
        }
        return set;
    }

    private static <E extends Enum<E>> Set<E> fillAbilitySet(List<String> names, Class<E> type, boolean invert) {
        EnumSet<E> result = EnumSet.noneOf(type);
        for (String name : names) {
            Optional<Enum<?>> ability = AbilityUtils.fromString(name);
            if (ability.isEmpty()) continue;
            if (type.isInstance(ability.get())) {
                result.add(type.cast(ability.get()));
            }
        }
        return (invert) ? EnumSet.complementOf(result) : result;
    }

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
