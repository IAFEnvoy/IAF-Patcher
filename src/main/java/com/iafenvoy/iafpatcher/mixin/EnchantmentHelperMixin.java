package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
//? >=1.17 {
/*import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
*///?} else {
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getItemEnchantmentLevel", at = @At("HEAD"), cancellable = true)
    private static void wrapFireAspect(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment == Enchantments.FIRE_ASPECT && (stack.getItem() == IafItemRegistry.DRAGONBONE_SWORD_FIRE/*? >=1.18 {*//*.get()*//*?}*/ || stack.getItem() == IafItemRegistry.DRAGONSTEEL_FIRE_SWORD/*? >=1.18 {*//*.get()*//*?}*/))
            cir.setReturnValue(1);
    }
}
