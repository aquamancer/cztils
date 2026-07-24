package com.aquamancer.cztils.config;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.AbilityIcon;

import java.util.*;

public final class ConfigDefaults {
    public static final int grayedOutColor = 0x80000000;
    public static final int borderWidth = 1;
    public static final Map<Spec, String> names = new EnumMap<>(Spec.class);
    public static final Map<Spec, Map<Spec, Integer>> teammatePriority = new EnumMap<>(Spec.class);
    public static final Map<Spec, Map<AbilitySpec, Integer>> specPriority = new EnumMap<>(Spec.class);
    public static final Map<Spec, Map<ActiveSlot, Integer>> slotPriority = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<SpecConfig.ActiveSorters>> activeSortOrder = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<SpecConfig.PassiveSorters>> passiveSortOrder = new EnumMap<>(Spec.class);

    public static final Map<Spec, List<String>> alwaysShow = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<String>> showIfHasSpec = new EnumMap<>(Spec.class);
    public static final Map<Spec, SpecConfig.IconListMode> activeListMode = new EnumMap<>(Spec.class);
    public static final Map<Spec, SpecConfig.IconListMode> passiveListMode = new EnumMap<>(Spec.class);
    public static final Map<Spec, SpecConfig.IconListMode> giftListMode = new EnumMap<>(Spec.class);
    public static final Map<Spec, SpecConfig.IconListMode> curseListMode = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<String>> activeList = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<String>> passiveList = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<String>> giftList = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<String>> curseList = new EnumMap<>(Spec.class);

