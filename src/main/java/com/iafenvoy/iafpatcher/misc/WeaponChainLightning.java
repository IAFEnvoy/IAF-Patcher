package com.iafenvoy.iafpatcher.misc;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.iafenvoy.iafpatcher.config.IAFPConfig;
import com.iafenvoy.iafpatcher.network.ChainLightningPacket;
//? >=1.17 {
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
//?} else {
/*import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.DamageSource;
*///?}
//? >=1.17 <1.20 {
import net.minecraft.world.damagesource.DamageSource;
//?}

import java.util.ArrayList;
import java.util.List;

public final class WeaponChainLightning {
    private WeaponChainLightning() {
    }

    public static boolean replacesStrike(ItemStack stack) {
        if (!IAFPConfig.DRAGONSTEEL_CHAIN_LIGHTNING.get()) return false;
        Item item = stack.getItem();
        return item == IafItemRegistry.DRAGONSTEEL_LIGHTNING_SWORD/*? >=1.17 {*/.get()/*?}*/
                || item == IafItemRegistry.DRAGONSTEEL_LIGHTNING_AXE/*? >=1.17 {*/.get()/*?}*/
                || item == IafItemRegistry.DRAGONSTEEL_LIGHTNING_PICKAXE/*? >=1.17 {*/.get()/*?}*/
                || item == IafItemRegistry.DRAGONSTEEL_LIGHTNING_SHOVEL/*? >=1.17 {*/.get()/*?}*/
                || item == IafItemRegistry.DRAGONSTEEL_LIGHTNING_HOE/*? >=1.17 {*/.get()/*?}*/;
    }

    public static void hit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!replacesStrike(stack) || !IafConfig.dragonWeaponLightningAbility
                || !(attacker./*? >=1.20 {*/ /*level()*/ /*?} else {*/level/*?}*/ instanceof /*? >=1.17 {*/ServerLevel/*?} else {*//*ServerWorld*//*?}*/)
                || !(target instanceof /*? >=1.17 {*/Mob/*?} else {*//*MobEntity*//*?}*/)) return;
        int budget = IAFPConfig.CHAIN_LIGHTNING_TARGETS.get();
        if (budget <= 1) return;
        /*? >=1.17 {*/ServerLevel/*?} else {*//*ServerWorld*//*?}*/ level = (/*? >=1.17 {*/ServerLevel/*?} else {*//*ServerWorld*//*?}*/) attacker./*? >=1.20 {*/ /*level()*/ /*?} else {*/level/*?}*/;
        double range = IAFPConfig.CHAIN_LIGHTNING_RANGE.get();
        double baseDamage = 0;
        for (AttributeModifier modifier : stack.getAttributeModifiers(/*? >=1.17 {*/EquipmentSlot/*?} else {*//*EquipmentSlotType*//*?}*/.MAINHAND).get(Attributes.ATTACK_DAMAGE))
            baseDamage += modifier.getAmount();
        float damage = (float) ((baseDamage > 0 ? baseDamage : 1) * IAFPConfig.CHAIN_LIGHTNING_DAMAGE.get());
        List<ChainLightningPacket.Link> links = new ArrayList<>();
        /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/ start = target.getBoundingBox().getCenter();
        // CE's search is centered on the attacker. Query once; each selected mob is visited once.
        List<? extends LivingEntity> candidates = level.getEntitiesOfClass(/*? >=1.17 {*/Mob/*?} else {*//*MobEntity*//*?}*/.class,
                new /*? >=1.17 {*/AABB/*?} else {*//*AxisAlignedBB*//*?}*/(attacker.getX() - range, attacker.getY() - range, attacker.getZ() - range,
                        attacker.getX() + range, attacker.getY() + range, attacker.getZ() + range));
        for (LivingEntity next : candidates) {
            if (links.size() >= budget - 1) break;
            if (next == target || next == attacker || !next.isAlive() || !attacker./*? >=1.17 {*/hasLineOfSight/*?} else {*//*canSee*//*?}*/(next)) continue;
            /*? >=1.17 {*/Vec3/*?} else {*//*Vector3d*//*?}*/ end = next.getBoundingBox().getCenter();
            next.hurt(/*? >=1.20 {*/ /*level.damageSources().mobAttack(attacker)*/ /*?} else {*/DamageSource.mobAttack(attacker)/*?}*/, damage);
            links.add(new ChainLightningPacket.Link(start.x, start.y, start.z, end.x, end.y, end.z));
        }
        if (!links.isEmpty()) ChainLightningPacket.send(level, attacker, links);
    }
}
