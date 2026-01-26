/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.Breadcrumb;
import io.sentry.CompositePerformanceCollector;
import io.sentry.DataCategory;
import io.sentry.DiagnosticLogger;
import io.sentry.Dsn;
import io.sentry.DuplicateEventDetectionEventProcessor;
import io.sentry.EnvelopeReader;
import io.sentry.EventProcessor;
import io.sentry.ExperimentalOptions;
import io.sentry.ExternalOptions;
import io.sentry.FilterString;
import io.sentry.FullyDisplayedReporter;
import io.sentry.Hint;
import io.sentry.IConnectionStatusProvider;
import io.sentry.IContinuousProfiler;
import io.sentry.IDistributionApi;
import io.sentry.IEnvelopeReader;
import io.sentry.ILogger;
import io.sentry.IOptionsObserver;
import io.sentry.IPerformanceCollector;
import io.sentry.IProfileConverter;
import io.sentry.IScopeObserver;
import io.sentry.ISentryExecutorService;
import io.sentry.ISentryLifecycleToken;
import io.sentry.ISerializer;
import io.sentry.ISocketTagger;
import io.sentry.ISpanFactory;
import io.sentry.ITransactionProfiler;
import io.sentry.ITransportFactory;
import io.sentry.IVersionDetector;
import io.sentry.InitPriority;
import io.sentry.Instrumenter;
import io.sentry.Integration;
import io.sentry.JsonSerializer;
import io.sentry.MainEventProcessor;
import io.sentry.NoOpCompositePerformanceCollector;
import io.sentry.NoOpConnectionStatusProvider;
import io.sentry.NoOpContinuousProfiler;
import io.sentry.NoOpDistributionApi;
import io.sentry.NoOpEnvelopeReader;
import io.sentry.NoOpLogger;
import io.sentry.NoOpProfileConverter;
import io.sentry.NoOpReplayController;
import io.sentry.NoOpSentryExecutorService;
import io.sentry.NoOpSerializer;
import io.sentry.NoOpSocketTagger;
import io.sentry.NoOpSpanFactory;
import io.sentry.NoOpTransactionProfiler;
import io.sentry.NoOpTransportFactory;
import io.sentry.NoopVersionDetector;
import io.sentry.ProfileLifecycle;
import io.sentry.ReplayController;
import io.sentry.SamplingContext;
import io.sentry.ScopeType;
import io.sentry.SentryAutoDateProvider;
import io.sentry.SentryDateProvider;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEvent;
import io.sentry.SentryExecutorService;
import io.sentry.SentryFeedbackOptions;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryOpenTelemetryMode;
import io.sentry.SentryReplayEvent;
import io.sentry.SentryReplayOptions;
import io.sentry.SentryRuntimeEventProcessor;
import io.sentry.ShutdownHookIntegration;
import io.sentry.SpanFactoryFactory;
import io.sentry.SpotlightIntegration;
import io.sentry.TracesSampler;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.backpressure.IBackpressureMonitor;
import io.sentry.backpressure.NoOpBackpressureMonitor;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.cache.PersistingScopeObserver;
import io.sentry.clientreport.ClientReportRecorder;
import io.sentry.clientreport.DiscardReason;
import io.sentry.clientreport.IClientReportRecorder;
import io.sentry.clientreport.NoOpClientReportRecorder;
import io.sentry.internal.debugmeta.IDebugMetaLoader;
import io.sentry.internal.debugmeta.NoOpDebugMetaLoader;
import io.sentry.internal.gestures.GestureTargetLocator;
import io.sentry.internal.modules.IModulesLoader;
import io.sentry.internal.modules.NoOpModulesLoader;
import io.sentry.internal.viewhierarchy.ViewHierarchyExporter;
import io.sentry.logger.DefaultLoggerBatchProcessorFactory;
import io.sentry.logger.ILoggerBatchProcessorFactory;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.ITransportGate;
import io.sentry.transport.NoOpEnvelopeCache;
import io.sentry.transport.NoOpTransportGate;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.LazyEvaluator;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import io.sentry.util.SampleRateUtils;
import io.sentry.util.StringUtils;
import io.sentry.util.runtime.IRuntimeManager;
import io.sentry.util.runtime.NeutralRuntimeManager;
import io.sentry.util.thread.IThreadChecker;
import io.sentry.util.thread.NoOpThreadChecker;
import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.net.ssl.SSLSocketFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

