package com.aquamancer.cztils.hud;

import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Nametag extends HudElement {
    private enum StringType { LITERAL, PLACEHOLDER }
    private enum PlaceholderType {
        NAME("{name}", (n) -> n.name, (n, c) -> (s) -> s.withColor(c.nameColor)),
        SPEC("{spec}", (n) -> n.config.name, (n, c) -> s -> s.withColor(c.specColor)),
        HP(
                "{hp}",
                nametag -> {
                    if (Cztils.config.nametag.showHpAsPercentage) {
                        return String.valueOf(Math.round(nametag.hp / nametag.hpMax * 100)) + '%';
                    } else {
                        return String.valueOf(Math.round(nametag.hp * 10) / 10.0) + '/' + Math.round(nametag.hpMax * 10) / 10.0;
                    }
                },
                (n, c) -> (s) -> {
                    double percent = n.hp / n.hpMax;
                    if (percent < Cztils.config.nametag.critHp) {
                        return s.withColor(Cztils.config.nametag.critHpColor).withBold(Cztils.config.nametag.critHpBolded);
                    } else if (percent < Cztils.config.nametag.lowHp) {
                        return s.withColor(Cztils.config.nametag.lowHpColor).withBold(Cztils.config.nametag.lowHpBolded);
                    } else if (percent < Cztils.config.nametag.midHp) {
                        return s.withColor(Cztils.config.nametag.midHpColor).withBold(Cztils.config.nametag.midHpBolded);
                    } else {
                        return s.withColor(Cztils.config.nametag.goodHpColor).withBold(Cztils.config.nametag.goodHpBolded);
                    }
                }
        ),
        GRAVE(
                "{grave}",
                (n) -> String.valueOf(n.graveTimer) + 's',
                (n, c) -> (s) -> {
                    if (n.graveTimer <= Cztils.config.nametag.critGrave) {
                        return s.withColor(Cztils.config.nametag.critGraveColor).withBold(Cztils.config.nametag.critGraveBolded);
                    } else {
                        return s.withColor(Cztils.config.nametag.graveColor);
                    }
                }
        );

        private static final Pattern BRACKET = Pattern.compile("\\{[^}]+}");
        private static final Map<String, PlaceholderType> FROM_STRING =
                Arrays.stream(values())
                        .collect(Collectors.toUnmodifiableMap(
                                PlaceholderType::getPlaceholder,
                                Function.identity()
                        ));

        private final String placeholder;
        private final Function<Nametag, String> replacer;
        private final BiFunction<Nametag, SpecConfig, UnaryOperator<Style>> styler;

        PlaceholderType(String placeholder, Function<Nametag, String> replacer, BiFunction<Nametag, SpecConfig, UnaryOperator<Style>> styler) {
            this.placeholder = placeholder;
            this.replacer = replacer;
            this.styler = styler;
        }

        private String getPlaceholder() {
            return this.placeholder;
        }

        private String getReplacement(Nametag nametag) {
            return this.replacer.apply(nametag);
        }

        private UnaryOperator<Style> getStyler(Nametag nametag, SpecConfig config) {
            return this.styler.apply(nametag, config);
        }

        private static Optional<PlaceholderType> fromString(String string) {
            return Optional.ofNullable(FROM_STRING.get(string));
        }
    }

    private SpecConfig config;
    private String name = "";
    private double hp, hpMax, graveTimer;
    private MutableText text = Text.empty();

    private List<StringType> formatOrder = new ArrayList<>();
    private List<String> literals = new ArrayList<>();
    private List<PlaceholderType> placeholders = new ArrayList<>();

    public Nametag(int x, int y, SpecConfig config) {
        super(x, y);
        this.config = config;
        this.setFormat(Cztils.config.nametag.nametagFormat);
    }

    void rebuild() {
        this.text = Text.empty();
        Iterator<String> literals = this.literals.iterator();
        Iterator<PlaceholderType> placeholders = this.placeholders.iterator();
        for (StringType type : this.formatOrder) {
            switch (type) {
                case LITERAL:
                    if (literals.hasNext()) {
                        this.text.append(literals.next());
                    }
                    break;
                case PLACEHOLDER:
                    if (placeholders.hasNext()) {
                        PlaceholderType placeholder = placeholders.next();
                        this.text.append(Text.literal(placeholder.getReplacement(this)).styled(placeholder.getStyler(this, this.config)));
                    }
                    break;
            }
        }
    }

    public Nametag setFormat(String formatString) {
        this.formatOrder.clear();
        this.literals.clear();
        this.placeholders.clear();

        if (formatString.isBlank()) return this;
        Matcher matcher = PlaceholderType.BRACKET.matcher(formatString);
        int literalStart = 0;
        while (matcher.find()) {
            String match = matcher.group();
            Optional<PlaceholderType> type = PlaceholderType.fromString(match);
            if (type.isEmpty()) continue;
            // capture previous literal
            if (matcher.start() > literalStart) {
                this.formatOrder.add(StringType.LITERAL);
                this.literals.add(formatString.substring(literalStart, matcher.start()));
            }
            // capture placeholder
            this.formatOrder.add(StringType.PLACEHOLDER);
            this.placeholders.add(type.get());
            literalStart = matcher.end();
        }
        // capture trailing literal
        if (literalStart < formatString.length()) {
            this.formatOrder.add(StringType.LITERAL);
            this.literals.add(formatString.substring(literalStart));
        }
        return this;
    }

    public Nametag setName(String name) {
        if (this.name.equals(name)) return this;
        this.name = name;
        this.rebuild();
        return this;
    }

    public Nametag setConfig(SpecConfig config) {
        if (this.config == config) return this;
        this.config = config;
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
