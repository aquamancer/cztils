package com.aquamancer.cztils.config;

import com.aquamancer.czlib.api.abils.Spec;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Config(name = Cztils.MOD_ID)
public class ModConfig implements ConfigData {
    public enum InGameRenderMode { ALWAYS, TABLIST, OFF }

    @ConfigEntry.Category("hud")
    public boolean hudEnabled = true;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public InGameRenderMode inGameRenderMode = InGameRenderMode.ALWAYS;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public boolean renderInInventory = true;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public float verticalPos = 0.35f;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public float horizontalPos = 0.005f;

    @ConfigEntry.Category("hud")
    public float textScale = 1.0f;

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Category("hud")
    public NametagEntry nametag = new NametagEntry();

    public static class NametagEntry {
        @ConfigEntry.Gui.Tooltip
        public String nametagFormat = "{name} {spec} - {grave}: {hp}";
        public boolean showHpAsPercentage = false;
        public double midHp = 0.7;
        public double lowHp = 0.5;
        public double critHp = 0.25;
        @ConfigEntry.ColorPicker
        public int goodHpColor = 0x33ef2f;
        @ConfigEntry.ColorPicker
        public int midHpColor = 0xd9ef2f;
        @ConfigEntry.ColorPicker
        public int lowHpColor = 0xefa22f;
        @ConfigEntry.ColorPicker
        public int critHpColor = 0xef472d;
        public boolean goodHpBolded = true;
        public boolean midHpBolded = true;
        public boolean lowHpBolded = true;
        public boolean critHpBolded = true;
        @ConfigEntry.ColorPicker
        public int graveColor = 0xffffff;
        public double critGrave = 5.15;
        @ConfigEntry.ColorPicker
        public int critGraveColor = 0xef472d;
        public boolean critGraveBolded = true;
    }

    @ConfigEntry.Category("hud")
    public int iconSize = 16;

    @ConfigEntry.Category("hud")
    public int borderWidth = 1;

    @ConfigEntry.Category("hud")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int backgroundFill = 0x88000000;

    @ConfigEntry.Category("hud")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int grayedOut = 0x80888888;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public float activesOffsetX = 0;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public float activesOffsetY = 0;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public float passivesOffsetX = 0;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public float passivesOffsetY = 0;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public int playerSpacing = 8;

    @ConfigEntry.Category("hud")
    @ConfigEntry.Gui.Tooltip
    public boolean showSelf = true;

    @ConfigEntry.Category("specs")
    @SpecConfigs
    public Map<Spec, SpecConfig> specConfigs = ConfigDefaults.createDefaultSpecConfigs();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SpecConfigs {}

    public ModConfig() {}
}