public class SentryOptions {
    @ApiStatus.Internal
    @NotNull
    public static final String DEFAULT_PROPAGATION_TARGETS = ".*";
    static final SentryLevel DEFAULT_DIAGNOSTIC_LEVEL = SentryLevel.DEBUG;
    private static final String DEFAULT_ENVIRONMENT = "production";
    @NotNull
    private final List<EventProcessor> eventProcessors = new CopyOnWriteArrayList<EventProcessor>();
    @NotNull
    private final Set<Class<? extends Throwable>> ignoredExceptionsForType = new CopyOnWriteArraySet<Class<? extends Throwable>>();
    @Nullable
    private List<FilterString> ignoredErrors = null;
    @NotNull
    private final List<Integration> integrations = new CopyOnWriteArrayList<Integration>();
    @NotNull
    private final Set<String> bundleIds = new CopyOnWriteArraySet<String>();
    @Nullable
    private String dsn;
    @NotNull
    private final LazyEvaluator<Dsn> parsedDsn = new LazyEvaluator<Dsn>(() -> new Dsn(this.dsn));
    @Nullable
    private String dsnHash;
    private long shutdownTimeoutMillis = 2000L;
    private long flushTimeoutMillis = 15000L;
    private long sessionFlushTimeoutMillis = 15000L;
    private boolean debug;
    @NotNull
    private ILogger logger = NoOpLogger.getInstance();
    @ApiStatus.Experimental
    @NotNull
    private ILogger fatalLogger = NoOpLogger.getInstance();
    @NotNull
    private SentryLevel diagnosticLevel = DEFAULT_DIAGNOSTIC_LEVEL;
    @NotNull
    private final LazyEvaluator<ISerializer> serializer = new LazyEvaluator<ISerializer>(() -> new JsonSerializer(this));
    @NotNull
    private final LazyEvaluator<IEnvelopeReader> envelopeReader = new LazyEvaluator<IEnvelopeReader>(() -> new EnvelopeReader(this.serializer.getValue()));
    private int maxDepth = 100;
    @Nullable
    private String sentryClientName;
    @Nullable
    private BeforeSendCallback beforeSend;
    @Nullable
    private BeforeSendCallback beforeSendFeedback;
    @Nullable
    private BeforeSendTransactionCallback beforeSendTransaction;
    @Nullable
    private BeforeSendReplayCallback beforeSendReplay;
    @Nullable
    private BeforeBreadcrumbCallback beforeBreadcrumb;
    @Nullable
    private OnDiscardCallback onDiscard;
    @Nullable
    private String cacheDirPath;
    private int maxCacheItems;
    private int maxQueueSize = this.maxCacheItems = 30;
    private int maxBreadcrumbs = 100;
    private int maxFeatureFlags = 100;
    @Nullable
    private String release;
    @Nullable
    private String environment;
    @Nullable
    private Proxy proxy;
    @Nullable
    private Double sampleRate;
    @Nullable
    private Double tracesSampleRate;
    @Nullable
    private TracesSamplerCallback tracesSampler;
    @Nullable
    private volatile TracesSampler internalTracesSampler;
    @NotNull
    private final List<String> inAppExcludes = new CopyOnWriteArrayList<String>();
    @NotNull
    private final List<String> inAppIncludes = new CopyOnWriteArrayList<String>();
    @NotNull
    private ITransportFactory transportFactory = NoOpTransportFactory.getInstance();
    @NotNull
    private ITransportGate transportGate = NoOpTransportGate.getInstance();
    @Nullable
    private String dist;
    private boolean attachThreads;
    private boolean attachStacktrace = true;
    private boolean enableAutoSessionTracking = true;
    private long sessionTrackingIntervalMillis = 30000L;
    @Nullable
    private String distinctId;
    @Nullable
    private String serverName;
    private boolean attachServerName = true;
    private boolean enableUncaughtExceptionHandler = true;
    private boolean printUncaughtStackTrace = false;
    @NotNull
    private ISentryExecutorService executorService = NoOpSentryExecutorService.getInstance();
    private int connectionTimeoutMillis = 30000;
    private int readTimeoutMillis = 30000;
    @NotNull
    private IEnvelopeCache envelopeDiskCache = NoOpEnvelopeCache.getInstance();
    @Nullable
    private SdkVersion sdkVersion;
    private boolean sendDefaultPii = false;
    @Nullable
    private SSLSocketFactory sslSocketFactory;
    @NotNull
    private final List<IScopeObserver> observers = new CopyOnWriteArrayList<IScopeObserver>();
    @NotNull
    private final List<IOptionsObserver> optionsObservers = new CopyOnWriteArrayList<IOptionsObserver>();
    private boolean enableExternalConfiguration;
    @NotNull
    private final @NotNull Map<String, @NotNull String> tags = new ConcurrentHashMap<String, String>();
    private long maxAttachmentSize = 0x1400000L;
    private boolean enableDeduplication = true;
    private boolean enableEventSizeLimiting = false;
    @Nullable
    private OnOversizedEventCallback onOversizedEvent;
    private int maxSpans = 1000;
    private boolean enableShutdownHook = true;
    @NotNull
    private RequestSize maxRequestBodySize = RequestSize.NONE;
    private boolean traceSampling = true;
    @Nullable
    private Double profilesSampleRate;
    @Nullable
    private ProfilesSamplerCallback profilesSampler;
    private long maxTraceFileSize = 0x500000L;
    @NotNull
    private ITransactionProfiler transactionProfiler = NoOpTransactionProfiler.getInstance();
    @NotNull
    private IContinuousProfiler continuousProfiler = NoOpContinuousProfiler.getInstance();
    @NotNull
    private IProfileConverter profilerConverter = NoOpProfileConverter.getInstance();
    @Nullable
    private List<String> tracePropagationTargets = null;
    @NotNull
    private final List<String> defaultTracePropagationTargets = Collections.singletonList(".*");
    private boolean propagateTraceparent = false;
    @Nullable
    private String proguardUuid;
    @Nullable
    private Long idleTimeout = 3000L;
    @NotNull
    private final List<String> contextTags = new CopyOnWriteArrayList<String>();
    private boolean sendClientReports = true;
    @NotNull
    IClientReportRecorder clientReportRecorder = new ClientReportRecorder(this);
    @NotNull
    private IModulesLoader modulesLoader = NoOpModulesLoader.getInstance();
    @NotNull
    private IDebugMetaLoader debugMetaLoader = NoOpDebugMetaLoader.getInstance();
    private boolean enableUserInteractionTracing = false;
    private boolean enableUserInteractionBreadcrumbs = true;
    @NotNull
    private Instrumenter instrumenter = Instrumenter.SENTRY;
    @NotNull
    private final List<GestureTargetLocator> gestureTargetLocators = new ArrayList<GestureTargetLocator>();
    @NotNull
    private final List<ViewHierarchyExporter> viewHierarchyExporters = new ArrayList<ViewHierarchyExporter>();
    @NotNull
    private IThreadChecker threadChecker = NoOpThreadChecker.getInstance();
    private boolean traceOptionsRequests = true;
    @ApiStatus.Internal
    @NotNull
    private final LazyEvaluator<SentryDateProvider> dateProvider = new LazyEvaluator<SentryDateProvider>(() -> new SentryAutoDateProvider());
    @NotNull
    private final List<IPerformanceCollector> performanceCollectors = new ArrayList<IPerformanceCollector>();
    @NotNull
    private CompositePerformanceCollector compositePerformanceCollector = NoOpCompositePerformanceCollector.getInstance();
    private boolean enableTimeToFullDisplayTracing = false;
    @NotNull
    private FullyDisplayedReporter fullyDisplayedReporter = FullyDisplayedReporter.getInstance();
    @NotNull
    private IConnectionStatusProvider connectionStatusProvider = new NoOpConnectionStatusProvider();
    private boolean enabled = true;
    private boolean enablePrettySerializationOutput = true;
    private boolean sendModules = true;
    @Nullable
    private BeforeEnvelopeCallback beforeEnvelopeCallback;
    private boolean enableSpotlight = false;
    @Nullable
    private String spotlightConnectionUrl;
    private boolean enableScopePersistence = true;
    @ApiStatus.Experimental
    @Nullable
    private List<FilterString> ignoredCheckIns = null;
    @ApiStatus.Experimental
    @Nullable
    private List<FilterString> ignoredSpanOrigins = null;
    @Nullable
    private List<FilterString> ignoredTransactions = null;
    @ApiStatus.Experimental
    @NotNull
    private IBackpressureMonitor backpressureMonitor = NoOpBackpressureMonitor.getInstance();
    private boolean enableBackpressureHandling = true;
    private boolean enableAppStartProfiling = false;
    @NotNull
    private ISpanFactory spanFactory = NoOpSpanFactory.getInstance();
    private int profilingTracesHz = 101;
    @ApiStatus.Experimental
    @Nullable
    private Cron cron = null;
    @NotNull
    private final ExperimentalOptions experimental;
    @NotNull
    private ReplayController replayController = NoOpReplayController.getInstance();
    @NotNull
    private IDistributionApi distributionController = NoOpDistributionApi.getInstance();
    @ApiStatus.Experimental
    private boolean enableScreenTracking = true;
    @NotNull
    private ScopeType defaultScopeType = ScopeType.ISOLATION;
    @NotNull
    private InitPriority initPriority = InitPriority.MEDIUM;
    private boolean forceInit = false;
    @Nullable
    private Boolean globalHubMode = null;
    @NotNull
    protected final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
    @NotNull
    private SentryOpenTelemetryMode openTelemetryMode = SentryOpenTelemetryMode.AUTO;
    @NotNull
    private SentryReplayOptions sessionReplay;
    @NotNull
    private SentryFeedbackOptions feedbackOptions;
    @ApiStatus.Experimental
    private boolean captureOpenTelemetryEvents = false;
    @NotNull
    private IVersionDetector versionDetector = NoopVersionDetector.getInstance();
    @Nullable
    private Double profileSessionSampleRate;
    @NotNull
    private ProfileLifecycle profileLifecycle = ProfileLifecycle.MANUAL;
    private boolean startProfilerOnAppStart = false;
    private long deadlineTimeout = 30000L;
    @NotNull
    private Logs logs = new Logs();
    @NotNull
    private ISocketTagger socketTagger = NoOpSocketTagger.getInstance();
    @NotNull
    private IRuntimeManager runtimeManager = new NeutralRuntimeManager();
    @Nullable
    private String profilingTracesDirPath;
    @NotNull
    private DistributionOptions distribution = new DistributionOptions();

    @NotNull
    public IProfileConverter getProfilerConverter() {
        return this.profilerConverter;
    }

    public void setProfilerConverter(@NotNull IProfileConverter profilerConverter) {
        this.profilerConverter = profilerConverter;
    }

    public void addEventProcessor(@NotNull EventProcessor eventProcessor) {
        this.eventProcessors.add(eventProcessor);
    }

    @NotNull
    public List<EventProcessor> getEventProcessors() {
        return this.eventProcessors;
    }

    public void addIntegration(@NotNull Integration integration) {
        this.integrations.add(integration);
    }

    @NotNull
    public List<Integration> getIntegrations() {
        return this.integrations;
    }

    @Nullable
    public String getDsn() {
        return this.dsn;
    }

    @ApiStatus.Internal
    @NotNull
    Dsn retrieveParsedDsn() throws IllegalArgumentException {
        return this.parsedDsn.getValue();
    }

