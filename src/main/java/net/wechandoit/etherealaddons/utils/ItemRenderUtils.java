package net.wechandoit.etherealaddons.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.wechandoit.etherealaddons.QuagsireClient;

public class ItemRenderUtils {

    public static final Identifier RARITY = new Identifier(QuagsireClient.MODID, "gui/aura.png");

    private static void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int color) {
        renderGuiQuadGradient(buffer, x, y, width, height, color, color);
    }

    public static void renderCustomBars(int x, int y, ItemStack itemStack) {
        if (MiscUtils.isWateringCan(itemStack)) {
            int max_amount = MiscUtils.getMaxWateringCanAmount(itemStack);
            if (max_amount > 0) {
                int amount = MiscUtils.getWateringCanAmount(itemStack);
                ItemRenderUtils.renderItemBarOnItemStack(x, y, amount, max_amount, 63, 118, 228);
            }
        } else if (MiscUtils.isGrappleGun(itemStack)) {
            int amount = MiscUtils.getGrappleGunUses(itemStack);
            ItemRenderUtils.renderItemBarOnItemStack(x, y, amount, 100, 0, 255, 0);
        }
    }

    private static void renderGuiQuadGradient(BufferBuilder buffer, int x, int y, int width, int height, int startColor, int endColor) {

        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x, y, 0.0).color(startRed, startGreen, startBlue, startAlpha).next();
        buffer.vertex(x, y + height, 0.0).color(startRed, startGreen, startBlue, startAlpha).next();
        buffer.vertex(x + width, y + height, 0.0).color(endRed, endGreen, endBlue, endAlpha).next();
        buffer.vertex(x + width, y, 0.0).color(endRed, endGreen, endBlue, endAlpha).next();
        buffer.end();
        BufferRenderer.draw(buffer);
    }

    public static void renderItemRarity(MatrixStack matrix, int x, int y, int color) {

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, RARITY);
        RenderSystem.setShaderColor(red, green, blue, alpha);
        DrawableHelper.drawTexture(matrix, x - 1, y - 1, 0, 0, 18, 18, 18, 18);
        RenderSystem.disableBlend();
    }

    public static void renderItemBarOnItemStack(int x, int y, int val, int max, int red, int green, int blue) {
        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        int i = Math.round(13.0F - (float) (max - val) * 13.0F / (float) max);
        renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, RGBA(0, 0, 0, 255));
        renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, RGBA(red, green, blue, 255));
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();
    }

    public static int RGBA(int r, int g, int b, int a) {
        return (a << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
}
