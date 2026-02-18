/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap;

import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.common.util.CompletableFutureUtil;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.packets.player.RemoveMapMarker;
import com.hypixel.hytale.protocol.packets.worldmap.CreateUserMarker;
import com.hypixel.hytale.protocol.packets.worldmap.MapImage;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapSettings;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MarkersCollector;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.DeathMarkerProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.OtherPlayersMarkerProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.POIMarkerProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.PersonalMarkersProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.RespawnMarkerProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.SharedMarkersProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.SpawnMarkerProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarker;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMapMarkersStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.user.UserMarkerValidator;
import com.hypixel.hytale.server.core.universe.world.worldmap.markers.worldstore.WorldMarkersResource;
import com.hypixel.hytale.server.core.util.thread.TickingThread;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldMapManager
extends TickingThread {
    private static final int IMAGE_KEEP_ALIVE = 60;
    private static final float DEFAULT_UNLOAD_DELAY = 1.0f;
    @Nonnull
    private final HytaleLogger logger;
    @Nonnull
    private final World world;
    private final Long2ObjectConcurrentHashMap<ImageEntry> images = new Long2ObjectConcurrentHashMap(true, ChunkUtil.indexChunk(Integer.MIN_VALUE, Integer.MIN_VALUE));
    private final Long2ObjectConcurrentHashMap<CompletableFuture<MapImage>> generating = new Long2ObjectConcurrentHashMap(true, ChunkUtil.indexChunk(Integer.MIN_VALUE, Integer.MIN_VALUE));
    private final Map<String, MarkerProvider> markerProviders = new ConcurrentHashMap<String, MarkerProvider>();
    private final Map<String, MapMarker> pointsOfInterest = new ConcurrentHashMap<String, MapMarker>();
    @Nonnull
    private WorldMapSettings worldMapSettings = WorldMapSettings.DISABLED;
    @Nullable
    private IWorldMap generator;
    @Nonnull
    private CompletableFuture<Void> generatorLoaded = new CompletableFuture();
    private float unloadDelay = 1.0f;

    public WorldMapManager(@Nonnull World world) {
        super("WorldMap - " + world.getName(), 10, true);
        this.logger = HytaleLogger.get("World|" + world.getName() + "|M");
        this.world = world;
        this.addMarkerProvider("spawn", SpawnMarkerProvider.INSTANCE);
        this.addMarkerProvider("playerIcons", OtherPlayersMarkerProvider.INSTANCE);
        this.addMarkerProvider("death", DeathMarkerProvider.INSTANCE);
        this.addMarkerProvider("respawn", RespawnMarkerProvider.INSTANCE);
        this.addMarkerProvider("personal", PersonalMarkersProvider.INSTANCE);
        this.addMarkerProvider("shared", SharedMarkersProvider.INSTANCE);
        this.addMarkerProvider("poi", POIMarkerProvider.INSTANCE);
    }

    @Nullable
    public IWorldMap getGenerator() {
        return this.generator;
    }

    public void setGenerator(@Nullable IWorldMap generator) {
        boolean before = this.shouldTick();
        if (this.generator != null) {
            this.generator.shutdown();
        }
        this.generator = generator;
        if (generator != null) {
            this.logger.at(Level.INFO).log("Initializing world map generator: %s", generator.toString());
            this.generatorLoaded.complete(null);
            this.generatorLoaded = new CompletableFuture();
            this.worldMapSettings = generator.getWorldMapSettings();
            this.images.clear();
            this.generating.clear();
            for (Player worldPlayer : this.world.getPlayers()) {
                worldPlayer.getWorldMapTracker().clear();
            }
            this.updateTickingState(before);
            this.sendSettings();
            this.logger.at(Level.INFO).log("Generating Points of Interest...");
            CompletableFutureUtil._catch(generator.generatePointsOfInterest(this.world).thenAcceptAsync(pointsOfInterest -> {
                this.pointsOfInterest.putAll((Map<String, MapMarker>)pointsOfInterest);
                this.logger.at(Level.INFO).log("Finished Generating Points of Interest!");
            }));
        } else {
            this.logger.at(Level.INFO).log("World map disabled!");
            this.worldMapSettings = WorldMapSettings.DISABLED;
            this.sendSettings();
        }
    }

    @Override
    protected boolean isIdle() {
        return this.world.getPlayerCount() == 0;
    }

    @Override
    protected void tick(float dt) {
        for (Player player : this.world.getPlayers()) {
            player.getWorldMapTracker().tick(dt);
        }
        this.unloadDelay -= dt;
        if (this.unloadDelay <= 0.0f) {
            this.unloadDelay = 1.0f;
            this.unloadImages();
        }
    }

    @Override
    protected void onShutdown() {
    }

    public void unloadImages() {
        int toRemoveSize;
        int imagesCount = this.images.size();
        if (imagesCount == 0) {
            return;
        }
        List<Player> players = this.world.getPlayers();
        LongOpenHashSet toRemove = new LongOpenHashSet();
        this.images.forEach((index, chunk) -> {
            if (this.isWorldMapEnabled() && WorldMapManager.isWorldMapImageVisibleToAnyPlayer(players, index, this.worldMapSettings)) {
                chunk.keepAlive.set(60);
                return;
            }
            if (chunk.keepAlive.decrementAndGet() <= 0) {
                toRemove.add(index);
            }
        });
        if (!toRemove.isEmpty()) {
            toRemove.forEach(value -> {
                this.logger.at(Level.FINE).log("Unloading world map image: %s", value);
                this.images.remove(value);
            });
        }
        if ((toRemoveSize = toRemove.size()) > 0) {
            this.logger.at(Level.FINE).log("Cleaned %s world map images from memory, with %s images remaining in memory.", toRemoveSize, imagesCount - toRemoveSize);
        }
    }

    public boolean isWorldMapEnabled() {
        return this.worldMapSettings.getSettingsPacket().enabled;
    }

    public static boolean isWorldMapImageVisibleToAnyPlayer(@Nonnull List<Player> players, long imageIndex, @Nonnull WorldMapSettings settings) {
        for (Player player : players) {
            int viewRadius = settings.getViewRadius(player.getViewRadius());
            if (!player.getWorldMapTracker().shouldBeVisible(viewRadius, imageIndex)) continue;
            return true;
        }
        return false;
    }

    @Nonnull
    public World getWorld() {
        return this.world;
    }

    @Nonnull
    public WorldMapSettings getWorldMapSettings() {
        return this.worldMapSettings;
    }

    public Map<String, MarkerProvider> getMarkerProviders() {
        return this.markerProviders;
    }

    public void addMarkerProvider(@Nonnull String key, @Nonnull MarkerProvider provider) {
        this.markerProviders.put(key, provider);
    }

    public Map<String, MapMarker> getPointsOfInterest() {
        return this.pointsOfInterest;
    }

    @Nullable
    public MapImage getImageIfInMemory(int x, int z) {
        return this.getImageIfInMemory(ChunkUtil.indexChunk(x, z));
    }

    @Nullable
    public MapImage getImageIfInMemory(long index) {
        ImageEntry pair = this.images.get(index);
        return pair != null ? pair.image : null;
    }

    @Nonnull
    public CompletableFuture<MapImage> getImageAsync(int x, int z) {
        return this.getImageAsync(ChunkUtil.indexChunk(x, z));
    }

    @Nonnull
    public CompletableFuture<MapImage> getImageAsync(long index) {
        MapImage image;
        ImageEntry pair = this.images.get(index);
        MapImage mapImage = image = pair != null ? pair.image : null;
        if (image != null) {
            return CompletableFuture.completedFuture(image);
        }
        CompletableFuture<MapImage> gen = this.generating.get(index);
        if (gen != null) {
            return gen;
        }
        int imageSize = MathUtil.fastFloor(32.0f * this.worldMapSettings.getImageScale());
        LongOpenHashSet chunksToGenerate = new LongOpenHashSet();
        chunksToGenerate.add(index);
        CompletableFuture<MapImage> future = CompletableFutureUtil._catch(this.generator.generate(this.world, imageSize, imageSize, chunksToGenerate).thenApplyAsync(worldMap -> {
            MapImage newImage = (MapImage)worldMap.getChunks().get(index);
            if (this.generating.remove(index) != null) {
                this.images.put(index, new ImageEntry(newImage));
            }
            return newImage;
        }));
        this.generating.put(index, future);
        return future;
    }

    public void generate() {
    }

    public void sendSettings() {
        for (Player player : this.world.getPlayers()) {
            player.getWorldMapTracker().sendSettings(this.world);
        }
    }

    public boolean shouldTick() {
        return this.world.getWorldConfig().isCompassUpdating() || this.isWorldMapEnabled();
    }

    public void updateTickingState(boolean before) {
        boolean after = this.shouldTick();
        if (before != after) {
            if (after) {
                this.start();
            } else {
                this.stop();
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void handleUserCreateMarker(PlayerRef playerRef, CreateUserMarker packet) {
        String string;
        UserMapMarkersStore markersStore;
        block6: {
            DisplayNameComponent displayNameComponent;
            block4: {
                block5: {
                    UserMarkerValidator.PlaceResult validation;
                    block3: {
                        Ref<EntityStore> ref = playerRef.getReference();
                        if (ref == null) {
                            return;
                        }
                        validation = UserMarkerValidator.validatePlacing(ref, packet);
                        if (!(validation instanceof UserMarkerValidator.CanSpawn)) break block3;
                        UserMarkerValidator.CanSpawn canSpawn = (UserMarkerValidator.CanSpawn)validation;
                        Store<EntityStore> store = ref.getStore();
                        markersStore = canSpawn.markersStore();
                        displayNameComponent = store.getComponent(ref, DisplayNameComponent.getComponentType());
                        if (displayNameComponent != null) break block4;
                        break block5;
                    }
                    if (!(validation instanceof UserMarkerValidator.Fail)) return;
                    UserMarkerValidator.Fail fail = (UserMarkerValidator.Fail)validation;
                    try {
                        Message message;
                        Message errorMsg = message = fail.errorMsg();
                        playerRef.sendMessage(errorMsg.color("#ffc800"));
                        return;
                    }
                    catch (Throwable throwable) {
                        throw new MatchException(throwable.toString(), throwable);
                    }
                }
                string = playerRef.getUsername();
                break block6;
            }
            string = displayNameComponent.getDisplayName().getRawText();
        }
        String createdByName = string;
        UserMapMarker userMapMarker = new UserMapMarker();
        userMapMarker.setId("user_" + (packet.shared ? "shared" : "personal") + "_" + String.valueOf(UUID.randomUUID()));
        userMapMarker.setPosition(packet.x, packet.z);
        userMapMarker.setName(packet.name);
        userMapMarker.setIcon(packet.markerImage == null ? "User1.png" : packet.markerImage);
        userMapMarker.setColorTint(packet.tintColor == null ? new Color(0, 0, 0) : packet.tintColor);
        userMapMarker.withCreatedByName(createdByName);
        userMapMarker.withCreatedByUuid(playerRef.getUuid());
        markersStore.addUserMapMarker(userMapMarker);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void handleUserRemoveMarker(PlayerRef playerRef, RemoveMapMarker packet) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
        boolean removedDeathMarker = perWorldData.removeLastDeath(packet.markerId);
        if (removedDeathMarker) {
            return;
        }
        MarkerAndItsStore userMarkerAndStore = this.findUserMapMarker(packet.markerId, player, world);
        if (userMarkerAndStore == null) {
            playerRef.sendMessage(Message.translation("server.worldmap.markers.edit.notFound").color("#ffc800"));
            HytaleLogger.getLogger().at(Level.WARNING).log("Couldn't find marker to remove '" + packet.markerId + "' from " + playerRef.getUsername() + " " + String.valueOf(playerRef.getUuid()));
            return;
        }
        UserMarkerValidator.RemoveResult validation = UserMarkerValidator.validateRemove(ref, userMarkerAndStore.marker);
        if (!(validation instanceof UserMarkerValidator.CanRemove)) {
            if (!(validation instanceof UserMarkerValidator.Fail)) return;
            UserMarkerValidator.Fail fail = (UserMarkerValidator.Fail)validation;
            try {
                Message message;
                Message errorMsg = message = fail.errorMsg();
                playerRef.sendMessage(errorMsg.color("#ffc800"));
            }
            catch (Throwable throwable) {
                throw new MatchException(throwable.toString(), throwable);
            }
            return;
        }
        userMarkerAndStore.store.removeUserMapMarker(userMarkerAndStore.marker.getId());
    }

    @Nullable
    private MarkerAndItsStore findUserMapMarker(String markerId, Player player, World world) {
        PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
        UserMapMarker personalMarker = perWorldData.getUserMapMarker(markerId);
        if (personalMarker != null) {
            return new MarkerAndItsStore(personalMarker, perWorldData);
        }
        WorldMarkersResource sharedMarkers = world.getChunkStore().getStore().getResource(WorldMarkersResource.getResourceType());
        UserMapMarker sharedMarker = sharedMarkers.getUserMapMarker(markerId);
        if (sharedMarker != null) {
            return new MarkerAndItsStore(sharedMarker, sharedMarkers);
        }
        return null;
    }

    public void clearImages() {
        this.images.clear();
        this.generating.clear();
    }

    public void clearImagesInChunks(@Nonnull LongSet chunkIndices) {
        chunkIndices.forEach(index -> {
            this.images.remove(index);
            this.generating.remove(index);
        });
    }

    public static void sendSettingsToAllWorlds() {
        for (World world : Universe.get().getWorlds().values()) {
            world.execute(() -> world.getWorldMapManager().sendSettings());
        }
    }

    static {
        MarkerReference.CODEC.register("Player", (Class<MarkerReference>)PlayerMarkerReference.class, (Codec<MarkerReference>)PlayerMarkerReference.CODEC);
    }

    public static interface MarkerProvider {
        public void update(@Nonnull World var1, @Nonnull Player var2, @Nonnull MarkersCollector var3);
    }

    public static class ImageEntry {
        private final AtomicInteger keepAlive = new AtomicInteger();
        private final MapImage image;

        public ImageEntry(MapImage image) {
            this.image = image;
        }
    }

    private record MarkerAndItsStore(UserMapMarker marker, UserMapMarkersStore store) {
    }

    public static interface MarkerReference {
        public static final CodecMapCodec<MarkerReference> CODEC = new CodecMapCodec();

        public String getMarkerId();

        public void remove();
    }

    public static class PlayerMarkerReference
    implements MarkerReference {
        public static final BuilderCodec<PlayerMarkerReference> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlayerMarkerReference.class, PlayerMarkerReference::new).addField(new KeyedCodec<UUID>("Player", Codec.UUID_BINARY), (playerMarkerReference, uuid) -> {
            playerMarkerReference.player = uuid;
        }, playerMarkerReference -> playerMarkerReference.player)).addField(new KeyedCodec<String>("World", Codec.STRING), (playerMarkerReference, s) -> {
            playerMarkerReference.world = s;
        }, playerMarkerReference -> playerMarkerReference.world)).addField(new KeyedCodec<String>("MarkerId", Codec.STRING), (playerMarkerReference, s) -> {
            playerMarkerReference.markerId = s;
        }, playerMarkerReference -> playerMarkerReference.markerId)).build();
        private UUID player;
        private String world;
        private String markerId;

        private PlayerMarkerReference() {
        }

        public PlayerMarkerReference(@Nonnull UUID player, @Nonnull String world, @Nonnull String markerId) {
            this.player = player;
            this.world = world;
            this.markerId = markerId;
        }

        public UUID getPlayer() {
            return this.player;
        }

        @Override
        public String getMarkerId() {
            return this.markerId;
        }

        @Override
        public void remove() {
            PlayerRef playerRef = Universe.get().getPlayer(this.player);
            if (playerRef != null) {
                Player playerComponent = playerRef.getComponent(Player.getComponentType());
                this.removeMarkerFromOnlinePlayer(playerComponent);
            } else {
                this.removeMarkerFromOfflinePlayer();
            }
        }

        private void removeMarkerFromOnlinePlayer(@Nonnull Player player) {
            PlayerConfigData data = player.getPlayerConfigData();
            String world = this.world;
            if (world == null) {
                world = player.getWorld().getName();
            }
            PlayerMarkerReference.removeMarkerFromData(data, world, this.markerId);
        }

        private void removeMarkerFromOfflinePlayer() {
            ((CompletableFuture)Universe.get().getPlayerStorage().load(this.player).thenApply(holder -> {
                Player player = holder.getComponent(Player.getComponentType());
                PlayerConfigData data = player.getPlayerConfigData();
                String world = this.world;
                if (world == null) {
                    world = data.getWorld();
                }
                PlayerMarkerReference.removeMarkerFromData(data, world, this.markerId);
                return holder;
            })).thenCompose(holder -> Universe.get().getPlayerStorage().save(this.player, (Holder<EntityStore>)holder));
        }

        @Nullable
        private static void removeMarkerFromData(@Nonnull PlayerConfigData data, @Nonnull String worldName, @Nonnull String markerId) {
            PlayerWorldData perWorldData = data.getPerWorldData(worldName);
            ArrayList<? extends UserMapMarker> playerMarkers = new ArrayList<UserMapMarker>(perWorldData.getUserMapMarkers());
            playerMarkers.removeIf(marker -> markerId.equals(marker.getId()));
            perWorldData.setUserMapMarkers(playerMarkers);
        }
    }
}

