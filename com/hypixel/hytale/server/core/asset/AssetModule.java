/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset;

import com.hypixel.hytale.assetstore.AssetLoadResult;
import com.hypixel.hytale.assetstore.AssetMap;
import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.event.RegisterAssetStoreEvent;
import com.hypixel.hytale.assetstore.event.RemoveAssetStoreEvent;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.common.util.java.ManifestUtil;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.asset.AssetPackRegisterEvent;
import com.hypixel.hytale.server.core.asset.AssetPackUnregisterEvent;
import com.hypixel.hytale.server.core.asset.AssetRegistryLoader;
import com.hypixel.hytale.server.core.asset.LoadAssetEvent;
import com.hypixel.hytale.server.core.asset.monitor.AssetMonitor;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.HomeOrSpawnPoint;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.WorldSpawnPoint;
import com.hypixel.hytale.server.core.asset.type.item.DroplistCommand;
import com.hypixel.hytale.server.core.config.ModConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.BootEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.ValidatableWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.IWorldMapProvider;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetModule
extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(AssetModule.class).build();
    private static AssetModule instance;
    @Nullable
    private AssetMonitor assetMonitor;
    @Nonnull
    private final List<AssetPack> assetPacks = new CopyOnWriteArrayList<AssetPack>();
    private final List<ObjectBooleanPair<AssetPack>> pendingAssetPacks = new ArrayList<ObjectBooleanPair<AssetPack>>();
    private boolean hasSetup = false;
    private boolean hasLoaded = false;
    private final List<AssetStore<?, ?, ?>> pendingAssetStores = new CopyOnWriteArrayList();

    public static AssetModule get() {
        return instance;
    }

    public AssetModule(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        if (Options.getOptionSet().has(Options.DISABLE_FILE_WATCHER)) {
            this.getLogger().at(Level.WARNING).log("Not running asset watcher because --disable-file-watcher was set");
        } else {
            try {
                this.assetMonitor = new AssetMonitor();
                this.getLogger().at(Level.INFO).log("Asset monitor enabled!");
            }
            catch (IOException e) {
                ((HytaleLogger.Api)this.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to create asset monitor!");
            }
        }
        List<Path> paths = Options.getOptionSet().valuesOf(Options.ASSET_DIRECTORY);
        for (Path path : paths) {
            this.loadAndRegisterPack(path, false);
        }
        this.hasSetup = true;
        for (ObjectBooleanPair objectBooleanPair : this.pendingAssetPacks) {
            if (this.getAssetPack(((AssetPack)objectBooleanPair.left()).getName()) != null) {
                if (objectBooleanPair.rightBoolean()) {
                    this.getLogger().at(Level.WARNING).log("Asset pack with name '%s' already exists, skipping registration from path: %s", (Object)((AssetPack)objectBooleanPair.left()).getName(), (Object)((AssetPack)objectBooleanPair.left()).getRoot());
                    continue;
                }
                throw new IllegalStateException("Asset pack with name '" + ((AssetPack)objectBooleanPair.left()).getName() + "' already exists");
            }
            this.assetPacks.add((AssetPack)objectBooleanPair.left());
        }
        this.pendingAssetPacks.clear();
        this.loadPacksFromDirectory(PluginManager.MODS_PATH);
        for (Path path : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
            this.loadPacksFromDirectory(path);
        }
        if (this.assetPacks.isEmpty()) {
            HytaleServer.get().shutdownServer(ShutdownReason.MISSING_ASSETS.withMessage("Failed to load any asset packs"));
            return;
        }
        boolean hasOutdatedPacks = false;
        String string = ManifestUtil.getVersion();
        for (AssetPack pack : this.assetPacks) {
            PluginManifest manifest;
            String targetServerVersion;
            if (pack.getName().equals("Hytale:Hytale") || (targetServerVersion = (manifest = pack.getManifest()).getServerVersion()) != null && targetServerVersion.equals(string)) continue;
            hasOutdatedPacks = true;
            if (targetServerVersion == null || "*".equals(targetServerVersion)) {
                this.getLogger().at(Level.WARNING).log("Plugin '%s' does not specify a target server version. You may encounter issues, please check for plugin updates. This will be a hard error in the future", pack.getName());
                continue;
            }
            this.getLogger().at(Level.WARNING).log("Plugin '%s' targets a different server version %s. You may encounter issues, please check for plugin updates.", (Object)pack.getName(), (Object)string);
        }
        if (hasOutdatedPacks && System.getProperty("hytale.allow_outdated_mods") == null) {
            this.getLogger().at(Level.SEVERE).log("One or more asset packs are targeting an older server version. It is recommended to update these plugins to ensure compatibility.");
            try {
                if (!Constants.SINGLEPLAYER) {
                    Thread.sleep(Duration.ofSeconds(2L));
                }
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            HytaleServer.get().getEventBus().registerGlobal(AddPlayerToWorldEvent.class, event -> {
                PlayerRef playerRef = event.getHolder().getComponent(PlayerRef.getComponentType());
                Player player = event.getHolder().getComponent(Player.getComponentType());
                if (playerRef == null || player == null) {
                    return;
                }
                if (!player.hasPermission("hytale.mods.outdated.notify")) {
                    return;
                }
                playerRef.sendMessage(Message.translation("server.assetModule.outOfDatePacks").color(Color.RED));
            });
        }
        this.getEventRegistry().register((short)-16, LoadAssetEvent.class, event -> {
            if (this.hasLoaded) {
                throw new IllegalStateException("LoadAssetEvent has already been dispatched");
            }
            AssetRegistry.ASSET_LOCK.writeLock().lock();
            try {
                this.hasLoaded = true;
                AssetRegistryLoader.preLoadAssets(event);
                for (AssetPack pack : this.assetPacks) {
                    AssetRegistryLoader.loadAssets(event, pack);
                }
            }
            finally {
                AssetRegistry.ASSET_LOCK.writeLock().unlock();
            }
        });
        this.getEventRegistry().register((short)-16, AssetPackRegisterEvent.class, event -> AssetRegistryLoader.loadAssets(null, event.getAssetPack()));
        this.getEventRegistry().register(AssetPackUnregisterEvent.class, event -> {
            for (AssetStore<?, ?, ?> assetStore : AssetRegistry.getStoreMap().values()) {
                assetStore.removeAssetPack(event.getAssetPack().getName());
            }
        });
        this.getEventRegistry().register(LoadAssetEvent.class, AssetModule::validateWorldGen);
        this.getEventRegistry().register(EventPriority.FIRST, LoadAssetEvent.class, SneakyThrow.sneakyConsumer(AssetRegistryLoader::writeSchemas));
        this.getEventRegistry().register(RegisterAssetStoreEvent.class, this::onNewStore);
        this.getEventRegistry().register(RemoveAssetStoreEvent.class, this::onRemoveStore);
        this.getEventRegistry().registerGlobal(BootEvent.class, event -> {
            StringBuilder sb = new StringBuilder("Total Loaded Assets: ");
            AssetStore[] assetStores = (AssetStore[])AssetRegistry.getStoreMap().values().toArray(AssetStore[]::new);
            Arrays.sort(assetStores, Comparator.comparingInt(o -> ((AssetMap)o.getAssetMap()).getAssetCount()));
            for (int i = assetStores.length - 1; i >= 0; --i) {
                AssetStore assetStore = assetStores[i];
                String simpleName = assetStore.getAssetClass().getSimpleName();
                int assetCount = ((AssetMap)assetStore.getAssetMap()).getAssetCount();
                sb.append(simpleName).append(": ").append(assetCount).append(", ");
            }
            sb.setLength(sb.length() - 2);
            this.getLogger().at(Level.INFO).log(sb.toString());
        });
        RespawnController.CODEC.register("HomeOrSpawnPoint", (Class<RespawnController>)HomeOrSpawnPoint.class, (Codec<RespawnController>)HomeOrSpawnPoint.CODEC);
        RespawnController.CODEC.register("WorldSpawnPoint", (Class<RespawnController>)WorldSpawnPoint.class, (Codec<RespawnController>)WorldSpawnPoint.CODEC);
        this.getCommandRegistry().registerCommand(new DroplistCommand());
    }

    @Override
    protected void shutdown() {
        if (this.assetMonitor != null) {
            this.assetMonitor.shutdown();
            this.assetMonitor = null;
        }
        for (AssetPack pack : this.assetPacks) {
            if (pack.getFileSystem() == null) continue;
            try {
                pack.getFileSystem().close();
            }
            catch (IOException e) {
                ((HytaleLogger.Api)this.getLogger().at(Level.WARNING).withCause(e)).log("Failed to close asset pack filesystem: %s", pack.getName());
            }
        }
        this.assetPacks.clear();
    }

    @Nonnull
    public AssetPack getBaseAssetPack() {
        return this.assetPacks.getFirst();
    }

    @Nonnull
    public List<AssetPack> getAssetPacks() {
        return this.assetPacks;
    }

    @Nullable
    public AssetMonitor getAssetMonitor() {
        return this.assetMonitor;
    }

    @Nullable
    public AssetPack findAssetPackForPath(Path path) {
        path = path.toAbsolutePath().normalize();
        for (AssetPack pack : this.assetPacks) {
            if (path.getFileSystem() != pack.getRoot().getFileSystem() || !path.startsWith(pack.getRoot())) continue;
            return pack;
        }
        return null;
    }

    public boolean isWithinPackSubDir(@Nonnull Path path, @Nonnull String subDir) {
        for (AssetPack pack : this.assetPacks) {
            Path packSubDir = pack.getRoot().resolve(subDir);
            if (!PathUtil.isChildOf(packSubDir, path)) continue;
            return true;
        }
        return false;
    }

    public boolean isAssetPathImmutable(@Nonnull Path path) {
        AssetPack pack = this.findAssetPackForPath(path);
        return pack != null && pack.isImmutable();
    }

    @Nullable
    private PluginManifest loadPackManifest(Path packPath) throws IOException {
        block19: {
            Path manifestPath;
            if (packPath.getFileName().toString().toLowerCase().endsWith(".zip")) {
                try (FileSystem fs = FileSystems.newFileSystem(packPath, (ClassLoader)null);){
                    PluginManifest pluginManifest;
                    block20: {
                        Path manifestPath2 = fs.getPath("manifest.json", new String[0]);
                        if (!Files.exists(manifestPath2, new LinkOption[0])) break block19;
                        BufferedReader reader = Files.newBufferedReader(manifestPath2, StandardCharsets.UTF_8);
                        try {
                            char[] buffer = RawJsonReader.READ_BUFFER.get();
                            RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
                            ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                            PluginManifest manifest = PluginManifest.CODEC.decodeJson(rawJsonReader, extraInfo);
                            extraInfo.getValidationResults().logOrThrowValidatorExceptions(this.getLogger());
                            pluginManifest = manifest;
                            if (reader == null) break block20;
                        }
                        catch (Throwable buffer) {
                            if (reader != null) {
                                try {
                                    reader.close();
                                }
                                catch (Throwable rawJsonReader) {
                                    buffer.addSuppressed(rawJsonReader);
                                }
                            }
                            throw buffer;
                        }
                        reader.close();
                    }
                    return pluginManifest;
                }
            }
            if (Files.isDirectory(packPath, new LinkOption[0]) && Files.exists(manifestPath = packPath.resolve("manifest.json"), new LinkOption[0])) {
                try (FileReader reader = new FileReader(manifestPath.toFile(), StandardCharsets.UTF_8);){
                    char[] buffer = RawJsonReader.READ_BUFFER.get();
                    RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
                    ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                    PluginManifest manifest = PluginManifest.CODEC.decodeJson(rawJsonReader, extraInfo);
                    extraInfo.getValidationResults().logOrThrowValidatorExceptions(this.getLogger());
                    PluginManifest pluginManifest = manifest;
                    return pluginManifest;
                }
            }
        }
        return null;
    }

    private void loadPacksFromDirectory(Path modsPath) {
        if (!Files.isDirectory(modsPath, new LinkOption[0])) {
            return;
        }
        this.getLogger().at(Level.INFO).log("Loading packs from directory: %s", modsPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsPath);){
            for (Path packPath : stream) {
                if (packPath.getFileName() == null || packPath.getFileName().toString().toLowerCase().endsWith(".jar")) continue;
                this.loadAndRegisterPack(packPath, true);
            }
        }
        catch (IOException e) {
            ((HytaleLogger.Api)this.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to load mods from: %s", modsPath);
        }
    }

    private void loadAndRegisterPack(Path packPath, boolean isExternal) {
        PluginManifest manifest;
        try {
            manifest = this.loadPackManifest(packPath);
            if (manifest == null) {
                this.getLogger().at(Level.WARNING).log("Skipping pack at %s: missing or invalid manifest.json", packPath.getFileName());
                return;
            }
        }
        catch (Exception e) {
            ((HytaleLogger.Api)this.getLogger().at(Level.WARNING).withCause(e)).log("Failed to load manifest for pack at %s", packPath);
            return;
        }
        PluginIdentifier packIdentifier = new PluginIdentifier(manifest);
        HytaleServerConfig serverConfig = HytaleServer.get().getConfig();
        ModConfig modConfig = serverConfig.getModConfig().get(packIdentifier);
        boolean enabled = modConfig == null || modConfig.getEnabled() == null ? !manifest.isDisabledByDefault() && (!isExternal || serverConfig.getDefaultModsEnabled()) : modConfig.getEnabled();
        String packId = packIdentifier.toString();
        if (enabled) {
            this.registerPack(packId, packPath, manifest, false);
            this.getLogger().at(Level.INFO).log("Loaded pack: %s from %s", (Object)packId, (Object)packPath.getFileName());
        } else {
            this.getLogger().at(Level.INFO).log("Skipped disabled pack: %s", packId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerPack(@Nonnull String name, @Nonnull Path path, @Nonnull PluginManifest manifest, boolean ignoreIfExists) {
        Path absolutePath;
        Path packLocation = absolutePath = path.toAbsolutePath().normalize();
        FileSystem fileSystem = null;
        boolean isImmutable = false;
        String lowerFileName = absolutePath.getFileName().toString().toLowerCase();
        if (lowerFileName.endsWith(".zip") || lowerFileName.endsWith(".jar")) {
            try {
                fileSystem = FileSystems.newFileSystem(absolutePath, (ClassLoader)null);
                absolutePath = fileSystem.getPath("", new String[0]).toAbsolutePath().normalize();
                isImmutable = true;
            }
            catch (IOException e) {
                throw SneakyThrow.sneakyThrow(e);
            }
        } else {
            isImmutable = Files.isRegularFile(absolutePath.resolve("CommonAssetsIndex.hashes"), new LinkOption[0]);
        }
        AssetPack pack = new AssetPack(packLocation, name, absolutePath, fileSystem, isImmutable, manifest);
        if (!this.hasSetup) {
            this.pendingAssetPacks.add(ObjectBooleanPair.of(pack, ignoreIfExists));
            return;
        }
        if (this.getAssetPack(name) != null) {
            if (ignoreIfExists) {
                this.getLogger().at(Level.WARNING).log("Asset pack with name '%s' already exists, skipping registration from path: %s", (Object)name, (Object)path);
                return;
            }
            throw new IllegalStateException("Asset pack with name '" + name + "' already exists");
        }
        this.assetPacks.add(pack);
        AssetRegistry.ASSET_LOCK.writeLock().lock();
        try {
            if (!this.hasLoaded) {
                return;
            }
            HytaleServer.get().getEventBus().dispatchFor(AssetPackRegisterEvent.class).dispatch(new AssetPackRegisterEvent(pack));
        }
        finally {
            AssetRegistry.ASSET_LOCK.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void unregisterPack(@Nonnull String name) {
        AssetPack pack = this.getAssetPack(name);
        if (pack == null) {
            this.getLogger().at(Level.WARNING).log("Tried to unregister non-existent asset pack: %s", name);
            return;
        }
        this.assetPacks.remove(pack);
        if (pack.getFileSystem() != null) {
            try {
                pack.getFileSystem().close();
            }
            catch (IOException e) {
                throw SneakyThrow.sneakyThrow(e);
            }
        }
        AssetRegistry.ASSET_LOCK.writeLock().lock();
        try {
            HytaleServer.get().getEventBus().dispatchFor(AssetPackUnregisterEvent.class).dispatch(new AssetPackUnregisterEvent(pack));
        }
        finally {
            AssetRegistry.ASSET_LOCK.writeLock().unlock();
        }
    }

    public AssetPack getAssetPack(@Nonnull String name) {
        for (AssetPack pack : this.assetPacks) {
            if (!name.equals(pack.getName())) continue;
            return pack;
        }
        return null;
    }

    private void onRemoveStore(@Nonnull RemoveAssetStoreEvent event) {
        AssetStore<?, ?, ?> assetStore = event.getAssetStore();
        String path = assetStore.getPath();
        if (path == null) {
            return;
        }
        for (AssetPack pack : this.assetPacks) {
            Path assetsPath;
            if (pack.isImmutable() || !Files.isDirectory(assetsPath = pack.getRoot().resolve("Server").resolve(path), new LinkOption[0])) continue;
            assetStore.removeFileMonitor(assetsPath);
        }
    }

    private void onNewStore(@Nonnull RegisterAssetStoreEvent event) {
        if (!AssetRegistry.HAS_INIT) {
            return;
        }
        this.pendingAssetStores.add(event.getAssetStore());
    }

    public void initPendingStores() {
        for (int i = 0; i < this.pendingAssetStores.size(); ++i) {
            this.initStore(this.pendingAssetStores.get(i));
        }
        this.pendingAssetStores.clear();
    }

    private void initStore(@Nonnull AssetStore<?, ?, ?> assetStore) {
        AssetRegistry.ASSET_LOCK.writeLock().lock();
        try {
            AssetLoadResult<?, ?> loadResult;
            List<?> preAddedAssets = assetStore.getPreAddedAssets();
            if (preAddedAssets != null && !preAddedAssets.isEmpty() && (loadResult = assetStore.loadAssets("Hytale:Hytale", preAddedAssets)).hasFailed()) {
                throw new RuntimeException("Failed to load asset store: " + String.valueOf(assetStore.getAssetClass()));
            }
            for (AssetPack pack : this.assetPacks) {
                Path assetsPath;
                Path serverAssetDirectory = pack.getRoot().resolve("Server");
                String path = assetStore.getPath();
                if (path != null) {
                    assetsPath = serverAssetDirectory.resolve(path);
                    if (Files.isDirectory(assetsPath, new LinkOption[0])) {
                        AssetLoadResult<?, ?> loadResult2 = assetStore.loadAssetsFromDirectory(pack.getName(), assetsPath);
                        if (loadResult2.hasFailed()) {
                            throw new RuntimeException("Failed to load asset store: " + String.valueOf(assetStore.getAssetClass()));
                        }
                    } else {
                        this.getLogger().at(Level.SEVERE).log("Path for %s isn't a directory or doesn't exist: %s", (Object)assetStore.getAssetClass().getSimpleName(), (Object)assetsPath);
                    }
                }
                assetStore.validateCodecDefaults();
                if (path == null || !Files.isDirectory(assetsPath = serverAssetDirectory.resolve(path), new LinkOption[0])) continue;
                assetStore.addFileMonitor(pack.getName(), assetsPath);
            }
        }
        catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        }
        finally {
            AssetRegistry.ASSET_LOCK.writeLock().unlock();
        }
    }

    private static void validateWorldGen(@Nonnull LoadAssetEvent event) {
        if (!Options.getOptionSet().has(Options.VALIDATE_WORLD_GEN)) {
            return;
        }
        long start = System.nanoTime();
        try {
            boolean valid;
            IWorldGenProvider provider = IWorldGenProvider.CODEC.getDefault();
            IWorldGen generator = provider.getGenerator();
            generator.getDefaultSpawnProvider(0);
            if (generator instanceof ValidatableWorldGen && !(valid = ((ValidatableWorldGen)((Object)generator)).validate())) {
                event.failed(true, "failed to validate world gen");
            }
            if (generator instanceof IWorldMapProvider) {
                IWorldMapProvider worldMapProvider = (IWorldMapProvider)((Object)generator);
                IWorldMap worldMap = worldMapProvider.getGenerator(null);
                worldMap.getWorldMapSettings();
            }
        }
        catch (WorldGenLoadException e) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to load default world gen!");
            HytaleLogger.getLogger().at(Level.SEVERE).log("\n" + e.getTraceMessage("\n"));
            event.failed(true, "failed to validate world gen: " + e.getTraceMessage(" -> "));
        }
        catch (Throwable e) {
            ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to load default world gen!");
            event.failed(true, "failed to validate world gen");
        }
        HytaleLogger.getLogger().at(Level.INFO).log("Validate world gen phase completed! Boot time %s, Took %s", (Object)FormatUtil.nanosToString(System.nanoTime() - event.getBootStart()), (Object)FormatUtil.nanosToString(System.nanoTime() - start));
    }
}

