package com.iafenvoy.iafpatcher.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class IAFPClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue CHAIN_LIGHTNING_EFFECTS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        CHAIN_LIGHTNING_EFFECTS = builder.comment("Render dragonsteel chain lightning arcs. Does not affect server damage.")
                .define("chainLightningEffects", true);
        SPEC = builder.build();
    }

    private IAFPClientConfig() {
    }
}
