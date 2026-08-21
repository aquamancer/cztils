package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.Spec;
import com.aquamancer.czlib.api.event.ZenithApiStateEvents;
import com.aquamancer.czlib.api.event.ZenithApiUpdateEvents;
import com.aquamancer.cztils.Cztils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.function.Consumer;

public class Hud {
    public enum PositionAnchor { CENTERED, TOP }

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
                ZenithApiUpdateEvents.ACTIVE, player -> {
                    party.get(player.getName()).setActives(player);
                    party.get(player.getName()).setCurses(player);  // for greed/pride
                },
                ZenithApiUpdateEvents.PASSIVE, player -> {
                    party.get(player.getName()).setPassives(player);
                    party.get(player.getName()).setCurses(player);  // for greed/pride
                },
                ZenithApiUpdateEvents.CURSE, player -> party.get(player.getName()).setCurses(player),
                ZenithApiUpdateEvents.GIFT, player -> party.get(player.getName()).setGifts(player),
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

    public void render(DrawContext context, Player.RenderMode renderMode) {
        if (renderMode == Player.RenderMode.OFF) return;
        if (this.sorted.isEmpty()) return;

        float playerHeight = 10*Cztils.config.nametag.textScale;
        if (renderMode == Player.RenderMode.ALL) {
            playerHeight += 2*Cztils.config.iconSize + Cztils.config.playerSpacing;
        }

        float xi = context.getScaledWindowWidth()*Cztils.config.horizontalPos;
        float yi = context.getScaledWindowHeight()*Cztils.config.verticalPos;
        if (Cztils.config.positionAnchor == PositionAnchor.CENTERED) {
            yi -= this.sorted.size()/2.0f*playerHeight;
        }

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(xi, yi, 0f);
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).render(context, renderMode);
            matrices.translate(0f, playerHeight, 0f);
        }
        matrices.pop();
    }
}
