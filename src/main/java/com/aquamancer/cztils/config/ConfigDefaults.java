package com.aquamancer.cztils.config;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.config.custom.SpecConfig;
import com.aquamancer.cztils.hud.AbilityIcon;

import java.util.*;

public final class ConfigDefaults {
    public static final Map<Spec, List<String>> alwaysShow = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<String>> alwaysShowIfHasSpec = new EnumMap<>(Spec.class);
    public static final Map<Spec, List<String>> showIfHas = new EnumMap<>(Spec.class);
    static {
    for (Spec spec : Spec.values()) {
        List<String> alwaysShow = new ArrayList<>();
        List<String> showIfHasSpec = new ArrayList<>();
        List<String> showIfHas = new ArrayList<>();

        Arrays.stream(Curse.values()).forEach(curse -> showIfHas.add(curse.getDisplayName()));
        Arrays.stream(Gifts.values()).forEach(gift -> showIfHas.add(gift.getDisplayName()));

        showIfHasSpec.add("Skyhook");

        showIfHas.add("Steel Stallion");
        showIfHas.add("Escape Artist");
        showIfHas.add("Apocalypse");
        showIfHas.add("Cryobox");
        showIfHas.add("Last Breath");
        showIfHas.add("Eternal Savior");

        showIfHas.add("Generosity");
        showIfHas.add("Multiplicity");
        showIfHas.add("Rebirth");

        showIfHas.add("Wind Walk");
        showIfHas.add("Rapid Fire");

        switch (spec) {
            case STEEL:
                alwaysShow.add("Sidearm");
                alwaysShow.add("Scrapshot");
                alwaysShow.add("Rapid Fire");
                alwaysShow.add("Sharpshooter");
                showIfHas.add("Focused Combos");
                showIfHas.add("Firework Blast");
                showIfHas.add("Volley");
                showIfHas.add("Gravity Bomb");
                showIfHas.add("Split Arrow");
                showIfHasSpec.add("Dethroner");
                showIfHasSpec.add("Primordial Mastery");
                break;
            case SHADOW:
                alwaysShow.add("Advancing Shadows");
                alwaysShow.add("Blade Flurry");
                alwaysShow.add("Cloak of Shadows");
                alwaysShow.add("Chaos Dagger");
                alwaysShow.add("Deadly Strike");
                alwaysShow.add("Brutalize");
                alwaysShow.add("Dethroner");
                showIfHas.add("Dark Combos");
                showIfHas.add("Phantom Force");
                showIfHas.add("Dummy Decoy");
                showIfHasSpec.add("Windswept Combos");
                break;
            case FLAME:
                alwaysShow.add("Igneous Rune");
                alwaysShow.add("Flamestrike");
                alwaysShow.add("Volcanic Meteor");
                alwaysShow.add("Detonation");
                alwaysShow.add("Primordial Mastery");
                showIfHas.add("Volcanic Combos");
                showIfHas.add("Fireball");
                showIfHas.add("Flame Spirit");
                showIfHas.add("Pyroblast");
                showIfHas.add("Solar Ray");
                showIfHasSpec.add("Dethroner");
                break;
            case FROST:
                alwaysShow.add("Snowstorm");
                alwaysShow.add("Avalanche");
                alwaysShow.add("Frozen Domain");
                alwaysShow.add("Icebreaker");
                showIfHas.add("Frigid Combos");
                showIfHas.add("Ice Lance");
                showIfHas.add("Ice Barrier");
                showIfHas.add("Permafrost");
                showIfHas.add("Piercing Cold");
                showIfHas.add("Solar Ray");
                showIfHasSpec.add("Dethroner");
                showIfHasSpec.add("Detonation");
                showIfHasSpec.add("Primordial Mastery");
                break;
            case WIND:
                alwaysShow.add("Guarding Bolt");
                alwaysShow.add("Aeroblast");
                alwaysShow.add("Thundercloud Form");
                alwaysShow.add("Skyhook");
                alwaysShow.add("Aeromancy");
                showIfHas.add("Windswept Combos");
                showIfHas.add("Wind Walk");
                showIfHas.add("Whirlwind");
                showIfHasSpec.add("Dethroner");
                showIfHasSpec.add("Detonation");
                showIfHasSpec.add("Primordial Mastery");
                break;
            case EARTH:
                alwaysShow.add("Beast's Claw");
                alwaysShow.add("Iron Grip");
                alwaysShow.add("Taunt");
                alwaysShow.add("Earthen Wrath");
                showIfHas.add("Earthen Combos");
                showIfHas.add("Entrench");
                showIfHas.add("Earthquake");
                showIfHasSpec.add("Dethroner");
                break;
            case DAWN:
                alwaysShow.add("Soothing Combos");
                alwaysShow.add("Radiant Blessing");
                alwaysShow.add("Spark of Inspiration");
                alwaysShow.add("Sundrops");
                showIfHas.add("Ward of Light");
                showIfHas.add("Bottled Sunlight");
                showIfHas.add("Ward of Light");
                showIfHas.add("Lightning Bottle");
                showIfHas.add("Divine Beam");
                break;
        }

        ConfigDefaults.alwaysShow.put(spec, alwaysShow);
        ConfigDefaults.alwaysShowIfHasSpec.put(spec, showIfHasSpec);
        ConfigDefaults.showIfHas.put(spec, showIfHas);
    }
}

