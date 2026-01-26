/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DefaultScopesStorage;
import io.sentry.ILogger;
import io.sentry.IScopesStorage;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class ScopesStorageFactory {
    private static final String OTEL_SCOPES_STORAGE = "io.sentry.opentelemetry.OtelContextScopesStorage";

    @NotNull
    public static IScopesStorage create(@NotNull LoadClass loadClass, @NotNull ILogger logger) {
        @NotNull IScopesStorage storage = ScopesStorageFactory.createInternal(loadClass, logger);
        storage.init();
        return storage;
    }

    @NotNull
    private static IScopesStorage createInternal(@NotNull LoadClass loadClass, @NotNull ILogger logger) {
        Class<?> otelScopesStorageClazz;
        if (Platform.isJvm() && loadClass.isClassAvailable(OTEL_SCOPES_STORAGE, logger) && (otelScopesStorageClazz = loadClass.loadClass(OTEL_SCOPES_STORAGE, logger)) != null) {
            try {
                @Nullable ? otelScopesStorage = otelScopesStorageClazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                if (otelScopesStorage != null && otelScopesStorage instanceof IScopesStorage) {
                    return (IScopesStorage)otelScopesStorage;
                }
            }
            catch (InstantiationException instantiationException) {
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        return new DefaultScopesStorage();
    }
}

