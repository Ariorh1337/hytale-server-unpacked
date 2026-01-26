/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IScopes;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.NotNull;

public interface Integration {
    public void register(@NotNull IScopes var1, @NotNull SentryOptions var2);
}

