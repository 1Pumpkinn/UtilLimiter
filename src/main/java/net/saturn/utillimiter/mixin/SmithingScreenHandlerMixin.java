package net.saturn.utillimiter.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void blockNetheriteArmorUpgrade(CallbackInfo ci) {
        SmithingScreenHandler handler = (SmithingScreenHandler) (Object) this;

        // Get the template, base, and addition slots
        ItemStack template = handler.getSlot(0).getStack();
        ItemStack base = handler.getSlot(1).getStack();
        ItemStack addition = handler.getSlot(2).getStack();

        // Check if trying to upgrade diamond armor to netherite
        boolean isDiamondArmor = base.isOf(Items.DIAMOND_HELMET)
                || base.isOf(Items.DIAMOND_CHESTPLATE)
                || base.isOf(Items.DIAMOND_LEGGINGS)
                || base.isOf(Items.DIAMOND_BOOTS);

        boolean isNetheriteUpgrade = addition.isOf(Items.NETHERITE_INGOT);
        boolean isNetheriteTemplate = template.isOf(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);

        // If trying to upgrade diamond armor to netherite, block it
        if (isDiamondArmor && isNetheriteUpgrade && isNetheriteTemplate) {
            // Clear the result slot
            handler.getSlot(3).setStack(ItemStack.EMPTY);
            ci.cancel();
        }
    }
}