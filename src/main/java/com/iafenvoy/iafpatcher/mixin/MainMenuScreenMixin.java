package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.IafConfig;
import com.iafenvoy.iafpatcher.TitleScreenRenderManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MainMenuScreen.class)
public abstract class MainMenuScreenMixin extends Screen {
    @Shadow
    private String splash;
    @Shadow
    @Final
    private boolean fading;
    @Shadow
    private long fadeInStart;
    @Unique
    private MatrixStack iafpatcher$poseStack;

    protected MainMenuScreenMixin(ITextComponent title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (!IafConfig.customMainMenu) return;
        String text = TitleScreenRenderManager.getSplash();
        if (text != null)
            this.splash = text;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        if (!IafConfig.customMainMenu) return;
        TitleScreenRenderManager.tick();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(MatrixStack p_96739_, int p_96740_, int p_96741_, float p_96742_, CallbackInfo ci) {
        this.iafpatcher$poseStack = p_96739_;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderSkybox;render(FF)V"))
    private void onRenderBackground(RenderSkybox instance, float p_110004_, float p_110005_) {
        if (!IafConfig.customMainMenu) instance.render(p_110004_, p_110005_);
        TitleScreenRenderManager.renderBackground(this.iafpatcher$poseStack, this.width, this.height);
        float f = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
        float g = this.fading ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int i = MathHelper.ceil(g * 255.0F) << 24;
        if ((i & -67108864) != 0)
            TitleScreenRenderManager.drawModName(this.iafpatcher$poseStack, this.height, i);
    }
}
