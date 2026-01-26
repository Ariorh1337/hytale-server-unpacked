/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.plugin;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.common.semver.Semver;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.common.util.java.ManifestUtil;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.metrics.MetricsRegistry;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.ShutdownReason;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.MissingPluginDependencyException;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginClassLoader;
import com.hypixel.hytale.server.core.plugin.PluginListPageManager;
import com.hypixel.hytale.server.core.plugin.PluginState;
import com.hypixel.hytale.server.core.plugin.commands.PluginCommand;
import com.hypixel.hytale.server.core.plugin.event.PluginSetupEvent;
import com.hypixel.hytale.server.core.plugin.pending.PendingLoadJavaPlugin;
import com.hypixel.hytale.server.core.plugin.pending.PendingLoadPlugin;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PluginManager {
    @Nonnull
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    @Nonnull
    public static final Path MODS_PATH = Path.of("mods", new String[0]);
    @Nonnull
    public static final MetricsRegistry<PluginManager> METRICS_REGISTRY = new MetricsRegistry<PluginManager>().register("Plugins", pluginManager -> (PluginBase[])pluginManager.getPlugins().toArray(PluginBase[]::new), new ArrayCodec<PluginBase>(PluginBase.METRICS_REGISTRY, PluginBase[]::new));
    private static PluginManager instance;
    @Nonnull
    private final PluginClassLoader corePluginClassLoader = new PluginClassLoader(this, true, new URL[0]);
    @Nonnull
    private final List<PendingLoadPlugin> corePlugins = new ObjectArrayList<PendingLoadPlugin>();
    private final PluginBridgeClassLoader bridgeClassLoader = new PluginBridgeClassLoader(this, PluginManager.class.getClassLoader());
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<PluginIdentifier, PluginBase> plugins = new Object2ObjectLinkedOpenHashMap<PluginIdentifier, PluginBase>();
    private final Map<Path, PluginClassLoader> classLoaders = new ConcurrentHashMap<Path, PluginClassLoader>();
    private final boolean loadExternalPlugins = true;
    @Nonnull
    private PluginState state = PluginState.NONE;
    @Nullable
    private List<PendingLoadPlugin> loadOrder;
    @Nullable
    private Map<PluginIdentifier, PluginBase> loading;
    @Nonnull
    private final Map<PluginIdentifier, PluginManifest> availablePlugins = new Object2ObjectLinkedOpenHashMap<PluginIdentifier, PluginManifest>();
    public PluginListPageManager pluginListPageManager;
    private ComponentType<EntityStore, PluginListPageManager.SessionSettings> sessionSettingsComponentType;

    public static PluginManager get() {
        return instance;
    }

    public PluginManager() {
        instance = this;
        this.pluginListPageManager = new PluginListPageManager();
    }

    public void registerCorePlugin(@Nonnull PluginManifest builder) {
        this.corePlugins.add(new PendingLoadJavaPlugin(null, builder, this.corePluginClassLoader));
    }

    private boolean canLoadOnBoot(@Nonnull PluginManifest manifest) {
        PluginIdentifier identifier = new PluginIdentifier(manifest);
        HytaleServerConfig.ModConfig modConfig = HytaleServer.get().getConfig().getModConfig().get(identifier);
        boolean enabled = modConfig == null || modConfig.getEnabled() == null ? !manifest.isDisabledByDefault() : modConfig.getEnabled();
        if (enabled) {
            return true;
        }
        LOGGER.at(Level.WARNING).log("Skipping mod %s (Disabled by server config)", identifier);
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setup() {
        PluginBase plugin;
        Path self;
        if (this.state != PluginState.NONE) {
            throw new IllegalStateException("Expected PluginState.NONE but found " + String.valueOf((Object)this.state));
        }
        this.state = PluginState.SETUP;
        CommandManager.get().registerSystemCommand(new PluginCommand());
        this.sessionSettingsComponentType = EntityStore.REGISTRY.registerComponent(PluginListPageManager.SessionSettings.class, PluginListPageManager.SessionSettings::new);
        HashMap<PluginIdentifier, PendingLoadPlugin> pending = new HashMap<PluginIdentifier, PendingLoadPlugin>();
        this.availablePlugins.clear();
        LOGGER.at(Level.INFO).log("Loading pending core plugins!");
        for (int i = 0; i < this.corePlugins.size(); ++i) {
            PendingLoadPlugin plugin2 = this.corePlugins.get(i);
            LOGGER.at(Level.INFO).log("- %s", plugin2.getIdentifier());
            if (this.canLoadOnBoot(plugin2.getManifest())) {
                PluginManager.loadPendingPlugin(pending, plugin2);
                continue;
            }
            this.availablePlugins.put(plugin2.getIdentifier(), plugin2.getManifest());
        }
        try {
            self = Paths.get(PluginManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.loadPluginsFromDirectory(pending, self.getParent().resolve("builtin"), false, this.availablePlugins);
        this.loadPluginsInClasspath(pending, this.availablePlugins);
        this.loadPluginsFromDirectory(pending, MODS_PATH, !Options.getOptionSet().has(Options.BARE), this.availablePlugins);
        for (Path modsPath : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
            this.loadPluginsFromDirectory(pending, modsPath, false, this.availablePlugins);
        }
        this.lock.readLock().lock();
        try {
            this.plugins.keySet().forEach(key -> {
                pending.remove(key);
                LOGGER.at(Level.WARNING).log("Skipping loading of %s because it is already loaded!", key);
            });
            Iterator<PendingLoadPlugin> iterator = pending.values().iterator();
            while (iterator.hasNext()) {
                PendingLoadPlugin pendingLoadPlugin = iterator.next();
                try {
                    this.validatePluginDeps(pendingLoadPlugin, pending);
                }
                catch (MissingPluginDependencyException e) {
                    LOGGER.at(Level.SEVERE).log(e.getMessage());
                    iterator.remove();
                }
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        this.loadOrder = PendingLoadPlugin.calculateLoadOrder(pending);
        this.loading = new Object2ObjectOpenHashMap<PluginIdentifier, PluginBase>();
        pending.forEach((identifier, pendingLoad) -> this.availablePlugins.put((PluginIdentifier)identifier, pendingLoad.getManifest()));
        ObjectArrayList<CompletableFuture<Void>> preLoadFutures = new ObjectArrayList<CompletableFuture<Void>>();
        this.lock.writeLock().lock();
        try {
            LOGGER.at(Level.FINE).log("Loading plugins!");
            for (PendingLoadPlugin pendingLoadPlugin : this.loadOrder) {
                LOGGER.at(Level.FINE).log("- %s", pendingLoadPlugin.getIdentifier());
                plugin = pendingLoadPlugin.load();
                if (plugin == null) continue;
                this.plugins.put(plugin.getIdentifier(), plugin);
                this.loading.put(plugin.getIdentifier(), plugin);
                CompletableFuture<Void> future = plugin.preLoad();
                if (future == null) continue;
                preLoadFutures.add(future);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        CompletableFuture.allOf((CompletableFuture[])preLoadFutures.toArray(CompletableFuture[]::new)).join();
        for (PendingLoadPlugin pendingPlugin : this.loadOrder) {
            plugin = this.loading.get(pendingPlugin.getIdentifier());
            if (plugin == null || this.setup(plugin)) continue;
            this.loading.remove(pendingPlugin.getIdentifier());
        }
    }

    public void start() {
        if (this.state != PluginState.SETUP) {
            throw new IllegalStateException("Expected PluginState.SETUP but found " + String.valueOf((Object)this.state));
        }
        this.state = PluginState.START;
        for (PendingLoadPlugin pendingLoadPlugin : this.loadOrder) {
            PluginBase pluginBase = this.loading.get(pendingLoadPlugin.getIdentifier());
            if (pluginBase == null || this.start(pluginBase)) continue;
            this.loading.remove(pendingLoadPlugin.getIdentifier());
        }
        this.loadOrder = null;
        this.loading = null;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<PluginIdentifier, HytaleServerConfig.ModConfig> entry : HytaleServer.get().getConfig().getModConfig().entrySet()) {
            PluginIdentifier identifier = entry.getKey();
            HytaleServerConfig.ModConfig modConfig = entry.getValue();
            SemverRange requiredVersion = modConfig.getRequiredVersion();
            if (requiredVersion == null || this.hasPlugin(identifier, requiredVersion)) continue;
            sb.append(String.format("%s, Version: %s\n", identifier, modConfig));
            return;
        }
        if (!sb.isEmpty()) {
            String string = "Failed to start server! Missing Mods:\n" + String.valueOf(sb);
            LOGGER.at(Level.SEVERE).log(string);
            HytaleServer.get().shutdownServer(ShutdownReason.MISSING_REQUIRED_PLUGIN.withMessage(string));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void shutdown() {
        this.state = PluginState.SHUTDOWN;
        LOGGER.at(Level.INFO).log("Saving plugins config...");
        this.lock.writeLock().lock();
        try {
            ObjectArrayList<PluginBase> list = new ObjectArrayList<PluginBase>(this.plugins.values());
            for (int i = list.size() - 1; i >= 0; --i) {
                PluginBase plugin = (PluginBase)list.get(i);
                if (plugin.getState() != PluginState.ENABLED) continue;
                LOGGER.at(Level.FINE).log("Shutting down %s %s", (Object)plugin.getType().getDisplayName(), (Object)plugin.getIdentifier());
                plugin.shutdown0(true);
                HytaleServer.get().doneStop(plugin);
                LOGGER.at(Level.INFO).log("Shut down plugin %s", plugin.getIdentifier());
            }
            this.plugins.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Nonnull
    public PluginState getState() {
        return this.state;
    }

    @Nonnull
    public PluginBridgeClassLoader getBridgeClassLoader() {
        return this.bridgeClassLoader;
    }

    private void validatePluginDeps(@Nonnull PendingLoadPlugin pendingLoadPlugin, @Nullable Map<PluginIdentifier, PendingLoadPlugin> pending) {
        Semver serverVersion = ManifestUtil.getVersion();
        SemverRange serverVersionRange = pendingLoadPlugin.getManifest().getServerVersion();
        if (serverVersionRange != null && serverVersion != null && !serverVersionRange.satisfies(serverVersion)) {
            throw new MissingPluginDependencyException(String.format("Failed to load '%s' because version of server does not satisfy '%s'! ", pendingLoadPlugin.getIdentifier(), serverVersion));
        }
        for (Map.Entry<PluginIdentifier, SemverRange> entry : pendingLoadPlugin.getManifest().getDependencies().entrySet()) {
            PluginBase loadedBase;
            PendingLoadPlugin pendingDependency;
            PluginIdentifier identifier = entry.getKey();
            PluginManifest dependency = null;
            if (pending != null && (pendingDependency = pending.get(identifier)) != null) {
                dependency = pendingDependency.getManifest();
            }
            if (dependency == null && (loadedBase = this.plugins.get(identifier)) != null) {
                dependency = loadedBase.getManifest();
            }
            if (dependency == null) {
                throw new MissingPluginDependencyException(String.format("Failed to load '%s' because the dependency '%s' could not be found!", pendingLoadPlugin.getIdentifier(), identifier));
            }
            SemverRange expectedVersion = entry.getValue();
            if (dependency.getVersion().satisfies(expectedVersion)) continue;
            throw new MissingPluginDependencyException(String.format("Failed to load '%s' because version of dependency '%s'(%s) does not satisfy '%s'!", pendingLoadPlugin.getIdentifier(), identifier, dependency.getVersion(), expectedVersion));
        }
    }

    private void loadPluginsFromDirectory(@Nonnull Map<PluginIdentifier, PendingLoadPlugin> pending, @Nonnull Path path, boolean create, @Nonnull Map<PluginIdentifier, PluginManifest> bootRejectMap) {
        if (!Files.isDirectory(path, new LinkOption[0])) {
            if (create) {
                try {
                    Files.createDirectories(path, new FileAttribute[0]);
                }
                catch (IOException e) {
                    ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to create directory: %s", path);
                }
            }
            return;
        }
        LOGGER.at(Level.INFO).log("Loading pending plugins from directory: " + String.valueOf(path));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path);){
            for (Path file : stream) {
                PendingLoadJavaPlugin plugin;
                if (!Files.isRegularFile(file, new LinkOption[0]) || !file.getFileName().toString().toLowerCase().endsWith(".jar") || (plugin = this.loadPendingJavaPlugin(file)) == null) continue;
                assert (plugin.getPath() != null);
                LOGGER.at(Level.INFO).log("- %s from path %s", (Object)plugin.getIdentifier(), (Object)path.relativize(plugin.getPath()));
                if (this.canLoadOnBoot(plugin.getManifest())) {
                    PluginManager.loadPendingPlugin(pending, plugin);
                    continue;
                }
                bootRejectMap.put(plugin.getIdentifier(), plugin.getManifest());
            }
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to find pending plugins from: %s", path);
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private PendingLoadJavaPlugin loadPendingJavaPlugin(@Nonnull Path file) {
        try {
            URL url = file.toUri().toURL();
            PluginClassLoader pluginClassLoader = this.classLoaders.computeIfAbsent(file, path -> new PluginClassLoader(this, false, url));
            URL resource = pluginClassLoader.findResource("manifest.json");
            if (resource == null) {
                LOGGER.at(Level.SEVERE).log("Failed to load pending plugin from '%s'. Failed to load manifest file!", file.toString());
                return null;
            }
            try (InputStream stream = resource.openStream();){
                PendingLoadJavaPlugin pendingLoadJavaPlugin;
                try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                    char[] buffer = RawJsonReader.READ_BUFFER.get();
                    RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
                    ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                    PluginManifest manifest = PluginManifest.CODEC.decodeJson(rawJsonReader, extraInfo);
                    extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
                    pendingLoadJavaPlugin = new PendingLoadJavaPlugin(file, manifest, pluginClassLoader);
                }
                return pendingLoadJavaPlugin;
            }
        }
        catch (MalformedURLException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to load pending plugin from '%s'. Failed to create URLClassLoader!", file.toString());
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to load pending plugin %s. Failed to load manifest file!", file.toString());
        }
        return null;
    }

    private void loadPluginsInClasspath(@Nonnull Map<PluginIdentifier, PendingLoadPlugin> pending, @Nonnull Map<PluginIdentifier, PluginManifest> rejectedBootList) {
        block31: {
            LOGGER.at(Level.INFO).log("Loading pending classpath plugins!");
            try {
                URI uri = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                ClassLoader classLoader = PluginManager.class.getClassLoader();
                try {
                    HashSet<URL> manifestUrls = new HashSet<URL>(Collections.list(classLoader.getResources("manifest.json")));
                    for (URL manifestUrl : manifestUrls) {
                        URLConnection connection = manifestUrl.openConnection();
                        InputStream stream = connection.getInputStream();
                        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                            PendingLoadJavaPlugin plugin;
                            char[] buffer = RawJsonReader.READ_BUFFER.get();
                            RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
                            ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                            PluginManifest manifest = PluginManifest.CODEC.decodeJson(rawJsonReader, extraInfo);
                            extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
                            if (connection instanceof JarURLConnection) {
                                JarURLConnection jarURLConnection = (JarURLConnection)connection;
                                URL classpathUrl = jarURLConnection.getJarFileURL();
                                path = Path.of(classpathUrl.toURI());
                                PluginClassLoader pluginClassLoader = this.classLoaders.computeIfAbsent(path, f -> new PluginClassLoader(this, true, classpathUrl));
                                plugin = new PendingLoadJavaPlugin(path, manifest, pluginClassLoader);
                            } else {
                                URI pluginUri = manifestUrl.toURI().resolve(".");
                                path = Paths.get(pluginUri);
                                URL classpathUrl = pluginUri.toURL();
                                PluginClassLoader pluginClassLoader = this.classLoaders.computeIfAbsent(path, f -> new PluginClassLoader(this, true, classpathUrl));
                                plugin = new PendingLoadJavaPlugin(path, manifest, pluginClassLoader);
                            }
                            LOGGER.at(Level.INFO).log("- %s", plugin.getIdentifier());
                            if (this.canLoadOnBoot(plugin.getManifest())) {
                                PluginManager.loadPendingPlugin(pending, plugin);
                                continue;
                            }
                            rejectedBootList.put(plugin.getIdentifier(), plugin.getManifest());
                        }
                        finally {
                            if (stream == null) continue;
                            stream.close();
                        }
                    }
                    URL manifestsUrl = classLoader.getResource("manifests.json");
                    if (manifestsUrl == null) break block31;
                    try (InputStream stream = manifestsUrl.openStream();
                         InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
                        char[] buffer = RawJsonReader.READ_BUFFER.get();
                        RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
                        ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                        PluginManifest[] manifests = PluginManifest.ARRAY_CODEC.decodeJson(rawJsonReader, extraInfo);
                        extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
                        URL url = uri.toURL();
                        Path path = Paths.get(uri);
                        PluginClassLoader pluginClassLoader = this.classLoaders.computeIfAbsent(path, f -> new PluginClassLoader(this, true, url));
                        for (PluginManifest manifest : manifests) {
                            PendingLoadJavaPlugin plugin = new PendingLoadJavaPlugin(path, manifest, pluginClassLoader);
                            LOGGER.at(Level.INFO).log("- %s", plugin.getIdentifier());
                            if (this.canLoadOnBoot(plugin.getManifest())) {
                                PluginManager.loadPendingPlugin(pending, plugin);
                                continue;
                            }
                            rejectedBootList.put(plugin.getIdentifier(), plugin.getManifest());
                        }
                    }
                }
                catch (IOException e) {
                    ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to load pending classpath plugin from '%s'. Failed to load manifest file!", uri.toString());
                }
            }
            catch (URISyntaxException e) {
                ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to get jar path!");
            }
        }
    }

    @Nonnull
    public List<PluginBase> getPlugins() {
        this.lock.readLock().lock();
        try {
            ObjectArrayList<PluginBase> objectArrayList = new ObjectArrayList<PluginBase>(this.plugins.values());
            return objectArrayList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Nullable
    public PluginBase getPlugin(PluginIdentifier identifier) {
        this.lock.readLock().lock();
        try {
            PluginBase pluginBase = this.plugins.get(identifier);
            return pluginBase;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean hasPlugin(PluginIdentifier identifier, @Nonnull SemverRange range) {
        PluginBase plugin = this.getPlugin(identifier);
        return plugin != null && plugin.getManifest().getVersion().satisfies(range);
    }

    public boolean reload(@Nonnull PluginIdentifier identifier) {
        boolean result = this.unload(identifier) && this.load(identifier);
        this.pluginListPageManager.notifyPluginChange(this.plugins, identifier);
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean unload(@Nonnull PluginIdentifier identifier) {
        this.lock.writeLock().lock();
        AssetRegistry.ASSET_LOCK.writeLock().lock();
        try {
            PluginBase plugin = this.plugins.get(identifier);
            if (plugin.getState() == PluginState.ENABLED) {
                plugin.shutdown0(false);
                HytaleServer.get().doneStop(plugin);
                this.plugins.remove(identifier);
                if (plugin instanceof JavaPlugin) {
                    JavaPlugin javaPlugin = (JavaPlugin)plugin;
                    this.unloadJavaPlugin(javaPlugin);
                }
                this.pluginListPageManager.notifyPluginChange(this.plugins, identifier);
                boolean bl = true;
                return bl;
            }
            this.pluginListPageManager.notifyPluginChange(this.plugins, identifier);
            boolean bl = false;
            return bl;
        }
        finally {
            AssetRegistry.ASSET_LOCK.writeLock().unlock();
            this.lock.writeLock().unlock();
        }
    }

    protected void unloadJavaPlugin(JavaPlugin plugin) {
        Path path = plugin.getFile();
        PluginClassLoader classLoader = this.classLoaders.remove(path);
        if (classLoader != null) {
            try {
                classLoader.close();
            }
            catch (IOException e) {
                LOGGER.at(Level.SEVERE).log("Failed to close Class Loader for JavaPlugin %s", plugin.getIdentifier());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean load(@Nonnull PluginIdentifier identifier) {
        this.lock.readLock().lock();
        try {
            PluginBase plugin = this.plugins.get(identifier);
            if (plugin != null) {
                this.pluginListPageManager.notifyPluginChange(this.plugins, identifier);
                boolean bl = false;
                return bl;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        boolean result = this.findAndLoadPlugin(identifier);
        this.pluginListPageManager.notifyPluginChange(this.plugins, identifier);
        return result;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private boolean findAndLoadPlugin(PluginIdentifier identifier) {
        block46: {
            for (PendingLoadPlugin plugin : this.corePlugins) {
                if (!plugin.getIdentifier().equals(identifier)) continue;
                return this.load(plugin);
            }
            try {
                Path path;
                Closeable stream;
                URI uri = PluginManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                ClassLoader classLoader = PluginManager.class.getClassLoader();
                HashSet<URL> manifestUrls = new HashSet<URL>(Collections.list(classLoader.getResources("manifest.json")));
                for (URL manifestUrl : manifestUrls) {
                    stream = manifestUrl.openStream();
                    try {
                        PluginManifest manifest;
                        InputStreamReader reader;
                        block44: {
                            reader = new InputStreamReader((InputStream)stream, StandardCharsets.UTF_8);
                            try {
                                char[] buffer = RawJsonReader.READ_BUFFER.get();
                                RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
                                ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                                manifest = PluginManifest.CODEC.decodeJson(rawJsonReader, extraInfo);
                                extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
                                if (new PluginIdentifier(manifest).equals(identifier)) break block44;
                            }
                            catch (Throwable buffer) {
                                try {
                                    reader.close();
                                }
                                catch (Throwable rawJsonReader) {
                                    buffer.addSuppressed(rawJsonReader);
                                }
                                throw buffer;
                            }
                            reader.close();
                            continue;
                        }
                        PluginClassLoader pluginClassLoader = new PluginClassLoader(this, true, uri.toURL());
                        PendingLoadJavaPlugin plugin = new PendingLoadJavaPlugin(Paths.get(uri), manifest, pluginClassLoader);
                        boolean bl = this.load(plugin);
                        reader.close();
                        return bl;
                    }
                    finally {
                        if (stream == null) continue;
                        ((InputStream)stream).close();
                    }
                }
                URL manifestsUrl = classLoader.getResource("manifests.json");
                if (manifestsUrl != null) {
                    try (InputStream stream2 = manifestsUrl.openStream();
                         InputStreamReader reader = new InputStreamReader(stream2, StandardCharsets.UTF_8);){
                        char[] buffer = RawJsonReader.READ_BUFFER.get();
                        RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
                        ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                        PluginManifest[] manifests = PluginManifest.ARRAY_CODEC.decodeJson(rawJsonReader, extraInfo);
                        extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
                        for (PluginManifest manifest : manifests) {
                            if (!new PluginIdentifier(manifest).equals(identifier)) continue;
                            PluginClassLoader pluginClassLoader = new PluginClassLoader(this, true, uri.toURL());
                            PendingLoadJavaPlugin plugin = new PendingLoadJavaPlugin(Paths.get(uri), manifest, pluginClassLoader);
                            boolean bl = this.load(plugin);
                            return bl;
                        }
                    }
                }
                if (!Files.exists(path = Paths.get(uri).getParent().resolve("builtin"), new LinkOption[0])) break block46;
                try {
                    stream = Files.newDirectoryStream(path);
                    try {
                        Iterator iterator = stream.iterator();
                        while (iterator.hasNext()) {
                            Path file = (Path)iterator.next();
                            if (!Files.isRegularFile(file, new LinkOption[0]) || !file.getFileName().toString().toLowerCase().endsWith(".jar")) continue;
                            PluginManifest manifest = PluginManager.loadManifest(file);
                            if (manifest == null || !new PluginIdentifier(manifest).equals(identifier)) continue;
                            PendingLoadJavaPlugin pendingLoadJavaPlugin = this.loadPendingJavaPlugin(file);
                            if (pendingLoadJavaPlugin != null) {
                                boolean bl = this.load(pendingLoadJavaPlugin);
                                return bl;
                            }
                            break;
                        }
                    }
                    finally {
                        if (stream != null) {
                            stream.close();
                        }
                    }
                }
                catch (IOException e) {
                    ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to find plugins!");
                }
            }
            catch (IOException | URISyntaxException e) {
                ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to load pending classpath plugin. Failed to load manifest file!");
            }
        }
        Boolean result = this.findPluginInDirectory(identifier, MODS_PATH);
        if (result != null) {
            return result;
        }
        for (Path modsPath : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
            result = this.findPluginInDirectory(identifier, modsPath);
            if (result == null) continue;
            return result;
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private Boolean findPluginInDirectory(@Nonnull PluginIdentifier identifier, @Nonnull Path modsPath) {
        if (!Files.isDirectory(modsPath, new LinkOption[0])) {
            return null;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsPath);){
            PluginManifest manifest;
            Path file;
            Iterator<Path> iterator = stream.iterator();
            do {
                if (!iterator.hasNext()) return null;
            } while (!Files.isRegularFile(file = iterator.next(), new LinkOption[0]) || !file.getFileName().toString().toLowerCase().endsWith(".jar") || (manifest = PluginManager.loadManifest(file)) == null || !new PluginIdentifier(manifest).equals(identifier));
            PendingLoadJavaPlugin pendingLoadJavaPlugin = this.loadPendingJavaPlugin(file);
            if (pendingLoadJavaPlugin != null) {
                Boolean bl = this.load(pendingLoadJavaPlugin);
                return bl;
            }
            Boolean bl = false;
            return bl;
        }
        catch (IOException e) {
            ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to find plugins in %s!", modsPath);
        }
        return null;
    }

    /*
     * Exception decompiling
     */
    @Nullable
    private static PluginManifest loadManifest(@Nonnull Path file) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private boolean load(@Nullable PendingLoadPlugin pendingLoadPlugin) {
        if (pendingLoadPlugin == null) {
            return false;
        }
        this.validatePluginDeps(pendingLoadPlugin, null);
        PluginBase plugin = pendingLoadPlugin.load();
        if (plugin != null) {
            this.lock.writeLock().lock();
            try {
                this.plugins.put(plugin.getIdentifier(), plugin);
            }
            finally {
                this.lock.writeLock().unlock();
            }
            CompletableFuture<Void> preload = plugin.preLoad();
            if (preload == null) {
                boolean result = this.setup(plugin) && this.start(plugin);
                this.pluginListPageManager.notifyPluginChange(this.plugins, plugin.getIdentifier());
                return result;
            }
            preload.thenAccept(v -> {
                this.setup(plugin);
                this.start(plugin);
                this.pluginListPageManager.notifyPluginChange(this.plugins, plugin.getIdentifier());
            });
        }
        this.pluginListPageManager.notifyPluginChange(this.plugins, pendingLoadPlugin.getIdentifier());
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean setup(@Nonnull PluginBase plugin) {
        if (plugin.getState() == PluginState.NONE && this.dependenciesMatchState(plugin, PluginState.SETUP, PluginState.SETUP)) {
            LOGGER.at(Level.FINE).log("Setting up plugin %s", plugin.getIdentifier());
            boolean prev = AssetStore.DISABLE_DYNAMIC_DEPENDENCIES;
            AssetStore.DISABLE_DYNAMIC_DEPENDENCIES = false;
            plugin.setup0();
            AssetStore.DISABLE_DYNAMIC_DEPENDENCIES = prev;
            AssetModule.get().initPendingStores();
            HytaleServer.get().doneSetup(plugin);
            if (plugin.getState() == PluginState.DISABLED) {
                plugin.shutdown0(false);
                this.plugins.remove(plugin.getIdentifier());
                return false;
            }
            IEventDispatcher<PluginSetupEvent, PluginSetupEvent> dispatch = HytaleServer.get().getEventBus().dispatchFor(PluginSetupEvent.class, plugin.getClass());
            if (!dispatch.hasListener()) return true;
            dispatch.dispatch(new PluginSetupEvent(plugin));
            return true;
        }
        plugin.shutdown0(false);
        this.plugins.remove(plugin.getIdentifier());
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean start(@Nonnull PluginBase plugin) {
        if (plugin.getState() == PluginState.SETUP && this.dependenciesMatchState(plugin, PluginState.ENABLED, PluginState.START)) {
            LOGGER.at(Level.FINE).log("Starting plugin %s", plugin.getIdentifier());
            plugin.start0();
            HytaleServer.get().doneStart(plugin);
            if (plugin.getState() == PluginState.DISABLED) {
                plugin.shutdown0(false);
                this.plugins.remove(plugin.getIdentifier());
                return false;
            }
            LOGGER.at(Level.INFO).log("Enabled plugin %s", plugin.getIdentifier());
            return true;
        }
        plugin.shutdown0(false);
        this.plugins.remove(plugin.getIdentifier());
        return false;
    }

    private boolean dependenciesMatchState(PluginBase plugin, PluginState requiredState, PluginState stage) {
        Set<PluginIdentifier> dependenciesOnManifest = plugin.getManifest().getDependencies().keySet();
        for (PluginIdentifier dependencyOnManifest : dependenciesOnManifest) {
            PluginBase dependency = this.plugins.get(dependencyOnManifest);
            if (dependency != null && dependency.getState() == requiredState) continue;
            LOGGER.at(Level.SEVERE).log(plugin.getName() + " is lacking dependency " + dependencyOnManifest.getName() + " at stage " + String.valueOf((Object)stage));
            LOGGER.at(Level.SEVERE).log(plugin.getName() + " DISABLED!");
            return false;
        }
        return true;
    }

    private static void loadPendingPlugin(@Nonnull Map<PluginIdentifier, PendingLoadPlugin> pending, @Nonnull PendingLoadPlugin plugin) {
        if (pending.putIfAbsent(plugin.getIdentifier(), plugin) != null) {
            throw new IllegalArgumentException("Tried to load duplicate plugin");
        }
        for (PendingLoadPlugin subPlugin : plugin.createSubPendingLoadPlugins()) {
            PluginManager.loadPendingPlugin(pending, subPlugin);
        }
    }

    @Nonnull
    public Map<PluginIdentifier, PluginManifest> getAvailablePlugins() {
        return this.availablePlugins;
    }

    public ComponentType<EntityStore, PluginListPageManager.SessionSettings> getSessionSettingsComponentType() {
        return this.sessionSettingsComponentType;
    }

    public static class PluginBridgeClassLoader
    extends ClassLoader {
        private final PluginManager pluginManager;

        public PluginBridgeClassLoader(PluginManager pluginManager, ClassLoader parent) {
            super(parent);
            this.pluginManager = pluginManager;
        }

        @Override
        @Nonnull
        protected Class<?> loadClass(@Nonnull String name, boolean resolve) throws ClassNotFoundException {
            return this.loadClass0(name, null);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nonnull
        public Class<?> loadClass0(@Nonnull String name, PluginClassLoader pluginClassLoader) throws ClassNotFoundException {
            this.pluginManager.lock.readLock().lock();
            try {
                for (Map.Entry<PluginIdentifier, PluginBase> entry : this.pluginManager.plugins.entrySet()) {
                    PluginBase pluginBase = entry.getValue();
                    Class<?> loadClass = PluginBridgeClassLoader.tryGetClass(name, pluginClassLoader, pluginBase);
                    if (loadClass == null) continue;
                    Class<?> clazz = loadClass;
                    return clazz;
                }
            }
            finally {
                this.pluginManager.lock.readLock().unlock();
            }
            throw new ClassNotFoundException();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nonnull
        public Class<?> loadClass0(@Nonnull String name, PluginClassLoader pluginClassLoader, @Nonnull PluginManifest manifest) throws ClassNotFoundException {
            this.pluginManager.lock.readLock().lock();
            try {
                Class<?> loadClass;
                PluginBase pluginBase;
                for (PluginIdentifier pluginIdentifier : manifest.getDependencies().keySet()) {
                    pluginBase = this.pluginManager.plugins.get(pluginIdentifier);
                    loadClass = PluginBridgeClassLoader.tryGetClass(name, pluginClassLoader, pluginBase);
                    if (loadClass == null) continue;
                    Class<?> clazz = loadClass;
                    return clazz;
                }
                for (PluginIdentifier pluginIdentifier : manifest.getOptionalDependencies().keySet()) {
                    if (manifest.getDependencies().containsKey(pluginIdentifier) || (pluginBase = this.pluginManager.plugins.get(pluginIdentifier)) == null) continue;
                    loadClass = PluginBridgeClassLoader.tryGetClass(name, pluginClassLoader, pluginBase);
                    if (loadClass == null) continue;
                    Class<?> clazz = loadClass;
                    return clazz;
                }
                for (Map.Entry entry : this.pluginManager.plugins.entrySet()) {
                    if (manifest.getDependencies().containsKey(entry.getKey()) || manifest.getOptionalDependencies().containsKey(entry.getKey())) continue;
                    pluginBase = (PluginBase)entry.getValue();
                    loadClass = PluginBridgeClassLoader.tryGetClass(name, pluginClassLoader, pluginBase);
                    if (loadClass == null) continue;
                    Class<?> clazz = loadClass;
                    return clazz;
                }
            }
            finally {
                this.pluginManager.lock.readLock().unlock();
            }
            throw new ClassNotFoundException();
        }

        public static Class<?> tryGetClass(@Nonnull String name, PluginClassLoader pluginClassLoader, PluginBase pluginBase) {
            if (!(pluginBase instanceof JavaPlugin)) {
                return null;
            }
            try {
                Class<?> loadClass;
                PluginClassLoader classLoader = ((JavaPlugin)pluginBase).getClassLoader();
                if (classLoader != pluginClassLoader && (loadClass = classLoader.loadLocalClass(name)) != null) {
                    return loadClass;
                }
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public URL getResource0(@Nonnull String name, @Nullable PluginClassLoader pluginClassLoader) {
            this.pluginManager.lock.readLock().lock();
            try {
                for (Map.Entry<PluginIdentifier, PluginBase> entry : this.pluginManager.plugins.entrySet()) {
                    URL resource = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, entry.getValue());
                    if (resource == null) continue;
                    URL uRL = resource;
                    return uRL;
                }
            }
            finally {
                this.pluginManager.lock.readLock().unlock();
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        public URL getResource0(@Nonnull String name, @Nullable PluginClassLoader pluginClassLoader, @Nonnull PluginManifest manifest) {
            this.pluginManager.lock.readLock().lock();
            try {
                URL resource;
                for (PluginIdentifier pluginIdentifier : manifest.getDependencies().keySet()) {
                    resource = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, this.pluginManager.plugins.get(pluginIdentifier));
                    if (resource == null) continue;
                    URL uRL = resource;
                    return uRL;
                }
                for (PluginIdentifier pluginIdentifier : manifest.getOptionalDependencies().keySet()) {
                    PluginBase pluginBase;
                    if (manifest.getDependencies().containsKey(pluginIdentifier) || (pluginBase = this.pluginManager.plugins.get(pluginIdentifier)) == null) continue;
                    URL resource2 = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, pluginBase);
                    if (resource2 == null) continue;
                    URL uRL = resource2;
                    return uRL;
                }
                for (Map.Entry entry : this.pluginManager.plugins.entrySet()) {
                    if (manifest.getDependencies().containsKey(entry.getKey()) || manifest.getOptionalDependencies().containsKey(entry.getKey())) continue;
                    resource = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, (PluginBase)entry.getValue());
                    if (resource == null) continue;
                    URL uRL = resource;
                    return uRL;
                }
            }
            finally {
                this.pluginManager.lock.readLock().unlock();
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nonnull
        public Enumeration<URL> getResources0(@Nonnull String name, @Nullable PluginClassLoader pluginClassLoader) {
            ObjectArrayList<URL> results = new ObjectArrayList<URL>();
            this.pluginManager.lock.readLock().lock();
            try {
                for (Map.Entry<PluginIdentifier, PluginBase> entry : this.pluginManager.plugins.entrySet()) {
                    URL resource = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, entry.getValue());
                    if (resource == null) continue;
                    results.add(resource);
                }
            }
            finally {
                this.pluginManager.lock.readLock().unlock();
            }
            return Collections.enumeration(results);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nonnull
        public Enumeration<URL> getResources0(@Nonnull String name, @Nullable PluginClassLoader pluginClassLoader, @Nonnull PluginManifest manifest) {
            ObjectArrayList<URL> results = new ObjectArrayList<URL>();
            this.pluginManager.lock.readLock().lock();
            try {
                URL resource;
                for (PluginIdentifier pluginIdentifier : manifest.getDependencies().keySet()) {
                    resource = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, this.pluginManager.plugins.get(pluginIdentifier));
                    if (resource == null) continue;
                    results.add(resource);
                }
                for (PluginIdentifier pluginIdentifier : manifest.getOptionalDependencies().keySet()) {
                    URL resource2;
                    PluginBase pluginBase;
                    if (manifest.getDependencies().containsKey(pluginIdentifier) || (pluginBase = this.pluginManager.plugins.get(pluginIdentifier)) == null || (resource2 = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, pluginBase)) == null) continue;
                    results.add(resource2);
                }
                for (Map.Entry entry : this.pluginManager.plugins.entrySet()) {
                    if (manifest.getDependencies().containsKey(entry.getKey()) || manifest.getOptionalDependencies().containsKey(entry.getKey()) || (resource = PluginBridgeClassLoader.tryGetResource(name, pluginClassLoader, (PluginBase)entry.getValue())) == null) continue;
                    results.add(resource);
                }
            }
            finally {
                this.pluginManager.lock.readLock().unlock();
            }
            return Collections.enumeration(results);
        }

        @Nullable
        private static URL tryGetResource(@Nonnull String name, @Nullable PluginClassLoader pluginClassLoader, @Nullable PluginBase pluginBase) {
            if (!(pluginBase instanceof JavaPlugin)) {
                return null;
            }
            JavaPlugin javaPlugin = (JavaPlugin)pluginBase;
            PluginClassLoader classLoader = javaPlugin.getClassLoader();
            if (classLoader != pluginClassLoader) {
                return classLoader.findResource(name);
            }
            return null;
        }

        static {
            PluginBridgeClassLoader.registerAsParallelCapable();
        }
    }
}

