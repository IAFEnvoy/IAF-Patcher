package com.iafenvoy.iafpatcher.regression;

import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.iafenvoy.iafpatcher.config.IAFPConfig;
import com.iafenvoy.iafpatcher.misc.FoodTags;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Predicate;

/** Runs with the test data pack prepared by scripts/test-features.ps1. */
public final class FeatureRegression {
    public static String run(Object serverObject) throws Exception {
        ServerLevel level = ((MinecraftServer) serverObject).overworld();
        ServerPlayer owner = FakePlayerFactory.get(level,
                new GameProfile(UUID.fromString("659ab2fa-e685-4e1b-a98d-6fdd9cb6b9be"), "IAFPTest"));
        // TamableAnimal resolves its owner through the world's player lookup.
        level.addNewPlayer(owner);
        StringBuilder results = new StringBuilder();
        try {
            check("feature: all nine food tags loaded from replacement data pack", results, () -> tags());
            check("feature: amphithere breeding and healing use tags", results, () -> amphithere(level, owner));
            check("feature: hippocampus breeding and healing use tags", results, () -> hippocampus(level, owner));
            check("feature: hippogryph breeding keeps owner checks", results, () -> hippogryph(level, owner));
            check("feature: cockatrice healing rejects removed default foods", results, () -> cockatrice(level, owner));
            check("feature: pixie right-click and pickup healing use tags", results, () -> pixie(level, owner));
            check("feature: dropped-food predicates use all relevant tags", results, () -> predicates(level));
            check("feature: dragon meal config, owner checks and persistence", results, () -> growth(level, owner));
            check("feature: bubble immunity config covers both directions and callbacks", results, () -> bubbles(level));
        } finally {
            owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            owner.discard();
        }
        if (results.indexOf("FAIL:") >= 0) throw new AssertionError("\n" + results);
        return results.toString();
    }

    private static void tags() {
        for (FoodTags tag : FoodTags.values()) {
            Item expected = tag == FoodTags.BREED_AMPITHERE || tag == FoodTags.BREED_HIPPOCAMPUS
                    || tag == FoodTags.BREED_HIPPOGRYPH || tag == FoodTags.HEAL_PIXIE ? Items.EMERALD : Items.DIAMOND;
            require(tag.matches(new ItemStack(expected)), "missing test food for " + tag);
            require(!tag.matches(ItemStack.EMPTY), "empty stack accepted by " + tag);
        }
        require(!FoodTags.HEAL_COCKATRICE.matches(new ItemStack(Items.ROTTEN_FLESH)), "replacement retained rotten flesh");
        require(!FoodTags.BREED_AMPITHERE.matches(new ItemStack(Items.COOKIE)), "replacement retained cookies");
    }

