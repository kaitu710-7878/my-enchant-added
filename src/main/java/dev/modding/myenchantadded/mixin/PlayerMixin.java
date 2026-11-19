package dev.modding.myenchantadded.mixin;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents dropping or stripping Rock-locked items via Player#drop and Player#setItemSlot.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void myEnchantAdded$preventDropLockedItem(ItemStack stack, boolean dropAround, boolean includeThrowerName, CallbackInfoReturnable<ItemEntity> cir) {
        if (RockEnchantment.isLocked(stack)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventReplaceLockedSlot(EquipmentSlot slot, ItemStack newStack, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (self.level().isClientSide()) {
            return;
        }
        ItemStack current = self.getItemBySlot(slot);
        if (RockEnchantment.isLocked(current) && !ItemStack.isSameItemSameTags(current, newStack)) {
            ci.cancel();
        }
    }
}
