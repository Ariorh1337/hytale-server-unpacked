/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Breadcrumb;
import io.sentry.ReplayBreadcrumbConverter;
import io.sentry.rrweb.RRWebEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpReplayBreadcrumbConverter
implements ReplayBreadcrumbConverter {
    private static final NoOpReplayBreadcrumbConverter instance = new NoOpReplayBreadcrumbConverter();

    public static NoOpReplayBreadcrumbConverter getInstance() {
        return instance;
    }

    private NoOpReplayBreadcrumbConverter() {
    }

    @Override
    @Nullable
    public RRWebEvent convert(@NotNull Breadcrumb breadcrumb) {
        return null;
    }
}

