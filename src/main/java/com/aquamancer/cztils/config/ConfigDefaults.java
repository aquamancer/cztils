package com.aquamancer.cztils.config;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.AbilityIcon;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ConfigDefaults {
    public static final Map<Spec, String> names = new HashMap<>();
    public static final Map<Spec, Integer> nameColors = new HashMap<>();
    public static final Map<Spec, Integer> specColors = new HashMap<>();
    public static final Map<Spec, Map<Spec, Integer>> teammatePriority = new HashMap<>();
    public static final Map<Spec, Map<AbilitySpec, Integer>> specPriority = new HashMap<>();
    public static final Map<Spec, Map<ActiveSlot, Integer>> slotPriority = new HashMap<>();
    public static final Map<Spec, List<SpecConfig.ActiveSorters>> activeSortOrder = new HashMap<>();
    public static final Map<Spec, List<SpecConfig.PassiveSorters>> passiveSortOrder = new HashMap<>();

    public static final Map<Spec, List<String>> alwaysShow = new HashMap<>();
    public static final Map<Spec, List<String>> showIfHasSpec = new HashMap<>();
    public static final Map<Spec, SpecConfig.IconListMode> activeListMode = new HashMap<>();
    public static final Map<Spec, SpecConfig.IconListMode> passiveListMode = new HashMap<>();
    public static final Map<Spec, SpecConfig.IconListMode> giftListMode = new HashMap<>();
    public static final Map<Spec, SpecConfig.IconListMode> curseListMode = new HashMap<>();
    public static final Map<Spec, List<String>> activeList = new HashMap<>();
    public static final Map<Spec, List<String>> passiveList = new HashMap<>();
    public static final Map<Spec, List<String>> giftList = new HashMap<>();
    public static final Map<Spec, List<String>> curseList = new HashMap<>();

    static {
        String bow = new String(Character.toChars(0x1F3F9));
        String sword = new String(Character.toChars(0x1F5E1));
        String fire = new String(Character.toChars(0x1F525));
        String pickaxe = new String(Character.toChars(0x26CF));
        names.put(Spec.STEEL, bow);
        names.put(Spec.SHADOW, sword);
        names.put(Spec.FLAME, fire);
        names.put(Spec.FROST, "❄");
        names.put(Spec.WIND, "⚡");
        names.put(Spec.EARTH, pickaxe);
        names.put(Spec.DAWN, "☀");

        nameColors.put(Spec.STEEL, 0x929292);
        nameColors.put(Spec.SHADOW, 0x7948af);
        nameColors.put(Spec.FLAME, 0xf04e21);
        nameColors.put(Spec.FROST, 0xa3cbe1);
        nameColors.put(Spec.WIND, 0xc0dea9);
        nameColors.put(Spec.EARTH, 0x6b3d2d);
        nameColors.put(Spec.DAWN, 0xf0b326);

        specColors.put(Spec.STEEL, 0x929292);
        specColors.put(Spec.SHADOW, 0x7948af);
        specColors.put(Spec.FLAME, 0xf04e21);
        specColors.put(Spec.FROST, 0xa3cbe1);
        specColors.put(Spec.WIND, 0xc0dea9);
        specColors.put(Spec.EARTH, 0x6b3d2d);
        specColors.put(Spec.DAWN, 0xf0b326);



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
    // config for null spec
    static {
        names.put(null, "Charmless");
        nameColors.put(null, 0xffffff);
        specColors.put(null, 0xffffff);

        teammatePriority.put(null, Arrays.stream(Spec.values()).collect(Collectors.toMap(Function.identity(), spec -> 0)));
        specPriority.put(null, Arrays.stream(AbilitySpec.values()).collect(Collectors.toMap(Function.identity(), spec -> 0)));
        slotPriority.put(null, Arrays.stream(ActiveSlot.values()).collect(
                () -> new EnumMap<>(ActiveSlot.class),
                (map, slot) -> map.put(slot, slot.ordinal()),
                EnumMap::putAll
        ));
        activeSortOrder.put(null, activeSortOrder.get(Spec.SHADOW));
        passiveSortOrder.put(null, passiveSortOrder.get(Spec.SHADOW));
        alwaysShow.put(null, new ArrayList<>());
        showIfHasSpec.put(null, new ArrayList<>());
        activeListMode.put(null, SpecConfig.IconListMode.BLOCKLIST);
        passiveListMode.put(null, SpecConfig.IconListMode.BLOCKLIST);
        giftListMode.put(null, SpecConfig.IconListMode.BLOCKLIST);
        curseListMode.put(null, SpecConfig.IconListMode.BLOCKLIST);
        activeList.put(null, new ArrayList<>());
        passiveList.put(null, new ArrayList<>());
        giftList.put(null, new ArrayList<>());
        curseList.put(null, new ArrayList<>());
    }

    public static Map<Spec, SpecConfig> createDefaultSpecConfigs() {
        Map<Spec, SpecConfig> result = new HashMap<>();
        for (Spec spec : Spec.values()) {
            result.put(spec, createDefaultSpecConfig(spec));
        }
        result.put(null, createDefaultSpecConfig(null));
        return result;
    }

    private static SpecConfig createDefaultSpecConfig(Spec spec) {
        SpecConfig config = new SpecConfig();
        config.name = names.get(spec);
        config.nameColor = nameColors.get(spec);
        config.specColor = specColors.get(spec);
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
