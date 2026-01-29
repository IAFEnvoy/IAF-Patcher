package com.iafenvoy.iafpatcher.mixin;

//? <=1.16.5 {
/*import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIRide;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIMate;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITarget;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITargetItems;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.iafenvoy.iafpatcher.misc.HippogryphAIWanderPatched;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHippogryph.class)
public abstract class EntityHippogryphMixinOld extends TameableEntity {
    protected EntityHippogryphMixinOld(EntityType<? extends TameableEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    private void patchGoals(CallbackInfo ci) {
        ci.cancel();
        EntityHippogryph hippogryph = (EntityHippogryph) (Object) this;
        this.goalSelector.addGoal(0, new DragonAIRide<>(hippogryph));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(4, new LookAtGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new HippogryphAIMate(hippogryph, 1.0D));
        this.goalSelector.addGoal(6, new TemptGoal(this, 1.0D, Ingredient.of(Items.RABBIT, Items.COOKED_RABBIT), false));
        this.goalSelector.addGoal(8, new HippogryphAIWanderPatched(hippogryph, 1.0D));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new HippogryphAITargetItems<>(this, false));
        this.targetSelector.addGoal(5, new HippogryphAITarget<>(hippogryph, LivingEntity.class, false, entity -> !(entity instanceof AbstractHorseEntity) && DragonUtils.isAlive(entity)));
        this.targetSelector.addGoal(5, new HippogryphAITarget<>(hippogryph, PlayerEntity.class, 350, false, entity -> entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative()));
    }
}*/