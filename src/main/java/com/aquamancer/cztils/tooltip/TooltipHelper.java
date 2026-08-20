package com.aquamancer.cztils.tooltip;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.screens.ZenithScreens;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class TooltipHelper {
//    private static final Text ANCHOR = Text.literal(new String(Character.toChars(0x2693)));
    private static final Text CROSS_MARK = Text.literal("✗").formatted(Formatting.RED);
    private static final Text CHECK_MARK = Text.literal("✓").formatted(Formatting.GREEN);
    private static final int MAX_WORD_WIDTH = 4;
    private static final int FIRST_LINE_WIDTH = 3;

    private static final Map<ZenithScreens, Map<Ability<?>, Consumer<List<Text>>>> tooltips = new EnumMap<>(ZenithScreens.class);
    private static final Map<ZenithScreens, Map<Rarity, List<Consumer<List<Text>>>>> rarityTooltips = new EnumMap<>(ZenithScreens.class);
    private static final Map<ZenithScreens, Map<AbilitySpec, List<Consumer<List<Text>>>>> specTooltips = new EnumMap<>(ZenithScreens.class);

    private static void registerAbilityTooltip(Ability<?> ability, Collection<ZenithScreens> screens, Consumer<List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerAbilityTooltip(ability, screen, modifier);
        }
    }

    private static void registerAbilityTooltip(Ability<?> ability, ZenithScreens screen, Consumer<List<Text>> modifier) {
        tooltips.computeIfAbsent(screen, k -> new HashMap<>())
                .put(ability, modifier);
    }

    private static void registerRarityTooltip(Rarity rarity, Collection<ZenithScreens> screens, Consumer<List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerRarityTooltip(rarity, screen, modifier);
        }
    }

    private static void registerRarityTooltip(Rarity rarity, ZenithScreens screen, Consumer<List<Text>> modifier) {
        rarityTooltips.computeIfAbsent(screen, k -> new EnumMap<>(Rarity.class))
                .computeIfAbsent(rarity, k -> new ArrayList<>())
                .add(modifier);
    }

    private static void registerSpecTooltip(AbilitySpec spec, Collection<ZenithScreens> screens, Consumer<List<Text>> modifier) {
        for (ZenithScreens screen : screens) {
            registerSpecTooltip(spec, screen, modifier);
        }
    }

    private static void registerSpecTooltip(AbilitySpec spec, ZenithScreens screen, Consumer<List<Text>> modifier) {
        specTooltips.computeIfAbsent(screen, k -> new EnumMap<>(AbilitySpec.class))
                .computeIfAbsent(spec, k -> new ArrayList<>())
                .add(modifier);
    }

    static {
        // curses
        // todo test removing envy
        registerAbilityTooltip(Curse.ENVY, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_ADD), (tooltip) -> {
            PartyMember self = ZenithApi.getInstance().getSelf().orElse(null);
            if (self == null) return;

            if (ZenithApi.getInstance().getCurrentFloor() == 1) {
                tooltip.add(createSpecList(Text.literal("New trees: "), self.getInvertedSpecs()));
            } else {
                createEnvyList(tooltip);
            }
        });

        registerAbilityTooltip(Curse.GLUTTONY, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_ADD), (tooltip) -> {
            PartyMember self = ZenithApi.getInstance().getSelf().orElse(null);
            if (self == null) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.getCharmedSpec().orElse(null));
            Comparator<Actives> activeSorter = new Actives.ActiveSlotComparator(config.slotPriority);

            Map<Rarity, List<Actives>> actives = new EnumMap<>(Rarity.class);
            for (Active active : self.getActives().values()) {
                int rarity = active.getRarity().getLevel();
                if (rarity >= Rarity.TWISTED.getLevel()) continue;

                actives.computeIfAbsent(active.getRarity(), (k) -> new ArrayList<>())
                        .add(active.getAbility());
            }

            Map<Rarity, List<Passives>> passives = new EnumMap<>(Rarity.class);
            for (Passive passive : self.getPassives().values()) {
                int rarity = passive.getRarity().getLevel();
                if (rarity >= Rarity.TWISTED.getLevel()) continue;

                passives.computeIfAbsent(passive.getRarity(), (k) -> new ArrayList<>())
                        .add(passive.getAbility());
            }

            int remainingToRemove = 2;
            for (int i = Rarity.LEGENDARY.getLevel(); i >= Rarity.COMMON.getLevel(); i--) {
                Rarity rarity = Rarity.fromInt(i).orElse(null);
                if (rarity == null) break;
                List<Ability<?>> abilities = new ArrayList<>();

                List<Actives> sortedActives = actives.getOrDefault(rarity, List.of());
                sortedActives.sort(activeSorter);
                abilities.addAll(sortedActives);
                abilities.addAll(passives.getOrDefault(rarity, List.of()));

                if (abilities.isEmpty()) continue;

                boolean losingAll = remainingToRemove >= abilities.size();
                String losing = losingAll ? "all" : String.valueOf(remainingToRemove);
                boolean plural = (losingAll || remainingToRemove != 1);

                MutableText prefix = Text.empty();
                prefix.append("Lose ");
                prefix.append(Text.literal(losing).styled(s -> s.withUnderline(true)));
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

        registerAbilityTooltip(Curse.GREED, List.of(ZenithScreens.ABILITY, ZenithScreens.STATUE_OF_REGRET_ADD), (tooltip) -> {
            PartyMember self = ZenithApi.getInstance().getSelf().orElse(null);
            if (self == null) return;
            long amount = self.getGreedAmount()*5;
            tooltip.add(Text.empty().append("Result: -").append(String.valueOf(amount)).append("%").append(" health"));

            boolean hasEarthSpec = self.getSpecs().contains(Spec.EARTH);
            Passive toughness = self.getPassives().get(Passives.TOUGHNESS);
            tooltip.add(Text.empty().append(getSpecName(AbilitySpec.EARTH)).append(" tree: ").append(hasEarthSpec ? CHECK_MARK : CROSS_MARK));
            tooltip.add(Text.empty().append(Text.literal(Passives.TOUGHNESS.getDisplayName()).withColor(getSpecColor(Passives.TOUGHNESS.getSpec()))).append(": ").append((toughness == null) ? CROSS_MARK : toughness.getRarity().getText()));
        });
    }

    static {
        // gifts
        registerAbilityTooltip(Gifts.BROODMOTHERS_WEBBING, ZenithScreens.ABILITY, (tooltip) -> {
            ZenithApi.getInstance().getParty().values().stream()
                    .sorted(Comparator.comparingDouble(PartyMember::getGraveTimer))
                    .forEach(player -> {
                        Spec spec = player.getCharmedSpec().orElse(null);
                        SpecConfig config = Cztils.config.specConfigs.get(spec);
                        tooltip.add(Text.literal(player.getName()).styled(s -> s.withColor(config.nameColor))
                                .append(" - ")
                                .append(getSpecName(spec)).withColor(getSpecColor(spec))
                                .append(": ")
                                .append(String.valueOf(player.getGraveTimer())));
                    }
            );
        });
        registerAbilityTooltip(Gifts.CALLICARPAS_POINTED_HAT, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            self.get().getSpecs().stream()
                    .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                    .forEach(s -> {
                        tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });
            // todo add curse of pride
        });
        registerAbilityTooltip(Gifts.FORSAKEN_GRIMOIRE, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            self.get().getSpecs().stream()
                    .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                    .forEach(s -> {
                        AbilitySpec spec = s.toAbilitySpec();

                        EnumSet<Actives> actives = AbilitySpec.getActives(spec);
                        Collection<Actives> eligible = actives.stream()
                                .filter(a -> self.get().isBlocked(a, Cztils.config.a14) != PartyMember.BlockReason.SLOT_TAKEN)
                                .sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                                .toList();

                        MutableText prefix = getSpecName(s).append(Text.literal(": "));
                        tooltip.addAll(createAbilityList(prefix, eligible));
                    });
        });

        registerAbilityTooltip(Gifts.KALEIDOSCOPIC_LENS, ZenithScreens.ABILITY, TooltipHelper::createEnvyList);

        registerAbilityTooltip(Gifts.MEGA_HAMMER, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));
            Comparator<Active> activeSorter = config.getActiveSorter();
            Comparator<Passive> passiveSorter = config.getPassiveSorter();

            Stream<Active> actives = self.get().getActives().values().stream().filter(a -> a.getRarity().getLevel() <= Rarity.UNCOMMON.getLevel());
            if (activeSorter != null) {
                actives = actives.sorted(activeSorter);
            }
            Stream<Passive> passives = self.get().getPassives().values().stream().filter(p -> p.getRarity().getLevel() <= Rarity.UNCOMMON.getLevel());
            if (passiveSorter != null) {
                passives = passives.sorted(passiveSorter);
            }

            tooltip.addAll(createAbilityList(Text.literal("Actives: "), actives.toList()));
            tooltip.addAll(createAbilityList(Text.literal("Passives: "), passives.toList()));
        });

        registerAbilityTooltip(Gifts.ORB_OF_DARKNESS, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;

            List<Active> actives = self.get().getActives().values().stream().filter(a -> a.getSpec() == AbilitySpec.PRISMATIC).toList();
            List<Passive> passives = self.get().getPassives().values().stream().filter(p -> p.getSpec() == AbilitySpec.PRISMATIC).toList();

            tooltip.addAll(createAbilityList(Text.literal("Actives: "), actives));
            tooltip.addAll(createAbilityList(Text.literal("Passives: "), passives));
        });

        registerAbilityTooltip(Gifts.POETS_QUILL, ZenithScreens.ABILITY, TooltipHelper::createEnvyList);

        registerAbilityTooltip(Gifts.PRISMATIC_CUBE, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            self.get().getActives().keySet().stream().filter(a -> a.getSpec() != AbilitySpec.PRISMATIC).sorted(new Actives.ActiveSlotComparator(config.slotPriority))
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

        registerAbilityTooltip(Gifts.PURGING_STONE, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            tooltip.addAll(createAbilityList(
                    Text.literal("Current curses: "),
                    self.get().getCurses(),
                    (c, t) -> {
                        t.withColor(Curse.COLOR);
                    }
            ));
        });

        registerAbilityTooltip(Gifts.STATUE_OF_REGRET, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            tooltip.addAll(createAbilityList(
                    Text.literal("Current curses: "),
                    self.get().getCurses(),
                    (c, t) -> {
                        t.withColor(Curse.COLOR);
                    }
            ));
        });

        registerAbilityTooltip(Gifts.VENOM_OF_THE_BROODMOTHER, ZenithScreens.ABILITY, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Grave timer: " + self.get().getGraveTimer()));
        });
    }

    public static void onTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (lines.isEmpty()) return;
        Map<String, PartyMember> party = ZenithApi.getInstance().getParty();
        if (party.isEmpty()) return;
        if (MinecraftClient.getInstance().currentScreen == null) return;
        String screenTitle = MinecraftClient.getInstance().currentScreen.getTitle().getString();
        ZenithScreens screen = ZenithScreens.fromString(screenTitle).orElse(null);
        if (screen == null) return;
        Ability<?> ability = Ability.fromString(lines.get(0).getString()).orElse(null);
        if (ability == null) return;
        Consumer<List<Text>> operation = tooltips.get(screen).get(ability);
        if (operation != null) {
            operation.accept(lines);
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
            line.append("None");
            result.add(line);
            return result;
        }

        int i = 0;
        for (T ability : abilities) {
            if (i != 0) {
                line.append(Text.literal(", "));
                if ((i - FIRST_LINE_WIDTH) % MAX_WORD_WIDTH == 0) {
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
        remainingAbilities.removeIf(a -> (a instanceof Actives active) && (player.isBlocked(active, Cztils.config.a14) == PartyMember.BlockReason.SLOT_TAKEN));
//        remainingAbilities.removeAll(player.getActiveSet(spec));  // slot taken should handle this
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

    private static void createEnvyList(List<Text> tooltip) {
        PartyMember self = ZenithApi.getInstance().getSelf().orElse(null);
        if (self == null) return;
        Spec.SpecComparator specSorter = Spec.SpecComparator.fromAbilitySpec(Cztils.config.specConfigs.get(self.getCharmedSpec().orElse(null)).specPriority);

        tooltip.add(Text.literal("Current trees:").styled(s -> s.withUnderline(true)));
        self.getSpecs().stream().sorted(specSorter)
                .forEach(s -> {
                    tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), self));
                });

        tooltip.add(Text.literal("New trees:").styled(s -> s.withUnderline(true)));
        self.getInvertedSpecs().stream().sorted(specSorter)
                .forEach(s -> {
                    tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), self));
                });
    }

    private static MutableText createSpecList(MutableText prefix, Collection<Spec> specs) {
        return createSpecList(prefix, specs, (s, t) -> t);
    }

    private static MutableText createSpecList(MutableText prefix, Collection<Spec> specs, BiFunction<Spec, MutableText, MutableText> postOperator) {
        MutableText line = Text.empty();
        line.append(prefix);
        if (specs.isEmpty()) {
            line.append("None");
            return line;
        }

        int i = 0;
        for (Spec spec : specs) {
            if (i != 0) {
                line.append(", ");
            }
            MutableText name = Text.literal(spec.getDisplayName()).withColor(getSpecColor(spec));
            line.append(postOperator.apply(spec, name));
            i++;
        }
        return line;
    }

    private static MutableText createGreedLine(int delta) {
        PartyMember self = ZenithApi.getInstance().getSelf().orElse(null);
        if (self == null) return Text.empty();
        long before = self.getGreedAmount();
        long after = before + delta;

        MutableText line = Text.empty();
        line.append(Text.literal(Curse.GREED.getDisplayName()).withColor(Curse.COLOR));
        line.append(": ");
        line.append(String.valueOf(before*5)).append(" -> ").append(String.valueOf(after*5)).append("%");
        return line;
    }
}
