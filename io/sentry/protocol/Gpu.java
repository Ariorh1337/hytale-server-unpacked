/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Gpu
implements JsonUnknown,
JsonSerializable {
    public static final String TYPE = "gpu";
    @Nullable
    private String name;
    @Nullable
    private Integer id;
    @Nullable
    private String vendorId;
    @Nullable
    private String vendorName;
    @Nullable
    private Integer memorySize;
    @Nullable
    private String apiType;
    @Nullable
    private Boolean multiThreadedRendering;
    @Nullable
    private String version;
    @Nullable
    private String npotSupport;
    private @Nullable Map<String, @NotNull Object> unknown;

    public Gpu() {
    }

    Gpu(@NotNull Gpu gpu) {
        this.name = gpu.name;
        this.id = gpu.id;
        this.vendorId = gpu.vendorId;
        this.vendorName = gpu.vendorName;
        this.memorySize = gpu.memorySize;
        this.apiType = gpu.apiType;
        this.multiThreadedRendering = gpu.multiThreadedRendering;
        this.version = gpu.version;
        this.npotSupport = gpu.npotSupport;
        this.unknown = CollectionUtils.newConcurrentHashMap(gpu.unknown);
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Nullable
    public String getVendorId() {
        return this.vendorId;
    }

    public void setVendorId(@Nullable String vendorId) {
        this.vendorId = vendorId;
    }

    @Nullable
    public String getVendorName() {
        return this.vendorName;
    }

    public void setVendorName(@Nullable String vendorName) {
        this.vendorName = vendorName;
    }

    @Nullable
    public Integer getMemorySize() {
        return this.memorySize;
    }

    public void setMemorySize(@Nullable Integer memorySize) {
        this.memorySize = memorySize;
    }

    @Nullable
    public String getApiType() {
        return this.apiType;
    }

    public void setApiType(@Nullable String apiType) {
        this.apiType = apiType;
    }

    @Nullable
    public Boolean isMultiThreadedRendering() {
        return this.multiThreadedRendering;
    }

    public void setMultiThreadedRendering(@Nullable Boolean multiThreadedRendering) {
        this.multiThreadedRendering = multiThreadedRendering;
    }

    @Nullable
    public String getVersion() {
        return this.version;
    }

    public void setVersion(@Nullable String version) {
        this.version = version;
    }

    @Nullable
    public String getNpotSupport() {
        return this.npotSupport;
    }

    public void setNpotSupport(@Nullable String npotSupport) {
        this.npotSupport = npotSupport;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Gpu gpu = (Gpu)o;
        return Objects.equals(this.name, gpu.name) && Objects.equals(this.id, gpu.id) && Objects.equals(this.vendorId, gpu.vendorId) && Objects.equals(this.vendorName, gpu.vendorName) && Objects.equals(this.memorySize, gpu.memorySize) && Objects.equals(this.apiType, gpu.apiType) && Objects.equals(this.multiThreadedRendering, gpu.multiThreadedRendering) && Objects.equals(this.version, gpu.version) && Objects.equals(this.npotSupport, gpu.npotSupport);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.id, this.vendorId, this.vendorName, this.memorySize, this.apiType, this.multiThreadedRendering, this.version, this.npotSupport);
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        if (this.name != null) {
            writer.name("name").value(this.name);
        }
        if (this.id != null) {
            writer.name("id").value(this.id);
        }
        if (this.vendorId != null) {
            writer.name("vendor_id").value(this.vendorId);
        }
        if (this.vendorName != null) {
            writer.name("vendor_name").value(this.vendorName);
        }
        if (this.memorySize != null) {
            writer.name("memory_size").value(this.memorySize);
        }
        if (this.apiType != null) {
            writer.name("api_type").value(this.apiType);
        }
        if (this.multiThreadedRendering != null) {
            writer.name("multi_threaded_rendering").value(this.multiThreadedRendering);
        }
        if (this.version != null) {
            writer.name("version").value(this.version);
        }
        if (this.npotSupport != null) {
            writer.name("npot_support").value(this.npotSupport);
        }
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
        public static final String NAME = "name";
        public static final String ID = "id";
        public static final String VENDOR_ID = "vendor_id";
        public static final String VENDOR_NAME = "vendor_name";
        public static final String MEMORY_SIZE = "memory_size";
        public static final String API_TYPE = "api_type";
        public static final String MULTI_THREADED_RENDERING = "multi_threaded_rendering";
        public static final String VERSION = "version";
        public static final String NPOT_SUPPORT = "npot_support";
    }

    public static final class Deserializer
    implements JsonDeserializer<Gpu> {
        @Override
        @NotNull
        public Gpu deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            Gpu gpu = new Gpu();
            ConcurrentHashMap<String, Object> unknown = null;
            block22: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "name": {
                        gpu.name = reader.nextStringOrNull();
                        continue block22;
                    }
                    case "id": {
                        gpu.id = reader.nextIntegerOrNull();
                        continue block22;
                    }
                    case "vendor_id": {
                        gpu.vendorId = reader.nextStringOrNull();
                        continue block22;
                    }
                    case "vendor_name": {
                        gpu.vendorName = reader.nextStringOrNull();
                        continue block22;
                    }
                    case "memory_size": {
                        gpu.memorySize = reader.nextIntegerOrNull();
                        continue block22;
                    }
                    case "api_type": {
                        gpu.apiType = reader.nextStringOrNull();
                        continue block22;
                    }
                    case "multi_threaded_rendering": {
                        gpu.multiThreadedRendering = reader.nextBooleanOrNull();
                        continue block22;
                    }
                    case "version": {
                        gpu.version = reader.nextStringOrNull();
                        continue block22;
                    }
                    case "npot_support": {
                        gpu.npotSupport = reader.nextStringOrNull();
                        continue block22;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            gpu.setUnknown(unknown);
            reader.endObject();
            return gpu;
        }
    }
}

