/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ISentryLifecycleToken;
import io.sentry.ISpan;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
@ApiStatus.Internal
public final class SentrySpanStorage {
    @Nullable
    private static volatile SentrySpanStorage INSTANCE;
    @NotNull
    private static final AutoClosableReentrantLock staticLock;
    @NotNull
    private final Map<String, ISpan> spans = new ConcurrentHashMap<String, ISpan>();

    @NotNull
    public static SentrySpanStorage getInstance() {
        if (INSTANCE == null) {
            try (@NotNull ISentryLifecycleToken ignored = staticLock.acquire();){
                if (INSTANCE == null) {
                    INSTANCE = new SentrySpanStorage();
                }
            }
        }
        return INSTANCE;
    }

    private SentrySpanStorage() {
    }

    public void store(@NotNull String spanId, @NotNull ISpan span) {
        this.spans.put(spanId, span);
    }

    @Nullable
    public ISpan get(@Nullable String spanId) {
        return this.spans.get(spanId);
    }

    @Nullable
    public ISpan removeAndGet(@Nullable String spanId) {
        return this.spans.remove(spanId);
    }

    static {
        staticLock = new AutoClosableReentrantLock();
    }
}

