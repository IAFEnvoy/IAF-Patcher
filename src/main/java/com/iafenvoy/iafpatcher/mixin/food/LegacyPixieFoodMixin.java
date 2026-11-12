package com.iafenvoy.iafpatcher.mixin.food;

import com.github.alexthe666.iceandfire.entity.EntityPixie;
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
@Mixin(value = EntityPixie.class, remap = false)
public class LegacyPixieFoodMixin {

    @Redirect(method = "mobInteract", remap = true, at = @At(value = "FIELD", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/Items;SUGAR:Lnet/minecraft/world/item/Item;"/*?} else {*//*"Lnet/minecraft/item/Items;SUGAR:Lnet/minecraft/item/Item;"*//*?}*/, remap = true))
    private Item iafpatcher$healingFood(/*? >=1.17 {*/Player/*?} else {*//*PlayerEntity*//*?}*/ player, /*? >=1.17 {*/InteractionHand/*?} else {*//*Hand*//*?}*/ hand) {
        return FoodTags.HEAL_PIXIE.comparisonItem(player.getItemInHand(hand));
    }

}

