/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.util.runtime;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public interface IRuntimeManager {
    public <T> T runWithRelaxedPolicy(@NotNull IRuntimeManagerCallback<T> var1);

    public void runWithRelaxedPolicy(@NotNull Runnable var1);

    public static interface IRuntimeManagerCallback<T> {
        public T run();
    }
}

