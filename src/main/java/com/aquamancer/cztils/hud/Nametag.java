package com.aquamancer.cztils.hud;

import com.aquamancer.cztils.Cztils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Nametag extends HudElement {
    private enum StringType { LITERAL, PLACEHOLDER }
    private enum PlaceholderType {
        NAME("{name}"),
        SPEC("{tree}"),
        HP("{hp}"),
        HP_MAX("{hpmax}"),
        GRAVE("{grave}");

        private static final Map<String, PlaceholderType> FROM_STRING =
                Arrays.stream(values())
                        .collect(Collectors.toUnmodifiableMap(
                                PlaceholderType::getPlaceholder,
                                Function.identity()
                        ));

        private final String placeholder;

        PlaceholderType(String placeholder) {
            this.placeholder = placeholder;
        }

        private String getPlaceholder() {
            return this.placeholder;
        }

        private Optional<PlaceholderType> fromString(String string) {
            return Optional.ofNullable(FROM_STRING.get(string));
        }
    }

    private static final char HEART = '♥';

    private Text name = Text.literal("");
    private Text spec = Text.literal("");
    private double hp, hpMax, graveTimer;
    private Text hpMaxTest = Text.literal("");
    private Text hpMaxText = Text.literal("");

    private List<StringType> formatOrder = new ArrayList<>();
    private List<String> literals = new ArrayList<>();
    private List<String> placeholders = new ArrayList<>();

    public Nametag(int x, int y) {
        super(x, y);
    }

    void rebuild() {
        this.hpString = String.valueOf(Math.round(this.hp * 10) / 10.0);
        this.hpMaxString = String.valueOf(Math.round(this.hpMax * 10) / 10.0);
        this.text = Text.literal(name).append(" (").append(spec).append(", ").append(String.valueOf(graveTimer)).append("s): ").append(this.hpString).append("/").append(this.hpMaxString);
    }

    public Nametag setFormat(String formatString) {

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
