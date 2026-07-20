package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.abils.Active;
import com.aquamancer.czlib.api.abils.Actives;
import com.aquamancer.czlib.api.abils.Rarity;
import com.aquamancer.czlib.api.textures.ZenithTextures;
import com.aquamancer.cztils.Cztils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

import java.util.*;
import java.util.stream.Stream;

public class Player extends HudElement {
    private final Nametag nametag;
    private AbilityBar actives, passives;

    private Vector2i activesOffset, passivesOffset;

    public Player(int x, int y, Vector2i activesOffset, Vector2i passivesOffset) {
        super(x, y);
        this.activesOffset = activesOffset;
        this.passivesOffset = passivesOffset;
        this.nametag = new Nametag(0, 0);
    }

    public Player setName(String name) {
        this.nametag.setName(name);
        return this;
    }

    public Player setSpec(String spec) {
        this.nametag.setSpec(spec);
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

    public Player setActives(Set<Active> actives, Set<Active> grayedOut, int iconSize) {
        Map<Active, Boolean> combined = new HashMap<>();
        grayedOut.forEach(a -> combined.put(a, true));
        actives.forEach(a -> combined.put(a, false));  // replaces dupes from grayedOut

        int cap = actives.size() + grayedOut.size();
        List<Map.Entry<Active, Boolean>> sorted = combined.entrySet().stream()
                .sorted(Comparator.comparing((k) -> k.getKey().getAbility())).toList();  // todo actual sorting
        List<AbilityIcon> icons = new ArrayList<>(cap);

        for (int i = 0; i < sorted.size(); i++) {
            Actives active = sorted.get(i).getKey().getAbility();
            Rarity rarity = sorted.get(i).getKey().getRarity();
            Boolean grayed = sorted.get(i).getValue();
            AbilityIcon.Type iconType = Cztils.config.getIconType(active);

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