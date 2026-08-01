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
        this.scaleX = (w - borderWidth*2) / DEFAULT_SIZE;
        this.scaleY = (h - borderWidth*2) / DEFAULT_SIZE;
    }

    @Override
    public void render(DrawContext context) {
        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(this.x, this.y, 0f);
        context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.w, this.h, borderColor);
        matrices.translate(borderWidth, borderWidth, 0f);
        if (backgroundFill != 0) {
            context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.w - borderWidth*2, this.h - borderWidth*2, backgroundFill);
        }

        matrices.push();
        matrices.scale(this.scaleX, this.scaleY, 1.0f);
        context.drawItem(this.item, 0, 0);
        matrices.pop();

        if (grayedOut != 0) {
            context.fill(RenderLayer.getGuiOverlay(), 0, 0, this.w - borderWidth*2, this.h - borderWidth*2, grayedOut);
        }
        // draw subscript over gray-out so you can actually see it
        if (this.subscript != null) {
            matrices.push();
            matrices.scale(this.scaleX, this.scaleY, 1.0f);
            context.drawItemInSlot(
                    MinecraftClient.getInstance().textRenderer,
                    this.item,
                    0, 0,
                    this.subscript
            );
            matrices.pop();
        }
        matrices.pop();
    }
}
