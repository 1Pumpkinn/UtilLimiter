package net.saturn.utillimiter.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMaceMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void clearMaceFromHands(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player.getEntityWorld().isClient()) return;

        // Check main hand
        ItemStack mainHand = player.getStackInHand(Hand.MAIN_HAND);
        if (mainHand.isOf(Items.MACE)) {
            player.getInventory().removeOne(mainHand);
        }

        // Check off hand
        ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);
        if (offHand.isOf(Items.MACE)) {
            player.getInventory().removeOne(offHand);
        }
    }
}