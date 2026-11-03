package com.narutoclans.addon.registry;

import com.narutoclans.addon.NarutoClansAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NarutoClansAddon.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + NarutoClansAddon.MODID + ".main"))
                    .icon(() -> new ItemStack(ModItems.OTSUTSUKI_CLAN_SCROLL.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.NAMIKAZE_CLAN_SCROLL.get());
                        output.accept(ModItems.OTSUTSUKI_CLAN_SCROLL.get());
                        output.accept(ModItems.CHAKRA_FRUIT.get());
                    })
                    .build());

    private ModTabs() {
    }
}
