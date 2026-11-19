package dev.modding.myenchantadded.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Simple Protection enchantment: reduces final incoming damage by 0.01 per level per armor piece.
 * No incompatibilities with any other enchantments.
 */
public class SimpleProtectionEnchantment extends Enchantment {
    public static final float REDUCTION_PER_LEVEL = 0.01F;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public SimpleProtectionEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, ARMOR_SLOTS);
    }

    @Override
    public int getMinCost(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    /**
     * Pure calculation helper for unit testing and event logic.
     */
    public static float applyFlatReduction(float incomingDamage, int totalLevel) {
        if (incomingDamage <= 0.0F || totalLevel <= 0) {
            return incomingDamage;
        }
        return Math.max(0.0F, incomingDamage - (totalLevel * REDUCTION_PER_LEVEL));
    }
}
