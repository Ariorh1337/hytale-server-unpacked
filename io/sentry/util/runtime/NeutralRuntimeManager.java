/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.util.runtime;

import io.sentry.util.runtime.IRuntimeManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class NeutralRuntimeManager
implements IRuntimeManager {
    @Override
    public <T> T runWithRelaxedPolicy(@NotNull IRuntimeManager.IRuntimeManagerCallback<T> toRun) {
        return toRun.run();
    }

    @Override
    public void runWithRelaxedPolicy(@NotNull Runnable toRun) {
        toRun.run();
    }
}

