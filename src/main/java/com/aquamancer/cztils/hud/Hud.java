package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.ZenithApi;
import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.event.ZenithApiUpdateEvents;
import com.aquamancer.cztils.Cztils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector2i;

import java.util.*;

public class Hud extends HudElement {
    public final Map<String, Player> party = new HashMap<>();
    private List<Player> sorted = List.of();

    public Hud(int x, int y) {
        super(x, y);

        ZenithApiUpdateEvents.PARTY_MEMBER.register(names -> {
            party.keySet().retainAll(names);
            names.forEach(name -> party.putIfAbsent(
                    name, new Player(0, 0, new Vector2i(0, 10), new Vector2i(0, 10+2+Cztils.config.iconSize), Cztils.config.iconSize)
            ));
            sort();
        });
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

//    private static Player createPlayer() {
//        return new Player()
//    }

    @Override
    public void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0f);
        for (int i = 0; i < sorted.size(); i++) {
            int y = 10+32+12;
            if (y != 0) {
                matrices.translate(0f, y, 0f);
            }
            sorted.get(i).render(context);
        }
        matrices.pop();
    }
}
