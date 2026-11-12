package com.iafenvoy.iafpatcher.mixin.food;

import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.iafenvoy.iafpatcher.misc.FoodTags;
//? >=1.17 {
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//?} else {
/*import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Only enabled before 1.19; later upstream versions already read these tags. */
@Mixin(value = EntityHippocampus.class, remap = false)
public class LegacyHippocampusFoodMixin {

    @Redirect(method = "mobInteract", remap = true, at = @At(value = "FIELD", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/Items;PRISMARINE_CRYSTALS:Lnet/minecraft/world/item/Item;"/*?} else {*//*"Lnet/minecraft/item/Items;PRISMARINE_CRYSTALS:Lnet/minecraft/item/Item;"*//*?}*/, remap = true))
    private Item iafpatcher$breedingFood(/*? >=1.17 {*/Player/*?} else {*//*PlayerEntity*//*?}*/ player, /*? >=1.17 {*/InteractionHand/*?} else {*//*Hand*//*?}*/ hand) {
        return FoodTags.BREED_HIPPOCAMPUS.comparisonItem(player.getItemInHand(hand));
    }


    @Redirect(method = "mobInteract", remap = true, at = @At(value = "FIELD", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/Items;KELP:Lnet/minecraft/world/item/Item;"/*?} else {*//*"Lnet/minecraft/item/Items;KELP:Lnet/minecraft/item/Item;"*//*?}*/, remap = true))
    private Item iafpatcher$healingFood(/*? >=1.17 {*/Player/*?} else {*//*PlayerEntity*//*?}*/ player, /*? >=1.17 {*/InteractionHand/*?} else {*//*Hand*//*?}*/ hand) {
        return FoodTags.HEAL_HIPPOCAMPUS.comparisonItem(player.getItemInHand(hand));
    }


    @Redirect(method = "isFood", remap = true, at = @At(value = "FIELD", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/Items;PRISMARINE_CRYSTALS:Lnet/minecraft/world/item/Item;"/*?} else {*//*"Lnet/minecraft/item/Items;PRISMARINE_CRYSTALS:Lnet/minecraft/item/Item;"*//*?}*/, remap = true))
    private Item iafpatcher$isBreedingFood(ItemStack stack) {
        return FoodTags.BREED_HIPPOCAMPUS.comparisonItem(stack);
    }

}

