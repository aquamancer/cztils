package com.aquamancer.cztils.hud;

import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class AbilityBar extends HudElement {
    private final List<TextureAbilityIcon> abilities;

    public AbilityBar(int x, int y, List<TextureAbilityIcon> abilities) {
        super(x, y);
        this.abilities = abilities;
    }

    @Override
    public void render(DrawContext context) {
        for (TextureAbilityIcon icon : abilities) {
            icon.render(context);
        }
    }
}
