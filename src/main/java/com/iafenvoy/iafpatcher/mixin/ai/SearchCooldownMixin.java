package com.iafenvoy.iafpatcher.mixin.ai;

import com.github.alexthe666.iceandfire.entity.ai.*;
//? >=1.17 {
import net.minecraft.world.level.Level;
//?} else {
/*import net.minecraft.world.World;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {
        AmphithereAIFleePlayer.class,
        AmphithereAITargetItems.class,
        CockatriceAITargetItems.class,
        DragonAITargetItems.class,
        DreadAIRideHorse.class,
        MyrmexAIFindMate.class,
        MyrmexAIForageForItems.class,
        MyrmexAIPickupBabies.class,
        PixieAIFlee.class,
        PixieAIPickupItem.class,
        //? <1.20 {
        HippogryphAITargetItems.class,
        //?}
        //? <=1.16.5 {
        /*DeathwormAITargetItems.class,
        *///?}
}, remap = false)
public class SearchCooldownMixin {
    @Unique
    private long iafpatcher$nextSearch = Long.MIN_VALUE;
    @Unique
    private long iafpatcher$lastTime = Long.MIN_VALUE;

    // Each target reads game time once, only for its % 4 search gate in canUse or updateList.
    // Return a gate value so each goal can search every four ticks on either AI phase.
    @Redirect(method = "*", at = @At(value = "INVOKE", target = /*? >=1.17 {*/"Lnet/minecraft/world/level/Level;getGameTime()J"/*?} else {*//*"Lnet/minecraft/world/World;getGameTime()J"*//*?}*/, remap = true), allow = 1)
    private long iafpatcher$searchGate(/*? >=1.17 {*/Level/*?} else {*//*World*//*?}*/ level) {
        long now = level.getGameTime();
        boolean timeWentBack = now < this.iafpatcher$lastTime;
        this.iafpatcher$lastTime = now;
        if (timeWentBack || now >= this.iafpatcher$nextSearch) {
            this.iafpatcher$nextSearch = now + 4;
            return 0;
        }
        return 1;
    }
}
