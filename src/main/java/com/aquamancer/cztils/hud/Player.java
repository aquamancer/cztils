package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.textures.ZenithTextures;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Player extends HudElement {
    private static final int PASSIVE_GAP_PX = 4;

    private final Nametag nametag = new Nametag(0, 0);
    private Spec spec;
    private AbilityBar actives = new AbilityBar(0, 0, List.of());
    private AbilityBar curses = new AbilityBar(0, 0, List.of());
    private AbilityBar passives = new AbilityBar(0, 0, List.of());
    private AbilityBar gifts = new AbilityBar(0, 0, List.of());
    private int curseIconWidth, passiveIconWidth, giftIconWidth;

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

    public Player setActives(List<Active> actives, int iconSize) {
        Map<Active, Boolean> combined = new HashMap<>();

        Set<Actives> alwaysShow = Cztils.config.getAlwaysShow(Actives.class, this.spec);
        for (Actives grayedOut : alwaysShow) {
            Optional<AbilitySpec> grayedOutSpec = AbilitySpec.fromAbilityName(grayedOut.getDisplayName()); // giga jank
            if (grayedOutSpec.isEmpty()) continue;
            combined.put(new Active(grayedOut, grayedOutSpec.get(), null), true);
        }
        actives.forEach(a -> {
            combined.remove(a);  // put() on a dupe only replaces the value
            combined.put(a, false);
        });

        Comparator<Active> sorter = Cztils.config.getActiveSorter(this.spec);
        List<Map.Entry<Active, Boolean>> sorted = (sorter == null) ? List.copyOf(combined.entrySet()) : combined.entrySet().stream().sorted(Map.Entry.comparingByKey(sorter)).toList();

        List<AbilityIcon> icons = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Actives active = sorted.get(i).getKey().getAbility();
            Rarity rarity = sorted.get(i).getKey().getRarity();
            Boolean grayed = sorted.get(i).getValue();
            // relative to ability bar
            int iconX = i*(iconSize);
            int iconY = 0;
            // rarity == null if grayed
            int borderColor = (rarity == null) ? AbilityIcon.DEFAULT_BORDER_COLOR : AbilityIcon.RARITY_COLORS.get(rarity);
            icons.add(createIcon(iconX, iconY, iconSize, active, borderColor, grayed));
        }

        this.actives = new AbilityBar(activesOffset.x, activesOffset.y, icons);
        return this;
    }

    public Player setCurses(Set<Curse> curseSet, int iconSize) {
        this.curseIconWidth = iconSize;

        List<AbilityIcon> icons = new ArrayList<>(curseSet.size());
        int i = 0;
        for (Curse curse : curseSet) {
            icons.add(createIcon(i*iconSize, 0, iconSize, curse, AbilityIcon.CURSE_COLOR, false));
            i++;
        }
        this.curses = new AbilityBar(passivesOffset.x, passivesOffset.y, icons);
        return this;
    }

    public Player setGifts(Set<Gift> curseSet, int iconSize) {
        this.giftIconWidth = iconSize;
        return this;
    }

    public Player setPassives(List<Passive> passives, int iconSize) {
        this.passiveIconWidth = iconSize;

        Map<Passive, Boolean> combined = new HashMap<>();

        Set<Passives> alwaysShow = Cztils.config.getAlwaysShow(Passives.class, this.spec);
        for (Passives grayedOut : alwaysShow) {
            Optional<AbilitySpec> grayedOutSpec = AbilitySpec.fromAbilityName(grayedOut.getDisplayName()); // giga jank
            if (grayedOutSpec.isEmpty()) continue;
            combined.put(new Passive(grayedOut, grayedOutSpec.get(), null), true);
        }
        passives.forEach(a -> {
            combined.remove(a);  // put() on a dupe only replaces the value
            combined.put(a, false);
        });

        Comparator<Passive> sorter = Cztils.config.getPassiveSorter(this.spec);
        List<Map.Entry<Passive, Boolean>> sorted = (sorter == null) ? List.copyOf(combined.entrySet()) : combined.entrySet().stream().sorted(Map.Entry.comparingByKey(sorter)).toList();

        List<AbilityIcon> icons = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Passives passive = sorted.get(i).getKey().getAbility();
            Rarity rarity = sorted.get(i).getKey().getRarity();
            Boolean grayed = sorted.get(i).getValue();

            // relative to ability bar
            int iconX = i*(iconSize);
            int iconY = 0;
            int borderColor = (rarity == null) ? AbilityIcon.DEFAULT_BORDER_COLOR : AbilityIcon.RARITY_COLORS.get(rarity);
            icons.add(createIcon(iconX, iconY, iconSize, passive, borderColor, grayed));
        }

        this.passives = new AbilityBar(passivesOffset.x, passivesOffset.y, icons);
        return this;
    }

    public static AbilityIcon createIcon(int x, int y, int iconSize, Enum<?> ability, int borderColor, boolean grayedOut) {
        AbilityIcon.Type iconType = Cztils.config.textures.getIconType(ability);
        switch (iconType) {
            default:
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
                        x, y,
                        iconSize, iconSize,
                        Cztils.config.getBorderWidth(),
                        ZenithTextures.getItem(ability).orElse(new ItemStack(Items.AIR))
                );
                icon.setBackgroundFill(AbilityIcon.BACKGROUND_FILL);
                if (grayedOut) {
                    icon.setBorderColor(AbilityIcon.DEFAULT_BORDER_COLOR);
                    icon.setGrayedOut(Cztils.config.grayedOut);
                } else {
                    icon.setBorderColor(borderColor);
                }
                return icon;
        }
    }

    @Override
    public void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0f);
        this.nametag.render(context);
        this.actives.render(context);
        this.curses.render(context);
        matrices.translate(this.curses.size()*this.curseIconWidth + PASSIVE_GAP_PX, 0f, 0f);
        this.gifts.render(context);
        matrices.translate(this.gifts.size()*this.giftIconWidth + PASSIVE_GAP_PX, 0f, 0f);
        this.passives.render(context);
        matrices.pop();
    }
}