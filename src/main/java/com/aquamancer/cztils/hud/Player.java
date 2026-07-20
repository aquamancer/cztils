package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.Actives;
import com.aquamancer.cztils.Cztils;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Stream;

public class Player extends HudElement {
    private final Nametag nametag;
    private AbilityBar actives, passives;

    private Vector2i activesOffset, passivesOffset;

    // todo change to relative to screen
    public Player(int x, int y, Vector2i activesOffset, Vector2i passivesOffset) {
        super(x, y);
        this.activesOffset = activesOffset;
        this.passivesOffset = passivesOffset;
        this.nametag = new Nametag(this.x, this.y);
    }

    public void setName(String name) {
        this.nametag.setName(name);
    }

    public void setSpec(String spec) {
        this.nametag.setSpec(spec);
    }

    public void setHp(double hp) {
        this.nametag.setHp(hp);
    }

    public void setHpMax(double maxHp) {
        this.nametag.setHpMax(maxHp);
    }

    public void setGraveTimer(double graveTimer) {
        this.nametag.setGraveTimer(graveTimer);
    }

    public void setActives(Set<Actives> actives, Set<Actives> grayedOut, int iconSize) {
        Map<Actives, Boolean> combined = new HashMap<>();
        grayedOut.forEach(a -> combined.put(a, true));
        actives.forEach(a -> combined.put(a, false));  // replaces dupes from grayedOut

        int cap = actives.size() + grayedOut.size();
        List<Map.Entry<Actives, Boolean>> sorted = combined.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).toList();  // todo actual sorting
        List<AbilityIcon> icons = new ArrayList<>(cap);

        for (int i = 0; i < sorted.size(); i++) {
            Actives active = sorted.get(i).getKey();
            Boolean grayed = sorted.get(i).getValue();
            AbilityIcon.Type iconType = Cztils.config.getIconType(active);

            int iconX = this
            switch (iconType) {
                case VANILLA:
                    icons.add(new ItemAbilityIcon(
                    ))
            }
        }
    }
}