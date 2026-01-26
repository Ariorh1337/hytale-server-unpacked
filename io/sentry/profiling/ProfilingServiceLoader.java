/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.profiling;

import io.sentry.IContinuousProfiler;
import io.sentry.ILogger;
import io.sentry.IProfileConverter;
import io.sentry.ISentryExecutorService;
import io.sentry.NoOpContinuousProfiler;
import io.sentry.NoOpProfileConverter;
import io.sentry.ScopesAdapter;
import io.sentry.SentryLevel;
import io.sentry.profiling.JavaContinuousProfilerProvider;
import io.sentry.profiling.JavaProfileConverterProvider;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ProfilingServiceLoader {
    @NotNull
    public static IContinuousProfiler loadContinuousProfiler(ILogger logger, String profilingTracesDirPath, int profilingTracesHz, ISentryExecutorService executorService) {
        try {
            JavaContinuousProfilerProvider provider = ProfilingServiceLoader.loadSingleProvider(JavaContinuousProfilerProvider.class);
            if (provider != null) {
                logger.log(SentryLevel.DEBUG, "Loaded continuous profiler from provider: %s", provider.getClass().getName());
                return provider.getContinuousProfiler(logger, profilingTracesDirPath, profilingTracesHz, executorService);
            }
            logger.log(SentryLevel.DEBUG, "No continuous profiler provider found, using NoOpContinuousProfiler", new Object[0]);
            return NoOpContinuousProfiler.getInstance();
        }
        catch (Throwable t) {
            logger.log(SentryLevel.ERROR, "Failed to load continuous profiler provider, using NoOpContinuousProfiler", t);
            return NoOpContinuousProfiler.getInstance();
        }
    }

    @NotNull
    public static IProfileConverter loadProfileConverter() {
        ILogger logger = ScopesAdapter.getInstance().getGlobalScope().getOptions().getLogger();
        try {
            JavaProfileConverterProvider provider = ProfilingServiceLoader.loadSingleProvider(JavaProfileConverterProvider.class);
            if (provider != null) {
                logger.log(SentryLevel.DEBUG, "Loaded profile converter from provider: %s", provider.getClass().getName());
                return provider.getProfileConverter();
            }
            logger.log(SentryLevel.DEBUG, "No profile converter provider found, using NoOpProfileConverter", new Object[0]);
            return NoOpProfileConverter.getInstance();
        }
        catch (Throwable t) {
            logger.log(SentryLevel.ERROR, "Failed to load profile converter provider, using NoOpProfileConverter", t);
            return NoOpProfileConverter.getInstance();
        }
    }

    @Nullable
    private static <T> T loadSingleProvider(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}

