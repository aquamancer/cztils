package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.textures.ZenithTextures;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Stream;

public class Player extends HudElement {
    private final Nametag nametag = new Nametag(0, 0);
    private Spec spec;
    private AbilityBar actives = new AbilityBar(0, 0, List.of());
    private AbilityBar curses = new AbilityBar(0, 0, List.of());
    private AbilityBar passives = new AbilityBar(0, 0, List.of());
    private AbilityBar gifts = new AbilityBar(0, 0, List.of());

    private Vector2i activesOffset, passivesOffset;

    public Player(int x, int y, Vector2i activesOffset, Vector2i passivesOffset) {
        super(x, y);
        this.activesOffset = activesOffset;
        this.passivesOffset = passivesOffset;
    }

    public Player setName(String name) {
        this.nametag.setName(name);
        return this;
    }

    public Player setSpec(Spec spec) {
        this.spec = spec;
        this.nametag.setSpec(Cztils.config.specConfigs.get(spec).name);
        return this;
    }

    public Player setHp(double hp) {
        this.nametag.setHp(hp);
        return this;
    }

    public Player setHpMax(double maxHp) {
        this.nametag.setHpMax(maxHp);
        return this;
    }

    public Player setGraveTimer(double graveTimer) {
        this.nametag.setGraveTimer(graveTimer);
        return this;
    }

    public Player setActives(Set<Active> actives, int iconSize) {
        Map<Active, Boolean> combined = new HashMap<>();
        Comparator<Active> sorter = null;
        SpecConfig specConfig = Cztils.config.specConfigs.get(this.spec);
        if (specConfig != null) {
            specConfig.alwaysShowSet.forEach(e -> {
                if (e instanceof Actives grayedOut) {
                    Optional<AbilitySpec> grayedOutSpec = AbilitySpec.fromAbilityName(grayedOut.getDisplayName()); // giga jank
                    if (grayedOutSpec.isEmpty()) return;
                    combined.put(new Active(grayedOut, grayedOutSpec.get(), null), true);
                }
            });

            for (SpecConfig.ActiveSorters activeSorter : specConfig.activeSortOrder) {
                Comparator<Active> nextSorter = specConfig.getSorter(activeSorter);
                if (nextSorter == null) continue;
                if (sorter == null) {
                    sorter = nextSorter;
                } else {
                    sorter = sorter.thenComparing(nextSorter);
                }
            }
        }
        actives.forEach(a -> {
            combined.remove(a);  // put() on a dupe only replaces the value
            combined.put(a, false);
        });

        Stream<Map.Entry<Active, Boolean>> sortedStream = combined.entrySet().stream();
        if (sorter != null) {
            sortedStream = sortedStream.sorted(Map.Entry.comparingByKey(sorter));
        }
        List<Map.Entry<Active, Boolean>> sorted = sortedStream.toList();

        List<AbilityIcon> icons = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Actives active = sorted.get(i).getKey().getAbility();
            Rarity rarity = sorted.get(i).getKey().getRarity();
            Boolean grayed = sorted.get(i).getValue();
            AbilityIcon.Type iconType = Cztils.config.textures.getIconType(active);

            // relative to ability bar
            int iconX = i*(iconSize);
            int iconY = 0;
            switch (iconType) {
                case UMM:
                    // todo do later
//                    icons.add(new TextureAbilityIcon(
//                            iconX, iconY,
//                            iconSize, iconSize,
//                            1,
//                            new TextureInfo(
//
//                            )
//                    ))
                case VANILLA:
                    AbilityIcon icon = new ItemAbilityIcon(
                            iconX, iconY,
                            iconSize, iconSize,
                            Cztils.config.getBorderWidth(),
                            ZenithTextures.getItem(active).orElse(new ItemStack(Items.AIR))
                    );
                    icon.setBackgroundFill(AbilityIcon.BACKGROUND_FILL);
                    if (grayed) {
                        icon.setBorderColor(AbilityIcon.DEFAULT_BORDER_COLOR);
                        icon.setGrayedOut(Cztils.config.grayedOut);
                    } else {
                        icon.setBorderColor(AbilityIcon.RARITY_COLORS.get(rarity));
                    }
                    icons.add(icon);
            }
        }

        this.actives = new AbilityBar(activesOffset.x, activesOffset.y, icons);
        return this;
    }

    public Player setCurses(Set<Curse> curseSet, int iconSize) {
        List<AbilityIcon> icons = createUniformAbilities(curseSet, iconSize, Cztils.config.getBackgroundFill(), AbilityIcon.CURSE_COLOR);
        this.curses = new AbilityBar(passivesOffset.x, passivesOffset.y, icons);
        return this;
    }

    public Player setGifts(Set<Gift> curseSet, int iconSize) {
        return this;
    }

    public static <T extends Enum<?>> List<AbilityIcon> createUniformAbilities(Set<T> abilitySet, int iconSize, int backgroundFill, int borderColor) {
        List<AbilityIcon> icons = new ArrayList<>(abilitySet.size());
        List<T> abilities = new ArrayList<>(abilitySet);
        for (int i = 0; i < abilities.size(); i++) {
            T ability = abilities.get(i);
            int iconX = i*iconSize;
            int iconY = 0;

            AbilityIcon icon = new ItemAbilityIcon(
                    iconX, iconY,
                    iconSize, iconSize,
                    Cztils.config.getBorderWidth(),
                    ZenithTextures.getItem(ability).orElse(new ItemStack(Items.AIR))
            );
            icon.setBackgroundFill(backgroundFill);
            icon.setBorderColor(borderColor);
            icons.add(icon);
        }
        return icons;
    }

    @Override
    public void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0f);
        this.nametag.render(context);
        this.actives.render(context);
        matrices.pop();
    }
}