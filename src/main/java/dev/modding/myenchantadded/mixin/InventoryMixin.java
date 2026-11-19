package dev.modding.myenchantadded.mixin;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents removing Rock-locked items from the selected hotbar slot when pressing the drop key.
 */
@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @Shadow
    public abstract ItemStack getSelected();

    @Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventRemoveLockedSelected(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        if (RockEnchantment.isLocked(this.getSelected())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
