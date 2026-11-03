package com.narutoclans.addon.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/** Teclas das habilidades Otsutsuki (podem ser alteradas em Controles). */
public final class ModKeys {
    public static final String CATEGORY = "key.categories.narutoto_clans";

    public static final KeyMapping DIMENSIONAL_SHIFT = new KeyMapping(
            "key.narutoto_clans.dimensional_shift", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);

    public static final KeyMapping CYCLE_DOJUTSU = new KeyMapping(
            "key.narutoto_clans.cycle_dojutsu", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);

    private ModKeys() {
    }
}
