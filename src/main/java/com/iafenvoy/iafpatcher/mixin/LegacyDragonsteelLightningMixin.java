//? <1.17 {
/*package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.item.ItemModSword;
import com.github.alexthe666.iceandfire.item.ItemModAxe;
import com.github.alexthe666.iceandfire.item.ItemModPickaxe;
import com.github.alexthe666.iceandfire.item.ItemModShovel;
import com.github.alexthe666.iceandfire.item.ItemModHoe;
import com.iafenvoy.iafpatcher.misc.WeaponChainLightning;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {ItemModSword.class, ItemModAxe.class, ItemModPickaxe.class, ItemModShovel.class, ItemModHoe.class}, remap = false)
public class LegacyDragonsteelLightningMixin {
    @Inject(method = "hurtEnemy", remap = true, at = @At("HEAD"))
    private void iafpatcher$chainLightning(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        WeaponChainLightning.hit(stack, target, attacker);
    }

    @Redirect(method = "hurtEnemy", remap = true, at = @At(value = "FIELD", target = "Lcom/github/alexthe666/iceandfire/IafConfig;dragonWeaponLightningAbility:Z", remap = false))
    private boolean iafpatcher$replaceVanillaStrike(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return IafConfig.dragonWeaponLightningAbility && !WeaponChainLightning.replacesStrike(stack);
    }
}
*///?}