    public void setDsn(@Nullable String dsn) {
        this.dsn = dsn;
        this.parsedDsn.resetValue();
        this.dsnHash = StringUtils.calculateStringHash(this.dsn, this.logger);
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @NotNull
    public ILogger getLogger() {
        return this.logger;
    }

    public void setLogger(@Nullable ILogger logger) {
        this.logger = logger == null ? NoOpLogger.getInstance() : new DiagnosticLogger(this, logger);
    }

    @ApiStatus.Experimental
    @NotNull
    public ILogger getFatalLogger() {
        return this.fatalLogger;
    }

    @ApiStatus.Experimental
    public void setFatalLogger(@Nullable ILogger logger) {
        this.fatalLogger = logger == null ? NoOpLogger.getInstance() : logger;
    }

    @NotNull
    public SentryLevel getDiagnosticLevel() {
        return this.diagnosticLevel;
    }

    public void setDiagnosticLevel(@Nullable SentryLevel diagnosticLevel) {
        this.diagnosticLevel = diagnosticLevel != null ? diagnosticLevel : DEFAULT_DIAGNOSTIC_LEVEL;
    }

    @NotNull
    public ISerializer getSerializer() {
        return this.serializer.getValue();
    }

    public void setSerializer(@Nullable ISerializer serializer) {
        this.serializer.setValue(serializer != null ? serializer : NoOpSerializer.getInstance());
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @NotNull
    public IEnvelopeReader getEnvelopeReader() {
        return this.envelopeReader.getValue();
    }

    public void setEnvelopeReader(@Nullable IEnvelopeReader envelopeReader) {
        this.envelopeReader.setValue(envelopeReader != null ? envelopeReader : NoOpEnvelopeReader.getInstance());
    }

    public long getShutdownTimeoutMillis() {
        return this.shutdownTimeoutMillis;
    }

    public void setShutdownTimeoutMillis(long shutdownTimeoutMillis) {
        this.shutdownTimeoutMillis = shutdownTimeoutMillis;
    }

    @Nullable
    public String getSentryClientName() {
        return this.sentryClientName;
    }

    public void setSentryClientName(@Nullable String sentryClientName) {
        this.sentryClientName = sentryClientName;
    }

    @Nullable
    public BeforeSendCallback getBeforeSend() {
        return this.beforeSend;
    }

    public void setBeforeSend(@Nullable BeforeSendCallback beforeSend) {
        this.beforeSend = beforeSend;
    }

    @Nullable
    public BeforeSendTransactionCallback getBeforeSendTransaction() {
        return this.beforeSendTransaction;
    }

    public void setBeforeSendTransaction(@Nullable BeforeSendTransactionCallback beforeSendTransaction) {
        this.beforeSendTransaction = beforeSendTransaction;
    }

    @Nullable
    public BeforeSendCallback getBeforeSendFeedback() {
        return this.beforeSendFeedback;
    }

    public void setBeforeSendFeedback(@Nullable BeforeSendCallback beforeSendFeedback) {
        this.beforeSendFeedback = beforeSendFeedback;
    }

    @Nullable
    public BeforeSendReplayCallback getBeforeSendReplay() {
        return this.beforeSendReplay;
    }

    public void setBeforeSendReplay(@Nullable BeforeSendReplayCallback beforeSendReplay) {
        this.beforeSendReplay = beforeSendReplay;
    }

    @Nullable
    public BeforeBreadcrumbCallback getBeforeBreadcrumb() {
        return this.beforeBreadcrumb;
    }

    public void setBeforeBreadcrumb(@Nullable BeforeBreadcrumbCallback beforeBreadcrumb) {
        this.beforeBreadcrumb = beforeBreadcrumb;
    }

    @Nullable
    public OnDiscardCallback getOnDiscard() {
        return this.onDiscard;
    }

    public void setOnDiscard(@Nullable OnDiscardCallback onDiscard) {
        this.onDiscard = onDiscard;
    }

    @Nullable
    public String getCacheDirPath() {
        if (this.cacheDirPath == null || this.cacheDirPath.isEmpty()) {
            return null;
        }
        return this.dsnHash != null ? new File(this.cacheDirPath, this.dsnHash).getAbsolutePath() : this.cacheDirPath;
    }

    @Nullable
    String getCacheDirPathWithoutDsn() {
        if (this.cacheDirPath == null || this.cacheDirPath.isEmpty()) {
            return null;
        }
        return this.cacheDirPath;
    }

    @Nullable
    public String getOutboxPath() {
        String cacheDirPath = this.getCacheDirPath();
        if (cacheDirPath == null) {
            return null;
        }
        return new File(cacheDirPath, "outbox").getAbsolutePath();
    }

    public void setCacheDirPath(@Nullable String cacheDirPath) {
        this.cacheDirPath = cacheDirPath;
    }

    public int getMaxBreadcrumbs() {
        return this.maxBreadcrumbs;
    }

    public void setMaxBreadcrumbs(int maxBreadcrumbs) {
        this.maxBreadcrumbs = maxBreadcrumbs;
    }

    public int getMaxFeatureFlags() {
        return this.maxFeatureFlags;
    }

    public void setMaxFeatureFlags(int maxFeatureFlags) {
        this.maxFeatureFlags = maxFeatureFlags;
    }

    @Nullable
    public String getRelease() {
        return this.release;
    }

    public void setRelease(@Nullable String release) {
        this.release = release;
    }

    @Nullable
    public String getEnvironment() {
        return this.environment != null ? this.environment : DEFAULT_ENVIRONMENT;
    }

    public void setEnvironment(@Nullable String environment) {
        this.environment = environment;
    }

    @Nullable
    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(@Nullable Proxy proxy) {
        this.proxy = proxy;
    }

    @Nullable
    public Double getSampleRate() {
        return this.sampleRate;
    }

    public void setSampleRate(@Nullable Double sampleRate) {
        if (!SampleRateUtils.isValidSampleRate(sampleRate)) {
            throw new IllegalArgumentException("The value " + sampleRate + " is not valid. Use null to disable or values >= 0.0 and <= 1.0.");
        }
        this.sampleRate = sampleRate;
    }

    @Nullable
    public Double getTracesSampleRate() {
        return this.tracesSampleRate;
    }

    public void setTracesSampleRate(@Nullable Double tracesSampleRate) {
        if (!SampleRateUtils.isValidTracesSampleRate(tracesSampleRate)) {
            throw new IllegalArgumentException("The value " + tracesSampleRate + " is not valid. Use null to disable or values between 0.0 and 1.0.");
        }
        this.tracesSampleRate = tracesSampleRate;
    }

    @Nullable
    public TracesSamplerCallback getTracesSampler() {
        return this.tracesSampler;
    }

    public void setTracesSampler(@Nullable TracesSamplerCallback tracesSampler) {
        this.tracesSampler = tracesSampler;
    }

    @ApiStatus.Internal
    @NotNull
    public TracesSampler getInternalTracesSampler() {
        if (this.internalTracesSampler == null) {
            try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
                if (this.internalTracesSampler == null) {
                    this.internalTracesSampler = new TracesSampler(this);
                }
            }
        }
        return this.internalTracesSampler;
    }

    @NotNull
    public List<String> getInAppExcludes() {
        return this.inAppExcludes;
    }

    public void addInAppExclude(@NotNull String exclude) {
        this.inAppExcludes.add(exclude);
    }

    @NotNull
    public List<String> getInAppIncludes() {
        return this.inAppIncludes;
    }

    public void addInAppInclude(@NotNull String include) {
        this.inAppIncludes.add(include);
    }

    @NotNull
    public ITransportFactory getTransportFactory() {
        return this.transportFactory;
    }

    public void setTransportFactory(@Nullable ITransportFactory transportFactory) {
        this.transportFactory = transportFactory != null ? transportFactory : NoOpTransportFactory.getInstance();
    }

    @Nullable
    public String getDist() {
        return this.dist;
    }

    public void setDist(@Nullable String dist) {
        this.dist = dist;
    }

    @NotNull
    public ITransportGate getTransportGate() {
        return this.transportGate;
    }

    public void setTransportGate(@Nullable ITransportGate transportGate) {
        this.transportGate = transportGate != null ? transportGate : NoOpTransportGate.getInstance();
    }

    public boolean isAttachStacktrace() {
        return this.attachStacktrace;
    }

    public void setAttachStacktrace(boolean attachStacktrace) {
        this.attachStacktrace = attachStacktrace;
    }

    public boolean isAttachThreads() {
        return this.attachThreads;
    }

    public void setAttachThreads(boolean attachThreads) {
        this.attachThreads = attachThreads;
    }

    public boolean isEnableAutoSessionTracking() {
        return this.enableAutoSessionTracking;
    }

    public void setEnableAutoSessionTracking(boolean enableAutoSessionTracking) {
        this.enableAutoSessionTracking = enableAutoSessionTracking;
    }

    @Nullable
    public String getServerName() {
        return this.serverName;
    }

    public void setServerName(@Nullable String serverName) {
        this.serverName = serverName;
    }

    public boolean isAttachServerName() {
        return this.attachServerName;
    }

    public void setAttachServerName(boolean attachServerName) {
        this.attachServerName = attachServerName;
    }

    public long getSessionTrackingIntervalMillis() {
        return this.sessionTrackingIntervalMillis;
    }

    public void setSessionTrackingIntervalMillis(long sessionTrackingIntervalMillis) {
        this.sessionTrackingIntervalMillis = sessionTrackingIntervalMillis;
    }

    @Nullable
    public String getDistinctId() {
        return this.distinctId;
    }

    public void setDistinctId(@Nullable String distinctId) {
        this.distinctId = distinctId;
    }

    public long getFlushTimeoutMillis() {
        return this.flushTimeoutMillis;
    }

    public void setFlushTimeoutMillis(long flushTimeoutMillis) {
        this.flushTimeoutMillis = flushTimeoutMillis;
    }

    public boolean isEnableUncaughtExceptionHandler() {
        return this.enableUncaughtExceptionHandler;
    }

    public void setEnableUncaughtExceptionHandler(boolean enableUncaughtExceptionHandler) {
        this.enableUncaughtExceptionHandler = enableUncaughtExceptionHandler;
    }

    public boolean isPrintUncaughtStackTrace() {
        return this.printUncaughtStackTrace;
    }

    public void setPrintUncaughtStackTrace(boolean printUncaughtStackTrace) {
        this.printUncaughtStackTrace = printUncaughtStackTrace;
    }

    @ApiStatus.Internal
    @NotNull
    public ISentryExecutorService getExecutorService() {
        return this.executorService;
    }

    @ApiStatus.Internal
    @TestOnly
    public void setExecutorService(@NotNull ISentryExecutorService executorService) {
        if (executorService != null) {
            this.executorService = executorService;
        }
    }

    public int getConnectionTimeoutMillis() {
        return this.connectionTimeoutMillis;
    }

    public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
        this.connectionTimeoutMillis = connectionTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return this.readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    @NotNull
    public IEnvelopeCache getEnvelopeDiskCache() {
        return this.envelopeDiskCache;
    }

    public void setEnvelopeDiskCache(@Nullable IEnvelopeCache envelopeDiskCache) {
        this.envelopeDiskCache = envelopeDiskCache != null ? envelopeDiskCache : NoOpEnvelopeCache.getInstance();
    }

    public int getMaxQueueSize() {
        return this.maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        if (maxQueueSize > 0) {
            this.maxQueueSize = maxQueueSize;
        }
    }

    @Nullable
    public SdkVersion getSdkVersion() {
        return this.sdkVersion;
    }

    @Nullable
    public SSLSocketFactory getSslSocketFactory() {
        return this.sslSocketFactory;
    }

    public void setSslSocketFactory(@Nullable SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    @ApiStatus.Internal
    public void setSdkVersion(@Nullable SdkVersion sdkVersion) {
        @Nullable SdkVersion replaySdkVersion = this.getSessionReplay().getSdkVersion();
        if (this.sdkVersion != null && replaySdkVersion != null && this.sdkVersion.equals(replaySdkVersion)) {
            this.getSessionReplay().setSdkVersion(sdkVersion);
        }
        this.sdkVersion = sdkVersion;
    }

    public boolean isSendDefaultPii() {
        return this.sendDefaultPii;
    }

    public void setSendDefaultPii(boolean sendDefaultPii) {
        this.sendDefaultPii = sendDefaultPii;
    }

    public void addScopeObserver(@NotNull IScopeObserver observer) {
        this.observers.add(observer);
    }

    @NotNull
    public List<IScopeObserver> getScopeObservers() {
        return this.observers;
    }

    @ApiStatus.Internal
    @Nullable
    public PersistingScopeObserver findPersistingScopeObserver() {
        for (IScopeObserver observer : this.observers) {
            if (!(observer instanceof PersistingScopeObserver)) continue;
            return (PersistingScopeObserver)observer;
        }
        return null;
    }

    public void addOptionsObserver(@NotNull IOptionsObserver observer) {
        this.optionsObservers.add(observer);
    }

    @NotNull
    public List<IOptionsObserver> getOptionsObservers() {
        return this.optionsObservers;
    }

    public boolean isEnableExternalConfiguration() {
        return this.enableExternalConfiguration;
    }

    public void setEnableExternalConfiguration(boolean enableExternalConfiguration) {
        this.enableExternalConfiguration = enableExternalConfiguration;
    }

    @NotNull
    public Map<String, String> getTags() {
        return this.tags;
    }

    public void setTag(@Nullable String key, @Nullable String value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            this.tags.remove(key);
        } else {
            this.tags.put(key, value);
        }
    }

    public long getMaxAttachmentSize() {
        return this.maxAttachmentSize;
    }

    public void setMaxAttachmentSize(long maxAttachmentSize) {
        this.maxAttachmentSize = maxAttachmentSize;
    }

    public boolean isEnableDeduplication() {
        return this.enableDeduplication;
    }

    public void setEnableDeduplication(boolean enableDeduplication) {
        this.enableDeduplication = enableDeduplication;
    }

    public boolean isEnableEventSizeLimiting() {
        return this.enableEventSizeLimiting;
    }

    public void setEnableEventSizeLimiting(boolean enableEventSizeLimiting) {
        this.enableEventSizeLimiting = enableEventSizeLimiting;
    }

    @Nullable
    public OnOversizedEventCallback getOnOversizedEvent() {
        return this.onOversizedEvent;
    }

    public void setOnOversizedEvent(@Nullable OnOversizedEventCallback onOversizedEvent) {
        this.onOversizedEvent = onOversizedEvent;
    }

    public boolean isTracingEnabled() {
        return this.getTracesSampleRate() != null || this.getTracesSampler() != null;
    }

    @NotNull
    public Set<Class<? extends Throwable>> getIgnoredExceptionsForType() {
        return this.ignoredExceptionsForType;
    }

    public void addIgnoredExceptionForType(@NotNull Class<? extends Throwable> exceptionType) {
        this.ignoredExceptionsForType.add(exceptionType);
    }

    boolean containsIgnoredExceptionForType(@NotNull Throwable throwable) {
        return this.ignoredExceptionsForType.contains(throwable.getClass());
    }

    @Nullable
    public List<FilterString> getIgnoredErrors() {
        return this.ignoredErrors;
    }

    public void setIgnoredErrors(@Nullable List<String> ignoredErrors) {
        if (ignoredErrors == null) {
            this.ignoredErrors = null;
        } else {
            @NotNull ArrayList<FilterString> patterns = new ArrayList<FilterString>();
            for (String pattern : ignoredErrors) {
                if (pattern == null || pattern.isEmpty()) continue;
                patterns.add(new FilterString(pattern));
            }
            this.ignoredErrors = patterns;
        }
    }

    public void addIgnoredError(@NotNull String pattern) {
        if (this.ignoredErrors == null) {
            this.ignoredErrors = new ArrayList<FilterString>();
        }
        this.ignoredErrors.add(new FilterString(pattern));
    }

    @ApiStatus.Experimental
    public int getMaxSpans() {
        return this.maxSpans;
    }

    @ApiStatus.Experimental
    public void setMaxSpans(int maxSpans) {
        this.maxSpans = maxSpans;
    }

    public boolean isEnableShutdownHook() {
        return this.enableShutdownHook;
    }

    public void setEnableShutdownHook(boolean enableShutdownHook) {
        this.enableShutdownHook = enableShutdownHook;
    }

    public int getMaxCacheItems() {
        return this.maxCacheItems;
    }

    public void setMaxCacheItems(int maxCacheItems) {
        this.maxCacheItems = maxCacheItems;
    }

    @NotNull
    public RequestSize getMaxRequestBodySize() {
        return this.maxRequestBodySize;
    }

    public void setMaxRequestBodySize(@NotNull RequestSize maxRequestBodySize) {
        this.maxRequestBodySize = maxRequestBodySize;
    }

    @ApiStatus.Experimental
    public boolean isTraceSampling() {
        return this.traceSampling;
    }

    @Deprecated
    public void setTraceSampling(boolean traceSampling) {
        this.traceSampling = traceSampling;
    }

    public long getMaxTraceFileSize() {
        return this.maxTraceFileSize;
    }

    public void setMaxTraceFileSize(long maxTraceFileSize) {
        this.maxTraceFileSize = maxTraceFileSize;
    }

    @NotNull
    public ITransactionProfiler getTransactionProfiler() {
        return this.transactionProfiler;
    }

    public void setTransactionProfiler(@Nullable ITransactionProfiler transactionProfiler) {
        if (this.transactionProfiler == NoOpTransactionProfiler.getInstance() && transactionProfiler != null) {
            this.transactionProfiler = transactionProfiler;
        }
    }

    @NotNull
    public IContinuousProfiler getContinuousProfiler() {
        return this.continuousProfiler;
    }

    public void setContinuousProfiler(@Nullable IContinuousProfiler continuousProfiler) {
        if (this.continuousProfiler == NoOpContinuousProfiler.getInstance() && continuousProfiler != null) {
            this.continuousProfiler = continuousProfiler;
        }
    }

    public boolean isProfilingEnabled() {
        return this.profilesSampleRate != null && this.profilesSampleRate > 0.0 || this.profilesSampler != null;
    }

    @ApiStatus.Internal
    public boolean isContinuousProfilingEnabled() {
        return this.profilesSampleRate == null && this.profilesSampler == null && this.profileSessionSampleRate != null && this.profileSessionSampleRate > 0.0;
    }

    @Nullable
    public ProfilesSamplerCallback getProfilesSampler() {
        return this.profilesSampler;
    }

    public void setProfilesSampler(@Nullable ProfilesSamplerCallback profilesSampler) {
        this.profilesSampler = profilesSampler;
    }

    @Nullable
    public Double getProfilesSampleRate() {
        return this.profilesSampleRate;
    }

    public void setProfilesSampleRate(@Nullable Double profilesSampleRate) {
        if (!SampleRateUtils.isValidProfilesSampleRate(profilesSampleRate)) {
            throw new IllegalArgumentException("The value " + profilesSampleRate + " is not valid. Use null to disable or values between 0.0 and 1.0.");
        }
        this.profilesSampleRate = profilesSampleRate;
    }

    @Nullable
    public Double getProfileSessionSampleRate() {
        return this.profileSessionSampleRate;
    }

    public void setProfileSessionSampleRate(@Nullable Double profileSessionSampleRate) {
        if (!SampleRateUtils.isValidContinuousProfilesSampleRate(profileSessionSampleRate)) {
            throw new IllegalArgumentException("The value " + profileSessionSampleRate + " is not valid. Use values between 0.0 and 1.0.");
        }
        this.profileSessionSampleRate = profileSessionSampleRate;
    }

    @NotNull
    public ProfileLifecycle getProfileLifecycle() {
        return this.profileLifecycle;
    }

    public void setProfileLifecycle(@NotNull ProfileLifecycle profileLifecycle) {
        this.profileLifecycle = profileLifecycle;
        if (profileLifecycle == ProfileLifecycle.TRACE && !this.isTracingEnabled()) {
            this.logger.log(SentryLevel.WARNING, "Profiling lifecycle is set to TRACE but tracing is disabled. Profiling will not be started automatically.", new Object[0]);
        }
    }

    public boolean isStartProfilerOnAppStart() {
        return this.startProfilerOnAppStart;
    }

    public void setStartProfilerOnAppStart(boolean startProfilerOnAppStart) {
        this.startProfilerOnAppStart = startProfilerOnAppStart;
    }

    public long getDeadlineTimeout() {
        return this.deadlineTimeout;
    }

    public void setDeadlineTimeout(long deadlineTimeout) {
        this.deadlineTimeout = deadlineTimeout;
    }

    @Nullable
    public String getProfilingTracesDirPath() {
        if (this.profilingTracesDirPath != null && !this.profilingTracesDirPath.isEmpty()) {
            return this.dsnHash != null ? new File(this.profilingTracesDirPath, this.dsnHash).getAbsolutePath() : this.profilingTracesDirPath;
        }
        String cacheDirPath = this.getCacheDirPath();
        if (cacheDirPath == null) {
            return null;
        }
        return new File(cacheDirPath, "profiling_traces").getAbsolutePath();
    }

    public void setProfilingTracesDirPath(@Nullable String profilingTracesDirPath) {
        this.profilingTracesDirPath = profilingTracesDirPath;
    }

    @NotNull
    public List<String> getTracePropagationTargets() {
        if (this.tracePropagationTargets == null) {
            return this.defaultTracePropagationTargets;
        }
        return this.tracePropagationTargets;
    }

    public void setTracePropagationTargets(@Nullable List<String> tracePropagationTargets) {
        if (tracePropagationTargets == null) {
            this.tracePropagationTargets = null;
        } else {
            @NotNull ArrayList<String> filteredTracePropagationTargets = new ArrayList<String>();
            for (String target : tracePropagationTargets) {
                if (target.isEmpty()) continue;
                filteredTracePropagationTargets.add(target);
            }
            this.tracePropagationTargets = filteredTracePropagationTargets;
        }
    }

    public boolean isPropagateTraceparent() {
        return this.propagateTraceparent;
    }

    public void setPropagateTraceparent(boolean propagateTraceparent) {
        this.propagateTraceparent = propagateTraceparent;
    }

    @Nullable
    public String getProguardUuid() {
        return this.proguardUuid;
    }

    public void setProguardUuid(@Nullable String proguardUuid) {
        this.proguardUuid = proguardUuid;
    }

    public void addBundleId(@Nullable String bundleId) {
        String trimmedBundleId;
        if (bundleId != null && !(trimmedBundleId = bundleId.trim()).isEmpty()) {
            this.bundleIds.add(trimmedBundleId);
        }
    }

    @NotNull
    public Set<String> getBundleIds() {
        return this.bundleIds;
    }

    @NotNull
    public List<String> getContextTags() {
        return this.contextTags;
    }

    public void addContextTag(@NotNull String contextTag) {
        this.contextTags.add(contextTag);
    }

    @Nullable
    public Long getIdleTimeout() {
        return this.idleTimeout;
    }

    public void setIdleTimeout(@Nullable Long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public boolean isSendClientReports() {
        return this.sendClientReports;
    }

    public void setSendClientReports(boolean sendClientReports) {
        this.sendClientReports = sendClientReports;
        this.clientReportRecorder = sendClientReports ? new ClientReportRecorder(this) : new NoOpClientReportRecorder();
    }

    public boolean isEnableUserInteractionTracing() {
        return this.enableUserInteractionTracing;
    }

    public void setEnableUserInteractionTracing(boolean enableUserInteractionTracing) {
        this.enableUserInteractionTracing = enableUserInteractionTracing;
    }

    public boolean isEnableUserInteractionBreadcrumbs() {
        return this.enableUserInteractionBreadcrumbs;
    }

    public void setEnableUserInteractionBreadcrumbs(boolean enableUserInteractionBreadcrumbs) {
        this.enableUserInteractionBreadcrumbs = enableUserInteractionBreadcrumbs;
    }

    @Deprecated
    public void setInstrumenter(@NotNull Instrumenter instrumenter) {
        this.instrumenter = instrumenter;
    }

    @NotNull
    public Instrumenter getInstrumenter() {
        return this.instrumenter;
    }

    @ApiStatus.Internal
    @NotNull
    public IClientReportRecorder getClientReportRecorder() {
        return this.clientReportRecorder;
    }

    @ApiStatus.Internal
    @NotNull
    public IModulesLoader getModulesLoader() {
        return this.modulesLoader;
    }

    @ApiStatus.Internal
    public void setModulesLoader(@Nullable IModulesLoader modulesLoader) {
        this.modulesLoader = modulesLoader != null ? modulesLoader : NoOpModulesLoader.getInstance();
    }

    @ApiStatus.Internal
    @NotNull
    public IDebugMetaLoader getDebugMetaLoader() {
        return this.debugMetaLoader;
    }

    @ApiStatus.Internal
    public void setDebugMetaLoader(@Nullable IDebugMetaLoader debugMetaLoader) {
        this.debugMetaLoader = debugMetaLoader != null ? debugMetaLoader : NoOpDebugMetaLoader.getInstance();
    }

    public List<GestureTargetLocator> getGestureTargetLocators() {
        return this.gestureTargetLocators;
    }

    public void setGestureTargetLocators(@NotNull List<GestureTargetLocator> locators) {
        this.gestureTargetLocators.clear();
        this.gestureTargetLocators.addAll(locators);
    }

    @NotNull
    public final List<ViewHierarchyExporter> getViewHierarchyExporters() {
        return this.viewHierarchyExporters;
    }

    public void setViewHierarchyExporters(@NotNull List<ViewHierarchyExporter> exporters) {
        this.viewHierarchyExporters.clear();
        this.viewHierarchyExporters.addAll(exporters);
    }

    @NotNull
    public IThreadChecker getThreadChecker() {
        return this.threadChecker;
    }

    public void setThreadChecker(@NotNull IThreadChecker threadChecker) {
        this.threadChecker = threadChecker;
    }

    @ApiStatus.Internal
    @NotNull
    public CompositePerformanceCollector getCompositePerformanceCollector() {
        return this.compositePerformanceCollector;
    }

    @ApiStatus.Internal
    public void setCompositePerformanceCollector(@NotNull CompositePerformanceCollector compositePerformanceCollector) {
        this.compositePerformanceCollector = compositePerformanceCollector;
    }

    public boolean isEnableTimeToFullDisplayTracing() {
        return this.enableTimeToFullDisplayTracing;
    }

    public void setEnableTimeToFullDisplayTracing(boolean enableTimeToFullDisplayTracing) {
        this.enableTimeToFullDisplayTracing = enableTimeToFullDisplayTracing;
    }

    @ApiStatus.Internal
    @NotNull
    public FullyDisplayedReporter getFullyDisplayedReporter() {
        return this.fullyDisplayedReporter;
    }

    @ApiStatus.Internal
    @TestOnly
    public void setFullyDisplayedReporter(@NotNull FullyDisplayedReporter fullyDisplayedReporter) {
        this.fullyDisplayedReporter = fullyDisplayedReporter;
    }

    public boolean isTraceOptionsRequests() {
        return this.traceOptionsRequests;
    }

    public void setTraceOptionsRequests(boolean traceOptionsRequests) {
        this.traceOptionsRequests = traceOptionsRequests;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnablePrettySerializationOutput() {
        return this.enablePrettySerializationOutput;
    }

    public boolean isSendModules() {
        return this.sendModules;
    }

    public void setEnablePrettySerializationOutput(boolean enablePrettySerializationOutput) {
        this.enablePrettySerializationOutput = enablePrettySerializationOutput;
    }

    public boolean isEnableAppStartProfiling() {
        return (this.isProfilingEnabled() || this.isContinuousProfilingEnabled()) && this.enableAppStartProfiling;
    }

    public void setEnableAppStartProfiling(boolean enableAppStartProfiling) {
        this.enableAppStartProfiling = enableAppStartProfiling;
    }

    public void setSendModules(boolean sendModules) {
        this.sendModules = sendModules;
    }

    @ApiStatus.Experimental
    @Nullable
    public List<FilterString> getIgnoredSpanOrigins() {
        return this.ignoredSpanOrigins;
    }

    @ApiStatus.Experimental
    public void addIgnoredSpanOrigin(String ignoredSpanOrigin) {
        if (this.ignoredSpanOrigins == null) {
            this.ignoredSpanOrigins = new ArrayList<FilterString>();
        }
        this.ignoredSpanOrigins.add(new FilterString(ignoredSpanOrigin));
    }

    @ApiStatus.Experimental
    public void setIgnoredSpanOrigins(@Nullable List<String> ignoredSpanOrigins) {
        if (ignoredSpanOrigins == null) {
            this.ignoredSpanOrigins = null;
        } else {
            @NotNull ArrayList<FilterString> filtered = new ArrayList<FilterString>();
            for (String origin : ignoredSpanOrigins) {
                if (origin == null || origin.isEmpty()) continue;
                filtered.add(new FilterString(origin));
            }
            this.ignoredSpanOrigins = filtered;
        }
    }

    @ApiStatus.Experimental
    @Nullable
    public List<FilterString> getIgnoredCheckIns() {
        return this.ignoredCheckIns;
    }

    @ApiStatus.Experimental
    public void addIgnoredCheckIn(String ignoredCheckIn) {
        if (this.ignoredCheckIns == null) {
            this.ignoredCheckIns = new ArrayList<FilterString>();
        }
        this.ignoredCheckIns.add(new FilterString(ignoredCheckIn));
    }

    @ApiStatus.Experimental
    public void setIgnoredCheckIns(@Nullable List<String> ignoredCheckIns) {
        if (ignoredCheckIns == null) {
            this.ignoredCheckIns = null;
        } else {
            @NotNull ArrayList<FilterString> filteredIgnoredCheckIns = new ArrayList<FilterString>();
            for (String slug : ignoredCheckIns) {
                if (slug.isEmpty()) continue;
                filteredIgnoredCheckIns.add(new FilterString(slug));
            }
            this.ignoredCheckIns = filteredIgnoredCheckIns;
        }
    }

    @Nullable
    public List<FilterString> getIgnoredTransactions() {
        return this.ignoredTransactions;
    }

    @ApiStatus.Experimental
    public void addIgnoredTransaction(String ignoredTransaction) {
        if (this.ignoredTransactions == null) {
            this.ignoredTransactions = new ArrayList<FilterString>();
        }
        this.ignoredTransactions.add(new FilterString(ignoredTransaction));
    }

    @ApiStatus.Experimental
    public void setIgnoredTransactions(@Nullable List<String> ignoredTransactions) {
        if (ignoredTransactions == null) {
            this.ignoredTransactions = null;
        } else {
            @NotNull ArrayList<FilterString> filtered = new ArrayList<FilterString>();
            for (String transactionName : ignoredTransactions) {
                if (transactionName == null || transactionName.isEmpty()) continue;
                filtered.add(new FilterString(transactionName));
            }
            this.ignoredTransactions = filtered;
        }
    }

    @ApiStatus.Internal
    @NotNull
    public SentryDateProvider getDateProvider() {
        return this.dateProvider.getValue();
    }

    @ApiStatus.Internal
    public void setDateProvider(@NotNull SentryDateProvider dateProvider) {
        this.dateProvider.setValue(dateProvider);
    }

    @ApiStatus.Internal
    public void addPerformanceCollector(@NotNull IPerformanceCollector collector) {
        this.performanceCollectors.add(collector);
    }

    @ApiStatus.Internal
    @NotNull
    public List<IPerformanceCollector> getPerformanceCollectors() {
        return this.performanceCollectors;
    }

    @NotNull
    public IConnectionStatusProvider getConnectionStatusProvider() {
        return this.connectionStatusProvider;
    }

    public void setConnectionStatusProvider(@NotNull IConnectionStatusProvider connectionStatusProvider) {
        this.connectionStatusProvider = connectionStatusProvider;
    }

    @ApiStatus.Internal
    @NotNull
    public IBackpressureMonitor getBackpressureMonitor() {
        return this.backpressureMonitor;
    }

    @ApiStatus.Internal
    public void setBackpressureMonitor(@NotNull IBackpressureMonitor backpressureMonitor) {
        this.backpressureMonitor = backpressureMonitor;
    }

    @ApiStatus.Experimental
    public void setEnableBackpressureHandling(boolean enableBackpressureHandling) {
        this.enableBackpressureHandling = enableBackpressureHandling;
    }

    @ApiStatus.Internal
    @NotNull
    public IVersionDetector getVersionDetector() {
        return this.versionDetector;
    }

    @ApiStatus.Internal
    public void setVersionDetector(@NotNull IVersionDetector versionDetector) {
        this.versionDetector = versionDetector;
    }

    @ApiStatus.Internal
    public int getProfilingTracesHz() {
        return this.profilingTracesHz;
    }

    @ApiStatus.Internal
    public void setProfilingTracesHz(int profilingTracesHz) {
        this.profilingTracesHz = profilingTracesHz;
    }

    @ApiStatus.Experimental
    public boolean isEnableBackpressureHandling() {
        return this.enableBackpressureHandling;
    }

    @ApiStatus.Internal
    public long getSessionFlushTimeoutMillis() {
        return this.sessionFlushTimeoutMillis;
    }

    @ApiStatus.Internal
    public void setSessionFlushTimeoutMillis(long sessionFlushTimeoutMillis) {
        this.sessionFlushTimeoutMillis = sessionFlushTimeoutMillis;
    }

    @ApiStatus.Internal
    @Nullable
    public BeforeEnvelopeCallback getBeforeEnvelopeCallback() {
        return this.beforeEnvelopeCallback;
    }

    @ApiStatus.Internal
    public void setBeforeEnvelopeCallback(@Nullable BeforeEnvelopeCallback beforeEnvelopeCallback) {
        this.beforeEnvelopeCallback = beforeEnvelopeCallback;
    }

    @ApiStatus.Experimental
    @Nullable
    public String getSpotlightConnectionUrl() {
        return this.spotlightConnectionUrl;
    }

    @ApiStatus.Experimental
    public void setSpotlightConnectionUrl(@Nullable String spotlightConnectionUrl) {
        this.spotlightConnectionUrl = spotlightConnectionUrl;
    }

    @ApiStatus.Experimental
    public boolean isEnableSpotlight() {
        return this.enableSpotlight;
    }

    @ApiStatus.Experimental
    public void setEnableSpotlight(boolean enableSpotlight) {
        this.enableSpotlight = enableSpotlight;
    }

    public boolean isEnableScopePersistence() {
        return this.enableScopePersistence;
    }

    public void setEnableScopePersistence(boolean enableScopePersistence) {
        this.enableScopePersistence = enableScopePersistence;
    }

    @Nullable
    public Cron getCron() {
        return this.cron;
    }

    @ApiStatus.Experimental
    public void setCron(@Nullable Cron cron) {
        this.cron = cron;
    }

    @NotNull
    public ExperimentalOptions getExperimental() {
        return this.experimental;
    }

    @NotNull
    public ReplayController getReplayController() {
        return this.replayController;
    }

    public void setReplayController(@Nullable ReplayController replayController) {
        this.replayController = replayController != null ? replayController : NoOpReplayController.getInstance();
    }

    @ApiStatus.Experimental
    @NotNull
    public IDistributionApi getDistributionController() {
        return this.distributionController;
    }

    @ApiStatus.Experimental
    public void setDistributionController(@Nullable IDistributionApi distributionController) {
        this.distributionController = distributionController != null ? distributionController : NoOpDistributionApi.getInstance();
    }

    @ApiStatus.Experimental
    public boolean isEnableScreenTracking() {
        return this.enableScreenTracking;
    }

    @ApiStatus.Experimental
    public void setEnableScreenTracking(boolean enableScreenTracking) {
        this.enableScreenTracking = enableScreenTracking;
    }

    public void setDefaultScopeType(@NotNull ScopeType scopeType) {
        this.defaultScopeType = scopeType;
    }

    @NotNull
    public ScopeType getDefaultScopeType() {
        return this.defaultScopeType;
    }

    @ApiStatus.Internal
    public void setInitPriority(@NotNull InitPriority initPriority) {
        this.initPriority = initPriority;
    }

    @ApiStatus.Internal
    @NotNull
    public InitPriority getInitPriority() {
        return this.initPriority;
    }

    public void setForceInit(boolean forceInit) {
        this.forceInit = forceInit;
    }

    public boolean isForceInit() {
        return this.forceInit;
    }

    public void setGlobalHubMode(@Nullable Boolean globalHubMode) {
        this.globalHubMode = globalHubMode;
    }

    @Nullable
    public Boolean isGlobalHubMode() {
        return this.globalHubMode;
    }

    public void setOpenTelemetryMode(@NotNull SentryOpenTelemetryMode openTelemetryMode) {
        this.openTelemetryMode = openTelemetryMode;
    }

    @NotNull
    public SentryOpenTelemetryMode getOpenTelemetryMode() {
        return this.openTelemetryMode;
    }

    @NotNull
    public SentryReplayOptions getSessionReplay() {
        return this.sessionReplay;
    }

    public void setSessionReplay(@NotNull SentryReplayOptions sessionReplayOptions) {
        this.sessionReplay = sessionReplayOptions;
    }

    @NotNull
    public SentryFeedbackOptions getFeedbackOptions() {
        return this.feedbackOptions;
    }

    public void setFeedbackOptions(@NotNull SentryFeedbackOptions feedbackOptions) {
        this.feedbackOptions = feedbackOptions;
    }

    @ApiStatus.Experimental
    public void setCaptureOpenTelemetryEvents(boolean captureOpenTelemetryEvents) {
        this.captureOpenTelemetryEvents = captureOpenTelemetryEvents;
    }

    @ApiStatus.Experimental
    public boolean isCaptureOpenTelemetryEvents() {
        return this.captureOpenTelemetryEvents;
    }

    @NotNull
    public ISocketTagger getSocketTagger() {
        return this.socketTagger;
    }

    public void setSocketTagger(@Nullable ISocketTagger socketTagger) {
        this.socketTagger = socketTagger != null ? socketTagger : NoOpSocketTagger.getInstance();
    }

    @ApiStatus.Internal
    @NotNull
    public IRuntimeManager getRuntimeManager() {
        return this.runtimeManager;
    }

    @ApiStatus.Internal
    public void setRuntimeManager(@NotNull IRuntimeManager runtimeManager) {
        this.runtimeManager = runtimeManager;
    }

    void loadLazyFields() {
        this.getSerializer();
        this.retrieveParsedDsn();
        this.getEnvelopeReader();
        this.getDateProvider();
    }

    @ApiStatus.Internal
    @NotNull
    public static SentryOptions empty() {
        return new SentryOptions(true);
    }

    public SentryOptions() {
        this(false);
    }

    private SentryOptions(boolean empty) {
        @NotNull SdkVersion sdkVersion = this.createSdkVersion();
        this.experimental = new ExperimentalOptions(empty, sdkVersion);
        this.sessionReplay = new SentryReplayOptions(empty, sdkVersion);
        this.feedbackOptions = new SentryFeedbackOptions((associatedEventId, configurator) -> this.logger.log(SentryLevel.WARNING, "showDialog() can only be called in Android.", new Object[0]));
        if (!empty) {
            this.setSpanFactory(SpanFactoryFactory.create(new LoadClass(), NoOpLogger.getInstance()));
            this.executorService = new SentryExecutorService(this);
            this.executorService.prewarm();
            this.integrations.add(new UncaughtExceptionHandlerIntegration());
            this.integrations.add(new ShutdownHookIntegration());
            this.integrations.add(new SpotlightIntegration());
            this.eventProcessors.add(new MainEventProcessor(this));
            this.eventProcessors.add(new DuplicateEventDetectionEventProcessor(this));
            if (Platform.isJvm()) {
                this.eventProcessors.add(new SentryRuntimeEventProcessor());
            }
            this.setSentryClientName("sentry.java/8.29.0");
            this.setSdkVersion(sdkVersion);
            this.addPackageInfo();
        }
    }

    public void merge(@NotNull ExternalOptions options) {
        if (options.getDsn() != null) {
            this.setDsn(options.getDsn());
        }
        if (options.getEnvironment() != null) {
            this.setEnvironment(options.getEnvironment());
        }
        if (options.getRelease() != null) {
            this.setRelease(options.getRelease());
        }
        if (options.getDist() != null) {
            this.setDist(options.getDist());
        }
        if (options.getServerName() != null) {
            this.setServerName(options.getServerName());
        }
        if (options.getProxy() != null) {
            this.setProxy(options.getProxy());
        }
        if (options.getEnableUncaughtExceptionHandler() != null) {
            this.setEnableUncaughtExceptionHandler(options.getEnableUncaughtExceptionHandler());
        }
        if (options.getPrintUncaughtStackTrace() != null) {
            this.setPrintUncaughtStackTrace(options.getPrintUncaughtStackTrace());
        }
        if (options.getTracesSampleRate() != null) {
            this.setTracesSampleRate(options.getTracesSampleRate());
        }
        if (options.getProfilesSampleRate() != null) {
            this.setProfilesSampleRate(options.getProfilesSampleRate());
        }
        if (options.getDebug() != null) {
            this.setDebug(options.getDebug());
        }
        if (options.getEnableDeduplication() != null) {
            this.setEnableDeduplication(options.getEnableDeduplication());
        }
        if (options.getSendClientReports() != null) {
            this.setSendClientReports(options.getSendClientReports());
        }
        if (options.isForceInit() != null) {
            this.setForceInit(options.isForceInit());
        }
        HashMap<String, String> tags = new HashMap<String, String>(options.getTags());
        for (Map.Entry entry : tags.entrySet()) {
            this.tags.put((String)entry.getKey(), (String)entry.getValue());
        }
        ArrayList<String> inAppIncludes = new ArrayList<String>(options.getInAppIncludes());
        for (String string : inAppIncludes) {
            this.addInAppInclude(string);
        }
        ArrayList<String> arrayList = new ArrayList<String>(options.getInAppExcludes());
        for (String string : arrayList) {
            this.addInAppExclude(string);
        }
        for (Class<? extends Throwable> clazz : new HashSet<Class<? extends Throwable>>(options.getIgnoredExceptionsForType())) {
            this.addIgnoredExceptionForType(clazz);
        }
        if (options.getTracePropagationTargets() != null) {
            ArrayList<String> arrayList2 = new ArrayList<String>(options.getTracePropagationTargets());
            this.setTracePropagationTargets(arrayList2);
        }
        ArrayList<String> arrayList3 = new ArrayList<String>(options.getContextTags());
        for (String contextTag : arrayList3) {
            this.addContextTag(contextTag);
        }
        if (options.getProguardUuid() != null) {
            this.setProguardUuid(options.getProguardUuid());
        }
        if (options.getIdleTimeout() != null) {
            this.setIdleTimeout(options.getIdleTimeout());
        }
        for (String bundleId : options.getBundleIds()) {
            this.addBundleId(bundleId);
        }
        if (options.isEnabled() != null) {
            this.setEnabled(options.isEnabled());
        }
        if (options.isEnablePrettySerializationOutput() != null) {
            this.setEnablePrettySerializationOutput(options.isEnablePrettySerializationOutput());
        }
        if (options.isSendModules() != null) {
            this.setSendModules(options.isSendModules());
        }
        if (options.getIgnoredCheckIns() != null) {
            ArrayList<String> arrayList4 = new ArrayList<String>(options.getIgnoredCheckIns());
            this.setIgnoredCheckIns(arrayList4);
        }
        if (options.getIgnoredTransactions() != null) {
            ArrayList<String> arrayList5 = new ArrayList<String>(options.getIgnoredTransactions());
            this.setIgnoredTransactions(arrayList5);
        }
        if (options.getIgnoredErrors() != null) {
            ArrayList<String> arrayList6 = new ArrayList<String>(options.getIgnoredErrors());
            this.setIgnoredErrors(arrayList6);
        }
        if (options.isEnableBackpressureHandling() != null) {
            this.setEnableBackpressureHandling(options.isEnableBackpressureHandling());
        }
        if (options.getMaxRequestBodySize() != null) {
            this.setMaxRequestBodySize(options.getMaxRequestBodySize());
        }
        if (options.isSendDefaultPii() != null) {
            this.setSendDefaultPii(options.isSendDefaultPii());
        }
        if (options.isCaptureOpenTelemetryEvents() != null) {
            this.setCaptureOpenTelemetryEvents(options.isCaptureOpenTelemetryEvents());
        }
        if (options.isEnableSpotlight() != null) {
            this.setEnableSpotlight(options.isEnableSpotlight());
        }
        if (options.getSpotlightConnectionUrl() != null) {
            this.setSpotlightConnectionUrl(options.getSpotlightConnectionUrl());
        }
        if (options.isGlobalHubMode() != null) {
            this.setGlobalHubMode(options.isGlobalHubMode());
        }
        if (options.getCron() != null) {
            if (this.getCron() == null) {
                this.setCron(options.getCron());
            } else {
                if (options.getCron().getDefaultCheckinMargin() != null) {
                    this.getCron().setDefaultCheckinMargin(options.getCron().getDefaultCheckinMargin());
                }
                if (options.getCron().getDefaultMaxRuntime() != null) {
                    this.getCron().setDefaultMaxRuntime(options.getCron().getDefaultMaxRuntime());
                }
                if (options.getCron().getDefaultTimezone() != null) {
                    this.getCron().setDefaultTimezone(options.getCron().getDefaultTimezone());
                }
                if (options.getCron().getDefaultFailureIssueThreshold() != null) {
                    this.getCron().setDefaultFailureIssueThreshold(options.getCron().getDefaultFailureIssueThreshold());
                }
                if (options.getCron().getDefaultRecoveryThreshold() != null) {
                    this.getCron().setDefaultRecoveryThreshold(options.getCron().getDefaultRecoveryThreshold());
                }
            }
        }
        if (options.isEnableLogs() != null) {
            this.getLogs().setEnabled(options.isEnableLogs());
        }
        if (options.getProfileSessionSampleRate() != null) {
            this.setProfileSessionSampleRate(options.getProfileSessionSampleRate());
        }
        if (options.getProfilingTracesDirPath() != null) {
            this.setProfilingTracesDirPath(options.getProfilingTracesDirPath());
        }
        if (options.getProfileLifecycle() != null) {
            this.setProfileLifecycle(options.getProfileLifecycle());
        }
    }

    @NotNull
    private SdkVersion createSdkVersion() {
        String version = "8.29.0";
        SdkVersion sdkVersion = new SdkVersion("sentry.java", "8.29.0");
        sdkVersion.setVersion("8.29.0");
        return sdkVersion;
    }

    private void addPackageInfo() {
        SentryIntegrationPackageStorage.getInstance().addPackage("maven:io.sentry:sentry", "8.29.0");
    }

    @ApiStatus.Internal
    @NotNull
    public ISpanFactory getSpanFactory() {
        return this.spanFactory;
    }

    @ApiStatus.Internal
    public void setSpanFactory(@NotNull ISpanFactory spanFactory) {
        this.spanFactory = spanFactory;
    }

    @ApiStatus.Experimental
    @NotNull
    public Logs getLogs() {
        return this.logs;
    }

    @ApiStatus.Experimental
    public void setLogs(@NotNull Logs logs) {
        this.logs = logs;
    }

    @ApiStatus.Experimental
    @NotNull
    public DistributionOptions getDistribution() {
        return this.distribution;
    }

    @ApiStatus.Experimental
    public void setDistribution(@NotNull DistributionOptions distribution) {
        this.distribution = distribution != null ? distribution : new DistributionOptions();
    }

    public static interface BeforeSendCallback {
        @Nullable
        public SentryEvent execute(@NotNull SentryEvent var1, @NotNull Hint var2);
    }

    public static interface BeforeSendTransactionCallback {
        @Nullable
        public SentryTransaction execute(@NotNull SentryTransaction var1, @NotNull Hint var2);
    }

    public static interface BeforeSendReplayCallback {
        @Nullable
        public SentryReplayEvent execute(@NotNull SentryReplayEvent var1, @NotNull Hint var2);
    }

    public static interface BeforeBreadcrumbCallback {
        @Nullable
        public Breadcrumb execute(@NotNull Breadcrumb var1, @NotNull Hint var2);
    }

    public static interface OnDiscardCallback {
        public void execute(@NotNull DiscardReason var1, @NotNull DataCategory var2, @NotNull Long var3);
    }

    public static final class Proxy {
        @Nullable
        private String host;
        @Nullable
        private String port;
        @Nullable
        private String user;
        @Nullable
        private String pass;
        @Nullable
        private Proxy.Type type;

        public Proxy() {
            this(null, null, null, null, null);
        }

        public Proxy(@Nullable String host, @Nullable String port) {
            this(host, port, null, null, null);
        }

        public Proxy(@Nullable String host, @Nullable String port, @Nullable Proxy.Type type) {
            this(host, port, type, null, null);
        }

        public Proxy(@Nullable String host, @Nullable String port, @Nullable String user, @Nullable String pass) {
            this(host, port, null, user, pass);
        }

        public Proxy(@Nullable String host, @Nullable String port, @Nullable Proxy.Type type, @Nullable String user, @Nullable String pass) {
            this.host = host;
            this.port = port;
            this.type = type;
            this.user = user;
            this.pass = pass;
        }

        @Nullable
        public String getHost() {
            return this.host;
        }

        public void setHost(@Nullable String host) {
            this.host = host;
        }

        @Nullable
        public String getPort() {
            return this.port;
        }

        public void setPort(@Nullable String port) {
            this.port = port;
        }

        @Nullable
        public String getUser() {
            return this.user;
        }

        public void setUser(@Nullable String user) {
            this.user = user;
        }

        @Nullable
        public String getPass() {
            return this.pass;
        }

        public void setPass(@Nullable String pass) {
            this.pass = pass;
        }

        @Nullable
        public Proxy.Type getType() {
            return this.type;
        }

        public void setType(@Nullable Proxy.Type type) {
            this.type = type;
        }
    }

    public static interface TracesSamplerCallback {
        @Nullable
        public Double sample(@NotNull SamplingContext var1);
    }

    public static interface OnOversizedEventCallback {
        @NotNull
        public SentryEvent execute(@NotNull SentryEvent var1, @NotNull Hint var2);
    }

    public static enum RequestSize {
        NONE,
        SMALL,
        MEDIUM,
        ALWAYS;

    }

    public static interface ProfilesSamplerCallback {
        @Nullable
        public Double sample(@NotNull SamplingContext var1);
    }

    @ApiStatus.Internal
    public static interface BeforeEnvelopeCallback {
        public void execute(@NotNull SentryEnvelope var1, @Nullable Hint var2);
    }

    public static final class Cron {
        @Nullable
        private Long defaultCheckinMargin;
        @Nullable
        private Long defaultMaxRuntime;
        @Nullable
        private String defaultTimezone;
        @Nullable
        private Long defaultFailureIssueThreshold;
        @Nullable
        private Long defaultRecoveryThreshold;

        @Nullable
        public Long getDefaultCheckinMargin() {
            return this.defaultCheckinMargin;
        }

        public void setDefaultCheckinMargin(@Nullable Long defaultCheckinMargin) {
            this.defaultCheckinMargin = defaultCheckinMargin;
        }

        @Nullable
        public Long getDefaultMaxRuntime() {
            return this.defaultMaxRuntime;
        }

        public void setDefaultMaxRuntime(@Nullable Long defaultMaxRuntime) {
            this.defaultMaxRuntime = defaultMaxRuntime;
        }

        @Nullable
        public String getDefaultTimezone() {
            return this.defaultTimezone;
        }

        public void setDefaultTimezone(@Nullable String defaultTimezone) {
            this.defaultTimezone = defaultTimezone;
        }

        @Nullable
        public Long getDefaultFailureIssueThreshold() {
            return this.defaultFailureIssueThreshold;
        }

        public void setDefaultFailureIssueThreshold(@Nullable Long defaultFailureIssueThreshold) {
            this.defaultFailureIssueThreshold = defaultFailureIssueThreshold;
        }

        @Nullable
        public Long getDefaultRecoveryThreshold() {
            return this.defaultRecoveryThreshold;
        }

        public void setDefaultRecoveryThreshold(@Nullable Long defaultRecoveryThreshold) {
            this.defaultRecoveryThreshold = defaultRecoveryThreshold;
        }
    }

    public static final class Logs {
        private boolean enable = false;
        @Nullable
        private BeforeSendLogCallback beforeSend;
        @NotNull
        private ILoggerBatchProcessorFactory loggerBatchProcessorFactory = new DefaultLoggerBatchProcessorFactory();

        public boolean isEnabled() {
            return this.enable;
        }

        public void setEnabled(boolean enableLogs) {
            this.enable = enableLogs;
        }

        @Nullable
        public BeforeSendLogCallback getBeforeSend() {
            return this.beforeSend;
        }

        public void setBeforeSend(@Nullable BeforeSendLogCallback beforeSendLog) {
            this.beforeSend = beforeSendLog;
        }

        @ApiStatus.Internal
        @NotNull
        public ILoggerBatchProcessorFactory getLoggerBatchProcessorFactory() {
            return this.loggerBatchProcessorFactory;
        }

        @ApiStatus.Internal
        public void setLoggerBatchProcessorFactory(@NotNull ILoggerBatchProcessorFactory loggerBatchProcessorFactory) {
            this.loggerBatchProcessorFactory = loggerBatchProcessorFactory;
        }

        public static interface BeforeSendLogCallback {
            @Nullable
            public SentryLogEvent execute(@NotNull SentryLogEvent var1);
        }
    }

    @ApiStatus.Experimental
    public static final class DistributionOptions {
        public String orgAuthToken = "";
        public String orgSlug = "";
        public String projectSlug = "";
        public String sentryBaseUrl = "https://sentry.io";
        @Nullable
        public String buildConfiguration = null;
    }

    @ApiStatus.Experimental
    public static interface BeforeEmitMetricCallback {
        public boolean execute(@NotNull String var1, @Nullable Map<String, String> var2);
    }
}

