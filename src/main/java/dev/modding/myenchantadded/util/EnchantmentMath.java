package dev.modding.myenchantadded.util;

/**
 * Pure math helpers and constants for My Enchant Added enchantments.
 * Kept independent of Minecraft classes for clean unit testing.
 */
public final class EnchantmentMath {
    public static final float SIMPLE_PROTECTION_REDUCTION_PER_LEVEL = 0.01F;
    public static final double ARMOR_PER_LEVEL = 0.4D;
    public static final double HARDNESS_TOUGHNESS_PER_LEVEL = 0.3D;
    public static final double HASTE_SPEED_MULTIPLIER_PER_LEVEL = 0.075D;
    public static final double RATIO_SHARPNESS_DAMAGE_MULTIPLIER_PER_LEVEL = 0.04D;
    public static final double AUTOMATIC_REPAIR_RATIO_PER_TICK = 0.0001D;

    private EnchantmentMath() {
    }

    /**
     * Applies flat post-mitigation damage reduction for Simple Protection, clamped at 0.
     */
    public static float applySimpleProtectionReduction(float incomingDamage, int totalLevel) {
        if (incomingDamage <= 0.0F || totalLevel <= 0) {
            return incomingDamage;
        }
        return Math.max(0.0F, incomingDamage - (totalLevel * SIMPLE_PROTECTION_REDUCTION_PER_LEVEL));
    }

    /**
     * Steps the fractional durability repair accumulator by one tick (0.01% of maxDamage).
     *
     * @param currentAccumulator accumulated fractional repair points in [0, 1)
     * @param maxDamage          item's maximum durability
     * @return double[]{integerRepair, nextAccumulator}
     */
    public static double[] stepAutomaticRepair(double currentAccumulator, int maxDamage) {
        if (maxDamage <= 0) {
            return new double[]{0.0D, 0.0D};
        }
        double total = currentAccumulator + (maxDamage * AUTOMATIC_REPAIR_RATIO_PER_TICK);
        int wholeRepair = (int) Math.floor(total);
        double remainder = total - wholeRepair;
        return new double[]{wholeRepair, remainder};
    }
}
