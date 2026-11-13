package com.iafenvoy.iafpatcher.regression;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

/** Opt-in visual fixture, never included in release builds. Uses real S2C delivery. */
@Mod.EventBusSubscriber(modid = "iaf_patcher")
public final class LightningVisualServer {
    private static final List<Cow> MOBS = new ArrayList<>();
    private static int ticks;
    private static boolean joined;

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        if (server.getPlayerList().getPlayers().isEmpty()) {
            if (joined) server.halt(false);
            return;
        }
        ServerPlayer player = server.getPlayerList().getPlayers().get(0);
        ServerLevel level = server.overworld();
        if (!joined) {
            joined = true;
            player.setGameMode(GameType.CREATIVE);
            for (int x = -14; x <= 14; x++) for (int z = -14; z <= 10; z++)
                level.setBlock(new BlockPos(x, 109, z), Blocks.STONE.defaultBlockState(), 2);
            level.setDayTime(18000);
            for (int x : new int[]{0, -5, 5, -3, 3}) {
                Cow mob = EntityType.COW.create(level);
                mob.setPos(x, 110, x == 0 ? 0 : 1);
                mob.setNoAi(true);
                mob.setNoGravity(true);
                mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);
                mob.setHealth(200);
                level.addFreshEntity(mob);
                MOBS.add(mob);
            }
        }
        player.connection.teleport(0, 110, -8, 0, 7);
        if (++ticks % 30 == 0 && ticks > 60 && ticks < 500) {
            for (Cow mob : MOBS) {
                mob.invulnerableTime = 0;
                mob.setHealth(200);
                mob.setDeltaMovement(0, 0, 0);
            }
            ItemStack stack = new ItemStack(IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD.get());
            stack.getItem().hurtEnemy(stack, MOBS.get(0), player);
            System.out.println("IAFP_LIGHTNING_VISUAL server sent weapon chain, tick=" + ticks);
        }
        if (ticks > 900) server.halt(false);
    }
}
