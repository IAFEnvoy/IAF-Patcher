package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.ai.PixieAISteal;
import com.iafenvoy.iafpatcher.misc.IafpTags;
//? >=1.17 {
import net.minecraft.world.item.ItemStack;
//?} else {
/*import net.minecraft.item.ItemStack;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PixieAISteal.class)
public class PixieAIStealMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = /*? >=1.17 {*/"Lnet/minecraft/world/item/ItemStack;isStackable()Z"/*?} else {*//*"Lnet/minecraft/item/ItemStack;isStackable()Z"*//*?}*/))
    private boolean addBlackListTag(ItemStack instance) {
        return instance.isStackable() && !instance/*? <=1.16.5 {*//*.getItem()*//*?}*/.is(IafpTags.PIXIE_STOLEN_BLACKLIST);
    }
}
