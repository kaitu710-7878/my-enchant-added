package dev.modding.myenchantadded.event;

import dev.modding.myenchantadded.MyEnchantAddedMod;
import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.registry.ModEnchantments;
import dev.modding.myenchantadded.util.EnchantmentMath;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    private static final Map<UUID, Map<Integer, ItemStack>> LOCKED_SNAPSHOTS = new ConcurrentHashMap<>();

    private CommonEvents() {
    }

    public static void setLockedSnapshot(Player player, int slotIndex, ItemStack stack) {
        LOCKED_SNAPSHOTS
                .computeIfAbsent(player.getUUID(), id -> new HashMap<>())
                .put(slotIndex, stack);
    }

    public static void clearLockedSnapshot(Player player, int slotIndex) {
        Map<Integer, ItemStack> snapshots = LOCKED_SNAPSHOTS.get(player.getUUID());
        if (snapshots != null) {
            snapshots.remove(slotIndex);
        }
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onWeaponAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || event.getSlotType() != EquipmentSlot.MAINHAND) {
            return;
        }

        int ratioSharpnessLevel = stack.getEnchantmentLevel(ModEnchantments.RATIO_SHARPNESS.get());
        if (ratioSharpnessLevel <= 0) {
            return;
        }

        double weaponAttackAddition = 0.0D;
        for (AttributeModifier modifier : event.getModifiers().get(Attributes.ATTACK_DAMAGE)) {
            if (modifier.getOperation() == AttributeModifier.Operation.ADDITION
                    && !RATIO_SHARPNESS_UUID.equals(modifier.getId())) {
                weaponAttackAddition += modifier.getAmount();
            }
        }

        double bonus = EnchantmentMath.computeRatioSharpnessBonus(weaponAttackAddition, ratioSharpnessLevel);
        if (bonus > 0.0D) {
            event.addModifier(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                            RATIO_SHARPNESS_UUID,
                            "MyEnchantAdded Ratio Sharpness",
                            bonus,
                            AttributeModifier.Operation.ADDITION
                    )
            );
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
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) {
            return;
        }
        Player player = event.player;
        Inventory inventory = player.getInventory();

        // Protect Rock-locked items from direct NonNullList/slot tampering by other mods
        if (player.isAlive()) {
            enforceLockedSnapshots(player, inventory);
        }

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

    private static void enforceLockedSnapshots(Player player, Inventory inventory) {
        Map<Integer, ItemStack> snapshots = LOCKED_SNAPSHOTS.computeIfAbsent(player.getUUID(), id -> new HashMap<>());
        int size = inventory.getContainerSize();
        boolean restoredAny = false;

        for (Map.Entry<Integer, ItemStack> entry : new HashMap<>(snapshots).entrySet()) {
            int slotIndex = entry.getKey();
            ItemStack expected = entry.getValue();

            // If the item broke naturally (count reached 0 on the same instance), stop tracking it
            if (expected.isEmpty() || !RockEnchantment.isLocked(expected)) {
                snapshots.remove(slotIndex);
                continue;
            }

            ItemStack actual = inventory.getItem(slotIndex);
            if (actual == expected) {
                continue;
            }
            if (!actual.isEmpty() && ItemStack.isSameItemSameTags(actual, expected) && actual.getCount() == expected.getCount()) {
                snapshots.put(slotIndex, actual);
                continue;
            }

            // Item was forcibly removed or moved from its locked slot; remove any moved copy and restore
            for (int other = 0; other < size; other++) {
                if (other == slotIndex || snapshots.containsKey(other)) {
                    continue;
                }
                ItemStack candidate = inventory.getItem(other);
                if (!candidate.isEmpty() && ItemStack.isSameItemSameTags(candidate, expected) && RockEnchantment.isLocked(candidate)) {
                    setInventorySlotDirect(inventory, other, ItemStack.EMPTY);
                    break;
                }
            }

            setInventorySlotDirect(inventory, slotIndex, expected);
            restoredAny = true;
        }

        // Refresh snapshots for all currently locked slots
        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && RockEnchantment.isLocked(stack)) {
                snapshots.put(i, stack);
            } else {
                snapshots.remove(i);
            }
        }

        if (restoredAny) {
            inventory.setChanged();
            player.inventoryMenu.broadcastChanges();
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
                serverPlayer.containerMenu.broadcastChanges();
            }
        }
    }

    private static void setInventorySlotDirect(Inventory inventory, int slotIndex, ItemStack stack) {
        if (slotIndex < inventory.items.size()) {
            inventory.items.set(slotIndex, stack);
            return;
        }
        int armorIdx = slotIndex - inventory.items.size();
        if (armorIdx < inventory.armor.size()) {
            inventory.armor.set(armorIdx, stack);
            return;
        }
        int offhandIdx = armorIdx - inventory.armor.size();
        if (offhandIdx < inventory.offhand.size()) {
            inventory.offhand.set(offhandIdx, stack);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        REPAIR_ACCUMULATORS.remove(uuid);
        LOCKED_SNAPSHOTS.remove(uuid);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            LOCKED_SNAPSHOTS.remove(player.getUUID());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && RockEnchantment.isLocked(stack)) {
            if (stack.getItem() instanceof Equipable || Mob.getEquipmentSlotForItem(stack).getType() == EquipmentSlot.Type.ARMOR) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
            }
        }
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ItemEntity itemEntity) {
            if (RockEnchantment.isLocked(itemEntity.getItem())) {
                event.setCanceled(true);
            }
        }
    }
}
