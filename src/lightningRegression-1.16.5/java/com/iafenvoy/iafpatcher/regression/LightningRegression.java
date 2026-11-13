package com.iafenvoy.iafpatcher.regression;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.iafenvoy.iafpatcher.config.IAFPConfig;
import com.iafenvoy.iafpatcher.network.ChainLightningPacket;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/** Runs the real transformed weapon classes, with isolated, deterministic targets. */
public final class LightningRegression {
    private static ServerWorld level;
    private static ServerPlayerEntity attacker;
    private static final List<CowEntity> MOBS = new ArrayList<>();

    public static String run(Object server) throws Exception {
        level = ((MinecraftServer) server).overworld();
        attacker = FakePlayerFactory.get(level, new GameProfile(UUID.fromString("d2c1e172-1123-4d99-922c-b0738d4e118a"), "IAFPLightning"));
        attacker.setPos(0, 110, 0);
        level.getChunk(0, 0);
        StringBuilder result = new StringBuilder();
        boolean originalAbility = IafConfig.dragonWeaponLightningAbility;
        try {
            for (Item item : new Item[]{IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD, IafItemRegistry.DRAGONSTEEL_LIGHTNING_AXE,
                    IafItemRegistry.DRAGONSTEEL_LIGHTNING_PICKAXE, IafItemRegistry.DRAGONSTEEL_LIGHTNING_SHOVEL, IafItemRegistry.DRAGONSTEEL_LIGHTNING_HOE}) {
                check("weapon " + item, result, () -> weapon(item));
            }
            check("target budget and initial target exclusion", result, LightningRegression::budget);
            check("range and occlusion exclusions", result, LightningRegression::range);
            check("config disabled restores vanilla lightning", result, LightningRegression::disabled);
            check("upstream ability disabled", result, LightningRegression::abilityDisabled);
            check("non-mob primary target", result, LightningRegression::playerTarget);
            check("dragonbone sword retains vanilla lightning", result, LightningRegression::dragonbone);
            check("packet codec and malformed input", result, LightningRegression::packets);
        } finally {
            cleanup();
            defaults();
            IafConfig.dragonWeaponLightningAbility = originalAbility;
        }
        if (result.indexOf("FAIL:") >= 0) throw new AssertionError("\n" + result);
        return result.toString();
    }

    private static void defaults() {
        IAFPConfig.DRAGONSTEEL_CHAIN_LIGHTNING.set(true);
        IAFPConfig.CHAIN_LIGHTNING_RANGE.set(10.0);
        IAFPConfig.CHAIN_LIGHTNING_TARGETS.set(10);
        IAFPConfig.CHAIN_LIGHTNING_DAMAGE.set(0.5);
        IafConfig.dragonWeaponLightningAbility = true;
        attacker.attackAnim = 0;
    }

    private static void check(String name, StringBuilder result, Runnable action) {
        defaults();
        try {
            action.run();
            result.append("PASS: lightning: ").append(name).append('\n');
        } catch (Throwable failure) {
            failure.printStackTrace();
            result.append("FAIL: lightning: ").append(name).append(": ").append(failure).append('\n');
        } finally {
            cleanup();
        }
    }

