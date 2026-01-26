/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public enum RRWebEventType implements JsonSerializable
{
    DomContentLoaded,
    Load,
    FullSnapshot,
    IncrementalSnapshot,
    Meta,
    Custom,
    Plugin;


    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.value(this.ordinal());
    }

    public static final class Deserializer
    implements JsonDeserializer<RRWebEventType> {
        @Override
        @NotNull
        public RRWebEventType deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return RRWebEventType.values()[reader.nextInt()];
        }
    }
}

