package com.aquamancer.cztils.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class Nametag extends HudElement {
    private static final char HEART = '♥';

    private String name = "";
    private String spec = "";
    private double hp, hpMax, graveTimer;
    private MutableText text = Text.literal("");

    public Nametag(int x, int y) {
        super(x, y);
    }

    private void rebuild() {
        this.text = Text.literal(name).append(" (").append(spec).append(", ").append(String.valueOf(graveTimer)).append("s): ").append(String.valueOf(hp)).append("/").append(String.valueOf(hpMax));
    }

    public Nametag setName(String name) {
        if (this.name.equals(name)) return this;
        this.name = name;
        this.rebuild();
        return this;
    }

    public Nametag setSpec(String spec) {
        if (this.spec.equals(spec)) return this;
        this.spec = spec;
        this.rebuild();
        return this;
    }

    public Nametag setHp(double hp) {
        if (this.hp == hp) return this;
        this.hp = hp;
        this.rebuild();
        return this;
    }

    public Nametag setHpMax(double hp) {
        if (this.hpMax == hp) return this;
        this.hpMax = hp;
        this.rebuild();
        return this;
    }

    public Nametag setGraveTimer(double time) {
        if (this.graveTimer == time) return this;
        this.graveTimer = time;
        this.rebuild();
        return this;
    }

    @Override
    public void render(DrawContext context) {
        context.drawText(
                MinecraftClient.getInstance().textRenderer,
                this.text,
                this.x, this.y,
                0xFFFFFF,
                true
        );
    }
}
