package net.saturn.utillimiter.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @ModifyVariable(method = "updateResult", at = @At(value = "STORE"), ordinal = 0)
    private ItemStack limitProtectionOnAnvil(ItemStack result) {
        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(result);
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
            EnchantmentHelper.set(result, builder.build());
        }

        return result;
    }
}