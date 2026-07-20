package com.aquamancer.cztils.hud;

public abstract class AbilityIcon extends HudElement {
    public enum Type { VANILLA, UMM }
    protected Rectangle border;
    protected Rectangle texture;
    protected int borderColor = 0xFF000000;
    protected int backgroundFill = 0x0;
    protected int grayedOut = 0x0;

    public AbilityIcon(int x, int y, int w, int h, int borderWidth) {
        super(x, y);
        this.border = new Rectangle(x, y, w, h);
        this.texture = new Rectangle(x + borderWidth, y + borderWidth, w - borderWidth*2, h - borderWidth*2);
    }

    public AbilityIcon setBorderColor(int argb) {
        this.borderColor = argb;
        return this;
    }

    public AbilityIcon setBackgroundFill(int argb) {
        this.backgroundFill = argb;
        return this;
    }

    public AbilityIcon setGrayedOut(int argb) {
        this.grayedOut = argb;
        return this;
    }
}
