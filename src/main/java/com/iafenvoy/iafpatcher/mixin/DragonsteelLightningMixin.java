//? >=1.17 {
package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.item.DragonSteelOverrides;
import com.github.alexthe666.iceandfire.item.ItemModSword;
import com.github.alexthe666.iceandfire.item.ItemModAxe;
import com.github.alexthe666.iceandfire.item.ItemModPickaxe;
import com.github.alexthe666.iceandfire.item.ItemModShovel;
import com.github.alexthe666.iceandfire.item.ItemModHoe;
import com.iafenvoy.iafpatcher.misc.WeaponChainLightning;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {ItemModSword.class, ItemModAxe.class, ItemModPickaxe.class, ItemModShovel.class, ItemModHoe.class}, remap = false)
public class DragonsteelLightningMixin {
    // These calls resolve through each concrete class to an interface default method.
    // Redirect the helper call, preserving the caller's super.hurtEnemy durability handling.
    @Redirect(method = "hurtEnemy(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)Z", remap = true,
            at = @At(value = "INVOKE", target = "hurtEnemy(Lnet/minecraft/world/item/TieredItem;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;)V", remap = false))
    @SuppressWarnings("unchecked")
    private void iafpatcher$chainLightning(@Coerce Object instance, TieredItem item, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (WeaponChainLightning.replacesStrike(stack)) WeaponChainLightning.hit(stack, target, attacker);
        else ((DragonSteelOverrides<TieredItem>) instance).hurtEnemy(item, stack, target, attacker);
    }
}
//?}
