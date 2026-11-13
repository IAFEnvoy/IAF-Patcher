package com.iafenvoy.iafpatcher.regression;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.reflect.Method;
import java.util.UUID;

/** Exercises actual upstream entities and controllers in an isolated Forge server world. */
public final class MovementRegression {
    public static String run(Object serverObject) throws Exception {
        ServerWorld level = ((MinecraftServer) serverObject).overworld();
        ServerPlayerEntity owner = FakePlayerFactory.get(level, new GameProfile(
                UUID.fromString("926c01d8-0124-47c2-9b33-760ac9071588"), "IAFPMovement"));
        level.addNewPlayer(owner);
        StringBuilder results = new StringBuilder();
        try {
            check("dismount clears controls", results, () -> controls(level, owner));
            check("flute landing rejects stale takeoff input", results, () -> flute(level, owner));
            check("rider controls survive while mounted", results, () -> mounted(level, owner));
            check("autonomous flight recovers from stale movement input", results, () -> flight(level));
            check("high flight targets descend over land and water", results, () -> targets(level));
            check("autonomous flight returns from high altitude", results, () -> autonomousFlight(level));
        } finally {
            owner.stopRiding();
            owner.remove();
        }
        if (results.indexOf("FAIL:") >= 0) throw new AssertionError("\n" + results);
        return results.toString();
    }

    private static void controls(ServerWorld level, ServerPlayerEntity owner) {
        EntityAmphithere mob = amphithere(level);
        try {
            mob.tame(owner);
            require(owner.startRiding(mob, true), "could not mount fixture");
            mob.setControlState((byte) 15);
            owner.stopRiding();
            mob.tick();
            require(mob.getControlState() == 0, "dismounted input remains " + mob.getControlState());
        } finally {
            owner.stopRiding();
            mob.remove();
        }
    }

    private static void flute(ServerWorld level, ServerPlayerEntity owner) {
        EntityAmphithere mob = amphithere(level);
        BlockPos floor = new BlockPos(0, 99, 0);
        try {
            mob.tame(owner);
            // The flute lands the mount, but an old UP bit used to bypass its flight cooldown.
            mob.setFlying(true);
            mob.onHearFlute(owner);
            level.setBlock(floor, Blocks.STONE.defaultBlockState(), 2);
            mob.setPos(0.5, 100, 0.5);
            mob.setDeltaMovement(0, -0.08, 0);
            mob.setOnGround(true);
            mob.setControlState((byte) 1);
            mob.tick();
            require(!mob.isFlying(), "stale UP input relaunched after landing");
        } finally {
            level.setBlock(floor, Blocks.AIR.defaultBlockState(), 2);
            mob.remove();
        }
    }

    private static void mounted(ServerWorld level, ServerPlayerEntity owner) {
        EntityAmphithere mob = amphithere(level);
        try {
            mob.tame(owner);
            mob.setFlying(true);
            require(owner.startRiding(mob, true), "could not mount fixture");
            mob.setControlState((byte) 1);
            mob.tick();
            require(mob.isGoingUp(), "UP input was cleared while riding");
        } finally {
            owner.stopRiding();
            mob.remove();
        }
    }

    private static void flight(ServerWorld level) throws Exception {
        for (boolean noGravity : new boolean[]{false, true}) flight(level, noGravity);
    }

    private static void flight(ServerWorld level, boolean noGravity) throws Exception {
        EntityAmphithere mob = amphithere(level);
        try {
            mob.setFlying(true);
            Method switchNavigator = EntityAmphithere.class.getDeclaredMethod("switchNavigator", int.class);
            switchNavigator.setAccessible(true);
            switchNavigator.invoke(mob, 1);
            mob.setYya(1);
            mob.setZza(1);
            mob.setNoGravity(noGravity);
            mob.setDeltaMovement(Vector3d.ZERO);
            double startY = mob.getY();
            for (int i = 0; i < 160; i++) mob.tick();
            System.out.println("IAFP_MOVEMENT flight noGravity=" + noGravity + " start=" + startY + " end=" + mob.getY() + " yya=" + mob.yya);
            require(mob.yya == 0, "autonomous controller retained vertical input " + mob.yya);
            require(mob.getY() < startY + 0.2, "uncontrolled climb to " + mob.getY());
        } finally {
            mob.remove();
        }
    }

    private static void targets(ServerWorld level) {
        EntityAmphithere mob = amphithere(level);
        try {
            for (boolean water : new boolean[]{false, true}) {
                level.setBlock(new BlockPos(0, 70, 0), (water ? Blocks.WATER : Blocks.STONE).defaultBlockState(), 2);
                mob.setPos(0.5, 220, 0.5);
                BlockPos target = EntityAmphithere.getPositionRelativetoGround(mob, level, 0, 0, mob.getRandom());
                require(target.getY() >= 72 && target.getY() <= 103,
                        "high target stayed at " + target + " over " + (water ? "water" : "land"));
            }
        } finally {
            level.setBlock(new BlockPos(0, 70, 0), Blocks.AIR.defaultBlockState(), 2);
            mob.remove();
        }
    }

    private static void autonomousFlight(ServerWorld level) {
        EntityAmphithere mob = IafEntityRegistry.AMPHITHERE.create(level);
        try {
            mob.setTame(true);
            mob.setPos(0.5, 220, 0.5);
            mob.setFlying(true);
            mob.setDeltaMovement(0, 0.8, 0);
            mob.getRandom().setSeed(29218);
            level.addFreshEntity(mob);
            double highest = mob.getY();
            double lowest = mob.getY();
            for (int i = 0; i < 320; i++) {
                mob.tick();
                highest = Math.max(highest, mob.getY());
                lowest = Math.min(lowest, mob.getY());
            }
            System.out.println("IAFP_MOVEMENT autonomous highest=" + highest + " lowest=" + lowest + " end=" + mob.position());
            require(highest < 232, "autonomous AI kept climbing to " + highest);
            require(lowest < 180, "autonomous AI did not descend: " + lowest);
        } finally {
            mob.remove();
        }
    }

    private static EntityAmphithere amphithere(ServerWorld level) {
        EntityAmphithere mob = IafEntityRegistry.AMPHITHERE.create(level);
        mob.setPos(0.5, 180, 0.5);
        removeGoals(mob);
        level.addFreshEntity(mob);
        return mob;
    }

    private static void removeGoals(MobEntity mob) {
        // 1.16.5 has no public accessor for the registered goals.
        try {
            java.lang.reflect.Field goals = net.minecraft.entity.ai.goal.GoalSelector.class.getDeclaredField("availableGoals");
            goals.setAccessible(true);
            ((java.util.Collection<?>) goals.get(mob.goalSelector)).clear();
            ((java.util.Collection<?>) goals.get(mob.targetSelector)).clear();
        } catch (ReflectiveOperationException failure) {
            throw new AssertionError("Could not isolate movement fixture", failure);
        }
    }

    private static void check(String name, StringBuilder results, Check action) {
        try {
            action.run();
            results.append("PASS: movement: ").append(name).append('\n');
        } catch (Throwable failure) {
            results.append("FAIL: movement: ").append(name).append(": ").append(failure).append('\n');
            failure.printStackTrace();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private interface Check {
        void run() throws Exception;
    }
}
