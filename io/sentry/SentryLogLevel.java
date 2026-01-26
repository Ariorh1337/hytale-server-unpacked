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

public enum SentryLogLevel implements JsonSerializable
{
    TRACE(1),
    DEBUG(5),
    INFO(9),
    WARN(13),
    ERROR(17),
    FATAL(21);

    private final int severityNumber;

    private SentryLogLevel(int severityNumber) {
        this.severityNumber = severityNumber;
    }

    public int getSeverityNumber() {
        return this.severityNumber;
    }

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.value(this.name().toLowerCase(Locale.ROOT));
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryLogLevel> {
        @Override
        @NotNull
        public SentryLogLevel deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return SentryLogLevel.valueOf(reader.nextString().toUpperCase(Locale.ROOT));
        }
    }
}

