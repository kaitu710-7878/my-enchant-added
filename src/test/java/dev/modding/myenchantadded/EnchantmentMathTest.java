package dev.modding.myenchantadded;

import dev.modding.myenchantadded.util.EnchantmentMath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnchantmentMathTest {

    @Test
    void simpleProtectionFlatReductionStacksAndClampsAtZero() {
        // Level 1 helmet + Level 2 chestplate = total level 3 -> -0.03 damage
        assertEquals(0.97F, EnchantmentMath.applySimpleProtectionReduction(1.0F, 3), 1e-6F);
        // Full set of Level 4 (total 16) -> -0.16 damage
        assertEquals(4.84F, EnchantmentMath.applySimpleProtectionReduction(5.0F, 16), 1e-6F);
        // Does not go below 0
        assertEquals(0.0F, EnchantmentMath.applySimpleProtectionReduction(0.05F, 10), 1e-6F);
        // Zero/negative input unchanged
        assertEquals(0.0F, EnchantmentMath.applySimpleProtectionReduction(0.0F, 4), 1e-6F);
        assertEquals(2.5F, EnchantmentMath.applySimpleProtectionReduction(2.5F, 0), 1e-6F);
    }

    @Test
    void automaticRepairAccumulatesFractionalDurabilityPerTick() {
        // Diamond pickaxe: maxDamage = 1561, 0.01% per tick = 0.1561
        double acc = 0.0D;
        int totalRepaired = 0;
        for (int tick = 0; tick < 10; tick++) {
            double[] res = EnchantmentMath.stepAutomaticRepair(acc, 1561);
            totalRepaired += (int) res[0];
            acc = res[1];
        }
        // 10 ticks * 0.1561 = 1.561 -> 1 whole durability repaired, 0.561 remainder
        assertEquals(1, totalRepaired);
        assertEquals(0.561D, acc, 1e-9D);
    }

    @Test
    void automaticRepairRestoresFullDurabilityIn10000Ticks() {
        double acc = 0.0D;
        int totalRepaired = 0;
        int maxDamage = 500;
        for (int tick = 0; tick < 10_000; tick++) {
            double[] res = EnchantmentMath.stepAutomaticRepair(acc, maxDamage);
            totalRepaired += (int) res[0];
            acc = res[1];
        }
        assertEquals(maxDamage, totalRepaired);
        assertEquals(0.0D, acc, 1e-9D);
    }

    @Test
    void attributeConstantsMatchSpecification() {
        assertEquals(0.4D, EnchantmentMath.ARMOR_PER_LEVEL, 1e-9D);
        assertEquals(0.3D, EnchantmentMath.HARDNESS_TOUGHNESS_PER_LEVEL, 1e-9D);
        assertEquals(0.075D, EnchantmentMath.HASTE_SPEED_MULTIPLIER_PER_LEVEL, 1e-9D);
        assertEquals(0.04D, EnchantmentMath.RATIO_SHARPNESS_DAMAGE_MULTIPLIER_PER_LEVEL, 1e-9D);
    }
}
