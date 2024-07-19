package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDragonArmor;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.item.ItemDragonArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LayerDragonArmor.class, remap = false)
public abstract class LayerDragonArmorMixin extends RenderLayer<EntityDragonBase, AdvancedEntityModel<EntityDragonBase>> {
    @Unique
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public LayerDragonArmorMixin(RenderLayerParent<EntityDragonBase, AdvancedEntityModel<EntityDragonBase>> parent) {
        super(parent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/github/alexthe666/iceandfire/entity/EntityDragonBase;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void onRenderArmor(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityDragonBase dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        AdvancedEntityModel<EntityDragonBase> model = this.getParentModel();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = dragon.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            ResourceLocation texture = iafpatcher$getArmorTexture(stack, slot);
            if (texture != null) {
                VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderType.entityCutoutNoCull(texture));
                model.renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        ci.cancel();
    }

    @Unique
    private static ResourceLocation iafpatcher$getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        int ordinal = -1;
        if (!stack.isEmpty() && stack.getItem() instanceof ItemDragonArmor armorItem)
            ordinal = armorItem.type.ordinal();
        return switch (slot) {
            case MAINHAND, OFFHAND -> null;
            case FEET ->
                    ResourceLocation.tryBuild(IceAndFire.MODID, "textures/models/firedragon/armor_tail_" + ordinal + ".png");
            case LEGS ->
                    ResourceLocation.tryBuild(IceAndFire.MODID, "textures/models/firedragon/armor_body_" + ordinal + ".png");
            case CHEST ->
                    ResourceLocation.tryBuild(IceAndFire.MODID, "textures/models/firedragon/armor_neck_" + ordinal + ".png");
            case HEAD ->
                    ResourceLocation.tryBuild(IceAndFire.MODID, "textures/models/firedragon/armor_head_" + ordinal + ".png");
        };
    }
}
