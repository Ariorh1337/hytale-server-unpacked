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
import io.sentry.rrweb.RRWebEvent;
import io.sentry.rrweb.RRWebEventType;
import io.sentry.util.Objects;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public abstract class RRWebIncrementalSnapshotEvent
extends RRWebEvent {
    private IncrementalSource source;

    public RRWebIncrementalSnapshotEvent(@NotNull IncrementalSource source) {
        super(RRWebEventType.IncrementalSnapshot);
        this.source = source;
    }

    public IncrementalSource getSource() {
        return this.source;
    }

    public void setSource(IncrementalSource source) {
        this.source = source;
    }

    public static enum IncrementalSource implements JsonSerializable
    {
        Mutation,
        MouseMove,
        MouseInteraction,
        Scroll,
        ViewportResize,
        Input,
        TouchMove,
        MediaInteraction,
        StyleSheetRule,
        CanvasMutation,
        Font,
        Log,
        Drag,
        StyleDeclaration,
        Selection,
        AdoptedStyleSheet,
        CustomElement;


        @Override
        public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
            writer.value(this.ordinal());
        }

        public static final class Deserializer
        implements JsonDeserializer<IncrementalSource> {
            @Override
            @NotNull
            public IncrementalSource deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
                return IncrementalSource.values()[reader.nextInt()];
            }
        }
    }

    public static final class Deserializer {
        public boolean deserializeValue(@NotNull RRWebIncrementalSnapshotEvent baseEvent, @NotNull String nextName, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            if (nextName.equals("source")) {
                baseEvent.source = Objects.requireNonNull(reader.nextOrNull(logger, new IncrementalSource.Deserializer()), "");
                return true;
            }
            return false;
        }
    }

    public static final class Serializer {
        public void serialize(@NotNull RRWebIncrementalSnapshotEvent baseEvent, @NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
            writer.name("source").value(logger, baseEvent.source);
        }
    }

    public static final class JsonKeys {
        public static final String SOURCE = "source";
    }
}

