/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.IScopes;
import io.sentry.IScopesStorage;
import io.sentry.ISentryLifecycleToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class DefaultScopesStorage
implements IScopesStorage {
    @NotNull
    private static final ThreadLocal<IScopes> currentScopes = new ThreadLocal();

    @Override
    public void init() {
    }

    @Override
    public ISentryLifecycleToken set(@Nullable IScopes scopes) {
        @Nullable IScopes oldScopes = this.get();
        currentScopes.set(scopes);
        return new DefaultScopesLifecycleToken(oldScopes);
    }

    @Override
    @Nullable
    public IScopes get() {
        return currentScopes.get();
    }

    @Override
    public void close() {
        currentScopes.remove();
    }

    static final class DefaultScopesLifecycleToken
    implements ISentryLifecycleToken {
        @Nullable
        private final IScopes oldValue;

        DefaultScopesLifecycleToken(@Nullable IScopes scopes) {
            this.oldValue = scopes;
        }

        @Override
        public void close() {
            currentScopes.set(this.oldValue);
        }
    }
}

