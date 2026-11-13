package com.iafenvoy.iafpatcher.regression;

import com.iafenvoy.iafpatcher.client.WeaponLightningEffects;
import com.iafenvoy.iafpatcher.config.IAFPClientConfig;
import com.iafenvoy.iafpatcher.network.ChainLightningPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Collections;

/** Automated real-client render/packet fixture. Screenshots are saved for visual review. */
@Mod.EventBusSubscriber(modid = "iaf_patcher", value = Dist.CLIENT)
public final class LightningVisualClient {
    private static boolean connecting;
    private static int frames, captures, joinedTicks;
    private static int lastCaptureFrame;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) throws Exception {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (!connecting && minecraft.screen != null) {
            connecting = true;
            ServerData data = new ServerData("Lightning QA", "127.0.0.1:25586", false);
            for (Method method : ConnectScreen.class.getDeclaredMethods()) {
                if (!method.getName().equals("startConnecting")) continue;
                if (method.getParameterCount() == 5) method.invoke(null, minecraft.screen, minecraft, ServerAddress.parseString(data.ip), data, false);
                else if (method.getParameterCount() == 4) method.invoke(null, minecraft.screen, minecraft, ServerAddress.parseString(data.ip), data);
            }
            minecraft.options.hideGui = true;
        }
        if (minecraft.level == null || minecraft.player == null) return;
        if (++joinedTicks == 550) {
            require(captures >= 3, "S2C weapon arcs were not captured");
            require(renderer() == null, "expired arcs retained renderer");
            ChainLightningPacket wrongDimension = new ChainLightningPacket("minecraft:the_nether", Collections.singletonList(new ChainLightningPacket.Link(0, 110, 0, 2, 110, 0)));
            WeaponLightningEffects.receive(wrongDimension);
            require(renderer() == null, "cross-dimension packet rendered");
            IAFPClientConfig.CHAIN_LIGHTNING_EFFECTS.set(false);
            WeaponLightningEffects.receive(new ChainLightningPacket(minecraft.level.dimension().location().toString(), wrongDimension.links));
            require(renderer() == null, "disabled visual config ignored");
            IAFPClientConfig.CHAIN_LIGHTNING_EFFECTS.set(true);
            String result = "PASS: actual weapon S2C reached client renderer; " + captures + " screenshots; expiry, dimension filtering and client toggle passed.\n";
            Files.write(Paths.get("lightning-visual-result.txt"), result.getBytes(StandardCharsets.UTF_8));
            System.out.println("IAFP_LIGHTNING_VISUAL " + result);
            minecraft.stop();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void render(RenderLevelStageEvent event) throws Exception {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || renderer() == null || captures >= 4) return;
        Field field = WeaponLightningEffects.class.getDeclaredField("EXPIRATIONS");
        field.setAccessible(true);
        ArrayDeque<?> expirations = (ArrayDeque<?>) field.get(null);
        long remaining = expirations.isEmpty() ? 0 : (Long) expirations.peekFirst() - minecraft.level.getGameTime();
        if (++frames - lastCaptureFrame >= 30 && remaining >= 4 && remaining <= 8) {
            lastCaptureFrame = frames;
            Screenshot.grab(minecraft.gameDirectory, "weapon-chain-" + (++captures) + ".png", minecraft.getMainRenderTarget(), ignored -> { });
        }
    }

    private static Object renderer() throws Exception {
        Field field = WeaponLightningEffects.class.getDeclaredField("renderer");
        field.setAccessible(true);
        return field.get(null);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
