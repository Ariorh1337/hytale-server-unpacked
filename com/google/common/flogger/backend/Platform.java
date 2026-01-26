/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.backend;

import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.LogSite;
import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.backend.NoOpContextDataProvider;
import com.google.common.flogger.backend.PlatformProvider;
import com.google.common.flogger.context.ContextDataProvider;
import com.google.common.flogger.context.Tags;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class Platform {
    private static String DEFAULT_PLATFORM = "com.google.common.flogger.backend.system.DefaultPlatform";
    private static final String[] AVAILABLE_PLATFORMS = new String[]{DEFAULT_PLATFORM};

    public static LogCallerFinder getCallerFinder() {
        return LazyHolder.INSTANCE.getCallerFinderImpl();
    }

    protected abstract LogCallerFinder getCallerFinderImpl();

    public static LoggerBackend getBackend(String className) {
        return LazyHolder.INSTANCE.getBackendImpl(className);
    }

    protected abstract LoggerBackend getBackendImpl(String var1);

    public static ContextDataProvider getContextDataProvider() {
        return LazyHolder.INSTANCE.getContextDataProviderImpl();
    }

    protected ContextDataProvider getContextDataProviderImpl() {
        return NoOpContextDataProvider.getInstance();
    }

    public static boolean shouldForceLogging(String loggerName, Level level, boolean isEnabled) {
        return Platform.getContextDataProvider().shouldForceLogging(loggerName, level, isEnabled);
    }

    public static Tags getInjectedTags() {
        return Platform.getContextDataProvider().getTags();
    }

    public static Metadata getInjectedMetadata() {
        return Platform.getContextDataProvider().getMetadata();
    }

    public static long getCurrentTimeNanos() {
        return LazyHolder.INSTANCE.getCurrentTimeNanosImpl();
    }

    protected long getCurrentTimeNanosImpl() {
        return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis());
    }

    public static String getConfigInfo() {
        return LazyHolder.INSTANCE.getConfigInfoImpl();
    }

    protected abstract String getConfigInfoImpl();

    static /* synthetic */ String[] access$000() {
        return AVAILABLE_PLATFORMS;
    }

    public static abstract class LogCallerFinder {
        public abstract String findLoggingClass(Class<? extends AbstractLogger<?>> var1);

        public abstract LogSite findLogSite(Class<?> var1, int var2);
    }

    private static final class LazyHolder {
        private static final Platform INSTANCE = LazyHolder.loadFirstAvailablePlatform(Platform.access$000());

        private LazyHolder() {
        }

        private static Platform loadFirstAvailablePlatform(String[] platformClass) {
            Platform platform = null;
            try {
                platform = PlatformProvider.getPlatform();
            }
            catch (NoClassDefFoundError noClassDefFoundError) {
                // empty catch block
            }
            if (platform != null) {
                return platform;
            }
            StringBuilder errorMessage = new StringBuilder();
            for (String clazz : platformClass) {
                try {
                    return (Platform)Class.forName(clazz).getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Throwable e) {
                    if (e instanceof InvocationTargetException) {
                        e = e.getCause();
                    }
                    errorMessage.append('\n').append(clazz).append(": ").append(e);
                }
            }
            throw new IllegalStateException(errorMessage.insert(0, "No logging platforms found:").toString());
        }
    }
}

