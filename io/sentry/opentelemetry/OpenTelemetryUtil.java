/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.opentelemetry;

import io.sentry.NoOpLogger;
import io.sentry.SentryLevel;
import io.sentry.SentryOpenTelemetryMode;
import io.sentry.SentryOptions;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import io.sentry.util.SpanUtils;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class OpenTelemetryUtil {
    @ApiStatus.Internal
    public static void applyIgnoredSpanOrigins(@NotNull SentryOptions options) {
        if (Platform.isJvm()) {
            @NotNull List<String> ignored = OpenTelemetryUtil.ignoredSpanOrigins(options);
            for (String origin : ignored) {
                options.addIgnoredSpanOrigin(origin);
            }
        }
    }

    @ApiStatus.Internal
    public static void updateOpenTelemetryModeIfAuto(@NotNull SentryOptions options, @NotNull LoadClass loadClass) {
        if (!Platform.isJvm()) {
            return;
        }
        @NotNull SentryOpenTelemetryMode openTelemetryMode = options.getOpenTelemetryMode();
        if (SentryOpenTelemetryMode.AUTO.equals((Object)openTelemetryMode)) {
            if (loadClass.isClassAvailable("io.sentry.opentelemetry.agent.AgentMarker", NoOpLogger.getInstance())) {
                options.getLogger().log(SentryLevel.DEBUG, "openTelemetryMode has been inferred from AUTO to AGENT", new Object[0]);
                options.setOpenTelemetryMode(SentryOpenTelemetryMode.AGENT);
                return;
            }
            if (loadClass.isClassAvailable("io.sentry.opentelemetry.agent.AgentlessMarker", NoOpLogger.getInstance())) {
                options.getLogger().log(SentryLevel.DEBUG, "openTelemetryMode has been inferred from AUTO to AGENTLESS", new Object[0]);
                options.setOpenTelemetryMode(SentryOpenTelemetryMode.AGENTLESS);
                return;
            }
            if (loadClass.isClassAvailable("io.sentry.opentelemetry.agent.AgentlessSpringMarker", NoOpLogger.getInstance())) {
                options.getLogger().log(SentryLevel.DEBUG, "openTelemetryMode has been inferred from AUTO to AGENTLESS_SPRING", new Object[0]);
                options.setOpenTelemetryMode(SentryOpenTelemetryMode.AGENTLESS_SPRING);
                return;
            }
        }
    }

    @NotNull
    private static List<String> ignoredSpanOrigins(@NotNull SentryOptions options) {
        @NotNull SentryOpenTelemetryMode openTelemetryMode = options.getOpenTelemetryMode();
        if (SentryOpenTelemetryMode.OFF.equals((Object)openTelemetryMode)) {
            return Collections.emptyList();
        }
        return SpanUtils.ignoredSpanOriginsForOpenTelemetry(openTelemetryMode);
    }
}

