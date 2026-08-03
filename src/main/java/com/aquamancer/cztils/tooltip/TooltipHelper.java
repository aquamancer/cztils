package com.aquamancer.cztils.tooltip;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.Ability;
import com.aquamancer.czlib.api.abils.AbilityUtils;
import com.aquamancer.czlib.api.abils.Gifts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TooltipHelper {
    private static final Map<Ability, BiConsumer<List<Text>, ZenithApi>> operations = new HashMap<>();

    public static void onTooltip(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (lines.isEmpty()) return;
        Map<String, PartyMember> party = ZenithApi.getInstance().getParty();
        if (party.isEmpty()) return;
        if (MinecraftClient.getInstance().currentScreen == null) return;  // prevent lag if somehow a tooltip is being rendered in-game
        Optional<Ability> ability = AbilityUtils.fromString(lines.get(0).getString());
        if (ability.isEmpty()) return;

        BiConsumer<List<Text>, ZenithApi> operation = operations.get(ability.get());
        if (operation != null) {
            operation.accept(lines, ZenithApi.getInstance());
        }
    }

    static {
        // gifts
        operations.put(Gifts.BROODMOTHERS_WEBBING, (tooltip, api) -> {
            tooltip.addAll(api.getParty().values().stream().sorted(Comparator.comparingDouble(PartyMember::getGraveTimer)).map(
                    player -> Text.literal(player.getName() + ": " + player.getGraveTimer())
            ).toList());
        });
        operations.put(Gifts.CALLICARPAS_POINTED_HAT, (tooltip, api) -> {

        });
        operations.put(Gifts.FORSAKEN_GRIMOIRE, (tooltip, api) -> {

        });
        operations.put(Gifts.KALEIDOSCOPIC_LENS, (tooltip, api) -> {
            Optional<PartyMember> self = api.getSelf();
            if (self.isEmpty()) return;

        })
    }
}
