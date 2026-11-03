package com.narutoclans.addon.registry;

import com.narutoclans.addon.NarutoClansAddon;
import com.narutoclans.addon.clan.Clan;
import com.narutoclans.addon.item.ChakraFruitItem;
import com.narutoclans.addon.item.ClanScrollItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NarutoClansAddon.MODID);

    public static final RegistryObject<Item> NAMIKAZE_CLAN_SCROLL = ITEMS.register("namikaze_clan_scroll",
            () -> new ClanScrollItem(Clan.NAMIKAZE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> OTSUTSUKI_CLAN_SCROLL = ITEMS.register("otsutsuki_clan_scroll",
            () -> new ClanScrollItem(Clan.OTSUTSUKI, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> SENJU_CLAN_SCROLL = ITEMS.register("senju_clan_scroll",
            () -> new ClanScrollItem(Clan.SENJU, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> CHAKRA_FRUIT = ITEMS.register("chakra_fruit",
            () -> new ChakraFruitItem(new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)
                    .food(new FoodProperties.Builder().nutrition(10).saturationMod(1.2F).alwaysEat().build())));

    private ModItems() {
    }
}
