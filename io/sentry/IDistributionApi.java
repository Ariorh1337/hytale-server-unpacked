/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.UpdateInfo;
import io.sentry.UpdateStatus;
import java.util.concurrent.Future;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public interface IDistributionApi {
    @NotNull
    public UpdateStatus checkForUpdateBlocking();

    @NotNull
    public Future<UpdateStatus> checkForUpdate();

    public void downloadUpdate(@NotNull UpdateInfo var1);

    public boolean isEnabled();
}

