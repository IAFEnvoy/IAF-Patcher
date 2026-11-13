package com.iafenvoy.iafpatcher.regression;

import com.github.alexthe666.iceandfire.entity.EntityDreadBeast;
import com.github.alexthe666.iceandfire.entity.EntityDreadGhoul;
import com.github.alexthe666.iceandfire.entity.EntityDreadScuttler;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;

/** Confirms the retained implementation still handles actual entity scale and persistence. */
public final class LegacyMappingRegression {
    public static String run(Object serverObject) throws Exception {
        ServerWorld world = ((MinecraftServer) serverObject).overworld();
        LivingEntity[] entities = {
                new EntityDreadBeast(IafEntityRegistry.DREAD_BEAST, world),
                new EntityDreadGhoul(IafEntityRegistry.DREAD_GHOUL, world),
                new EntityDreadScuttler(IafEntityRegistry.DREAD_SCUTTLER, world)
        };
        for (LivingEntity entity : entities) {
            Class<?> type = entity.getClass();
            int getters = 0;
            for (java.lang.reflect.Method method : type.getDeclaredMethods()) {
                if (method.getName().equals("getScale") && method.getParameterCount() == 0) getters++;
            }
            require(getters == 1, type + " has " + getters + " scale getters");
            type.getMethod("setScale", float.class).invoke(entity, 1.25F);
            require(entity.getScale() == 1.25F, type + " lost its data-backed getter");
            CompoundNBT saved = new CompoundNBT();
            entity.addAdditionalSaveData(saved);
            String scaleKey = entity instanceof EntityDreadScuttler ? "Scale" : "DreadScale";
            require(saved.getFloat(scaleKey) == 1.25F, type + " did not save scale");
            type.getMethod("setScale", float.class).invoke(entity, 0.75F);
            entity.readAdditionalSaveData(saved);
            require(entity.getScale() == 1.25F, type + " did not restore scale");
        }
        return "PASS: mapping: all three dread entities load, retain scale and persist it\n";
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
