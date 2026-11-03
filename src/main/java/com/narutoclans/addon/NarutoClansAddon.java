package com.narutoclans.addon;

import com.narutoclans.addon.network.ModNetwork;
import com.narutoclans.addon.registry.ModItems;
import com.narutoclans.addon.registry.ModTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Narutoto: Clans Addon - adiciona os clas Namikaze, Otsutsuki, Senju, Hatake, Hozuki e Kaguya ao mod Narutoto: Ninja World.
 */
@Mod(NarutoClansAddon.MODID)
public class NarutoClansAddon {
    public static final String MODID = "narutoto_clans";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public NarutoClansAddon() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.ITEMS.register(modBus);
        ModTabs.TABS.register(modBus);
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }
}
