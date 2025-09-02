package com.iafenvoy.iafpatcher.misc;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public final class IafpTags {
    public static final Tag<Item> PIXIE_STOLEN_BLACKLIST = new ItemTags.Wrapper(new ResourceLocation(IceAndFire.MODID, "pixie_stolen_blacklist"));
}
