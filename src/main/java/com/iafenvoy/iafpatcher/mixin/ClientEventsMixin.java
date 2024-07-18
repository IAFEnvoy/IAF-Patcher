package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.event.ClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientEvents.class,remap = false)
public class ClientEventsMixin {
    @Inject(method = "onGuiOpened", at = @At("HEAD"), cancellable = true)
    private void onTitleSceenHook(CallbackInfo ci) {
        ci.cancel();
    }
}
