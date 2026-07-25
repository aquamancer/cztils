package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.event.ZenithApiUpdateEvents;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HudManager extends HudElement {
    private final Map<String, Player> party = new HashMap<>();
    private List<Player> sorted;

    public HudManager(int x, int y) {
        super(x, y);

//        ZenithApiUpdateEvents.PARTY_MEMBER.register(names -> {
//            party.keySet().retainAll(names);
//            names.forEach(name -> party.putIfAbsent(name, new Player(0, 0)))
//        })
    }


    @Override
    public void render(DrawContext context) {
        party.values().forEach(player -> player.render(context));
    }
}
