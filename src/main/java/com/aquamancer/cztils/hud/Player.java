package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.ActiveType;
import com.aquamancer.cztils.Cztils;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Player extends HudElement {
    private final Nametag nametag;
    private AbilityBar actives, passives;

    private Vector2i activesOffset, passivesOffset;

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

    public void setActives(Set<ActiveType> actives) {
        List<AbilityIcon> icons = new ArrayList<>(actives.size());
        for (ActiveType active : actives) {
            AbilityIcon.Type iconType = Cztils.config.getIconType((Enum<?>) active);
            switch (iconType) {
                case VANILLA:
                case UMM:
            }
        }
    }
}