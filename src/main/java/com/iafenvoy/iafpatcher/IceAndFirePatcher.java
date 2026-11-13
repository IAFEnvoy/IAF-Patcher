package com.iafenvoy.iafpatcher;

import com.iafenvoy.iafpatcher.config.IAFPConfig;
import com.iafenvoy.iafpatcher.config.IAFPClientConfig;
import com.iafenvoy.iafpatcher.network.ChainLightningPacket;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
//? >=1.18 {
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
//?} else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///?}

@Mod(IceAndFirePatcher.MOD_ID)
public class IceAndFirePatcher {
    public static final String MOD_ID = "iaf_patcher";
    public static final Logger LOGGER = /*? >=1.18 {*/LogUtils/*?} else {*//*LogManager*//*?}*/.getLogger();
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, IAFPConfig.SPEC, "iaf_patcher-server.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, IAFPClientConfig.SPEC, "iaf_patcher-client.toml");
        ChainLightningPacket.register();
        LOGGER.info("Trying to patch Ice And Fire");
    }
}
