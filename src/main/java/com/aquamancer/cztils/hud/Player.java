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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Player extends HudElement {
    private static final int PASSIVE_GAP_PX = 4;

    private Spec spec = null;
    private SpecConfig config = Cztils.config.specConfigs.get(null);

    private final Nametag nametag = new Nametag(0, 0);
    private AbilityBar actives = new AbilityBar(List.of());
    private AbilityBar curses = new AbilityBar(List.of());
    private AbilityBar passives = new AbilityBar(List.of());
    private AbilityBar gifts = new AbilityBar(List.of());

    private int iconSize;

    // for rebuild()
    private Collection<Active> lastActives = List.of();
    private Collection<Passive> lastPassives = List.of();
    private Collection<Gift> lastGifts = List.of();
    private Collection<Curse> lastCurses = List.of();
    private Set<Spec> lastSpecs = Set.of();

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
        this.nametag.setSpec(config.name);
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

    public Player setActives(Collection<Active> actives, Set<Spec> playerSpecs) {
        this.lastActives = actives;
        this.lastSpecs = playerSpecs;

        Map<Active, Boolean> combined = new HashMap<>();
        for (Actives grayedOut : this.config.getAlwaysShow(Actives.class)) {
            Optional<AbilitySpec> grayedOutSpec = AbilitySpec.fromAbilityName(grayedOut.getDisplayName());
            if (grayedOutSpec.isEmpty()) continue;
            combined.put(new Active(grayedOut, grayedOutSpec.get(), null), true);
        }
        for (Actives grayedOut : this.config.getShowIfHasSpec(Actives.class)) {
            Optional<AbilitySpec> grayedOutSpec = AbilitySpec.fromAbilityName(grayedOut.getDisplayName());
            if (grayedOutSpec.isEmpty()) continue;
            if (grayedOutSpec.get().toSpec().isEmpty()) continue;
            if (!playerSpecs.contains(grayedOutSpec.get().toSpec().get())) continue;
            combined.put(new Active(grayedOut, grayedOutSpec.get(), null), true);
        }
        actives.forEach(a -> {
            if (combined.remove(a) != null || this.config.activeSet.contains(a.getAbility())) {
                combined.put(a, false);
            }
        });

        Comparator<Active> sorter = this.config.getActiveSorter();
        List<Map.Entry<Active, Boolean>> sorted = (sorter == null) ? new ArrayList<>(combined.entrySet()) : combined.entrySet().stream().sorted(Map.Entry.comparingByKey(sorter)).toList();

        List<AbilityIcon> icons = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Actives active = sorted.get(i).getKey().getAbility();
            Rarity rarity = sorted.get(i).getKey().getRarity();
            Boolean grayed = sorted.get(i).getValue();

            int iconX = i*(this.iconSize);
            int iconY = 0;
            // rarity == null if grayed
            int borderColor = (rarity == null) ? AbilityIcon.DEFAULT_BORDER_COLOR : AbilityIcon.RARITY_COLORS.get(rarity);
            icons.add(createIcon(iconX, iconY, this.iconSize, active, borderColor, grayed));
        }

        this.actives = new AbilityBar(icons);
        return this;
    }

    public Player setPassives(Collection<Passive> passives, Set<Spec> playerSpecs) {
        this.lastPassives = passives;
        this.lastSpecs = playerSpecs;

        Map<Passive, Boolean> combined = new HashMap<>();
        for (Passives grayedOut : this.config.getAlwaysShow(Passives.class)) {
            Optional<AbilitySpec> grayedOutSpec = AbilitySpec.fromAbilityName(grayedOut.getDisplayName());
            if (grayedOutSpec.isEmpty()) continue;
            combined.put(new Passive(grayedOut, grayedOutSpec.get(), null), true);
        }
        for (Passives grayedOut : this.config.getShowIfHasSpec(Passives.class)) {
            Optional<AbilitySpec> grayedOutSpec = AbilitySpec.fromAbilityName(grayedOut.getDisplayName());
            if (grayedOutSpec.isEmpty()) continue;
            if (grayedOutSpec.get().toSpec().isEmpty()) continue;
            if (!playerSpecs.contains(grayedOutSpec.get().toSpec().get())) continue;
            combined.put(new Passive(grayedOut, grayedOutSpec.get(), null), true);
        }
        passives.forEach(a -> {
            if (combined.remove(a) != null || this.config.passiveSet.contains(a.getAbility())) {
                combined.put(a, false);
            }
        });

        Comparator<Passive> sorter = this.config.getPassiveSorter();
        List<Map.Entry<Passive, Boolean>> sorted = (sorter == null) ? new ArrayList<>(combined.entrySet()) : combined.entrySet().stream().sorted(Map.Entry.comparingByKey(sorter)).toList();

        List<AbilityIcon> icons = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Passives passive = sorted.get(i).getKey().getAbility();
            Rarity rarity = sorted.get(i).getKey().getRarity();
            Boolean grayed = sorted.get(i).getValue();

            // relative to ability bar
            int iconX = i*(this.iconSize);
            int iconY = 0;
            int borderColor = (rarity == null) ? AbilityIcon.DEFAULT_BORDER_COLOR : AbilityIcon.RARITY_COLORS.get(rarity);
            icons.add(createIcon(iconX, iconY, this.iconSize, passive, borderColor, grayed));
        }

        this.passives = new AbilityBar(icons);
        return this;
    }

    public Player setCurses(Collection<Curse> curses) {
        this.lastCurses = curses;

        curses = curses.stream().filter(c -> this.config.curseSet.contains(c)).toList();

        List<AbilityIcon> icons = new ArrayList<>(curses.size());
        int i = 0;
        for (Curse curse : curses) {
            icons.add(createIcon(i*this.iconSize, 0, this.iconSize, curse, AbilityIcon.CURSE_COLOR, false));
            i++;
        }
        this.curses = new AbilityBar(icons);
        return this;
    }

    public Player setGifts(Collection<Gift> gifts) {
        this.lastGifts = gifts;

        gifts = gifts.stream().filter(g -> this.config.giftSet.contains(g.getAbility())).toList();

        List<AbilityIcon> icons = new ArrayList<>(gifts.size());
        int i = 0;
        for (Gift gift : gifts) {
            AbilityIcon icon = createIcon(i*this.iconSize, 0, this.iconSize, gift.getAbility(), AbilityIcon.PRISMATIC_COLOR, false);
            String counter = (gift.getCounter() == 0) ? null : String.valueOf(gift.getCounter());
            icon.setSubscript(counter);
            icons.add(icon);
            i++;
        }

        this.gifts = new AbilityBar(icons);
        return this;
    }

    private static AbilityIcon createIcon(int x, int y, int iconSize, Enum<?> ability, int borderColor, boolean grayedOut) {
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
                        Cztils.config.borderWidth,
                        ZenithTextures.getItem(ability).orElse(new ItemStack(Items.AIR))
                );
                if (grayedOut) {
                    icon.setBorderColor(Cztils.config.grayedOut);
                    icon.setGrayedOut(Cztils.config.grayedOut);
                } else {
                    icon.setBackgroundFill(AbilityIcon.BACKGROUND_FILL);
                    icon.setBorderColor(borderColor);
                }
                return icon;
        }
    }

    public Spec getSpec() {
        return this.spec;
    }

    @Override
    public void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.x, this.y, 0f);

        matrices.push();
        matrices.scale(Cztils.config.textScale, Cztils.config.textScale, 0f);
        this.nametag.render(context);
        matrices.pop();

        float textHeight = 10*Cztils.config.textScale;
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
        matrices.pop();
    }

    public void rebuild() {
        this.iconSize = Cztils.config.iconSize;
        this.nametag.rebuild();
        this.setActives(this.lastActives, this.lastSpecs);
        this.setCurses(this.lastCurses);
        this.setGifts(this.lastGifts);
        this.setPassives(this.lastPassives, this.lastSpecs);
    }
}