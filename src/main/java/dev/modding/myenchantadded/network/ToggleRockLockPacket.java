package dev.modding.myenchantadded.network;

import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.registry.ModEnchantments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when right-clicking a Rock-enchanted item in the player's inventory.
 */
public record ToggleRockLockPacket(int containerId, int slotIndex) {

    public static void encode(ToggleRockLockPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.containerId);
        buf.writeVarInt(msg.slotIndex);
    }

    public static ToggleRockLockPacket decode(FriendlyByteBuf buf) {
        return new ToggleRockLockPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(ToggleRockLockPacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer player = ctx.getSender();
        if (player == null) {
            ctx.setPacketHandled(true);
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != msg.containerId) {
            ctx.setPacketHandled(true);
            return;
        }

        if (msg.slotIndex < 0 || msg.slotIndex >= menu.slots.size()) {
            ctx.setPacketHandled(true);
            return;
        }

        Slot slot = menu.getSlot(msg.slotIndex);
        if (slot.container != player.getInventory()) {
            ctx.setPacketHandled(true);
            return;
        }

        ItemStack stack = slot.getItem();
        if (!stack.isEmpty() && stack.getEnchantmentLevel(ModEnchantments.ROCK.get()) > 0) {
            RockEnchantment.toggleLock(stack);
            slot.setChanged();
            menu.broadcastChanges();
        }
        ctx.setPacketHandled(true);
    }
}
