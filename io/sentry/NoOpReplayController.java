/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.NoOpReplayBreadcrumbConverter;
import io.sentry.ReplayBreadcrumbConverter;
import io.sentry.ReplayController;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpReplayController
implements ReplayController {
    private static final NoOpReplayController instance = new NoOpReplayController();

    public static NoOpReplayController getInstance() {
        return instance;
    }

    private NoOpReplayController() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean isRecording() {
        return false;
    }

    @Override
    public void captureReplay(@Nullable Boolean isTerminating) {
    }

    @Override
    @NotNull
    public SentryId getReplayId() {
        return SentryId.EMPTY_ID;
    }

    @Override
    public void setBreadcrumbConverter(@NotNull ReplayBreadcrumbConverter converter) {
    }

    @Override
    @NotNull
    public ReplayBreadcrumbConverter getBreadcrumbConverter() {
        return NoOpReplayBreadcrumbConverter.getInstance();
    }

    @Override
    public boolean isDebugMaskingOverlayEnabled() {
        return false;
    }

    @Override
    public void enableDebugMaskingOverlay() {
    }

    @Override
    public void disableDebugMaskingOverlay() {
    }
}

