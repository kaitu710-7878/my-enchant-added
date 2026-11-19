package dev.modding.myenchantadded.registry;

import dev.modding.myenchantadded.MyEnchantAddedMod;
import dev.modding.myenchantadded.enchantment.ArmorEnchantment;
import dev.modding.myenchantadded.enchantment.AutomaticRepairEnchantment;
import dev.modding.myenchantadded.enchantment.HardnessEnchantment;
import dev.modding.myenchantadded.enchantment.HasteEnchantment;
import dev.modding.myenchantadded.enchantment.RatioSharpnessEnchantment;
import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.enchantment.SimpleProtectionEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * DeferredRegister holder for all enchantments added by My Enchant Added.
 */
public final class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MyEnchantAddedMod.MOD_ID);

    public static final RegistryObject<Enchantment> SIMPLE_PROTECTION =
            ENCHANTMENTS.register("simple_protection", SimpleProtectionEnchantment::new);

    public static final RegistryObject<Enchantment> ARMOR =
            ENCHANTMENTS.register("armor", ArmorEnchantment::new);

    public static final RegistryObject<Enchantment> HARDNESS =
            ENCHANTMENTS.register("hardness", HardnessEnchantment::new);

    public static final RegistryObject<Enchantment> HASTE =
            ENCHANTMENTS.register("haste", HasteEnchantment::new);

    public static final RegistryObject<Enchantment> RATIO_SHARPNESS =
            ENCHANTMENTS.register("ratio_sharpness", RatioSharpnessEnchantment::new);

    public static final RegistryObject<Enchantment> ROCK =
            ENCHANTMENTS.register("rock", RockEnchantment::new);

    public static final RegistryObject<Enchantment> AUTOMATIC_REPAIR =
            ENCHANTMENTS.register("automatic_repair", AutomaticRepairEnchantment::new);

    private ModEnchantments() {
    }

    public static void register(IEventBus modEventBus) {
        ENCHANTMENTS.register(modEventBus);
    }
}
