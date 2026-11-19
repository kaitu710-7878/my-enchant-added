package dev.modding.myenchantadded.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Ratio Sharpness enchantment: increases weapon attack damage attribute by 4% per level.
 * Applicable to swords and axes. No incompatibilities with any other enchantments.
 */
public class RatioSharpnessEnchantment extends Enchantment {
    public static final double DAMAGE_MULTIPLIER_PER_LEVEL = 0.04D;

    public RatioSharpnessEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 6 + (level - 1) * 9;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    private boolean isApplicableWeapon(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || this.category.canEnchant(stack.getItem()));
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return isApplicableWeapon(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return isApplicableWeapon(stack);
    }
}