    static {
        // names
        names.put(Spec.STEEL, "Steel");
        names.put(Spec.SHADOW, "Shadow");
        names.put(Spec.FLAME, "Flame");
        names.put(Spec.FROST, "Frost");
        names.put(Spec.WIND, "Wind");
        names.put(Spec.EARTH, "Earth");
        names.put(Spec.DAWN, "Dawn");
        // teammatePriority
        for (Spec spec : Spec.values()) {
            Map<Spec, Integer> teammates = new EnumMap<>(Spec.class);
            teammates.put(Spec.STEEL, 0);
            teammates.put(Spec.SHADOW, 0);
            teammates.put(Spec.FLAME, 1);
            teammates.put(Spec.FROST, 1);
            teammates.put(Spec.WIND, 2);
            teammates.put(Spec.EARTH, 3);
            teammates.put(Spec.DAWN, 5);
            teammatePriority.put(spec, teammates);
        }
        // specPriority
        for (Spec spec : Spec.values()) {
            Map<AbilitySpec, Integer> specs = new EnumMap<>(AbilitySpec.class);
            switch (spec) {
                case STEEL:
                    specs.put(AbilitySpec.STEEL, 0);
                    specs.put(AbilitySpec.WIND, 1);
                    specs.put(AbilitySpec.EARTH, 2);
                    specs.put(AbilitySpec.DAWN, 3);
                    specs.put(AbilitySpec.SHADOW, 4);
                    specs.put(AbilitySpec.FLAME, 7);
                    specs.put(AbilitySpec.FROST, 8);
                    specs.put(AbilitySpec.PRISMATIC, 9);
                    break;
                case SHADOW:
                    specs.put(AbilitySpec.SHADOW, 0);
                    specs.put(AbilitySpec.WIND, 1);
                    specs.put(AbilitySpec.EARTH, 2);
                    specs.put(AbilitySpec.STEEL, 5);
                    specs.put(AbilitySpec.FLAME, 6);
                    specs.put(AbilitySpec.DAWN, 7);
                    specs.put(AbilitySpec.FROST, 8);
                    specs.put(AbilitySpec.PRISMATIC, 9);
                    break;
                case FLAME:
                    specs.put(AbilitySpec.FLAME, 0);
                    specs.put(AbilitySpec.WIND, 1);
                    specs.put(AbilitySpec.EARTH, 2);
                    specs.put(AbilitySpec.DAWN, 3);
                    specs.put(AbilitySpec.FROST, 6);
                    specs.put(AbilitySpec.STEEL, 7);
                    specs.put(AbilitySpec.SHADOW, 8);
                    specs.put(AbilitySpec.PRISMATIC, 9);
                    break;
                case FROST:
                    specs.put(AbilitySpec.FROST, 0);
                    specs.put(AbilitySpec.WIND, 1);
                    specs.put(AbilitySpec.FLAME, 2);
                    specs.put(AbilitySpec.EARTH, 3);
                    specs.put(AbilitySpec.STEEL, 5);
                    specs.put(AbilitySpec.DAWN, 6);
                    specs.put(AbilitySpec.SHADOW, 8);
                    specs.put(AbilitySpec.PRISMATIC, 9);
                    break;
                case WIND:
                    specs.put(AbilitySpec.WIND, 0);
                    specs.put(AbilitySpec.EARTH, 1);
                    specs.put(AbilitySpec.FLAME, 9);
                    specs.put(AbilitySpec.DAWN, 9);
                    specs.put(AbilitySpec.STEEL, 9);
                    specs.put(AbilitySpec.SHADOW, 9);
                    specs.put(AbilitySpec.FROST, 9);
                    specs.put(AbilitySpec.PRISMATIC, 9);
                    break;
                case EARTH:
                    specs.put(AbilitySpec.EARTH, 0);
                    specs.put(AbilitySpec.WIND, 1);
                    specs.put(AbilitySpec.SHADOW, 2);
                    specs.put(AbilitySpec.DAWN, 3);
                    specs.put(AbilitySpec.STEEL, 6);
                    specs.put(AbilitySpec.FROST, 7);
                    specs.put(AbilitySpec.FLAME, 8);
                    specs.put(AbilitySpec.PRISMATIC, 9);
                    break;
                case DAWN:
                    specs.put(AbilitySpec.DAWN, 0);
                    specs.put(AbilitySpec.WIND, 1);
                    specs.put(AbilitySpec.EARTH, 2);
                    specs.put(AbilitySpec.STEEL, 3);
                    specs.put(AbilitySpec.FROST, 6);
                    specs.put(AbilitySpec.FLAME, 7);
                    specs.put(AbilitySpec.SHADOW, 9);
                    specs.put(AbilitySpec.PRISMATIC, 9);
                    break;
            }
            specPriority.put(spec, specs);
        }
        // slotPriority
        for (Spec spec : Spec.values()) {
            Map<ActiveSlot, Integer> slots = new EnumMap<>(ActiveSlot.class);
            slots.put(ActiveSlot.COMBO, 0);
            slots.put(ActiveSlot.RIGHT, 1);
            slots.put(ActiveSlot.LEFT_SHIFT, 2);
            slots.put(ActiveSlot.RIGHT_SHIFT, 3);
            slots.put(ActiveSlot.SWAP, 4);
            slots.put(ActiveSlot.WILDCARD, 5);
            slots.put(ActiveSlot.BOW, 6);
            slots.put(ActiveSlot.LIFELINE, 7);

            slotPriority.put(spec, slots);
        }
        // active sort order
        for (Spec spec : Spec.values()) {
            List<SpecConfig.ActiveSorters> sorters = new ArrayList<>();
            sorters.add(SpecConfig.ActiveSorters.SLOT);
            sorters.add(SpecConfig.ActiveSorters.SPEC);
            sorters.add(SpecConfig.ActiveSorters.DISABLED);
            sorters.add(SpecConfig.ActiveSorters.DISABLED);
            sorters.add(SpecConfig.ActiveSorters.DISABLED);
            sorters.add(SpecConfig.ActiveSorters.DISABLED);

            activeSortOrder.put(spec, sorters);
        }
        // passive sort order
        for (Spec spec : Spec.values()) {
            List<SpecConfig.PassiveSorters> sorters = new ArrayList<>();
            sorters.add(SpecConfig.PassiveSorters.SPEC);
            sorters.add(SpecConfig.PassiveSorters.DISABLED);
            sorters.add(SpecConfig.PassiveSorters.DISABLED);
            sorters.add(SpecConfig.PassiveSorters.DISABLED);

            passiveSortOrder.put(spec, sorters);
        }
        // icon lists
        for (Spec spec : Spec.values()) {
            List<String> always = new ArrayList<>();
            List<String> ifHasSpec = new ArrayList<>();
            SpecConfig.IconListMode activeMode = SpecConfig.IconListMode.ALLOWLIST;
            List<String> actives = new ArrayList<>();
            SpecConfig.IconListMode passiveMode = SpecConfig.IconListMode.ALLOWLIST;
            List<String> passives = new ArrayList<>();
            SpecConfig.IconListMode giftMode = SpecConfig.IconListMode.BLOCKLIST;
            List<String> gifts = new ArrayList<>();
            SpecConfig.IconListMode curseMode = SpecConfig.IconListMode.BLOCKLIST;
            List<String> curses = new ArrayList<>();

            curses.add("Curse of Death");
            curses.add("Curse of Envy");
            curses.add("Curse of Gluttony");

            ifHasSpec.add("Skyhook");

            actives.add("Wind Walk");
            actives.add("Rapid Fire");

            actives.add("Steel Stallion");
            actives.add("Escape Artist");
            actives.add("Apocalypse");
            actives.add("Cryobox");
            actives.add("Last Breath");
            actives.add("Eternal Savior");

            passives.add("Generosity");
            passives.add("Multiplicity");
            passives.add("Rebirth");

            switch (spec) {
                case STEEL:
                    always.add("Sidearm");
                    always.add("Scrapshot");
                    always.add("Rapid Fire");
                    always.add("Sharpshooter");
                    actives.add("Focused Combos");
                    actives.add("Firework Blast");
                    actives.add("Volley");
                    actives.add("Gravity Bomb");
                    passives.add("Split Arrow");
                    ifHasSpec.add("Dethroner");
                    ifHasSpec.add("Primordial Mastery");
                    break;
                case SHADOW:
                    always.add("Advancing Shadows");
                    always.add("Blade Flurry");
                    always.add("Cloak of Shadows");
                    always.add("Chaos Dagger");
                    always.add("Deadly Strike");
                    always.add("Brutalize");
                    always.add("Dethroner");
                    actives.add("Dark Combos");
                    actives.add("Phantom Force");
                    actives.add("Dummy Decoy");
                    ifHasSpec.add("Windswept Combos");
                    break;
                case FLAME:
                    always.add("Igneous Rune");
                    always.add("Flamestrike");
                    always.add("Volcanic Meteor");
                    always.add("Detonation");
                    always.add("Primordial Mastery");
                    actives.add("Volcanic Combos");
                    actives.add("Fireball");
                    actives.add("Flame Spirit");
                    actives.add("Pyroblast");
                    actives.add("Solar Ray");
                    ifHasSpec.add("Dethroner");
                    break;
                case FROST:
                    always.add("Snowstorm");
                    always.add("Avalanche");
                    always.add("Frozen Domain");
                    always.add("Icebreaker");
                    actives.add("Frigid Combos");
                    actives.add("Ice Lance");
                    actives.add("Ice Barrier");
                    actives.add("Permafrost");
                    actives.add("Piercing Cold");
                    actives.add("Solar Ray");
                    ifHasSpec.add("Dethroner");
                    ifHasSpec.add("Detonation");
                    ifHasSpec.add("Primordial Mastery");
                    break;
                case WIND:
                    always.add("Guarding Bolt");
                    always.add("Aeroblast");
                    always.add("Thundercloud Form");
                    always.add("Skyhook");
                    always.add("Aeromancy");
                    actives.add("Windswept Combos");
                    actives.add("Wind Walk");
                    actives.add("Whirlwind");
                    ifHasSpec.add("Dethroner");
                    ifHasSpec.add("Primordial Mastery");
                    break;
                case EARTH:
                    always.add("Beast's Claw");
                    always.add("Iron Grip");
                    always.add("Taunt");
                    always.add("Earthen Wrath");
                    actives.add("Earthen Combos");
                    actives.add("Entrench");
                    actives.add("Earthquake");
                    ifHasSpec.add("Dethroner");
                    break;
                case DAWN:
                    always.add("Soothing Combos");
                    always.add("Radiant Blessing");
                    always.add("Spark of Inspiration");
                    always.add("Sundrops");
                    actives.add("Ward of Light");
                    actives.add("Bottled Sunlight");
                    actives.add("Lightning Bottle");
                    actives.add("Divine Beam");
                    break;
            }

            alwaysShow.put(spec, always);
            showIfHasSpec.put(spec, ifHasSpec);

            activeListMode.put(spec, activeMode);
            activeList.put(spec, actives);
            passiveListMode.put(spec, passiveMode);
            passiveList.put(spec, passives);
            giftListMode.put(spec, giftMode);
            giftList.put(spec, gifts);
            curseListMode.put(spec, curseMode);
            curseList.put(spec, curses);
        }
    }

