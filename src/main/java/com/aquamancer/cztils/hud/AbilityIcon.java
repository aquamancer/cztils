package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class AbilityIcon extends HudElement {
    protected int w, h;
    protected int borderWidth;
    protected int borderColor;
    protected int backgroundFill;
    protected int grayedOut;
    protected @Nullable String subscript;

    public AbilityIcon(int x, int y, int w, int h, int borderWidth) {
        super(x, y);
        this.w = w;
        this.h = h;
        this.borderWidth = borderWidth;
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
        this.backgroundFill = 0;
        this.borderColor = argb;
        this.grayedOut = argb;
        return this;
    }

    public AbilityIcon setSubscript(@Nullable String text) {
        this.subscript = text;
        return this;
    }
}
