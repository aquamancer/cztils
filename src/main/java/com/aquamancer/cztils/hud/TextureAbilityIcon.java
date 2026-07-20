package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.Rarity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Map;

public class TextureAbilityIcon extends AbilityIcon {

    private Identifier identifier;
    private Rectangle sourceRegion;
    private int sourceWidth, sourceHeight;

    public TextureAbilityIcon(int x, int y, int w, int h, int borderWidth, TextureInfo t) {
        super(x, y, w, h, borderWidth);
        this.identifier = new Identifier(t.identifier);
        this.sourceRegion = new Rectangle(t.u, t.v, t.uw, t.uh);
        this.sourceWidth = t.sourceWidth;
        this.sourceHeight = t.sourceHeight;
    }

    @Override
    public void render(DrawContext context) {
        context.fill(border.x, border.y, border.x2, border.y2, borderColor);
        if (backgroundFill != 0) {
            context.fill(texture.x, texture.y, texture.x2, texture.y2, backgroundFill);
        }
        context.drawTexture(
                identifier,
                texture.x, texture.y,
                texture.w, texture.h,
                sourceRegion.x, sourceRegion.y,
                sourceRegion.w, sourceRegion.h,
                sourceWidth, sourceHeight
        );
        if (grayedOut != 0) {
            context.fill(texture.x, texture.y, texture.x2, texture.y2, grayedOut);
        }
    }
}
