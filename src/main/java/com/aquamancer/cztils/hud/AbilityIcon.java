package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.Rarity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Map;

public abstract class AbilityIcon extends HudElement {
    public static final Map<Rarity, Integer> RARITY_COLORS = Map.of(
            Rarity.COMMON, 0xff9f929c,
            Rarity.UNCOMMON, 0xff70bc6d,
            Rarity.RARE, 0xff705eca,
            Rarity.EPIC, 0xffcd5eca,
            Rarity.LEGENDARY, 0xffe49b20,
            Rarity.TWISTED, 0xff703663
    );
    public static final int DEFAULT_BORDER_COLOR = 0xff444444;
//    public static final int DEFAULT_BORDER_COLOR = 0;
    public static final int CURSE_COLOR = 0xffc41300;
    public static final int PRISMATIC_COLOR = 0xff25f6f5;
    public static final int BACKGROUND_FILL = 0x88000000;

    protected Rectangle border;
    protected Rectangle texture;
    protected int borderColor;
    protected int backgroundFill;
    protected int grayedOut;
    protected @Nullable String subscript;

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

    public AbilityIcon setSubscript(@Nullable String text) {
        this.subscript = text;
        return this;
    }
}
