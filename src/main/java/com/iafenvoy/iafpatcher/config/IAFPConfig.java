package com.iafenvoy.iafpatcher.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class IAFPConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DRAGON_MEAL_RESTORES_GROWTH;
    public static final ForgeConfigSpec.BooleanValue HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("features");
        DRAGON_MEAL_RESTORES_GROWTH = builder
                .comment("Allow the owner's normal dragon meal to resume growth after a sickly dragon meal.",
                        "Food consumption, healing and ownership checks still follow Ice and Fire's normal rules.")
                .define("dragonMealRestoresGrowth", true);
        HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY = builder
                .comment("Prevent bubble columns from pushing or pulling hippocampi, including while ridden.")
                .define("hippocampusBubbleColumnImmunity", false);
        builder.pop();
        SPEC = builder.build();
    }

    private IAFPConfig() {
    }
}
