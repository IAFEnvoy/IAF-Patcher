package com.iafenvoy.iafpatcher;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.iafenvoy.iafpatcher.util.RandomHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TitleScreenRenderManager {
    public static final ResourceLocation splash = new ResourceLocation(IceAndFire.MODID, "splashes.txt");
    public static final ResourceLocation[] pageFlipTextures;
    public static final ResourceLocation[] drawingTextures = new ResourceLocation[23];
    private static final ResourceLocation BESTIARY_TEXTURE = new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/bestiary_menu.png");
    private static final ResourceLocation TABLE_TEXTURE = new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/table.png");
    private static final Font textRenderer = Minecraft.getInstance().font;
    private static int layerTick;
    private static List<String> splashText;
    private static boolean isFlippingPage = false;
    private static int pageFlip = 0;
    private static Picture[] drawnPictures;
    private static float globalAlpha = 1F;

    static {
        pageFlipTextures = new ResourceLocation[]{new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_1.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_2.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_3.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_4.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_5.png"),
                new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/page_6.png")};
        for (int i = 0; i < drawingTextures.length; i++)
            drawingTextures[i] = new ResourceLocation(IceAndFire.MODID, "textures/gui/main_menu/drawing_" + i + ".png");
        resetDrawnImages();
    }

    public static String getSplash() {
        if (splashText == null)
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Minecraft.getInstance().getResourceManager().getResource(splash).getInputStream()));
                splashText = bufferedReader.lines().map(String::trim).filter((splashText) -> splashText.hashCode() != 125780783).toList();
                bufferedReader.close();
            } catch (IOException var8) {
                splashText = new ArrayList<>();
            }
        if (splashText.isEmpty()) return null;
        return splashText.get(RandomHelper.nextInt(0, splashText.size() - 1));
    }

    private static void resetDrawnImages() {
        globalAlpha = 0;
        Random random = ThreadLocalRandom.current();
        drawnPictures = new Picture[2];
        boolean left = random.nextBoolean();
        for (int i = 0; i < drawnPictures.length; i++) {
            left = !left;
            int x = left ? -15 - random.nextInt(20) - 128 : 30 + random.nextInt(20);
            int y = random.nextInt(25);
            drawnPictures[i] = new Picture(random.nextInt(drawingTextures.length), x, y, 0.5F, random.nextFloat() * 0.5F + 0.5F);
        }
    }

    public static void tick() {
        float flipTick = layerTick % 40;
        if (globalAlpha < 1 && !isFlippingPage && flipTick < 30)
            globalAlpha += 0.1F;
        if (globalAlpha > 0 && flipTick > 30)
            globalAlpha -= 0.1F;
        if (flipTick == 0 && !isFlippingPage)
            isFlippingPage = true;
        if (isFlippingPage) {
            if (layerTick % 2 == 0)
                pageFlip++;
            if (pageFlip == 6) {
                pageFlip = 0;
                isFlippingPage = false;
                resetDrawnImages();
            }
        }
        layerTick++;
    }

    public static void renderBackground(PoseStack ms, int width, int height) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();

        blit(ms, TABLE_TEXTURE, 0, 0, 0, 0, width, height, width, height);
        blit(ms, BESTIARY_TEXTURE, 50, 0, 0, 0, width - 100, height, width - 100, height);
        if (isFlippingPage)
            blit(ms, pageFlipTextures[Math.min(5, pageFlip)], 50, 0, 0, 0, width - 100, height, width - 100, height);
        else {
            int middleX = width / 2;
            int middleY = height / 5;
            float widthScale = width / 427F;
            float heightScale = height / 427F;
            float imageScale = Math.min(widthScale, heightScale) * 192;
            RenderSystem.enableBlend();
            for (Picture picture : drawnPictures) {
                RenderSystem.setShaderColor(1, 1, 1, globalAlpha);
                int x = (int) (picture.x * widthScale) + middleX;
                int y = (int) ((picture.y * heightScale) + middleY);
                blit(ms, drawingTextures[picture.image], x, y, 0, 0, (int) imageScale, (int) imageScale, (int) imageScale, (int) imageScale);
            }
            RenderSystem.disableBlend();
        }
    }

    public static void drawModName(PoseStack ms, int height, int alphaFormatted) {
        int textColor = 0x00FFFFFF | alphaFormatted;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        textRenderer.draw(ms, "Ice and Fire " + ChatFormatting.GOLD + IceAndFire.VERSION, 2, height - 60, textColor);
        textRenderer.draw(ms, "IAF Patcher " + ChatFormatting.GOLD + IceAndFirePatcher.VERSION, 2, height - 50, textColor);
    }

    private static class Picture {
        final int image;
        final int x;
        final int y;
        final float alpha;

        public Picture(int image, int x, int y, float alpha, float scale) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.alpha = alpha;
        }
    }

    public static void blit(PoseStack ms, ResourceLocation location, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        innerBlit(ms, location, x, x + width, y, y + height, u / textureWidth, (u + width) / textureWidth, v / textureHeight, (v + height) / textureHeight);
    }

    private static void innerBlit(PoseStack stack, ResourceLocation location, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2) {
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f var11 = stack.last().pose();
        BufferBuilder var12 = Tesselator.getInstance().getBuilder();
        var12.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        var12.vertex(var11, x1, y1, 0).uv(u1, v1).endVertex();
        var12.vertex(var11, x1, y2, 0).uv(u1, v2).endVertex();
        var12.vertex(var11, x2, y2, 0).uv(u2, v2).endVertex();
        var12.vertex(var11, x2, y1, 0).uv(u2, v1).endVertex();
        var12.end();
        BufferUploader.end(var12);
    }
}

