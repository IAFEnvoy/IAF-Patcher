package com.iafenvoy.iafpatcher.misc;

import com.github.alexthe666.iceandfire.IceAndFire;
//? >=1.17 {
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
//?} else {
/*import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
*///?}
//? >=1.18 {
//? >=1.20 {
/*import net.minecraft.core.registries.Registries;
*///?} else {
import net.minecraft.core.Registry;
//?}
import net.minecraft.tags.TagKey;
//?} else {
/*import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
*///?}

@SuppressWarnings("removal")
public final class IafpTags {
    //? >=1.18 {
    public static final TagKey<Item> PIXIE_STOLEN_BLACKLIST = create("pixie_stolen_blacklist");
    //?} else {
    /*public static final Tags.IOptionalNamedTag<Item> PIXIE_STOLEN_BLACKLIST = create("pixie_stolen_blacklist");
    *///?}

    //? >=1.18 {
    static TagKey<Item> create(String path) {
        return TagKey.create(/*? >=1.20 {*//*Registries.ITEM*//*?} else {*/Registry.ITEM_REGISTRY/*?}*/, new ResourceLocation(IceAndFire.MODID, path));
    }
    //?} else {
    /*static Tags.IOptionalNamedTag<Item> create(String path) {
        return ItemTags.createOptional(new ResourceLocation(IceAndFire.MODID, path));
    }
    *///?}
}
