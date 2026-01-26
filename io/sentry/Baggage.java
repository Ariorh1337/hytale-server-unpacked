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

import io.sentry.ILogger;
import io.sentry.IScope;
import io.sentry.ISentryLifecycleToken;
import io.sentry.PropagationContext;
import io.sentry.ScopesAdapter;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.SpanContext;
import io.sentry.TraceContext;
import io.sentry.TracesSamplingDecision;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.SampleRateUtils;
import io.sentry.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public final class Baggage {
    @NotNull
    static final String CHARSET = "UTF-8";
    @NotNull
    static final Integer MAX_BAGGAGE_STRING_LENGTH = 8192;
    @NotNull
    static final Integer MAX_BAGGAGE_LIST_MEMBER_COUNT = 64;
    @NotNull
    static final String SENTRY_BAGGAGE_PREFIX = "sentry-";
    private static final DecimalFormatterThreadLocal decimalFormatter = new DecimalFormatterThreadLocal();
    @NotNull
    private final ConcurrentHashMap<String, String> keyValues;
    @NotNull
    private final AutoClosableReentrantLock keyValuesLock = new AutoClosableReentrantLock();
    @Nullable
    private Double sampleRate;
    @Nullable
    private Double sampleRand;
    @Nullable
    private final String thirdPartyHeader;
    private boolean mutable;
    private final boolean shouldFreeze;
    @NotNull
    final ILogger logger;

    @NotNull
    public static Baggage fromHeader(@Nullable String headerValue) {
        return Baggage.fromHeader(headerValue, false, ScopesAdapter.getInstance().getOptions().getLogger());
    }

    @NotNull
    public static Baggage fromHeader(@Nullable List<String> headerValues) {
        return Baggage.fromHeader(headerValues, false, ScopesAdapter.getInstance().getOptions().getLogger());
    }

    @ApiStatus.Internal
    @NotNull
    public static Baggage fromHeader(String headerValue, @NotNull ILogger logger) {
        return Baggage.fromHeader(headerValue, false, logger);
    }

    @ApiStatus.Internal
    @NotNull
    public static Baggage fromHeader(@Nullable List<String> headerValues, @NotNull ILogger logger) {
        return Baggage.fromHeader(headerValues, false, logger);
    }

    @ApiStatus.Internal
    @NotNull
    public static Baggage fromHeader(@Nullable List<String> headerValues, boolean includeThirdPartyValues, @NotNull ILogger logger) {
        if (headerValues != null) {
            return Baggage.fromHeader(StringUtils.join(",", headerValues), includeThirdPartyValues, logger);
        }
        return Baggage.fromHeader((String)null, includeThirdPartyValues, logger);
    }

    @ApiStatus.Internal
    @NotNull
    public static Baggage fromHeader(@Nullable String headerValue, boolean includeThirdPartyValues, @NotNull ILogger logger) {
        @NotNull ConcurrentHashMap<String, String> keyValues = new ConcurrentHashMap<String, String>();
        @NotNull ArrayList<String> thirdPartyKeyValueStrings = new ArrayList<String>();
        boolean shouldFreeze = false;
        Double sampleRate = null;
        Double sampleRand = null;
        if (headerValue != null) {
            try {
                String[] keyValueStrings;
                for (String keyValueString : keyValueStrings = headerValue.split(",", -1)) {
                    if (keyValueString.trim().startsWith(SENTRY_BAGGAGE_PREFIX)) {
                        try {
                            int separatorIndex = keyValueString.indexOf("=");
                            String key = keyValueString.substring(0, separatorIndex).trim();
                            String keyDecoded = Baggage.decode(key);
                            String value = keyValueString.substring(separatorIndex + 1).trim();
                            String valueDecoded = Baggage.decode(value);
                            if ("sentry-sample_rate".equals(keyDecoded)) {
                                sampleRate = Baggage.toDouble(valueDecoded);
                            } else if ("sentry-sample_rand".equals(keyDecoded)) {
                                sampleRand = Baggage.toDouble(valueDecoded);
                            } else {
                                keyValues.put(keyDecoded, valueDecoded);
                            }
                            if ("sentry-sample_rand".equalsIgnoreCase(key)) continue;
                            shouldFreeze = true;
                        }
                        catch (Throwable e) {
                            logger.log(SentryLevel.ERROR, e, "Unable to decode baggage key value pair %s", keyValueString);
                        }
                        continue;
                    }
                    if (!includeThirdPartyValues) continue;
                    thirdPartyKeyValueStrings.add(keyValueString.trim());
                }
            }
            catch (Throwable e) {
                logger.log(SentryLevel.ERROR, e, "Unable to decode baggage header %s", headerValue);
            }
        }
        String thirdPartyHeader = thirdPartyKeyValueStrings.isEmpty() ? null : StringUtils.join(",", thirdPartyKeyValueStrings);
        return new Baggage(keyValues, sampleRate, sampleRand, thirdPartyHeader, true, shouldFreeze, logger);
    }

    @ApiStatus.Internal
    @NotNull
    public static Baggage fromEvent(@NotNull SentryBaseEvent event, @Nullable String transaction, @NotNull SentryOptions options) {
        Baggage baggage = new Baggage(options.getLogger());
        SpanContext trace = event.getContexts().getTrace();
        baggage.setTraceId(trace != null ? trace.getTraceId().toString() : null);
        baggage.setPublicKey(options.retrieveParsedDsn().getPublicKey());
        baggage.setRelease(event.getRelease());
        baggage.setEnvironment(event.getEnvironment());
        baggage.setTransaction(transaction);
        baggage.setSampleRate(null);
        baggage.setSampled(null);
        baggage.setSampleRand(null);
        @Nullable Object replayId = event.getContexts().get("replay_id");
        if (replayId != null && !replayId.toString().equals(SentryId.EMPTY_ID.toString())) {
            baggage.setReplayId(replayId.toString());
            event.getContexts().remove("replay_id");
        }
        baggage.freeze();
        return baggage;
    }

    @ApiStatus.Internal
    public Baggage(@NotNull ILogger logger) {
        this(new ConcurrentHashMap<String, String>(), null, null, null, true, false, logger);
    }

    @ApiStatus.Internal
    public Baggage(@NotNull Baggage baggage) {
        this(baggage.keyValues, baggage.sampleRate, baggage.sampleRand, baggage.thirdPartyHeader, baggage.mutable, baggage.shouldFreeze, baggage.logger);
    }

    @ApiStatus.Internal
    public Baggage(@NotNull ConcurrentHashMap<String, String> keyValues, @Nullable Double sampleRate, @Nullable Double sampleRand, @Nullable String thirdPartyHeader, boolean isMutable, boolean shouldFreeze, @NotNull ILogger logger) {
        this.keyValues = keyValues;
        this.sampleRate = sampleRate;
        this.sampleRand = sampleRand;
        this.logger = logger;
        this.thirdPartyHeader = thirdPartyHeader;
        this.mutable = isMutable;
        this.shouldFreeze = shouldFreeze;
    }

    @ApiStatus.Internal
    public void freeze() {
        this.mutable = false;
    }

    @ApiStatus.Internal
    public boolean isMutable() {
        return this.mutable;
    }

    @ApiStatus.Internal
    public boolean isShouldFreeze() {
        return this.shouldFreeze;
    }

    @Nullable
    public String getThirdPartyHeader() {
        return this.thirdPartyHeader;
    }

    @NotNull
    public String toHeaderString(@Nullable String thirdPartyBaggageHeaderString) {
        TreeSet<String> keys;
        StringBuilder sb = new StringBuilder();
        String separator = "";
        int listMemberCount = 0;
        if (thirdPartyBaggageHeaderString != null && !thirdPartyBaggageHeaderString.isEmpty()) {
            sb.append(thirdPartyBaggageHeaderString);
            listMemberCount = StringUtils.countOf(thirdPartyBaggageHeaderString, ',') + 1;
            separator = ",";
        }
        try (@NotNull ISentryLifecycleToken ignored = this.keyValuesLock.acquire();){
            keys = new TreeSet<String>(Collections.list(this.keyValues.keys()));
        }
        keys.add("sentry-sample_rate");
        keys.add("sentry-sample_rand");
        for (String key : keys) {
            @Nullable String value = "sentry-sample_rate".equals(key) ? Baggage.sampleRateToString(this.sampleRate) : ("sentry-sample_rand".equals(key) ? Baggage.sampleRateToString(this.sampleRand) : this.keyValues.get(key));
            if (value == null) continue;
            if (listMemberCount >= MAX_BAGGAGE_LIST_MEMBER_COUNT) {
                this.logger.log(SentryLevel.ERROR, "Not adding baggage value %s as the total number of list members would exceed the maximum of %s.", key, MAX_BAGGAGE_LIST_MEMBER_COUNT);
                continue;
            }
            try {
                String encodedKey = this.encode(key);
                String encodedValue = this.encode(value);
                String encodedKeyValue = separator + encodedKey + "=" + encodedValue;
                int valueLength = encodedKeyValue.length();
                int totalLengthIfValueAdded = sb.length() + valueLength;
                if (totalLengthIfValueAdded > MAX_BAGGAGE_STRING_LENGTH) {
                    this.logger.log(SentryLevel.ERROR, "Not adding baggage value %s as the total header value length would exceed the maximum of %s.", key, MAX_BAGGAGE_STRING_LENGTH);
                    continue;
                }
                ++listMemberCount;
                sb.append(encodedKeyValue);
                separator = ",";
            }
            catch (Throwable e) {
                this.logger.log(SentryLevel.ERROR, e, "Unable to encode baggage key value pair (key=%s,value=%s).", key, value);
            }
        }
        return sb.toString();
    }

    private String encode(@NotNull String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, CHARSET).replaceAll("\\+", "%20");
    }

    private static String decode(@NotNull String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, CHARSET);
    }

    @ApiStatus.Internal
    @Nullable
    public String get(@Nullable String key) {
        if (key == null) {
            return null;
        }
        return this.keyValues.get(key);
    }

    @ApiStatus.Internal
    @Nullable
    public String getTraceId() {
        return this.get("sentry-trace_id");
    }

    @ApiStatus.Internal
    public void setTraceId(@Nullable String traceId) {
        this.set("sentry-trace_id", traceId);
    }

    @ApiStatus.Internal
    @Nullable
    public String getPublicKey() {
        return this.get("sentry-public_key");
    }

    @ApiStatus.Internal
    public void setPublicKey(@Nullable String publicKey) {
        this.set("sentry-public_key", publicKey);
    }

    @ApiStatus.Internal
    @Nullable
    public String getEnvironment() {
        return this.get("sentry-environment");
    }

    @ApiStatus.Internal
    public void setEnvironment(@Nullable String environment) {
        this.set("sentry-environment", environment);
    }

    @ApiStatus.Internal
    @Nullable
    public String getRelease() {
        return this.get("sentry-release");
    }

    @ApiStatus.Internal
    public void setRelease(@Nullable String release) {
        this.set("sentry-release", release);
    }

    @ApiStatus.Internal
    @Nullable
    public String getUserId() {
        return this.get("sentry-user_id");
    }

    @ApiStatus.Internal
    public void setUserId(@Nullable String userId) {
        this.set("sentry-user_id", userId);
    }

    @ApiStatus.Internal
    @Nullable
    public String getTransaction() {
        return this.get("sentry-transaction");
    }

    @ApiStatus.Internal
    public void setTransaction(@Nullable String transaction) {
        this.set("sentry-transaction", transaction);
    }

    @ApiStatus.Internal
    @Nullable
    public Double getSampleRate() {
        return this.sampleRate;
    }

    @ApiStatus.Internal
    @Nullable
    public String getSampled() {
        return this.get("sentry-sampled");
    }

    @ApiStatus.Internal
    public void setSampleRate(@Nullable Double sampleRate) {
        if (this.isMutable()) {
            this.sampleRate = sampleRate;
        }
    }

    @ApiStatus.Internal
    public void forceSetSampleRate(@Nullable Double sampleRate) {
        this.sampleRate = sampleRate;
    }

    @ApiStatus.Internal
    @Nullable
    public Double getSampleRand() {
        return this.sampleRand;
    }

    @ApiStatus.Internal
    public void setSampleRand(@Nullable Double sampleRand) {
        if (this.isMutable()) {
            this.sampleRand = sampleRand;
        }
    }

    @ApiStatus.Internal
    public void setSampled(@Nullable String sampled) {
        this.set("sentry-sampled", sampled);
    }

    @ApiStatus.Internal
    @Nullable
    public String getReplayId() {
        return this.get("sentry-replay_id");
    }

    @ApiStatus.Internal
    public void setReplayId(@Nullable String replayId) {
        this.set("sentry-replay_id", replayId);
    }

    @ApiStatus.Internal
    public void set(@NotNull String key, @Nullable String value) {
        if (this.mutable) {
            if (value == null) {
                this.keyValues.remove(key);
            } else {
                this.keyValues.put(key, value);
            }
        }
    }

    @ApiStatus.Internal
    @NotNull
    public Map<String, Object> getUnknown() {
        @NotNull ConcurrentHashMap<String, Object> unknown = new ConcurrentHashMap<String, Object>();
        try (@NotNull ISentryLifecycleToken ignored = this.keyValuesLock.acquire();){
            for (Map.Entry<String, String> keyValue : this.keyValues.entrySet()) {
                @NotNull String key = keyValue.getKey();
                @Nullable String value = keyValue.getValue();
                if (DSCKeys.ALL.contains(key) || value == null) continue;
                @NotNull String unknownKey = key.replaceFirst(SENTRY_BAGGAGE_PREFIX, "");
                unknown.put(unknownKey, value);
            }
        }
        return unknown;
    }

    @ApiStatus.Internal
    public void setValuesFromTransaction(@NotNull SentryId traceId, @Nullable SentryId replayId, @NotNull SentryOptions sentryOptions, @Nullable TracesSamplingDecision samplingDecision, @Nullable String transactionName, @Nullable TransactionNameSource transactionNameSource) {
        this.setTraceId(traceId.toString());
        this.setPublicKey(sentryOptions.retrieveParsedDsn().getPublicKey());
        this.setRelease(sentryOptions.getRelease());
        this.setEnvironment(sentryOptions.getEnvironment());
        this.setTransaction(Baggage.isHighQualityTransactionName(transactionNameSource) ? transactionName : null);
        if (replayId != null && !SentryId.EMPTY_ID.equals(replayId)) {
            this.setReplayId(replayId.toString());
        }
        this.setSampleRate(Baggage.sampleRate(samplingDecision));
        this.setSampled(StringUtils.toString(Baggage.sampled(samplingDecision)));
        this.setSampleRand(Baggage.sampleRand(samplingDecision));
    }

    @ApiStatus.Internal
    public void setValuesFromSamplingDecision(@Nullable TracesSamplingDecision samplingDecision) {
        if (samplingDecision == null) {
            return;
        }
        this.setSampled(StringUtils.toString(Baggage.sampled(samplingDecision)));
        if (samplingDecision.getSampleRand() != null) {
            this.setSampleRand(Baggage.sampleRand(samplingDecision));
        }
        if (samplingDecision.getSampleRate() != null) {
            this.forceSetSampleRate(Baggage.sampleRate(samplingDecision));
        }
    }

    @ApiStatus.Internal
    public void setValuesFromScope(@NotNull IScope scope, @NotNull SentryOptions options) {
        @NotNull PropagationContext propagationContext = scope.getPropagationContext();
        @NotNull SentryId replayId = scope.getReplayId();
        this.setTraceId(propagationContext.getTraceId().toString());
        this.setPublicKey(options.retrieveParsedDsn().getPublicKey());
        this.setRelease(options.getRelease());
        this.setEnvironment(options.getEnvironment());
        if (!SentryId.EMPTY_ID.equals(replayId)) {
            this.setReplayId(replayId.toString());
        }
        this.setTransaction(null);
        this.setSampleRate(null);
        this.setSampled(null);
    }

    @Nullable
    private static Double sampleRate(@Nullable TracesSamplingDecision samplingDecision) {
        if (samplingDecision == null) {
            return null;
        }
        return samplingDecision.getSampleRate();
    }

    @Nullable
    private static Double sampleRand(@Nullable TracesSamplingDecision samplingDecision) {
        if (samplingDecision == null) {
            return null;
        }
        return samplingDecision.getSampleRand();
    }

    @Nullable
    private static String sampleRateToString(@Nullable Double sampleRateAsDouble) {
        if (!SampleRateUtils.isValidTracesSampleRate(sampleRateAsDouble, false)) {
            return null;
        }
        return ((DecimalFormat)decimalFormatter.get()).format(sampleRateAsDouble);
    }

    @Nullable
    private static Boolean sampled(@Nullable TracesSamplingDecision samplingDecision) {
        if (samplingDecision == null) {
            return null;
        }
        return samplingDecision.getSampled();
    }

    private static boolean isHighQualityTransactionName(@Nullable TransactionNameSource transactionNameSource) {
        return transactionNameSource != null && !TransactionNameSource.URL.equals((Object)transactionNameSource);
    }

    @Nullable
    private static Double toDouble(@Nullable String stringValue) {
        if (stringValue != null) {
            try {
                double doubleValue = Double.parseDouble(stringValue);
                if (SampleRateUtils.isValidTracesSampleRate(doubleValue, false)) {
                    return doubleValue;
                }
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @ApiStatus.Internal
    @Nullable
    public TraceContext toTraceContext() {
        String traceIdString = this.getTraceId();
        String replayIdString = this.getReplayId();
        String publicKey = this.getPublicKey();
        if (traceIdString != null && publicKey != null) {
            @NotNull TraceContext traceContext = new TraceContext(new SentryId(traceIdString), publicKey, this.getRelease(), this.getEnvironment(), this.getUserId(), this.getTransaction(), Baggage.sampleRateToString(this.getSampleRate()), this.getSampled(), replayIdString == null ? null : new SentryId(replayIdString), Baggage.sampleRateToString(this.getSampleRand()));
            traceContext.setUnknown(this.getUnknown());
            return traceContext;
        }
        return null;
    }

    @ApiStatus.Internal
    public static final class DSCKeys {
        public static final String TRACE_ID = "sentry-trace_id";
        public static final String PUBLIC_KEY = "sentry-public_key";
        public static final String RELEASE = "sentry-release";
        public static final String USER_ID = "sentry-user_id";
        public static final String ENVIRONMENT = "sentry-environment";
        public static final String TRANSACTION = "sentry-transaction";
        public static final String SAMPLE_RATE = "sentry-sample_rate";
        public static final String SAMPLE_RAND = "sentry-sample_rand";
        public static final String SAMPLED = "sentry-sampled";
        public static final String REPLAY_ID = "sentry-replay_id";
        public static final List<String> ALL = Arrays.asList("sentry-trace_id", "sentry-public_key", "sentry-release", "sentry-user_id", "sentry-environment", "sentry-transaction", "sentry-sample_rate", "sentry-sample_rand", "sentry-sampled", "sentry-replay_id");
    }

    private static class DecimalFormatterThreadLocal
    extends ThreadLocal<DecimalFormat> {
        private DecimalFormatterThreadLocal() {
        }

        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("#.################", DecimalFormatSymbols.getInstance(Locale.ROOT));
        }
    }
}

