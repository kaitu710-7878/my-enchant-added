package dev.modding.myenchantadded.network;

import dev.modding.myenchantadded.MyEnchantAddedMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Handles network channel registration for client-to-server synchronization.
 */
public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MyEnchantAddedMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(ToggleRockLockPacket.class, id++)
                .encoder(ToggleRockLockPacket::encode)
                .decoder(ToggleRockLockPacket::decode)
                .consumerMainThread(ToggleRockLockPacket::handle)
                .add();
    }
}
