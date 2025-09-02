package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getItemEnchantmentLevel", at = @At("HEAD"), cancellable = true)
    private static void wrapFireAspect(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (enchantment == Enchantments.FIRE_ASPECT && (stack.getItem() == IafItemRegistry.DRAGONBONE_SWORD_FIRE || stack.getItem() == IafItemRegistry.DRAGONSTEEL_FIRE_SWORD))
            cir.setReturnValue(1);
    }
}
