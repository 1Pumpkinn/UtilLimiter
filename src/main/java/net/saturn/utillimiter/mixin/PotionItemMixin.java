package net.saturn.utillimiter.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PotionItemMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
    private void blockBannedDrinkablePotions(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        // Only check for potions
        if (!stack.isOf(Items.POTION)) {
            return;
        }

        PotionContentsComponent contents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) return;

        // Check potion type
        boolean shouldBlock = contents.potion().map(entry -> {
            String id = entry.getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("");
            return id.equals("turtle_master")
                    || id.equals("long_turtle_master")
                    || id.equals("strong_turtle_master");
        }).orElse(false);

        if (shouldBlock) {
            cir.setReturnValue(stack);
            return;
        }

        // Check custom effects
        for (StatusEffectInstance effect : contents.getEffects()) {
            String effectId = effect.getEffectType().getKey()
                    .map(k -> k.getValue().getPath())
                    .orElse("");
            int amp = effect.getAmplifier();

            if ((effectId.equals("strength") && amp >= 1)
                    || (effectId.equals("speed") && amp >= 1)
                    || effectId.equals("poison")
                    || effectId.equals("instant_damage")
                    || effectId.equals("slow_falling")) {
                cir.setReturnValue(stack);
                return;
            }
        }
    }
}