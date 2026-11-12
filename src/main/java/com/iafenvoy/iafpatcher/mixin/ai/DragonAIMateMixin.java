package com.iafenvoy.iafpatcher.mixin.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIMate;
//? >=1.17 {
import net.minecraft.world.entity.ai.goal.Goal;
//?} else {
/*import net.minecraft.entity.ai.goal.Goal;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = DragonAIMate.class, remap = false)
public abstract class DragonAIMateMixin extends Goal {
    @Shadow
    @Final
    private EntityDragonBase dragon;
    @Shadow
    private EntityDragonBase targetMate;
    @Shadow
    int spawnBabyDelay;

    @Override
    public boolean canContinueToUse() {
        return this.targetMate != null && this.dragon.canMove()
                && this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }
}
