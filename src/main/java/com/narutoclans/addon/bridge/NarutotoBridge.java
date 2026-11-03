package com.narutoclans.addon.bridge;

import com.narutoclans.addon.NarutoClansAddon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Unico ponto de contato com as classes internas do Narutoto: Ninja World.
 *
 * <p>Usa reflexao de proposito: o addon compila sem o jar do Narutoto e continua funcionando
 * entre pequenas atualizacoes do mod base. Se algum campo/classe mudar, so este arquivo precisa
 * ser ajustado - e nada quebra com crash, apenas registra um aviso no log.</p>
 */
public final class NarutotoBridge {
    public static final String NARUTOTO = "narutoto";

    private static final String VARIABLES_CLASS = "net.mcreator.narutoto.network.NarutotoModVariables";
    private static final String PROCEDURE_PACKAGE = "net.mcreator.narutoto.procedures.";
    /** O Narutoto guarda os jutsus aprendidos em NBT persistente: jutsu1 .. jutsu20. */
    private static final int JUTSU_SLOTS = 20;

    private static Capability<?> capability;
    private static boolean initialised;
    private static final Map<String, Field> FIELD_CACHE = new HashMap<>();
    private static final Set<String> WARNED = new HashSet<>();
    private static Method syncMethod;

    private NarutotoBridge() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(NARUTOTO);
    }

    private static synchronized boolean init() {
        if (initialised) {
            return capability != null;
        }
        initialised = true;
        try {
            Class<?> vars = Class.forName(VARIABLES_CLASS);
            capability = (Capability<?>) vars.getField("PLAYER_VARIABLES_CAPABILITY").get(null);
        } catch (Throwable t) {
            capability = null;
            NarutoClansAddon.LOGGER.error("Nao foi possivel acessar as variaveis do Narutoto ({}). "
                    + "Os clas serao desativados.", t.toString());
        }
        return capability != null;
    }

    private static void warnOnce(String key, Throwable t) {
        if (WARNED.add(key)) {
            NarutoClansAddon.LOGGER.warn("Integracao com Narutoto falhou em '{}': {}", key, t.toString());
        }
    }

    /** Retorna as variaveis do jogador no Narutoto, ou null se indisponivel. */
    public static Vars vars(Entity entity) {
        if (entity == null || !init()) {
            return null;
        }
        Object o = entity.getCapability(capability).resolve().orElse(null);
        return o == null ? null : new Vars(o, entity);
    }

    /** Executa um "procedure" estatico execute(Entity) do Narutoto. */
    public static boolean runProcedure(String simpleName, Entity entity) {
        try {
            Class<?> c = Class.forName(PROCEDURE_PACKAGE + simpleName);
            Method m = c.getMethod("execute", Entity.class);
            m.invoke(null, entity);
            return true;
        } catch (Throwable t) {
            warnOnce("procedure:" + simpleName, t);
            return false;
        }
    }

    /** Busca um item do Narutoto pelo nome de registro; null se nao existir. */
    public static Item item(String path) {
        ResourceLocation id = new ResourceLocation(NARUTOTO, path);
        if (!ForgeRegistries.ITEMS.containsKey(id)) {
            warnOnce("item:" + path, new IllegalStateException("item nao registrado: " + id));
            return null;
        }
        return ForgeRegistries.ITEMS.getValue(id);
    }

    /**
     * Coloca um jutsu no primeiro slot livre do livro de jutsus (mesma estrutura que o Narutoto usa).
     *
     * @return true se o jutsu ja estava ou foi colocado em um slot; false se nao havia slot livre.
     */
    public static boolean bindJutsu(ServerPlayer player, String translationKey) {
        String name = Component.translatable(translationKey).getString();
        CompoundTag data = player.getPersistentData();
        for (int i = 1; i <= JUTSU_SLOTS; i++) {
            if (name.equals(data.getString("jutsu" + i))) {
                return true;
            }
        }
        for (int i = 1; i <= JUTSU_SLOTS; i++) {
            if (data.getString("jutsu" + i).isEmpty()) {
                data.putString("jutsu" + i, name);
                Vars v = vars(player);
                if (v != null && v.getString("jutsu_key" + i).isEmpty()) {
                    v.setString("jutsu_key" + i, name);
                    v.sync();
                }
                return true;
            }
        }
        return false;
    }

    /** Acesso tipado (e a prova de falhas) aos campos publicos de NarutotoModVariables.PlayerVariables. */
    public static final class Vars {
        private final Object target;
        private final Entity owner;

        private Vars(Object target, Entity owner) {
            this.target = target;
            this.owner = owner;
        }

        private Field field(String name) throws NoSuchFieldException {
            synchronized (FIELD_CACHE) {
                Field f = FIELD_CACHE.get(name);
                if (f == null) {
                    f = target.getClass().getField(name);
                    FIELD_CACHE.put(name, f);
                }
                return f;
            }
        }

        public boolean getBoolean(String name) {
            try {
                return field(name).getBoolean(target);
            } catch (Throwable t) {
                warnOnce("get:" + name, t);
                return false;
            }
        }

        public void setBoolean(String name, boolean value) {
            try {
                field(name).setBoolean(target, value);
            } catch (Throwable t) {
                warnOnce("set:" + name, t);
            }
        }

        public double getDouble(String name) {
            try {
                return field(name).getDouble(target);
            } catch (Throwable t) {
                warnOnce("get:" + name, t);
                return 0.0D;
            }
        }

        public void setDouble(String name, double value) {
            try {
                field(name).setDouble(target, value);
            } catch (Throwable t) {
                warnOnce("set:" + name, t);
            }
        }

        public void addDouble(String name, double delta) {
            setDouble(name, getDouble(name) + delta);
        }

        public String getString(String name) {
            try {
                Object o = field(name).get(target);
                return o == null ? "" : o.toString();
            } catch (Throwable t) {
                warnOnce("get:" + name, t);
                return "";
            }
        }

        public void setString(String name, String value) {
            try {
                field(name).set(target, value);
            } catch (Throwable t) {
                warnOnce("set:" + name, t);
            }
        }

        /** Envia as variaveis ao cliente (necessario apos alterar qualquer campo). */
        public void sync() {
            try {
                synchronized (FIELD_CACHE) {
                    if (syncMethod == null) {
                        syncMethod = target.getClass().getMethod("syncPlayerVariables", Entity.class);
                    }
                }
                syncMethod.invoke(target, owner);
            } catch (Throwable t) {
                warnOnce("sync", t);
            }
        }
    }
}
