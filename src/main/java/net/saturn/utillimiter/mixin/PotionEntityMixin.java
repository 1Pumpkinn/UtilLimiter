package net.saturn.utillimiter.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin {

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void blockBannedPotions(HitResult hitResult, CallbackInfo ci) {
        PotionEntity potion = (PotionEntity) (Object) this;
        ItemStack stack = potion.getStack();

        PotionContentsComponent contents =
                stack.get(DataComponentTypes.POTION_CONTENTS);

        if (contents == null) return;

        contents.potion().ifPresent(entry -> {
            String id = entry.getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("");

            if (id.equals("turtle_master")
                    || id.equals("long_turtle_master")
                    || id.equals("strong_turtle_master")) {
                ci.cancel();
            }
        });
    }
}
