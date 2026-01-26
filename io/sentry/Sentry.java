/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.BaggageHeader;
import io.sentry.Breadcrumb;
import io.sentry.CheckIn;
import io.sentry.DefaultScopesStorage;
import io.sentry.DefaultSpanFactory;
import io.sentry.ExternalOptions;
import io.sentry.Hint;
import io.sentry.HubScopesWrapper;
import io.sentry.IDistributionApi;
import io.sentry.IHub;
import io.sentry.ILogger;
import io.sentry.IOptionsObserver;
import io.sentry.IReplayApi;
import io.sentry.IScope;
import io.sentry.IScopes;
import io.sentry.IScopesStorage;
import io.sentry.ISentryClient;
import io.sentry.ISentryExecutorService;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Integration;
import io.sentry.JavaMemoryCollector;
import io.sentry.MovePreviousSession;
import io.sentry.NoOpLogger;
import io.sentry.NoOpScopes;
import io.sentry.NoOpScopesLifecycleToken;
import io.sentry.NoOpScopesStorage;
import io.sentry.OptionsContainer;
import io.sentry.PreviousSessionFinalizer;
import io.sentry.SamplingContext;
import io.sentry.Scope;
import io.sentry.ScopeCallback;
import io.sentry.ScopeType;
import io.sentry.Scopes;
import io.sentry.ScopesAdapter;
import io.sentry.ScopesStorageFactory;
import io.sentry.SentryAppStartProfilingOptions;
import io.sentry.SentryClient;
import io.sentry.SentryEvent;
import io.sentry.SentryExecutorService;
import io.sentry.SentryFeedbackOptions;
import io.sentry.SentryLevel;
import io.sentry.SentryOpenTelemetryMode;
import io.sentry.SentryOptions;
import io.sentry.SentryTraceHeader;
import io.sentry.SystemOutLogger;
import io.sentry.TracesSamplingDecision;
import io.sentry.TransactionContext;
import io.sentry.TransactionOptions;
import io.sentry.UserFeedback;
import io.sentry.backpressure.BackpressureMonitor;
import io.sentry.backpressure.NoOpBackpressureMonitor;
import io.sentry.cache.EnvelopeCache;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.cache.PersistingScopeObserver;
import io.sentry.config.PropertiesProviderFactory;
import io.sentry.internal.debugmeta.NoOpDebugMetaLoader;
import io.sentry.internal.debugmeta.ResourcesDebugMetaLoader;
import io.sentry.internal.modules.CompositeModulesLoader;
import io.sentry.internal.modules.IModulesLoader;
import io.sentry.internal.modules.ManifestModulesLoader;
import io.sentry.internal.modules.NoOpModulesLoader;
import io.sentry.internal.modules.ResourcesModulesLoader;
import io.sentry.logger.ILoggerApi;
import io.sentry.opentelemetry.OpenTelemetryUtil;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.DebugMetaPropertiesApplier;
import io.sentry.util.FileUtils;
import io.sentry.util.InitUtil;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import io.sentry.util.SentryRandom;
import io.sentry.util.thread.IThreadChecker;
import io.sentry.util.thread.NoOpThreadChecker;
import io.sentry.util.thread.ThreadChecker;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Sentry {
    @NotNull
    private static volatile IScopesStorage scopesStorage = NoOpScopesStorage.getInstance();
    @NotNull
    private static volatile IScopes rootScopes = NoOpScopes.getInstance();
    @NotNull
    private static final IScope globalScope = new Scope(SentryOptions.empty());
    private static final boolean GLOBAL_HUB_DEFAULT_MODE = false;
    private static volatile boolean globalHubMode = false;
    @ApiStatus.Internal
    @NotNull
    public static final String APP_START_PROFILING_CONFIG_FILE_NAME = "app_start_profiling_config";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final long classCreationTimestamp = System.currentTimeMillis();
    private static final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

    private Sentry() {
    }

    @Deprecated
    @ApiStatus.Internal
    @NotNull
    public static IHub getCurrentHub() {
        return new HubScopesWrapper(Sentry.getCurrentScopes());
    }

    @ApiStatus.Internal
    @NotNull
    public static IScopes getCurrentScopes() {
        return Sentry.getCurrentScopes(true);
    }

    @ApiStatus.Internal
    @NotNull
    public static IScopes getCurrentScopes(boolean ensureForked) {
        if (globalHubMode) {
            return rootScopes;
        }
        @Nullable IScopes scopes = Sentry.getScopesStorage().get();
        if (scopes == null || scopes.isNoOp()) {
            if (!ensureForked) {
                return NoOpScopes.getInstance();
            }
            scopes = rootScopes.forkedScopes("getCurrentScopes");
            Sentry.getScopesStorage().set(scopes);
        }
        return scopes;
    }

    @NotNull
    private static IScopesStorage getScopesStorage() {
        return scopesStorage;
    }

    @ApiStatus.Internal
    @NotNull
    public static IScopes forkedRootScopes(@NotNull String creator) {
        if (globalHubMode) {
            return rootScopes;
        }
        return rootScopes.forkedScopes(creator);
    }

    @NotNull
    public static IScopes forkedScopes(@NotNull String creator) {
        return Sentry.getCurrentScopes().forkedScopes(creator);
    }

    @NotNull
    public static IScopes forkedCurrentScope(@NotNull String creator) {
        return Sentry.getCurrentScopes().forkedCurrentScope(creator);
    }

    @Deprecated
    @ApiStatus.Internal
    @NotNull
    public static ISentryLifecycleToken setCurrentHub(@NotNull IHub hub) {
        return Sentry.setCurrentScopes(hub);
    }

    @ApiStatus.Internal
    @NotNull
    public static ISentryLifecycleToken setCurrentScopes(@NotNull IScopes scopes) {
        return Sentry.getScopesStorage().set(scopes);
    }

    @NotNull
    public static IScope getGlobalScope() {
        return globalScope;
    }

    public static boolean isEnabled() {
        return Sentry.getCurrentScopes().isEnabled();
    }

    public static void init() {
        Sentry.init((SentryOptions options) -> options.setEnableExternalConfiguration(true), false);
    }

    public static void init(@NotNull String dsn) {
        Sentry.init((SentryOptions options) -> options.setDsn(dsn));
    }

    public static <T extends SentryOptions> void init(@NotNull OptionsContainer<T> clazz, @NotNull OptionsConfiguration<T> optionsConfiguration) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Sentry.init(clazz, optionsConfiguration, false);
    }

    public static <T extends SentryOptions> void init(@NotNull OptionsContainer<T> clazz, @NotNull OptionsConfiguration<T> optionsConfiguration, boolean globalHubMode) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        SentryOptions options = (SentryOptions)clazz.createInstance();
        Sentry.applyOptionsConfiguration(optionsConfiguration, options);
        Sentry.init(options, globalHubMode);
    }

    public static void init(@NotNull OptionsConfiguration<SentryOptions> optionsConfiguration) {
        Sentry.init(optionsConfiguration, false);
    }

    public static void init(@NotNull OptionsConfiguration<SentryOptions> optionsConfiguration, boolean globalHubMode) {
        SentryOptions options = new SentryOptions();
        Sentry.applyOptionsConfiguration(optionsConfiguration, options);
        Sentry.init(options, globalHubMode);
    }

    private static <T extends SentryOptions> void applyOptionsConfiguration(OptionsConfiguration<T> optionsConfiguration, T options) {
        try {
            optionsConfiguration.configure(options);
        }
        catch (Throwable t) {
            options.getLogger().log(SentryLevel.ERROR, "Error in the 'OptionsConfiguration.configure' callback.", t);
        }
    }

    @ApiStatus.Internal
    public static void init(@NotNull SentryOptions options) {
        Sentry.init(options, false);
    }

    private static void init(@NotNull SentryOptions options, boolean globalHubMode) {
        block17: {
            try (@NotNull ISentryLifecycleToken ignored = lock.acquire();){
                if (!options.getClass().getName().equals("io.sentry.android.core.SentryAndroidOptions") && Platform.isAndroid()) {
                    throw new IllegalArgumentException("You are running Android. Please, use SentryAndroid.init. " + options.getClass().getName());
                }
                if (!Sentry.preInitConfigurations(options)) {
                    return;
                }
                @Nullable Boolean globalHubModeFromOptions = options.isGlobalHubMode();
                boolean globalHubModeToUse = globalHubModeFromOptions != null ? globalHubModeFromOptions : globalHubMode;
                options.getLogger().log(SentryLevel.INFO, "GlobalHubMode: '%s'", String.valueOf(globalHubModeToUse));
                Sentry.globalHubMode = globalHubModeToUse;
                Sentry.initFatalLogger(options);
                boolean shouldInit = InitUtil.shouldInit(globalScope.getOptions(), options, Sentry.isEnabled());
                if (shouldInit) {
                    if (Sentry.isEnabled()) {
                        options.getLogger().log(SentryLevel.WARNING, "Sentry has been already initialized. Previous configuration will be overwritten.", new Object[0]);
                    }
                    IScopes scopes = Sentry.getCurrentScopes();
                    scopes.close(true);
                    globalScope.replaceOptions(options);
                    Scope rootScope = new Scope(options);
                    Scope rootIsolationScope = new Scope(options);
                    rootScopes = new Scopes(rootScope, rootIsolationScope, globalScope, "Sentry.init");
                    Sentry.initLogger(options);
                    Sentry.initForOpenTelemetryMaybe(options);
                    Sentry.getScopesStorage().set(rootScopes);
                    Sentry.initConfigurations(options);
                    globalScope.bindClient(new SentryClient(options));
                    if (options.getExecutorService().isClosed()) {
                        options.setExecutorService(new SentryExecutorService(options));
                        options.getExecutorService().prewarm();
                    }
                    try {
                        options.getExecutorService().submit(() -> options.loadLazyFields());
                    }
                    catch (RejectedExecutionException e) {
                        options.getLogger().log(SentryLevel.DEBUG, "Failed to call the executor. Lazy fields will not be loaded. Did you call Sentry.close()?", e);
                    }
                    Sentry.movePreviousSession(options);
                    for (Integration integration : options.getIntegrations()) {
                        try {
                            integration.register(ScopesAdapter.getInstance(), options);
                        }
                        catch (Throwable t) {
                            options.getLogger().log(SentryLevel.WARNING, "Failed to register the integration " + integration.getClass().getName(), t);
                        }
                    }
                    Sentry.notifyOptionsObservers(options);
                    Sentry.finalizePreviousSession(options, ScopesAdapter.getInstance());
                    Sentry.handleAppStartProfilingConfig(options, options.getExecutorService());
                    options.getLogger().log(SentryLevel.DEBUG, "Using openTelemetryMode %s", new Object[]{options.getOpenTelemetryMode()});
                    options.getLogger().log(SentryLevel.DEBUG, "Using span factory %s", options.getSpanFactory().getClass().getName());
                    options.getLogger().log(SentryLevel.DEBUG, "Using scopes storage %s", scopesStorage.getClass().getName());
                    break block17;
                }
                options.getLogger().log(SentryLevel.WARNING, "This init call has been ignored due to priority being too low.", new Object[0]);
            }
        }
    }

    private static void initForOpenTelemetryMaybe(SentryOptions options) {
        OpenTelemetryUtil.updateOpenTelemetryModeIfAuto(options, new LoadClass());
        if (SentryOpenTelemetryMode.OFF == options.getOpenTelemetryMode()) {
            options.setSpanFactory(new DefaultSpanFactory());
        }
        Sentry.initScopesStorage(options);
        OpenTelemetryUtil.applyIgnoredSpanOrigins(options);
    }

    private static void initLogger(@NotNull SentryOptions options) {
        if (options.isDebug() && options.getLogger() instanceof NoOpLogger) {
            options.setLogger(new SystemOutLogger());
        }
    }

    private static void initFatalLogger(@NotNull SentryOptions options) {
        if (options.getFatalLogger() instanceof NoOpLogger) {
            options.setFatalLogger(new SystemOutLogger());
        }
    }

    private static void initScopesStorage(SentryOptions options) {
        Sentry.getScopesStorage().close();
        scopesStorage = SentryOpenTelemetryMode.OFF == options.getOpenTelemetryMode() ? new DefaultScopesStorage() : ScopesStorageFactory.create(new LoadClass(), NoOpLogger.getInstance());
    }

    private static void handleAppStartProfilingConfig(@NotNull SentryOptions options, @NotNull ISentryExecutorService sentryExecutorService) {
        try {
            sentryExecutorService.submit(() -> {
                block15: {
                    String cacheDirPath = options.getCacheDirPathWithoutDsn();
                    if (cacheDirPath != null) {
                        @NotNull File appStartProfilingConfigFile = new File(cacheDirPath, APP_START_PROFILING_CONFIG_FILE_NAME);
                        try {
                            FileUtils.deleteRecursively(appStartProfilingConfigFile);
                            if (!options.isEnableAppStartProfiling() && !options.isStartProfilerOnAppStart()) {
                                return;
                            }
                            if (!options.isStartProfilerOnAppStart() && !options.isTracingEnabled()) {
                                options.getLogger().log(SentryLevel.INFO, "Tracing is disabled and app start profiling will not start.", new Object[0]);
                                return;
                            }
                            if (!appStartProfilingConfigFile.createNewFile()) break block15;
                            @NotNull TracesSamplingDecision appStartSamplingDecision = options.isEnableAppStartProfiling() ? Sentry.sampleAppStartProfiling(options) : new TracesSamplingDecision(false);
                            @NotNull SentryAppStartProfilingOptions appStartProfilingOptions = new SentryAppStartProfilingOptions(options, appStartSamplingDecision);
                            try (FileOutputStream outputStream = new FileOutputStream(appStartProfilingConfigFile);
                                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)outputStream, UTF_8));){
                                options.getSerializer().serialize(appStartProfilingOptions, writer);
                            }
                        }
                        catch (Throwable e) {
                            options.getLogger().log(SentryLevel.ERROR, "Unable to create app start profiling config file. ", e);
                        }
                    }
                }
            });
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. App start profiling config will not be changed. Did you call Sentry.close()?", e);
        }
    }

    @NotNull
    private static TracesSamplingDecision sampleAppStartProfiling(@NotNull SentryOptions options) {
        TransactionContext appStartTransactionContext = new TransactionContext("app.launch", "profile");
        appStartTransactionContext.setForNextAppStart(true);
        SamplingContext appStartSamplingContext = new SamplingContext(appStartTransactionContext, null, SentryRandom.current().nextDouble(), null);
        return options.getInternalTracesSampler().sample(appStartSamplingContext);
    }

    private static void movePreviousSession(@NotNull SentryOptions options) {
        try {
            options.getExecutorService().submit(new MovePreviousSession(options));
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.DEBUG, "Failed to move previous session.", e);
        }
    }

    private static void finalizePreviousSession(@NotNull SentryOptions options, @NotNull IScopes scopes) {
        try {
            options.getExecutorService().submit(new PreviousSessionFinalizer(options, scopes));
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.DEBUG, "Failed to finalize previous session.", e);
        }
    }

    private static void notifyOptionsObservers(@NotNull SentryOptions options) {
        try {
            options.getExecutorService().submit(() -> {
                for (IOptionsObserver observer : options.getOptionsObservers()) {
                    observer.setRelease(options.getRelease());
                    observer.setProguardUuid(options.getProguardUuid());
                    observer.setSdkVersion(options.getSdkVersion());
                    observer.setDist(options.getDist());
                    observer.setEnvironment(options.getEnvironment());
                    observer.setTags(options.getTags());
                    observer.setReplayErrorSampleRate(options.getSessionReplay().getOnErrorSampleRate());
                }
                @Nullable PersistingScopeObserver scopeCache = options.findPersistingScopeObserver();
                if (scopeCache != null) {
                    scopeCache.resetCache();
                }
            });
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.DEBUG, "Failed to notify options observers.", e);
        }
    }

    private static boolean preInitConfigurations(@NotNull SentryOptions options) {
        if (options.isEnableExternalConfiguration()) {
            options.merge(ExternalOptions.from(PropertiesProviderFactory.create(), options.getLogger()));
        }
        String dsn = options.getDsn();
        if (!options.isEnabled() || dsn != null && dsn.isEmpty()) {
            Sentry.close();
            return false;
        }
        if (dsn == null) {
            throw new IllegalArgumentException("DSN is required. Use empty string or set enabled to false in SentryOptions to disable SDK.");
        }
        options.retrieveParsedDsn();
        return true;
    }

    private static void initConfigurations(@NotNull SentryOptions options) {
        @NotNull ILogger logger = options.getLogger();
        logger.log(SentryLevel.INFO, "Initializing SDK with DSN: '%s'", options.getDsn());
        String outboxPath = options.getOutboxPath();
        if (outboxPath != null) {
            File outboxDir = new File(outboxPath);
            options.getRuntimeManager().runWithRelaxedPolicy(() -> outboxDir.mkdirs());
        } else {
            logger.log(SentryLevel.INFO, "No outbox dir path is defined in options.", new Object[0]);
        }
        String cacheDirPath = options.getCacheDirPath();
        if (cacheDirPath != null) {
            File cacheDir = new File(cacheDirPath);
            options.getRuntimeManager().runWithRelaxedPolicy(() -> cacheDir.mkdirs());
            IEnvelopeCache envelopeCache = options.getEnvelopeDiskCache();
            if (envelopeCache instanceof NoOpEnvelopeCache) {
                options.setEnvelopeDiskCache(EnvelopeCache.create(options));
            }
        }
        String profilingTracesDirPath = options.getProfilingTracesDirPath();
        if ((options.isProfilingEnabled() || options.isContinuousProfilingEnabled()) && profilingTracesDirPath != null) {
            File profilingTracesDir = new File(profilingTracesDirPath);
            options.getRuntimeManager().runWithRelaxedPolicy(() -> profilingTracesDir.mkdirs());
            try {
                options.getExecutorService().submit(() -> {
                    File @NotNull [] oldTracesDirContent = profilingTracesDir.listFiles();
                    if (oldTracesDirContent == null) {
                        return;
                    }
                    for (File f : oldTracesDirContent) {
                        if (f.lastModified() >= classCreationTimestamp - TimeUnit.MINUTES.toMillis(5L)) continue;
                        FileUtils.deleteRecursively(f);
                    }
                });
            }
            catch (RejectedExecutionException e) {
                options.getLogger().log(SentryLevel.ERROR, "Failed to call the executor. Old profiles will not be deleted. Did you call Sentry.close()?", e);
            }
        }
        @NotNull IModulesLoader modulesLoader = options.getModulesLoader();
        if (!options.isSendModules()) {
            options.setModulesLoader(NoOpModulesLoader.getInstance());
        } else if (modulesLoader instanceof NoOpModulesLoader) {
            options.setModulesLoader(new CompositeModulesLoader(Arrays.asList(new ManifestModulesLoader(options.getLogger()), new ResourcesModulesLoader(options.getLogger())), options.getLogger()));
        }
        if (options.getDebugMetaLoader() instanceof NoOpDebugMetaLoader) {
            options.setDebugMetaLoader(new ResourcesDebugMetaLoader(options.getLogger()));
        }
        @Nullable List<Properties> propertiesList = options.getDebugMetaLoader().loadDebugMeta();
        DebugMetaPropertiesApplier.apply(options, propertiesList);
        IThreadChecker threadChecker = options.getThreadChecker();
        if (threadChecker instanceof NoOpThreadChecker) {
            options.setThreadChecker(ThreadChecker.getInstance());
        }
        if (options.getPerformanceCollectors().isEmpty()) {
            options.addPerformanceCollector(new JavaMemoryCollector());
        }
        if (options.isEnableBackpressureHandling() && Platform.isJvm()) {
            if (options.getBackpressureMonitor() instanceof NoOpBackpressureMonitor) {
                options.setBackpressureMonitor(new BackpressureMonitor(options, ScopesAdapter.getInstance()));
            }
            options.getBackpressureMonitor().start();
        }
        Sentry.initJvmContinuousProfiling(options);
        options.getLogger().log(SentryLevel.INFO, "Continuous profiler is enabled %s mode: %s", new Object[]{options.isContinuousProfilingEnabled(), options.getProfileLifecycle()});
    }

    private static void initJvmContinuousProfiling(@NotNull SentryOptions options) {
        InitUtil.initializeProfiler(options);
        InitUtil.initializeProfileConverter(options);
    }

    public static void close() {
        try (@NotNull ISentryLifecycleToken ignored = lock.acquire();){
            IScopes scopes = Sentry.getCurrentScopes();
            rootScopes = NoOpScopes.getInstance();
            Sentry.getScopesStorage().close();
            scopes.close(false);
        }
    }

    @NotNull
    public static SentryId captureEvent(@NotNull SentryEvent event) {
        return Sentry.getCurrentScopes().captureEvent(event);
    }

    @NotNull
    public static SentryId captureEvent(@NotNull SentryEvent event, @NotNull ScopeCallback callback) {
        return Sentry.getCurrentScopes().captureEvent(event, callback);
    }

    @NotNull
    public static SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
        return Sentry.getCurrentScopes().captureEvent(event, hint);
    }

    @NotNull
    public static SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return Sentry.getCurrentScopes().captureEvent(event, hint, callback);
    }

    @NotNull
    public static SentryId captureMessage(@NotNull String message) {
        return Sentry.getCurrentScopes().captureMessage(message);
    }

    @NotNull
    public static SentryId captureMessage(@NotNull String message, @NotNull ScopeCallback callback) {
        return Sentry.getCurrentScopes().captureMessage(message, callback);
    }

    @NotNull
    public static SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
        return Sentry.getCurrentScopes().captureMessage(message, level);
    }

    @NotNull
    public static SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
        return Sentry.getCurrentScopes().captureMessage(message, level, callback);
    }

    @NotNull
    public static SentryId captureFeedback(@NotNull Feedback feedback) {
        return Sentry.getCurrentScopes().captureFeedback(feedback);
    }

    @NotNull
    public static SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint) {
        return Sentry.getCurrentScopes().captureFeedback(feedback, hint);
    }

    @NotNull
    public static SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
        return Sentry.getCurrentScopes().captureFeedback(feedback, hint, callback);
    }

    @NotNull
    public static SentryId captureException(@NotNull Throwable throwable) {
        return Sentry.getCurrentScopes().captureException(throwable);
    }

    @NotNull
    public static SentryId captureException(@NotNull Throwable throwable, @NotNull ScopeCallback callback) {
        return Sentry.getCurrentScopes().captureException(throwable, callback);
    }

    @NotNull
    public static SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
        return Sentry.getCurrentScopes().captureException(throwable, hint);
    }

    @NotNull
    public static SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
        return Sentry.getCurrentScopes().captureException(throwable, hint, callback);
    }

    public static void captureUserFeedback(@NotNull UserFeedback userFeedback) {
        Sentry.getCurrentScopes().captureUserFeedback(userFeedback);
    }

    public static void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
        Sentry.getCurrentScopes().addBreadcrumb(breadcrumb, hint);
    }

    public static void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
        Sentry.getCurrentScopes().addBreadcrumb(breadcrumb);
    }

    public static void addBreadcrumb(@NotNull String message) {
        Sentry.getCurrentScopes().addBreadcrumb(message);
    }

    public static void addBreadcrumb(@NotNull String message, @NotNull String category) {
        Sentry.getCurrentScopes().addBreadcrumb(message, category);
    }

    public static void setLevel(@Nullable SentryLevel level) {
        Sentry.getCurrentScopes().setLevel(level);
    }

    public static void setTransaction(@Nullable String transaction) {
        Sentry.getCurrentScopes().setTransaction(transaction);
    }

    public static void setUser(@Nullable User user) {
        Sentry.getCurrentScopes().setUser(user);
    }

    public static void setFingerprint(@NotNull List<String> fingerprint) {
        Sentry.getCurrentScopes().setFingerprint(fingerprint);
    }

    public static void clearBreadcrumbs() {
        Sentry.getCurrentScopes().clearBreadcrumbs();
    }

    public static void setTag(@Nullable String key, @Nullable String value) {
        Sentry.getCurrentScopes().setTag(key, value);
    }

    public static void removeTag(@Nullable String key) {
        Sentry.getCurrentScopes().removeTag(key);
    }

    public static void setExtra(@Nullable String key, @Nullable String value) {
        Sentry.getCurrentScopes().setExtra(key, value);
    }

    public static void removeExtra(@Nullable String key) {
        Sentry.getCurrentScopes().removeExtra(key);
    }

    @NotNull
    public static SentryId getLastEventId() {
        return Sentry.getCurrentScopes().getLastEventId();
    }

    @NotNull
    public static ISentryLifecycleToken pushScope() {
        if (!globalHubMode) {
            return Sentry.getCurrentScopes().pushScope();
        }
        return NoOpScopesLifecycleToken.getInstance();
    }

    @NotNull
    public static ISentryLifecycleToken pushIsolationScope() {
        if (!globalHubMode) {
            return Sentry.getCurrentScopes().pushIsolationScope();
        }
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Deprecated
    public static void popScope() {
        if (!globalHubMode) {
            Sentry.getCurrentScopes().popScope();
        }
    }

    public static void withScope(@NotNull ScopeCallback callback) {
        Sentry.getCurrentScopes().withScope(callback);
    }

    public static void withIsolationScope(@NotNull ScopeCallback callback) {
        Sentry.getCurrentScopes().withIsolationScope(callback);
    }

    public static void configureScope(@NotNull ScopeCallback callback) {
        Sentry.configureScope(null, callback);
    }

    public static void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
        Sentry.getCurrentScopes().configureScope(scopeType, callback);
    }

    public static void bindClient(@NotNull ISentryClient client) {
        Sentry.getCurrentScopes().bindClient(client);
    }

    public static boolean isHealthy() {
        return Sentry.getCurrentScopes().isHealthy();
    }

    public static void flush(long timeoutMillis) {
        Sentry.getCurrentScopes().flush(timeoutMillis);
    }

    public static void startSession() {
        Sentry.getCurrentScopes().startSession();
    }

    public static void endSession() {
        Sentry.getCurrentScopes().endSession();
    }

    @NotNull
    public static ITransaction startTransaction(@NotNull String name, @NotNull String operation) {
        return Sentry.getCurrentScopes().startTransaction(name, operation);
    }

    @NotNull
    public static ITransaction startTransaction(@NotNull String name, @NotNull String operation, @NotNull TransactionOptions transactionOptions) {
        return Sentry.getCurrentScopes().startTransaction(name, operation, transactionOptions);
    }

    @NotNull
    public static ITransaction startTransaction(@NotNull String name, @NotNull String operation, @Nullable String description, @NotNull TransactionOptions transactionOptions) {
        ITransaction transaction = Sentry.getCurrentScopes().startTransaction(name, operation, transactionOptions);
        transaction.setDescription(description);
        return transaction;
    }

    @NotNull
    public static ITransaction startTransaction(@NotNull TransactionContext transactionContexts) {
        return Sentry.getCurrentScopes().startTransaction(transactionContexts);
    }

    @NotNull
    public static ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
        return Sentry.getCurrentScopes().startTransaction(transactionContext, transactionOptions);
    }

    @ApiStatus.Experimental
    public static void startProfiler() {
        Sentry.getCurrentScopes().startProfiler();
    }

    @ApiStatus.Experimental
    public static void stopProfiler() {
        Sentry.getCurrentScopes().stopProfiler();
    }

    @Nullable
    public static ISpan getSpan() {
        if (globalHubMode && Platform.isAndroid()) {
            return Sentry.getCurrentScopes().getTransaction();
        }
        return Sentry.getCurrentScopes().getSpan();
    }

    @Nullable
    public static Boolean isCrashedLastRun() {
        return Sentry.getCurrentScopes().isCrashedLastRun();
    }

    public static void reportFullyDisplayed() {
        Sentry.getCurrentScopes().reportFullyDisplayed();
    }

    @Nullable
    public static TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
        return Sentry.getCurrentScopes().continueTrace(sentryTrace, baggageHeaders);
    }

    @Nullable
    public static SentryTraceHeader getTraceparent() {
        return Sentry.getCurrentScopes().getTraceparent();
    }

    @Nullable
    public static BaggageHeader getBaggage() {
        return Sentry.getCurrentScopes().getBaggage();
    }

    @NotNull
    public static SentryId captureCheckIn(@NotNull CheckIn checkIn) {
        return Sentry.getCurrentScopes().captureCheckIn(checkIn);
    }

    @NotNull
    public static ILoggerApi logger() {
        return Sentry.getCurrentScopes().logger();
    }

    @NotNull
    public static IReplayApi replay() {
        return Sentry.getCurrentScopes().getScope().getOptions().getReplayController();
    }

    @NotNull
    public static IDistributionApi distribution() {
        return Sentry.getCurrentScopes().getScope().getOptions().getDistributionController();
    }

    public static void showUserFeedbackDialog() {
        Sentry.showUserFeedbackDialog(null);
    }

    public static void showUserFeedbackDialog(@Nullable SentryFeedbackOptions.OptionsConfigurator configurator) {
        Sentry.showUserFeedbackDialog(null, configurator);
    }

    public static void showUserFeedbackDialog(@Nullable SentryId associatedEventId, @Nullable SentryFeedbackOptions.OptionsConfigurator configurator) {
        @NotNull SentryOptions options = Sentry.getCurrentScopes().getOptions();
        options.getFeedbackOptions().getDialogHandler().showDialog(associatedEventId, configurator);
    }

    public static void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
        Sentry.getCurrentScopes().addFeatureFlag(flag, result);
    }

    public static interface OptionsConfiguration<T extends SentryOptions> {
        public void configure(@NotNull T var1);
    }
}

