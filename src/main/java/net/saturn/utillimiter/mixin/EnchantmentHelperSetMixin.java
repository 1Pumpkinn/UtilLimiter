package net.saturn.utillimiter.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperSetMixin {

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private static void limitProtectionOnSet(ItemStack stack, ItemEnchantmentsComponent enchantments, CallbackInfo ci) {
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
            stack.set(net.minecraft.component.DataComponentTypes.ENCHANTMENTS, builder.build());
            ci.cancel();
        }
    }
}