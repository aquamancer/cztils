package com.aquamancer.cztils.hud;

import net.minecraft.client.gui.DrawContext;

public abstract class HudElement {
    protected int x, y;

    protected HudElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context) {}
}
