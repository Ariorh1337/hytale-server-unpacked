/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.asseteditor;

import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.builtin.asseteditor.AssetEditorPlugin;
import com.hypixel.hytale.builtin.asseteditor.AssetPath;
import com.hypixel.hytale.builtin.asseteditor.EditorClient;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.AssetStoreTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.assettypehandler.AssetTypeHandler;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorActivateButtonEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorAssetCreatedEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorClientDisconnectEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorFetchAutoCompleteDataEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorRequestDataSetEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorSelectAssetEvent;
import com.hypixel.hytale.builtin.asseteditor.event.AssetEditorUpdateWeatherPreviewLockEvent;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InstantData;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.Vector2f;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorPopupNotificationType;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorPreviewCameraSettings;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateModelPreview;
import com.hypixel.hytale.protocol.packets.asseteditor.AssetEditorUpdateSecondsPerGameDay;
import com.hypixel.hytale.protocol.packets.world.ClearEditorTimeOverride;
import com.hypixel.hytale.protocol.packets.world.UpdateEditorTimeOverride;
import com.hypixel.hytale.protocol.packets.world.UpdateEditorWeatherOverride;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
import com.hypixel.hytale.server.core.asset.type.item.config.AssetIconProperties;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.modules.item.ItemModule;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Deprecated
public class AssetSpecificFunctionality {
    private static final Message NO_GAME_CLIENT_MESSAGE = Message.translation("server.assetEditor.messages.noGameClient");
    private static final ClearEditorTimeOverride CLEAR_EDITOR_TIME_OVERRIDE_PACKET = new ClearEditorTimeOverride();
    private static final UpdateEditorWeatherOverride CLEAR_WEATHER_OVERRIDE_PACKET = new UpdateEditorWeatherOverride(0);
    private static final String MODEL_ASSET_ID = ModelAsset.class.getSimpleName();
    private static final String ITEM_ASSET_ID = Item.class.getSimpleName();
    private static final String WEATHER_ASSET_ID = Weather.class.getSimpleName();
    private static final String ENVIRONMENT_ASSET_ID = Environment.class.getSimpleName();
    private static final Map<UUID, PlayerPreviewData> activeWeatherPreviewMapping = new ConcurrentHashMap<UUID, PlayerPreviewData>();
    private static final AssetEditorPreviewCameraSettings DEFAULT_PREVIEW_CAMERA_SETTINGS = new AssetEditorPreviewCameraSettings(0.25f, new Vector3f(0.0f, 75.0f, 0.0f), new Vector3f(0.0f, (float)Math.toRadians(45.0), 0.0f));

