package com.aquamancer.cztils.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class ItemAbilityIcon extends AbilityIcon {
    private static final float DEFAULT_SIZE = 16f;

    private ItemStack item;
    private float scaleX;
    private float scaleY;

    public ItemAbilityIcon(int x, int y, int w, int h, int borderWidth, ItemStack item) {
        super(x, y, w, h, borderWidth);
        this.item = item;
        this.scaleX = this.texture.w / DEFAULT_SIZE;
        this.scaleY = this.texture.h / DEFAULT_SIZE;
    }

    @Override
    public void render(DrawContext context) {
        context.fill(RenderLayer.getGuiOverlay(), border.x, border.y, border.x2, border.y2, borderColor);
        if (backgroundFill != 0) {
            context.fill(RenderLayer.getGuiOverlay(), texture.x, texture.y, texture.x2, texture.y2, backgroundFill);
        }
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(texture.x, texture.y, 0);
        matrices.scale(this.scaleX, this.scaleY, 1.0f);
        context.drawItem(this.item, 0, 0);
        if (this.subscript != null) {
            context.drawItemInSlot(
                    MinecraftClient.getInstance().textRenderer,
                    this.item,
                    0, 0,
                    this.subscript
            );
        }
        matrices.pop();
        if (grayedOut != 0) {
            context.fill(RenderLayer.getGuiOverlay(), texture.x, texture.y, texture.x2, texture.y2, grayedOut);
        }
    }

}
