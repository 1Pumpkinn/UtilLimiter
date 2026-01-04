package net.saturn.utillimiter.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MaceDisableMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void preventMaceDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandStack();
            if (weapon.isOf(Items.MACE)) {
                cir.setReturnValue(false);
            }
        }
    }
}