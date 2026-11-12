package com.iafenvoy.iafpatcher.mixin.food;

import com.github.alexthe666.iceandfire.entity.ai.PixieAIPickupItem;
import com.iafenvoy.iafpatcher.misc.FoodTags;
//? >=1.17 {
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
//?} else {
/*import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PixieAIPickupItem.class, remap = false)
public class LegacyPixiePickupFoodMixin {
    @Shadow
    protected ItemEntity targetEntity;

    @Redirect(method = "tick", remap = true, at = @At(value = "FIELD", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/Items;SUGAR:Lnet/minecraft/world/item/Item;"/*?} else {*//*"Lnet/minecraft/item/Items;SUGAR:Lnet/minecraft/item/Item;"*//*?}*/, remap = true))
    private Item iafpatcher$healingFood() {
        return FoodTags.HEAL_PIXIE.comparisonItem(this.targetEntity.getItem());
    }

    @Redirect(method = "tick", remap = true, at = @At(value = "FIELD", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/Items;CAKE:Lnet/minecraft/world/item/Item;"/*?} else {*//*"Lnet/minecraft/item/Items;CAKE:Lnet/minecraft/item/Item;"*//*?}*/, remap = true))
    private Item iafpatcher$tamingFood() {
        return FoodTags.TAME_PIXIE.comparisonItem(this.targetEntity.getItem());
    }
}
