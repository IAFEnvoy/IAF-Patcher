package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.entity.ai.PixieAISteal;
import com.iafenvoy.iafpatcher.misc.IafpTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PixieAISteal.class)
public class PixieAIStealMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isStackable()Z"))
    private boolean addBlackListTag(ItemStack instance) {
        return instance.isStackable() && !instance.is(IafpTags.PIXIE_STOLEN_BLACKLIST);
    }
}
