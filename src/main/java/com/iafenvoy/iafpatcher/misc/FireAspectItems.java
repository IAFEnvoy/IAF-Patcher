package com.iafenvoy.iafpatcher.misc;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

//? >=1.18 {
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
//?} else {
//import net.minecraft.item.Item;
//?}

import java.util.LinkedList;
import java.util.List;

public final class FireAspectItems {
    private static final List</*? >=1.18 {*/RegistryObject<Item>/*?} else {*//*Item*//*?}*/> ITEMS = new LinkedList<>();

    public static boolean match(Item item) {
        for (/*? >=1.18 {*/RegistryObject<Item>/*?} else {*//*Item*//*?}*/ i : ITEMS)
            if (item == i/*? >=1.18 {*/.get()/*?}*/) return true;
        return false;
    }

    static {
        ITEMS.add(IafItemRegistry.DRAGONBONE_SWORD_FIRE);
        ITEMS.add(IafItemRegistry.DRAGONSTEEL_FIRE_SWORD);
        ITEMS.add(IafItemRegistry.DRAGONSTEEL_FIRE_PICKAXE);
        ITEMS.add(IafItemRegistry.DRAGONSTEEL_FIRE_AXE);
        ITEMS.add(IafItemRegistry.DRAGONSTEEL_FIRE_SHOVEL);
        ITEMS.add(IafItemRegistry.DRAGONSTEEL_FIRE_HOE);
    }
}
