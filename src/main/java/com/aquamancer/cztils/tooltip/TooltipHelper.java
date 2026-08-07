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

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class TooltipHelper {
    private static final Text ANCHOR = Text.literal(new String(Character.toChars(0x2693)));

    private static final Map<Ability<?>, BiConsumer<List<Text>, ZenithApi>> abilitySelectOperations = new HashMap<>();

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
                BiConsumer<List<Text>, ZenithApi> operation = abilitySelectOperations.get(ability.get());
                if (operation != null) {
                    operation.accept(lines, ZenithApi.getInstance());
                }
                break;
        }
    }

    private static MutableText getSpecName(Spec spec) {
        return getSpecName(spec.toAbilitySpec());
    }

    private static MutableText getSpecName(AbilitySpec spec) {
        SpecConfig config = Cztils.config.specConfigs.get(spec.toSpec().orElse(null));
        MutableText result = (Cztils.config.useConfigForTooltips && spec != AbilitySpec.PRISMATIC) ? Text.literal(config.name) : Text.literal(spec.getDisplayName());
        return result.styled(getSpecColorer(spec));
    }

    private static int getSpecColor(AbilitySpec spec) {
        SpecConfig config = Cztils.config.specConfigs.get(spec.toSpec().orElse(null));
        return (Cztils.config.useConfigForTooltips && spec != AbilitySpec.PRISMATIC) ? config.specColor : spec.getColor();
    }

    private static UnaryOperator<Style> getSpecColorer(AbilitySpec spec) {
        return (s) -> s.withColor(getSpecColor(spec));
    }

    private static MutableText createSpecList(Collection<Spec> specs) {
        return createSpecList(specs, (s, t) -> {});
    }

    private static MutableText createSpecList(Collection<Spec> specs, BiConsumer<Spec, MutableText> postOperator) {
        return createSpecList(specs, (s, t) -> {
            postOperator.accept(s, t);
            return t;
        });
    }

    private static MutableText createSpecList(Collection<Spec> specs, BiFunction<Spec, MutableText, MutableText> postOperator) {
        MutableText result = Text.empty();
        int i = 0;
        for (Spec spec : specs) {
            if (i % 4 == 0) {
                if (i != 0) {
                    result.append(",\n");
                }
            } else {
                result.append(Text.literal(", "));
            }
            MutableText name = getSpecName(spec);
            result.append(postOperator.apply(spec, name));
            i++;
        }
        return result;
    }

    private static <T extends Ability<?>> MutableText createAbilityList(Collection<T> abilities) {
        return createAbilityList(abilities, (a, t) -> {});
    }

    private static <T extends Ability<?>> MutableText createAbilityList(Collection<T> abilities, BiConsumer<T, MutableText> postOperator) {
        return createAbilityList(abilities, (a, t) -> {
            postOperator.accept(a, t);
            return t;
        });
    }

    private static <T extends Ability<?>> MutableText createAbilityList(Collection<T> abilities, BiFunction<T, MutableText, MutableText> postOperator) {
        MutableText result = Text.empty();
        int i = 0;
        for (T ability : abilities) {
            if (i % 4 == 0) {
                if (i != 0) {
                    result.append(",\n");
                }
            } else {
                result.append(", ");
            }
            MutableText name = Text.literal(ability.getAbility().name());
            if (ability instanceof HasAbilitySpec hasAbilitySpec) {
                name.styled(getSpecColorer(hasAbilitySpec.getSpec()));
            }
            result.append(postOperator.apply(ability, name));
            i++;
        }
        return result;
    }

    private static MutableText createRemainingAbilityList(AbilitySpec spec, PartyMember player) {
        return createRemainingAbilityList(spec, player, (a, t) -> t);
    }

    private static MutableText createRemainingAbilityList(AbilitySpec spec, PartyMember player, BiFunction<Ability<?>, MutableText, MutableText> postOperator) {
        MutableText line = getSpecName(spec);
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

        if (Cztils.config.a14) {
            long activeCount = player.getActiveCount(spec);
            line.append(" (" + activeCount + "/4 actives)");
        }
        line.append(": ");
        line.append(createAbilityList(remainingAbilities, postOperator));

        return line;
    }

    static {
        // gifts
        abilitySelectOperations.put(Gifts.BROODMOTHERS_WEBBING, (tooltip, api) -> {
            tooltip.addAll(api.getParty().values().stream().sorted(Comparator.comparingDouble(PartyMember::getGraveTimer)).map(
                    player -> {
                        SpecConfig config = Cztils.config.specConfigs.get(player.getCharmedSpec().orElse(null));
                        return Text.literal(player.getName()).styled(s -> s.withColor(config.nameColor))
                                .append(" - ")
                                .append(config.name).styled(s -> s.withColor(config.specColor))
                                .append(": ")
                                .append(String.valueOf(player.getGraveTimer()));
                    }
            ).toList());
        });
        abilitySelectOperations.put(Gifts.CALLICARPAS_POINTED_HAT, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            tooltip.add(Text.literal("Available abilities:"));
            self.get().getSpecs().stream()
                    .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                    .forEach(s -> {
                        tooltip.add(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });
            // todo add curse of pride
        });
        abilitySelectOperations.put(Gifts.FORSAKEN_GRIMOIRE, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            tooltip.add(Text.literal("Eligible actives"));
            self.get().getSpecs().stream()
                    .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                    .forEach(s -> {
                        AbilitySpec spec = s.toAbilitySpec();
                        MutableText line = getSpecName(s).append(": ");

                        EnumSet<Actives> actives = AbilitySpec.getActives(spec);
                        Collection<Actives> eligible = actives.stream()
                                .filter(a -> self.get().isBlocked(a, Cztils.config.a14) != PartyMember.BlockReason.SLOT_TAKEN)
                                .sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                                .toList();

                        line.append(createAbilityList(eligible));
                        tooltip.add(line);
                    });
        });
        abilitySelectOperations.put(Gifts.KALEIDOSCOPIC_LENS, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            MutableText current = Text.literal("Current trees: ");
            current.append(createSpecList(self.get().getSpecs()));
            tooltip.add(current);
            tooltip.add(Text.empty());

            MutableText after = Text.literal("New trees: ");
            after.append(createSpecList(EnumSet.complementOf(self.get().getSpecs())));
            tooltip.add(after);
        });

        abilitySelectOperations.put(Gifts.MEGA_HAMMER, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
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

            tooltip.add(Text.literal("Actives: ").append(createAbilityList(actives.toList())));
            tooltip.add(Text.empty());
            tooltip.add(Text.literal("Passives: ").append(createAbilityList(passives.toList())));
        });

        abilitySelectOperations.put(Gifts.ORB_OF_DARKNESS, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;

            List<Active> actives = self.get().getActives().values().stream().filter(a -> a.getSpec() == AbilitySpec.PRISMATIC).toList();
            List<Passive> passives = self.get().getPassives().values().stream().filter(p -> p.getSpec() == AbilitySpec.PRISMATIC).toList();

            tooltip.add(Text.literal("Actives: ").append(createAbilityList(actives)));
            tooltip.add(Text.empty());
            tooltip.add(Text.literal("Passives: ").append(createAbilityList(passives)));
        });

        abilitySelectOperations.put(Gifts.POETS_QUILL, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            Spec.SpecComparator specSorter = Spec.SpecComparator.fromAbilitySpec(Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null)).specPriority);

            tooltip.add(Text.literal("Current trees"));
            self.get().getSpecs().stream().sorted(specSorter)
                    .forEach(s -> {
                        tooltip.add(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });
            tooltip.add(Text.empty());
            tooltip.add(Text.literal("Available trees"));

            EnumSet.complementOf(self.get().getSpecs()).stream().sorted(specSorter)
                    .forEach(s -> {
                        tooltip.add(createRemainingAbilityList(s.toAbilitySpec(), self.get()));
                    });
        });

        abilitySelectOperations.put(Gifts.PRISMATIC_CUBE, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            tooltip.add(Text.literal("Available replacements:"));
            self.get().getActives().keySet().stream().sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                    .forEach(a -> {
                        Set<Actives> replacements = Actives.getActives(a.getSlot()).get(AbilitySpec.PRISMATIC);
                        if (replacements.isEmpty()) return;
                        tooltip.add(
                                Text.literal(a.getDisplayName()).styled(getSpecColorer(a.getSpec()))
                                        .append(Text.literal(" -> "))
                                        .append(createAbilityList(replacements))
                        );
                    });
        });

        abilitySelectOperations.put(Gifts.PURGING_STONE, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current curses: ").append(createAbilityList(self.get().getCurses(), (c, t) -> {
                t.styled(s -> s.withColor(Curse.COLOR));
            })));
        });

        abilitySelectOperations.put(Gifts.STATUE_OF_REGRET, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current curses: ").append(createAbilityList(self.get().getCurses(), (c, t) -> {
                t.styled(s -> s.withColor(Curse.COLOR));
            })));
        });

        abilitySelectOperations.put(Gifts.VENOM_OF_THE_BROODMOTHER, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current grave timer: " + self.get().getGraveTimer()));
        });
    }
}
