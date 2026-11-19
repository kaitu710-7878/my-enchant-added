package dev.modding.myenchantadded.mixin;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents moving or swapping Rock-locked items inside any container menu on both client and server.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventLockedSlotClick(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.container == player.getInventory() && RockEnchantment.isLocked(slot.getItem())) {
                ci.cancel();
                return;
            }
        }

        if (clickType == ClickType.SWAP && button >= 0) {
            if (button < 9 && RockEnchantment.isLocked(player.getInventory().getItem(button))) {
                ci.cancel();
                return;
            }
            if (button == 40 && RockEnchantment.isLocked(player.getOffhandItem())) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "canTakeItemForPickAll", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventPickAllFromLocked(ItemStack stack, Slot slot, CallbackInfoReturnable<Boolean> cir) {
        if (RockEnchantment.isLocked(slot.getItem())) {
            cir.setReturnValue(false);
        }
    }
}
