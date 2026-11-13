package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
//? >=1.17 {
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
//?} else {
/*import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
*///?}
//? >=1.19 {
import net.minecraft.util.RandomSource;
//?} else {
/*import java.util.Random;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityAmphithere.class, remap = false)
public class AmphithereFlightMixin {
    @Inject(method = "tick", remap = true, at = @At("HEAD"))
    private void iafpatcher$clearDismountedInput(CallbackInfo ci) {
        EntityAmphithere amphithere = (EntityAmphithere) (Object) this;
        if (!amphithere.isVehicle()) {
            // No key-release packet arrives after dismount. Also discard movement input left
            // by the previous controller; FlyMoveHelper steers deltaMovement directly.
            amphithere.setControlState((byte) 0);
            amphithere.setYya(0);
            amphithere.setZza(0);
            amphithere.setXxa(0);
        }
    }

    //? <1.20 {
    @ModifyConstant(method = "aiStep", remap = true, constant = @Constant(doubleValue = 0.08))
    private double iafpatcher$balanceAutonomousLift(double lift) {
        EntityAmphithere amphithere = (EntityAmphithere) (Object) this;
        if (amphithere.isVehicle() || amphithere.isInWater() || amphithere.isInLava()) return lift;
        // Vanilla applies 0.98 vertical drag after gravity, before this lift. Adding a
        // full 0.08 afterwards leaves an upward drift; NoGravity needs no compensation.
        return amphithere.isNoGravity() ? 0 : lift * 0.98;
    }
    //?}

    @Inject(method = "getPositionRelativetoGround", at = @At("RETURN"), cancellable = true)
    private static void iafpatcher$boundAutonomousAltitude(Entity entity, /*? >=1.17 {*/Level/*?} else {*//*World*//*?}*/ world,
                                                         /*? >=1.19 {*/int/*?} else {*//*double*//*?}*/ x,
                                                         /*? >=1.19 {*/int/*?} else {*//*double*//*?}*/ z,
                                                         /*? >=1.19 {*/RandomSource/*?} else {*//*Random*//*?}*/ random,
                                                         CallbackInfoReturnable<BlockPos> cir) {
        BlockPos target = cir.getReturnValue();
        // The extra heightmap lookup must not load or generate chunks.
        if (!world.hasChunkAt(target)) return;
        int surface = world.getHeight(Heightmap./*? >=1.17 {*/Types/*?} else {*//*Type*//*?}*/.MOTION_BLOCKING_NO_LEAVES, target.getX(), target.getZ());
        if (surface <= /*? >=1.17 {*/world.getMinBuildHeight()/*?} else {*//*0*//*?}*/) return;
        // The original helper keeps using the current altitude when its short ground scan
        // misses. Give a high autonomous mount a descending target instead of a moving ceiling.
        if (target.getY() > surface + 16) {
            cir.setReturnValue(new BlockPos(target.getX(), surface + 16, target.getZ()));
        }
    }
}
