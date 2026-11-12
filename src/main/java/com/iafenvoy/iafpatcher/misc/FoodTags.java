package com.iafenvoy.iafpatcher.misc;

//? >=1.17 {
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//?} else {
/*import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
*///?}
//? >=1.18 {
import net.minecraft.tags.TagKey;
//?} else {
/*import net.minecraftforge.common.Tags;
*///?}
import javax.annotation.Nullable;

public enum FoodTags {
    BREED_AMPITHERE("breed_ampithere"),
    BREED_HIPPOCAMPUS("breed_hippocampus"),
    BREED_HIPPOGRYPH("breed_hippogryph"),
    HEAL_AMPITHERE("heal_ampithere"),
    HEAL_COCKATRICE("heal_cockatrice"),
    HEAL_HIPPOCAMPUS("heal_hippocampus"),
    HEAL_PIXIE("heal_pixie"),
    TAME_HIPPOGRYPH("tame_hippogryph"),
    TAME_PIXIE("tame_pixie");

    private final /*? >=1.18 {*/TagKey<Item>/*?} else {*//*Tags.IOptionalNamedTag<Item>*//*?}*/ tag;

    FoodTags(String path) {
        this.tag = IafpTags.create(path);
    }

    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && stack/*? <=1.16.5 {*//*.getItem()*//*?}*/.is(this.tag);
    }

    /** Replace only the right-hand item constant in a legacy identity comparison. */
    @Nullable
    public Item comparisonItem(ItemStack stack) {
        return this.matches(stack) ? stack.getItem() : null;
    }
}
