package com.iafenvoy.iafpatcher;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.objectweb.asm.tree.ClassNode;
//? <1.17 {
/*import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import java.util.ArrayList;
*///?}
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class IAFPMixinPlugin implements IMixinConfigPlugin {
    @Override
    public List<String> getMixins() {
        List<String> mixins = new LinkedList<>();
        //? >=1.17 {
        mixins.add("DragonsteelLightningMixin");
        //?} else {
        /*mixins.add("LegacyDragonsteelLightningMixin");
        *///?}
        //? <1.17 {
        /*if (!FMLEnvironment.production) mixins.add("dev.LegacyScaleMappingMixin");
        *///?}
        //? >=1.20 {
        /*mixins.add("HippocampusSwimmingMixin");
        *///?}
        //? >=1.18 {
        mixins.add("TileEntityJarMixin");
        //?}
        //? <1.19 {
        /*mixins.add("food.LegacyAmphithereFoodMixin");
        mixins.add("food.LegacyHippocampusFoodMixin");
        mixins.add("food.LegacyHippogryphFoodMixin");
        mixins.add("food.LegacyCockatriceFoodMixin");
        mixins.add("food.LegacyPixieFoodMixin");
        mixins.add("food.LegacyPixiePickupFoodMixin");
        *///?}
        if (FMLEnvironment.dist == Dist.CLIENT) {
            //? >=1.17 {
            mixins.add("TitleScreenMixin");
            //?} else {
            /*mixins.add("MainMenuScreenMixin");
            *///?}
        }
        //? >=1.17 {
            mixins.add("EntityHippogryphMixin");
        //?} else {
        /*mixins.add("EntityHippogryphMixinOld");
        *///?}
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
        //? <1.17 {
        /*if (!FMLEnvironment.production && s1.equals("com.iafenvoy.iafpatcher.mixin.dev.LegacyScaleMappingMixin")) {
            repairLegacyScaleMapping(classNode);
        }
        *///?}
    }

    //? <1.17 {
    /*private static void repairLegacyScaleMapping(ClassNode target) {
        List<MethodNode> scaleMethods = new ArrayList<>();
        for (MethodNode method : target.methods) {
            if (method.name.equals("getScale") && method.desc.equals("()F")) scaleMethods.add(method);
        }
        if (scaleMethods.size() < 2) return;
        List<MethodNode> wrappers = new ArrayList<>();
        for (MethodNode method : scaleMethods) {
            List<AbstractInsnNode> code = new ArrayList<>();
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction.getOpcode() >= 0) code.add(instruction);
            }
            if (code.size() != 3 || !(code.get(0) instanceof VarInsnNode)
                    || code.get(0).getOpcode() != Opcodes.ALOAD || ((VarInsnNode) code.get(0)).var != 0
                    || !(code.get(1) instanceof MethodInsnNode) || code.get(1).getOpcode() != Opcodes.INVOKEVIRTUAL
                    || code.get(2).getOpcode() != Opcodes.FRETURN) continue;
            MethodInsnNode call = (MethodInsnNode) code.get(1);
            if (call.owner.equals(target.name) && call.name.equals("getScale") && call.desc.equals("()F")) {
                wrappers.add(method);
            }
        }
        // ForgeGradle merges the SRG override and the custom getter into one name.
        // Drop only the now-recursive forwarding override, keeping the data-backed getter.
        if (scaleMethods.size() != 2 || wrappers.size() != 1) {
            throw new IllegalStateException("Unexpected getScale mapping collision in " + target.name);
        }
        target.methods.remove(wrappers.get(0));
        org.apache.logging.log4j.LogManager.getLogger("IAF Patcher")
                .info("Removed development mapping getScale wrapper from {}", target.name);
    }
    *///?}

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
    }
}
