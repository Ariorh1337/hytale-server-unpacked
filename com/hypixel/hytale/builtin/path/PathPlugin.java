/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.path;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.builtin.path.PathSpatialSystem;
import com.hypixel.hytale.builtin.path.PrefabPathSystems;
import com.hypixel.hytale.builtin.path.WorldPathBuilder;
import com.hypixel.hytale.builtin.path.WorldPathData;
import com.hypixel.hytale.builtin.path.commands.PrefabPathCommand;
import com.hypixel.hytale.builtin.path.commands.WorldPathCommand;
import com.hypixel.hytale.builtin.path.entities.PatrolPathMarkerEntity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.spatial.KDTree;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.prefab.event.PrefabPasteEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class PathPlugin
extends JavaPlugin {
    public static final KeyedCodec<String> PATH_MARKER_MODEL = new KeyedCodec<String>("PathMarkerModel", Codec.STRING);
    public static final String DEFAULT_PATH_MARKER_MODEL = "NPC_Path_Marker";
    private static PathPlugin instance;
    private ResourceType<EntityStore, WorldPathData> worldPathDataResourceType;
    private ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> prefabPathSpatialResource;
    private ComponentType<EntityStore, WorldPathBuilder> worldPathBuilderComponentType;
    private Model pathMarkerModel;

    public static PathPlugin get() {
        return instance;
    }

    public PathPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        CommandRegistry commandRegistry = this.getCommandRegistry();
        EventRegistry eventRegistry = this.getEventRegistry();
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        if (BuilderToolsPlugin.get().isEnabled()) {
            commandRegistry.registerCommand(new PrefabPathCommand());
        }
        commandRegistry.registerCommand(new WorldPathCommand());
        eventRegistry.register(LoadedAssetsEvent.class, ModelAsset.class, this::onModelsChanged);
        entityStoreRegistry.registerSystem(new PrefabPasteEventSystem());
        this.getEntityRegistry().registerEntity("PatrolPathMarker", PatrolPathMarkerEntity.class, PatrolPathMarkerEntity::new, PatrolPathMarkerEntity.CODEC);
        this.worldPathDataResourceType = entityStoreRegistry.registerResource(WorldPathData.class, WorldPathData::new);
        this.prefabPathSpatialResource = entityStoreRegistry.registerSpatialResource(() -> new KDTree<Ref>(Ref::isValid));
        this.worldPathBuilderComponentType = entityStoreRegistry.registerComponent(WorldPathBuilder.class, () -> {
            throw new UnsupportedOperationException("Not implemented");
        });
        entityStoreRegistry.registerSystem(new PrefabPathSystems.AddOrRemove());
        entityStoreRegistry.registerSystem(new PrefabPathSystems.AddedFromWorldGen());
        entityStoreRegistry.registerSystem(new PathSpatialSystem(this.prefabPathSpatialResource));
        entityStoreRegistry.registerSystem(new PrefabPathSystems.NameplateHolderSystem());
        entityStoreRegistry.registerSystem(new PrefabPathSystems.NameplateRefChangeSystem());
        entityStoreRegistry.registerSystem(new PrefabPathSystems.WorldGenChangeSystem());
        entityStoreRegistry.registerSystem(new PrefabPathSystems.PrefabPlaceEntityEventSystem());
    }

    @Override
    protected void start() {
        HytaleServerConfig.Module config = HytaleServer.get().getConfig().getModule("PathPlugin");
        String pathMarkerModelId = config.getData(PATH_MARKER_MODEL).orElse(DEFAULT_PATH_MARKER_MODEL);
        DefaultAssetMap<String, ModelAsset> modelAssetMap = ModelAsset.getAssetMap();
        ModelAsset modelAsset = modelAssetMap.getAsset(pathMarkerModelId);
        if (modelAsset == null) {
            this.getLogger().at(Level.SEVERE).log("Path marker model %s does not exist");
            modelAsset = modelAssetMap.getAsset(DEFAULT_PATH_MARKER_MODEL);
            if (modelAsset == null) {
                throw new IllegalStateException(String.format("Default path marker '%s' not found", DEFAULT_PATH_MARKER_MODEL));
            }
        }
        this.pathMarkerModel = Model.createUnitScaleModel(modelAsset);
    }

    public ResourceType<EntityStore, WorldPathData> getWorldPathDataResourceType() {
        return this.worldPathDataResourceType;
    }

    public ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> getPrefabPathSpatialResource() {
        return this.prefabPathSpatialResource;
    }

    public ComponentType<EntityStore, WorldPathBuilder> getWorldPathBuilderComponentType() {
        return this.worldPathBuilderComponentType;
    }

    public Model getPathMarkerModel() {
        return this.pathMarkerModel;
    }

    protected void onModelsChanged(@Nonnull LoadedAssetsEvent<String, ModelAsset, DefaultAssetMap<String, ModelAsset>> event) {
        if (this.pathMarkerModel == null) {
            return;
        }
        ModelAsset modelAsset = event.getLoadedAssets().get(this.pathMarkerModel.getModelAssetId());
        if (modelAsset == null) {
            return;
        }
        this.pathMarkerModel = Model.createUnitScaleModel(modelAsset);
    }

    private static class PrefabPasteEventSystem
    extends WorldEventSystem<EntityStore, PrefabPasteEvent> {
        @Nonnull
        private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency(Order.AFTER, BuilderToolsPlugin.PrefabPasteEventSystem.class, OrderPriority.CLOSEST));

        protected PrefabPasteEventSystem() {
            super(PrefabPasteEvent.class);
        }

        @Override
        public void handle(@Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull PrefabPasteEvent event) {
            if (event.isPasteStart()) {
                return;
            }
            ConcurrentHashMap<UUID, UUID> pastedPrefabPathUUIDMap = BuilderToolsPlugin.get().getPastedPrefabPathUUIDMap().get(event.getPrefabId());
            if (pastedPrefabPathUUIDMap == null) {
                return;
            }
            WorldPathData worldPathDataResource = store.getResource(WorldPathData.getResourceType());
            for (UUID value : pastedPrefabPathUUIDMap.values()) {
                worldPathDataResource.compactPrefabPath(0, value);
            }
        }

        @Override
        @Nonnull
        public Set<Dependency<EntityStore>> getDependencies() {
            return this.dependencies;
        }
    }
}

