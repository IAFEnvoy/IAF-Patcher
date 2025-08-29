package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.web.WebHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings({"LoggingSimilarMessage", "CallToPrintStackTrace"})
@Mixin(value = WebHelper.class, remap = false)
public class CitadelMixin {
    @Inject(method = "getURLContents", at = @At("HEAD"), cancellable = true)
    private static void wrapPatreonListToSingleThread(String urlString, String backupFileLoc, CallbackInfoReturnable<BufferedReader> cir) {
        cir.setReturnValue(null);
        new Thread(() -> {
            BufferedReader urlContents = null;
            try {
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                InputStream stream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream);
                urlContents = new BufferedReader(reader);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (urlContents != null) {
                String line;
                try {
                    while ((line = urlContents.readLine()) != null) {
                        Citadel.PATREONS.add(line);
                    }
                } catch (IOException var3) {
                    Citadel.LOGGER.warn("Failed to load patreon contributor perks");
                }
            } else {
                Citadel.LOGGER.warn("Failed to load patreon contributor perks");
            }
        }).start();
    }
}
