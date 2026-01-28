package com.iafenvoy.iafpatcher.misc;

import com.github.alexthe666.iceandfire.IceAndFire;
//? >=1.17 {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
*///?} else {
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
//?}
//? >=1.18 {
/*import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
*///?} else {
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
//?}

@SuppressWarnings("removal")
public final class IafpTags {
    //? >=1.18 {
    /*public static final TagKey<Item> PIXIE_STOLEN_BLACKLIST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(IceAndFire.MODID, "pixie_stolen_blacklist"));
    *///?} else {
    public static final Tags.IOptionalNamedTag<Item> PIXIE_STOLEN_BLACKLIST = ItemTags.createOptional(new ResourceLocation(IceAndFire.MODID, "pixie_stolen_blacklist"));
    //?}
}
