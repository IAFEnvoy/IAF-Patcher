package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.web.WebHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.IOException;

@Mixin(value = Citadel.class, remap = false)
public class CitadelMixin {
    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/citadel/web/WebHelper;getURLContents(Ljava/lang/String;Ljava/lang/String;)Ljava/io/BufferedReader;"), cancellable = true)
    private void wrapPatreonListToSingleThread(CallbackInfo ci) {
        ci.cancel();
        new Thread(() -> {
            BufferedReader urlContents = WebHelper.getURLContents("https://raw.githubusercontent.com/Alex-the-666/Citadel/master/src/main/resources/assets/citadel/patreon.txt", "assets/citadel/patreon.txt");
            if (urlContents != null) {
                String line;
                try {
                    while ((line = urlContents.readLine()) != null) {
                        Citadel.PATREONS.add(line);
                    }
                } catch (IOException var5) {
                    Citadel.LOGGER.warn("Failed to load patreon contributor perks");
                }
            } else Citadel.LOGGER.warn("Failed to load patreon contributor perks");
        }).start();
    }
}
