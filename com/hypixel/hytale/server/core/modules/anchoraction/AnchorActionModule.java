/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.anchoraction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.anchoraction.AnchorActionHandler;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;

public class AnchorActionModule
extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(AnchorActionModule.class).build();
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static AnchorActionModule instance;
    private final Map<String, AnchorActionHandler> handlers = new ConcurrentHashMap<String, AnchorActionHandler>();

    public static AnchorActionModule get() {
        return instance;
    }

    public AnchorActionModule(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public void register(@Nonnull String action, @Nonnull AnchorActionHandler handler) {
        this.handlers.put(action, handler);
    }

    public void register(@Nonnull String action, @Nonnull WorldThreadAnchorActionHandler handler) {
        this.register(action, (PlayerRef playerRef, JsonObject data) -> {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null) {
                return;
            }
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                if (!ref.isValid()) {
                    return;
                }
                handler.handle(playerRef, ref, store, data);
            });
        });
    }

    public void unregister(@Nonnull String action) {
        this.handlers.remove(action);
    }

    public boolean tryHandle(@Nonnull PlayerRef playerRef, @Nonnull String rawData) {
        String action = null;
        try {
            JsonObject data = JsonParser.parseString(rawData).getAsJsonObject();
            if (!data.has("action")) {
                return false;
            }
            action = data.get("action").getAsString();
            AnchorActionHandler handler = this.handlers.get(action);
            if (handler == null) {
                return false;
            }
            handler.handle(playerRef, data);
            return true;
        }
        catch (Exception e) {
            ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atWarning()).withCause(e)).log("Failed to handle anchor action '%s' for player %s", (Object)action, (Object)playerRef.getUuid());
            return false;
        }
    }

    @FunctionalInterface
    public static interface WorldThreadAnchorActionHandler {
        public void handle(@Nonnull PlayerRef var1, @Nonnull Ref<EntityStore> var2, @Nonnull Store<EntityStore> var3, @Nonnull JsonObject var4);
    }
}

