package dev.modding.myenchantadded.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Automatic Repair enchantment: repairs 0.01% of maximum durability every tick.
 * Applicable to any damageable tool, armor, or weapon.
 */
public class AutomaticRepairEnchantment extends Enchantment {
    public static final double REPAIR_RATIO_PER_TICK = 0.0001D;

    public AutomaticRepairEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinCost(int level) {
        return 20;
    }

    @Override
    public int getMaxCost(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    private boolean isRepairable(ItemStack stack) {
        return !stack.isEmpty() && (stack.isDamageableItem() || this.category.canEnchant(stack.getItem()));
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return isRepairable(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return isRepairable(stack);
    }

    /**
     * Computes the next fractional durability accumulator and integer repair amount.
     *
     * @param currentAccumulator accumulated fractional repair points in [0, 1)
     * @param maxDamage          item's maximum durability
     * @return double[]{integerRepair, nextAccumulator}
     */
    public static double[] stepRepairAccumulator(double currentAccumulator, int maxDamage) {
        if (maxDamage <= 0) {
            return new double[]{0.0D, 0.0D};
        }
        double total = currentAccumulator + (maxDamage * REPAIR_RATIO_PER_TICK);
        int wholeRepair = (int) Math.floor(total);
        double remainder = total - wholeRepair;
        return new double[]{wholeRepair, remainder};
    }
}
