package dev.modding.myenchantadded.event;

import dev.modding.myenchantadded.MyEnchantAddedMod;
import dev.modding.myenchantadded.enchantment.RockEnchantment;
import dev.modding.myenchantadded.network.ModNetwork;
import dev.modding.myenchantadded.network.ToggleRockLockPacket;
import dev.modding.myenchantadded.registry.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side event handlers for Rock enchantment lock toggling, slot protection, and tooltips.
 */
@Mod.EventBusSubscriber(modid = MyEnchantAddedMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientEvents {

    private ClientEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Slot hoveredSlot = containerScreen.getSlotUnderMouse();
        if (hoveredSlot == null || hoveredSlot.container != player.getInventory()) {
            return;
        }

        ItemStack slotStack = hoveredSlot.getItem();
        if (slotStack.isEmpty()) {
            return;
        }

        // Right-click with empty cursor on a Rock-enchanted item in player inventory toggles lock
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT
                && containerScreen.getMenu().getCarried().isEmpty()
                && slotStack.getEnchantmentLevel(ModEnchantments.ROCK.get()) > 0) {
            ModNetwork.CHANNEL.sendToServer(new ToggleRockLockPacket(containerScreen.getMenu().containerId, hoveredSlot.index));
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.4F, 1.2F);
            event.setCanceled(true);
            return;
        }

        // Block any other click interaction on a locked item
        if (RockEnchantment.isLocked(slotStack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

        Slot hoveredSlot = containerScreen.getSlotUnderMouse();
        if (hoveredSlot != null && hoveredSlot.container == player.getInventory() && RockEnchantment.isLocked(hoveredSlot.getItem())) {
            if (mc.options.keyDrop.matches(event.getKeyCode(), event.getScanCode())
                    || mc.options.keySwapOffhand.matches(event.getKeyCode(), event.getScanCode())) {
                event.setCanceled(true);
                return;
            }
            for (var hotbarKey : mc.options.keyHotbarSlots) {
                if (hotbarKey.matches(event.getKeyCode(), event.getScanCode())) {
                    event.setCanceled(true);
                    return;
                }
            }
        }

        // Also prevent swapping a locked hotbar/offhand item into the hovered slot
        for (int i = 0; i < mc.options.keyHotbarSlots.length; i++) {
            if (mc.options.keyHotbarSlots[i].matches(event.getKeyCode(), event.getScanCode())) {
                if (RockEnchantment.isLocked(player.getInventory().getItem(i))) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
        if (mc.options.keySwapOffhand.matches(event.getKeyCode(), event.getScanCode())) {
            if (RockEnchantment.isLocked(player.getOffhandItem())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getEnchantmentLevel(ModEnchantments.ROCK.get()) <= 0) {
            return;
        }
        if (RockEnchantment.isLocked(stack)) {
            event.getToolTip().add(Component.translatable("tooltip.my_enchant_added.rock.locked").withStyle(ChatFormatting.RED));
        } else {
            event.getToolTip().add(Component.translatable("tooltip.my_enchant_added.rock.unlocked").withStyle(ChatFormatting.GRAY));
        }
    }
}
