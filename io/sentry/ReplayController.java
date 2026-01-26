/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.IReplayApi;
import io.sentry.ReplayBreadcrumbConverter;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface ReplayController
extends IReplayApi {
    public void start();

    public void stop();

    public void pause();

    public void resume();

    public boolean isRecording();

    public void captureReplay(@Nullable Boolean var1);

    @NotNull
    public SentryId getReplayId();

    public void setBreadcrumbConverter(@NotNull ReplayBreadcrumbConverter var1);

    @NotNull
    public ReplayBreadcrumbConverter getBreadcrumbConverter();

    public boolean isDebugMaskingOverlayEnabled();
}

