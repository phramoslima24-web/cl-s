package com.narutoclans.addon.clan;

import com.narutoclans.addon.NarutoClansAddon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

/** Clas adicionados por este addon. O cla de um jogador e representado por uma advancement (como no Narutoto). */
public enum Clan {
    NAMIKAZE("namikaze"),
    OTSUTSUKI("otsutsuki");

    public final String id;
    private final ResourceLocation advancement;

    Clan(String id) {
        this.id = id;
        this.advancement = new ResourceLocation(NarutoClansAddon.MODID, id + "_clan");
    }

    public ResourceLocation advancement() {
        return advancement;
    }

    public Component displayName() {
        return Component.translatable("clan." + NarutoClansAddon.MODID + "." + id);
    }

    public static Clan byId(String id) {
        String lower = id.toLowerCase(Locale.ROOT);
        for (Clan c : values()) {
            if (c.id.equals(lower)) {
                return c;
            }
        }
        return null;
    }
}
