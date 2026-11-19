package dev.modding.myenchantadded.util;

import net.minecraft.world.entity.player.Player;

/**
 * Duck interface applied to NonNullList via Mixin to associate player inventory lists with their owner.
 */
public interface OwnedInventoryList {
    void myEnchantAdded$setOwner(Player player);
}
