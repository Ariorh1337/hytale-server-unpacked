/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry.transport;

import io.sentry.transport.ICurrentDateProvider;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class CurrentDateProvider
implements ICurrentDateProvider {
    private static final ICurrentDateProvider instance = new CurrentDateProvider();

    public static ICurrentDateProvider getInstance() {
        return instance;
    }

    private CurrentDateProvider() {
    }

    @Override
    public final long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}

