package dev.modding.myenchantadded.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Hardness enchantment: increases armor toughness attribute by 0.3 per level when equipped.
 * No incompatibilities with any other enchantments.
 */
public class HardnessEnchantment extends Enchantment {
    public static final double TOUGHNESS_PER_LEVEL = 0.3D;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public HardnessEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, ARMOR_SLOTS);
    }

    @Override
    public int getMinCost(int level) {
        return 8 + (level - 1) * 9;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
