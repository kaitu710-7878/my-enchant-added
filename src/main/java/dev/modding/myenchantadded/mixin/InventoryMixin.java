package dev.modding.myenchantadded.mixin;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.util.OwnedInventoryList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents removing or overwriting Rock-locked items in player inventory on the server,
 * and associates the player's inventory lists with their owner for direct list protection.
 */
@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @Shadow
    @Final
    public Player player;

    @Shadow
    @Final
    public NonNullList<ItemStack> items;

    @Shadow
    @Final
    public NonNullList<ItemStack> armor;

    @Shadow
    @Final
    public NonNullList<ItemStack> offhand;

    @Shadow
    public abstract ItemStack getSelected();

    @Shadow
    public abstract ItemStack getItem(int index);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void myEnchantAdded$bindInventoryLists(Player player, CallbackInfo ci) {
        ((OwnedInventoryList) (Object) this.items).myEnchantAdded$setOwner(player);
        ((OwnedInventoryList) (Object) this.armor).myEnchantAdded$setOwner(player);
        ((OwnedInventoryList) (Object) this.offhand).myEnchantAdded$setOwner(player);
    }

    @Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventRemoveLockedSelected(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        if (RockEnchantment.isLocked(this.getSelected())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventRemoveLockedItem(int index, int count, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.player.level().isClientSide() && RockEnchantment.isLocked(this.getItem(index))) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "removeItemNoUpdate", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventRemoveLockedItemNoUpdate(int index, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.player.level().isClientSide() && RockEnchantment.isLocked(this.getItem(index))) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventOverwriteLockedItem(int index, ItemStack newStack, CallbackInfo ci) {
        if (this.player.level().isClientSide()) {
            return;
        }
        ItemStack current = this.getItem(index);
        if (RockEnchantment.isLocked(current) && !ItemStack.isSameItemSameTags(current, newStack)) {
            ci.cancel();
        }
    }
}
