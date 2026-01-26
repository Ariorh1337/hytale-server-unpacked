/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum SentryLevel implements JsonSerializable
{
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    FATAL;


    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.value(this.name().toLowerCase(Locale.ROOT));
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryLevel> {
        @Override
        @NotNull
        public SentryLevel deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return SentryLevel.valueOf(reader.nextString().toUpperCase(Locale.ROOT));
        }
    }
}

