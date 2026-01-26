/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.ITransaction;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.NoOpTransaction;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.ProfilingTransactionData;
import io.sentry.SentryUUID;
import io.sentry.profilemeasurements.ProfileMeasurement;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ProfilingTraceData
implements JsonUnknown,
JsonSerializable {
    private static final String DEFAULT_ENVIRONMENT = "production";
    @ApiStatus.Internal
    public static final String TRUNCATION_REASON_NORMAL = "normal";
    @ApiStatus.Internal
    public static final String TRUNCATION_REASON_TIMEOUT = "timeout";
    @ApiStatus.Internal
    public static final String TRUNCATION_REASON_BACKGROUNDED = "backgrounded";
    @NotNull
    private final File traceFile;
    @NotNull
    private final Callable<List<Integer>> deviceCpuFrequenciesReader;
    private int androidApiLevel;
    @NotNull
    private String deviceLocale;
    @NotNull
    private String deviceManufacturer;
    @NotNull
    private String deviceModel;
    @NotNull
    private String deviceOsBuildNumber;
    @NotNull
    private String deviceOsName;
    @NotNull
    private String deviceOsVersion;
    private boolean deviceIsEmulator;
    @NotNull
    private String cpuArchitecture;
    @NotNull
    private List<Integer> deviceCpuFrequencies = new ArrayList<Integer>();
    @NotNull
    private String devicePhysicalMemoryBytes;
    @NotNull
    private String platform;
    @NotNull
    private String buildId;
    @NotNull
    private List<ProfilingTransactionData> transactions;
    @NotNull
    private String transactionName;
    @NotNull
    private String durationNs;
    @NotNull
    private String versionCode;
    @NotNull
    private String release;
    @NotNull
    private String transactionId;
    @NotNull
    private String traceId;
    @NotNull
    private String profileId;
    @NotNull
    private String environment;
    @NotNull
    private String truncationReason;
    @NotNull
    private Date timestamp;
    @NotNull
    private final Map<String, ProfileMeasurement> measurementsMap;
    @Nullable
    private String sampledProfile = null;
    @Nullable
    private Map<String, Object> unknown;

    private ProfilingTraceData() {
        this(new File("dummy"), NoOpTransaction.getInstance());
    }

    public ProfilingTraceData(@NotNull File traceFile, @NotNull ITransaction transaction) {
        this(traceFile, DateUtils.getCurrentDateTime(), new ArrayList<ProfilingTransactionData>(), transaction.getName(), transaction.getEventId().toString(), transaction.getSpanContext().getTraceId().toString(), "0", 0, "", () -> new ArrayList(), null, null, null, null, null, null, null, null, TRUNCATION_REASON_NORMAL, new HashMap<String, ProfileMeasurement>());
    }

    public ProfilingTraceData(@NotNull File traceFile, @NotNull Date profileStartTimestamp, @NotNull List<ProfilingTransactionData> transactions, @NotNull String transactionName, @NotNull String transactionId, @NotNull String traceId, @NotNull String durationNanos, int sdkInt, @NotNull String cpuArchitecture, @NotNull Callable<List<Integer>> deviceCpuFrequenciesReader, @Nullable String deviceManufacturer, @Nullable String deviceModel, @Nullable String deviceOsVersion, @Nullable Boolean deviceIsEmulator, @Nullable String devicePhysicalMemoryBytes, @Nullable String buildId, @Nullable String release, @Nullable String environment, @NotNull String truncationReason, @NotNull Map<String, ProfileMeasurement> measurementsMap) {
        this.traceFile = traceFile;
        this.timestamp = profileStartTimestamp;
        this.cpuArchitecture = cpuArchitecture;
        this.deviceCpuFrequenciesReader = deviceCpuFrequenciesReader;
        this.androidApiLevel = sdkInt;
        this.deviceLocale = Locale.getDefault().toString();
        this.deviceManufacturer = deviceManufacturer != null ? deviceManufacturer : "";
        this.deviceModel = deviceModel != null ? deviceModel : "";
        this.deviceOsVersion = deviceOsVersion != null ? deviceOsVersion : "";
        this.deviceIsEmulator = deviceIsEmulator != null ? deviceIsEmulator : false;
        this.devicePhysicalMemoryBytes = devicePhysicalMemoryBytes != null ? devicePhysicalMemoryBytes : "0";
        this.deviceOsBuildNumber = "";
        this.deviceOsName = "android";
        this.platform = "android";
        this.buildId = buildId != null ? buildId : "";
        this.transactions = transactions;
        this.transactionName = transactionName.isEmpty() ? "unknown" : transactionName;
        this.durationNs = durationNanos;
        this.versionCode = "";
        this.release = release != null ? release : "";
        this.transactionId = transactionId;
        this.traceId = traceId;
        this.profileId = SentryUUID.generateSentryId();
        this.environment = environment != null ? environment : DEFAULT_ENVIRONMENT;
        this.truncationReason = truncationReason;
        if (!this.isTruncationReasonValid()) {
            this.truncationReason = TRUNCATION_REASON_NORMAL;
        }
        this.measurementsMap = measurementsMap;
    }

    private boolean isTruncationReasonValid() {
        return this.truncationReason.equals(TRUNCATION_REASON_NORMAL) || this.truncationReason.equals(TRUNCATION_REASON_TIMEOUT) || this.truncationReason.equals(TRUNCATION_REASON_BACKGROUNDED);
    }

    @NotNull
    public File getTraceFile() {
        return this.traceFile;
    }

    public int getAndroidApiLevel() {
        return this.androidApiLevel;
    }

    @NotNull
    public String getCpuArchitecture() {
        return this.cpuArchitecture;
    }

    @NotNull
    public String getDeviceLocale() {
        return this.deviceLocale;
    }

    @NotNull
    public String getDeviceManufacturer() {
        return this.deviceManufacturer;
    }

    @NotNull
    public String getDeviceModel() {
        return this.deviceModel;
    }

    @NotNull
    public String getDeviceOsBuildNumber() {
        return this.deviceOsBuildNumber;
    }

    @NotNull
    public String getDeviceOsName() {
        return this.deviceOsName;
    }

    @NotNull
    public String getDeviceOsVersion() {
        return this.deviceOsVersion;
    }

    public boolean isDeviceIsEmulator() {
        return this.deviceIsEmulator;
    }

    @NotNull
    public String getPlatform() {
        return this.platform;
    }

    @NotNull
    public String getBuildId() {
        return this.buildId;
    }

    @NotNull
    public String getTransactionName() {
        return this.transactionName;
    }

    @NotNull
    public String getRelease() {
        return this.release;
    }

    @NotNull
    public String getTransactionId() {
        return this.transactionId;
    }

    @NotNull
    public List<ProfilingTransactionData> getTransactions() {
        return this.transactions;
    }

    @NotNull
    public String getTraceId() {
        return this.traceId;
    }

    @NotNull
    public String getProfileId() {
        return this.profileId;
    }

    @NotNull
    public String getEnvironment() {
        return this.environment;
    }

    @Nullable
    public String getSampledProfile() {
        return this.sampledProfile;
    }

    @NotNull
    public String getDurationNs() {
        return this.durationNs;
    }

    @NotNull
    public List<Integer> getDeviceCpuFrequencies() {
        return this.deviceCpuFrequencies;
    }

    @NotNull
    public String getDevicePhysicalMemoryBytes() {
        return this.devicePhysicalMemoryBytes;
    }

    @NotNull
    public String getTruncationReason() {
        return this.truncationReason;
    }

    @NotNull
    public Date getTimestamp() {
        return this.timestamp;
    }

    @NotNull
    public Map<String, ProfileMeasurement> getMeasurementsMap() {
        return this.measurementsMap;
    }

    public void setAndroidApiLevel(int androidApiLevel) {
        this.androidApiLevel = androidApiLevel;
    }

    public void setCpuArchitecture(@NotNull String cpuArchitecture) {
        this.cpuArchitecture = cpuArchitecture;
    }

    public void setDeviceLocale(@NotNull String deviceLocale) {
        this.deviceLocale = deviceLocale;
    }

    public void setDeviceManufacturer(@NotNull String deviceManufacturer) {
        this.deviceManufacturer = deviceManufacturer;
    }

    public void setDeviceModel(@NotNull String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setDeviceOsBuildNumber(@NotNull String deviceOsBuildNumber) {
        this.deviceOsBuildNumber = deviceOsBuildNumber;
    }

    public void setDeviceOsVersion(@NotNull String deviceOsVersion) {
        this.deviceOsVersion = deviceOsVersion;
    }

    public void setDeviceIsEmulator(boolean deviceIsEmulator) {
        this.deviceIsEmulator = deviceIsEmulator;
    }

    public void setDeviceCpuFrequencies(@NotNull List<Integer> deviceCpuFrequencies) {
        this.deviceCpuFrequencies = deviceCpuFrequencies;
    }

    public void setDevicePhysicalMemoryBytes(@NotNull String devicePhysicalMemoryBytes) {
        this.devicePhysicalMemoryBytes = devicePhysicalMemoryBytes;
    }

    public void setTimestamp(@NotNull Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTruncationReason(@NotNull String truncationReason) {
        this.truncationReason = truncationReason;
    }

    public void setTransactions(@NotNull List<ProfilingTransactionData> transactions) {
        this.transactions = transactions;
    }

    public void setBuildId(@NotNull String buildId) {
        this.buildId = buildId;
    }

    public void setTransactionName(@NotNull String transactionName) {
        this.transactionName = transactionName;
    }

    public void setDurationNs(@NotNull String durationNs) {
        this.durationNs = durationNs;
    }

    public void setRelease(@NotNull String release) {
        this.release = release;
    }

    public void setTransactionId(@NotNull String transactionId) {
        this.transactionId = transactionId;
    }

    public void setTraceId(@NotNull String traceId) {
        this.traceId = traceId;
    }

    public void setProfileId(@NotNull String profileId) {
        this.profileId = profileId;
    }

    public void setEnvironment(@NotNull String environment) {
        this.environment = environment;
    }

    public void setSampledProfile(@Nullable String sampledProfile) {
        this.sampledProfile = sampledProfile;
    }

    public void readDeviceCpuFrequencies() {
        try {
            this.deviceCpuFrequencies = this.deviceCpuFrequenciesReader.call();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("android_api_level").value(logger, this.androidApiLevel);
        writer.name("device_locale").value(logger, this.deviceLocale);
        writer.name("device_manufacturer").value(this.deviceManufacturer);
        writer.name("device_model").value(this.deviceModel);
        writer.name("device_os_build_number").value(this.deviceOsBuildNumber);
        writer.name("device_os_name").value(this.deviceOsName);
        writer.name("device_os_version").value(this.deviceOsVersion);
        writer.name("device_is_emulator").value(this.deviceIsEmulator);
        writer.name("architecture").value(logger, this.cpuArchitecture);
        writer.name("device_cpu_frequencies").value(logger, this.deviceCpuFrequencies);
        writer.name("device_physical_memory_bytes").value(this.devicePhysicalMemoryBytes);
        writer.name("platform").value(this.platform);
        writer.name("build_id").value(this.buildId);
        writer.name("transaction_name").value(this.transactionName);
        writer.name("duration_ns").value(this.durationNs);
        writer.name("version_name").value(this.release);
        writer.name("version_code").value(this.versionCode);
        if (!this.transactions.isEmpty()) {
            writer.name("transactions").value(logger, this.transactions);
        }
        writer.name("transaction_id").value(this.transactionId);
        writer.name("trace_id").value(this.traceId);
        writer.name("profile_id").value(this.profileId);
        writer.name("environment").value(this.environment);
        writer.name("truncation_reason").value(this.truncationReason);
        if (this.sampledProfile != null) {
            writer.name("sampled_profile").value(this.sampledProfile);
        }
        String prevIndent = writer.getIndent();
        writer.setIndent("");
        writer.name("measurements").value(logger, this.measurementsMap);
        writer.setIndent(prevIndent);
        writer.name("timestamp").value(logger, this.timestamp);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key);
                writer.value(logger, value);
            }
        }
        writer.endObject();
    }

    @Override
    @Nullable
    public Map<String, Object> getUnknown() {
        return this.unknown;
    }

    @Override
    public void setUnknown(@Nullable Map<String, Object> unknown) {
        this.unknown = unknown;
    }

    public static final class JsonKeys {
        public static final String ANDROID_API_LEVEL = "android_api_level";
        public static final String DEVICE_LOCALE = "device_locale";
        public static final String DEVICE_MANUFACTURER = "device_manufacturer";
        public static final String DEVICE_MODEL = "device_model";
        public static final String DEVICE_OS_BUILD_NUMBER = "device_os_build_number";
        public static final String DEVICE_OS_NAME = "device_os_name";
        public static final String DEVICE_OS_VERSION = "device_os_version";
        public static final String DEVICE_IS_EMULATOR = "device_is_emulator";
        public static final String ARCHITECTURE = "architecture";
        public static final String DEVICE_CPU_FREQUENCIES = "device_cpu_frequencies";
        public static final String DEVICE_PHYSICAL_MEMORY_BYTES = "device_physical_memory_bytes";
        public static final String PLATFORM = "platform";
        public static final String BUILD_ID = "build_id";
        public static final String TRANSACTION_NAME = "transaction_name";
        public static final String DURATION_NS = "duration_ns";
        public static final String RELEASE = "version_name";
        public static final String VERSION_CODE = "version_code";
        public static final String TRANSACTION_LIST = "transactions";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String TRACE_ID = "trace_id";
        public static final String PROFILE_ID = "profile_id";
        public static final String ENVIRONMENT = "environment";
        public static final String SAMPLED_PROFILE = "sampled_profile";
        public static final String TRUNCATION_REASON = "truncation_reason";
        public static final String MEASUREMENTS = "measurements";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Deserializer
    implements JsonDeserializer<ProfilingTraceData> {
        @Override
        @NotNull
        public ProfilingTraceData deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            ProfilingTraceData data = new ProfilingTraceData();
            ConcurrentHashMap<String, Object> unknown = null;
            block56: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "android_api_level": {
                        Integer apiLevel = reader.nextIntegerOrNull();
                        if (apiLevel == null) continue block56;
                        data.androidApiLevel = apiLevel;
                        continue block56;
                    }
                    case "device_locale": {
                        String deviceLocale = reader.nextStringOrNull();
                        if (deviceLocale == null) continue block56;
                        data.deviceLocale = deviceLocale;
                        continue block56;
                    }
                    case "device_manufacturer": {
                        String deviceManufacturer = reader.nextStringOrNull();
                        if (deviceManufacturer == null) continue block56;
                        data.deviceManufacturer = deviceManufacturer;
                        continue block56;
                    }
                    case "device_model": {
                        String deviceModel = reader.nextStringOrNull();
                        if (deviceModel == null) continue block56;
                        data.deviceModel = deviceModel;
                        continue block56;
                    }
                    case "device_os_build_number": {
                        String deviceOsBuildNumber = reader.nextStringOrNull();
                        if (deviceOsBuildNumber == null) continue block56;
                        data.deviceOsBuildNumber = deviceOsBuildNumber;
                        continue block56;
                    }
                    case "device_os_name": {
                        String deviceOsName = reader.nextStringOrNull();
                        if (deviceOsName == null) continue block56;
                        data.deviceOsName = deviceOsName;
                        continue block56;
                    }
                    case "device_os_version": {
                        String deviceOsVersion = reader.nextStringOrNull();
                        if (deviceOsVersion == null) continue block56;
                        data.deviceOsVersion = deviceOsVersion;
                        continue block56;
                    }
                    case "device_is_emulator": {
                        Boolean deviceIsEmulator = reader.nextBooleanOrNull();
                        if (deviceIsEmulator == null) continue block56;
                        data.deviceIsEmulator = deviceIsEmulator;
                        continue block56;
                    }
                    case "architecture": {
                        String cpuArchitecture = reader.nextStringOrNull();
                        if (cpuArchitecture == null) continue block56;
                        data.cpuArchitecture = cpuArchitecture;
                        continue block56;
                    }
                    case "device_cpu_frequencies": {
                        List deviceCpuFrequencies = (List)reader.nextObjectOrNull();
                        if (deviceCpuFrequencies == null) continue block56;
                        data.deviceCpuFrequencies = deviceCpuFrequencies;
                        continue block56;
                    }
                    case "device_physical_memory_bytes": {
                        String devicePhysicalMemoryBytes = reader.nextStringOrNull();
                        if (devicePhysicalMemoryBytes == null) continue block56;
                        data.devicePhysicalMemoryBytes = devicePhysicalMemoryBytes;
                        continue block56;
                    }
                    case "platform": {
                        String platform = reader.nextStringOrNull();
                        if (platform == null) continue block56;
                        data.platform = platform;
                        continue block56;
                    }
                    case "build_id": {
                        String buildId = reader.nextStringOrNull();
                        if (buildId == null) continue block56;
                        data.buildId = buildId;
                        continue block56;
                    }
                    case "transaction_name": {
                        String transactionName = reader.nextStringOrNull();
                        if (transactionName == null) continue block56;
                        data.transactionName = transactionName;
                        continue block56;
                    }
                    case "duration_ns": {
                        String durationNs = reader.nextStringOrNull();
                        if (durationNs == null) continue block56;
                        data.durationNs = durationNs;
                        continue block56;
                    }
                    case "version_code": {
                        String versionCode = reader.nextStringOrNull();
                        if (versionCode == null) continue block56;
                        data.versionCode = versionCode;
                        continue block56;
                    }
                    case "version_name": {
                        String versionName = reader.nextStringOrNull();
                        if (versionName == null) continue block56;
                        data.release = versionName;
                        continue block56;
                    }
                    case "transactions": {
                        List<ProfilingTransactionData> transactions = reader.nextListOrNull(logger, new ProfilingTransactionData.Deserializer());
                        if (transactions == null) continue block56;
                        data.transactions.addAll(transactions);
                        continue block56;
                    }
                    case "transaction_id": {
                        String transactionId = reader.nextStringOrNull();
                        if (transactionId == null) continue block56;
                        data.transactionId = transactionId;
                        continue block56;
                    }
                    case "trace_id": {
                        String traceId = reader.nextStringOrNull();
                        if (traceId == null) continue block56;
                        data.traceId = traceId;
                        continue block56;
                    }
                    case "profile_id": {
                        String profileId = reader.nextStringOrNull();
                        if (profileId == null) continue block56;
                        data.profileId = profileId;
                        continue block56;
                    }
                    case "environment": {
                        String environment = reader.nextStringOrNull();
                        if (environment == null) continue block56;
                        data.environment = environment;
                        continue block56;
                    }
                    case "truncation_reason": {
                        String truncationReason = reader.nextStringOrNull();
                        if (truncationReason == null) continue block56;
                        data.truncationReason = truncationReason;
                        continue block56;
                    }
                    case "measurements": {
                        Map<String, ProfileMeasurement> measurements = reader.nextMapOrNull(logger, new ProfileMeasurement.Deserializer());
                        if (measurements == null) continue block56;
                        data.measurementsMap.putAll(measurements);
                        continue block56;
                    }
                    case "timestamp": {
                        Date timestamp = reader.nextDateOrNull(logger);
                        if (timestamp == null) continue block56;
                        data.timestamp = timestamp;
                        continue block56;
                    }
                    case "sampled_profile": {
                        String sampledProfile = reader.nextStringOrNull();
                        if (sampledProfile == null) continue block56;
                        data.sampledProfile = sampledProfile;
                        continue block56;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            data.setUnknown(unknown);
            reader.endObject();
            return data;
        }
    }
}

