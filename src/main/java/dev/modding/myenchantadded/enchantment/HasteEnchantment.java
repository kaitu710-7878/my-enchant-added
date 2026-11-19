package dev.modding.myenchantadded.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Haste enchantment: increases movement speed attribute by 7.5% per level on boots.
 * No incompatibilities with any other enchantments.
 */
public class HasteEnchantment extends Enchantment {
    public static final double SPEED_MULTIPLIER_PER_LEVEL = 0.075D;

    public HasteEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 12;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
