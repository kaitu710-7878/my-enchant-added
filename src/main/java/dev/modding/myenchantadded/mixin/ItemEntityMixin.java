package dev.modding.myenchantadded.mixin;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ensures Rock-locked items can never exist as dropped ItemEntities in the world.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "setItem", at = @At("TAIL"))
    private void myEnchantAdded$discardIfLockedOnSetItem(ItemStack stack, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.level().isClientSide() && RockEnchantment.isLocked(stack)) {
            self.discard();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void myEnchantAdded$discardIfLockedOnTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.level().isClientSide() && RockEnchantment.isLocked(this.getItem())) {
            self.discard();
            ci.cancel();
        }
    }
}
