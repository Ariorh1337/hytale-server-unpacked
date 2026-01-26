/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.ISentryLifecycleToken;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.ProfileContext;
import io.sentry.SpanContext;
import io.sentry.protocol.App;
import io.sentry.protocol.Browser;
import io.sentry.protocol.Device;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.Gpu;
import io.sentry.protocol.OperatingSystem;
import io.sentry.protocol.Response;
import io.sentry.protocol.SentryRuntime;
import io.sentry.protocol.Spring;
import io.sentry.util.AutoClosableReentrantLock;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Contexts
implements JsonSerializable {
    private static final long serialVersionUID = 252445813254943011L;
    public static final String REPLAY_ID = "replay_id";
    @NotNull
    private final ConcurrentHashMap<String, Object> internalStorage = new ConcurrentHashMap();
    @NotNull
    protected final AutoClosableReentrantLock responseLock = new AutoClosableReentrantLock();

    public Contexts() {
    }

    public Contexts(@NotNull Contexts contexts) {
        for (Map.Entry<String, Object> entry : contexts.entrySet()) {
            if (entry == null) continue;
            Object value = entry.getValue();
            if ("app".equals(entry.getKey()) && value instanceof App) {
                this.setApp(new App((App)value));
                continue;
            }
            if ("browser".equals(entry.getKey()) && value instanceof Browser) {
                this.setBrowser(new Browser((Browser)value));
                continue;
            }
            if ("device".equals(entry.getKey()) && value instanceof Device) {
                this.setDevice(new Device((Device)value));
                continue;
            }
            if ("os".equals(entry.getKey()) && value instanceof OperatingSystem) {
                this.setOperatingSystem(new OperatingSystem((OperatingSystem)value));
                continue;
            }
            if ("runtime".equals(entry.getKey()) && value instanceof SentryRuntime) {
                this.setRuntime(new SentryRuntime((SentryRuntime)value));
                continue;
            }
            if ("feedback".equals(entry.getKey()) && value instanceof Feedback) {
                this.setFeedback(new Feedback((Feedback)value));
                continue;
            }
            if ("gpu".equals(entry.getKey()) && value instanceof Gpu) {
                this.setGpu(new Gpu((Gpu)value));
                continue;
            }
            if ("trace".equals(entry.getKey()) && value instanceof SpanContext) {
                this.setTrace(new SpanContext((SpanContext)value));
                continue;
            }
            if ("profile".equals(entry.getKey()) && value instanceof ProfileContext) {
                this.setProfile(new ProfileContext((ProfileContext)value));
                continue;
            }
            if ("response".equals(entry.getKey()) && value instanceof Response) {
                this.setResponse(new Response((Response)value));
                continue;
            }
            if ("spring".equals(entry.getKey()) && value instanceof Spring) {
                this.setSpring(new Spring((Spring)value));
                continue;
            }
            this.put(entry.getKey(), value);
        }
    }

    @Nullable
    private <T> T toContextType(@NotNull String key, @NotNull Class<T> clazz) {
        Object item = this.get(key);
        return clazz.isInstance(item) ? (T)clazz.cast(item) : null;
    }

    @Nullable
    public SpanContext getTrace() {
        return this.toContextType("trace", SpanContext.class);
    }

    public void setTrace(@NotNull SpanContext traceContext) {
        Objects.requireNonNull(traceContext, "traceContext is required");
        this.put("trace", traceContext);
    }

    @Nullable
    public ProfileContext getProfile() {
        return this.toContextType("profile", ProfileContext.class);
    }

    public void setProfile(@Nullable ProfileContext profileContext) {
        Objects.requireNonNull(profileContext, "profileContext is required");
        this.put("profile", profileContext);
    }

    @Nullable
    public App getApp() {
        return this.toContextType("app", App.class);
    }

    public void setApp(@NotNull App app) {
        this.put("app", app);
    }

    @Nullable
    public Browser getBrowser() {
        return this.toContextType("browser", Browser.class);
    }

    public void setBrowser(@NotNull Browser browser) {
        this.put("browser", browser);
    }

    @Nullable
    public Device getDevice() {
        return this.toContextType("device", Device.class);
    }

    public void setDevice(@NotNull Device device) {
        this.put("device", device);
    }

    @Nullable
    public OperatingSystem getOperatingSystem() {
        return this.toContextType("os", OperatingSystem.class);
    }

    public void setOperatingSystem(@NotNull OperatingSystem operatingSystem) {
        this.put("os", operatingSystem);
    }

    @Nullable
    public SentryRuntime getRuntime() {
        return this.toContextType("runtime", SentryRuntime.class);
    }

    public void setRuntime(@NotNull SentryRuntime runtime) {
        this.put("runtime", runtime);
    }

    @Nullable
    public Feedback getFeedback() {
        return this.toContextType("feedback", Feedback.class);
    }

    public void setFeedback(@NotNull Feedback feedback) {
        this.put("feedback", feedback);
    }

    @Nullable
    public Gpu getGpu() {
        return this.toContextType("gpu", Gpu.class);
    }

    public void setGpu(@NotNull Gpu gpu) {
        this.put("gpu", gpu);
    }

    @Nullable
    public Response getResponse() {
        return this.toContextType("response", Response.class);
    }

    public void withResponse(HintUtils.SentryConsumer<Response> callback) {
        try (@NotNull ISentryLifecycleToken ignored = this.responseLock.acquire();){
            @Nullable Response response = this.getResponse();
            if (response != null) {
                callback.accept(response);
            } else {
                @NotNull Response newResponse = new Response();
                this.setResponse(newResponse);
                callback.accept(newResponse);
            }
        }
    }

    public void setResponse(@NotNull Response response) {
        try (@NotNull ISentryLifecycleToken ignored = this.responseLock.acquire();){
            this.put("response", response);
        }
    }

    @Nullable
    public Spring getSpring() {
        return this.toContextType("spring", Spring.class);
    }

    public void setSpring(@NotNull Spring spring) {
        this.put("spring", spring);
    }

    @Nullable
    public FeatureFlags getFeatureFlags() {
        return this.toContextType("flags", FeatureFlags.class);
    }

    public void setFeatureFlags(@NotNull FeatureFlags featureFlags) {
        this.put("flags", featureFlags);
    }

    public int size() {
        return this.internalStorage.size();
    }

    public int getSize() {
        return this.size();
    }

    public boolean isEmpty() {
        return this.internalStorage.isEmpty();
    }

    public boolean containsKey(@Nullable Object key) {
        if (key == null) {
            return false;
        }
        return this.internalStorage.containsKey(key);
    }

    @Nullable
    public Object get(@Nullable Object key) {
        if (key == null) {
            return null;
        }
        return this.internalStorage.get(key);
    }

    @Nullable
    public Object put(@Nullable String key, @Nullable Object value) {
        if (key == null) {
            return null;
        }
        if (value == null) {
            return this.internalStorage.remove(key);
        }
        return this.internalStorage.put(key, value);
    }

    @Nullable
    public Object set(@Nullable String key, @Nullable Object value) {
        return this.put(key, value);
    }

    @Nullable
    public Object remove(@Nullable Object key) {
        if (key == null) {
            return null;
        }
        return this.internalStorage.remove(key);
    }

    @NotNull
    public Enumeration<String> keys() {
        return this.internalStorage.keys();
    }

    @NotNull
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.internalStorage.entrySet();
    }

    public void putAll(@Nullable Map<? extends String, ? extends Object> m) {
        if (m == null) {
            return;
        }
        @NotNull HashMap<String, Object> tmpMap = new HashMap<String, Object>();
        for (Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            tmpMap.put(entry.getKey(), entry.getValue());
        }
        this.internalStorage.putAll(tmpMap);
    }

    public void putAll(@Nullable Contexts contexts) {
        if (contexts == null) {
            return;
        }
        this.internalStorage.putAll(contexts.internalStorage);
    }

    public boolean equals(@Nullable Object obj) {
        if (obj != null && obj instanceof Contexts) {
            @NotNull Contexts otherContexts = (Contexts)obj;
            return this.internalStorage.equals(otherContexts.internalStorage);
        }
        return false;
    }

    public int hashCode() {
        return this.internalStorage.hashCode();
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        ArrayList<String> sortedKeys = Collections.list(this.keys());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            Object value = this.get(key);
            if (value == null) continue;
            writer.name(key).value(logger, value);
        }
        writer.endObject();
    }

    public static final class Deserializer
    implements JsonDeserializer<Contexts> {
        @Override
        @NotNull
        public Contexts deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            Contexts contexts = new Contexts();
            reader.beginObject();
            block28: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "app": {
                        contexts.setApp(new App.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "browser": {
                        contexts.setBrowser(new Browser.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "device": {
                        contexts.setDevice(new Device.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "gpu": {
                        contexts.setGpu(new Gpu.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "os": {
                        contexts.setOperatingSystem(new OperatingSystem.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "runtime": {
                        contexts.setRuntime(new SentryRuntime.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "feedback": {
                        contexts.setFeedback(new Feedback.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "trace": {
                        contexts.setTrace(new SpanContext.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "profile": {
                        contexts.setProfile(new ProfileContext.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "response": {
                        contexts.setResponse(new Response.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "spring": {
                        contexts.setSpring(new Spring.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                    case "flags": {
                        contexts.setFeatureFlags(new FeatureFlags.Deserializer().deserialize(reader, logger));
                        continue block28;
                    }
                }
                Object object = reader.nextObjectOrNull();
                if (object == null) continue;
                contexts.put(nextName, object);
            }
            reader.endObject();
            return contexts;
        }
    }
}

