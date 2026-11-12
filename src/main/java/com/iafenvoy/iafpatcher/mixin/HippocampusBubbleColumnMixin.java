package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.iafenvoy.iafpatcher.config.IAFPConfig;
//? >=1.17 {
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
//?} else {
/*import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
*///?}
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = EntityHippocampus.class, remap = false)
public abstract class HippocampusBubbleColumnMixin extends /*? >=1.17 {*/TamableAnimal/*?} else {*//*TameableEntity*//*?}*/ {
    protected HippocampusBubbleColumnMixin(EntityType<? extends /*? >=1.17 {*/TamableAnimal/*?} else {*//*TameableEntity*//*?}*/> type, /*? >=1.17 {*/Level/*?} else {*//*World*//*?}*/ level) {
        super(type, level);
    }

    @Override
    public void onInsideBubbleColumn(boolean dragDown) {
        if (!IAFPConfig.HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY.get()) {
            super.onInsideBubbleColumn(dragDown);
        }
    }

    @Override
    public void onAboveBubbleCol(boolean dragDown) {
        if (!IAFPConfig.HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY.get()) {
            super.onAboveBubbleCol(dragDown);
        }
    }
}
