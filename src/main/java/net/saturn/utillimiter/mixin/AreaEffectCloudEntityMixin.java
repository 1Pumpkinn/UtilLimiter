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
        AreaEffectCloudEntity cloud = (AreaEffectCloudEntity) (Object) this;
        PotionContentsComponent contents = this.potionContentsComponent;
        if (contents == null) return;

        // ---- POTION TYPE CHECK ----
        boolean shouldRemove = contents.potion().map(potionEntry -> {
            String id = potionEntry.getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("");

            return id.equals("turtle_master")
                    || id.equals("long_turtle_master")
                    || id.equals("strong_turtle_master");
        }).orElse(false);

        if (shouldRemove) {
            cloud.discard();
            ci.cancel();
            return;
        }

        // ---- EFFECT CHECK ----
        for (StatusEffectInstance effect : contents.getEffects()) {
            String effectId = effect.getEffectType().getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("");

            int amp = effect.getAmplifier();

            if (effectId.equals("strength") && amp >= 1) {
                cloud.discard();
                ci.cancel();
                return;
            }

            if (effectId.equals("speed") && amp >= 1) {
                cloud.discard();
                ci.cancel();
                return;
            }

            if (effectId.equals("poison")
                    || effectId.equals("instant_damage")
                    || effectId.equals("slow_falling")) {
                cloud.discard();
                ci.cancel();
                return;
            }
        }
    }
}