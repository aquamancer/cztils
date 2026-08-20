package com.aquamancer.cztils.config.custom;

import com.aquamancer.czlib.api.abils.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class SpecConfig {
    public enum ActiveSorters {
        SPEC,
        SLOT,
        RARITY,
        SPEC_REVERSED,
        SLOT_REVERSED,
        RARITY_ASCENDING,
        DISABLED
    }
    public enum PassiveSorters {
        SPEC,
        RARITY,
        SPEC_REVERSED,
        RARITY_ASCENDING,
        DISABLED
    }

    public enum IconListMode {
        ALLOWLIST, BLOCKLIST
    }

    public String name;
    public int nameColor;
    public int specColor;
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
    public transient Set<Ability<?>> alwaysShowSet;
    public transient Set<Ability<?>> showIfHasSpecSet;
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

    private static Set<Ability<?>> fillAbilitySet(List<String> names, Function<Ability<?>, Boolean> excludeIf) {
        Set<Ability<?>> set = new HashSet<>();
        for (String name : names) {
            Optional<Ability<?>> ability = Ability.fromString(name);
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
            Optional<Ability<?>> ability = Ability.fromString(name);
            if (ability.isEmpty()) continue;
            if (type.isInstance(ability.get())) {
                result.add(type.cast(ability.get()));
            }
        }
        return (invert) ? EnumSet.complementOf(result) : result;
    }

    public <E extends Enum<E>> Set<E> getAlwaysShow(Class<E> abilityType) {
        Set<E> result = new HashSet<>();
        for (Ability<?> e : this.alwaysShowSet) {
            if (abilityType.isInstance(e)) {
                result.add(abilityType.cast(e));
            }
        }
        return result;
    }

    public <E extends Enum<E>> Set<E> getShowIfHasSpec(Class<E> abilityType) {
        Set<E> result = new HashSet<>();
        for (Ability<?> e : this.showIfHasSpecSet) {
            if (abilityType.isInstance(e)) {
                result.add(abilityType.cast(e));
            }
        }
        return result;
    }

    @Nullable
    public Comparator<Active> getActiveSorter() {
        Comparator<Active> sorter = null;

        for (SpecConfig.ActiveSorters activeSorter : this.activeSortOrder) {
            Comparator<Active> nextSorter = this.getSorter(activeSorter);
            if (nextSorter == null) continue;
            if (sorter == null) {
                sorter = nextSorter;
            } else {
                sorter = sorter.thenComparing(nextSorter);
            }
        }
        return sorter;
    }

    @Nullable
    public Comparator<Passive> getPassiveSorter() {
        Comparator<Passive> sorter = null;

        for (SpecConfig.PassiveSorters passiveSorter : this.passiveSortOrder) {
            Comparator<Passive> nextSorter = this.getSorter(passiveSorter);
            if (nextSorter == null) continue;
            if (sorter == null) {
                sorter = nextSorter;
            } else {
                sorter = sorter.thenComparing(nextSorter);
            }
        }
        return sorter;
    }



    @Nullable
    public Comparator<Active> getSorter(ActiveSorters type) {
        Comparator<? super Active> comparator = switch (type) {
            case SPEC -> new AbilitySpec.AbilitySpecComparator(this.specPriority);
            case SPEC_REVERSED -> new AbilitySpec.AbilitySpecComparator(this.specPriority).reversed();
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
            case SPEC -> new AbilitySpec.AbilitySpecComparator(this.specPriority);
            case SPEC_REVERSED -> new AbilitySpec.AbilitySpecComparator(this.specPriority).reversed();
            case RARITY -> new Rarity.RarityComparator().reversed();
            case RARITY_ASCENDING -> new Rarity.RarityComparator();
            case DISABLED -> null;
        };
        return comparator == null ? null : comparator::compare;
    }
}
