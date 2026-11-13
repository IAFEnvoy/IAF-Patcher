package com.iafenvoy.iafpatcher.regression;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Opt-in development sources; never included in normal builds. */
@Mod.EventBusSubscriber(modid = "iaf_patcher")
public final class MixinApplicationSmoke {
    @SubscribeEvent
    public static void onServerEvent(Event event) {
        if (!event.getClass().getSimpleName().endsWith("ServerStartedEvent")) return;
        Object server = null;
        String result;
        try {
            server = event.getClass().getMethod("getServer").invoke(event);
            String[] targets = {
                    "entity.EntityCyclops", "entity.EntityHydra", "entity.EntityMutlipartPart",
                    "entity.ai.DragonAIMate", "entity.ai.EntitySheepAIFollowCyclops",
                    "entity.ai.EntityAIWatchClosestIgnoreRider", "entity.ai.PixieAIFlee",
                    "entity.ai.AmphithereAIFleePlayer", "entity.ai.AmphithereAITargetItems",
                    "entity.ai.CockatriceAITargetItems", "entity.ai.DragonAITargetItems",
                    "entity.ai.DreadAIRideHorse", "entity.ai.MyrmexAIFindMate",
                    "entity.ai.MyrmexAIForageForItems", "entity.ai.MyrmexAIPickupBabies",
                    "entity.ai.PixieAIPickupItem", "entity.EntityAmphithere", "entity.EntityHippocampus",
                    "entity.EntityHippogryph", "entity.EntityCockatrice", "entity.EntityPixie"
            };
            for (String target : targets) Class.forName("com.github.alexthe666.iceandfire." + target);
            for (String optional : new String[]{"HippogryphAITargetItems", "DeathwormAITargetItems"}) {
                String name = "com.github.alexthe666.iceandfire.entity.ai." + optional;
                if (MixinApplicationSmoke.class.getClassLoader().getResource(name.replace('.', '/') + ".class") != null) Class.forName(name);
            }
            result = "PASS: all new Mixin targets loaded on a real Forge dedicated server.\n";
            try {
                Class<?> regression = Class.forName("com.iafenvoy.iafpatcher.regression.LegacyMappingRegression");
                result += regression.getMethod("run", Object.class).invoke(null, server);
            } catch (ClassNotFoundException ignored) {
                // The mapping collision exists only on 1.16.5 with official development names.
            }
            try {
                Class<?> regression = Class.forName("com.iafenvoy.iafpatcher.regression.SimplePatchRegression");
                result += regression.getMethod("run", Object.class).invoke(null, server);
            } catch (ClassNotFoundException ignored) {
                result += "The earlier simple-patch behavior suite is available for 1.20.1 only.\n";
            }
            try {
                Class<?> regression = Class.forName("com.iafenvoy.iafpatcher.regression.FeatureRegression");
                result += regression.getMethod("run", Object.class).invoke(null, server);
            } catch (ClassNotFoundException ignored) {
                // Feature checks are only included with -PfeatureRegression on 1.18.2 and later.
            }
            try {
                Class<?> regression = Class.forName("com.iafenvoy.iafpatcher.regression.LightningRegression");
                result += regression.getMethod("run", Object.class).invoke(null, server);
            } catch (ClassNotFoundException ignored) {
                // Only included with -PlightningRegression.
            }
            try {
                Class<?> regression = Class.forName("com.iafenvoy.iafpatcher.regression.MovementRegression");
                result += regression.getMethod("run", Object.class).invoke(null, server);
            } catch (ClassNotFoundException ignored) {
                // Movement checks are only included with -PmovementRegression.
            }
        } catch (Throwable failure) {
            if (failure instanceof InvocationTargetException) failure = ((InvocationTargetException) failure).getCause();
            failure.printStackTrace();
            result = "FAIL: " + failure + "\n";
        }
        System.out.println("IAFP_PATCH_REGRESSION " + result);
        try {
            Files.write(Paths.get("patch-regression-result.txt"), result.getBytes(StandardCharsets.UTF_8));
        } catch (Exception failure) {
            throw new RuntimeException(failure);
        } finally {
            if (server != null) {
                try {
                    server.getClass().getMethod("halt", boolean.class).invoke(server, false);
                } catch (Exception failure) {
                    throw new RuntimeException(failure);
                }
            }
        }
    }
}
