package com.iafenvoy.iafpatcher.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class IAFPConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue DRAGON_MEAL_RESTORES_GROWTH;
    public static final ForgeConfigSpec.BooleanValue HIPPOCAMPUS_BUBBLE_COLUMN_IMMUNITY;
    public static final ForgeConfigSpec.BooleanValue DRAGONSTEEL_CHAIN_LIGHTNING;
    public static final ForgeConfigSpec.DoubleValue CHAIN_LIGHTNING_RANGE;
    public static final ForgeConfigSpec.IntValue CHAIN_LIGHTNING_TARGETS;
    public static final ForgeConfigSpec.DoubleValue CHAIN_LIGHTNING_DAMAGE;

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
        builder.push("chainLightning");
        DRAGONSTEEL_CHAIN_LIGHTNING = builder
                .comment("Replace dragonsteel lightning weapons' vanilla lightning strikes with CE chain lightning.",
                        "Also requires Ice and Fire's dragonWeaponLightningAbility. Disable to restore vanilla behavior.")
                .define("enabled", true);
        CHAIN_LIGHTNING_RANGE = builder.comment("Search radius around the attacker, in blocks.")
                .defineInRange("searchRange", 10.0, 0.0, 128.0);
        CHAIN_LIGHTNING_TARGETS = builder.comment("Maximum targets including the initially struck mob. 0 or 1 disables secondary hits.")
                .defineInRange("maxTargets", 10, 0, 1024);
        CHAIN_LIGHTNING_DAMAGE = builder.comment("Weapon attack damage multiplier for each secondary target.",
                        "Matches CE targeting, including nearby pets and allied mobs.")
                .defineInRange("damageMultiplier", 0.5, 0.0, 1.0);
        builder.pop();
        SPEC = builder.build();
    }

    private IAFPConfig() {
    }
}
