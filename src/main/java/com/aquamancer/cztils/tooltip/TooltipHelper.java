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
        SpecConfig config = Cztils.config.specConfigs.get(spec);
        MutableText result = Cztils.config.useConfigForTooltips ? Text.literal(config.name) : Text.literal(spec.getDisplayName());
        return result.styled(getSpecColorer(spec.toSpec()));
    }

    private static int getSpecColor(AbilitySpec spec) {
        Optional<Spec> converted = spec.toSpec();
        return (Cztils.config.useConfigForTooltips && spec != AbilitySpec.PRISMATIC) ? Cztils.config.specConfigs.get(converted.orElse(null)).specColor : spec.getColor();
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
        return createAbilityList(
                abilities,
                (ability) -> {
                    if (ability instanceof HasAbilitySpec hasAbilitySpec) {
                        return getSpecColor(hasAbilitySpec.getSpec());
                    }
                    return 0xFFFFFF;
                },
                (s, t) -> {
                    postOperator.accept(s, t);
                    return t;
                }
        );
    }

    private static <T extends Ability<?>> MutableText createAbilityList(Collection<T> abilities, Function<T, Integer> colorer, BiFunction<T, MutableText, MutableText> postOperator) {
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
            name.styled(s -> s.withColor(colorer.apply(ability)));
            result.append(postOperator.apply(ability, name));
            i++;
        }
        return result;
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

            tooltip.add(Text.literal("Remaining abilities:"));
            tooltip.addAll(
                    self.get().getSpecs().stream()
                            .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                            .map(s -> {
                                AbilitySpec spec = s.toSpec();
                                MutableText line = getSpecName(s);

                                if (Cztils.config.a14) {
                                    long activeCount = self.get().getActiveCount(spec);
                                    line.append(" (" + activeCount + " ");
                                    line.append((activeCount == 1) ? "active)" : "actives)");
                                }
                                line.append(": ");

                                EnumSet<Actives> actives = AbilitySpec.getActives(spec);
                                actives.removeAll(self.get().getActiveSet(spec));
                                EnumSet<Passives> passives = AbilitySpec.getPassives(spec);
                                passives.removeAll(self.get().getPassiveSet(spec));
                                List<Ability<?>> combined = new ArrayList<>(actives.stream().sorted(new Actives.ActiveSlotComparator(config.slotPriority)).toList());
                                combined.addAll(passives);

                                line.append(createAbilityList(
                                        combined,
                                        (ability, name) -> {
                                            if (ability instanceof Actives active) {
                                                if (self.get().isBlocked(active, Cztils.config.a14) == PartyMember.BlockReason.SLOT_TAKEN) {
                                                    name.append(ANCHOR);
                                                }
                                            }
                                        }
                                ));
                                return line;
                            }).toList()
            );
            // todo add curse of pride
        });
        abilitySelectOperations.put(Gifts.FORSAKEN_GRIMOIRE, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            tooltip.add(Text.literal("Eligible actives"));
            tooltip.addAll(
                    self.get().getSpecs().stream()
                            .sorted(Spec.SpecComparator.fromAbilitySpec(config.specPriority))
                            .map(s -> {
                                AbilitySpec spec = s.toSpec();
                                MutableText line = getSpecName(s).append(": ");

                                EnumSet<Actives> actives = AbilitySpec.getActives(spec);
                                Collection<Actives> eligible = actives.stream()
                                        .filter(a -> self.get().isBlocked(a, Cztils.config.a14) != PartyMember.BlockReason.SLOT_TAKEN)
                                        .sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                                        .toList();

                                line.append(createAbilityList(eligible));
                                return line;
                            }).toList()
            );
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
            // todo remaining abils for current
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;

            MutableText current = Text.literal("Current trees: ");
            current.append(createSpecList(self.get().getSpecs()));
            tooltip.add(current);
            tooltip.add(Text.empty());

            tooltip.add(Text.literal("Eligible trees:"));
            for (Spec spec : EnumSet.complementOf(self.get().getSpecs())) {
                tooltip.add(
                        getSpecName(spec)
                                .append(": ")
                                .append(createAbilityList(
                                        AbilitySpec.getActives(spec.toSpec()).stream()
                                                .filter(a -> self.get().isBlocked(a, Cztils.config.a14) == PartyMember.BlockReason.NOT_BLOCKED)
                                                .toList()
                                ))
                );
            }
        });

        abilitySelectOperations.put(Gifts.PRISMATIC_CUBE, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            SpecConfig config = Cztils.config.specConfigs.get(self.get().getCharmedSpec().orElse(null));

            self.get().getActives().keySet().stream().sorted(new Actives.ActiveSlotComparator(config.slotPriority))
                    .forEach(a -> {
                        tooltip.add(
                                Text.literal(a.getDisplayName()).styled(getSpecColorer(a.getSpec()))
                                        .append(Text.literal(" -> "))
                                        .append(createAbilityList(a.getSlot().getActives().get(AbilitySpec.PRISMATIC)))
                        );
                    });
        });

        abilitySelectOperations.put(Gifts.PURGING_STONE, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current curses: ").append(createAbilityList(self.get().getCurses(), (c, t) -> t.styled(s -> s.withColor(Curse.COLOR)))));
        });

        abilitySelectOperations.put(Gifts.STATUE_OF_REGRET, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current curses: ").append(createAbilityList(self.get().getCurses(), (c, t) -> t.styled(s -> s.withColor(Curse.COLOR)))));
        });

        abilitySelectOperations.put(Gifts.VENOM_OF_THE_BROODMOTHER, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current grave timer: " + self.get().getGraveTimer()));
        });
    }
}
