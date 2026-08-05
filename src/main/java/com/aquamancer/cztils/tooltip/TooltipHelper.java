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
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class TooltipHelper {
    private static final Map<Ability, BiConsumer<List<Text>, ZenithApi>> abilitySelectOperations = new HashMap<>();

    public static void onTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (lines.isEmpty()) return;
        Map<String, PartyMember> party = ZenithApi.getInstance().getParty();
        if (party.isEmpty()) return;
        if (MinecraftClient.getInstance().currentScreen == null) return;
        String screenTitle = MinecraftClient.getInstance().currentScreen.getTitle().getString();

        switch (screenTitle) {
            case "Select an Ability":
                Optional<Ability> ability = AbilityUtils.fromString(lines.get(0).getString());
                if (ability.isEmpty()) return;
                BiConsumer<List<Text>, ZenithApi> operation = abilitySelectOperations.get(ability.get());
                if (operation != null) {
                    operation.accept(lines, ZenithApi.getInstance());
                }
                break;
        }
    }

    private static MutableText createSpecList(List<Spec> specs) {
        MutableText result = Text.empty();
        for (int i = 0; i < specs.size(); i++) {
            Spec spec = specs.get(i);
            MutableText word;
            if (i % 4 == 0) {
                if (i != 0) {
                    result.append(",\n");
                }
                word = Text.literal(spec.getDisplayName());
            } else {
                word = Text.literal(", ").append(spec.getDisplayName());
            }
            word.styled(s -> s.withColor(spec.getColor()));
            result.append(word);
        }
        return result;
    }

    private static <T extends Ability & HasAbilitySpec> MutableText createAbilityList(List<T> abilities) {
        MutableText result = Text.empty();
        for (int i = 0; i < abilities.size(); i++) {
            T ability = abilities.get(i);
            MutableText word;
            if (i % 4 == 0) {
                if (i != 0) {
                    result.append(",\n");
                }
                word = Text.literal(ability.getAbility().name());
            } else {
                word = Text.literal(", ").append(ability.getAbility().name());
            }
            word.styled(s -> s.withColor(ability.getSpec().getColor()));
            result.append(word);
        }
        return result;
    }

    private static MutableText createAbilityList(List<Ability> abilities, int color) {
        MutableText result = Text.empty();
        for (int i = 0; i < abilities.size(); i++) {
            Ability ability = abilities.get(i);
            MutableText word;
            if (i % 4 == 0) {
                if (i != 0) {
                    result.append(",\n");
                }
                word = Text.literal(ability.getAbility().name());
            } else {
                word = Text.literal(", ").append(ability.getAbility().name());
            }
            word.styled(s -> s.withColor(color));
            result.append(word);
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

        });
        abilitySelectOperations.put(Gifts.FORSAKEN_GRIMOIRE, (tooltip, api) -> {

        });
        abilitySelectOperations.put(Gifts.KALEIDOSCOPIC_LENS, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            MutableText current = Text.literal("Current trees: ");
            current.append(createSpecList(List.copyOf(self.get().getSpecs())));
            tooltip.add(current);
            tooltip.add(Text.empty());

            MutableText after = Text.literal("New trees: ");
            after.append(createSpecList(List.copyOf(EnumSet.complementOf(self.get().getSpecs()))));
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

            MutableText current = Text.literal("Current trees: ");
            current.append(createSpecList(List.copyOf(self.get().getSpecs())));
            tooltip.add(current);
            tooltip.add(Text.empty());

            tooltip.add(Text.literal("Eligible trees:"));
            List<Spec> eligible = List.copyOf(EnumSet.complementOf(self.get().getSpecs()));
            for (Spec spec : eligible) {
                tooltip.add(
                        Text.literal(spec.getDisplayName()).styled(s -> s.withColor(Cztils.config.specConfigs.get(spec).specColor))
                                .append(": ")
                                .append(createAbilityList(
                                        spec.toSpec().getActives().stream()
                                                .filter(a -> self.get().isBlocked(a, true) == PartyMember.BlockReason.NOT_BLOCKED)
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
                                Text.literal(a.getDisplayName()).styled(s -> s.withColor(a.getColor()))
                                        .append(Text.literal(" -> "))
                                        .append(createAbilityList(List.copyOf(a.getSlot().getActives().get(AbilitySpec.PRISMATIC))))
                        );
                    });
        });

        abilitySelectOperations.put(Gifts.PURGING_STONE, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current curses: ").append(createAbilityList(List.copyOf(self.get().getCurses()), Curse.COLOR)));
        });

        abilitySelectOperations.put(Gifts.STATUE_OF_REGRET, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;
            tooltip.add(Text.literal("Current curses: ").append(createAbilityList(List.copyOf(self.get().getCurses()), Curse.COLOR)));
        });

        abilitySelectOperations.put(Gifts.VENOM_OF_THE_BROODMOTHER, (tooltip, api) -> {
//            Optional<PartyMember> self = api.getSelf();
//            if (self.isEmpty()) return;
//            tooltip.add(Text.literal(""))
        });
    }
}
