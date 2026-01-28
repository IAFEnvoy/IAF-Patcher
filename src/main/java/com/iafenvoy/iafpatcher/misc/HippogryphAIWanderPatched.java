package com.iafenvoy.iafpatcher.misc;

import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
//? >=1.17 {
/*import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
*///?} else {
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
//?}

import java.util.EnumSet;

public class HippogryphAIWanderPatched extends Goal {
    private final EntityHippogryph hippo;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private final double speed;
    private int executionChance;
    private boolean mustUpdate;

    public HippogryphAIWanderPatched(EntityHippogryph creatureIn, double speedIn) {
        this(creatureIn, speedIn, 20);
    }

    public HippogryphAIWanderPatched(EntityHippogryph creatureIn, double speedIn, int chance) {
        this.hippo = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (!this.hippo.canMove()) {
            return false;
        } else if (!this.hippo.isFlying() && !this.hippo.isHovering()) {
            if (!this.mustUpdate && this.hippo.getRandom().nextInt(this.executionChance) != 0) {
                return false;
            } else {
                //? >=1.17 {
                /*Vec3 vec3 = DefaultRandomPos.getPos(this.hippo, 10, 7);
                *///?} else {
                Vector3d vec3 = RandomPositionGenerator.getPos(this.hippo, 10, 7);
                //?}
                if (vec3 == null) {
                    return false;
                } else {
                    this.xPosition = vec3.x;
                    this.yPosition = vec3.y + /*? >=1.19 {*//*this.hippo.getRandom().nextIntBetweenInclusive(-4, 2)*//*?} else >=1.18 {*//*this.hippo.getRandom().nextInt(-4, 2)*//*?} else {*/this.hippo.getRandom().nextInt(7) - 4/*?}*/;
                    this.zPosition = vec3.z;
                    this.mustUpdate = false;
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    public boolean canContinueToUse() {
        return !this.hippo.getNavigation().isDone();
    }

    public void start() {
        this.hippo.getNavigation().moveTo(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }
}
