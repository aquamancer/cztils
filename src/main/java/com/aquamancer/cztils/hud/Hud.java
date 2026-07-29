package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.event.ZenithApiStateEvents;
import com.aquamancer.czlib.api.event.ZenithApiUpdateEvents;
import com.aquamancer.cztils.Cztils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Consumer;

public class Hud {
    public final Map<String, Player> party = new HashMap<>();
    private List<Player> sorted = List.of();

    public Hud() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Map.Entry<String, Player> entry : this.party.entrySet()) {
                Optional<PartyMember> data = ZenithApi.getInstance().getPlayer(entry.getKey());
                if (data.isEmpty()) continue;
                if (data.get().getEntity() == null) continue;
                entry.getValue().setHp(data.get().getEntity().getHealth() + data.get().getEntity().getAbsorptionAmount());
                entry.getValue().setHpMax(data.get().getEntity().getMaxHealth());
            }
        });

        ZenithApiStateEvents.EXIT_ZENITH_SHARD.register((p, c) -> {
            party.clear();
            sorted = List.of();
        });

        ZenithApiUpdateEvents.PARTY_MEMBER.register(names -> {
            party.keySet().retainAll(names);
            names.forEach(name -> party.putIfAbsent(
                    name, new Player().setName(name)
            ));
            sort();
        });

        Map<Event<ZenithApiUpdateEvents.PartyMemberUpdate>, Consumer<PartyMember>> listeners = Map.of(
                ZenithApiUpdateEvents.ACTIVE, p -> party.get(p.getName()).setActives(p.getActives().values(), p.getSpecs()),
                ZenithApiUpdateEvents.PASSIVE, player -> party.get(player.getName()).setPassives(player.getPassives().values(), player.getSpecs()),
                ZenithApiUpdateEvents.CURSE, player -> party.get(player.getName()).setCurses(player.getCurses()),
                ZenithApiUpdateEvents.GIFT, player -> party.get(player.getName()).setGifts(player.getGifts().values()),
                ZenithApiUpdateEvents.GRAVE_TIMER, player -> party.get(player.getName()).setGraveTimer(player.getGraveTimer()),
                ZenithApiUpdateEvents.VZC, player -> {
                    party.get(player.getName()).setSpec(player.getCharmedSpec().orElse(null));
                    rebuild();
                }
        );
        for (Map.Entry<Event<ZenithApiUpdateEvents.PartyMemberUpdate>, Consumer<PartyMember>> entry : listeners.entrySet()) {
            entry.getKey().register(player -> {
                if (this.party.putIfAbsent(player.getName(), new Player()) == null) {
                    sort();
                }
                entry.getValue().accept(player);
            });
        }
    }

    public void sort() {
        Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
        if (self.isEmpty() || self.get().getCharmedSpec().isEmpty()) {
            this.sorted = new ArrayList<>(this.party.values());
            return;
        }
        Spec selfSpec = self.get().getCharmedSpec().get();
        String selfName = ZenithApi.getInstance().getSelfName();
        this.sorted = this.party.entrySet().stream()
                .filter(p -> Cztils.config.showSelf || !p.getKey().equals(selfName))
                .map(Map.Entry::getValue).sorted(
                        Comparator.nullsLast(Comparator.comparing(
                                Player::getSpec,
                                new Spec.SpecComparator(Cztils.config.specConfigs.get(selfSpec).teammatePriority
                        ))
                )
        ).toList();
    }

    public void rebuild() {
        this.sort();
        party.values().forEach(Player::rebuild);
    }

    public void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(context.getScaledWindowWidth() * Cztils.config.horizontalPos, context.getScaledWindowHeight() * Cztils.config.verticalPos, 0f);
        for (int i = 0; i < sorted.size(); i++) {
            double y = 10*Cztils.config.textScale + 2*Cztils.config.iconSize + Cztils.config.playerSpacing;
            matrices.translate(0f, y, 0f);
            sorted.get(i).render(context);
        }
        matrices.pop();
    }
}
