package com.aquamancer.cztils.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class AbilityBar extends HudElement {
    private final List<AbilityIcon> abilities;

    public AbilityBar(int x, int y, List<AbilityIcon> abilities) {
        super(x, y);
        this.abilities = abilities;
    }

    @Override
    public void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0f);
        for (AbilityIcon icon : abilities) {
            icon.render(context);
        }
        matrices.pop();
    }
}
