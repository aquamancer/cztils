package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.event.ZenithApiUpdateEvents;
import com.aquamancer.cztils.Cztils;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2i;

import java.util.*;

public class Hud extends HudElement {
    public final Map<String, Player> party = new HashMap<>();
    private List<Player> sorted;

    public Hud(int x, int y) {
        super(x, y);

        ZenithApiUpdateEvents.PARTY_MEMBER.register(names -> {
            party.keySet().retainAll(names);
            names.forEach(name -> party.putIfAbsent(
                    name, new Player(0, 0, new Vector2i(0, 10), new Vector2i(0, 10+2+Cztils.config.iconSize), Cztils.config.iconSize)
            ));
        });
    }

    private void sort() {
        Optional<PartyMember> self = ZenithApi.getInstance().getSelf();
        if (self.isEmpty() || self.get().getCharmedSpec().isEmpty()) {
            this.sorted = new ArrayList<>(this.party.values());
            return;
        }
        Spec selfSpec = self.get().getCharmedSpec().get();
        this.sorted = this.party.values().stream().sorted(
                Comparator.nullsLast(Comparator.comparing(
                        Player::getSpec,
                        new Spec.SpecComparator(Cztils.config.specConfigs.get(selfSpec).teammatePriority)
                ))
        ).toList();
    }

    public void rebuild() {
        party.values().forEach(Player::rebuild);
    }

    @Override
    public void render(DrawContext context) {
        party.values().forEach(player -> player.render(context));
    }
}
