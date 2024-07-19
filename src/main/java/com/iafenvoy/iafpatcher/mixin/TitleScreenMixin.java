package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.IafConfig;
import com.iafenvoy.iafpatcher.TitleScreenRenderManager;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow
    @Nullable
    private SplashRenderer splash;

    @Shadow
    @Final
    private boolean fading;

    @Shadow
    private long fadeInStart;

    @Unique
    private GuiGraphics iafpatcher$context;

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (!IafConfig.customMainMenu) return;
        SplashRenderer renderer = TitleScreenRenderManager.getSplash();
        if (renderer != null)
            this.splash = renderer;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        if (!IafConfig.customMainMenu) return;
        TitleScreenRenderManager.tick();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(GuiGraphics p_282860_, int p_281753_, int p_283539_, float p_282628_, CallbackInfo ci) {
        this.iafpatcher$context = p_282860_;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V"))
    private void onRenderBackground(PanoramaRenderer instance, float delta, float alpha) {
        if (!IafConfig.customMainMenu) {
            instance.render(delta, alpha);
            return;
        }
        TitleScreenRenderManager.renderBackground(this.iafpatcher$context, this.width, this.height);
        float f = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
        float g = this.fading ? Mth.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int i = Mth.ceil(g * 255.0F) << 24;
        if ((i & -67108864) != 0)
            TitleScreenRenderManager.drawModName(this.iafpatcher$context, this.height, i);
    }
}