    public static void loadDefaults(ModConfig config) {
        loadTextures(config.textures);

        for (Spec spec : Spec.values()) {
            SpecConfig specConfig = config.specConfigs.computeIfAbsent(spec, (k) -> {
                SpecConfig defaultConfig = new SpecConfig();
                loadSpecConfig(spec, defaultConfig);
                return defaultConfig;
            });
            specConfig.alwaysShowSet.clear();
            specConfig.alwaysShow.forEach(s -> {
                Optional<Enum<?>> ability = AbilityUtils.fromString(s);
                if (ability.isEmpty() || ability.get() instanceof Curse || ability.get() instanceof Gifts) return;
                if (ability.isPresent()) {
                    specConfig.alwaysShowSet.add(ability.get());
                }
            });
            specConfig.showIfHasSet.clear();
            specConfig.showIfHas.forEach(s -> {
                Optional<Enum<?>> ability = AbilityUtils.fromString(s);
                if (ability.isPresent()) {
                    specConfig.showIfHasSet.add(ability.get());
                }
            });
        }
    }

    private static void loadTextures(ModConfig.Textures textures) {
        loadEnumMap(Actives.values(), textures.actives);
        loadEnumMap(Passives.values(), textures.passives);
        loadEnumMap(Curse.values(), textures.curses);
        loadEnumMap(Gifts.values(), textures.gifts);
    }

    private static <T extends Enum<?>> void loadEnumMap(T[] values, Map<T, AbilityIcon.Type> map) {
        for (T key : values) {
            map.putIfAbsent(key, AbilityIcon.Type.VANILLA);
        }
    }

    private static void loadSpecConfig(Spec spec, SpecConfig config) {
        if (config.name == null) {
            switch (spec) {
                case STEEL:
                    config.name = "Steelsage";
                    break;
                case SHADOW:
                    config.name = "Shadow";
                    break;
                case FLAME:
                    config.name = "Flame";
                    break;
                case FROST:
                    config.name = "Frost";
                    break;
                case WIND:
                    config.name = "Wind";
                    break;
                case EARTH:
                    config.name = "Earth";
                    break;
                case DAWN:
                    config.name = "Dawn";
                    break;
            }
        }

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
                config.specPriority.putIfAbsent(AbilitySpec.SHADOW, 9);
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 9);
                config.specPriority.putIfAbsent(AbilitySpec.PRISMATIC, 9);
                break;
            case EARTH:
                config.specPriority.putIfAbsent(AbilitySpec.EARTH, 0);
                config.specPriority.putIfAbsent(AbilitySpec.WIND, 1);
                config.specPriority.putIfAbsent(AbilitySpec.SHADOW, 2);
                config.specPriority.putIfAbsent(AbilitySpec.DAWN, 3);
                config.specPriority.putIfAbsent(AbilitySpec.STEEL, 6);
                config.specPriority.putIfAbsent(AbilitySpec.FROST, 7);
                config.specPriority.putIfAbsent(AbilitySpec.FLAME, 8);
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

        if (config.activeSortOrder.size() != SpecConfig.ActiveSorters.values().length - 1) {
            config.activeSortOrder.clear();
            config.activeSortOrder.add(SpecConfig.ActiveSorters.SLOT);
            config.activeSortOrder.add(SpecConfig.ActiveSorters.SPEC);
            config.activeSortOrder.add(SpecConfig.ActiveSorters.DISABLED);
            config.activeSortOrder.add(SpecConfig.ActiveSorters.DISABLED);
            config.activeSortOrder.add(SpecConfig.ActiveSorters.DISABLED);
            config.activeSortOrder.add(SpecConfig.ActiveSorters.DISABLED);
        }

        if (config.passiveSortOrder.size() != SpecConfig.PassiveSorters.values().length - 1) {
            config.passiveSortOrder.clear();
            config.passiveSortOrder.add(SpecConfig.PassiveSorters.SPEC);
            config.passiveSortOrder.add(SpecConfig.PassiveSorters.DISABLED);
            config.passiveSortOrder.add(SpecConfig.PassiveSorters.DISABLED);
            config.passiveSortOrder.add(SpecConfig.PassiveSorters.DISABLED);
        }
    }

    private ConfigDefaults() {}
}
