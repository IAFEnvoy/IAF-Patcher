package com.iafenvoy.iafpatcher.client;

import com.github.alexthe666.iceandfire.client.particle.LightningBoltData;
import com.github.alexthe666.iceandfire.client.particle.LightningRender;
import com.iafenvoy.iafpatcher.IceAndFirePatcher;
import com.iafenvoy.iafpatcher.config.IAFPClientConfig;
import com.iafenvoy.iafpatcher.network.ChainLightningPacket;
import net.minecraft.client.Minecraft;
//? >=1.17 {
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
//?} else {
/*import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
*///?}
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;

@Mod.EventBusSubscriber(modid = IceAndFirePatcher.MOD_ID, value = Dist.CLIENT)
public final class WeaponLightningEffects {
    private static final int MAX_VISIBLE_LINKS = 1024;
    private static final ArrayDeque<Long> EXPIRATIONS = new ArrayDeque<>();
    private static Object world;
    private static LightningRender renderer;
    private static /*? >=1.17 {*/MultiBufferSource.BufferSource/*?} else {*//*IRenderTypeBuffer.Impl*//*?}*/ buffers;
    private static long lastTime;

    private WeaponLightningEffects() {
    }

    private static void clear() {
        renderer = null;
        EXPIRATIONS.clear();
    }

    private static void syncWorld() {
        Minecraft minecraft = Minecraft.getInstance();
        Object current = minecraft.level;
        if (current != world || !IAFPClientConfig.CHAIN_LIGHTNING_EFFECTS.get()
                || (minecraft.level != null && minecraft.level.getGameTime() < lastTime)) {
            clear();
            world = current;
        }
        if (minecraft.level != null) lastTime = minecraft.level.getGameTime();
    }

    /** Called only by the S2C handler on the client thread. */
    public static void receive(ChainLightningPacket packet) {
        syncWorld();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !IAFPClientConfig.CHAIN_LIGHTNING_EFFECTS.get()
                || !minecraft.level.dimension().location().toString().equals(packet.dimension)) return;
        while (!EXPIRATIONS.isEmpty() && EXPIRATIONS.peekFirst() <= lastTime) EXPIRATIONS.removeFirst();
        for (ChainLightningPacket.Link link : packet.links) {
            if (EXPIRATIONS.size() >= MAX_VISIBLE_LINKS) break;
            if (renderer == null) renderer = new LightningRender();
            LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY,
                    new /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/(link.x1, link.y1, link.z1),
                    new /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/(link.x2, link.y2, link.z2), 4)
                    .size(0.05F).lifespan(10).fade(LightningBoltData.FadeFunction.fade(0.1F))
                    .spawn(LightningBoltData.SpawnFunction.NO_DELAY);
            renderer.update(null, bolt, 0);
            // Keep a conservative extra tick for the renderer's fractional refresh interval.
            EXPIRATIONS.addLast(lastTime + 11);
        }
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        syncWorld();
        while (!EXPIRATIONS.isEmpty() && EXPIRATIONS.peekFirst() <= lastTime) EXPIRATIONS.removeFirst();
        if (EXPIRATIONS.isEmpty()) clear();
    }

    @SubscribeEvent
    public static void render(/*? >=1.17 {*/RenderLevelStageEvent/*?} else {*//*RenderWorldLastEvent*//*?}*/ event) {
        //? >=1.17 {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        //?}
        syncWorld();
        Minecraft minecraft = Minecraft.getInstance();
        if (renderer == null || minecraft.level == null) return;
        if (buffers == null) buffers = /*? >=1.17 {*/MultiBufferSource/*?} else {*//*IRenderTypeBuffer*//*?}*/.immediate(new BufferBuilder(256));
        /*? >=1.17 {*/PoseStack/*?} else {*//*MatrixStack*//*?}*/ matrices = event./*? >=1.17 {*/getPoseStack()/*?} else {*//*getMatrixStack()*//*?}*/;
        /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/ camera = minecraft.gameRenderer.getMainCamera().getPosition();
        matrices.pushPose();
        try {
            matrices.translate(-camera.x, -camera.y, -camera.z);
            renderer.render(event./*? >=1.17 {*/getPartialTick()/*?} else {*//*getPartialTicks()*//*?}*/, matrices, buffers);
            // This event can run after vanilla's buffer flush. Own and flush only our arc buffers.
            buffers.endBatch();
        } finally {
            matrices.popPose();
        }
    }
}
