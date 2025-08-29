package com.iafenvoy.iafpatcher;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class IafpTags {
    public static final TagKey<Item> PIXIE_STOLEN_BLACKLIST = TagKey.create(Registries.ITEM, new ResourceLocation(IceAndFire.MODID, "pixie_stolen_blacklist"));
}