    private static void amphithere(ServerLevel level, ServerPlayer player) {
        EntityAmphithere mob = IafEntityRegistry.AMPHITHERE.get().create(level);
        mob.tame(player);
        mob.setAge(0);
        require(mob.isFood(new ItemStack(Items.EMERALD)), "custom breeding food rejected");
        require(!mob.isFood(new ItemStack(Items.COOKIE)), "removed breeding food accepted");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.EMERALD, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.isInLove() && player.getMainHandItem().getCount() == 1, "breeding interaction failed");
        mob.setHealth(mob.getMaxHealth() - 10);
        float before = mob.getHealth();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.getHealth() > before && player.getMainHandItem().getCount() == 1, "healing interaction failed");
    }

    private static void hippocampus(ServerLevel level, ServerPlayer player) {
        EntityHippocampus mob = IafEntityRegistry.HIPPOCAMPUS.get().create(level);
        mob.tame(player);
        mob.setAge(0);
        require(mob.isFood(new ItemStack(Items.EMERALD)), "custom breeding food rejected");
        require(!mob.isFood(new ItemStack(Items.PRISMARINE_CRYSTALS)), "removed breeding food accepted");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.EMERALD, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.isInLove() && player.getMainHandItem().getCount() == 1, "breeding interaction failed");
        mob.setHealth(mob.getMaxHealth() - 10);
        float before = mob.getHealth();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.getHealth() > before && player.getMainHandItem().getCount() == 1, "healing interaction failed");
    }

    private static void hippogryph(ServerLevel level, ServerPlayer player) {
        EntityHippogryph mob = IafEntityRegistry.HIPPOGRYPH.get().create(level);
        mob.setAge(0);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.EMERALD, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(!mob.isInLove(), "untamed hippogryph was bred");
        mob.tame(player);
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.isInLove() && player.getMainHandItem().getCount() == 1, "owner could not breed hippogryph");
    }

    private static void cockatrice(ServerLevel level, ServerPlayer player) {
        EntityCockatrice mob = IafEntityRegistry.COCKATRICE.get().create(level);
        mob.tame(player);
        mob.setHealth(mob.getMaxHealth() - 12);
        float before = mob.getHealth();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.ROTTEN_FLESH, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.getHealth() == before && player.getMainHandItem().getCount() == 2, "removed default food still healed");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.getHealth() > before && player.getMainHandItem().getCount() == 1, "custom healing food rejected");
    }

    private static void pixie(ServerLevel level, ServerPlayer player) throws Exception {
        EntityPixie mob = IafEntityRegistry.PIXIE.get().create(level);
        mob.tame(player);
        mob.setHealth(1);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.EMERALD, 2));
        mob.mobInteract(player, InteractionHand.MAIN_HAND);
        require(mob.getHealth() > 1 && player.getMainHandItem().getCount() == 1, "custom right-click healing failed");
        mob.setHealth(1);
        mob.setPos(100, 80, 100);
        PixieAIPickupItem<ItemEntity> goal = new PixieAIPickupItem<>(mob, false);
        ItemEntity item = new ItemEntity(level, 100, 80, 100, new ItemStack(Items.EMERALD, 2));
        field(goal, "targetEntity").set(goal, item);
        goal.tick();
        require(mob.getHealth() > 1 && item.getItem().getCount() == 1, "custom pickup healing failed");
    }

    private static void predicates(ServerLevel level) throws Exception {
        EntityAmphithere amphithere = IafEntityRegistry.AMPHITHERE.get().create(level);
        EntityCockatrice cockatrice = IafEntityRegistry.COCKATRICE.get().create(level);
        EntityHippogryph hippogryph = IafEntityRegistry.HIPPOGRYPH.get().create(level);
        for (Object goal : new Object[]{new AmphithereAITargetItems<>(amphithere, false),
                new CockatriceAITargetItems<>(cockatrice, false), new HippogryphAITargetItems<>(hippogryph, false)}) {
            Predicate<ItemEntity> selector = selector(goal);
            require(selector.test(new ItemEntity(level, 100, 80, 100, new ItemStack(Items.DIAMOND))), "custom dropped food rejected by " + goal.getClass());
            require(!selector.test(new ItemEntity(level, 100, 80, 100, new ItemStack(Items.ROTTEN_FLESH))), "removed dropped food accepted");
            require(!selector.test(null), "null dropped item accepted");
        }
        EntityPixie pixie = IafEntityRegistry.PIXIE.get().create(level);
        Predicate<ItemEntity> selector = selector(new PixieAIPickupItem<>(pixie, false));
        ItemEntity diamond = new ItemEntity(level, 100, 80, 100, new ItemStack(Items.DIAMOND));
        ItemEntity emerald = new ItemEntity(level, 100, 80, 100, new ItemStack(Items.EMERALD));
        require(selector.test(diamond) && !selector.test(emerald), "wild pixie taming selection failed");
        pixie.setTame(true);
        pixie.setHealth(1);
        require(selector.test(emerald) && !selector.test(diamond), "tamed pixie healing selection failed");
        pixie.setHealth(pixie.getMaxHealth());
        require(!selector.test(emerald), "full-health pixie selected healing food");
    }

    private static void growth(ServerLevel level, ServerPlayer player) {
        boolean original = IAFPConfig.DRAGON_MEAL_RESTORES_GROWTH.get();
        try {
            EntityFireDragon dragon = new EntityFireDragon(level);
            dragon.tame(player);
            dragon.setAgeInDays(30);
            dragon.setAgingDisabled(true);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(IafItemRegistry.DRAGON_MEAL.get(), 2));
            IAFPConfig.DRAGON_MEAL_RESTORES_GROWTH.set(false);
            dragon.mobInteract(player, InteractionHand.MAIN_HAND);
            require(dragon.isAgingDisabled() && dragon.getAgeInDays() == 30, "disabled option changed growth");
            IAFPConfig.DRAGON_MEAL_RESTORES_GROWTH.set(true);
            dragon.mobInteract(player, InteractionHand.MAIN_HAND);
            require(!dragon.isAgingDisabled() && dragon.getAgeInDays() == 31,
                    "enabled option did not resume growth: disabled=" + dragon.isAgingDisabled()
                            + ", age=" + dragon.getAgeInDays() + ", owned=" + dragon.isOwnedBy(player)
                            + ", food=" + player.getMainHandItem() + ", config=" + IAFPConfig.DRAGON_MEAL_RESTORES_GROWTH.get());
            require(player.getMainHandItem().isEmpty(), "normal food consumption changed");
            CompoundTag saved = new CompoundTag();
            dragon.addAdditionalSaveData(saved);
            require(!saved.getBoolean("AgingDisabled"), "resumed state was not saved");
            EntityFireDragon other = new EntityFireDragon(level);
            other.setAgeInDays(30);
            other.setAgingDisabled(true);
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(IafItemRegistry.DRAGON_MEAL.get()));
            other.mobInteract(player, InteractionHand.MAIN_HAND);
            require(other.isAgingDisabled() && other.getAgeInDays() == 30, "non-owner resumed growth");
        } finally {
            IAFPConfig.DRAGON_MEAL_RESTORES_GROWTH.set(original);
        }
    }

    private static void bubbles(ServerLevel level) {
        boolean original = IAFPConfig.HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY.get();
        try {
            EntityHippocampus mob = IafEntityRegistry.HIPPOCAMPUS.get().create(level);
            mob.setPos(100, 80, 100);
            require(EntityType.PIG.create(level).startRiding(mob, true), "could not set up mounted hippocampus");
            for (boolean above : new boolean[]{false, true}) {
                for (boolean down : new boolean[]{false, true}) {
                    Vec3 initial = new Vec3(0.2, 0.3, 0.4);
                    IAFPConfig.HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY.set(true);
                    mob.setDeltaMovement(initial);
                    if (above) mob.onAboveBubbleCol(down); else mob.onInsideBubbleColumn(down);
                    require(mob.getDeltaMovement().equals(initial), "immunity changed velocity");
                    IAFPConfig.HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY.set(false);
                    mob.setDeltaMovement(initial);
                    if (above) mob.onAboveBubbleCol(down); else mob.onInsideBubbleColumn(down);
                    require(mob.getDeltaMovement().y != initial.y, "disabled option did not restore vanilla motion");
                }
            }
        } finally {
            IAFPConfig.HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY.set(original);
        }
    }

    @SuppressWarnings("unchecked")
    private static Predicate<ItemEntity> selector(Object goal) throws Exception {
        return (Predicate<ItemEntity>) field(goal, "targetEntitySelector").get(goal);
    }

    private static Field field(Object owner, String name) throws NoSuchFieldException {
        Field field = owner.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static void check(String name, StringBuilder result, CheckedRunnable test) {
        try {
            test.run();
            result.append("PASS: ").append(name).append('\n');
            System.out.println("IAFP_CHECK_PASS " + name);
        } catch (Throwable failure) {
            failure.printStackTrace();
            result.append("FAIL: ").append(name).append(" - ").append(failure).append('\n');
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
