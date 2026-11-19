package dev.modding.myenchantadded.enchantment;

import dev.modding.myenchantadded.registry.ModEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Rock enchantment: allows players to lock/unlock an item in their inventory by right-clicking it.
 * Locked items cannot be dropped, moved between slots, or force-dropped by other abilities.
 */
public class RockEnchantment extends Enchantment {
    public static final String NBT_LOCKED_KEY = "my_enchant_added_rock_locked";

    public static final EnchantmentCategory ALL_ENCHANTABLE = EnchantmentCategory.create(
            "my_enchant_added_all_enchantable",
            item -> item.getEnchantmentValue() > 0
    );

    public RockEnchantment() {
        super(Rarity.RARE, ALL_ENCHANTABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinCost(int level) {
        return 15;
    }

    @Override
    public int getMaxCost(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getEnchantmentValue() > 0 || stack.getItem().isEnchantable(stack) || stack.isEnchanted();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return !stack.isEmpty() && (stack.getEnchantmentValue() > 0 || stack.getItem().isEnchantable(stack));
    }

    /**
     * Checks whether the given ItemStack has the Rock enchantment and is currently locked.
     */
    public static boolean isLocked(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return false;
        }
        if (stack.getEnchantmentLevel(ModEnchantments.ROCK.get()) <= 0) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(NBT_LOCKED_KEY);
    }

    /**
     * Toggles the lock state of an ItemStack if it has the Rock enchantment.
     *
     * @return the new lock state, or false if the item does not have Rock.
     */
    public static boolean toggleLock(ItemStack stack) {
        if (stack.isEmpty() || stack.getEnchantmentLevel(ModEnchantments.ROCK.get()) <= 0) {
            return false;
        }
        boolean currentlyLocked = isLocked(stack);
        if (currentlyLocked) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(NBT_LOCKED_KEY);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
            return false;
        } else {
            stack.getOrCreateTag().putBoolean(NBT_LOCKED_KEY, true);
            return true;
        }
    }
}
