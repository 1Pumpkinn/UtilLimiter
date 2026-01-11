package net.saturn.utillimiter.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentTableMixin {

    @Inject(method = "onContentChanged", at = @At("RETURN"))
    private void limitProtectionFromTable(CallbackInfo ci) {
        EnchantmentScreenHandler handler = (EnchantmentScreenHandler) (Object) this;

        // Get the item being enchanted (slot 0)
        ItemStack stack = handler.getSlot(0).getStack();
        if (stack.isEmpty()) return;

        ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);
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
            EnchantmentHelper.set(stack, builder.build());
        }
    }
}