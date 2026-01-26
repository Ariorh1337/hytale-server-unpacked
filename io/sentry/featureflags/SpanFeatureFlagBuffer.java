/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.featureflags;

import io.sentry.ISentryLifecycleToken;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.protocol.FeatureFlag;
import io.sentry.protocol.FeatureFlags;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SpanFeatureFlagBuffer
implements IFeatureFlagBuffer {
    private static final int MAX_SIZE = 10;
    @Nullable
    private Map<String, Boolean> flags = null;
    @NotNull
    private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

    private SpanFeatureFlagBuffer() {
    }

    @Override
    public void add(@Nullable String flag, @Nullable Boolean result) {
        if (flag == null || result == null) {
            return;
        }
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            if (this.flags == null) {
                this.flags = new LinkedHashMap<String, Boolean>(10);
            }
            if (this.flags.size() < 10 || this.flags.containsKey(flag)) {
                this.flags.put(flag, result);
            }
        }
    }

    @Override
    @Nullable
    public FeatureFlags getFeatureFlags() {
        try (@NotNull ISentryLifecycleToken ignored = this.lock.acquire();){
            if (this.flags == null || this.flags.isEmpty()) {
                FeatureFlags featureFlags = null;
                return featureFlags;
            }
            ArrayList<FeatureFlag> featureFlags = new ArrayList<FeatureFlag>(this.flags.size());
            for (Map.Entry<String, Boolean> entry : this.flags.entrySet()) {
                featureFlags.add(new FeatureFlag(entry.getKey(), entry.getValue()));
            }
            FeatureFlags featureFlags2 = new FeatureFlags(featureFlags);
            return featureFlags2;
        }
    }

    @Override
    @NotNull
    public IFeatureFlagBuffer clone() {
        return SpanFeatureFlagBuffer.create();
    }

    @NotNull
    public static IFeatureFlagBuffer create() {
        return new SpanFeatureFlagBuffer();
    }
}

