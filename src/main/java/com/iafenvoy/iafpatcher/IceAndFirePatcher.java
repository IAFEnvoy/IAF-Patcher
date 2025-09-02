package com.iafenvoy.iafpatcher;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(IceAndFirePatcher.MOD_ID)
public class IceAndFirePatcher {
    public static final String MOD_ID = "iaf_patcher";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String VERSION;

    static {
        String version = "UNKNOWN";
        try {
            ModContainer mod = ModList.get().getModContainerById(MOD_ID).orElseThrow(NullPointerException::new);
            version = mod.getModInfo().getVersion().toString();
        } catch (Exception e) {
            LOGGER.error("Failed to get mod version: ", e);
        } finally {
            VERSION = version;
        }
    }

    public IceAndFirePatcher() {
        LOGGER.info("Trying to patch Ice And Fire");
    }
}
