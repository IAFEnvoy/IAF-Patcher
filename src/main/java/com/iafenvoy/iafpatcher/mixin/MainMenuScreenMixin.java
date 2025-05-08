package com.iafenvoy.iafpatcher.mixin;

import com.github.alexthe666.iceandfire.IafConfig;
import com.iafenvoy.iafpatcher.TitleScreenRenderManager;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderSkybox;render(FF)V"))
    private void onRenderBackground(RenderSkybox instance, float p_110004_, float p_110005_) {
        if (!IafConfig.customMainMenu) instance.render(p_110004_, p_110005_);
        TitleScreenRenderManager.renderBackground(this.width, this.height);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/BrandingControl;forEachLine(ZZLjava/util/function/BiConsumer;)V", remap = false))
    private void renderOwn(int p_render_1_, int p_render_2_, float p_render_3_, CallbackInfo ci) {
        float f = this.fading ? (float) (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
        float g = this.fading ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int i = MathHelper.ceil(g * 255.0F) << 24;
        TitleScreenRenderManager.drawModName(this.height, i);
    }
}
