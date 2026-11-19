package dev.modding.myenchantadded.mixin;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.util.OwnedInventoryList;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents direct NonNullList#set tampering (e.g., armor stripping) on server-side player inventory lists.
 */
@Mixin(NonNullList.class)
public abstract class NonNullListMixin<E> implements OwnedInventoryList {

    @Unique
    private Player myEnchantAdded$owner;

    @Shadow
    public abstract E get(int index);

    @Override
    public void myEnchantAdded$setOwner(Player player) {
        this.myEnchantAdded$owner = player;
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "set(ILjava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$preventOverwriteLocked(int index, E element, CallbackInfoReturnable<E> cir) {
        if (this.myEnchantAdded$owner == null
                || this.myEnchantAdded$owner.level().isClientSide()
                || !this.myEnchantAdded$owner.isAlive()) {
            return;
        }
        E currentObj = this.get(index);
        if (currentObj instanceof ItemStack current && element instanceof ItemStack newStack) {
            if (!current.isEmpty()
                    && RockEnchantment.isLocked(current)
                    && !ItemStack.isSameItemSameTags(current, newStack)) {
                cir.setReturnValue((E) current);
            }
        }
    }
}
