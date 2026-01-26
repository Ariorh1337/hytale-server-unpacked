/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.ObjectWriter;
import io.sentry.ScopeType;
import io.sentry.SpanContext;
import io.sentry.protocol.App;
import io.sentry.protocol.Browser;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.Device;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Gpu;
import io.sentry.protocol.OperatingSystem;
import io.sentry.protocol.Response;
import io.sentry.protocol.SentryRuntime;
import io.sentry.protocol.Spring;
import io.sentry.util.HintUtils;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CombinedContextsView
extends Contexts {
    private static final long serialVersionUID = 3585992094653318439L;
    @NotNull
    private final Contexts globalContexts;
    @NotNull
    private final Contexts isolationContexts;
    @NotNull
    private final Contexts currentContexts;
    @NotNull
    private final ScopeType defaultScopeType;

    public CombinedContextsView(@NotNull Contexts globalContexts, @NotNull Contexts isolationContexts, @NotNull Contexts currentContexts, @NotNull ScopeType defaultScopeType) {
        this.globalContexts = globalContexts;
        this.isolationContexts = isolationContexts;
        this.currentContexts = currentContexts;
        this.defaultScopeType = defaultScopeType;
    }

    @Override
    @Nullable
    public SpanContext getTrace() {
        @Nullable SpanContext current = this.currentContexts.getTrace();
        if (current != null) {
            return current;
        }
        @Nullable SpanContext isolation = this.isolationContexts.getTrace();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getTrace();
    }

    @Override
    public void setTrace(@NotNull SpanContext traceContext) {
        this.getDefaultContexts().setTrace(traceContext);
    }

    @NotNull
    private Contexts getDefaultContexts() {
        switch (this.defaultScopeType) {
            case CURRENT: {
                return this.currentContexts;
            }
            case ISOLATION: {
                return this.isolationContexts;
            }
            case GLOBAL: {
                return this.globalContexts;
            }
        }
        return this.currentContexts;
    }

    @Override
    @Nullable
    public App getApp() {
        @Nullable App current = this.currentContexts.getApp();
        if (current != null) {
            return current;
        }
        @Nullable App isolation = this.isolationContexts.getApp();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getApp();
    }

    @Override
    public void setApp(@NotNull App app) {
        this.getDefaultContexts().setApp(app);
    }

    @Override
    @Nullable
    public Browser getBrowser() {
        @Nullable Browser current = this.currentContexts.getBrowser();
        if (current != null) {
            return current;
        }
        @Nullable Browser isolation = this.isolationContexts.getBrowser();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getBrowser();
    }

    @Override
    public void setBrowser(@NotNull Browser browser) {
        this.getDefaultContexts().setBrowser(browser);
    }

    @Override
    @Nullable
    public Device getDevice() {
        @Nullable Device current = this.currentContexts.getDevice();
        if (current != null) {
            return current;
        }
        @Nullable Device isolation = this.isolationContexts.getDevice();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getDevice();
    }

    @Override
    public void setDevice(@NotNull Device device) {
        this.getDefaultContexts().setDevice(device);
    }

    @Override
    @Nullable
    public OperatingSystem getOperatingSystem() {
        @Nullable OperatingSystem current = this.currentContexts.getOperatingSystem();
        if (current != null) {
            return current;
        }
        @Nullable OperatingSystem isolation = this.isolationContexts.getOperatingSystem();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getOperatingSystem();
    }

    @Override
    public void setOperatingSystem(@NotNull OperatingSystem operatingSystem) {
        this.getDefaultContexts().setOperatingSystem(operatingSystem);
    }

    @Override
    @Nullable
    public SentryRuntime getRuntime() {
        @Nullable SentryRuntime current = this.currentContexts.getRuntime();
        if (current != null) {
            return current;
        }
        @Nullable SentryRuntime isolation = this.isolationContexts.getRuntime();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getRuntime();
    }

    @Override
    public void setRuntime(@NotNull SentryRuntime runtime) {
        this.getDefaultContexts().setRuntime(runtime);
    }

    @Override
    @Nullable
    public Gpu getGpu() {
        @Nullable Gpu current = this.currentContexts.getGpu();
        if (current != null) {
            return current;
        }
        @Nullable Gpu isolation = this.isolationContexts.getGpu();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getGpu();
    }

    @Override
    public void setGpu(@NotNull Gpu gpu) {
        this.getDefaultContexts().setGpu(gpu);
    }

    @Override
    @Nullable
    public Response getResponse() {
        @Nullable Response current = this.currentContexts.getResponse();
        if (current != null) {
            return current;
        }
        @Nullable Response isolation = this.isolationContexts.getResponse();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getResponse();
    }

    @Override
    public void withResponse(HintUtils.SentryConsumer<Response> callback) {
        if (this.currentContexts.getResponse() != null) {
            this.currentContexts.withResponse(callback);
        } else if (this.isolationContexts.getResponse() != null) {
            this.isolationContexts.withResponse(callback);
        } else if (this.globalContexts.getResponse() != null) {
            this.globalContexts.withResponse(callback);
        } else {
            this.getDefaultContexts().withResponse(callback);
        }
    }

    @Override
    public void setResponse(@NotNull Response response) {
        this.getDefaultContexts().setResponse(response);
    }

    @Override
    @Nullable
    public Spring getSpring() {
        @Nullable Spring current = this.currentContexts.getSpring();
        if (current != null) {
            return current;
        }
        @Nullable Spring isolation = this.isolationContexts.getSpring();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getSpring();
    }

    @Override
    public void setSpring(@NotNull Spring spring) {
        this.getDefaultContexts().setSpring(spring);
    }

    @Override
    @Nullable
    public FeatureFlags getFeatureFlags() {
        @Nullable FeatureFlags current = this.currentContexts.getFeatureFlags();
        if (current != null) {
            return current;
        }
        @Nullable FeatureFlags isolation = this.isolationContexts.getFeatureFlags();
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.getFeatureFlags();
    }

    @Override
    @ApiStatus.Internal
    public void setFeatureFlags(@NotNull FeatureFlags spring) {
        this.getDefaultContexts().setFeatureFlags(spring);
    }

    @Override
    public int size() {
        return this.mergeContexts().size();
    }

    @Override
    public int getSize() {
        return this.size();
    }

    @Override
    public boolean isEmpty() {
        return this.globalContexts.isEmpty() && this.isolationContexts.isEmpty() && this.currentContexts.isEmpty();
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return this.globalContexts.containsKey(key) || this.isolationContexts.containsKey(key) || this.currentContexts.containsKey(key);
    }

    @Override
    @Nullable
    public Object get(@Nullable Object key) {
        @Nullable Object current = this.currentContexts.get(key);
        if (current != null) {
            return current;
        }
        @Nullable Object isolation = this.isolationContexts.get(key);
        if (isolation != null) {
            return isolation;
        }
        return this.globalContexts.get(key);
    }

    @Override
    @Nullable
    public Object put(@Nullable String key, @Nullable Object value) {
        return this.getDefaultContexts().put(key, value);
    }

    @Override
    @Nullable
    public Object remove(@Nullable Object key) {
        return this.getDefaultContexts().remove(key);
    }

    @Override
    @NotNull
    public Enumeration<String> keys() {
        return this.mergeContexts().keys();
    }

    @Override
    @NotNull
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.mergeContexts().entrySet();
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        this.mergeContexts().serialize(writer, logger);
    }

    @Override
    @Nullable
    public Object set(@Nullable String key, @Nullable Object value) {
        return this.put(key, value);
    }

    public void putAll(@Nullable Map<? extends String, ?> m) {
        this.getDefaultContexts().putAll(m);
    }

    @Override
    public void putAll(@Nullable Contexts contexts) {
        this.getDefaultContexts().putAll(contexts);
    }

    @NotNull
    private Contexts mergeContexts() {
        @NotNull Contexts allContexts = new Contexts();
        allContexts.putAll(this.globalContexts);
        allContexts.putAll(this.isolationContexts);
        allContexts.putAll(this.currentContexts);
        return allContexts;
    }
}

