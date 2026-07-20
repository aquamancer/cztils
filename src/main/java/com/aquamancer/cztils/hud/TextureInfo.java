package com.aquamancer.cztils.hud;

import net.minecraft.util.Identifier;

public class TextureInfo {
    public static TextureInfo DEFAULT = new TextureInfo("minecraft:air", 0, 0, 0, 0, 0, 0);

    String identifier;
    int u, v, uw, uh, sourceWidth, sourceHeight;

    public TextureInfo(String identifier, int u, int v, int uw, int uh, int sourceWidth, int sourceHeight) {
        this.identifier = identifier;
        this.u = u;
        this.v = v;
        this.uw = uw;
        this.uh = uh;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public int getUw() {
        return uw;
    }

    public int getUh() {
        return uh;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setU(int u) {
        this.u = u;
    }

    public void setV(int v) {
        this.v = v;
    }

    public void setUw(int uw) {
        this.uw = uw;
    }

    public void setUh(int uh) {
        this.uh = uh;
    }

    public void setSourceWidth(int sourceWidth) {
        this.sourceWidth = sourceWidth;
    }

    public void setSourceHeight(int sourceHeight) {
        this.sourceHeight = sourceHeight;
    }
}