    public static Map<Spec, SpecConfig> createDefaultSpecConfigs() {
        Map<Spec, SpecConfig> result = new EnumMap<>(Spec.class);
        for (Spec spec : Spec.values()) {
            result.put(spec, createDefaultSpecConfig(spec));
        }
        return result;
    }

    private static SpecConfig createDefaultSpecConfig(Spec spec) {
        SpecConfig config = new SpecConfig();
        config.name = names.get(spec);
        config.teammatePriority = new EnumMap<>(teammatePriority.get(spec));
        config.specPriority = new EnumMap<>(specPriority.get(spec));
        config.slotPriority = new EnumMap<>(slotPriority.get(spec));
        config.activeSortOrder = new ArrayList<>(activeSortOrder.get(spec));
        config.passiveSortOrder = new ArrayList<>(passiveSortOrder.get(spec));
        config.alwaysShow = new ArrayList<>(alwaysShow.get(spec));
        config.showIfHasSpec = new ArrayList<>(showIfHasSpec.get(spec));
        config.activeListMode = activeListMode.get(spec);
        config.passiveListMode = passiveListMode.get(spec);
        config.giftListMode = giftListMode.get(spec);
        config.curseListMode = curseListMode.get(spec);
        config.activeList = new ArrayList<>(activeList.get(spec));
        config.passiveList = new ArrayList<>(passiveList.get(spec));
        config.giftList = new ArrayList<>(giftList.get(spec));
        config.curseList = new ArrayList<>(curseList.get(spec));
        config.updateEnumSets();
        return config;
    }

    public static ModConfig.Textures createDefaultTextures() {
        ModConfig.Textures textures = new ModConfig.Textures();
        textures.actives = new EnumMap<>(Actives.class);
        textures.passives = new EnumMap<>(Passives.class);
        textures.curses = new EnumMap<>(Curse.class);
        textures.gifts = new EnumMap<>(Gifts.class);
        loadEnumMap(Actives.values(), textures.actives);
        loadEnumMap(Passives.values(), textures.passives);
        loadEnumMap(Curse.values(), textures.curses);
        loadEnumMap(Gifts.values(), textures.gifts);
        return textures;
    }

    private static <T extends Enum<?>> void loadEnumMap(T[] values, Map<T, AbilityIcon.Type> map) {
        for (T key : values) {
            map.put(key, AbilityIcon.Type.VANILLA);
        }
    }

    private ConfigDefaults() {}
}
