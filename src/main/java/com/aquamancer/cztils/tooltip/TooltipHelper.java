package com.aquamancer.cztils.tooltip;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class TooltipHelper {
    private static final Text ANCHOR = Text.literal(new String(Character.toChars(0x2693)));

    private static final Map<Ability<?>, Consumer<List<Text>>> abilitySelectOperations = new HashMap<>();

    public static void onTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (lines.isEmpty()) return;
        Map<String, PartyMember> party = ZenithApi.getInstance().getParty();
        if (party.isEmpty()) return;
        if (MinecraftClient.getInstance().currentScreen == null) return;
        String screenTitle = MinecraftClient.getInstance().currentScreen.getTitle().getString();

        switch (screenTitle) {
            case "Select an Ability":
                Optional<Ability<?>> ability = AbilityUtils.fromString(lines.get(0).getString());
                if (ability.isEmpty()) return;
                Consumer<List<Text>> operation = abilitySelectOperations.get(ability.get());
                if (operation != null) {
                    operation.accept(lines);
                }
                break;
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

    private static <T extends Ability<?>> List<MutableText> createAbilityList(MutableText prefix, Collection<T> abilities, int width, int firstLineWidth) {
        return createAbilityList(prefix, abilities, width, firstLineWidth, (a, t) -> {});
    }

    private static <T extends Ability<?>> List<MutableText> createAbilityList(MutableText prefix, Collection<T> abilities, int width, int firstLineWidth, BiConsumer<T, MutableText> postOperator) {
        return createAbilityList(prefix, abilities, width, firstLineWidth, (a, t) -> {
            postOperator.accept(a, t);
            return t;
        });
    }

    private static <T extends Ability<?>> List<MutableText> createAbilityList(MutableText prefix, Collection<T> abilities, int width, int firstLineWidth, BiFunction<T, MutableText, MutableText> postOperator) {
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
                if ((i - firstLineWidth) % width == 0) {
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
        return createAbilityList(prefix, remainingAbilities, 4, 3, postOperator);
    }

    static {
        // gifts
        abilitySelectOperations.put(Gifts.BROODMOTHERS_WEBBING, (tooltip) -> {
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
        abilitySelectOperations.put(Gifts.CALLICARPAS_POINTED_HAT, (tooltip) -> {
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
        abilitySelectOperations.put(Gifts.FORSAKEN_GRIMOIRE, (tooltip) -> {
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
                        tooltip.addAll(createAbilityList(prefix, eligible, 4, 3));
                    });
        });
        abilitySelectOperations.put(Gifts.KALEIDOSCOPIC_LENS, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            Spec.SpecComparator specSorter = Spec.SpecComparator.fromAbilitySpec(Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null)).specPriority);

            tooltip.add(Text.literal("Current trees:").styled(s -> s.withUnderline(true)));
            self.get().getSpecs().stream().sorted(specSorter)
                    .forEach(s -> {
                        tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });

            tooltip.add(Text.literal("New trees:").styled(s -> s.withUnderline(true)));
            EnumSet.complementOf(self.get().getSpecs()).stream().sorted(specSorter)
                    .forEach(s -> {
                        tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });
        });

        abilitySelectOperations.put(Gifts.MEGA_HAMMER, (tooltip) -> {
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

            tooltip.addAll(createAbilityList(Text.literal("Actives: "), actives.toList(), 4, 3));
            tooltip.addAll(createAbilityList(Text.literal("Passives: "), passives.toList(), 4, 3));
        });

        abilitySelectOperations.put(Gifts.ORB_OF_DARKNESS, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;

            List<Active> actives = self.get().getActives().values().stream().filter(a -> a.getSpec() == AbilitySpec.PRISMATIC).toList();
            List<Passive> passives = self.get().getPassives().values().stream().filter(p -> p.getSpec() == AbilitySpec.PRISMATIC).toList();

            tooltip.addAll(createAbilityList(Text.literal("Actives: "), actives, 4, 3));
            tooltip.addAll(createAbilityList(Text.literal("Passives: "), passives, 4, 3));
        });

        abilitySelectOperations.put(Gifts.POETS_QUILL, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            Spec.SpecComparator specSorter = Spec.SpecComparator.fromAbilitySpec(Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null)).specPriority);

            tooltip.add(Text.literal("Current trees:").styled(s -> s.withUnderline(true)));
            self.get().getSpecs().stream().sorted(specSorter)
                    .forEach(s -> {
                        tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });

            tooltip.add(Text.literal("New trees:").styled(s -> s.withUnderline(true)));
            EnumSet.complementOf(self.get().getSpecs()).stream().sorted(specSorter)
                    .forEach(s -> {
                        tooltip.addAll(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });
        });

        abilitySelectOperations.put(Gifts.PRISMATIC_CUBE, (tooltip) -> {
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
                                        replacements,
                                        4, 3
                                )
                        );
                    });
        });

        abilitySelectOperations.put(Gifts.PURGING_STONE, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            tooltip.addAll(createAbilityList(
                    Text.literal("Current curses: "),
                    self.get().getCurses(),
                    4, 3,
                    (c, t) -> {
                        t.withColor(Curse.COLOR);
                    }
            ));
        });

        abilitySelectOperations.put(Gifts.STATUE_OF_REGRET, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            tooltip.addAll(createAbilityList(
                    Text.literal("Current curses: "),
                    self.get().getCurses(),
                    4, 3,
                    (c, t) -> {
                        t.withColor(Curse.COLOR);
                    }
            ));
        });

        abilitySelectOperations.put(Gifts.VENOM_OF_THE_BROODMOTHER, (tooltip) -> {
            Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Grave timer: " + self.get().getGraveTimer()));
        });
    }
}
