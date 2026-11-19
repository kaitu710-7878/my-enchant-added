package dev.modding.myenchantadded.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Armor enchantment: increases armor attribute by 0.4 per level when equipped.
 * No incompatibilities with any other enchantments.
 */
public class ArmorEnchantment extends Enchantment {
    public static final double ARMOR_PER_LEVEL = 0.4D;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, ARMOR_SLOTS);
    }

    @Override
    public int getMinCost(int level) {
        return 5 + (level - 1) * 9;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 18;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
