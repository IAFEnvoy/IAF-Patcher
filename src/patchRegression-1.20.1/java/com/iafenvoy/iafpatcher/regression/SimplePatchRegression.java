package com.iafenvoy.iafpatcher.regression;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.entity.ai.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Behavioral checks against transformed upstream classes in an isolated development world. */
public final class SimplePatchRegression {
    public static String run(Object serverObject) throws Exception {
        ServerLevel level = ((MinecraftServer) serverObject).overworld();
        StringBuilder result = new StringBuilder();
        check("odd-phase food search", result, () -> search(level));
        check("search deadlines and time rollback", result, () -> clock(level));
        check("dragon mating continuation", result, () -> mating(level));
        check("sheep follows living cyclops", result, () -> sheep(level));
        check("pixie flees until arrival", result, () -> pixie(level));
        check("look goal excludes riders", result, () -> look(level));
        check("hydra head indices", result, () -> hydra(level));
        check("one collision query per part tick", result, () -> collision(level));
        check("cyclops slowdown is applied", result, () -> movement(level));
        if (result.indexOf("FAIL:") >= 0) throw new AssertionError("\n" + result);
        return result.toString();
    }

    private static void search(ServerLevel level) {
        EntityPixie pixie = IafEntityRegistry.PIXIE.get().create(level);
        pixie.setPos(100, 80, 100);
        PixieAIPickupItem<ItemEntity> goal = new PixieAIPickupItem<>(pixie, false);
        level.getServer().getWorldData().overworldData().setGameTime(101);
        require(!goal.canUse(), "empty world unexpectedly contains pixie food");
        ItemEntity food = new ItemEntity(level, 101, 80, 100, new ItemStack(Items.CAKE));
        level.addFreshEntity(food);
        level.getServer().getWorldData().overworldData().setGameTime(103);
        require(!goal.canUse(), "search ran before its four-tick deadline");
        level.getServer().getWorldData().overworldData().setGameTime(105);
        require(goal.canUse(), "odd-phase goal never found newly dropped food");
        food.discard();
    }

    private static void clock(ServerLevel level) throws Exception {
        EntityPixie pixie = IafEntityRegistry.PIXIE.get().create(level);
        Object goal = new PixieAIPickupItem<>(pixie, false);
        Method gate = null;
        for (Method method : goal.getClass().getDeclaredMethods()) {
            if (method.getName().contains("iafpatcher$searchGate")) gate = method;
        }
        require(gate != null, "search redirect was not merged");
        gate.setAccessible(true);
        for (long[] pair : new long[][]{{11, 0}, {11, 1}, {13, 1}, {15, 0}, {16, 1}, {2, 0}, {3, 1}, {6, 0}}) {
            level.getServer().getWorldData().overworldData().setGameTime(pair[0]);
            require(((Long) gate.invoke(goal, level)) == pair[1], "wrong search gate at world tick " + pair[0]);
        }
    }

    private static void mating(ServerLevel level) throws Exception {
        EntityFireDragon dragon = new EntityFireDragon(level);
        EntityFireDragon mate = new EntityFireDragon(level);
        mate.setInLove(null);
        DragonAIMate goal = new DragonAIMate(dragon, 1);
        field(goal, "targetMate").set(goal, mate);
        field(goal, "spawnBabyDelay").setInt(goal, 59);
        require(goal.canContinueToUse(), "valid mating stopped or re-ran canUse");
        field(goal, "spawnBabyDelay").setInt(goal, 60);
        require(!goal.canContinueToUse(), "mating ignored its duration limit");
        field(goal, "spawnBabyDelay").setInt(goal, 0);
        mate.resetLove();
        require(!goal.canContinueToUse(), "mating continued after mate reset love");
        field(goal, "targetMate").set(goal, null);
        require(!goal.canContinueToUse(), "mating continued without a mate");
    }

    private static void sheep(ServerLevel level) throws Exception {
        Sheep sheep = EntityType.SHEEP.create(level);
        EntityCyclops cyclops = IafEntityRegistry.CYCLOPS.get().create(level);
        sheep.setPos(200, 80, 200);
        cyclops.setPos(205, 80, 200);
        EntitySheepAIFollowCyclops goal = new EntitySheepAIFollowCyclops(sheep, 1);
        field(goal, "cyclops").set(goal, cyclops);
        require(goal.canContinueToUse(), "sheep rejected living cyclops");
        cyclops.setPos(201, 80, 200);
        require(!goal.canContinueToUse(), "sheep kept following at arrival");
        cyclops.setPos(205, 80, 200);
        cyclops.discard();
        require(!goal.canContinueToUse(), "sheep followed removed cyclops");
        field(goal, "cyclops").set(goal, null);
        require(!goal.canContinueToUse(), "sheep followed missing cyclops");
    }

