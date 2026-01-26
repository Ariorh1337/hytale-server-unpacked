/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DefaultSpanFactory;
import io.sentry.ILogger;
import io.sentry.ISpanFactory;
import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SpanFactoryFactory {
    private static final String OTEL_SPAN_FACTORY = "io.sentry.opentelemetry.OtelSpanFactory";

    @NotNull
    public static ISpanFactory create(@NotNull LoadClass loadClass, @NotNull ILogger logger) {
        Class<?> otelSpanFactoryClazz;
        if (Platform.isJvm() && loadClass.isClassAvailable(OTEL_SPAN_FACTORY, logger) && (otelSpanFactoryClazz = loadClass.loadClass(OTEL_SPAN_FACTORY, logger)) != null) {
            try {
                @Nullable ? otelSpanFactory = otelSpanFactoryClazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                if (otelSpanFactory != null && otelSpanFactory instanceof ISpanFactory) {
                    return (ISpanFactory)otelSpanFactory;
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
        return new DefaultSpanFactory();
    }
}

