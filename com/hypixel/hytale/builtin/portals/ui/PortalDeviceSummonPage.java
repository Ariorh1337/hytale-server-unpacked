/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.portals.ui;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.builtin.instances.config.InstanceDiscoveryConfig;
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig;
import com.hypixel.hytale.builtin.portals.PortalsPlugin;
import com.hypixel.hytale.builtin.portals.components.PortalDevice;
import com.hypixel.hytale.builtin.portals.components.PortalDeviceConfig;
import com.hypixel.hytale.builtin.portals.integrations.PortalGameplayConfig;
import com.hypixel.hytale.builtin.portals.integrations.PortalRemovalCondition;
import com.hypixel.hytale.builtin.portals.resources.PortalWorld;
import com.hypixel.hytale.builtin.portals.ui.PortalSpawnFinder;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.PortalKey;
import com.hypixel.hytale.server.core.asset.type.portalworld.PillTag;
import com.hypixel.hytale.server.core.asset.type.portalworld.PortalDescription;
import com.hypixel.hytale.server.core.asset.type.portalworld.PortalSpawn;
import com.hypixel.hytale.server.core.asset.type.portalworld.PortalType;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.spawn.IndividualSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PortalDeviceSummonPage
extends InteractiveCustomUIPage<Data> {
    private final PortalDeviceConfig config;
    private final Ref<ChunkStore> blockRef;
    private final ItemStack offeredItemStack;
    private static final Transform DEFAULT_WORLDGEN_SPAWN = new Transform(0.0, 140.0, 0.0);

    public PortalDeviceSummonPage(@Nonnull PlayerRef playerRef, PortalDeviceConfig config, Ref<ChunkStore> blockRef, ItemStack offeredItemStack) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, Data.CODEC);
        this.config = config;
        this.blockRef = blockRef;
        this.offeredItemStack = offeredItemStack;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        boolean bl;
        String[] wisdomKeys;
        String[] objectivesKeys;
        PortalType portalType;
        PortalKey portalKey;
        block16: {
            block14: {
                block15: {
                    State state;
                    block13: {
                        Player playerComponent = store.getComponent(ref, Player.getComponentType());
                        assert (playerComponent != null);
                        state = this.computeState(playerComponent, store);
                        if (state == Error.INVALID_BLOCK) {
                            return;
                        }
                        if (!(state instanceof CanSpawnPortal)) break block13;
                        CanSpawnPortal canSpawn = (CanSpawnPortal)state;
                        commandBuilder.append("Pages/PortalDeviceSummon.ui");
                        portalKey = canSpawn.portalKey();
                        portalType = canSpawn.portalType();
                        PortalDescription portalDesc = portalType.getDescription();
                        commandBuilder.set("#Artwork.Background", "Pages/Portals/" + portalDesc.getSplashImageFilename());
                        commandBuilder.set("#Title0.TextSpans", portalDesc.getDisplayName());
                        commandBuilder.set("#FlavorLabel.TextSpans", portalDesc.getFlavorText());
                        PortalDeviceSummonPage.updateCustomPills(commandBuilder, portalType);
                        objectivesKeys = portalDesc.getObjectivesKeys();
                        wisdomKeys = portalDesc.getWisdomKeys();
                        if (objectivesKeys.length <= 0) break block14;
                        break block15;
                    }
                    commandBuilder.append("Pages/PortalDeviceError.ui");
                    if (state == Error.NOTHING_OFFERED || state == Error.NOT_A_PORTAL_KEY) {
                        commandBuilder.set("#UsageErrorTitle.Text", Message.translation("server.customUI.portalDevice.needPortalKey"));
                        commandBuilder.set("#UsageErrorLabel.Text", Message.translation("server.customUI.portalDevice.nothingHeld"));
                        return;
                    }
                    if (state == Error.PORTAL_INSIDE_PORTAL) {
                        commandBuilder.set("#UsageErrorLabel.Text", Message.translation("server.customUI.portalDevice.portalInsidePortal"));
                        return;
                    }
                    if (state == Error.MAX_ACTIVE_PORTALS) {
                        commandBuilder.set("#UsageErrorLabel.Text", Message.translation("server.customUI.portalDevice.maxFragments").param("max", 4));
                        return;
                    }
                    if (state instanceof InstanceKeyNotFound) {
                        InstanceKeyNotFound instanceKeyNotFound = (InstanceKeyNotFound)state;
                        try {
                            String string;
                            String instanceId = string = instanceKeyNotFound.instanceId();
                            commandBuilder.set("#UsageErrorLabel.Text", "The instance id '" + instanceId + "' does not exist, this is a developer error with the portaltype.");
                            return;
                        }
                        catch (Throwable throwable) {
                            throw new MatchException(throwable.toString(), throwable);
                        }
                    }
                    if (state instanceof PortalTypeNotFound) {
                        PortalTypeNotFound portalTypeNotFound = (PortalTypeNotFound)state;
                        {
                            String string;
                            String portalTypeId = string = portalTypeNotFound.portalTypeId();
                            commandBuilder.set("#UsageErrorLabel.Text", "The portaltype id '" + portalTypeId + "' does not exist, this is a developer error with the portal key.");
                        }
                        return;
                    }
                    if (state == Error.BOTCHED_GAMEPLAY_CONFIG) {
                        commandBuilder.set("#UsageErrorLabel.Text", "The gameplay config set on the PortalType set in the key does not have a Portal plugin configuration, this is a developer error.");
                        return;
                    }
                    commandBuilder.set("#UsageErrorLabel.Text", Message.translation("server.customUI.portalDevice.unknownError").param("state", state.toString()));
                    return;
                }
                bl = true;
                break block16;
            }
            bl = false;
        }
        commandBuilder.set("#Objectives.Visible", bl);
        commandBuilder.set("#Tips.Visible", wisdomKeys.length > 0);
        PortalDeviceSummonPage.updateBulletList(commandBuilder, "#ObjectivesList", objectivesKeys);
        PortalDeviceSummonPage.updateBulletList(commandBuilder, "#TipsList", wisdomKeys);
        PortalGameplayConfig gameplayConfig = portalType.getGameplayConfig().getPluginConfig().get(PortalGameplayConfig.class);
        long totalTimeLimit = TimeUnit.SECONDS.toMinutes(portalKey.getTimeLimitSeconds());
        if (portalType.isVoidInvasionEnabled()) {
            long minutesBreach = TimeUnit.SECONDS.toMinutes(gameplayConfig.getVoidEvent().getDurationSeconds());
            long exploMinutes = totalTimeLimit - minutesBreach;
            commandBuilder.set("#ExplorationTimeText.TextSpans", Message.translation("server.customUI.portalDevice.minutesToExplore").param("time", exploMinutes));
            commandBuilder.set("#BreachTimeBullet.Visible", true);
            commandBuilder.set("#BreachTimeText.TextSpans", Message.translation("server.customUI.portalDevice.minutesVoidInvasion").param("time", minutesBreach));
        } else {
            commandBuilder.set("#ExplorationTimeText.TextSpans", Message.translation("server.customUI.portalDevice.durationMins").param("time", totalTimeLimit));
        }
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SummonButton", EventData.of("Action", "SummonActivated"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.MouseEntered, "#SummonButton", EventData.of("Action", "SummonMouseEntered"), false);
        eventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#SummonButton", EventData.of("Action", "SummonMouseExited"), false);
    }

    private static void updateCustomPills(UICommandBuilder commandBuilder, PortalType portalType) {
        List<PillTag> pills = portalType.getDescription().getPillTags();
        for (int i = 0; i < pills.size(); ++i) {
            PillTag pillTag = pills.get(i);
            String child = "#Pills[" + i + "]";
            commandBuilder.append("#Pills", "Pages/Portals/Pill.ui");
            commandBuilder.set(child + ".Background.Color", ColorParseUtil.colorToHexString(pillTag.getColor()));
            commandBuilder.set(child + " #Label.TextSpans", pillTag.getMessage());
        }
    }

    private static void updateBulletList(UICommandBuilder commandBuilder, String selector, String[] messageKeys) {
        for (int i = 0; i < messageKeys.length; ++i) {
            String messageKey = messageKeys[i];
            String child = selector + "[" + i + "]";
            commandBuilder.append(selector, "Pages/Portals/BulletPoint.ui");
            commandBuilder.set(child + " #Label.TextSpans", Message.translation(messageKey));
        }
    }

    public static Message createDescription(PortalType portalType, int timeLimitSeconds) {
        Message msg = Message.empty();
        Message durationMsg = PortalDeviceSummonPage.formatDurationCrudely(timeLimitSeconds);
        msg.insert(Message.translation("server.customUI.portalDevice.timeLimit").param("limit", durationMsg.color("#f9cb13")));
        return msg;
    }

    private static Message formatDurationCrudely(int seconds) {
        if (seconds < 0) {
            return Message.translation("server.customUI.portalDevice.durationUnlimited");
        }
        if (seconds >= 120) {
            int minutes = seconds / 60;
            return Message.translation("server.customUI.portalDevice.durationMinutes").param("duration", minutes);
        }
        return Message.translation("server.customUI.portalDevice.durationSeconds").param("duration", seconds);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull Data data) {
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        assert (playerComponent != null);
        State state = this.computeState(playerComponent, store);
        if (!(state instanceof CanSpawnPortal)) {
            return;
        }
        CanSpawnPortal canSpawn = (CanSpawnPortal)state;
        if ("SummonMouseEntered".equals(data.action)) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#Vignette.Visible", true);
            this.sendUpdate(commandBuilder, null, false);
            return;
        }
        if ("SummonMouseExited".equals(data.action)) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            commandBuilder.set("#Vignette.Visible", false);
            this.sendUpdate(commandBuilder, null, false);
            return;
        }
        playerComponent.getPageManager().setPage(ref, store, Page.None);
        World originWorld = store.getExternalData().getWorld();
        int index = canSpawn.blockState().getIndex();
        int x = ChunkUtil.xFromBlockInColumn(index);
        int y = ChunkUtil.yFromBlockInColumn(index);
        int z = ChunkUtil.zFromBlockInColumn(index);
        WorldChunk worldChunk = canSpawn.worldChunk();
        PortalKey portalKey = canSpawn.portalKey();
        PortalDevice portalDevice = canSpawn.portalDevice();
        BlockType blockType = worldChunk.getBlockType(x, y, z);
        if (blockType != portalDevice.getBaseBlockType()) {
            return;
        }
        if (!this.config.areBlockStatesValid(blockType)) {
            return;
        }
        int rotation = worldChunk.getRotationIndex(x, y, z);
        BlockType spawningType = blockType.getBlockForState(this.config.getSpawningState());
        BlockType onType = blockType.getBlockForState(this.config.getOnState());
        BlockType offType = blockType.getBlockForState(this.config.getOffState());
        int setting = 6;
        worldChunk.setBlock(x, y, z, BlockType.getAssetMap().getIndex(spawningType.getId()), spawningType, rotation, 0, 6);
        double worldX = (double)ChunkUtil.worldCoordFromLocalCoord(worldChunk.getX(), x) + 0.5;
        double worldY = (double)y + 0.5;
        double worldZ = (double)ChunkUtil.worldCoordFromLocalCoord(worldChunk.getZ(), z) + 0.5;
        if (spawningType.getInteractionSoundEventIndex() != 0) {
            SoundUtil.playSoundEvent3d(spawningType.getInteractionSoundEventIndex(), SoundCategory.SFX, worldX, worldY, worldZ, store);
        }
        PortalDeviceSummonPage.decrementItemInHand(playerComponent.getInventory(), 1);
        Transform transform = new Transform((double)x + 0.5, (double)y + 1.0, (double)z + 0.5);
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
        assert (uuidComponent != null);
        PortalType portalType = canSpawn.portalType;
        UUID playerUUID = uuidComponent.getUuid();
        PortalGameplayConfig gameplayConfig = canSpawn.portalGameplayConfig;
        ((CompletableFuture)((CompletableFuture)InstancesPlugin.get().spawnInstance(portalType.getInstanceId(), originWorld, transform).thenCompose(spawnedWorld -> {
            WorldConfig worldConfig = spawnedWorld.getWorldConfig();
            worldConfig.setDeleteOnUniverseStart(true);
            worldConfig.setDeleteOnRemove(true);
            worldConfig.setGameplayConfig(portalType.getGameplayConfigId());
            InstanceWorldConfig instanceConfig = InstanceWorldConfig.ensureAndGet(worldConfig);
            if (instanceConfig.getDiscovery() == null) {
                InstanceDiscoveryConfig discoveryConfig = new InstanceDiscoveryConfig();
                discoveryConfig.setTitleKey(portalType.getDescription().getDisplayNameKey());
                discoveryConfig.setSubtitleKey("server.portals.discoverySubtitle");
                discoveryConfig.setDisplay(true);
                discoveryConfig.setAlwaysDisplay(true);
                instanceConfig.setDiscovery(discoveryConfig);
            }
            PortalRemovalCondition portalRemoval = new PortalRemovalCondition(portalKey.getTimeLimitSeconds());
            instanceConfig.setRemovalConditions(portalRemoval);
            PortalWorld portalWorld = spawnedWorld.getEntityStore().getStore().getResource(PortalWorld.getResourceType());
            portalWorld.init(portalType, portalKey.getTimeLimitSeconds(), portalRemoval, gameplayConfig);
            String returnBlockType = portalDevice.getConfig().getReturnBlock();
            if (returnBlockType == null) {
                throw new RuntimeException("Return block type on PortalDevice is misconfigured");
            }
            return PortalDeviceSummonPage.spawnReturnPortal(spawnedWorld, portalWorld, playerUUID, returnBlockType);
        })).thenAcceptAsync(spawnedWorld -> {
            portalDevice.setDestinationWorld((World)spawnedWorld);
            worldChunk.setBlock(x, y, z, BlockType.getAssetMap().getIndex(onType.getId()), onType, rotation, 0, 6);
        }, (Executor)originWorld)).exceptionallyAsync(t -> {
            playerComponent.sendMessage(Message.translation("server.portals.device.internalErrorSpawning"));
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause((Throwable)t)).log("Error creating instance for Portal Device " + String.valueOf(portalKey), t);
            worldChunk.setBlock(x, y, z, BlockType.getAssetMap().getIndex(offType.getId()), offType, rotation, 0, 6);
            return null;
        }, (Executor)originWorld);
    }

    private static CompletableFuture<World> spawnReturnPortal(World world, PortalWorld portalWorld, UUID sampleUuid, String portalBlockType) {
        PortalSpawn portalSpawn = portalWorld.getPortalType().getPortalSpawn();
        return PortalDeviceSummonPage.getSpawnTransform(world, sampleUuid, portalSpawn).thenCompose(spawnTransform -> {
            Vector3d spawnPoint = spawnTransform.getPosition();
            return ((CompletableFuture)world.getChunkAsync(ChunkUtil.indexChunkFromBlock((int)spawnPoint.x, (int)spawnPoint.z)).thenAccept(chunk -> {
                for (int dy = 0; dy < 3; ++dy) {
                    for (int dx = -1; dx <= 1; ++dx) {
                        for (int dz = -1; dz <= 1; ++dz) {
                            chunk.setBlock((int)spawnPoint.x + dx, (int)spawnPoint.y + dy, (int)spawnPoint.z + dz, BlockType.EMPTY);
                        }
                    }
                }
                chunk.setBlock((int)spawnPoint.x, (int)spawnPoint.y, (int)spawnPoint.z, portalBlockType);
                portalWorld.setSpawnPoint((Transform)spawnTransform);
                world.getWorldConfig().setSpawnProvider(new IndividualSpawnProvider((Transform)spawnTransform));
                HytaleLogger.getLogger().at(Level.INFO).log("Spawned return portal for " + world.getName() + " at " + (int)spawnPoint.x + ", " + (int)spawnPoint.y + ", " + (int)spawnPoint.z);
            })).thenApply(nothing -> world);
        });
    }

    private static CompletableFuture<Transform> getSpawnTransform(World world, UUID sampleUuid, @Nullable PortalSpawn portalSpawn) {
        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        if (spawnProvider == null) {
            return CompletableFuture.completedFuture(null);
        }
        Transform worldSpawnPoint = spawnProvider.getSpawnPoint(world, sampleUuid);
        if (!DEFAULT_WORLDGEN_SPAWN.equals(worldSpawnPoint) || portalSpawn == null) {
            Transform uppedSpawnPoint = worldSpawnPoint.clone();
            uppedSpawnPoint.getPosition().add(0.0, 0.5, 0.0);
            return CompletableFuture.completedFuture(uppedSpawnPoint);
        }
        return CompletableFuture.supplyAsync(() -> {
            Transform computedSpawn = PortalSpawnFinder.computeSpawnTransform(world, portalSpawn);
            return computedSpawn == null ? worldSpawnPoint : computedSpawn;
        }, world);
    }

    private State computeState(@Nonnull Player player, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        PortalGameplayConfig portalGameplayConfig;
        if (!this.blockRef.isValid()) {
            return Error.INVALID_BLOCK;
        }
        int activeFragments = PortalsPlugin.getInstance().countActiveFragments();
        if (activeFragments >= 4) {
            return Error.MAX_ACTIVE_PORTALS;
        }
        Store<ChunkStore> chunkStore = this.blockRef.getStore();
        BlockModule.BlockStateInfo blockStateInfo = chunkStore.getComponent(this.blockRef, BlockModule.BlockStateInfo.getComponentType());
        PortalDevice portalDevice = chunkStore.getComponent(this.blockRef, PortalDevice.getComponentType());
        if (blockStateInfo == null || portalDevice == null) {
            return Error.INVALID_BLOCK;
        }
        Ref<ChunkStore> chunkRef = blockStateInfo.getChunkRef();
        if (chunkRef == null || !chunkRef.isValid()) {
            return Error.INVALID_BLOCK;
        }
        WorldChunk worldChunk = chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
        if (worldChunk == null) {
            return Error.INVALID_BLOCK;
        }
        World existingDestinationWorld = portalDevice.getDestinationWorld();
        if (existingDestinationWorld != null) {
            return Error.INVALID_DESTINATION;
        }
        if (this.offeredItemStack == null) {
            return Error.NOTHING_OFFERED;
        }
        ItemStack inHand = player.getInventory().getItemInHand();
        if (!this.offeredItemStack.equals(inHand)) {
            return Error.OFFERED_IS_NOT_HELD;
        }
        Item offeredItem = this.offeredItemStack.getItem();
        PortalKey portalKey = offeredItem.getPortalKey();
        if (portalKey == null) {
            return Error.NOT_A_PORTAL_KEY;
        }
        String portalTypeId = portalKey.getPortalTypeId();
        PortalType portalType = PortalType.getAssetMap().getAsset(portalTypeId);
        if (portalType == null) {
            return new PortalTypeNotFound(portalTypeId);
        }
        String instanceId = portalType.getInstanceId();
        InstancesPlugin.get();
        boolean instanceExists = InstancesPlugin.doesInstanceAssetExist(instanceId);
        if (!instanceExists) {
            return new InstanceKeyNotFound(instanceId);
        }
        PortalWorld insidePortalWorld = componentAccessor.getResource(PortalWorld.getResourceType());
        if (insidePortalWorld.exists()) {
            return Error.PORTAL_INSIDE_PORTAL;
        }
        String gameplayConfigId = portalType.getGameplayConfigId();
        GameplayConfig gameplayConfig = GameplayConfig.getAssetMap().getAsset(gameplayConfigId);
        PortalGameplayConfig portalGameplayConfig2 = portalGameplayConfig = gameplayConfig == null ? null : gameplayConfig.getPluginConfig().get(PortalGameplayConfig.class);
        if (portalGameplayConfig == null) {
            return Error.BOTCHED_GAMEPLAY_CONFIG;
        }
        return new CanSpawnPortal(portalKey, portalType, worldChunk, blockStateInfo, portalDevice, portalGameplayConfig);
    }

    private static void decrementItemInHand(Inventory inventory, int amount) {
        if (inventory.usingToolsItem()) {
            return;
        }
        byte hotbarSlot = inventory.getActiveHotbarSlot();
        if (hotbarSlot == -1) {
            return;
        }
        ItemContainer hotbar = inventory.getHotbar();
        ItemStack inHand = hotbar.getItemStack(hotbarSlot);
        if (inHand == null) {
            return;
        }
        hotbar.removeItemStackFromSlot(hotbarSlot, inHand, amount, false, true);
    }

    protected static class Data {
        private static final String KEY_ACTION = "Action";
        public static final BuilderCodec<Data> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(Data.class, Data::new).append(new KeyedCodec<String>("Action", Codec.STRING), (entry, s) -> {
            entry.action = s;
        }, entry -> entry.action).add()).build();
        private String action;

        protected Data() {
        }
    }

    private static sealed interface State
    permits CanSpawnPortal, Error, InstanceKeyNotFound, PortalTypeNotFound {
    }

    private static enum Error implements State
    {
        NOTHING_OFFERED,
        OFFERED_IS_NOT_HELD,
        NOT_A_PORTAL_KEY,
        INVALID_BLOCK,
        INVALID_DESTINATION,
        PORTAL_INSIDE_PORTAL,
        BOTCHED_GAMEPLAY_CONFIG,
        MAX_ACTIVE_PORTALS;

    }

    private record CanSpawnPortal(PortalKey portalKey, PortalType portalType, WorldChunk worldChunk, BlockModule.BlockStateInfo blockState, PortalDevice portalDevice, PortalGameplayConfig portalGameplayConfig) implements State
    {
    }

    private record InstanceKeyNotFound(String instanceId) implements State
    {
    }

    private record PortalTypeNotFound(String portalTypeId) implements State
    {
    }
}

