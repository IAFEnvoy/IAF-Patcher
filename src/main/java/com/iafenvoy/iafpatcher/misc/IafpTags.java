package com.iafenvoy.iafpatcher.misc;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public final class IafpTags {
    public static final Tags.IOptionalNamedTag<Item> PIXIE_STOLEN_BLACKLIST = ItemTags.createOptional(new ResourceLocation(IceAndFire.MODID, "pixie_stolen_blacklist"));
}
