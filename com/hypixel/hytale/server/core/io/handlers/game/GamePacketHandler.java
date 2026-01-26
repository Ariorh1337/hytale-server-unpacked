/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io.handlers.game;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockRotation;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.HostAddress;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
import com.hypixel.hytale.protocol.packets.camera.RequestFlyCameraMode;
import com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode;
import com.hypixel.hytale.protocol.packets.connection.Disconnect;
import com.hypixel.hytale.protocol.packets.connection.Pong;
import com.hypixel.hytale.protocol.packets.entities.MountMovement;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.protocol.packets.interface_.ChatMessage;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageEvent;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.protocol.packets.interface_.SetPage;
import com.hypixel.hytale.protocol.packets.interface_.UpdateLanguage;
import com.hypixel.hytale.protocol.packets.machinima.RequestMachinimaActorModel;
import com.hypixel.hytale.protocol.packets.machinima.SetMachinimaActorModel;
import com.hypixel.hytale.protocol.packets.machinima.UpdateMachinimaScene;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.protocol.packets.player.ClientPlaceBlock;
import com.hypixel.hytale.protocol.packets.player.ClientReady;
import com.hypixel.hytale.protocol.packets.player.MouseInteraction;
import com.hypixel.hytale.protocol.packets.player.RemoveMapMarker;
import com.hypixel.hytale.protocol.packets.player.SyncPlayerPreferences;
import com.hypixel.hytale.protocol.packets.serveraccess.SetServerAccess;
import com.hypixel.hytale.protocol.packets.serveraccess.UpdateServerAccess;
import com.hypixel.hytale.protocol.packets.setup.RequestAssets;
import com.hypixel.hytale.protocol.packets.setup.ViewRadius;
import com.hypixel.hytale.protocol.packets.window.ClientOpenWindow;
import com.hypixel.hytale.protocol.packets.window.CloseWindow;
import com.hypixel.hytale.protocol.packets.window.SendWindowAction;
import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
import com.hypixel.hytale.protocol.packets.world.SetPaused;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.protocol.packets.worldmap.TeleportToWorldMapMarker;
import com.hypixel.hytale.protocol.packets.worldmap.TeleportToWorldMapPosition;
import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMapVisible;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.asset.common.CommonAssetModule;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleModule;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ValidatedWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.ProtocolVersion;
import com.hypixel.hytale.server.core.io.ServerManager;
import com.hypixel.hytale.server.core.io.handlers.GenericPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.IPacketHandler;
import com.hypixel.hytale.server.core.io.handlers.SubPacketHandler;
import com.hypixel.hytale.server.core.io.netty.NettyUtil;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerCreativeSettings;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.modules.interaction.BlockPlaceUtils;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.MessageUtil;
import com.hypixel.hytale.server.core.util.PositionUtil;
import com.hypixel.hytale.server.core.util.ValidateUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Deque;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class GamePacketHandler
extends GenericPacketHandler
implements IPacketHandler {
    private static final double RELATIVE_POSITION_DELTA_SCALE = 10000.0;
    private PlayerRef playerRef;
    @Deprecated
    private Player playerComponent;
    @Nonnull
    private final Deque<SyncInteractionChain> interactionPacketQueue = new ConcurrentLinkedDeque<SyncInteractionChain>();

    public GamePacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, @Nonnull PlayerAuthentication auth) {
        super(channel, protocolVersion);
        this.auth = auth;
        ServerManager.get().populateSubPacketHandlers(this);
        this.registerHandlers();
    }

    @Nonnull
    public Deque<SyncInteractionChain> getInteractionPacketQueue() {
        return this.interactionPacketQueue;
    }

    @Override
    @Nonnull
    public PlayerRef getPlayerRef() {
        return this.playerRef;
    }

    public void setPlayerRef(@Nonnull PlayerRef playerRef, @Nonnull Player playerComponent) {
        this.playerRef = playerRef;
        this.playerComponent = playerComponent;
    }

    @Override
    @Nonnull
    public String getIdentifier() {
        return "{Playing(" + NettyUtil.formatRemoteAddress(this.channel) + "), " + (String)(this.playerRef != null ? String.valueOf(this.playerRef.getUuid()) + ", " + this.playerRef.getUsername() : "null player") + "}";
    }

    @Override
    protected void registered0(PacketHandler oldHandler) {
        HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
        this.enterStage("play", timeouts.getPlay());
    }

    protected void registerHandlers() {
        this.registerHandler(1, p -> this.handle((Disconnect)p));
        this.registerHandler(3, p -> this.handlePong((Pong)p));
        this.registerHandler(108, p -> this.handle((ClientMovement)p));
        this.registerHandler(211, p -> this.handle((ChatMessage)p));
        this.registerHandler(23, p -> this.handle((RequestAssets)p));
        this.registerHandler(219, p -> this.handle((CustomPageEvent)p));
        this.registerHandler(32, p -> this.handle((ViewRadius)p));
        this.registerHandler(232, p -> this.handle((UpdateLanguage)p));
        this.registerHandler(111, p -> this.handle((MouseInteraction)p));
        this.registerHandler(251, p -> this.handle((UpdateServerAccess)p));
        this.registerHandler(252, p -> this.handle((SetServerAccess)p));
        this.registerHandler(204, p -> this.handle((ClientOpenWindow)p));
        this.registerHandler(203, p -> this.handle((SendWindowAction)p));
        this.registerHandler(202, p -> this.handle((CloseWindow)p));
        this.registerHandler(260, p -> this.handle((RequestMachinimaActorModel)p));
        this.registerHandler(262, p -> this.handle((UpdateMachinimaScene)p));
        this.registerHandler(105, p -> this.handle((ClientReady)p));
        this.registerHandler(166, p -> this.handle((MountMovement)p));
        this.registerHandler(116, p -> this.handle((SyncPlayerPreferences)p));
        this.registerHandler(117, p -> this.handle((ClientPlaceBlock)p));
        this.registerHandler(119, p -> this.handle((RemoveMapMarker)p));
        this.registerHandler(243, p -> this.handle((UpdateWorldMapVisible)p));
        this.registerHandler(244, p -> this.handle((TeleportToWorldMapMarker)p));
        this.registerHandler(245, p -> this.handle((TeleportToWorldMapPosition)p));
        this.registerHandler(290, p -> this.handle((SyncInteractionChains)p));
        this.registerHandler(158, p -> this.handle((SetPaused)p));
        this.registerHandler(282, p -> this.handle((RequestFlyCameraMode)p));
        this.packetHandlers.forEach(SubPacketHandler::registerHandlers);
    }

    @Override
    public void closed(ChannelHandlerContext ctx) {
        super.closed(ctx);
        Universe.get().removePlayer(this.playerRef);
    }

    @Override
    public void disconnect(@Nonnull String message) {
        this.disconnectReason.setServerDisconnectReason(message);
        if (this.playerRef != null) {
            HytaleLogger.getLogger().at(Level.INFO).log("Disconnecting %s at %s with the message: %s", this.playerRef.getUsername(), NettyUtil.formatRemoteAddress(this.channel), message);
            this.disconnect0(message);
            Universe.get().removePlayer(this.playerRef);
        } else {
            super.disconnect(message);
        }
    }

    public void handle(@Nonnull Disconnect packet) {
        this.disconnectReason.setClientDisconnectType(packet.type);
        HytaleLogger.getLogger().at(Level.INFO).log("%s - %s at %s left with reason: %s - %s", this.playerRef.getUuid(), this.playerRef.getUsername(), NettyUtil.formatRemoteAddress(this.channel), packet.type.name(), packet.reason);
        ProtocolUtil.closeApplicationConnection(this.channel);
    }

    public void handle(@Nonnull MouseInteraction packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            InteractionModule.get().doMouseInteraction(ref, store, packet, playerComponent, this.playerRef);
        });
    }

    public void handle(@Nonnull ClientMovement packet) {
        if (packet.absolutePosition != null && !ValidateUtil.isSafePosition(packet.absolutePosition)) {
            this.disconnect("Sent impossible position data!");
            return;
        }
        if (packet.bodyOrientation != null && !ValidateUtil.isSafeDirection(packet.bodyOrientation) || packet.lookOrientation != null && !ValidateUtil.isSafeDirection(packet.lookOrientation)) {
            this.disconnect("Sent impossible orientation data!");
            return;
        }
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            PendingTeleport pendingTeleport;
            if (!ref.isValid()) {
                return;
            }
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            if (playerComponent.isWaitingForClientReady()) {
                return;
            }
            PlayerInput playerInputComponent = store.getComponent(ref, PlayerInput.getComponentType());
            if (playerInputComponent == null) {
                return;
            }
            if (packet.movementStates != null) {
                playerInputComponent.queue(new PlayerInput.SetMovementStates(packet.movementStates));
            }
            if (packet.velocity != null) {
                playerInputComponent.queue(new PlayerInput.SetClientVelocity(packet.velocity));
            }
            if ((pendingTeleport = store.getComponent(ref, PendingTeleport.getComponentType())) != null) {
                if (packet.teleportAck == null) {
                    return;
                }
                switch (pendingTeleport.validate(packet.teleportAck.teleportId, packet.absolutePosition)) {
                    case OK: {
                        break;
                    }
                    case INVALID_ID: {
                        this.disconnect("Incorrect teleportId");
                        return;
                    }
                    case INVALID_POSITION: {
                        this.disconnect("Invalid teleport");
                        return;
                    }
                }
                if (!pendingTeleport.isEmpty()) {
                    return;
                }
                store.removeComponent(ref, PendingTeleport.getComponentType());
            }
            if (packet.mountedTo != 0) {
                if (packet.mountedTo != playerInputComponent.getMountId()) {
                    return;
                }
                if (packet.riderMovementStates != null) {
                    playerInputComponent.queue(new PlayerInput.SetRiderMovementStates(packet.riderMovementStates));
                }
            }
            if (packet.bodyOrientation != null) {
                playerInputComponent.queue(new PlayerInput.SetBody(packet.bodyOrientation));
            }
            if (packet.lookOrientation != null) {
                playerInputComponent.queue(new PlayerInput.SetHead(packet.lookOrientation));
            }
            if (packet.wishMovement != null) {
                playerInputComponent.queue(new PlayerInput.WishMovement(packet.wishMovement.x, packet.wishMovement.y, packet.wishMovement.z));
            }
            if (packet.absolutePosition != null) {
                playerInputComponent.queue(new PlayerInput.AbsoluteMovement(packet.absolutePosition.x, packet.absolutePosition.y, packet.absolutePosition.z));
            } else if (packet.relativePosition != null && (packet.relativePosition.x != 0 || packet.relativePosition.y != 0 || packet.relativePosition.z != 0 || packet.movementStates != null)) {
                playerInputComponent.queue(new PlayerInput.RelativeMovement((double)packet.relativePosition.x / 10000.0, (double)packet.relativePosition.y / 10000.0, (double)packet.relativePosition.z / 10000.0));
            }
        });
    }

    public void handle(@Nonnull ChatMessage packet) {
        if (packet.message == null || packet.message.isEmpty()) {
            this.disconnect("Invalid chat message packet! Message was empty.");
            return;
        }
        String message = packet.message;
        char firstChar = message.charAt(0);
        if (firstChar == '/') {
            CommandManager.get().handleCommand(this.playerComponent, message.substring(1));
        } else if (firstChar == '.') {
            this.playerRef.sendMessage(Message.translation("server.io.gamepackethandler.localCommandDenied").param("msg", message));
        } else {
            Ref<EntityStore> ref = this.playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                return;
            }
            UUID playerUUID = this.playerRef.getUuid();
            ObjectArrayList<PlayerRef> targetPlayerRefs = new ObjectArrayList<PlayerRef>(Universe.get().getPlayers());
            targetPlayerRefs.removeIf(targetPlayerRef -> targetPlayerRef.getHiddenPlayersManager().isPlayerHidden(playerUUID));
            HytaleServer.get().getEventBus().dispatchForAsync(PlayerChatEvent.class).dispatch(new PlayerChatEvent(this.playerRef, targetPlayerRefs, message)).whenComplete((playerChatEvent, throwable) -> {
                if (throwable != null) {
                    ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause((Throwable)throwable)).log("An error occurred while dispatching PlayerChatEvent for player %s", this.playerRef.getUsername());
                    return;
                }
                if (playerChatEvent.isCancelled()) {
                    return;
                }
                Message sentMessage = playerChatEvent.getFormatter().format(this.playerRef, playerChatEvent.getContent());
                HytaleLogger.getLogger().at(Level.INFO).log(MessageUtil.toAnsiString(sentMessage).toAnsi(ConsoleModule.get().getTerminal()));
                for (PlayerRef targetPlayerRef : playerChatEvent.getTargets()) {
                    targetPlayerRef.sendMessage(sentMessage);
                }
            });
        }
    }

    public void handle(@Nonnull RequestAssets packet) {
        CommonAssetModule.get().sendAssetsToPlayer((PacketHandler)this, packet.assets, true);
    }

    public void handle(@Nonnull CustomPageEvent packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            this.playerRef.getPacketHandler().writeNoCache(new SetPage(Page.None, true));
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            PageManager pageManager = playerComponent.getPageManager();
            pageManager.handleEvent(ref, store, packet);
        });
    }

    public void handle(@Nonnull ViewRadius packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            EntityTrackerSystems.EntityViewer entityViewerComponent = store.getComponent(ref, EntityTrackerSystems.EntityViewer.getComponentType());
            assert (entityViewerComponent != null);
            int viewRadiusChunks = MathUtil.ceil((float)packet.value / 32.0f);
            playerComponent.setClientViewRadius(viewRadiusChunks);
            entityViewerComponent.viewRadiusBlocks = playerComponent.getViewRadius() * 32;
        });
    }

    public void handle(@Nonnull UpdateLanguage packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        this.playerRef.setLanguage(packet.language);
        I18nModule.get().sendTranslations(this, packet.language);
    }

    protected void handle(@Nonnull ClientOpenWindow packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Supplier<? extends Window> supplier = Window.CLIENT_REQUESTABLE_WINDOW_TYPES.get((Object)packet.type);
        if (supplier == null) {
            throw new RuntimeException("Unable to process ClientOpenWindow packet. Window type is not supported!");
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            UpdateWindow updateWindowPacket = playerComponent.getWindowManager().clientOpenWindow(ref, (Window)supplier.get(), store);
            if (updateWindowPacket != null) {
                this.writeNoCache(updateWindowPacket);
            }
        });
    }

    public void handle(@Nonnull SendWindowAction packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            ValidatedWindow validatedWindow;
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            Window window = playerComponent.getWindowManager().getWindow(packet.id);
            if (window == null) {
                return;
            }
            if (window instanceof ValidatedWindow && !(validatedWindow = (ValidatedWindow)((Object)window)).validate(ref, store)) {
                window.close(ref, store);
                return;
            }
            window.handleAction(this.playerRef.getReference(), store, packet.action);
        });
    }

    public void handle(@Nonnull SyncPlayerPreferences packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            ComponentType<EntityStore, PlayerSettings> componentType = EntityModule.get().getPlayerSettingsComponentType();
            store.putComponent(ref, componentType, new PlayerSettings(packet.showEntityMarkers, packet.armorItemsPreferredPickupLocation, packet.weaponAndToolItemsPreferredPickupLocation, packet.usableItemsItemsPreferredPickupLocation, packet.solidBlockItemsPreferredPickupLocation, packet.miscItemsPreferredPickupLocation, new PlayerCreativeSettings(packet.allowNPCDetection, packet.respondToHit), packet.hideHelmet, packet.hideCuirass, packet.hideGauntlets, packet.hidePants));
            store.getComponent(ref, Player.getComponentType()).invalidateEquipmentNetwork();
        });
    }

    public void handle(@Nonnull ClientPlaceBlock packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Vector3d blockCenter;
            Vector3d position;
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            Inventory inventory = playerComponent.getInventory();
            Vector3i targetBlock = new Vector3i(packet.position.x, packet.position.y, packet.position.z);
            BlockRotation blockRotation = new BlockRotation(packet.rotation.rotationYaw, packet.rotation.rotationPitch, packet.rotation.rotationRoll);
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            if (transformComponent != null && playerComponent.getGameMode() != GameMode.Creative && (position = transformComponent.getPosition()).distanceSquaredTo(blockCenter = new Vector3d((double)targetBlock.x + 0.5, (double)targetBlock.y + 0.5, (double)targetBlock.z + 0.5)) > 36.0) {
                return;
            }
            Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
            long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
            Ref<ChunkStore> chunkReference = chunkStore.getExternalData().getChunkReference(chunkIndex);
            if (chunkReference == null) {
                return;
            }
            BlockChunk blockChunk = chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
            if (blockChunk == null) {
                return;
            }
            BlockSection section = blockChunk.getSectionAtBlockY(targetBlock.y);
            if (section == null) {
                return;
            }
            ItemStack itemInHand = playerComponent.getInventory().getItemInHand();
            if (itemInHand == null) {
                section.invalidateBlock(targetBlock.x, targetBlock.y, targetBlock.z);
                return;
            }
            String heldBlockKey = itemInHand.getBlockKey();
            if (heldBlockKey == null) {
                section.invalidateBlock(targetBlock.x, targetBlock.y, targetBlock.z);
                return;
            }
            if (packet.placedBlockId != -1) {
                String clientPlacedBlockTypeKey = BlockType.getAssetMap().getAsset(packet.placedBlockId).getId();
                BlockType heldBlockType = (BlockType)BlockType.getAssetMap().getAsset(heldBlockKey);
                if (heldBlockType != null && BlockPlaceUtils.canPlaceBlock(heldBlockType, clientPlacedBlockTypeKey)) {
                    heldBlockKey = clientPlacedBlockTypeKey;
                }
            }
            BlockPlaceUtils.placeBlock(ref, itemInHand, heldBlockKey, inventory.getHotbar(), Vector3i.ZERO, targetBlock, blockRotation, inventory, inventory.getActiveHotbarSlot(), playerComponent.getGameMode() != GameMode.Creative, chunkReference, chunkStore, store);
        });
    }

    public void handle(@Nonnull RemoveMapMarker packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
            perWorldData.removeLastDeath(packet.markerId);
        });
    }

    public void handle(@Nonnull CloseWindow packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            playerComponent.getWindowManager().closeWindow(ref, packet.id, store);
        });
    }

    public void handle(@Nonnull UpdateServerAccess packet) {
        if (!Constants.SINGLEPLAYER) {
            throw new IllegalArgumentException("UpdateServerAccess can only be used in singleplayer!");
        }
        if (!SingleplayerModule.isOwner(this.playerRef)) {
            throw new IllegalArgumentException("UpdateServerAccess can only be by the owner of the singleplayer world!");
        }
        CopyOnWriteArrayList<InetSocketAddress> publicAddresses = new CopyOnWriteArrayList<InetSocketAddress>();
        for (HostAddress host : packet.hosts) {
            publicAddresses.add(InetSocketAddress.createUnresolved(host.host, host.port & 0xFFFF));
        }
        SingleplayerModule singleplayerModule = SingleplayerModule.get();
        singleplayerModule.setPublicAddresses(publicAddresses);
        singleplayerModule.updateAccess(packet.access);
    }

    public void handle(@Nonnull SetServerAccess packet) {
        if (!Constants.SINGLEPLAYER) {
            throw new IllegalArgumentException("SetServerAccess can only be used in singleplayer!");
        }
        if (!SingleplayerModule.isOwner(this.playerRef)) {
            throw new IllegalArgumentException("SetServerAccess can only be used by the owner of the singleplayer world!");
        }
        HytaleServerConfig config = HytaleServer.get().getConfig();
        if (config != null) {
            config.setPassword(packet.password != null ? packet.password : "");
            HytaleServerConfig.save(config);
        }
        SingleplayerModule.get().requestServerAccess(packet.access);
    }

    public void handle(@Nonnull RequestMachinimaActorModel packet) {
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(packet.modelId);
        this.writeNoCache(new SetMachinimaActorModel(Model.createUnitScaleModel(modelAsset).toPacket(), packet.sceneName, packet.actorName));
    }

    public void handle(@Nonnull UpdateMachinimaScene packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            UpdateMachinimaScene updatePacket = new UpdateMachinimaScene(this.playerRef.getUsername(), packet.sceneName, packet.frame, packet.updateType, packet.scene);
            if ("*".equals(packet.player)) {
                for (PlayerRef otherPlayerRef : world.getPlayerRefs()) {
                    if (Objects.equals(otherPlayerRef, this.playerRef)) continue;
                    otherPlayerRef.getPacketHandler().writeNoCache(updatePacket);
                }
                this.playerRef.sendMessage(Message.translation("server.io.gamepackethandler.sceneUpdateSent"));
            } else {
                PlayerRef target = NameMatching.DEFAULT.find(Universe.get().getPlayers(), packet.player, PlayerRef::getUsername);
                if (target != null && target.getReference().getStore().getExternalData().getWorld() == world) {
                    target.getPacketHandler().write((Packet)updatePacket);
                    this.playerRef.sendMessage(Message.translation("server.io.gamepackethander.sceneUpdateSentToPlayer").param("name", target.getUsername()));
                } else {
                    this.playerRef.sendMessage(Message.translation("server.io.gamepackethandler.playerNotFound").param("name", packet.player));
                }
            }
        });
    }

    public void handle(@Nonnull ClientReady packet) {
        HytaleLogger.getLogger().at(Level.WARNING).log("%s: Received %s", (Object)this.getIdentifier(), (Object)packet);
        CompletableFuture future = this.clientReadyForChunksFuture;
        if (packet.readyForChunks && !packet.readyForGameplay && future != null) {
            this.clientReadyForChunksFutureStack = null;
            this.clientReadyForChunksFuture = null;
            future.completeAsync(() -> null);
        }
        if (packet.readyForGameplay) {
            Ref<EntityStore> ref = this.playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                return;
            }
            Store<EntityStore> store = ref.getStore();
            World world = store.getExternalData().getWorld();
            world.execute(() -> {
                Player playerComponent = store.getComponent(ref, Player.getComponentType());
                assert (playerComponent != null);
                playerComponent.handleClientReady(false);
            });
        }
    }

    public void handle(@Nonnull UpdateWorldMapVisible packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            playerComponent.getWorldMapTracker().setClientHasWorldMapVisible(packet.visible);
        });
    }

    public void handle(@Nonnull TeleportToWorldMapMarker packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            WorldMapTracker worldMapTracker = playerComponent.getWorldMapTracker();
            if (!worldMapTracker.isAllowTeleportToMarkers()) {
                this.disconnect("You are not allowed to use TeleportToWorldMapMarker!");
                return;
            }
            MapMarker marker = worldMapTracker.getSentMarkers().get(packet.id);
            if (marker != null) {
                Transform transform = PositionUtil.toTransform(marker.transform);
                Teleport teleportComponent = Teleport.createForPlayer(transform);
                world.getEntityStore().getStore().addComponent(this.playerRef.getReference(), Teleport.getComponentType(), teleportComponent);
            }
        });
    }

    public void handle(@Nonnull TeleportToWorldMapPosition packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            WorldMapTracker worldMapTracker = playerComponent.getWorldMapTracker();
            if (!worldMapTracker.isAllowTeleportToCoordinates()) {
                this.disconnect("You are not allowed to use TeleportToWorldMapMarker!");
                return;
            }
            world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunkFromBlock(packet.x, packet.y)).thenAcceptAsync(chunkRef -> {
                BlockChunk blockChunkComponent = world.getChunkStore().getStore().getComponent((Ref<ChunkStore>)chunkRef, BlockChunk.getComponentType());
                assert (blockChunkComponent != null);
                Vector3d position = new Vector3d(packet.x, blockChunkComponent.getHeight(packet.x, packet.y) + 2, packet.y);
                Teleport teleportComponent = Teleport.createForPlayer(null, position, new Vector3f(0.0f, 0.0f, 0.0f));
                world.getEntityStore().getStore().addComponent(this.playerRef.getReference(), Teleport.getComponentType(), teleportComponent);
            }, (Executor)world);
        });
    }

    public void handle(@Nonnull SyncInteractionChains packet) {
        Collections.addAll(this.interactionPacketQueue, packet.updates);
    }

    public void handle(@Nonnull MountMovement packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(playerComponent.getMountEntityId());
            if (entityReference == null || !entityReference.isValid()) {
                return;
            }
            TransformComponent transformComponent = store.getComponent(entityReference, TransformComponent.getComponentType());
            assert (transformComponent != null);
            transformComponent.setPosition(PositionUtil.toVector3d(packet.absolutePosition));
            transformComponent.setRotation(PositionUtil.toRotation(packet.bodyOrientation));
            MovementStatesComponent movementStatesComponent = store.getComponent(entityReference, MovementStatesComponent.getComponentType());
            assert (movementStatesComponent != null);
            movementStatesComponent.setMovementStates(packet.movementStates);
        });
    }

    public void handle(@Nonnull SetPaused packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            if (world.getPlayerCount() != 1 || !Constants.SINGLEPLAYER) {
                return;
            }
            world.setPaused(packet.paused);
        });
    }

    public void handle(@Nonnull RequestFlyCameraMode packet) {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        world.execute(() -> {
            Player playerComponent = store.getComponent(ref, Player.getComponentType());
            assert (playerComponent != null);
            if (playerComponent.hasPermission("hytale.camera.flycam")) {
                this.writeNoCache(new SetFlyCameraMode(packet.entering));
                if (packet.entering) {
                    this.playerRef.sendMessage(Message.translation("server.general.flyCamera.enabled"));
                } else {
                    this.playerRef.sendMessage(Message.translation("server.general.flyCamera.disabled"));
                }
            } else {
                this.playerRef.sendMessage(Message.translation("server.general.flyCamera.noPermission"));
            }
        });
    }
}