    private static void pixie(ServerLevel level) throws Exception {
        EntityPixie pixie = IafEntityRegistry.PIXIE.get().create(level);
        Sheep threat = EntityType.SHEEP.create(level);
        pixie.setPos(300, 80, 300);
        pixie.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND));
        PixieAIFlee<Sheep> goal = new PixieAIFlee<>(pixie, Sheep.class, 8, entity -> true);
        field(goal, "closestLivingEntity").set(goal, threat);
        field(goal, "hidePlace").set(goal, new Vec3(310, 80, 300));
        require(goal.canContinueToUse(), "pixie stopped before reaching its hiding place");
        pixie.setPos(310, 80, 300);
        require(!goal.canContinueToUse(), "pixie kept fleeing after arrival");
        pixie.setPos(300, 80, 300);
        pixie.setTame(true);
        require(!goal.canContinueToUse(), "tamed pixie kept fleeing");
        pixie.setTame(false);
        pixie.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        require(!goal.canContinueToUse(), "empty-handed pixie kept fleeing");
    }

    private static void look(ServerLevel level) throws Exception {
        Sheep observer = EntityType.SHEEP.create(level);
        Pig target = EntityType.PIG.create(level);
        observer.setPos(100, 80, 110);
        target.setPos(102, 80, 110);
        level.addFreshEntity(observer);
        level.addFreshEntity(target);
        EntityAIWatchClosestIgnoreRider goal = new EntityAIWatchClosestIgnoreRider(observer, Pig.class, 6);
        require(field(goal, "entity").get(goal) == observer, "observer field is uninitialized");
        observer.getRandom().setSeed(12345);
        boolean found = false;
        for (int i = 0; i < 1000 && !found; i++) found = goal.canUse();
        require(found, "look goal could not select an unrelated nearby entity");
        require(target.startRiding(observer, true), "failed to set up passenger fixture");
        for (int i = 0; i < 1000; i++) require(!goal.canUse(), "look goal selected its own passenger");
        target.stopRiding();
        observer.discard();
        target.discard();
    }

    private static void hydra(ServerLevel level) throws Exception {
        EntityHydra hydra = IafEntityRegistry.HYDRA.get().create(level);
        hydra.setHeadCount(9);
        for (int[] pair : new int[][]{{-1, 0}, {9, 8}, {Integer.MAX_VALUE, 8}, {Integer.MIN_VALUE, 0}, {4, 4}}) {
            hydra.triggerHeadFlags(pair[0]);
            hydra.hurt(level.damageSources().generic(), 0.01F);
            require(field(hydra, "lastHitHead").getInt(hydra) == pair[1], "head index was not bounded: " + pair[0]);
        }
        hydra.setHeadCount(3);
        hydra.triggerHeadFlags(8);
        hydra.hurt(level.damageSources().generic(), 0.01F);
        require(field(hydra, "lastHitHead").getInt(hydra) == 2, "stale head index survived head-count reduction");
    }

    private static void collision(ServerLevel level) {
        Sheep parent = EntityType.SHEEP.create(level);
        parent.setPos(100, 80, 115);
        level.addFreshEntity(parent);
        for (boolean slow : new boolean[]{false, true}) {
            CountingPart part = new CountingPart(parent, slow);
            part.tickCount = 11;
            part.tick();
            require(part.queries == 1, "collision query count was " + part.queries + " for slow=" + slow);
        }
        parent.discard();
    }

    private static void movement(ServerLevel level) {
        boolean previous = IafConfig.cyclopsGriefing;
        IafConfig.cyclopsGriefing = true;
        try {
            EntityCyclops cyclops = IafEntityRegistry.CYCLOPS.get().create(level);
            cyclops.setPos(108, 80, 108);
            BlockPos log = new BlockPos(108, 81, 108);
            level.setBlockAndUpdate(log, Blocks.OAK_LOG.defaultBlockState());
            cyclops.setDeltaMovement(1, 0, 0);
            cyclops.breakBlock();
            require(Math.abs(cyclops.getDeltaMovement().x - 0.6) < 1E-7, "slowdown vector was discarded");
            require(!level.getBlockState(log).is(Blocks.OAK_LOG), "log fixture was not broken");
        } finally {
            IafConfig.cyclopsGriefing = previous;
        }
    }

    private static Field field(Object owner, String name) throws NoSuchFieldException {
        Field field = owner.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void check(String name, StringBuilder result, CheckedRunnable test) throws Exception {
        try {
            test.run();
            result.append("PASS: ").append(name).append('\n');
            System.out.println("IAFP_CHECK_PASS " + name);
        } catch (Throwable failure) {
            result.append("FAIL: ").append(name).append(" - ").append(failure).append('\n');
            failure.printStackTrace();
        }
    }

    private interface CheckedRunnable {
        void run() throws Exception;
    }

    private static class CountingPart extends EntityMutlipartPart {
        private final boolean slow;
        int queries;

        CountingPart(Entity parent, boolean slow) {
            super(IafEntityRegistry.SLOW_MULTIPART.get(), parent, 1, 180, 0, 1, 1, 1);
            this.slow = slow;
        }

        @Override
        public boolean isSlowFollow() {
            return this.slow;
        }

        @Override
        public void collideWithNearbyEntities() {
            this.queries++;
        }
    }
}
