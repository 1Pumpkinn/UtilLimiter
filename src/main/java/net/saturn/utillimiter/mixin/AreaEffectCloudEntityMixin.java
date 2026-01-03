package net.saturn.utillimiter.mixin;

import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloudEntity.class)
public abstract class AreaEffectCloudEntityMixin {

    @Shadow
    private PotionContentsComponent potionContentsComponent;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void blockBannedCloudEffects(CallbackInfo ci) {

        PotionContentsComponent contents = this.potionContentsComponent;
        if (contents == null) return;

        // ---- POTION TYPE CHECK ----
        contents.potion().ifPresent(potionEntry -> {
            String id = potionEntry.getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("");

            if (id.equals("turtle_master")
                    || id.equals("long_turtle_master")
                    || id.equals("strong_turtle_master")) {
                ci.cancel();
            }
        });

        // ---- EFFECT CHECK ----
        for (StatusEffectInstance effect : contents.getEffects()) {
            String effectId = effect.getEffectType().getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("");

            int amp = effect.getAmplifier();

            if (effectId.equals("strength") && amp >= 1) {
                ci.cancel();
                return;
            }

            if (effectId.equals("speed") && amp >= 1) {
                ci.cancel();
                return;
            }

            if (effectId.equals("poison")
                    || effectId.equals("instant_damage")
                    || effectId.equals("slow_falling")) {
                ci.cancel();
                return;
            }
        }
    }
}
