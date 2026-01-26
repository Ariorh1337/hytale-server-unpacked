/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.teleport;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.teleport.Warp;
import com.hypixel.hytale.builtin.teleport.commands.teleport.SpawnCommand;
import com.hypixel.hytale.builtin.teleport.commands.teleport.TeleportCommand;
import com.hypixel.hytale.builtin.teleport.commands.warp.WarpCommand;
import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.HiddenFromAdventurePlayers;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.AllWorldsLoadedEvent;
import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.PositionUtil;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.bson.BsonArray;
import org.bson.BsonDocument;

public class TeleportPlugin
extends JavaPlugin {
    private static TeleportPlugin instance;
    public static final String WARP_MODEL_ID = "Warp";
    private ComponentType<EntityStore, TeleportHistory> teleportHistoryComponentType;
    private ComponentType<EntityStore, WarpComponent> warpComponentType;
    @Nonnull
    private final AtomicBoolean loaded = new AtomicBoolean();
    @Nonnull
    private final ReentrantLock saveLock = new ReentrantLock();
    @Nonnull
    private final AtomicBoolean postSaveRedo = new AtomicBoolean(false);
    @Nonnull
    private final Map<String, Warp> warps = new ConcurrentHashMap<String, Warp>();
    private Model warpModel;

    @Nonnull
    public static TeleportPlugin get() {
        return instance;
    }

    public TeleportPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Nonnull
    public ComponentType<EntityStore, TeleportHistory> getTeleportHistoryComponentType() {
        return this.teleportHistoryComponentType;
    }

    public boolean isWarpsLoaded() {
        return this.loaded.get();
    }

    @Override
    protected void setup() {
        instance = this;
        CommandRegistry commandRegistry = this.getCommandRegistry();
        EventRegistry eventRegistry = this.getEventRegistry();
        commandRegistry.registerCommand(new TeleportCommand());
        commandRegistry.registerCommand(new WarpCommand());
        commandRegistry.registerCommand(new SpawnCommand());
        eventRegistry.register(LoadedAssetsEvent.class, ModelAsset.class, this::onModelAssetChange);
        eventRegistry.registerGlobal(ChunkPreLoadProcessEvent.class, this::onChunkPreLoadProcess);
        eventRegistry.registerGlobal(AddWorldEvent.class, event -> event.getWorld().getWorldMapManager().addMarkerProvider("warps", WarpMarkerProvider.INSTANCE));
        eventRegistry.registerGlobal(AllWorldsLoadedEvent.class, event -> this.loadWarps());
        this.teleportHistoryComponentType = EntityStore.REGISTRY.registerComponent(TeleportHistory.class, TeleportHistory::new);
        this.warpComponentType = EntityStore.REGISTRY.registerComponent(WarpComponent.class, () -> {
            throw new UnsupportedOperationException("WarpComponent must be created manually");
        });
    }

    @Override
    protected void start() {
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(WARP_MODEL_ID);
        if (modelAsset == null) {
            throw new IllegalStateException(String.format("Default warp model '%s' not found", WARP_MODEL_ID));
        }
        this.warpModel = Model.createUnitScaleModel(modelAsset);
    }

    @Override
    protected void shutdown() {
    }

    public void loadWarps() {
        BsonDocument document = null;
        Path universePath = Universe.get().getPath();
        Path oldPath = universePath.resolve("warps.bson");
        Path path = universePath.resolve("warps.json");
        if (Files.exists(oldPath, new LinkOption[0]) && !Files.exists(path, new LinkOption[0])) {
            try {
                Files.move(oldPath, path, new CopyOption[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        if (Files.exists(path, new LinkOption[0])) {
            document = BsonUtil.readDocument(path).join();
        }
        if (document != null) {
            BsonArray bsonWarps = document.containsKey("Warps") ? document.getArray("Warps") : document.getArray("warps");
            this.warps.clear();
            for (Warp warp : (Warp[])Warp.ARRAY_CODEC.decode(bsonWarps)) {
                this.warps.put(warp.getId().toLowerCase(), warp);
            }
            this.getLogger().at(Level.INFO).log("Loaded %d warps", bsonWarps.size());
        } else {
            this.getLogger().at(Level.INFO).log("Loaded 0 warps (No warps.json found)");
        }
        this.loaded.set(true);
    }

    private void saveWarps0() {
        Warp[] array = (Warp[])this.warps.values().toArray(Warp[]::new);
        BsonDocument document = new BsonDocument("Warps", Warp.ARRAY_CODEC.encode((Warp)array));
        Path path = Universe.get().getPath().resolve("warps.json");
        BsonUtil.writeDocument(path, document).join();
        this.getLogger().at(Level.INFO).log("Saved %d warps to warps.json", array.length);
    }

    public void saveWarps() {
        if (this.saveLock.tryLock()) {
            try {
                this.saveWarps0();
            }
            catch (Throwable e) {
                ((HytaleLogger.Api)this.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to save warps:");
            }
            finally {
                this.saveLock.unlock();
            }
            if (this.postSaveRedo.getAndSet(false)) {
                this.saveWarps();
            }
        } else {
            this.postSaveRedo.set(true);
        }
    }

    public Map<String, Warp> getWarps() {
        return this.warps;
    }

    private void onModelAssetChange(@Nonnull LoadedAssetsEvent<String, ModelAsset, DefaultAssetMap<String, ModelAsset>> event) {
        Map<String, ModelAsset> modelMap = event.getLoadedAssets();
        ModelAsset modelAsset = modelMap.get(WARP_MODEL_ID);
        if (modelAsset == null) {
            return;
        }
        this.warpModel = Model.createUnitScaleModel(modelAsset);
    }

    private void onChunkPreLoadProcess(@Nonnull ChunkPreLoadProcessEvent event) {
        WorldChunk chunk = event.getChunk();
        BlockChunk blockChunk = chunk.getBlockChunk();
        if (blockChunk == null) {
            return;
        }
        int chunkX = blockChunk.getX();
        int chunkZ = blockChunk.getZ();
        World world = chunk.getWorld();
        String worldName = world.getName();
        for (Map.Entry<String, Warp> warpEntry : this.warps.entrySet()) {
            Warp warp = warpEntry.getValue();
            Transform transform = warp.getTransform();
            if (transform == null) continue;
            Vector3d position = transform.getPosition();
            if (!ChunkUtil.isInsideChunk(chunkX, chunkZ, MathUtil.floor(position.x), MathUtil.floor(position.z)) || !warp.getWorld().equals(worldName)) continue;
            world.execute(() -> {
                Store<EntityStore> store = world.getEntityStore().getStore();
                store.addEntity(this.createWarp(warp, store), AddReason.LOAD);
            });
        }
    }

    @Nonnull
    public Holder<EntityStore> createWarp(@Nonnull Warp warp, @Nonnull Store<EntityStore> store) {
        Transform transform = warp.getTransform();
        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(transform.getPosition(), transform.getRotation()));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.ensureComponent(Intangible.getComponentType());
        holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(this.warpModel.getBoundingBox()));
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(this.warpModel));
        holder.addComponent(Nameplate.getComponentType(), new Nameplate(warp.getId()));
        holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
        holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
        holder.addComponent(this.warpComponentType, new WarpComponent(warp));
        return holder;
    }

    public record WarpComponent(Warp warp) implements Component<EntityStore>
    {
        public static ComponentType<EntityStore, WarpComponent> getComponentType() {
            return TeleportPlugin.get().warpComponentType;
        }

        @Override
        @Nonnull
        public Component<EntityStore> clone() {
            return new WarpComponent(this.warp);
        }
    }

    public static class WarpMarkerProvider
    implements WorldMapManager.MarkerProvider {
        public static final WarpMarkerProvider INSTANCE = new WarpMarkerProvider();

        @Override
        public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
            Map<String, Warp> warps = TeleportPlugin.get().getWarps();
            if (warps.isEmpty()) {
                return;
            }
            GameplayConfig gameplayConfig = world.getGameplayConfig();
            if (!gameplayConfig.getWorldMapConfig().isDisplayWarps()) {
                return;
            }
            for (Warp warp : warps.values()) {
                if (!warp.getWorld().equals(world.getName())) continue;
                tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, warp.getTransform().getPosition(), warp.getTransform().getRotation().getYaw(), "Warp-" + warp.getId(), "Warp: " + warp.getId(), warp, (id, name, w) -> new MapMarker((String)id, (String)name, "Warp.png", PositionUtil.toTransformPacket(w.getTransform()), null));
            }
        }
    }
}

