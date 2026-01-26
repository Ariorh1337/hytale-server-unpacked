/*
 * Decompiled with CFR 0.152.
 */
package io.sentry;

import io.sentry.ISentryLifecycleToken;

public final class NoOpScopesLifecycleToken
implements ISentryLifecycleToken {
    private static final NoOpScopesLifecycleToken instance = new NoOpScopesLifecycleToken();

    private NoOpScopesLifecycleToken() {
    }

    public static NoOpScopesLifecycleToken getInstance() {
        return instance;
    }

    @Override
    public void close() {
    }
}

