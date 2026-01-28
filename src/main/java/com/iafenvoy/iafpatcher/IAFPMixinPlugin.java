package com.iafenvoy.iafpatcher;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class IAFPMixinPlugin implements IMixinConfigPlugin {
    @Override
    public List<String> getMixins() {
        List<String> mixins = new LinkedList<>();
        //? >=1.18 {
        /*mixins.add("TileEntityJarMixin");
        *///?}
        if (FMLEnvironment.dist == Dist.CLIENT) {
            //? >=1.17 {
            /*mixins.add("TitleScreenMixin");
            *///?} else {
            mixins.add("MainMenuScreenMixin");
            //?}
        }
        //? >=1.17 {
            /*mixins.add("EntityHippogryphMixin");
        *///?} else {
        mixins.add("EntityHippogryphMixinOld");
        //?}
        return mixins;
    }

    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
