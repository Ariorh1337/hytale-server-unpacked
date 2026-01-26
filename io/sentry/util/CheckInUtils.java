/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.CheckIn;
import io.sentry.CheckInStatus;
import io.sentry.DateUtils;
import io.sentry.FilterString;
import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.MonitorConfig;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import io.sentry.util.TracingUtils;
import java.util.List;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public final class CheckInUtils {
    public static <U> U withCheckIn(@NotNull String monitorSlug, @Nullable String environment, @Nullable MonitorConfig monitorConfig, @NotNull Callable<U> callable) throws Exception {
        try (@NotNull ISentryLifecycleToken ignored = Sentry.forkedScopes("CheckInUtils").makeCurrent();){
            CheckInStatus status;
            U u;
            @NotNull IScopes scopes = Sentry.getCurrentScopes();
            long startTime = System.currentTimeMillis();
            boolean didError = false;
            TracingUtils.startNewTrace(scopes);
            CheckIn inProgressCheckIn = new CheckIn(monitorSlug, CheckInStatus.IN_PROGRESS);
            if (monitorConfig != null) {
                inProgressCheckIn.setMonitorConfig(monitorConfig);
            }
            if (environment != null) {
                inProgressCheckIn.setEnvironment(environment);
            }
            @Nullable SentryId checkInId = scopes.captureCheckIn(inProgressCheckIn);
            try {
                u = callable.call();
                status = didError ? CheckInStatus.ERROR : CheckInStatus.OK;
            }
            catch (Throwable t) {
                try {
                    didError = true;
                    throw t;
                }
                catch (Throwable throwable) {
                    @NotNull CheckInStatus status2 = didError ? CheckInStatus.ERROR : CheckInStatus.OK;
                    CheckIn checkIn = new CheckIn(checkInId, monitorSlug, status2);
                    if (environment != null) {
                        checkIn.setEnvironment(environment);
                    }
                    checkIn.setDuration(DateUtils.millisToSeconds(System.currentTimeMillis() - startTime));
                    scopes.captureCheckIn(checkIn);
                    throw throwable;
                }
            }
            CheckIn checkIn = new CheckIn(checkInId, monitorSlug, status);
            if (environment != null) {
                checkIn.setEnvironment(environment);
            }
            checkIn.setDuration(DateUtils.millisToSeconds(System.currentTimeMillis() - startTime));
            scopes.captureCheckIn(checkIn);
            return u;
        }
    }

    public static <U> U withCheckIn(@NotNull String monitorSlug, @Nullable MonitorConfig monitorConfig, @NotNull Callable<U> callable) throws Exception {
        return CheckInUtils.withCheckIn(monitorSlug, null, monitorConfig, callable);
    }

    public static <U> U withCheckIn(@NotNull String monitorSlug, @Nullable String environment, @NotNull Callable<U> callable) throws Exception {
        return CheckInUtils.withCheckIn(monitorSlug, environment, null, callable);
    }

    public static <U> U withCheckIn(@NotNull String monitorSlug, @NotNull Callable<U> callable) throws Exception {
        return CheckInUtils.withCheckIn(monitorSlug, null, null, callable);
    }

    @ApiStatus.Internal
    public static boolean isIgnored(@Nullable List<FilterString> ignoredSlugs, @NotNull String slug) {
        if (ignoredSlugs == null || ignoredSlugs.isEmpty()) {
            return false;
        }
        for (FilterString ignoredSlug : ignoredSlugs) {
            if (!ignoredSlug.getFilterString().equalsIgnoreCase(slug)) continue;
            return true;
        }
        for (FilterString ignoredSlug : ignoredSlugs) {
            try {
                if (!ignoredSlug.matches(slug)) continue;
                return true;
            }
            catch (Throwable throwable) {
            }
        }
        return false;
    }
}

