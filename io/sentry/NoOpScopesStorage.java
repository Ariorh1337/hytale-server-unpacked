/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.IScopes;
import io.sentry.IScopesStorage;
import io.sentry.ISentryLifecycleToken;
import io.sentry.NoOpScopes;
import io.sentry.NoOpScopesLifecycleToken;
import org.jetbrains.annotations.Nullable;

public final class NoOpScopesStorage
implements IScopesStorage {
    private static final NoOpScopesStorage instance = new NoOpScopesStorage();

    private NoOpScopesStorage() {
    }

    public static NoOpScopesStorage getInstance() {
        return instance;
    }

    @Override
    public void init() {
    }

    @Override
    public ISentryLifecycleToken set(@Nullable IScopes scopes) {
        return NoOpScopesLifecycleToken.getInstance();
    }

    @Override
    @Nullable
    public IScopes get() {
        return NoOpScopes.getInstance();
    }

    @Override
    public void close() {
    }
}

