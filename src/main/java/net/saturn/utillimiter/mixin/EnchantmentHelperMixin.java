package net.saturn.utillimiter.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getEnchantments", at = @At("RETURN"), cancellable = true)
    private static void limitProtectionLevel(ItemStack stack, CallbackInfoReturnable<ItemEnchantmentsComponent> cir) {
        ItemEnchantmentsComponent enchantments = cir.getReturnValue();
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(enchantments);
        boolean modified = false;

        for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
            if (enchantment.getKey().isPresent() &&
                    enchantment.getKey().get().getValue().getPath().equals("protection")) {
                int level = enchantments.getLevel(enchantment);
                if (level > 3) {
                    builder.set(enchantment, 3);
                    modified = true;
                }
            }
        }

        if (modified) {
            cir.setReturnValue(builder.build());
        }
    }
}