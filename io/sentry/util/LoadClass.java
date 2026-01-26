/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.LazyEvaluator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoadClass {
    @Nullable
    public Class<?> loadClass(@NotNull String clazz, @Nullable ILogger logger) {
        block6: {
            try {
                return Class.forName(clazz);
            }
            catch (ClassNotFoundException e) {
                if (logger != null) {
                    logger.log(SentryLevel.INFO, "Class not available: " + clazz, new Object[0]);
                }
            }
            catch (UnsatisfiedLinkError e) {
                if (logger != null) {
                    logger.log(SentryLevel.ERROR, "Failed to load (UnsatisfiedLinkError) " + clazz, e);
                }
            }
            catch (Throwable e) {
                if (logger == null) break block6;
                logger.log(SentryLevel.ERROR, "Failed to initialize " + clazz, e);
            }
        }
        return null;
    }

    public boolean isClassAvailable(@NotNull String clazz, @Nullable ILogger logger) {
        return this.loadClass(clazz, logger) != null;
    }

    public boolean isClassAvailable(@NotNull String clazz, @Nullable SentryOptions options) {
        return this.isClassAvailable(clazz, options != null ? options.getLogger() : null);
    }

    public LazyEvaluator<Boolean> isClassAvailableLazy(@NotNull String clazz, @Nullable ILogger logger) {
        return new LazyEvaluator<Boolean>(() -> this.isClassAvailable(clazz, logger));
    }

    public LazyEvaluator<Boolean> isClassAvailableLazy(@NotNull String clazz, @Nullable SentryOptions options) {
        return new LazyEvaluator<Boolean>(() -> this.isClassAvailable(clazz, options));
    }
}

