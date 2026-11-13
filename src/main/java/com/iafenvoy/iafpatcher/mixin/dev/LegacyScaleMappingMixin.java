package com.iafenvoy.iafpatcher.mixin.dev;

//? <1.17 {
/*import org.spongepowered.asm.mixin.Mixin;

// Development-only target hook. The plugin removes the exact conflicting wrapper
// before these mapped classes reach the JVM; production SRG classes remain untouched.
// String targets keep reobf from eagerly parsing the invalid mapped dependency classes.
@Mixin(targets = {
        "com.github.alexthe666.iceandfire.entity.EntityDreadBeast",
        "com.github.alexthe666.iceandfire.entity.EntityDreadGhoul",
        "com.github.alexthe666.iceandfire.entity.EntityDreadScuttler"
}, remap = false)
public class LegacyScaleMappingMixin {
}
*///?}
