package com.iafenvoy.iafpatcher.regression;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

/** Exercises actual upstream entities and controllers in an isolated Forge server world. */
public final class MovementRegression {
    public static String run(Object serverObject) throws Exception {
        ServerLevel level = ((MinecraftServer) serverObject).overworld();
        ServerPlayer owner = FakePlayerFactory.get(level, new GameProfile(
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
            if (hasHippoController()) {
                water(level);
                check("hippocampus idle swimming holds depth", results, () -> idleSwimming(level));
                check("hippocampus dives from surface without affecting riders", results, () -> surfaceSwimming(level, owner));
            }
        } finally {
            owner.stopRiding();
            owner.discard();
        }
        if (results.indexOf("FAIL:") >= 0) throw new AssertionError("\n" + results);
        return results.toString();
    }

    private static void controls(ServerLevel level, ServerPlayer owner) {
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
            mob.discard();
        }
    }

    private static void flute(ServerLevel level, ServerPlayer owner) {
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
            mob.discard();
        }
    }

    private static void mounted(ServerLevel level, ServerPlayer owner) {
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
            mob.discard();
        }
    }

    private static void flight(ServerLevel level) throws Exception {
        for (boolean noGravity : new boolean[]{false, true}) flight(level, noGravity);
    }

    private static void flight(ServerLevel level, boolean noGravity) throws Exception {
        EntityAmphithere mob = amphithere(level);
        try {
            mob.setFlying(true);
            Method switchNavigator = EntityAmphithere.class.getDeclaredMethod("switchNavigator", int.class);
            switchNavigator.setAccessible(true);
            switchNavigator.invoke(mob, 1);
            mob.setYya(1);
            mob.setZza(1);
            mob.setNoGravity(noGravity);
            mob.setDeltaMovement(Vec3.ZERO);
            double startY = mob.getY();
            for (int i = 0; i < 160; i++) mob.tick();
            System.out.println("IAFP_MOVEMENT flight noGravity=" + noGravity + " start=" + startY + " end=" + mob.getY() + " yya=" + mob.yya);
            require(mob.yya == 0, "autonomous controller retained vertical input " + mob.yya);
            require(mob.getY() < startY + 0.2, "uncontrolled climb to " + mob.getY());
        } finally {
            mob.discard();
        }
    }

    private static void targets(ServerLevel level) {
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
            mob.discard();
        }
    }

    private static void autonomousFlight(ServerLevel level) {
        EntityAmphithere mob = IafEntityRegistry.AMPHITHERE.get().create(level);
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
            mob.discard();
        }
    }

    private static void water(ServerLevel level) {
        for (int x = 4; x <= 12; x++) for (int z = 4; z <= 12; z++) {
            for (int y = 89; y <= 100; y++) {
                level.setBlock(new BlockPos(x, y, z), (y == 89 ? Blocks.STONE : Blocks.WATER).defaultBlockState(), 2);
            }
        }
    }

    private static void idleSwimming(ServerLevel level) {
        EntityHippocampus mob = hippocampus(level, 94);
        try {
            double startY = mob.getY();
            for (int i = 0; i < 160; i++) mob.tick();
            require(Math.abs(mob.getY() - startY) < 0.2, "idle mount drifted from " + startY + " to " + mob.getY());
        } finally {
            mob.discard();
        }
    }

    private static void surfaceSwimming(ServerLevel level, ServerPlayer owner) throws Exception {
        EntityHippocampus mob = hippocampus(level, 100);
        try {
            for (int i = 0; i < 100; i++) mob.tick();
            require(mob.getY() < 99, "surface mount did not dive: " + mob.getY());
            mob.setPos(8, 100, 8);
            mob.setOrderedToSit(true);
            mob.setDeltaMovement(Vec3.ZERO);
            mob.getMoveControl().tick();
            require(mob.getDeltaMovement().y == 0, "sitting mount was forced to dive");
            mob.setOrderedToSit(false);
            mob.tame(owner);
            mob.setSaddled(true);
            require(owner.startRiding(mob, true), "could not mount hippocampus fixture");
            mob.setDeltaMovement(Vec3.ZERO);
            mob.getMoveControl().tick();
            require(mob.getDeltaMovement().y == 0, "autonomous buoyancy changed rider movement");
            owner.stopRiding();
            require(mob.getNavigation().moveTo(8, 96, 8, 1), "could not prepare underwater path");
            require(!mob.getNavigation().isDone(), "underwater path was already finished");
            mob.setDeltaMovement(Vec3.ZERO);
            Method updateSpeed = mob.getMoveControl().getClass().getDeclaredMethod("updateSpeed");
            updateSpeed.setAccessible(true);
            updateSpeed.invoke(mob.getMoveControl());
            require(mob.getDeltaMovement().y == 0, "idle diving impulse interfered with an active path");
        } finally {
            owner.stopRiding();
            mob.discard();
        }
    }

    private static EntityAmphithere amphithere(ServerLevel level) {
        EntityAmphithere mob = IafEntityRegistry.AMPHITHERE.get().create(level);
        mob.setPos(0.5, 180, 0.5);
        removeGoals(mob);
        level.addFreshEntity(mob);
        return mob;
    }

    private static EntityHippocampus hippocampus(ServerLevel level, double y) {
        EntityHippocampus mob = IafEntityRegistry.HIPPOCAMPUS.get().create(level);
        mob.setPos(8, y, 8);
        removeGoals(mob);
        level.addFreshEntity(mob);
        mob.tick(); // Populate vanilla water-contact state before checking the controller.
        mob.setDeltaMovement(Vec3.ZERO);
        return mob;
    }

    private static void removeGoals(Mob mob) {
        // The predicate overload was added in 1.20; removing a snapshot works on all three versions.
        new ArrayList<>(mob.goalSelector.getAvailableGoals()).forEach(goal -> mob.goalSelector.removeGoal(goal.getGoal()));
        new ArrayList<>(mob.targetSelector.getAvailableGoals()).forEach(goal -> mob.targetSelector.removeGoal(goal.getGoal()));
    }

    private static boolean hasHippoController() {
        for (Class<?> inner : EntityHippocampus.class.getDeclaredClasses()) {
            if (inner.getSimpleName().equals("HippoMoveControl")) return true;
        }
        return false;
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
