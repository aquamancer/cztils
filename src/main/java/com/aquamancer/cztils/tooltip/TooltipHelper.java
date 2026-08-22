package com.aquamancer.cztils.tooltip;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.abils.gifts.Gifts;
import com.aquamancer.czlib.api.screens.ZenithScreens;
import com.aquamancer.czlib.internal.TooltipParser;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class TooltipHelper {
    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
//    private static final Text ANCHOR = Text.literal(new String(Character.toChars(0x2693)));
    private static final Text CROSS_MARK = Text.literal("✗").formatted(Formatting.RED);
    private static final Text CHECK_MARK = Text.literal("✓").formatted(Formatting.GREEN);

    private static final Map<ZenithScreens, Map<String, List<BiConsumer<PartyMember, List<Text>>>>> tooltips = new EnumMap<>(ZenithScreens.class);
    private static final Map<ZenithScreens, List<TriConsumer<AbilitySpec, PartyMember, List<Text>>>> specTooltips = new EnumMap<>(ZenithScreens.class);
    private static final Map<ZenithScreens, List<TriConsumer<Rarity, PartyMember, List<Text>>>> rarityTooltips = new EnumMap<>(ZenithScreens.class);
    private static final Map<ZenithScreens, List<BiConsumer<PartyMember, List<Text>>>> globalTooltips = new EnumMap<>(ZenithScreens.class);

    private static void registerAbilityTooltip(Collection<Ability<?>> abilities, Collection<ZenithScreens> screens, BiConsumer<PartyMember, List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerAbilityTooltip(abilities, screen, modifier);
        }
    }

    private static void registerAbilityTooltip(Collection<Ability<?>> abilities, ZenithScreens screen, BiConsumer<PartyMember, List<Text>> modifier) {
        for (Ability<?> ability : abilities) {
            registerAbilityTooltip(ability, screen, modifier);
        }
    }

    private static void registerAbilityTooltip(Ability<?> ability, Collection<ZenithScreens> screens, BiConsumer<PartyMember, List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerAbilityTooltip(ability, screen, modifier);
        }
    }

    private static void registerAbilityTooltip(Ability<?> ability, ZenithScreens screen, BiConsumer<PartyMember, List<Text>> modifier) {
        registerAbilityTooltip(ability.getDisplayName(), screen, modifier);
    }

    private static void registerAbilityTooltip(String firstLine, Collection<ZenithScreens> screens, BiConsumer<PartyMember, List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerAbilityTooltip(firstLine, screen, modifier);
        }
    }

    private static void registerAbilityTooltip(String firstLine, ZenithScreens screen, BiConsumer<PartyMember, List<Text>> modifier) {
        tooltips.computeIfAbsent(screen, k -> new HashMap<>())
                .computeIfAbsent(firstLine, k -> new ArrayList<>())
                .add(modifier);
    }

    private static void registerRarityTooltip(Collection<ZenithScreens> screens, TriConsumer<Rarity, PartyMember, List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerRarityTooltip(screen, modifier);
        }
    }

    private static void registerRarityTooltip(ZenithScreens screen, TriConsumer<Rarity, PartyMember, List<Text>> modifier) {
        rarityTooltips.computeIfAbsent(screen, k -> new ArrayList<>())
                .add(modifier);
    }

    private static void registerSpecTooltip(Collection<ZenithScreens> screens, TriConsumer<AbilitySpec, PartyMember, List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerSpecTooltip(screen, modifier);
        }
    }

    private static void registerSpecTooltip(ZenithScreens screen, TriConsumer<AbilitySpec, PartyMember, List<Text>> modifier) {
        specTooltips.computeIfAbsent(screen, k -> new ArrayList<>())
                .add(modifier);
    }

    private static void registerGlobalTooltip(Collection<ZenithScreens> screens, BiConsumer<PartyMember, List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerGlobalTooltip(screen, modifier);
        }
    }

    private static void registerGlobalTooltip(ZenithScreens screen, BiConsumer<PartyMember, List<Text>> modifier) {
        globalTooltips.computeIfAbsent(screen, k -> new ArrayList<>())
                .add(modifier);
    }

    static {
        // gifts
        registerAbilityTooltip(Gifts.BROODMOTHERS_WEBBING, ZenithScreens.ABILITY, (player, tooltip) -> {
            List<PartyMember> players = ZenithApi.getInstance().getParty().values().stream()
                    .sorted(Comparator.comparingDouble(PartyMember::getGraveTimer))
                    .toList();
            tooltip.addAll(createPlayerList(players));
        });

        registerAbilityTooltip(Gifts.CALLICARPAS_POINTED_HAT, ZenithScreens.ABILITY, (player, tooltip) -> {
            SpecConfig config = Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null));

            player.getSpecs().stream()
                    .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                    .forEach(s -> {
                        tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), player));
                    });
        });
        registerAbilityTooltip(Gifts.FORSAKEN_GRIMOIRE, ZenithScreens.ABILITY, (player, tooltip) -> {
            SpecConfig config = Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null));

            player.getSpecs().stream()
                    .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                    .forEach(s -> {
                        AbilitySpec spec = s.toAbilitySpec();

                        EnumSet<Actives> actives = AbilitySpec.getActives(spec);
                        Collection<Actives> eligible = actives.stream()
                                .filter(a -> player.isBlocked(a, false) == PartyMember.BlockReason.NOT_BLOCKED)
                                .sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                                .toList();

                        MutableText prefix = getSpecName(s).append(Text.literal(": "));
                        tooltip.addAll(createAbilityList(prefix, eligible));
                    });
        });

        registerAbilityTooltip(Gifts.KALEIDOSCOPIC_LENS, ZenithScreens.ABILITY, TooltipHelper::createEnvyList);

        registerAbilityTooltip(Gifts.MEGA_HAMMER, ZenithScreens.ABILITY, (player, tooltip) -> {
            SpecConfig config = Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null));
            Comparator<Active> activeSorter = config.getActiveSorter();
            Comparator<Passive> passiveSorter = config.getPassiveSorter();

            Stream<Active> actives = player.getActives().values().stream().filter(a -> a.getRarity().getLevel() <= Rarity.UNCOMMON.getLevel());
            if (activeSorter != null) {
                actives = actives.sorted(activeSorter);
            }
            Stream<Passive> passives = player.getPassives().values().stream().filter(p -> p.getRarity().getLevel() <= Rarity.UNCOMMON.getLevel());
            if (passiveSorter != null) {
                passives = passives.sorted(passiveSorter);
            }

            tooltip.addAll(createAbilityList(Text.literal("Actives: "), actives.toList()));
            tooltip.addAll(createAbilityList(Text.literal("Passives: "), passives.toList()));
        });

        registerAbilityTooltip(Gifts.ORB_OF_DARKNESS, ZenithScreens.ABILITY, (player, tooltip) -> {
            List<Active> actives = player.getActives().values().stream().filter(a -> a.getSpec() == AbilitySpec.PRISMATIC).toList();
            List<Passive> passives = player.getPassives().values().stream().filter(p -> p.getSpec() == AbilitySpec.PRISMATIC).toList();

            tooltip.addAll(createAbilityList(Text.literal("Actives: "), actives));
            tooltip.addAll(createAbilityList(Text.literal("Passives: "), passives));
        });

        registerAbilityTooltip(Gifts.POETS_QUILL, ZenithScreens.ABILITY, TooltipHelper::createEnvyList);

        registerAbilityTooltip(Gifts.PRISMATIC_CUBE, ZenithScreens.ABILITY, (player, tooltip) -> {
            SpecConfig config = Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null));

            player.getActives().keySet().stream().filter(a -> a.getSpec() != AbilitySpec.PRISMATIC).sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                    .forEach(a -> {
                        Set<Actives> replacements = Actives.getActives(a.getSlot()).get(AbilitySpec.PRISMATIC);
                        if (replacements.isEmpty()) return;
                        tooltip.addAll(
                                createAbilityList(
                                        Text.literal(a.getDisplayName()).withColor(getSpecColor(a.getSpec())).append(" -> "),
                                        replacements
                                )
                        );
                    });
        });

        registerAbilityTooltip(Gifts.PURGING_STONE, ZenithScreens.ABILITY, (player, tooltip) -> {
            tooltip.addAll(createAbilityList(
                    Text.literal("Current curses: "),
                    player.getCurses(),
                    (c, t) -> {
                        t.withColor(Curse.COLOR);
                    }
            ));
        });

        registerAbilityTooltip(Gifts.STATUE_OF_REGRET, ZenithScreens.ABILITY, (player, tooltip) -> {
            tooltip.addAll(createAbilityList(
                    Text.literal("Current curses: "),
                    player.getCurses(),
                    (c, t) -> {
                        t.withColor(Curse.COLOR);
                    }
            ));
        });

        registerAbilityTooltip(Gifts.VENOM_OF_THE_BROODMOTHER, ZenithScreens.ABILITY, (player, tooltip) -> {
            tooltip.add(Text.literal("Grave timer: " + player.getGraveTimer()));
        });
    }

    static {
        // curses
        // todo test removing envy
        registerAbilityTooltip(Curse.ENVY, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_ADD), (player, tooltip) -> {
            if (ZenithApi.getInstance().getCurrentFloor() == 1) {
                tooltip.add(createSpecList(Text.literal("New trees: "), player.getInvertedSpecs()));
            } else {
                createEnvyList(player, tooltip);
            }
        });

        registerAbilityTooltip(Curse.GLUTTONY, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_ADD), (player, tooltip) -> {
            Map<Rarity, List<Actives>> actives = new EnumMap<>(Rarity.class);
            for (Active active : player.getActives().values()) {
                int rarity = active.getRarity().getLevel();
                if (rarity >= Rarity.TWISTED.getLevel()) continue;

                actives.computeIfAbsent(active.getRarity(), (k) -> new ArrayList<>())
                        .add(active.getAbility());
            }

            Map<Rarity, List<Passives>> passives = new EnumMap<>(Rarity.class);
            for (Passive passive : player.getPassives().values()) {
                int rarity = passive.getRarity().getLevel();
                if (rarity >= Rarity.TWISTED.getLevel()) continue;

                passives.computeIfAbsent(passive.getRarity(), (k) -> new ArrayList<>())
                        .add(passive.getAbility());
            }

            int remainingToRemove = 2;
            for (int i = Rarity.LEGENDARY.getLevel(); i >= Rarity.COMMON.getLevel(); i--) {
                Rarity rarity = Rarity.fromInt(i).orElse(null);
                if (rarity == null) break;
                List<Ability<?>> abilities = sortAbilities(
                        player,
                        actives.getOrDefault(rarity, new ArrayList<>()),
                        passives.getOrDefault(rarity, new ArrayList<>())
                );
                if (abilities.isEmpty()) continue;

                boolean losingAll = remainingToRemove >= abilities.size();
                Text losing = losingAll ? Text.literal("all").styled(s -> s.withUnderline(true)) : Text.literal(String.valueOf(remainingToRemove));
                boolean plural = (losingAll || remainingToRemove != 1);

                MutableText prefix = Text.empty();
                prefix.append("Lose ");
                prefix.append(losing);
                prefix.append(" ");
                prefix.append(Text.literal(rarity.getDisplayName()).withColor(rarity.getColor()));
                prefix.append(" ");
                prefix.append(plural ? "abilities" : "ability");
                prefix.append(": ");

                tooltip.addAll(createAbilityList(prefix, abilities));

                remainingToRemove -= abilities.size();
                if (remainingToRemove <= 0) break;
            }
        });

        registerAbilityTooltip(Curse.GREED, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_REMOVE, ZenithScreens.STATUE_OF_REGRET_ADD), (player, tooltip) -> {
            long amount = player.getGreedAmount()*5;
            tooltip.add(Text.empty().append("Amount: -").append(String.valueOf(amount)).append("%").append(" health"));

            boolean hasEarthSpec = player.getSpecs().contains(Spec.EARTH);
            Passive toughness = player.getPassives().get(Passives.TOUGHNESS);
            tooltip.add(Text.empty().append(getSpecName(AbilitySpec.EARTH)).append(" tree: ").append(hasEarthSpec ? CHECK_MARK : CROSS_MARK));
            tooltip.add(Text.empty().append(Text.literal(Passives.TOUGHNESS.getDisplayName()).withColor(getSpecColor(Passives.TOUGHNESS.getSpec()))).append(": ").append((toughness == null) ? CROSS_MARK : toughness.getRarity().getText()));
        });

        registerAbilityTooltip(Curse.PESSIMISM, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_REMOVE, ZenithScreens.STATUE_OF_REGRET_ADD), (player, tooltip) -> {
            Collection<PartyMember> party = ZenithApi.getInstance().getParty().values();
            List<PartyMember> hasPessimism = party.stream()
                    .filter(p -> p.getCurses().contains(Curse.PESSIMISM))
                    .filter(p -> p != player)
                    .toList();
            if (hasPessimism.isEmpty()) return;

            tooltip.add(Text.literal("Other players with ").append(Curse.PESSIMISM.getText()).append(": "));
            tooltip.addAll(createPlayerList(hasPessimism));
        });

        BiConsumer<PartyMember, List<Text>> prideListForTrinket = (player, tooltip) -> {
            player.getAbilityCounts().entrySet().stream()
                    .filter(e -> e.getKey() != AbilitySpec.PRISMATIC)
                    .sorted(Comparator.comparingLong((Map.Entry<AbilitySpec, Long> e) -> e.getValue()).reversed())
                    .forEach(e -> {
                        AbilitySpec spec = e.getKey();
                        long abilityCount = e.getValue();
                        Text count = Text.literal(abilityCount + "/4").formatted(abilityCount > 4 ? Formatting.RED : Formatting.GREEN);

                        MutableText prefix = Text.empty();
                        prefix.append(getSpecName(spec));
                        prefix.append(" ").append(count);
                        if (abilityCount > 0) {
                            prefix.append(": ");
                            tooltip.addAll(createAbilityList(prefix, getSortedAbilities(player, spec)));
                        }
                    });
        };
        BiConsumer<PartyMember, List<Text>> prideList = (player, tooltip) -> {
            long amount = player.getPrideAmount()*10;
            tooltip.add(Text.empty().append(Curse.PRIDE.getText()).append(": +").append(String.valueOf(amount)).append("%"));
            prideListForTrinket.accept(player, tooltip);
        };
        registerAbilityTooltip(Curse.PRIDE, ZenithScreens.TRINKET, prideListForTrinket);
        registerAbilityTooltip(Curse.PRIDE, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_REMOVE, ZenithScreens.STATUE_OF_REGRET_ADD), prideList);
        registerAbilityTooltip(List.of(Gifts.FORSAKEN_GRIMOIRE, Gifts.CALLICARPAS_POINTED_HAT), ZenithScreens.ABILITY, ifHasThen(Curse.PRIDE, prideList));
        registerAbilityTooltip(Aspect.BOX, ZenithScreens.ASPECT, ifHasThen(Curse.PRIDE, prideList));
        registerSpecTooltip(List.of(ZenithScreens.ABILITY, ZenithScreens.GENEROSITY), ifHasThen(Curse.PRIDE, createPrideLine(-1)));
        registerSpecTooltip(ZenithScreens.CLEANSE, ifHasThen(Curse.PRIDE, createPrideLine(1)));
        registerSpecTooltip(ZenithScreens.MUTATE, (spec, player, tooltip) -> {
            ifHasThen(Curse.PRIDE, createPrideLine(1)).accept(spec, player, tooltip);
            ifHasThen(Curse.PRIDE, prideList).accept(player, tooltip);
        });
        registerAbilityTooltip(Gifts.PRISMATIC_CUBE, ZenithScreens.ABILITY, ifHasThen(Curse.PRIDE, prideList));
    }

    static {
        // generosity
        // todo limitation cant track if other players' reached diversity
        registerGlobalTooltip(List.of(ZenithScreens.CLEANSE, ZenithScreens.MUTATE), ifHasThen(Passives.GENEROSITY, (unused, tooltip) -> {
            if (ZenithApi.getInstance().hasCleansed() || ZenithApi.getInstance().hasMutated()) return;
            if (tooltip.isEmpty()) return;
            Ability<?> ability = Ability.fromString(tooltip.get(0).getString()).orElse(null);
            if (!(ability instanceof Actives || ability instanceof Passives)) return;

            List<PartyMember> recipients = ZenithApi.getInstance().getParty().values().stream()
                    .filter(p -> !p.isSelf())
                    .filter(p -> !p.getActives().containsKey(ability) && !p.getPassives().containsKey(ability))
                    .filter(p -> !(ability instanceof Actives a) || p.isBlocked(a, false) == PartyMember.BlockReason.NOT_BLOCKED)
                    .toList();
            tooltip.add(Text.empty().append(Passives.GENEROSITY.getText()).append(Text.literal(" will offer to:")));
            tooltip.addAll(createPlayerList(recipients));
        }));
        // diversity
        BiConsumer<PartyMember, List<Text>> diversitySummary = (unused, tooltip) -> {
            PartyMember self = ZenithApi.getInstance().getSelf().orElse(null);
            if (self == null) return;
            Map<AbilitySpec, Long> counts = self.getAbilityCounts();
            long unique = counts.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .count();

            List<AbilitySpec> achieved = counts.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .toList();
            EnumSet<AbilitySpec> remaining = EnumSet.noneOf(AbilitySpec.class);
            remaining.addAll(achieved);
            remaining = EnumSet.complementOf(remaining);

            tooltip.add(Text.empty().append(Text.literal("Progress: " + unique + "/6")));
            tooltip.add(createAbilitySpecList(Text.literal("Remaining: "), remaining));
        };
        registerAbilityTooltip(Passives.DIVERSITY, List.of(ZenithScreens.ABILITY, ZenithScreens.TRINKET), diversitySummary);
        registerGlobalTooltip(ZenithScreens.GRIMOIRE_TREE, ifHasThen(Passives.DIVERSITY, (unused, tooltip) -> {
            if (tooltip.isEmpty()) return;
            Spec spec = Spec.fromString(tooltip.get(0).getString()).orElse(null);
            if (spec == null) return;
            createDiversityLine(1).accept(spec.toAbilitySpec(), unused, tooltip);
        }));
        registerGlobalTooltip(ZenithScreens.POINTED_HAT, ifHasThen(Passives.DIVERSITY, (player, tooltip) -> {
            if (tooltip.isEmpty()) return;
            Spec spec = Spec.fromString(tooltip.get(0).getString()).orElse(null);
            if (spec == null) return;
            createDiversityLine(3).accept(spec.toAbilitySpec(), player, tooltip);
        }));
        registerSpecTooltip(List.of(ZenithScreens.ABILITY, ZenithScreens.GENEROSITY, ZenithScreens.GRIMOIRE_ABILITY), ifHasThen(Passives.DIVERSITY, createDiversityLine(1)));
        registerSpecTooltip(List.of(ZenithScreens.CLEANSE, ZenithScreens.MUTATE), ifHasThen(Passives.DIVERSITY, createDiversityLine(-1)));
        registerAbilityTooltip(Gifts.PRISMATIC_CUBE, ZenithScreens.ABILITY, ifHasThen(Passives.DIVERSITY, diversitySummary));
    }

    public static void onTooltip(List<Text> lines) {
        if (lines.isEmpty()) return;
        Map<String, PartyMember> party = ZenithApi.getInstance().getParty();
        if (party.isEmpty()) return;
        if (MinecraftClient.getInstance().currentScreen == null) return;
        String screenTitle = MinecraftClient.getInstance().currentScreen.getTitle().getString();
        ZenithScreens screen = ZenithScreens.fromString(screenTitle).orElse(null);
        if (screen == null) return;

        PartyMember player = (screen == ZenithScreens.TRINKET) ? ZenithApi.getInstance().getCurrentlySelectedInTrinket().orElse(null) : ZenithApi.getInstance().getSelf().orElse(null);
        if (player == null) return;

        Map<String, List<BiConsumer<PartyMember, List<Text>>>> firstLineOps = tooltips.get(screen);
        if (firstLineOps != null) {
            List<BiConsumer<PartyMember, List<Text>>> ops = firstLineOps.get(lines.get(0).getString());
            if (ops != null) {
                ops.forEach(op -> op.accept(player, lines));
            }
        }

        if (lines.size() > 1) {
            TooltipParser.SpecRarityParseResult specAndRarity = TooltipParser.parseSpecRarity(lines.get(1).getString());
            AbilitySpec spec = specAndRarity.spec().orElse(null);
            Rarity rarity = specAndRarity.rarity().orElse(null);
            List<TriConsumer<AbilitySpec, PartyMember, List<Text>>> specOps = specTooltips.get(screen);
            if (spec != null && specOps != null) {
                specOps.forEach(op -> op.accept(spec, player, lines));
            }
            List<TriConsumer<Rarity, PartyMember, List<Text>>> rarityOps = rarityTooltips.get(screen);
            if (rarity != null && rarityOps != null) {
                rarityOps.forEach(op -> op.accept(rarity, player, lines));
            }
        }

        List<BiConsumer<PartyMember, List<Text>>> globalOps = globalTooltips.get(screen);
        if (globalOps != null) {
            globalOps.forEach((op) -> op.accept(player, lines));
        }
    }

    private static MutableText getSpecName(@Nullable Spec spec) {
        if (spec == null) return Text.literal(Cztils.config.specConfigs.get(null).name);
        SpecConfig config = Cztils.config.specConfigs.get(spec);
        MutableText result = Cztils.config.useConfigForTooltips ? Text.literal(config.name) : Text.literal(spec.getDisplayName());

        return result.withColor(getSpecColor(spec));
    }

    private static MutableText getSpecName(@Nullable AbilitySpec spec) {
        if (spec == null) return Text.literal(Cztils.config.specConfigs.get(null).name);
        if (spec == AbilitySpec.PRISMATIC) return Text.literal(AbilitySpec.PRISMATIC.getDisplayName()).withColor(getSpecColor(AbilitySpec.PRISMATIC));
        return getSpecName(spec.toSpec().orElse(null));
    }

    private static int getSpecColor(@Nullable Spec spec) {
        if (spec == null) return Cztils.config.specConfigs.get(null).specColor;
        SpecConfig config = Cztils.config.specConfigs.get(spec);
        return Cztils.config.useConfigForTooltips ? config.specColor : spec.getColor();
    }

    private static int getSpecColor(@Nullable AbilitySpec spec) {
        if (spec == null) return Cztils.config.specConfigs.get(null).specColor;
        if (spec == AbilitySpec.PRISMATIC) return AbilitySpec.PRISMATIC.getColor();
        return getSpecColor(spec.toSpec().orElse(null));
    }

    private static List<Ability<?>> getSortedAbilities(PartyMember player, AbilitySpec spec) {
        return sortAbilities(player, player.getActiveSet(spec), player.getPassiveSet(spec));
    }

    private static List<Ability<?>> sortAbilities(PartyMember player, Collection<Actives> actives, Collection<Passives> passives) {
        SpecConfig config = Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null));
        List<Ability<?>> combined = new ArrayList<>();
        List<Actives> sortedActives = actives.stream()
                .sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                .toList();
        combined.addAll(sortedActives);
        combined.addAll(passives);
        return combined;
    }

    private static <T extends Ability<?>> List<MutableText> createAbilityList(MutableText prefix, Collection<T> abilities) {
        return createAbilityList(prefix, abilities, (a, t) -> {});
    }

    private static <T extends Ability<?>> List<MutableText> createAbilityList(MutableText prefix, Collection<T> abilities, BiConsumer<T, MutableText> postOperator) {
        return createAbilityList(prefix, abilities, (a, t) -> {
            postOperator.accept(a, t);
            return t;
        });
    }

    private static <T extends Ability<?>> List<MutableText> createAbilityList(MutableText prefix, Collection<T> abilities, BiFunction<T, MutableText, MutableText> postOperator) {
        List<MutableText> result = new ArrayList<>();
        MutableText line = Text.empty();
        line.append(prefix);
        if (abilities.isEmpty()) {
            result.add(line);
            return result;
        }

        int i = 0;
        for (T ability : abilities) {
            if (i != 0) {
                line.append(Text.literal(", "));
                if ((i - Cztils.config.firstLineWidth) % Cztils.config.lineWidth == 0) {
                    result.add(line);
                    line = Text.empty();
                }
            }
            MutableText name = Text.literal(ability.getDisplayName());
            if (ability instanceof HasAbilitySpec hasAbilitySpec) {
                name.withColor(getSpecColor(hasAbilitySpec.getSpec()));
            }
            line.append(postOperator.apply(ability, name));
            i++;
        }
        if (!line.equals(Text.empty()) && !abilities.isEmpty()) {  // total abilities not divided evenly
            result.add(line);
        }
        return result;
    }

    private static List<MutableText> createRemainingAbilityList(AbilitySpec spec, PartyMember player) {
        return createRemainingAbilityList(spec, player, (a, t) -> t);
    }

    private static List<MutableText> createRemainingAbilityList(AbilitySpec spec, PartyMember player, BiFunction<Ability<?>, MutableText, MutableText> postOperator) {
        SpecConfig config = Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null));

        Set<Ability<?>> remainingAbilities = AbilitySpec.getAllAbilities(
                spec,
                new Actives.ActiveSlotComparator(config.slotPriority),
                null,
                true
        );
        remainingAbilities.removeIf(a -> (a instanceof Actives active) && (player.isBlocked(active, Cztils.config.a14) != PartyMember.BlockReason.NOT_BLOCKED));
        remainingAbilities.removeAll(player.getPassiveSet(spec));

        MutableText prefix = Text.empty();
        prefix.append(getSpecName(spec));
        if (Cztils.config.a14) {
            long activeCount = player.getActiveCount(spec);
            prefix.append(" (" + activeCount + "/4 actives)");
        }
        prefix.append(": ");
        return createAbilityList(prefix, remainingAbilities, postOperator);
    }

    private static void createEnvyList(PartyMember player, List<Text> tooltip) {
        Spec.SpecComparator specSorter = Spec.SpecComparator.fromAbilitySpec(Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null)).specPriority);

        tooltip.add(Text.literal("Current trees:").styled(s -> s.withUnderline(true)));
        player.getSpecs().stream().sorted(specSorter)
                .forEach(s -> {
                    tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), player));
                });

        tooltip.add(Text.literal("New trees:").styled(s -> s.withUnderline(true)));
        player.getInvertedSpecs().stream().sorted(specSorter)
                .forEach(s -> {
                    tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), player));
                });
    }

    private static MutableText createSpecList(MutableText prefix, Collection<Spec> specs) {
        return createSpecList(prefix, specs, (s, t) -> t);
    }

    private static MutableText createSpecList(MutableText prefix, Collection<Spec> specs, BiFunction<Spec, MutableText, MutableText> postOperator) {
        List<AbilitySpec> converted = specs.stream().map(Spec::toAbilitySpec).toList();
        return createAbilitySpecList(prefix, converted, (s, t) -> (s.toSpec().isPresent()) ? postOperator.apply(s.toSpec().get(), t) : t);
    }

    private static MutableText createAbilitySpecList(MutableText prefix, Collection<AbilitySpec> specs) {
        return createAbilitySpecList(prefix, specs, (s, t) -> t);
    }

    private static MutableText createAbilitySpecList(MutableText prefix, Collection<AbilitySpec> specs, BiFunction<AbilitySpec, MutableText, MutableText> postOperator) {
        if (specs.isEmpty()) {
            return prefix;
        }
        MutableText line = Text.empty();
        line.append(prefix);

        int i = 0;
        for (AbilitySpec spec : specs) {
            if (i != 0) {
                line.append(", ");
            }
            MutableText name = getSpecName(spec);
            line.append(postOperator.apply(spec, name));
            i++;
        }
        return line;
    }

    private static List<MutableText> createPlayerList(Collection<PartyMember> players) {
        List<MutableText> result = new ArrayList<>();
        for (PartyMember player : players) {
            Spec spec = player.getCharmedSpec().orElse(null);
            SpecConfig config = Cztils.config.specConfigs.get(spec);
            result.add(Text.literal(player.getName()).styled(s -> s.withColor(config.nameColor))
                    .append(" - ")
                    .append(getSpecName(spec))
                    .append(": ")
                    .append(String.valueOf(player.getGraveTimer())));
        }
        return result;
    }

    private static BiConsumer<PartyMember, List<Text>> ifHasThen(Ability<?> ability, BiConsumer<PartyMember, List<Text>> operator) {
        return (player, tooltip) -> {
            if (!player.hasAbility(ability)) return;
            operator.accept(player, tooltip);
        };
    }

    private static <T> TriConsumer<T, PartyMember, List<Text>> ifHasThen(Ability<?> ability, TriConsumer<T, PartyMember, List<Text>> operator) {
        return (a, player, tooltip) -> {
            if (!player.hasAbility(ability)) return;
            operator.accept(a, player, tooltip);
        };
    }

    private static TriConsumer<AbilitySpec, PartyMember, List<Text>> createPrideLine(int delta) {
        return (spec, player, tooltip) -> {
            if (spec == AbilitySpec.PRISMATIC) return;
            long beforeAbilities = 4 - player.getAbilityCount(spec);
            long afterAbilities = beforeAbilities + delta;
            MutableText beforeText = Text.literal(String.valueOf(beforeAbilities)).formatted((beforeAbilities < 0) ? Formatting.RED : Formatting.GREEN);
            MutableText afterText = Text.literal(String.valueOf(afterAbilities)).formatted((afterAbilities < 0) ? Formatting.RED : Formatting.GREEN);

            MutableText line = Text.empty();
            line.append(Curse.PRIDE.getText()).append(": ").append(getSpecName(spec)).append(" ");
            line.append(beforeText).append(" -> ").append(afterText);
            line.append(" abilities remaining");
            tooltip.add(line);
        };
    }

    private static TriConsumer<AbilitySpec, PartyMember, List<Text>> createDiversityLine(int delta) {
        return (spec, player, tooltip) -> {
            if (!player.isSelf()) return;  // currently only works for self
            if (ZenithApi.getInstance().hasAchievedDiversity()) return;

            Map<AbilitySpec, Long> abilityCounts = player.getAbilityCounts();
            long abilities = abilityCounts.getOrDefault(spec, 0L);
            long result = abilities + delta;
            // only show on thresholds
            if (delta >= 0 && abilities > 0) return;
            if (delta < 0 && result > 0) return;
            long unique = abilityCounts.values().stream().filter(c -> c > 0).count();

            List<AbilitySpec> achieved = abilityCounts.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(Map.Entry::getKey)
                    .toList();
            EnumSet<AbilitySpec> remaining = EnumSet.noneOf(AbilitySpec.class);
            remaining.addAll(achieved);
            remaining = EnumSet.complementOf(remaining);

            MutableText line = Text.empty();
            line.append(Passives.DIVERSITY.getText()).append(" (" + unique + "/6): ").append(getSpecName(spec));
            line.append(" " + abilities + " -> ").append(Text.literal(String.valueOf(result)).formatted(result == 1 ? Formatting.GREEN : Formatting.RED));
            line.append(" (").append(createAbilitySpecList(Text.empty(), remaining)).append(" remaining)");

            tooltip.add(line);
        };
    }
}
