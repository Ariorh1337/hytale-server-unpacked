/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.featureflags;

import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.protocol.FeatureFlags;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class NoOpFeatureFlagBuffer
implements IFeatureFlagBuffer {
    private static final NoOpFeatureFlagBuffer instance = new NoOpFeatureFlagBuffer();

    public static NoOpFeatureFlagBuffer getInstance() {
        return instance;
    }

    @Override
    public void add(@Nullable String flag, @Nullable Boolean result) {
    }

    @Override
    @Nullable
    public FeatureFlags getFeatureFlags() {
        return null;
    }

    @Override
    @NotNull
    public IFeatureFlagBuffer clone() {
        return instance;
    }
}

