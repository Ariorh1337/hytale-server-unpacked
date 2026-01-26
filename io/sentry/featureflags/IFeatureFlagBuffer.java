/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.featureflags;

import io.sentry.protocol.FeatureFlags;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface IFeatureFlagBuffer {
    public void add(@Nullable String var1, @Nullable Boolean var2);

    @Nullable
    public FeatureFlags getFeatureFlags();

    @NotNull
    public IFeatureFlagBuffer clone();
}

