package net.saturn.utillimiter.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockBadEffects(
            StatusEffectInstance effect,
            Entity source,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // Block all effects from tipped arrows
        if (source instanceof net.minecraft.entity.projectile.ArrowEntity) {
            cir.setReturnValue(false);
            return;
        }
        
        if (!(source instanceof PotionEntity)) return;

        String id = effect.getEffectType().getKey()
                .map(k -> k.getValue().getPath())
                .orElse("");

        int amp = effect.getAmplifier();

        if (id.equals("strength") && amp >= 1) {
            cir.setReturnValue(false);
            return;
        }

        if (id.equals("speed") && amp >= 1) {
            cir.setReturnValue(false);
            return;
        }

        if (id.equals("poison")
                || id.equals("instant_damage")
                || id.equals("slow_falling")) {
            cir.setReturnValue(false);
        }
    }
}