    private static CowEntity cow(double x, double z) {
        CowEntity mob = EntityType.COW.create(level);
        mob.setPos(x, 110, z);
        mob.setNoAi(true);
        mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200);
        mob.setHealth(200);
        level.addFreshEntity(mob);
        MOBS.add(mob);
        return mob;
    }

    private static void cleanup() {
        for (CowEntity mob : MOBS) mob.remove();
        MOBS.clear();
        for (LightningBoltEntity bolt : bolts()) bolt.remove();
    }

    private static List<LightningBoltEntity> bolts() {
        return level.getEntitiesOfClass(LightningBoltEntity.class, new AxisAlignedBB(-20, 100, -20, 20, 130, 20)).stream().filter(Entity::isAlive).collect(java.util.stream.Collectors.toList());
    }

    private static ItemStack hit(Item item, net.minecraft.entity.LivingEntity target) {
        ItemStack stack = new ItemStack(item);
        require(item.hurtEnemy(stack, target, attacker), "weapon hit returned false");
        return stack;
    }

    private static void weapon(Item item) {
        CowEntity primary = cow(0, 2), secondary = cow(2, 2);
        ItemStack stack = hit(item, primary);
        double base = 0;
        for (AttributeModifier modifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE)) base += modifier.getAmount();
        double expected = (base > 0 ? base : 1) * 0.5;
        require(Math.abs((200 - secondary.getHealth()) - expected) < 0.001, "wrong chain damage: " + secondary.getHealth() + ", expected " + expected);
        require(primary.getHealth() == 200, "primary hit twice by effect helper");
        require(stack.getDamageValue() > 0, "durability handling was skipped");
        require(bolts().isEmpty(), "old vanilla lightning still spawned");
        require(secondary.getLastHurtByMob() == attacker, "attacker attribution lost");
    }

    private static void budget() {
        CowEntity primary = cow(0, 2);
        cow(2, 2); cow(3, 2); cow(4, 2);
        IAFPConfig.CHAIN_LIGHTNING_TARGETS.set(2);
        hit(IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD, primary);
        require(MOBS.stream().filter(mob -> mob.getHealth() < 200).count() == 1, "budget did not count initial target");
        require(primary.getHealth() == 200, "source was selected again");
        cleanup();
        for (int cap : new int[]{0, 1}) {
            primary = cow(0, 2);
            CowEntity secondary = cow(2, 2);
            IAFPConfig.CHAIN_LIGHTNING_TARGETS.set(cap);
            hit(IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD, primary);
            require(secondary.getHealth() == 200 && bolts().isEmpty(), "zero secondary budget still attacked");
            cleanup();
        }
    }

    private static void range() {
        CowEntity primary = cow(0, 2), visible = cow(2, 2), far = cow(9, 0), hidden = cow(-4, 0);
        IAFPConfig.CHAIN_LIGHTNING_RANGE.set(5.0);
        for (int y = 109; y <= 114; y++) for (int z = -2; z <= 2; z++) level.setBlock(new BlockPos(-2, y, z), Blocks.STONE.defaultBlockState(), 2);
        try {
            hit(IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD, primary);
            require(visible.getHealth() < 200, "visible target not hit");
            require(far.getHealth() == 200, "out-of-range target hit");
            require(hidden.getHealth() == 200, "occluded target hit");
        } finally {
            for (int y = 109; y <= 114; y++) for (int z = -2; z <= 2; z++) level.setBlock(new BlockPos(-2, y, z), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private static void disabled() {
        CowEntity primary = cow(0, 2), secondary = cow(2, 2);
        IAFPConfig.DRAGONSTEEL_CHAIN_LIGHTNING.set(false);
        hit(IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD, primary);
        require(!bolts().isEmpty() && secondary.getHealth() == 200, "original strike was not restored");
    }

    private static void abilityDisabled() {
        CowEntity primary = cow(0, 2), secondary = cow(2, 2);
        IafConfig.dragonWeaponLightningAbility = false;
        hit(IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD, primary);
        require(bolts().isEmpty() && secondary.getHealth() == 200, "upstream config ignored");
    }

    private static void playerTarget() {
        CowEntity secondary = cow(2, 2);
        hit(IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD, attacker);
        require(secondary.getHealth() == 200 && bolts().isEmpty(), "non-Mob primary triggered chain");
    }

    private static void dragonbone() {
        CowEntity primary = cow(0, 2), secondary = cow(2, 2);
        hit(IafItemRegistry.DRAGONBONE_SWORD_LIGHTNING, primary);
        require(!bolts().isEmpty() && secondary.getHealth() == 200, "dragonbone sword ability was replaced");
    }

    private static void packets() {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        try {
            ChainLightningPacket original = new ChainLightningPacket("minecraft:overworld", Arrays.asList(new ChainLightningPacket.Link(1, 2, 3, 4, 5, 6)));
            ChainLightningPacket.encode(original, buffer);
            ChainLightningPacket decoded = ChainLightningPacket.decode(buffer);
            require(decoded.dimension.equals(original.dimension) && decoded.links.size() == 1 && decoded.links.get(0).z2 == 6, "codec round-trip failed");
            for (int bad : new int[]{-1, 1024, Integer.MAX_VALUE, 1}) {
                buffer.clear(); buffer.writeUtf("minecraft:overworld"); buffer.writeVarInt(bad);
                try { ChainLightningPacket.decode(buffer); throw new AssertionError("invalid count accepted: " + bad); }
                catch (DecoderException expected) { }
            }
            buffer.clear(); buffer.writeUtf("minecraft:overworld"); buffer.writeVarInt(1);
            for (int i = 0; i < 6; i++) buffer.writeDouble(i == 0 ? Double.NaN : i);
            try { ChainLightningPacket.decode(buffer); throw new AssertionError("NaN accepted"); }
            catch (DecoderException expected) { }
        } finally {
            buffer.release();
        }
    }

    private static void require(boolean passed, String message) {
        if (!passed) throw new AssertionError(message);
    }
}
