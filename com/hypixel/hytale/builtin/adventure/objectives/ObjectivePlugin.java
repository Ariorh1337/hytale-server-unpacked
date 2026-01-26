/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectiveDataStore;
import com.hypixel.hytale.builtin.adventure.objectives.blockstates.TreasureChestState;
import com.hypixel.hytale.builtin.adventure.objectives.commands.ObjectiveCommand;
import com.hypixel.hytale.builtin.adventure.objectives.completion.ClearObjectiveItemsCompletion;
import com.hypixel.hytale.builtin.adventure.objectives.completion.GiveItemsCompletion;
import com.hypixel.hytale.builtin.adventure.objectives.completion.ObjectiveCompletion;
import com.hypixel.hytale.builtin.adventure.objectives.components.ObjectiveHistoryComponent;
import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveLineAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.ObjectiveLocationMarkerAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.completion.ClearObjectiveItemsCompletionAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.completion.GiveItemsCompletionAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.completion.ObjectiveCompletionAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.gameplayconfig.ObjectiveGameplayConfig;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.CraftObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.GatherObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ReachLocationTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.TreasureMapObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.UseBlockObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.UseEntityObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.historydata.CommonObjectiveHistoryData;
import com.hypixel.hytale.builtin.adventure.objectives.historydata.ItemObjectiveRewardHistoryData;
import com.hypixel.hytale.builtin.adventure.objectives.historydata.ObjectiveHistoryData;
import com.hypixel.hytale.builtin.adventure.objectives.historydata.ObjectiveLineHistoryData;
import com.hypixel.hytale.builtin.adventure.objectives.historydata.ObjectiveRewardHistoryData;
import com.hypixel.hytale.builtin.adventure.objectives.interactions.CanBreakRespawnPointInteraction;
import com.hypixel.hytale.builtin.adventure.objectives.interactions.StartObjectiveInteraction;
import com.hypixel.hytale.builtin.adventure.objectives.markers.ObjectiveMarkerProvider;
import com.hypixel.hytale.builtin.adventure.objectives.markers.objectivelocation.ObjectiveLocationMarker;
import com.hypixel.hytale.builtin.adventure.objectives.markers.objectivelocation.ObjectiveLocationMarkerSystems;
import com.hypixel.hytale.builtin.adventure.objectives.markers.reachlocation.ReachLocationMarker;
import com.hypixel.hytale.builtin.adventure.objectives.markers.reachlocation.ReachLocationMarkerAsset;
import com.hypixel.hytale.builtin.adventure.objectives.markers.reachlocation.ReachLocationMarkerSystems;
import com.hypixel.hytale.builtin.adventure.objectives.systems.ObjectiveItemEntityRemovalSystem;
import com.hypixel.hytale.builtin.adventure.objectives.systems.ObjectivePlayerSetupSystem;
import com.hypixel.hytale.builtin.adventure.objectives.task.CraftObjectiveTask;
import com.hypixel.hytale.builtin.adventure.objectives.task.GatherObjectiveTask;
import com.hypixel.hytale.builtin.adventure.objectives.task.ObjectiveTask;
import com.hypixel.hytale.builtin.adventure.objectives.task.ReachLocationTask;
import com.hypixel.hytale.builtin.adventure.objectives.task.TreasureMapObjectiveTask;
import com.hypixel.hytale.builtin.adventure.objectives.task.UseBlockObjectiveTask;
import com.hypixel.hytale.builtin.adventure.objectives.task.UseEntityObjectiveTask;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.AndQuery;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.function.function.TriFunction;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.packets.assets.TrackOrUpdateObjective;
import com.hypixel.hytale.protocol.packets.assets.UntrackObjective;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityInventoryChangeEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.datastore.DataStoreProvider;
import com.hypixel.hytale.server.core.universe.datastore.DiskDataStoreProvider;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ObjectivePlugin
extends JavaPlugin {
    protected static ObjectivePlugin instance;
    public static final String OBJECTIVE_LOCATION_MARKER_MODEL_ID = "Objective_Location_Marker";
    public static final long SAVE_INTERVAL_MINUTES = 5L;
    private final Map<Class<? extends ObjectiveTaskAsset>, TriFunction<ObjectiveTaskAsset, Integer, Integer, ? extends ObjectiveTask>> taskGenerators = new ConcurrentHashMap<Class<? extends ObjectiveTaskAsset>, TriFunction<ObjectiveTaskAsset, Integer, Integer, ? extends ObjectiveTask>>();
    private final Map<Class<? extends ObjectiveCompletionAsset>, Function<ObjectiveCompletionAsset, ? extends ObjectiveCompletion>> completionGenerators = new ConcurrentHashMap<Class<? extends ObjectiveCompletionAsset>, Function<ObjectiveCompletionAsset, ? extends ObjectiveCompletion>>();
    private final Config<ObjectivePluginConfig> config = this.withConfig(ObjectivePluginConfig.CODEC);
    private Model objectiveLocationMarkerModel;
    private ComponentType<EntityStore, ObjectiveHistoryComponent> objectiveHistoryComponentType;
    private ComponentType<EntityStore, ReachLocationMarker> reachLocationMarkerComponentType;
    private ComponentType<EntityStore, ObjectiveLocationMarker> objectiveLocationMarkerComponentType;
    private ObjectiveDataStore objectiveDataStore;

    public static ObjectivePlugin get() {
        return instance;
    }

    public ObjectivePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public ComponentType<EntityStore, ObjectiveHistoryComponent> getObjectiveHistoryComponentType() {
        return this.objectiveHistoryComponentType;
    }

    public Model getObjectiveLocationMarkerModel() {
        return this.objectiveLocationMarkerModel;
    }

    public ObjectiveDataStore getObjectiveDataStore() {
        return this.objectiveDataStore;
    }

    @Override
    protected void setup() {
        instance = this;
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(ObjectiveAsset.class, new DefaultAssetMap()).setPath("Objective/Objectives")).setCodec((AssetCodec)ObjectiveAsset.CODEC)).setKeyFunction(ObjectiveAsset::getId)).loadsAfter(ItemDropList.class, Item.class, BlockType.class, ReachLocationMarkerAsset.class)).build());
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(ObjectiveLineAsset.class, new DefaultAssetMap()).setPath("Objective/ObjectiveLines")).setCodec((AssetCodec)ObjectiveLineAsset.CODEC)).setKeyFunction(ObjectiveLineAsset::getId)).loadsAfter(ObjectiveAsset.class)).loadsBefore(GameplayConfig.class)).build());
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(ObjectiveLocationMarkerAsset.class, new DefaultAssetMap()).setPath("Objective/ObjectiveLocationMarkers")).setCodec((AssetCodec)ObjectiveLocationMarkerAsset.CODEC)).setKeyFunction(ObjectiveLocationMarkerAsset::getId)).loadsAfter(ObjectiveAsset.class, Environment.class, Weather.class)).build());
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(ReachLocationMarkerAsset.class, new DefaultAssetMap()).setPath("Objective/ReachLocationMarkers")).setCodec((AssetCodec)ReachLocationMarkerAsset.CODEC)).setKeyFunction(ReachLocationMarkerAsset::getId)).build());
        this.objectiveDataStore = new ObjectiveDataStore(this.config.get().getDataStoreProvider().create(Objective.CODEC));
        this.reachLocationMarkerComponentType = this.getEntityStoreRegistry().registerComponent(ReachLocationMarker.class, "ReachLocationMarker", ReachLocationMarker.CODEC);
        this.objectiveLocationMarkerComponentType = this.getEntityStoreRegistry().registerComponent(ObjectiveLocationMarker.class, "ObjectiveLocation", ObjectiveLocationMarker.CODEC);
        this.registerTask("Craft", CraftObjectiveTaskAsset.class, CraftObjectiveTaskAsset.CODEC, CraftObjectiveTask.class, CraftObjectiveTask.CODEC, CraftObjectiveTask::new);
        this.registerTask("Gather", GatherObjectiveTaskAsset.class, GatherObjectiveTaskAsset.CODEC, GatherObjectiveTask.class, GatherObjectiveTask.CODEC, GatherObjectiveTask::new);
        this.registerTask("UseBlock", UseBlockObjectiveTaskAsset.class, UseBlockObjectiveTaskAsset.CODEC, UseBlockObjectiveTask.class, UseBlockObjectiveTask.CODEC, UseBlockObjectiveTask::new);
        this.registerTask("UseEntity", UseEntityObjectiveTaskAsset.class, UseEntityObjectiveTaskAsset.CODEC, UseEntityObjectiveTask.class, UseEntityObjectiveTask.CODEC, UseEntityObjectiveTask::new);
        this.registerTask("TreasureMap", TreasureMapObjectiveTaskAsset.class, TreasureMapObjectiveTaskAsset.CODEC, TreasureMapObjectiveTask.class, TreasureMapObjectiveTask.CODEC, TreasureMapObjectiveTask::new);
        this.registerTask("ReachLocation", ReachLocationTaskAsset.class, ReachLocationTaskAsset.CODEC, ReachLocationTask.class, ReachLocationTask.CODEC, ReachLocationTask::new);
        this.registerCompletion("GiveItems", GiveItemsCompletionAsset.class, GiveItemsCompletionAsset.CODEC, GiveItemsCompletion::new);
        this.registerCompletion("ClearObjectiveItems", ClearObjectiveItemsCompletionAsset.class, ClearObjectiveItemsCompletionAsset.CODEC, ClearObjectiveItemsCompletion::new);
        this.getEventRegistry().register(LoadedAssetsEvent.class, ObjectiveLineAsset.class, this::onObjectiveLineAssetLoaded);
        this.getEventRegistry().register(LoadedAssetsEvent.class, ObjectiveAsset.class, this::onObjectiveAssetLoaded);
        this.getEventRegistry().register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        this.getEventRegistry().register(LoadedAssetsEvent.class, ObjectiveLocationMarkerAsset.class, ObjectivePlugin::onObjectiveLocationMarkerChange);
        this.getEventRegistry().register(LoadedAssetsEvent.class, ModelAsset.class, this::onModelAssetChange);
        this.getEventRegistry().registerGlobal(LivingEntityInventoryChangeEvent.class, this::onLivingEntityInventoryChange);
        this.getEventRegistry().registerGlobal(AddWorldEvent.class, this::onWorldAdded);
        this.getCommandRegistry().registerCommand(new ObjectiveCommand());
        EntityModule entityModule = EntityModule.get();
        ComponentType<EntityStore, PlayerRef> playerRefComponentType = PlayerRef.getComponentType();
        ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent = entityModule.getPlayerSpatialResourceType();
        this.getEntityStoreRegistry().registerSystem(new ReachLocationMarkerSystems.EntityAdded(this.reachLocationMarkerComponentType));
        this.getEntityStoreRegistry().registerSystem(new ReachLocationMarkerSystems.EnsureNetworkSendable());
        this.getEntityStoreRegistry().registerSystem(new ReachLocationMarkerSystems.Ticking(this.reachLocationMarkerComponentType, playerSpatialComponent));
        this.getEntityStoreRegistry().registerSystem(new ObjectiveLocationMarkerSystems.EnsureNetworkSendableSystem());
        this.getEntityStoreRegistry().registerSystem(new ObjectiveLocationMarkerSystems.InitSystem(this.objectiveLocationMarkerComponentType));
        this.getEntityStoreRegistry().registerSystem(new ObjectiveLocationMarkerSystems.TickingSystem(this.objectiveLocationMarkerComponentType, playerRefComponentType, playerSpatialComponent));
        CommonObjectiveHistoryData.CODEC.register("Objective", (Class<CommonObjectiveHistoryData>)ObjectiveHistoryData.class, (Codec<CommonObjectiveHistoryData>)ObjectiveHistoryData.CODEC);
        CommonObjectiveHistoryData.CODEC.register("ObjectiveLine", (Class<CommonObjectiveHistoryData>)ObjectiveLineHistoryData.class, (Codec<CommonObjectiveHistoryData>)ObjectiveLineHistoryData.CODEC);
        ObjectiveRewardHistoryData.CODEC.register("Item", (Class<ObjectiveRewardHistoryData>)ItemObjectiveRewardHistoryData.class, (Codec<ObjectiveRewardHistoryData>)ItemObjectiveRewardHistoryData.CODEC);
        this.objectiveHistoryComponentType = this.getEntityStoreRegistry().registerComponent(ObjectiveHistoryComponent.class, "ObjectiveHistory", ObjectiveHistoryComponent.CODEC);
        this.getEntityStoreRegistry().registerSystem(new ObjectivePlayerSetupSystem(this.objectiveHistoryComponentType, Player.getComponentType()));
        this.getEntityStoreRegistry().registerSystem(new ObjectiveItemEntityRemovalSystem());
        this.getCodecRegistry(Interaction.CODEC).register("StartObjective", StartObjectiveInteraction.class, StartObjectiveInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("CanBreakRespawnPoint", CanBreakRespawnPointInteraction.class, CanBreakRespawnPointInteraction.CODEC);
        BlockStateModule.get().registerBlockState(TreasureChestState.class, "TreasureChest", TreasureChestState.CODEC);
        this.getCodecRegistry(GameplayConfig.PLUGIN_CODEC).register(ObjectiveGameplayConfig.class, "Objective", ObjectiveGameplayConfig.CODEC);
        this.getEntityStoreRegistry().registerSystem(new EntityModule.TangibleMigrationSystem(Query.or(ObjectiveLocationMarker.getComponentType(), ReachLocationMarker.getComponentType())), true);
        this.getEntityStoreRegistry().registerSystem(new EntityModule.HiddenFromPlayerMigrationSystem(Query.or(ObjectiveLocationMarker.getComponentType(), ReachLocationMarker.getComponentType())), true);
    }

    @Override
    protected void start() {
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(OBJECTIVE_LOCATION_MARKER_MODEL_ID);
        if (modelAsset == null) {
            throw new IllegalStateException(String.format("Default objective location marker model '%s' not found", OBJECTIVE_LOCATION_MARKER_MODEL_ID));
        }
        this.objectiveLocationMarkerModel = Model.createUnitScaleModel(modelAsset);
        HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> this.objectiveDataStore.saveToDiskAllObjectives(), 5L, 5L, TimeUnit.MINUTES);
    }

    @Override
    protected void shutdown() {
        this.objectiveDataStore.saveToDiskAllObjectives();
    }

    public ComponentType<EntityStore, ReachLocationMarker> getReachLocationMarkerComponentType() {
        return this.reachLocationMarkerComponentType;
    }

    public ComponentType<EntityStore, ObjectiveLocationMarker> getObjectiveLocationMarkerComponentType() {
        return this.objectiveLocationMarkerComponentType;
    }

    public <T extends ObjectiveTaskAsset, U extends ObjectiveTask> void registerTask(String id, Class<T> assetClass, Codec<T> assetCodec, Class<U> implementationClass, Codec<U> implementationCodec, TriFunction<T, Integer, Integer, U> generator) {
        ObjectiveTaskAsset.CODEC.register(id, assetClass, assetCodec);
        ObjectiveTask.CODEC.register(id, implementationClass, implementationCodec);
        this.taskGenerators.put(assetClass, generator);
        this.objectiveDataStore.registerTaskRef(implementationClass);
    }

    public <T extends ObjectiveCompletionAsset, U extends ObjectiveCompletion> void registerCompletion(String id, Class<T> assetClass, Codec<T> codec, Function<T, U> generator) {
        ObjectiveCompletionAsset.CODEC.register(id, assetClass, codec);
        this.completionGenerators.put(assetClass, generator);
    }

    public ObjectiveTask createTask(@Nonnull ObjectiveTaskAsset task, int taskSetIndex, int taskIndex) {
        return this.taskGenerators.get(task.getClass()).apply(task, taskSetIndex, taskIndex);
    }

    public ObjectiveCompletion createCompletion(@Nonnull ObjectiveCompletionAsset completionAsset) {
        return this.completionGenerators.get(completionAsset.getClass()).apply(completionAsset);
    }

    @Nullable
    public Objective startObjective(@Nonnull String objectiveId, @Nonnull Set<UUID> playerUUIDs, @Nonnull UUID worldUUID, @Nullable UUID markerUUID, @Nonnull Store<EntityStore> store) {
        return this.startObjective(objectiveId, null, playerUUIDs, worldUUID, markerUUID, store);
    }

    @Nullable
    public Objective startObjective(@Nonnull String objectiveId, @Nullable UUID objectiveUUID, @Nonnull Set<UUID> playerUUIDs, @Nonnull UUID worldUUID, @Nullable UUID markerUUID, @Nonnull Store<EntityStore> store) {
        ObjectiveAsset asset = ObjectiveAsset.getAssetMap().getAsset(objectiveId);
        if (asset == null) {
            this.getLogger().at(Level.WARNING).log("Failed to find objective asset '%s'", objectiveId);
            return null;
        }
        if (markerUUID == null && !asset.isValidForPlayer()) {
            this.getLogger().at(Level.WARNING).log("Objective %s can't be used for Player", asset.getId());
            return null;
        }
        Objective objective = new Objective(asset, objectiveUUID, playerUUIDs, worldUUID, markerUUID);
        boolean setupResult = objective.setup(store);
        Message assetTitleMessage = Message.translation(asset.getTitleKey());
        if (!setupResult || !this.objectiveDataStore.addObjective(objective.getObjectiveUUID(), objective)) {
            this.getLogger().at(Level.WARNING).log("Failed to start objective %s", asset.getId());
            if (objective.getPlayerUUIDs() == null) {
                return null;
            }
            objective.forEachParticipant(participantReference -> {
                PlayerRef playerRefComponent = store.getComponent((Ref<EntityStore>)participantReference, PlayerRef.getComponentType());
                if (playerRefComponent != null) {
                    playerRefComponent.sendMessage(Message.translation("server.modules.objective.start.failed").param("title", assetTitleMessage));
                }
            });
            return null;
        }
        if (objective.getPlayerUUIDs() == null) {
            return objective;
        }
        TrackOrUpdateObjective trackObjectivePacket = new TrackOrUpdateObjective(objective.toPacket());
        String objectiveAssetId = asset.getId();
        objective.forEachParticipant(participantReference -> {
            Player playerComponent = store.getComponent((Ref<EntityStore>)participantReference, Player.getComponentType());
            if (playerComponent == null) {
                return;
            }
            if (!this.canPlayerDoObjective(playerComponent, objectiveAssetId)) {
                playerComponent.sendMessage(Message.translation("server.modules.objective.playerAlreadyDoingObjective").param("title", assetTitleMessage));
                return;
            }
            PlayerRef playerRefComponent = store.getComponent((Ref<EntityStore>)participantReference, PlayerRef.getComponentType());
            assert (playerRefComponent != null);
            UUIDComponent uuidComponent = store.getComponent((Ref<EntityStore>)participantReference, UUIDComponent.getComponentType());
            assert (uuidComponent != null);
            objective.addActivePlayerUUID(uuidComponent.getUuid());
            PlayerConfigData playerConfigData = playerComponent.getPlayerConfigData();
            HashSet<UUID> activeObjectiveUUIDs = new HashSet<UUID>(playerConfigData.getActiveObjectiveUUIDs());
            activeObjectiveUUIDs.add(objective.getObjectiveUUID());
            playerConfigData.setActiveObjectiveUUIDs(activeObjectiveUUIDs);
            playerRefComponent.sendMessage(Message.translation("server.modules.objective.start.success").param("title", assetTitleMessage));
            playerRefComponent.sendMessage(objective.getTaskInfoMessage());
            playerRefComponent.getPacketHandler().writeNoCache(trackObjectivePacket);
        });
        objective.markDirty();
        return objective;
    }

    public boolean canPlayerDoObjective(@Nonnull Player player, @Nonnull String objectiveAssetId) {
        Set<UUID> activeObjectiveUUIDs = player.getPlayerConfigData().getActiveObjectiveUUIDs();
        if (activeObjectiveUUIDs == null) {
            return true;
        }
        for (UUID objectiveUUID : activeObjectiveUUIDs) {
            Objective objective = this.objectiveDataStore.getObjective(objectiveUUID);
            if (objective == null || !objective.getObjectiveId().equals(objectiveAssetId)) continue;
            return false;
        }
        return true;
    }

    @Nullable
    public Objective startObjectiveLine(@Nonnull Store<EntityStore> store, @Nonnull String objectiveLineId, @Nonnull Set<UUID> playerUUIDs, @Nonnull UUID worldUUID, @Nullable UUID markerUUID) {
        ObjectiveLineAsset objectiveLineAsset = ObjectiveLineAsset.getAssetMap().getAsset(objectiveLineId);
        if (objectiveLineAsset == null) {
            return null;
        }
        String[] objectiveIds = objectiveLineAsset.getObjectiveIds();
        if (objectiveIds == null || objectiveIds.length == 0) {
            return null;
        }
        Universe universe = Universe.get();
        HashSet<UUID> playerList = new HashSet<UUID>();
        for (UUID playerUUID : playerUUIDs) {
            Ref<EntityStore> playerReference;
            PlayerRef playerRef = universe.getPlayer(playerUUID);
            if (playerRef == null || (playerReference = playerRef.getReference()) == null || !playerReference.isValid()) continue;
            Player playerComponent = store.getComponent(playerReference, Player.getComponentType());
            assert (playerComponent != null);
            if (this.canPlayerDoObjectiveLine(playerComponent, objectiveLineId)) {
                playerList.add(playerUUID);
                continue;
            }
            Message objectiveLineIdMessage = Message.translation(objectiveLineId);
            playerRef.sendMessage(Message.translation("server.modules.objective.playerAlreadyDoingObjectiveLine").param("id", objectiveLineIdMessage));
        }
        Objective objective = this.startObjective(objectiveLineAsset.getObjectiveIds()[0], playerList, worldUUID, markerUUID, store);
        if (objective == null) {
            return null;
        }
        objective.setObjectiveLineHistoryData(new ObjectiveLineHistoryData(objectiveLineId, objectiveLineAsset.getCategory(), objectiveLineAsset.getNextObjectiveLineIds()));
        objective.checkTaskSetCompletion(store);
        return objective;
    }

    public boolean canPlayerDoObjectiveLine(@Nonnull Player player, @Nonnull String objectiveLineId) {
        Set<UUID> activeObjectiveUUIDs = player.getPlayerConfigData().getActiveObjectiveUUIDs();
        if (activeObjectiveUUIDs == null) {
            return true;
        }
        for (UUID objectiveUUID : activeObjectiveUUIDs) {
            ObjectiveLineHistoryData objectiveLineHistoryData;
            Objective objective = this.objectiveDataStore.getObjective(objectiveUUID);
            if (objective == null || (objectiveLineHistoryData = objective.getObjectiveLineHistoryData()) == null || !objectiveLineId.equals(objectiveLineHistoryData.getId())) continue;
            return false;
        }
        return true;
    }

    public void objectiveCompleted(@Nonnull Objective objective, @Nonnull Store<EntityStore> store) {
        for (UUID playerUUID : objective.getPlayerUUIDs()) {
            this.untrackObjectiveForPlayer(objective, playerUUID);
        }
        UUID objectiveUUID = objective.getObjectiveUUID();
        this.objectiveDataStore.removeObjective(objectiveUUID);
        if (!this.objectiveDataStore.removeFromDisk(objectiveUUID.toString())) {
            return;
        }
        ObjectiveLineAsset objectiveLineAsset = objective.getObjectiveLineAsset();
        if (objectiveLineAsset == null) {
            this.storeObjectiveHistoryData(objective);
            return;
        }
        ObjectiveLineHistoryData objectiveLineHistoryData = objective.getObjectiveLineHistoryData();
        assert (objectiveLineHistoryData != null);
        objectiveLineHistoryData.addObjectiveHistoryData(objective.getObjectiveHistoryData());
        String nextObjectiveId = objectiveLineAsset.getNextObjectiveId(objective.getObjectiveId());
        if (nextObjectiveId == null) {
            this.storeObjectiveLineHistoryData(objectiveLineHistoryData, objective.getPlayerUUIDs());
            String[] nextObjectiveLineIds = objectiveLineHistoryData.getNextObjectiveLineIds();
            if (nextObjectiveLineIds == null) {
                return;
            }
            for (String nextObjectiveLineId : nextObjectiveLineIds) {
                this.startObjectiveLine(store, nextObjectiveLineId, objective.getPlayerUUIDs(), objective.getWorldUUID(), objective.getMarkerUUID());
            }
            return;
        }
        Objective newObjective = this.startObjective(nextObjectiveId, objectiveUUID, objective.getPlayerUUIDs(), objective.getWorldUUID(), objective.getMarkerUUID(), store);
        if (newObjective == null) {
            return;
        }
        newObjective.setObjectiveLineHistoryData(objectiveLineHistoryData);
        newObjective.checkTaskSetCompletion(store);
    }

    public void storeObjectiveHistoryData(@Nonnull Objective objective) {
        String objectiveId = objective.getObjectiveId();
        Universe universe = Universe.get();
        for (UUID playerUUID : objective.getPlayerUUIDs()) {
            Ref<EntityStore> playerReference;
            PlayerRef playerRef = universe.getPlayer(playerUUID);
            if (playerRef == null || !playerRef.isValid() || (playerReference = playerRef.getReference()) == null || !playerReference.isValid()) continue;
            Store<EntityStore> store = playerReference.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                ObjectiveHistoryComponent objectiveHistoryComponent = store.getComponent(playerReference, this.objectiveHistoryComponentType);
                assert (objectiveHistoryComponent != null);
                Map<String, ObjectiveHistoryData> completedObjectiveDataMap = objectiveHistoryComponent.getObjectiveHistoryMap();
                ObjectiveHistoryData completedObjectiveData = completedObjectiveDataMap.get(objectiveId);
                if (completedObjectiveData != null) {
                    completedObjectiveData.completed(playerUUID, objective.getObjectiveHistoryData());
                } else {
                    completedObjectiveDataMap.put(objectiveId, objective.getObjectiveHistoryData().cloneForPlayer(playerUUID));
                }
            });
        }
    }

    public void storeObjectiveLineHistoryData(@Nonnull ObjectiveLineHistoryData objectiveLineHistoryData, @Nonnull Set<UUID> playerUUIDs) {
        Map<UUID, ObjectiveLineHistoryData> objectiveLineHistoryPerPlayerMap = objectiveLineHistoryData.cloneForPlayers(playerUUIDs);
        String objectiveLineId = objectiveLineHistoryData.getId();
        Universe universe = Universe.get();
        for (Map.Entry<UUID, ObjectiveLineHistoryData> entry : objectiveLineHistoryPerPlayerMap.entrySet()) {
            Ref<EntityStore> playerReference;
            UUID playerUUID = entry.getKey();
            PlayerRef playerRef = universe.getPlayer(playerUUID);
            if (playerRef == null || !playerRef.isValid() || (playerReference = playerRef.getReference()) == null || !playerReference.isValid()) continue;
            Store<EntityStore> store = playerReference.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                ObjectiveHistoryComponent objectiveHistoryComponent = store.getComponent(playerReference, this.objectiveHistoryComponentType);
                assert (objectiveHistoryComponent != null);
                Map<String, ObjectiveLineHistoryData> completedObjectiveLineDataMap = objectiveHistoryComponent.getObjectiveLineHistoryMap();
                ObjectiveLineHistoryData completedObjectiveLineData = completedObjectiveLineDataMap.get(objectiveLineId);
                if (completedObjectiveLineData != null) {
                    completedObjectiveLineData.completed(playerUUID, (ObjectiveLineHistoryData)entry.getValue());
                } else {
                    completedObjectiveLineDataMap.put(objectiveLineId, (ObjectiveLineHistoryData)entry.getValue());
                }
            });
        }
    }

    public void cancelObjective(@Nonnull UUID objectiveUUID, @Nonnull Store<EntityStore> store) {
        Objective objective = this.objectiveDataStore.loadObjective(objectiveUUID, store);
        if (objective == null) {
            return;
        }
        objective.cancel();
        for (UUID playerUUID : objective.getPlayerUUIDs()) {
            this.untrackObjectiveForPlayer(objective, playerUUID);
        }
        this.objectiveDataStore.removeObjective(objectiveUUID);
        this.objectiveDataStore.removeFromDisk(objectiveUUID.toString());
    }

    public void untrackObjectiveForPlayer(@Nonnull Objective objective, @Nonnull UUID playerUUID) {
        ObjectiveTask[] currentTasks;
        UUID objectiveUUID = objective.getObjectiveUUID();
        for (ObjectiveTask task : currentTasks = objective.getCurrentTasks()) {
            if (!(task instanceof UseEntityObjectiveTask)) continue;
            this.objectiveDataStore.removeEntityTaskForPlayer(objectiveUUID, ((UseEntityObjectiveTask)task).getAsset().getTaskId(), playerUUID);
        }
        PlayerRef playerRef = Universe.get().getPlayer(playerUUID);
        if (playerRef == null) {
            return;
        }
        Player player = playerRef.getComponent(Player.getComponentType());
        HashSet<UUID> activeObjectiveUUIDs = new HashSet<UUID>(player.getPlayerConfigData().getActiveObjectiveUUIDs());
        activeObjectiveUUIDs.remove(objectiveUUID);
        player.getPlayerConfigData().setActiveObjectiveUUIDs(activeObjectiveUUIDs);
        playerRef.getPacketHandler().writeNoCache(new UntrackObjective(objectiveUUID));
    }

    public void addPlayerToExistingObjective(@Nonnull Store<EntityStore> store, @Nonnull UUID playerUUID, @Nonnull UUID objectiveUUID) {
        ObjectiveTask[] currentTasks;
        Objective objective = this.objectiveDataStore.loadObjective(objectiveUUID, store);
        if (objective == null) {
            return;
        }
        objective.addActivePlayerUUID(playerUUID);
        ObjectiveDataStore objectiveDataStore = ObjectivePlugin.get().getObjectiveDataStore();
        for (ObjectiveTask task : currentTasks = objective.getCurrentTasks()) {
            if (!(task instanceof UseEntityObjectiveTask)) continue;
            objectiveDataStore.addEntityTaskForPlayer(playerUUID, ((UseEntityObjectiveTask)task).getAsset().getTaskId(), objectiveUUID);
        }
        PlayerRef playerRef = Universe.get().getPlayer(playerUUID);
        if (playerRef == null || !playerRef.isValid()) {
            return;
        }
        Ref<EntityStore> playerReference = playerRef.getReference();
        if (playerReference == null || !playerReference.isValid()) {
            return;
        }
        Player playerComponent = store.getComponent(playerReference, Player.getComponentType());
        assert (playerComponent != null);
        HashSet<UUID> activeObjectiveUUIDs = new HashSet<UUID>(playerComponent.getPlayerConfigData().getActiveObjectiveUUIDs());
        activeObjectiveUUIDs.add(objectiveUUID);
        playerComponent.getPlayerConfigData().setActiveObjectiveUUIDs(activeObjectiveUUIDs);
        playerRef.getPacketHandler().writeNoCache(new TrackOrUpdateObjective(objective.toPacket()));
    }

    public void removePlayerFromExistingObjective(@Nonnull Store<EntityStore> store, @Nonnull UUID playerUUID, @Nonnull UUID objectiveUUID) {
        Objective objective = this.objectiveDataStore.loadObjective(objectiveUUID, store);
        if (objective == null) {
            return;
        }
        objective.removeActivePlayerUUID(playerUUID);
        if (objective.getActivePlayerUUIDs().isEmpty()) {
            this.objectiveDataStore.saveToDisk(objectiveUUID.toString(), objective);
            this.objectiveDataStore.unloadObjective(objectiveUUID);
        }
        this.untrackObjectiveForPlayer(objective, playerUUID);
    }

    private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
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
            UUID playerUUID = playerRef.getUuid();
            this.getLogger().at(Level.INFO).log("Checking objectives for disconnecting player '" + playerRef.getUsername() + "' (" + String.valueOf(playerUUID) + ")");
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            if (playerComponent == null) {
                return;
            }
            Set<UUID> activeObjectiveUUIDs = playerComponent.getPlayerConfigData().getActiveObjectiveUUIDs();
            if (activeObjectiveUUIDs == null) {
                this.getLogger().at(Level.INFO).log("No active objectives found for player '" + playerRef.getUsername() + "' (" + String.valueOf(playerUUID) + ")");
                return;
            }
            this.getLogger().at(Level.INFO).log("Processing " + activeObjectiveUUIDs.size() + " active objectives for '" + playerRef.getUsername() + "' (" + String.valueOf(playerUUID) + ")");
            for (UUID objectiveUUID : activeObjectiveUUIDs) {
                Objective objective = this.objectiveDataStore.getObjective(objectiveUUID);
                if (objective == null) continue;
                objective.removeActivePlayerUUID(playerUUID);
                if (!objective.getActivePlayerUUIDs().isEmpty()) continue;
                this.objectiveDataStore.saveToDisk(objectiveUUID.toString(), objective);
                this.objectiveDataStore.unloadObjective(objectiveUUID);
            }
        });
    }

    private void onObjectiveLineAssetLoaded(@Nonnull LoadedAssetsEvent<String, ObjectiveLineAsset, DefaultAssetMap<String, ObjectiveLineAsset>> event) {
        if (this.objectiveDataStore == null) {
            return;
        }
        block0: for (Map.Entry<String, ObjectiveLineAsset> objectiveLineEntry : event.getLoadedAssets().entrySet()) {
            String objectiveLineId = objectiveLineEntry.getKey();
            String[] objectiveIds = objectiveLineEntry.getValue().getObjectiveIds();
            for (Objective activeObjective : this.objectiveDataStore.getObjectiveCollection()) {
                ObjectiveLineHistoryData objectiveLineHistoryData = activeObjective.getObjectiveLineHistoryData();
                if (objectiveLineHistoryData == null || !objectiveLineId.equals(objectiveLineHistoryData.getId()) || ArrayUtil.contains(objectiveIds, activeObjective.getObjectiveId())) continue;
                World objectiveWorld = Universe.get().getWorld(activeObjective.worldUUID);
                if (objectiveWorld == null) continue block0;
                objectiveWorld.execute(() -> {
                    Store<EntityStore> store = objectiveWorld.getEntityStore().getStore();
                    this.cancelObjective(activeObjective.getObjectiveUUID(), store);
                });
                continue block0;
            }
        }
    }

    private void onObjectiveAssetLoaded(@Nonnull LoadedAssetsEvent<String, ObjectiveAsset, DefaultAssetMap<String, ObjectiveAsset>> event) {
        this.objectiveDataStore.getObjectiveCollection().forEach(objective -> objective.reloadObjectiveAsset(event.getLoadedAssets()));
    }

    private static void onObjectiveLocationMarkerChange(@Nonnull LoadedAssetsEvent<String, ObjectiveLocationMarkerAsset, DefaultAssetMap<String, ObjectiveLocationMarkerAsset>> event) {
        Map<String, ObjectiveLocationMarkerAsset> loadedAssets = event.getLoadedAssets();
        AndQuery query = Query.and(ObjectiveLocationMarker.getComponentType(), ModelComponent.getComponentType(), TransformComponent.getComponentType());
        Universe.get().getWorlds().forEach((s, world) -> world.execute(() -> {
            Store<EntityStore> store = world.getEntityStore().getStore();
            store.forEachChunk((Query<EntityStore>)query, (archetypeChunk, commandBuffer) -> {
                for (int index = 0; index < archetypeChunk.size(); ++index) {
                    ObjectiveLocationMarker objectiveLocationMarkerComponent = archetypeChunk.getComponent(index, ObjectiveLocationMarker.getComponentType());
                    assert (objectiveLocationMarkerComponent != null);
                    ObjectiveLocationMarkerAsset objectiveLocationMarkerAsset = (ObjectiveLocationMarkerAsset)loadedAssets.get(objectiveLocationMarkerComponent.getObjectiveLocationMarkerId());
                    if (objectiveLocationMarkerAsset == null) continue;
                    TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
                    assert (transformComponent != null);
                    Vector3f rotation = transformComponent.getRotation();
                    objectiveLocationMarkerComponent.updateLocationMarkerValues(objectiveLocationMarkerAsset, rotation.getYaw(), store);
                    ModelComponent modelComponent = archetypeChunk.getComponent(index, ModelComponent.getComponentType());
                    assert (modelComponent != null);
                    Model oldModel = modelComponent.getModel();
                    PersistentModel persistentModelComponent = archetypeChunk.getComponent(index, PersistentModel.getComponentType());
                    assert (persistentModelComponent != null);
                    Model newModel = new Model(oldModel.getModelAssetId(), oldModel.getScale(), oldModel.getRandomAttachmentIds(), oldModel.getAttachments(), objectiveLocationMarkerComponent.getArea().getBoxForEntryArea(), oldModel.getModel(), oldModel.getTexture(), oldModel.getGradientSet(), oldModel.getGradientId(), oldModel.getEyeHeight(), oldModel.getCrouchOffset(), oldModel.getAnimationSetMap(), oldModel.getCamera(), oldModel.getLight(), oldModel.getParticles(), oldModel.getTrails(), oldModel.getPhysicsValues(), oldModel.getDetailBoxes(), oldModel.getPhobia(), oldModel.getPhobiaModelAssetId());
                    persistentModelComponent.setModelReference(newModel.toReference());
                    commandBuffer.putComponent(archetypeChunk.getReferenceTo(index), ModelComponent.getComponentType(), new ModelComponent(newModel));
                }
            });
        }));
    }

    private void onModelAssetChange(@Nonnull LoadedAssetsEvent<String, ModelAsset, DefaultAssetMap<String, ModelAsset>> event) {
        Map<String, ModelAsset> modelMap = event.getLoadedAssets();
        ModelAsset modelAsset = modelMap.get(OBJECTIVE_LOCATION_MARKER_MODEL_ID);
        if (modelAsset == null) {
            return;
        }
        this.objectiveLocationMarkerModel = Model.createUnitScaleModel(modelAsset);
    }

    private void onLivingEntityInventoryChange(@Nonnull LivingEntityInventoryChangeEvent event) {
        LivingEntity entity = (LivingEntity)event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        Set<UUID> activeObjectiveUUIDs = player.getPlayerConfigData().getActiveObjectiveUUIDs();
        if (activeObjectiveUUIDs.isEmpty()) {
            return;
        }
        HashSet<UUID> inventoryItemObjectiveUUIDs = null;
        CombinedItemContainer inventory = entity.getInventory().getCombinedHotbarFirst();
        for (short i = 0; i < inventory.getCapacity(); i = (short)(i + 1)) {
            UUID objectiveUUID;
            ItemStack itemStack = inventory.getItemStack(i);
            if (ItemStack.isEmpty(itemStack) || (objectiveUUID = itemStack.getFromMetadataOrNull(StartObjectiveInteraction.OBJECTIVE_UUID)) == null) continue;
            if (inventoryItemObjectiveUUIDs == null) {
                inventoryItemObjectiveUUIDs = new HashSet<UUID>(activeObjectiveUUIDs);
            }
            inventoryItemObjectiveUUIDs.add(objectiveUUID);
        }
        for (UUID activeObjectiveUUID : activeObjectiveUUIDs) {
            ObjectiveAsset objectiveAsset;
            Objective objective;
            if (inventoryItemObjectiveUUIDs != null && inventoryItemObjectiveUUIDs.contains(activeObjectiveUUID) || (objective = this.objectiveDataStore.getObjective(activeObjectiveUUID)) == null || (objectiveAsset = objective.getObjectiveAsset()) == null || !objectiveAsset.isRemoveOnItemDrop()) continue;
            Ref<EntityStore> reference = entity.getReference();
            Store<EntityStore> store = reference.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                UUIDComponent uuidComponent = store.getComponent(reference, UUIDComponent.getComponentType());
                assert (uuidComponent != null);
                ObjectivePlugin.get().removePlayerFromExistingObjective(store, uuidComponent.getUuid(), activeObjectiveUUID);
            });
        }
    }

    private void onWorldAdded(AddWorldEvent event) {
        event.getWorld().getWorldMapManager().addMarkerProvider("objectives", ObjectiveMarkerProvider.INSTANCE);
    }

    @Nonnull
    public String getObjectiveDataDump() {
        StringBuilder sb = new StringBuilder("Objective Data\n");
        for (Objective objective : this.objectiveDataStore.getObjectiveCollection()) {
            sb.append("Objective ID: ").append(objective.getObjectiveId()).append("\n\t").append("UUID: ").append(objective.getObjectiveUUID()).append("\n\t").append("Players: ").append(Arrays.toString(objective.getPlayerUUIDs().toArray())).append("\n\t").append("Active players: ").append(Arrays.toString(objective.getActivePlayerUUIDs().toArray())).append("\n\n");
        }
        return sb.toString();
    }

    public static class ObjectivePluginConfig {
        public static final BuilderCodec<ObjectivePluginConfig> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ObjectivePluginConfig.class, ObjectivePluginConfig::new).append(new KeyedCodec<DataStoreProvider>("DataStore", DataStoreProvider.CODEC), (objectivePluginConfig, s) -> {
            objectivePluginConfig.dataStoreProvider = s;
        }, objectivePluginConfig -> objectivePluginConfig.dataStoreProvider).add()).build();
        private DataStoreProvider dataStoreProvider = new DiskDataStoreProvider("objectives");

        public DataStoreProvider getDataStoreProvider() {
            return this.dataStoreProvider;
        }
    }
}