    public static void setup() {
        AssetSpecificFunctionality.getEventRegistry().register(LoadedAssetsEvent.class, ModelAsset.class, AssetSpecificFunctionality::onModelAssetLoaded);
        AssetSpecificFunctionality.getEventRegistry().register(LoadedAssetsEvent.class, Item.class, AssetSpecificFunctionality::onItemAssetLoaded);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorActivateButtonEvent.class, "EquipItem", AssetSpecificFunctionality::onEquipItem);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorActivateButtonEvent.class, "UseModel", AssetSpecificFunctionality::onUseModel);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorActivateButtonEvent.class, "ResetModel", AssetSpecificFunctionality::onResetModel);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorUpdateWeatherPreviewLockEvent.class, AssetSpecificFunctionality::onUpdateWeatherPreviewLockEvent);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorAssetCreatedEvent.class, ITEM_ASSET_ID, AssetSpecificFunctionality::onItemAssetCreated);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorAssetCreatedEvent.class, MODEL_ASSET_ID, AssetSpecificFunctionality::onModelAssetCreated);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorFetchAutoCompleteDataEvent.class, "BlockGroups", AssetSpecificFunctionality::onRequestBlockGroupsDataSet);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorFetchAutoCompleteDataEvent.class, "LocalizationKeys", AssetSpecificFunctionality::onRequestLocalizationKeyDataSet);
        AssetSpecificFunctionality.getEventRegistry().register(AssetEditorRequestDataSetEvent.class, "ItemCategories", AssetSpecificFunctionality::onRequestItemCategoriesDataSet);
        AssetSpecificFunctionality.getEventRegistry().registerGlobal(AssetEditorSelectAssetEvent.class, AssetSpecificFunctionality::onSelectAsset);
        AssetSpecificFunctionality.getEventRegistry().registerGlobal(AssetEditorClientDisconnectEvent.class, AssetSpecificFunctionality::onClientDisconnected);
    }

    @Nullable
    private static PlayerRef tryGetPlayer(@Nonnull EditorClient editorClient) {
        PlayerRef playerRef = editorClient.tryGetPlayer();
        if (playerRef == null) {
            editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Warning, NO_GAME_CLIENT_MESSAGE);
            return null;
        }
        return playerRef;
    }

    private static void onModelAssetLoaded(@Nonnull LoadedAssetsEvent<String, ModelAsset, ?> event) {
        if (event.isInitial()) {
            return;
        }
        Map<EditorClient, AssetPath> clientOpenAssetPathMapping = AssetEditorPlugin.get().getClientOpenAssetPathMapping();
        if (clientOpenAssetPathMapping.isEmpty()) {
            return;
        }
        for (ModelAsset modelAsset : event.getLoadedAssets().values()) {
            for (Map.Entry<EditorClient, AssetPath> editor : clientOpenAssetPathMapping.entrySet()) {
                AssetTypeHandler assetType;
                Path path = editor.getValue().path();
                if (path.toString().isEmpty() || !((assetType = AssetEditorPlugin.get().getAssetTypeRegistry().getAssetTypeHandlerForPath(path)) instanceof AssetStoreTypeHandler) || !((AssetStoreTypeHandler)assetType).getAssetStore().getAssetClass().equals(ModelAsset.class)) continue;
                String id = ModelAsset.getAssetStore().decodeFilePathKey(path);
                if (!modelAsset.getId().equals(id)) continue;
                com.hypixel.hytale.protocol.Model modelPacket = Model.createUnitScaleModel(modelAsset).toPacket();
                AssetEditorUpdateModelPreview packet = new AssetEditorUpdateModelPreview(editor.getValue().toPacket(), modelPacket, null, DEFAULT_PREVIEW_CAMERA_SETTINGS);
                editor.getKey().getPacketHandler().write((Packet)packet);
            }
        }
    }

    private static void onItemAssetLoaded(@Nonnull LoadedAssetsEvent<String, Item, ?> event) {
        if (event.isInitial()) {
            return;
        }
        Map<EditorClient, AssetPath> clientOpenAssetPathMapping = AssetEditorPlugin.get().getClientOpenAssetPathMapping();
        if (clientOpenAssetPathMapping.isEmpty()) {
            return;
        }
        AssetUpdateQuery.RebuildCache rebuildCache = event.getQuery().getRebuildCache();
        if (!(rebuildCache.isBlockTextures() || rebuildCache.isModelTextures() || rebuildCache.isItemIcons() || rebuildCache.isModels())) {
            return;
        }
        for (Item item : event.getLoadedAssets().values()) {
            for (Map.Entry<EditorClient, AssetPath> editor : clientOpenAssetPathMapping.entrySet()) {
                AssetEditorUpdateModelPreview packet;
                AssetTypeHandler assetType;
                Path path = editor.getValue().path();
                if (path.toString().isEmpty() || !((assetType = AssetEditorPlugin.get().getAssetTypeRegistry().getAssetTypeHandlerForPath(path)) instanceof AssetStoreTypeHandler) || !((AssetStoreTypeHandler)assetType).getAssetStore().getAssetClass().equals(Item.class)) continue;
                String id = Item.getAssetStore().decodeFilePathKey(path);
                if (!item.getId().equals(id) || (packet = AssetSpecificFunctionality.getModelPreviewPacketForItem(editor.getValue(), item)) == null) continue;
                editor.getKey().getPacketHandler().write((Packet)packet);
            }
        }
    }

    private static void onItemAssetCreated(@Nonnull AssetEditorAssetCreatedEvent event) {
        if (!"EquipItem".equals(event.getButtonId())) {
            return;
        }
        AssetSpecificFunctionality.equipItem(event.getAssetPath(), event.getEditorClient());
    }

    private static void onModelAssetCreated(@Nonnull AssetEditorAssetCreatedEvent event) {
        if (!"UseModel".equals(event.getButtonId())) {
            return;
        }
        AssetSpecificFunctionality.useModel(event.getAssetPath(), event.getEditorClient());
    }

    private static void onEquipItem(@Nonnull AssetEditorActivateButtonEvent event) {
        AssetPath currentAssetPath = AssetEditorPlugin.get().getOpenAssetPath(event.getEditorClient());
        if (currentAssetPath == null || currentAssetPath.path().toString().isEmpty()) {
            return;
        }
        AssetSpecificFunctionality.equipItem(currentAssetPath.path(), event.getEditorClient());
    }

    private static void onUseModel(@Nonnull AssetEditorActivateButtonEvent event) {
        AssetPath currentAssetPath = AssetEditorPlugin.get().getOpenAssetPath(event.getEditorClient());
        if (currentAssetPath == null || currentAssetPath.path().toString().isEmpty()) {
            return;
        }
        AssetSpecificFunctionality.useModel(currentAssetPath.path(), event.getEditorClient());
    }

    private static void onUpdateWeatherPreviewLockEvent(@Nonnull AssetEditorUpdateWeatherPreviewLockEvent event) {
        PlayerPreviewData currentPreviewSettings = activeWeatherPreviewMapping.computeIfAbsent(event.getEditorClient().getUuid(), k -> new PlayerPreviewData());
        currentPreviewSettings.keepPreview = event.isLocked();
    }

    private static void onResetModel(@Nonnull AssetEditorActivateButtonEvent event) {
        EditorClient editorClient = event.getEditorClient();
        PlayerRef playerRef = AssetSpecificFunctionality.tryGetPlayer(editorClient);
        if (playerRef == null) {
            return;
        }
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            if (!store.getArchetype(ref).contains(PlayerSkinComponent.getComponentType())) {
                Message message = Message.translation("server.assetEditor.messages.model.noAuthSkinForPlayer").param("model", "Player");
                editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, message);
                return;
            }
            PlayerUtil.resetPlayerModel(ref, store);
        });
    }

    private static void equipItem(@Nonnull Path assetPath, @Nonnull EditorClient editorClient) {
        PlayerRef playerRef = AssetSpecificFunctionality.tryGetPlayer(editorClient);
        if (playerRef == null) {
            return;
        }
        Player player = playerRef.getComponent(Player.getComponentType());
        String key = Item.getAssetStore().decodeFilePathKey(assetPath);
        Item item = Item.getAssetMap().getAsset(key);
        if (item == null) {
            editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, Message.translation("server.assetEditor.messages.unknownItem").param("id", key.toString()));
            return;
        }
        ItemArmor itemArmor = item.getArmor();
        if (itemArmor != null) {
            player.getInventory().getArmor().setItemStackForSlot((short)itemArmor.getArmorSlot().ordinal(), new ItemStack(key));
            return;
        }
        player.getInventory().getCombinedHotbarFirst().addItemStack(new ItemStack(key));
    }

    private static void useModel(@Nonnull Path assetPath, @Nonnull EditorClient editorClient) {
        PlayerRef playerRef = AssetSpecificFunctionality.tryGetPlayer(editorClient);
        if (playerRef == null) {
            return;
        }
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            String key = ModelAsset.getAssetStore().decodeFilePathKey(assetPath);
            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(key);
            if (modelAsset == null) {
                Message unknownModelMessage = Message.translation("server.assetEditor.messages.unknownModel").param("id", key);
                editorClient.sendPopupNotification(AssetEditorPopupNotificationType.Error, unknownModelMessage);
                return;
            }
            Model model = Model.createRandomScaleModel(modelAsset);
            store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(model));
        });
    }

    private static void onRequestLocalizationKeyDataSet(@Nonnull AssetEditorFetchAutoCompleteDataEvent event) {
        ObjectArrayList<String> results = new ObjectArrayList<String>();
        String query = event.getQuery().toLowerCase();
        Set<String> messageKeys = I18nModule.get().getMessages("en-US").keySet();
        for (String key : messageKeys) {
            if (key.toLowerCase().startsWith(query)) {
                results.add(key);
            }
            if (results.size() < 25) continue;
            break;
        }
        event.setResults((String[])results.toArray(String[]::new));
    }

    private static void onRequestBlockGroupsDataSet(@Nonnull AssetEditorFetchAutoCompleteDataEvent event) {
        ObjectArrayList<String> results = new ObjectArrayList<String>();
        String query = event.getQuery().toLowerCase();
        for (String group : BlockType.getAssetMap().getGroups()) {
            if (group == null || group.trim().isEmpty() || !query.isEmpty() && !group.toLowerCase().contains(query)) continue;
            results.add(group);
        }
        event.setResults((String[])results.toArray(String[]::new));
    }

    private static void onRequestItemCategoriesDataSet(@Nonnull AssetEditorRequestDataSetEvent event) {
        ItemModule itemModule = ItemModule.get();
        if (itemModule.isDisabled()) {
            HytaleLogger.getLogger().at(Level.WARNING).log("Received ItemCategories dataset request but ItemModule is disabled!");
            return;
        }
        event.setResults((String[])itemModule.getFlatItemCategoryList().toArray(String[]::new));
    }

    private static void onClientDisconnected(@Nonnull AssetEditorClientDisconnectEvent event) {
        AssetEditorPlugin plugin = AssetEditorPlugin.get();
        EditorClient editorClient = event.getEditorClient();
        PlayerRef player = editorClient.tryGetPlayer();
        UUID uuid = editorClient.getUuid();
        Set<EditorClient> editorClients = plugin.getEditorClients(uuid);
        if (editorClients == null || editorClients.size() == 1) {
            if (player != null) {
                player.getPacketHandler().write((Packet)CLEAR_EDITOR_TIME_OVERRIDE_PACKET);
                player.getPacketHandler().write((Packet)CLEAR_WEATHER_OVERRIDE_PACKET);
            }
            activeWeatherPreviewMapping.remove(uuid);
            return;
        }
        AssetPath openAssetPath = plugin.getOpenAssetPath(editorClient);
        if (openAssetPath == null || openAssetPath.equals(AssetPath.EMPTY_PATH)) {
            return;
        }
        AssetTypeHandler assetType = plugin.getAssetTypeRegistry().getAssetTypeHandlerForPath(openAssetPath.path());
        if (assetType == null || !Weather.class.getSimpleName().equals(assetType.getConfig().id)) {
            return;
        }
        activeWeatherPreviewMapping.remove(uuid);
        if (player != null) {
            player.getPacketHandler().write((Packet)new UpdateEditorWeatherOverride(0));
        }
    }

    static void resetTimeSettings(@Nonnull EditorClient editorClient, @Nonnull PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        Player playerComponent = playerRef.getComponent(Player.getComponentType());
        assert (playerComponent != null);
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        PacketHandler packetHandler = editorClient.getPacketHandler();
        AssetEditorUpdateSecondsPerGameDay settingsPacket = new AssetEditorUpdateSecondsPerGameDay(world.getDaytimeDurationSeconds(), world.getNighttimeDurationSeconds());
        packetHandler.write((Packet)settingsPacket);
        Instant gameTime = worldTimeResource.getGameTime();
        UpdateEditorTimeOverride packet = new UpdateEditorTimeOverride(new InstantData(gameTime.getEpochSecond(), gameTime.getNano()), world.getWorldConfig().isGameTimePaused());
        packetHandler.write((Packet)packet);
        playerRef.getPacketHandler().write((Packet)CLEAR_EDITOR_TIME_OVERRIDE_PACKET);
    }

    static void handleWeatherOrEnvironmentUnselected(@Nonnull EditorClient editorClient, @Nonnull Path assetPath, boolean wasWeather) {
        PlayerRef player = editorClient.tryGetPlayer();
        if (player == null) {
            return;
        }
        PlayerPreviewData currentPreviewSettings = activeWeatherPreviewMapping.computeIfAbsent(editorClient.getUuid(), k -> new PlayerPreviewData());
        if (currentPreviewSettings.keepPreview) {
            return;
        }
        AssetSpecificFunctionality.resetTimeSettings(editorClient, player);
        if (wasWeather) {
            if (!assetPath.equals(currentPreviewSettings.weatherAssetPath)) {
                return;
            }
            currentPreviewSettings.weatherAssetPath = null;
            player.getPacketHandler().write((Packet)CLEAR_WEATHER_OVERRIDE_PACKET);
        }
    }

    static void handleWeatherOrEnvironmentSelected(@Nonnull EditorClient editorClient, @Nonnull Path assetPath, boolean isWeather) {
        PlayerRef player = editorClient.tryGetPlayer();
        if (player == null) {
            return;
        }
        PlayerPreviewData currentPreviewSettings = activeWeatherPreviewMapping.computeIfAbsent(editorClient.getUuid(), k -> new PlayerPreviewData());
        if (!currentPreviewSettings.keepPreview) {
            AssetSpecificFunctionality.resetTimeSettings(editorClient, player);
        }
        if (isWeather) {
            AssetStore<String, Weather, IndexedLookupTableAssetMap<String, Weather>> assetStore = Weather.getAssetStore();
            String key = assetStore.decodeFilePathKey(assetPath);
            int weatherIndex = assetStore.getAssetMap().getIndex(key);
            currentPreviewSettings.weatherAssetPath = assetPath;
            player.getPacketHandler().write((Packet)new UpdateEditorWeatherOverride(weatherIndex));
        }
    }

    private static void onSelectAsset(@Nonnull AssetEditorSelectAssetEvent event) {
        boolean isWeather;
        String previousAssetType;
        boolean wasWeather;
        String assetType = event.getAssetType();
        if (MODEL_ASSET_ID.equals(assetType)) {
            String key = ModelAsset.getAssetStore().decodeFilePathKey(event.getAssetFilePath().path());
            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(key);
            if (modelAsset != null) {
                com.hypixel.hytale.protocol.Model modelPacket = Model.createUnitScaleModel(modelAsset).toPacket();
                event.getEditorClient().getPacketHandler().write((Packet)new AssetEditorUpdateModelPreview(event.getAssetFilePath().toPacket(), modelPacket, null, DEFAULT_PREVIEW_CAMERA_SETTINGS));
            }
        }
        if (ITEM_ASSET_ID.equals(assetType)) {
            AssetEditorUpdateModelPreview packet;
            AssetPath assetPath = event.getAssetFilePath();
            String key = Item.getAssetStore().decodeFilePathKey(assetPath.path());
            Item item = Item.getAssetMap().getAsset(key);
            if (item != null && (packet = AssetSpecificFunctionality.getModelPreviewPacketForItem(assetPath, item)) != null) {
                event.getEditorClient().getPacketHandler().write((Packet)packet);
            }
        }
        if ((wasWeather = WEATHER_ASSET_ID.equals(previousAssetType = event.getPreviousAssetType())) || ENVIRONMENT_ASSET_ID.equals(previousAssetType)) {
            AssetSpecificFunctionality.handleWeatherOrEnvironmentUnselected(event.getEditorClient(), event.getPreviousAssetFilePath().path(), wasWeather);
        }
        if ((isWeather = WEATHER_ASSET_ID.equals(assetType)) || ENVIRONMENT_ASSET_ID.equals(assetType)) {
            AssetSpecificFunctionality.handleWeatherOrEnvironmentSelected(event.getEditorClient(), event.getAssetFilePath().path(), isWeather);
        }
    }

    public static AssetEditorUpdateModelPreview getModelPreviewPacketForItem(@Nonnull AssetPath assetPath, @Nullable Item item) {
        BlockType blockType;
        if (item == null) {
            return null;
        }
        AssetIconProperties iconProperties = item.getIconProperties();
        AssetIconProperties defaultIconProperties = AssetSpecificFunctionality.getDefaultItemIconProperties(item);
        if (iconProperties == null) {
            iconProperties = defaultIconProperties;
        }
        AssetEditorPreviewCameraSettings camera = new AssetEditorPreviewCameraSettings();
        camera.modelScale = iconProperties.getScale() * item.getScale();
        Vector2f translation = iconProperties.getTranslation() != null ? iconProperties.getTranslation() : defaultIconProperties.getTranslation();
        camera.cameraPosition = new Vector3f(-translation.x, -translation.y, 0.0f);
        Vector3f rotation = iconProperties.getRotation() != null ? iconProperties.getRotation() : defaultIconProperties.getRotation();
        camera.cameraOrientation = new Vector3f((float)(-Math.toRadians(rotation.x)), (float)(-Math.toRadians(rotation.y)), (float)(-Math.toRadians(rotation.z)));
        if (item.getBlockId() != null && (blockType = (BlockType)BlockType.getAssetStore().getAssetMap().getAsset(item.getBlockId())) != null) {
            camera.modelScale *= blockType.getCustomModelScale();
            return new AssetEditorUpdateModelPreview(assetPath.toPacket(), null, blockType.toPacket(), camera);
        }
        com.hypixel.hytale.protocol.Model modelPacket = AssetSpecificFunctionality.convertToModelPacket(item);
        return new AssetEditorUpdateModelPreview(assetPath.toPacket(), modelPacket, null, camera);
    }

    @Nonnull
    public static AssetIconProperties getDefaultItemIconProperties(@Nonnull Item item) {
        if (item.getWeapon() != null) {
            return new AssetIconProperties(0.37f, new Vector2f(-24.6f, -24.6f), new Vector3f(45.0f, 90.0f, 0.0f));
        }
        if (item.getTool() != null) {
            return new AssetIconProperties(0.5f, new Vector2f(-17.4f, -12.0f), new Vector3f(45.0f, 270.0f, 0.0f));
        }
        if (item.getArmor() != null) {
            return switch (item.getArmor().getArmorSlot()) {
                default -> throw new MatchException(null, null);
                case ItemArmorSlot.Chest -> new AssetIconProperties(0.5f, new Vector2f(0.0f, -5.0f), new Vector3f(22.5f, 45.0f, 22.5f));
                case ItemArmorSlot.Head -> new AssetIconProperties(0.5f, new Vector2f(0.0f, -3.0f), new Vector3f(22.5f, 45.0f, 22.5f));
                case ItemArmorSlot.Legs -> new AssetIconProperties(0.5f, new Vector2f(0.0f, -25.8f), new Vector3f(22.5f, 45.0f, 22.5f));
                case ItemArmorSlot.Hands -> new AssetIconProperties(0.92f, new Vector2f(0.0f, -10.8f), new Vector3f(22.5f, 45.0f, 22.5f));
            };
        }
        return new AssetIconProperties(0.58823f, new Vector2f(0.0f, -13.5f), new Vector3f(22.5f, 45.0f, 22.5f));
    }

    @Nonnull
    public static com.hypixel.hytale.protocol.Model convertToModelPacket(@Nonnull Item item) {
        com.hypixel.hytale.protocol.Model packet = new com.hypixel.hytale.protocol.Model();
        packet.path = item.getModel();
        packet.texture = item.getTexture();
        return packet;
    }

    @Nonnull
    private static EventRegistry getEventRegistry() {
        return AssetEditorPlugin.get().getEventRegistry();
    }

    public static class PlayerPreviewData {
        @Nullable
        private Path weatherAssetPath;
        private boolean keepPreview;
    }
}

