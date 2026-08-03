package com.aquamancer.cztils.hud;

import com.aquamancer.czlib.api.PartyMember;
import com.aquamancer.czlib.api.abils.*;
import com.aquamancer.czlib.api.textures.ZenithTextures;
import com.aquamancer.cztils.Cztils;
import com.aquamancer.cztils.config.custom.SpecConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Player extends HudElement {
    private static final String ANCHOR = new String(Character.toChars(0x2693));
    private static final int PASSIVE_GAP_PX = 4;

    public enum RenderMode { ALL, NAMETAG, OFF }

    private Spec spec = null;
    private SpecConfig config = Cztils.config.specConfigs.get(null);

    private final Nametag nametag = new Nametag(0, 0, this.config);
    private AbilityBar actives = new AbilityBar(List.of());
    private AbilityBar curses = new AbilityBar(List.of());
    private AbilityBar passives = new AbilityBar(List.of());
    private AbilityBar gifts = new AbilityBar(List.of());

    private int iconSize;

    // only for rebuild()
    private PartyMember cachedPlayer;

    public Player() {
        super(0, 0);
        this.iconSize = Cztils.config.iconSize;
    }
    public Player(int x, int y) {
        super(x, y);
        this.iconSize = Cztils.config.iconSize;
    }

    public Player setName(String name) {
        this.nametag.setName(name);
        return this;
    }

    public Player setSpec(@Nullable Spec spec) {
        this.spec = spec;
        this.config = Cztils.config.specConfigs.get(spec);
        this.nametag.setConfig(Cztils.config.specConfigs.get(spec));
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

    public Player setActives(PartyMember player) {
        this.cachedPlayer = player;

        Map<Active, Boolean> combined = new HashMap<>();
        for (Actives grayedOut : this.config.getAlwaysShow(Actives.class)) {
            combined.put(new Active(grayedOut, null), true);
        }
        for (Actives grayedOut : this.config.getShowIfHasSpec(Actives.class)) {
            Optional<Spec> spec = grayedOut.getSpec().toSpec();
            if (spec.isEmpty()) continue;
            if (!player.getSpecs().contains(spec.get())) continue;
            combined.put(new Active(grayedOut, null), true);
        }
        player.getActives().forEach((k, v) -> {
            if (combined.remove(v) != null || this.config.activeSet.contains(v.getAbility())) {
                combined.put(v, false);
            }
        });

        Comparator<Active> sorter = this.config.getActiveSorter();
        List<Map.Entry<Active, Boolean>> sorted = (sorter == null) ? new ArrayList<>(combined.entrySet()) : combined.entrySet().stream().sorted(Map.Entry.comparingByKey(sorter)).toList();
        Map<AbilitySpec, Integer> counts = sorted.stream().collect(
                () -> new EnumMap<>(AbilitySpec.class),
                (map, entry) -> map.compute(entry.getKey().getSpec(), (k, v) -> (v == null) ? 1 : v + 1),
                Map::putAll
        );

        List<AbilityIcon> icons = new ArrayList<>();
        int iconX = 0;
        for (Map.Entry<Active, Boolean> entry : sorted) {
            Active active = entry.getKey();
            Boolean isGrayedOut = entry.getValue();

            int iconY = 0;
            AbilityIcon icon = createIcon(iconX, 0, this.iconSize, active, isGrayedOut);

            if (isGrayedOut) {
                PartyMember.BlockReason isBlocked = player.isBlocked(active, true);
                if (isBlocked == PartyMember.BlockReason.SLOT_TAKEN) {
                    icon.setSubscript(ANCHOR);
                } else if (isBlocked == PartyMember.BlockReason.MORE_THAN_4 || counts.get(active.getSpec()) > 4) {
                    icon.setSubscript("4+");
                }
            }
            icons.add(icon);
            iconX += this.iconSize;
        }

        if (Cztils.config.showMissingLifelines && sorted.stream().noneMatch(entry -> entry.getKey().getSlot() == ActiveSlot.LIFELINE)) {
            icons.add(new ItemAbilityIcon(iconX, 0, this.iconSize, this.iconSize, Cztils.config.borderWidth, new ItemStack(Items.TOTEM_OF_UNDYING))
                    .setGrayedOut(Cztils.config.grayedOut)
            );
        }

        this.actives = new AbilityBar(icons);
        return this;
    }

    public Player setPassives(PartyMember player) {
        this.cachedPlayer = player;

        Map<Passive, Boolean> combined = new HashMap<>();
        for (Passives grayedOut : this.config.getAlwaysShow(Passives.class)) {
            combined.put(new Passive(grayedOut, null), true);
        }
        for (Passives grayedOut : this.config.getShowIfHasSpec(Passives.class)) {
            Optional<Spec> spec = grayedOut.getSpec().toSpec();
            if (spec.isEmpty()) continue;
            if (!player.getSpecs().contains(spec.get())) continue;
            combined.put(new Passive(grayedOut, null), true);
        }
        player.getPassives().forEach((k, v) -> {
            if (combined.remove(v) != null || this.config.passiveSet.contains(v.getAbility())) {
                combined.put(v, false);
            }
        });

        Comparator<Passive> sorter = this.config.getPassiveSorter();
        List<Map.Entry<Passive, Boolean>> sorted = (sorter == null) ? new ArrayList<>(combined.entrySet()) : combined.entrySet().stream().sorted(Map.Entry.comparingByKey(sorter)).toList();

        List<AbilityIcon> icons = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            int iconX = i*this.iconSize;
            int iconY = 0;
            icons.add(createIcon(iconX, iconY, this.iconSize, sorted.get(i).getKey(), sorted.get(i).getValue()));
        }

        this.passives = new AbilityBar(icons);
        return this;
    }

    public Player setCurses(PartyMember player) {
        this.cachedPlayer = player;

        List<Curse> curses = player.getCurses().stream().filter(c -> this.config.curseSet.contains(c)).toList();

        List<AbilityIcon> icons = new ArrayList<>(curses.size());
        int i = 0;
        for (Curse curse : curses) {
            AbilityIcon icon = createIcon(
                    i*this.iconSize, 0,
                    this.iconSize,
                    curse,
                    false
            );
            if (curse == Curse.GREED) {
                icon.setSubscript(String.valueOf(player.getGreedAmount()*5) + '%');
            } else if (curse == Curse.PRIDE) {
                icon.setSubscript(String.valueOf(player.getPrideAmount()*10) + '%');
            }
            icons.add(icon);
            i++;
        }
        this.curses = new AbilityBar(icons);
        return this;
    }

    public Player setGifts(PartyMember player) {
        this.cachedPlayer = player;

        List<Gift> gifts = player.getGifts().values().stream().filter(g -> this.config.giftSet.contains(g.getAbility())).toList();

        List<AbilityIcon> icons = new ArrayList<>(gifts.size());
        int i = 0;
        for (Gift gift : gifts) {
            AbilityIcon icon = createIcon(i*this.iconSize, 0, this.iconSize, gift, false);
            String counter = (gift.getCounter() == 0) ? null : String.valueOf(gift.getCounter());
            icon.setSubscript(counter);
            icons.add(icon);
            i++;
        }

        this.gifts = new AbilityBar(icons);
        return this;
    }

    private static <T extends Ability> AbilityIcon createIcon(int x, int y, int iconSize, T ability, boolean grayedOut) {
        AbilityIcon icon = new ItemAbilityIcon(
                x, y,
                iconSize, iconSize,
                Cztils.config.borderWidth,
                ZenithTextures.getItem(ability.getAbility()).orElse(new ItemStack(Items.BARRIER))
        );
        int borderColor = Cztils.config.grayedOut;
        if (ability instanceof Gift) {
            borderColor = AbilityIcon.GIFT_COLOR;
        } else if (ability instanceof HasRarity a && a.getRarity() != null) {
            borderColor = AbilityIcon.RARITY_COLORS.get(a.getRarity());
        } else if (ability instanceof Curse) {
            borderColor = AbilityIcon.CURSE_COLOR;
        }
        icon.setBorderColor(borderColor);
        icon.setBackgroundFill(Cztils.config.backgroundFill);
        if (grayedOut) {
            icon.setGrayedOut(Cztils.config.grayedOut);
        }
        return icon;
    }

    public Spec getSpec() {
        return this.spec;
    }

    public void render(DrawContext context, RenderMode mode) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0f);

        matrices.push();
        matrices.scale(Cztils.config.nametag.textScale, Cztils.config.nametag.textScale, 0f);
        this.nametag.render(context);
        matrices.pop();

        if (mode == RenderMode.ALL) {
            float textHeight = 10 * Cztils.config.nametag.textScale;
            matrices.translate(0f, textHeight, 0f);
            matrices.translate(Cztils.config.activesOffsetX, Cztils.config.activesOffsetY, 0f);
            this.actives.render(context);

            matrices.translate(0f, Cztils.config.iconSize, 0f);
            matrices.translate(Cztils.config.passivesOffsetX, Cztils.config.passivesOffsetY, 0f);
            this.curses.render(context);
            if (this.curses.size() > 0) {
                matrices.translate(this.curses.size() * this.iconSize + PASSIVE_GAP_PX, 0f, 0f);
            }
            this.gifts.render(context);
            if (this.gifts.size() > 0) {
                matrices.translate(this.gifts.size() * this.iconSize + PASSIVE_GAP_PX, 0f, 0f);
            }
            this.passives.render(context);
        }
        matrices.pop();
    }

    public void rebuild() {
        this.iconSize = Cztils.config.iconSize;
        this.nametag.setFormat(Cztils.config.nametag.nametagFormat);
        this.nametag.rebuild();
        if (cachedPlayer != null) {
            this.setActives(cachedPlayer);
            this.setCurses(cachedPlayer);
            this.setGifts(cachedPlayer);
            this.setPassives(cachedPlayer);
        }
    }
}