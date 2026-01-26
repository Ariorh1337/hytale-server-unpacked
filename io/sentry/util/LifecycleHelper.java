/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.ISentryLifecycleToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LifecycleHelper {
    public static void close(@Nullable Object tokenObject) {
        if (tokenObject != null && tokenObject instanceof ISentryLifecycleToken) {
            @NotNull ISentryLifecycleToken token = (ISentryLifecycleToken)tokenObject;
            token.close();
        }
    }
}

