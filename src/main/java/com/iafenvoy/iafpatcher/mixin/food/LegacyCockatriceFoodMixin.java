package com.iafenvoy.iafpatcher.mixin.food;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.iafenvoy.iafpatcher.misc.FoodTags;
//? >=1.17 {
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//?} else {
/*import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityCockatrice.class, remap = false)
public class LegacyCockatriceFoodMixin {
    @Redirect(method = "mobInteract", remap = true, at = @At(value = "INVOKE", target = /*? >=1.17 {*/"Lcom/github/alexthe666/iceandfire/api/FoodUtils;isSeeds(Lnet/minecraft/world/item/ItemStack;)Z"/*?} else {*//*"Lcom/github/alexthe666/iceandfire/api/FoodUtils;isSeeds(Lnet/minecraft/item/ItemStack;)Z"*//*?}*/, remap = false))
    private boolean iafpatcher$healingFood(ItemStack stack) {
        return FoodTags.HEAL_COCKATRICE.matches(stack);
    }

    @Redirect(method = "mobInteract", remap = true, at = @At(value = "FIELD", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/Items;ROTTEN_FLESH:Lnet/minecraft/world/item/Item;"/*?} else {*//*"Lnet/minecraft/item/Items;ROTTEN_FLESH:Lnet/minecraft/item/Item;"*//*?}*/, remap = true))
    private Item iafpatcher$removeHardcodedFoodFallback() {
        // The tag check above now includes rotten flesh when the data pack allows it.
        return null;
    }
}
