/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.creativehub;

import com.hypixel.hytale.builtin.creativehub.command.HubCommand;
import com.hypixel.hytale.builtin.creativehub.config.CreativeHubEntityConfig;
import com.hypixel.hytale.builtin.creativehub.config.CreativeHubWorldConfig;
import com.hypixel.hytale.builtin.creativehub.interactions.HubPortalInteraction;
import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.builtin.instances.config.InstanceEntityConfig;
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig;
import com.hypixel.hytale.builtin.instances.config.WorldReturnPoint;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreativeHubPlugin
extends JavaPlugin {
    @Nonnull
    private static final Message MESSAGE_HUB_RETURN_HINT = Message.translation("server.creativehub.portal.returnHint");
    private static CreativeHubPlugin instance;
    private final Map<UUID, World> activeHubInstances = new ConcurrentHashMap<UUID, World>();
    private ComponentType<EntityStore, CreativeHubEntityConfig> creativeHubEntityConfigComponentType;

    public static CreativeHubPlugin get() {
        return instance;
    }

    public CreativeHubPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Nonnull
    public World getOrSpawnHubInstance(@Nonnull World parentWorld, @Nonnull CreativeHubWorldConfig hubConfig, @Nonnull Transform returnPoint) {
        UUID parentUuid = parentWorld.getWorldConfig().getUuid();
        return this.activeHubInstances.compute(parentUuid, (uuid, existingInstance) -> {
            if (existingInstance != null && existingInstance.isAlive()) {
                return existingInstance;
            }
            try {
                return InstancesPlugin.get().spawnInstance(hubConfig.getStartupInstance(), parentWorld, returnPoint).join();
            }
            catch (Exception e) {
                ((HytaleLogger.Api)this.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to spawn hub instance");
                throw new RuntimeException("Failed to spawn hub instance", e);
            }
        });
    }

    @Nullable
    public World getActiveHubInstance(@Nonnull World parentWorld) {
        World hubInstance = this.activeHubInstances.get(parentWorld.getWorldConfig().getUuid());
        if (hubInstance != null && hubInstance.isAlive()) {
            return hubInstance;
        }
        return null;
    }

    public void clearHubInstance(@Nonnull UUID parentWorldUuid) {
        this.activeHubInstances.remove(parentWorldUuid);
    }

    @Nonnull
    public CompletableFuture<World> spawnPermanentWorldFromTemplate(@Nonnull String instanceAssetName, @Nonnull String permanentWorldName) {
        Universe universe = Universe.get();
        World existingWorld = universe.getWorld(permanentWorldName);
        if (existingWorld != null) {
            return CompletableFuture.completedFuture(existingWorld);
        }
        if (universe.isWorldLoadable(permanentWorldName)) {
            return universe.loadWorld(permanentWorldName);
        }
        Path assetPath = InstancesPlugin.getInstanceAssetPath(instanceAssetName);
        Path worldPath = universe.getPath().resolve("worlds").resolve(permanentWorldName);
        return ((CompletableFuture)WorldConfig.load(assetPath.resolve("instance.bson")).thenApplyAsync(SneakyThrow.sneakyFunction(config -> {
            config.setUuid(UUID.randomUUID());
            config.setDeleteOnRemove(false);
            config.setDisplayName(WorldConfig.formatDisplayName(instanceAssetName));
            config.getPluginConfig().remove(InstanceWorldConfig.class);
            config.markChanged();
            long start = System.nanoTime();
            this.getLogger().at(Level.INFO).log("Copying instance template %s to permanent world %s", (Object)instanceAssetName, (Object)permanentWorldName);
            try (Stream<Path> files = Files.walk(assetPath, FileUtil.DEFAULT_WALK_TREE_OPTIONS_ARRAY);){
                files.forEach(SneakyThrow.sneakyConsumer(filePath -> {
                    Path rel = assetPath.relativize((Path)filePath);
                    Path toPath = worldPath.resolve(rel.toString());
                    if (Files.isDirectory(filePath, new LinkOption[0])) {
                        Files.createDirectories(toPath, new FileAttribute[0]);
                        return;
                    }
                    if (Files.isRegularFile(filePath, new LinkOption[0])) {
                        Files.copy(filePath, toPath, new CopyOption[0]);
                    }
                }));
            }
            this.getLogger().at(Level.INFO).log("Completed copying instance template %s to permanent world %s in %s", instanceAssetName, permanentWorldName, FormatUtil.nanosToString(System.nanoTime() - start));
            return config;
        }))).thenCompose(config -> universe.makeWorld(permanentWorldName, worldPath, (WorldConfig)config));
    }

    @Nonnull
    public ComponentType<EntityStore, CreativeHubEntityConfig> getCreativeHubEntityConfigComponentType() {
        return this.creativeHubEntityConfigComponentType;
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new HubCommand());
        this.getCodecRegistry(Interaction.CODEC).register("HubPortal", HubPortalInteraction.class, HubPortalInteraction.CODEC);
        this.getCodecRegistry(WorldConfig.PLUGIN_CODEC).register(CreativeHubWorldConfig.class, "CreativeHub", CreativeHubWorldConfig.CODEC);
        this.creativeHubEntityConfigComponentType = this.getEntityStoreRegistry().registerComponent(CreativeHubEntityConfig.class, "CreativeHub", CreativeHubEntityConfig.CODEC);
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, CreativeHubPlugin::onPlayerConnect);
        this.getEventRegistry().registerGlobal(RemoveWorldEvent.class, CreativeHubPlugin::onWorldRemove);
        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, CreativeHubPlugin::onPlayerAddToWorld);
    }

    private static void onWorldRemove(@Nonnull RemoveWorldEvent event) {
        World world = event.getWorld();
        UUID worldUuid = world.getWorldConfig().getUuid();
        CreativeHubPlugin.get().activeHubInstances.entrySet().removeIf(entry -> {
            World hubInstance = (World)entry.getValue();
            return hubInstance != null && hubInstance.getWorldConfig().getUuid().equals(worldUuid);
        });
    }

    private static void onPlayerConnect(@Nonnull PlayerConnectEvent event) {
        CreativeHubWorldConfig parentHubConfig;
        World parentWorld;
        World targetWorld = event.getWorld();
        Holder<EntityStore> holder = event.getHolder();
        CreativeHubEntityConfig existingHubConfig = CreativeHubEntityConfig.get(holder);
        if (existingHubConfig != null && existingHubConfig.getParentHubWorldUuid() != null && (parentWorld = Universe.get().getWorld(existingHubConfig.getParentHubWorldUuid())) != null && (parentHubConfig = CreativeHubWorldConfig.get(parentWorld.getWorldConfig())) != null && parentHubConfig.getStartupInstance() != null && targetWorld == null) {
            event.setWorld(parentWorld);
            targetWorld = parentWorld;
            holder.removeComponent(TransformComponent.getComponentType());
        }
        if (targetWorld == null) {
            return;
        }
        WorldConfig worldConfig = targetWorld.getWorldConfig();
        CreativeHubWorldConfig hubConfig = CreativeHubWorldConfig.get(worldConfig);
        if (hubConfig == null || hubConfig.getStartupInstance() == null) {
            return;
        }
        PlayerRef playerRef = event.getPlayerRef();
        ISpawnProvider spawnProvider = worldConfig.getSpawnProvider();
        Transform returnPoint = spawnProvider != null ? spawnProvider.getSpawnPoint(targetWorld, playerRef.getUuid()) : new Transform();
        try {
            World hubInstance = CreativeHubPlugin.get().getOrSpawnHubInstance(targetWorld, hubConfig, returnPoint);
            InstanceEntityConfig instanceConfig = InstanceEntityConfig.ensureAndGet(holder);
            instanceConfig.setReturnPoint(new WorldReturnPoint(targetWorld.getWorldConfig().getUuid(), returnPoint, false));
            CreativeHubEntityConfig hubEntityConfig = CreativeHubEntityConfig.ensureAndGet(holder);
            hubEntityConfig.setParentHubWorldUuid(targetWorld.getWorldConfig().getUuid());
            event.setWorld(hubInstance);
        }
        catch (Exception e) {
            ((HytaleLogger.Api)CreativeHubPlugin.get().getLogger().at(Level.SEVERE).withCause(e)).log("Failed to get/spawn hub instance for player %s, falling back to default world", playerRef.getUuid());
        }
    }

    private static void onPlayerAddToWorld(@Nonnull AddPlayerToWorldEvent event) {
        Holder<EntityStore> holder = event.getHolder();
        World world = event.getWorld();
        CreativeHubEntityConfig hubEntityConfig = holder.getComponent(CreativeHubEntityConfig.getComponentType());
        if (hubEntityConfig == null || hubEntityConfig.getParentHubWorldUuid() == null) {
            return;
        }
        World parentWorld = Universe.get().getWorld(hubEntityConfig.getParentHubWorldUuid());
        if (parentWorld == null) {
            return;
        }
        World hubInstance = CreativeHubPlugin.get().getActiveHubInstance(parentWorld);
        if (hubInstance != null && world.equals(hubInstance)) {
            return;
        }
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (playerRef != null) {
            world.execute(() -> playerRef.sendMessage(MESSAGE_HUB_RETURN_HINT));
        }
    }
}

