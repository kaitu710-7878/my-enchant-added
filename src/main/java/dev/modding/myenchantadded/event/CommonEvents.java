package dev.modding.myenchantadded.event;

import dev.modding.myenchantadded.MyEnchantAddedMod;
import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.registry.ModEnchantments;
import dev.modding.myenchantadded.util.EnchantmentMath;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server/common Forge event handlers for My Enchant Added enchantments.
 */
@Mod.EventBusSubscriber(modid = MyEnchantAddedMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CommonEvents {

    private static final UUID[] ARMOR_UUIDS = new UUID[]{
            UUID.fromString("8a3e1001-5b11-4d9a-9a01-100000000001"), // FEET
            UUID.fromString("8a3e1001-5b11-4d9a-9a01-100000000002"), // LEGS
            UUID.fromString("8a3e1001-5b11-4d9a-9a01-100000000003"), // CHEST
            UUID.fromString("8a3e1001-5b11-4d9a-9a01-100000000004")  // HEAD
    };

    private static final UUID[] HARDNESS_UUIDS = new UUID[]{
            UUID.fromString("8a3e1002-5b11-4d9a-9a01-200000000001"), // FEET
            UUID.fromString("8a3e1002-5b11-4d9a-9a01-200000000002"), // LEGS
            UUID.fromString("8a3e1002-5b11-4d9a-9a01-200000000003"), // CHEST
            UUID.fromString("8a3e1002-5b11-4d9a-9a01-200000000004")  // HEAD
    };

    private static final UUID HASTE_UUID = UUID.fromString("8a3e1003-5b11-4d9a-9a01-300000000001");
    private static final UUID RATIO_SHARPNESS_UUID = UUID.fromString("8a3e1004-5b11-4d9a-9a01-400000000001");

    private static final Map<UUID, Map<Integer, Double>> REPAIR_ACCUMULATORS = new ConcurrentHashMap<>();

    private CommonEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide() || event.getAmount() <= 0.0F) {
            return;
        }
        int totalLevel = 0;
        for (ItemStack armor : event.getEntity().getArmorSlots()) {
            if (!armor.isEmpty()) {
                totalLevel += armor.getEnchantmentLevel(ModEnchantments.SIMPLE_PROTECTION.get());
            }
        }
        if (totalLevel > 0) {
            event.setAmount(EnchantmentMath.applySimpleProtectionReduction(event.getAmount(), totalLevel));
        }
    }

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }

        EquipmentSlot eventSlot = event.getSlotType();
        EquipmentSlot naturalSlot = Mob.getEquipmentSlotForItem(stack);

        if (eventSlot == naturalSlot && naturalSlot.getType() == EquipmentSlot.Type.ARMOR) {
            int slotIndex = naturalSlot.getIndex();

            int armorLevel = stack.getEnchantmentLevel(ModEnchantments.ARMOR.get());
            if (armorLevel > 0) {
                event.addModifier(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                ARMOR_UUIDS[slotIndex],
                                "MyEnchantAdded Armor",
                                EnchantmentMath.ARMOR_PER_LEVEL * armorLevel,
                                AttributeModifier.Operation.ADDITION
                        )
                );
            }

            int hardnessLevel = stack.getEnchantmentLevel(ModEnchantments.HARDNESS.get());
            if (hardnessLevel > 0) {
                event.addModifier(
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                HARDNESS_UUIDS[slotIndex],
                                "MyEnchantAdded Hardness",
                                EnchantmentMath.HARDNESS_TOUGHNESS_PER_LEVEL * hardnessLevel,
                                AttributeModifier.Operation.ADDITION
                        )
                );
            }

            if (eventSlot == EquipmentSlot.FEET) {
                int hasteLevel = stack.getEnchantmentLevel(ModEnchantments.HASTE.get());
                if (hasteLevel > 0) {
                    event.addModifier(
                            Attributes.MOVEMENT_SPEED,
                            new AttributeModifier(
                                    HASTE_UUID,
                                    "MyEnchantAdded Haste",
                                    EnchantmentMath.HASTE_SPEED_MULTIPLIER_PER_LEVEL * hasteLevel,
                                    AttributeModifier.Operation.MULTIPLY_BASE
                            )
                    );
                }
            }
        }

        if (eventSlot == EquipmentSlot.MAINHAND) {
            int ratioSharpnessLevel = stack.getEnchantmentLevel(ModEnchantments.RATIO_SHARPNESS.get());
            if (ratioSharpnessLevel > 0) {
                event.addModifier(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                RATIO_SHARPNESS_UUID,
                                "MyEnchantAdded Ratio Sharpness",
                                EnchantmentMath.RATIO_SHARPNESS_DAMAGE_MULTIPLIER_PER_LEVEL * ratioSharpnessLevel,
                                AttributeModifier.Operation.MULTIPLY_BASE
                        )
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        Player player = event.player;
        Inventory inventory = player.getInventory();
        Map<Integer, Double> slotAccumulators = REPAIR_ACCUMULATORS.computeIfAbsent(player.getUUID(), id -> new HashMap<>());

        int size = inventory.getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()
                    || !stack.isDamageableItem()
                    || !stack.isDamaged()
                    || stack.getEnchantmentLevel(ModEnchantments.AUTOMATIC_REPAIR.get()) <= 0) {
                slotAccumulators.remove(i);
                continue;
            }

            double currentAcc = slotAccumulators.getOrDefault(i, 0.0D);
            double[] result = EnchantmentMath.stepAutomaticRepair(currentAcc, stack.getMaxDamage());
            int wholeRepair = (int) result[0];
            double nextAcc = result[1];

            if (wholeRepair >= 1) {
                int newDamage = Math.max(0, stack.getDamageValue() - wholeRepair);
                stack.setDamageValue(newDamage);
                if (newDamage == 0) {
                    slotAccumulators.remove(i);
                    continue;
                }
            }
            slotAccumulators.put(i, nextAcc);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        REPAIR_ACCUMULATORS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (RockEnchantment.isLocked(stack)) {
            event.setCanceled(true);
            Player player = event.getPlayer();
            if (!player.getInventory().add(stack)) {
                // Fallback: place in selected slot if inventory somehow became full
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }
}
