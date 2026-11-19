package dev.modding.myenchantadded.network;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.event.CommonEvents;
import dev.modding.myenchantadded.registry.ModEnchantments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when right-clicking a Rock-enchanted item in the player's inventory.
 */
public record ToggleRockLockPacket(int inventorySlotIndex) {

    public static void encode(ToggleRockLockPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.inventorySlotIndex);
    }

    public static ToggleRockLockPacket decode(FriendlyByteBuf buf) {
        return new ToggleRockLockPacket(buf.readVarInt());
    }

    public static void handle(ToggleRockLockPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer player = ctx.getSender();
        if (player == null) {
            ctx.setPacketHandled(true);
            return;
        }

        Inventory inventory = player.getInventory();
        if (msg.inventorySlotIndex < 0 || msg.inventorySlotIndex >= inventory.getContainerSize()) {
            ctx.setPacketHandled(true);
            return;
        }

        ItemStack stack = inventory.getItem(msg.inventorySlotIndex);
        if (!stack.isEmpty() && stack.getEnchantmentLevel(ModEnchantments.ROCK.get()) > 0) {
            boolean nowLocked = RockEnchantment.toggleLock(stack);
            if (nowLocked) {
                CommonEvents.setLockedSnapshot(player, msg.inventorySlotIndex, stack);
            } else {
                CommonEvents.clearLockedSnapshot(player, msg.inventorySlotIndex);
            }
            inventory.setChanged();
            player.inventoryMenu.broadcastChanges();
            if (player.containerMenu != player.inventoryMenu) {
                player.containerMenu.broadcastChanges();
            }
        }
        ctx.setPacketHandled(true);
    }
}
