/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.DocumentContainingCodec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.codecs.map.ObjectMapCodec;
import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Constants;
import com.hypixel.hytale.server.core.Options;
import com.hypixel.hytale.server.core.auth.AuthCredentialStoreProvider;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.universe.playerdata.DefaultPlayerStorageProvider;
import com.hypixel.hytale.server.core.universe.playerdata.DiskPlayerStorageProvider;
import com.hypixel.hytale.server.core.universe.playerdata.PlayerStorageProvider;
import com.hypixel.hytale.server.core.util.BsonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.invoke.LambdaMetafactory;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class HytaleServerConfig {
    public static final int VERSION = 3;
    public static final int DEFAULT_MAX_VIEW_RADIUS = 32;
    @Nonnull
    public static final Path PATH = Path.of("config.json", new String[0]);
    @Nonnull
    public static final BuilderCodec<HytaleServerConfig> CODEC;
    @Nonnull
    private final transient AtomicBoolean hasChanged = new AtomicBoolean();
    private String serverName = "Hytale Server";
    private String motd = "";
    private String password = "";
    private int maxPlayers = 100;
    private int maxViewRadius = 32;
    @Nonnull
    private Defaults defaults = new Defaults(this);
    @Nonnull
    private TimeoutProfile connectionTimeouts = new TimeoutProfile(this);
    @Nonnull
    private RateLimitConfig rateLimitConfig = new RateLimitConfig(this);
    @Nonnull
    private Map<String, Module> modules = new ConcurrentHashMap<String, Module>();
    @Nonnull
    private Map<String, Level> logLevels = Collections.emptyMap();
    @Nullable
    private transient Map<PluginIdentifier, ModConfig> legacyPluginConfig;
    @Nonnull
    private Map<PluginIdentifier, ModConfig> modConfig = new ConcurrentHashMap<PluginIdentifier, ModConfig>();
    @Nonnull
    private Map<String, Module> unmodifiableModules = Collections.unmodifiableMap(this.modules);
    @Nonnull
    private Map<String, Level> unmodifiableLogLevels = Collections.unmodifiableMap(this.logLevels);
    @Nonnull
    private PlayerStorageProvider playerStorageProvider = PlayerStorageProvider.CODEC.getDefault();
    @Nullable
    private BsonDocument authCredentialStoreConfig = null;
    @Nullable
    private transient AuthCredentialStoreProvider authCredentialStoreProvider = null;
    private boolean displayTmpTagsInStrings;
    @Nonnull
    private UpdateConfig updateConfig = new UpdateConfig(this);

    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(@Nonnull String serverName) {
        this.serverName = serverName;
        this.markChanged();
    }

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(@Nonnull String motd) {
        this.motd = motd;
        this.markChanged();
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
        this.markChanged();
    }

    public boolean isDisplayTmpTagsInStrings() {
        return this.displayTmpTagsInStrings;
    }

    public void setDisplayTmpTagsInStrings(boolean displayTmpTagsInStrings) {
        this.displayTmpTagsInStrings = displayTmpTagsInStrings;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.markChanged();
    }

    public int getMaxViewRadius() {
        return this.maxViewRadius;
    }

    public void setMaxViewRadius(int maxViewRadius) {
        this.maxViewRadius = maxViewRadius;
        this.markChanged();
    }

    @Nonnull
    public Defaults getDefaults() {
        return this.defaults;
    }

    public void setDefaults(@Nonnull Defaults defaults) {
        this.defaults = defaults;
        this.markChanged();
    }

    @Nonnull
    public TimeoutProfile getConnectionTimeouts() {
        return this.connectionTimeouts;
    }

    public void setConnectionTimeouts(@Nonnull TimeoutProfile connectionTimeouts) {
        this.connectionTimeouts = connectionTimeouts;
        this.markChanged();
    }

    @Nonnull
    public RateLimitConfig getRateLimitConfig() {
        return this.rateLimitConfig;
    }

    public void setRateLimitConfig(@Nonnull RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
        this.markChanged();
    }

    @Nonnull
    public Map<String, Module> getModules() {
        return this.unmodifiableModules;
    }

    @Nonnull
    public Module getModule(String moduleName) {
        return this.modules.computeIfAbsent(moduleName, k -> new Module(this));
    }

    public void setModules(@Nonnull Map<String, Module> modules) {
        this.modules = modules;
        this.markChanged();
    }

    @Nonnull
    public Map<String, Level> getLogLevels() {
        return this.unmodifiableLogLevels;
    }

    public void setLogLevels(@Nonnull Map<String, Level> logLevels) {
        this.logLevels = logLevels;
        this.markChanged();
    }

    @Nonnull
    public Map<PluginIdentifier, ModConfig> getModConfig() {
        return this.modConfig;
    }

    public void setModConfig(@Nonnull Map<PluginIdentifier, ModConfig> modConfig) {
        this.modConfig = modConfig;
        this.markChanged();
    }

    @Nonnull
    public PlayerStorageProvider getPlayerStorageProvider() {
        return this.playerStorageProvider;
    }

    public void setPlayerStorageProvider(@Nonnull PlayerStorageProvider playerStorageProvider) {
        this.playerStorageProvider = playerStorageProvider;
        this.markChanged();
    }

    @Nonnull
    public AuthCredentialStoreProvider getAuthCredentialStoreProvider() {
        if (this.authCredentialStoreProvider != null) {
            return this.authCredentialStoreProvider;
        }
        this.authCredentialStoreProvider = this.authCredentialStoreConfig != null ? (AuthCredentialStoreProvider)AuthCredentialStoreProvider.CODEC.decode(this.authCredentialStoreConfig) : AuthCredentialStoreProvider.CODEC.getDefault();
        return this.authCredentialStoreProvider;
    }

    public void setAuthCredentialStoreProvider(@Nonnull AuthCredentialStoreProvider provider) {
        this.authCredentialStoreProvider = provider;
        this.authCredentialStoreConfig = (BsonDocument)AuthCredentialStoreProvider.CODEC.encode(provider);
        this.markChanged();
    }

    @Nonnull
    public UpdateConfig getUpdateConfig() {
        return this.updateConfig;
    }

    public void setUpdateConfig(@Nonnull UpdateConfig updateConfig) {
        this.updateConfig = updateConfig;
        this.markChanged();
    }

    public void removeModule(@Nonnull String module) {
        this.modules.remove(module);
        this.markChanged();
    }

    public void markChanged() {
        this.hasChanged.set(true);
    }

    public boolean consumeHasChanged() {
        return this.hasChanged.getAndSet(false);
    }

    @Nonnull
    public static HytaleServerConfig load() {
        return HytaleServerConfig.load(PATH);
    }

    @Nonnull
    public static HytaleServerConfig load(@Nonnull Path path) {
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            HytaleServerConfig hytaleServerConfig = new HytaleServerConfig();
            if (!Options.getOptionSet().has(Options.BARE)) {
                HytaleServerConfig.save(hytaleServerConfig).join();
            }
            return hytaleServerConfig;
        }
        try {
            HytaleServerConfig config = RawJsonReader.readSyncWithBak(path, CODEC, HytaleLogger.getLogger());
            if (config == null) {
                throw new RuntimeException("Failed to load server config from " + String.valueOf(path));
            }
            return config;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to read server config!", e);
        }
    }

    @Nonnull
    public static CompletableFuture<Void> save(@Nonnull HytaleServerConfig hytaleServerConfig) {
        return HytaleServerConfig.save(PATH, hytaleServerConfig);
    }

    @Nonnull
    public static CompletableFuture<Void> save(@Nonnull Path path, @Nonnull HytaleServerConfig hytaleServerConfig) {
        BsonDocument document = CODEC.encode((Object)hytaleServerConfig, ExtraInfo.THREAD_LOCAL.get()).asDocument();
        return BsonUtil.writeDocument(path, document);
    }

    static {
        PlayerStorageProvider.CODEC.register(Priority.DEFAULT, "Hytale", (Class<PlayerStorageProvider>)DefaultPlayerStorageProvider.class, DefaultPlayerStorageProvider.CODEC);
        PlayerStorageProvider.CODEC.register("Disk", DiskPlayerStorageProvider.class, DiskPlayerStorageProvider.CODEC);
        Module.BUILDER_CODEC_BUILDER.addField(new KeyedCodec("Modules", new MapCodec<Module, ConcurrentHashMap>(Module.CODEC, ConcurrentHashMap::new, false)), (o, m) -> {
            o.modules = m;
        }, o -> o.modules);
        CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HytaleServerConfig.class, HytaleServerConfig::new).versioned()).codecVersion(3)).append(new KeyedCodec<String>("ServerName", Codec.STRING), (o, s) -> {
            o.serverName = s;
        }, o -> o.serverName).add()).append(new KeyedCodec<String>("MOTD", Codec.STRING), (o, s) -> {
            o.motd = s;
        }, o -> o.motd).add()).append(new KeyedCodec<String>("Password", Codec.STRING), (o, s) -> {
            o.password = s;
        }, o -> o.password).add()).append(new KeyedCodec<Integer>("MaxPlayers", Codec.INTEGER), (o, i) -> {
            o.maxPlayers = i;
        }, o -> o.maxPlayers).add()).append(new KeyedCodec<Integer>("MaxViewRadius", Codec.INTEGER), (o, i) -> {
            o.maxViewRadius = i;
        }, o -> o.maxViewRadius).add()).append(new KeyedCodec<Defaults>("Defaults", Defaults.CODEC), (o, obj) -> {
            o.defaults = obj;
        }, o -> o.defaults).add()).append(new KeyedCodec<TimeoutProfile>("ConnectionTimeouts", TimeoutProfile.CODEC), (o, m) -> {
            o.connectionTimeouts = m;
        }, o -> o.connectionTimeouts).add()).append(new KeyedCodec<RateLimitConfig>("RateLimit", RateLimitConfig.CODEC), (o, m) -> {
            o.rateLimitConfig = m;
        }, o -> o.rateLimitConfig).add()).append(new KeyedCodec("Modules", new MapCodec<Module, ConcurrentHashMap>(Module.CODEC, ConcurrentHashMap::new, false)), (o, m) -> {
            o.modules = m;
            o.unmodifiableModules = Collections.unmodifiableMap(m);
        }, o -> o.modules).add()).append(new KeyedCodec("LogLevels", new MapCodec(Codec.LOG_LEVEL, ConcurrentHashMap::new, false)), (o, m) -> {
            o.logLevels = m;
            o.unmodifiableLogLevels = Collections.unmodifiableMap(o.logLevels);
        }, o -> o.logLevels).add()).append(new KeyedCodec("Plugins", new ObjectMapCodec<PluginIdentifier, ModConfig, Object2ObjectOpenHashMap>(ModConfig.CODEC, Object2ObjectOpenHashMap::new, PluginIdentifier::toString, PluginIdentifier::fromString, false)), (o, i) -> {
            o.legacyPluginConfig = i;
        }, o -> null).setVersionRange(0, 2).add()).append(new KeyedCodec("Mods", new ObjectMapCodec<PluginIdentifier, ModConfig, ConcurrentHashMap>(ModConfig.CODEC, ConcurrentHashMap::new, PluginIdentifier::toString, PluginIdentifier::fromString, false)), (o, i) -> {
            o.modConfig = i;
        }, o -> o.modConfig).add()).append(new KeyedCodec<Boolean>("DisplayTmpTagsInStrings", Codec.BOOLEAN), (o, displayTmpTagsInStrings) -> {
            o.displayTmpTagsInStrings = displayTmpTagsInStrings;
        }, o -> o.displayTmpTagsInStrings).add()).append(new KeyedCodec<PlayerStorageProvider>("PlayerStorage", PlayerStorageProvider.CODEC), (o, obj) -> {
            o.playerStorageProvider = obj;
        }, o -> o.playerStorageProvider).add()).append(new KeyedCodec<BsonDocument>("AuthCredentialStore", Codec.BSON_DOCUMENT), (o, value) -> {
            o.authCredentialStoreConfig = value;
        }, o -> o.authCredentialStoreConfig).add()).append(new KeyedCodec<UpdateConfig>("Update", UpdateConfig.CODEC), (o, value) -> {
            o.updateConfig = value;
        }, o -> o.updateConfig).add()).afterDecode(config -> {
            config.defaults.hytaleServerConfig = config;
            config.connectionTimeouts.setHytaleServerConfig((HytaleServerConfig)config);
            config.rateLimitConfig.hytaleServerConfig = config;
            config.updateConfig.hytaleServerConfig = config;
            config.modules.values().forEach(m -> m.setHytaleServerConfig((HytaleServerConfig)config));
            if (config.legacyPluginConfig != null && !config.legacyPluginConfig.isEmpty()) {
                for (Map.Entry<PluginIdentifier, ModConfig> entry : config.legacyPluginConfig.entrySet()) {
                    config.modConfig.putIfAbsent(entry.getKey(), entry.getValue());
                }
                config.legacyPluginConfig = null;
                config.markChanged();
            }
        })).build();
    }

    public static class Defaults {
        public static final KeyedCodec<String> WORLD = new KeyedCodec<String>("World", Codec.STRING);
        public static final KeyedCodec<GameMode> GAMEMODE = new KeyedCodec<GameMode>("GameMode", ProtocolCodecs.GAMEMODE_LEGACY);
        public static final BuilderCodec<Defaults> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Defaults.class, Defaults::new).addField(WORLD, (o, i) -> {
            o.world = i;
        }, o -> o.world)).addField(GAMEMODE, (o, s) -> {
            o.gameMode = s;
        }, o -> o.gameMode)).build();
        private transient HytaleServerConfig hytaleServerConfig;
        private String world = "default";
        private GameMode gameMode = GameMode.Adventure;

        private Defaults() {
        }

        private Defaults(HytaleServerConfig hytaleServerConfig) {
            this.hytaleServerConfig = hytaleServerConfig;
        }

        public String getWorld() {
            return this.world;
        }

        public void setWorld(String world) {
            this.world = world;
            this.hytaleServerConfig.markChanged();
        }

        public GameMode getGameMode() {
            return this.gameMode;
        }

        public void setGameMode(GameMode gameMode) {
            this.gameMode = gameMode;
            this.hytaleServerConfig.markChanged();
        }
    }

    public static class TimeoutProfile {
        private static final TimeoutProfile SINGLEPLAYER_DEFAULTS = new TimeoutProfile(Duration.ofSeconds(30L), Duration.ofSeconds(60L), Duration.ofSeconds(60L), Duration.ofSeconds(60L), Duration.ofSeconds(30L), Duration.ofSeconds(60L), Duration.ofSeconds(120L), Duration.ofSeconds(30L), Duration.ofSeconds(300L), Duration.ofSeconds(300L), Duration.ofSeconds(120L));
        private static final TimeoutProfile MULTIPLAYER_DEFAULTS = new TimeoutProfile(Duration.ofSeconds(15L), Duration.ofSeconds(30L), Duration.ofSeconds(30L), Duration.ofSeconds(30L), Duration.ofSeconds(15L), Duration.ofSeconds(45L), Duration.ofSeconds(60L), Duration.ofSeconds(15L), Duration.ofSeconds(120L), Duration.ofSeconds(120L), Duration.ofSeconds(60L));
        public static final Codec<TimeoutProfile> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TimeoutProfile.class, TimeoutProfile::new).addField(new KeyedCodec("InitialTimeout", Codec.DURATION), (o, d) -> {
            o.initial = d;
        }, o -> o.initial)).addField(new KeyedCodec("AuthTimeout", Codec.DURATION), (o, d) -> {
            o.auth = d;
        }, o -> o.auth)).addField(new KeyedCodec("AuthGrantTimeout", Codec.DURATION), (o, d) -> {
            o.authGrant = d;
        }, o -> o.authGrant)).addField(new KeyedCodec("AuthTokenTimeout", Codec.DURATION), (o, d) -> {
            o.authToken = d;
        }, o -> o.authToken)).addField(new KeyedCodec("AuthServerExchangeTimeout", Codec.DURATION), (o, d) -> {
            o.authServerExchange = d;
        }, o -> o.authServerExchange)).addField(new KeyedCodec("PasswordTimeout", Codec.DURATION), (o, d) -> {
            o.password = d;
        }, o -> o.password)).addField(new KeyedCodec("PlayTimeout", Codec.DURATION), (o, d) -> {
            o.play = d;
        }, o -> o.play)).addField(new KeyedCodec("SetupWorldSettings", Codec.DURATION), (o, d) -> {
            o.setupWorldSettings = d;
        }, o -> o.setupWorldSettings)).addField(new KeyedCodec("SetupAssetsRequest", Codec.DURATION), (o, d) -> {
            o.setupAssetsRequest = d;
        }, o -> o.setupAssetsRequest)).addField(new KeyedCodec("SetupSendAssets", Codec.DURATION), (o, d) -> {
            o.setupSendAssets = d;
        }, o -> o.setupSendAssets)).addField(new KeyedCodec("SetupAddToUniverse", Codec.DURATION), (o, d) -> {
            o.setupAddToUniverse = d;
        }, o -> o.setupAddToUniverse)).build();
        private Duration initial;
        private Duration auth;
        private Duration authGrant;
        private Duration authToken;
        private Duration authServerExchange;
        private Duration password;
        private Duration play;
        private Duration setupWorldSettings;
        private Duration setupAssetsRequest;
        private Duration setupSendAssets;
        private Duration setupAddToUniverse;
        private transient HytaleServerConfig hytaleServerConfig;

        public static TimeoutProfile defaults() {
            return Constants.SINGLEPLAYER ? SINGLEPLAYER_DEFAULTS : MULTIPLAYER_DEFAULTS;
        }

        public TimeoutProfile() {
        }

        public TimeoutProfile(HytaleServerConfig hytaleServerConfig) {
            this.hytaleServerConfig = hytaleServerConfig;
        }

        private TimeoutProfile(Duration initial, Duration auth, Duration authGrant, Duration authToken, Duration authServerExchange, Duration password, Duration play, Duration worldSettings, Duration assetsRequest, Duration sendAssets, Duration addToUniverse) {
            this.initial = initial;
            this.auth = auth;
            this.authGrant = authGrant;
            this.authToken = authToken;
            this.authServerExchange = authServerExchange;
            this.password = password;
            this.play = play;
            this.setupWorldSettings = worldSettings;
            this.setupAssetsRequest = assetsRequest;
            this.setupSendAssets = sendAssets;
            this.setupAddToUniverse = addToUniverse;
        }

        public Duration getInitial() {
            return this.initial != null ? this.initial : TimeoutProfile.defaults().initial;
        }

        public void setInitial(Duration d) {
            this.initial = d;
            this.markChanged();
        }

        public Duration getAuth() {
            return this.auth != null ? this.auth : TimeoutProfile.defaults().auth;
        }

        public void setAuth(Duration d) {
            this.auth = d;
            this.markChanged();
        }

        public Duration getAuthGrant() {
            return this.authGrant != null ? this.authGrant : TimeoutProfile.defaults().authGrant;
        }

        public void setAuthGrant(Duration d) {
            this.authGrant = d;
            this.markChanged();
        }

        public Duration getAuthToken() {
            return this.authToken != null ? this.authToken : TimeoutProfile.defaults().authToken;
        }

        public void setAuthToken(Duration d) {
            this.authToken = d;
            this.markChanged();
        }

        public Duration getAuthServerExchange() {
            return this.authServerExchange != null ? this.authServerExchange : TimeoutProfile.defaults().authServerExchange;
        }

        public void setAuthServerExchange(Duration d) {
            this.authServerExchange = d;
            this.markChanged();
        }

        public Duration getPassword() {
            return this.password != null ? this.password : TimeoutProfile.defaults().password;
        }

        public void setPassword(Duration d) {
            this.password = d;
            this.markChanged();
        }

        public Duration getPlay() {
            return this.play != null ? this.play : TimeoutProfile.defaults().play;
        }

        public void setPlay(Duration d) {
            this.play = d;
            this.markChanged();
        }

        public Duration getSetupWorldSettings() {
            return this.setupWorldSettings != null ? this.setupWorldSettings : TimeoutProfile.defaults().setupWorldSettings;
        }

        public void setSetupWorldSettings(Duration d) {
            this.setupWorldSettings = d;
            this.markChanged();
        }

        public Duration getSetupAssetsRequest() {
            return this.setupAssetsRequest != null ? this.setupAssetsRequest : TimeoutProfile.defaults().setupAssetsRequest;
        }

        public void setSetupAssetsRequest(Duration d) {
            this.setupAssetsRequest = d;
            this.markChanged();
        }

        public Duration getSetupSendAssets() {
            return this.setupSendAssets != null ? this.setupSendAssets : TimeoutProfile.defaults().setupSendAssets;
        }

        public void setSetupSendAssets(Duration d) {
            this.setupSendAssets = d;
            this.markChanged();
        }

        public Duration getSetupAddToUniverse() {
            return this.setupAddToUniverse != null ? this.setupAddToUniverse : TimeoutProfile.defaults().setupAddToUniverse;
        }

        public void setSetupAddToUniverse(Duration d) {
            this.setupAddToUniverse = d;
            this.markChanged();
        }

        private void markChanged() {
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        void setHytaleServerConfig(HytaleServerConfig hytaleServerConfig) {
            this.hytaleServerConfig = hytaleServerConfig;
        }
    }

    public static class RateLimitConfig {
        public static final int DEFAULT_PACKETS_PER_SECOND = 2000;
        public static final int DEFAULT_BURST_CAPACITY = 500;
        public static final Codec<RateLimitConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RateLimitConfig.class, RateLimitConfig::new).addField(new KeyedCodec<Boolean>("Enabled", Codec.BOOLEAN), (o, b) -> {
            o.enabled = b;
        }, o -> o.enabled)).addField(new KeyedCodec<Integer>("PacketsPerSecond", Codec.INTEGER), (o, i) -> {
            o.packetsPerSecond = i;
        }, o -> o.packetsPerSecond)).addField(new KeyedCodec<Integer>("BurstCapacity", Codec.INTEGER), (o, i) -> {
            o.burstCapacity = i;
        }, o -> o.burstCapacity)).build();
        private Boolean enabled;
        private Integer packetsPerSecond;
        private Integer burstCapacity;
        transient HytaleServerConfig hytaleServerConfig;

        public RateLimitConfig() {
        }

        public RateLimitConfig(HytaleServerConfig hytaleServerConfig) {
            this.hytaleServerConfig = hytaleServerConfig;
        }

        public boolean isEnabled() {
            return this.enabled != null ? this.enabled : true;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public int getPacketsPerSecond() {
            return this.packetsPerSecond != null ? this.packetsPerSecond : 2000;
        }

        public void setPacketsPerSecond(int packetsPerSecond) {
            this.packetsPerSecond = packetsPerSecond;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public int getBurstCapacity() {
            return this.burstCapacity != null ? this.burstCapacity : 500;
        }

        public void setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }
    }

    public static class UpdateConfig {
        public static final int DEFAULT_CHECK_INTERVAL_SECONDS = 3600;
        public static final Codec<UpdateConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UpdateConfig.class, UpdateConfig::new).addField(new KeyedCodec<Boolean>("Enabled", Codec.BOOLEAN), (o, b) -> {
            o.enabled = b;
        }, o -> o.enabled)).addField(new KeyedCodec<Integer>("CheckIntervalSeconds", Codec.INTEGER), (o, i) -> {
            o.checkIntervalSeconds = i;
        }, o -> o.checkIntervalSeconds)).addField(new KeyedCodec<Boolean>("NotifyPlayersOnAvailable", Codec.BOOLEAN), (o, b) -> {
            o.notifyPlayersOnAvailable = b;
        }, o -> o.notifyPlayersOnAvailable)).addField(new KeyedCodec<String>("Patchline", Codec.STRING), (o, s) -> {
            o.patchline = s;
        }, o -> o.patchline)).addField(new KeyedCodec<Boolean>("RunBackupBeforeUpdate", Codec.BOOLEAN), (o, b) -> {
            o.runBackupBeforeUpdate = b;
        }, o -> o.runBackupBeforeUpdate)).addField(new KeyedCodec<Boolean>("BackupConfigBeforeUpdate", Codec.BOOLEAN), (o, b) -> {
            o.backupConfigBeforeUpdate = b;
        }, o -> o.backupConfigBeforeUpdate)).addField(new KeyedCodec<AutoApplyMode>("AutoApplyMode", new EnumCodec<AutoApplyMode>(AutoApplyMode.class)), (o, m) -> {
            o.autoApplyMode = m;
        }, o -> o.autoApplyMode)).addField(new KeyedCodec<Integer>("AutoApplyDelayMinutes", Codec.INTEGER), (o, i) -> {
            o.autoApplyDelayMinutes = i;
        }, o -> o.autoApplyDelayMinutes)).build();
        private Boolean enabled;
        private Integer checkIntervalSeconds;
        private Boolean notifyPlayersOnAvailable;
        private String patchline;
        private Boolean runBackupBeforeUpdate;
        private Boolean backupConfigBeforeUpdate;
        private AutoApplyMode autoApplyMode;
        private Integer autoApplyDelayMinutes;
        transient HytaleServerConfig hytaleServerConfig;

        public UpdateConfig() {
        }

        public UpdateConfig(HytaleServerConfig hytaleServerConfig) {
            this.hytaleServerConfig = hytaleServerConfig;
        }

        public boolean isEnabled() {
            return this.enabled != null ? this.enabled : true;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public int getCheckIntervalSeconds() {
            return this.checkIntervalSeconds != null ? this.checkIntervalSeconds : 3600;
        }

        public void setCheckIntervalSeconds(int checkIntervalSeconds) {
            this.checkIntervalSeconds = checkIntervalSeconds;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public boolean isNotifyPlayersOnAvailable() {
            return this.notifyPlayersOnAvailable != null ? this.notifyPlayersOnAvailable : true;
        }

        public void setNotifyPlayersOnAvailable(boolean notifyPlayersOnAvailable) {
            this.notifyPlayersOnAvailable = notifyPlayersOnAvailable;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        @Nullable
        public String getPatchline() {
            return this.patchline;
        }

        public void setPatchline(@Nullable String patchline) {
            this.patchline = patchline;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public boolean isRunBackupBeforeUpdate() {
            return this.runBackupBeforeUpdate != null ? this.runBackupBeforeUpdate : true;
        }

        public void setRunBackupBeforeUpdate(boolean runBackupBeforeUpdate) {
            this.runBackupBeforeUpdate = runBackupBeforeUpdate;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public boolean isBackupConfigBeforeUpdate() {
            return this.backupConfigBeforeUpdate != null ? this.backupConfigBeforeUpdate : true;
        }

        public void setBackupConfigBeforeUpdate(boolean backupConfigBeforeUpdate) {
            this.backupConfigBeforeUpdate = backupConfigBeforeUpdate;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        @Nonnull
        public AutoApplyMode getAutoApplyMode() {
            return this.autoApplyMode != null ? this.autoApplyMode : AutoApplyMode.DISABLED;
        }

        public void setAutoApplyMode(@Nonnull AutoApplyMode autoApplyMode) {
            this.autoApplyMode = autoApplyMode;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public int getAutoApplyDelayMinutes() {
            return this.autoApplyDelayMinutes != null ? this.autoApplyDelayMinutes : 30;
        }

        public void setAutoApplyDelayMinutes(int autoApplyDelayMinutes) {
            this.autoApplyDelayMinutes = autoApplyDelayMinutes;
            if (this.hytaleServerConfig != null) {
                this.hytaleServerConfig.markChanged();
            }
        }

        public static enum AutoApplyMode {
            DISABLED,
            WHEN_EMPTY,
            SCHEDULED;

        }
    }

    public static class Module {
        @Nonnull
        protected static BuilderCodec.Builder<Module> BUILDER_CODEC_BUILDER = (BuilderCodec.Builder)BuilderCodec.builder(Module.class, Module::new).addField(new KeyedCodec<Boolean>("Enabled", Codec.BOOLEAN), (o, i) -> {
            o.enabled = i;
        }, o -> o.enabled);
        @Nonnull
        protected static BuilderCodec<Module> BUILDER_CODEC = BUILDER_CODEC_BUILDER.build();
        @Nonnull
        public static final DocumentContainingCodec<Module> CODEC = new DocumentContainingCodec<Module>(BUILDER_CODEC, (o, i) -> {
            o.document = i;
        }, o -> o.document);
        private transient HytaleServerConfig hytaleServerConfig;
        private Boolean enabled;
        @Nonnull
        private Map<String, Module> modules = new ConcurrentHashMap<String, Module>();
        @Nonnull
        private BsonDocument document = new BsonDocument();

        private Module() {
        }

        private Module(@Nonnull HytaleServerConfig hytaleServerConfig) {
            this.hytaleServerConfig = hytaleServerConfig;
        }

        public boolean isEnabled(boolean def) {
            return this.enabled != null ? this.enabled : def;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            this.hytaleServerConfig.markChanged();
        }

        public Boolean getEnabled() {
            return this.enabled;
        }

        @Nonnull
        public Map<String, Module> getModules() {
            return Collections.unmodifiableMap(this.modules);
        }

        @Nonnull
        public Module getModule(@Nonnull String moduleName) {
            return this.modules.computeIfAbsent(moduleName, k -> new Module(this.hytaleServerConfig));
        }

        public void setModules(@Nonnull Map<String, Module> modules) {
            this.modules = modules;
            this.hytaleServerConfig.markChanged();
        }

        @Nonnull
        public BsonDocument getDocument() {
            return this.document;
        }

        @Nullable
        public <T> T decode(@Nonnull Codec<T> codec) {
            return codec.decode(this.document);
        }

        public <T> void encode(@Nonnull Codec<T> codec, @Nonnull T t) {
            this.document = codec.encode(t).asDocument();
        }

        @Nonnull
        public <T> Optional<T> getData(@Nonnull KeyedCodec<T> keyedCodec) {
            return keyedCodec.get(this.document);
        }

        @Nullable
        public <T> T getDataOrNull(@Nonnull KeyedCodec<T> keyedCodec) {
            return keyedCodec.getOrNull(this.document);
        }

        public <T> T getDataNow(@Nonnull KeyedCodec<T> keyedCodec) {
            return keyedCodec.getNow(this.document);
        }

        public <T> void put(@Nonnull KeyedCodec<T> keyedCodec, T t) {
            keyedCodec.put(this.document, t);
            this.hytaleServerConfig.markChanged();
        }

        public void setDocument(@Nonnull BsonDocument document) {
            this.document = document;
            this.hytaleServerConfig.markChanged();
        }

        void setHytaleServerConfig(@Nonnull HytaleServerConfig hytaleServerConfig) {
            this.hytaleServerConfig = hytaleServerConfig;
            this.modules.values().forEach(module -> module.setHytaleServerConfig(hytaleServerConfig));
        }
    }

    public static class ModConfig {
        public static final BuilderCodec<ModConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ModConfig.class, ModConfig::new).append(new KeyedCodec<Boolean>("Enabled", Codec.BOOLEAN), (modConfig, enabled) -> {
            modConfig.enabled = enabled;
        }, modConfig -> modConfig.enabled).add()).append(new KeyedCodec<SemverRange>("RequiredVersion", SemverRange.CODEC), (modConfig, semverRange) -> {
            modConfig.requiredVersion = semverRange;
        }, modConfig -> modConfig.requiredVersion).add()).build();
        @Nullable
        private Boolean enabled;
        @Nullable
        private SemverRange requiredVersion;

        @Nullable
        public Boolean getEnabled() {
            return this.enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        @Nullable
        public SemverRange getRequiredVersion() {
            return this.requiredVersion;
        }

        public void setRequiredVersion(SemverRange requiredVersion) {
            this.requiredVersion = requiredVersion;
        }

        public static void setBoot(HytaleServerConfig serverConfig, PluginIdentifier identifier, boolean enabled) {
            serverConfig.getModConfig().computeIfAbsent((PluginIdentifier)identifier, (Function<PluginIdentifier, ModConfig>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$setBoot$0(com.hypixel.hytale.common.plugin.PluginIdentifier ), (Lcom/hypixel/hytale/common/plugin/PluginIdentifier;)Lcom/hypixel/hytale/server/core/HytaleServerConfig$ModConfig;)()).enabled = enabled;
        }

        private static /* synthetic */ ModConfig lambda$setBoot$0(PluginIdentifier id) {
            return new ModConfig();
        }
    }
}

