package com.iafenvoy.iafpatcher;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(IceAndFirePatcher.MOD_ID)
public class IceAndFirePatcher {
    public static final String MOD_ID = "iaf_patcher";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String VERSION = "1.0";

    public IceAndFirePatcher() {
        LOGGER.info("Trying to patch Ice And Fire");
    }
}
