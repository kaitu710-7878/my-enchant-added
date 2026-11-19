package dev.modding.myenchantadded;

import dev.modding.myenchantadded.network.ModNetwork;
import dev.modding.myenchantadded.registry.ModEnchantments;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Main entry point for My Enchant Added mod.
 */
@Mod(MyEnchantAddedMod.MOD_ID)
public class MyEnchantAddedMod {
    public static final String MOD_ID = "my_enchant_added";

    public MyEnchantAddedMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEnchantments.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
