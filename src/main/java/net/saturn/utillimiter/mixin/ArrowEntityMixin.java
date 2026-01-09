package net.saturn.utillimiter.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void blockTippedArrows(CallbackInfo ci) {
        ArrowEntity arrow = (ArrowEntity) (Object) this;

        if (arrow.getEntityWorld().isClient()) return;

        PotionContentsComponent potionContents =
                arrow.getItemStack().get(DataComponentTypes.POTION_CONTENTS);

        // Iterable -> use iterator().hasNext()
        if (potionContents != null && potionContents.getEffects().iterator().hasNext()) {
            arrow.discard();
            ci.cancel();
        }
    }
}
