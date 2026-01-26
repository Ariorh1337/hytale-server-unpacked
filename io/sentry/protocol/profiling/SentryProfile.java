/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.protocol.profiling;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.profiling.SentrySample;
import io.sentry.protocol.profiling.SentryThreadMetadata;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class SentryProfile
implements JsonUnknown,
JsonSerializable {
    @NotNull
    private List<SentrySample> samples = new ArrayList<SentrySample>();
    @NotNull
    private List<List<Integer>> stacks = new ArrayList<List<Integer>>();
    @NotNull
    private List<SentryStackFrame> frames = new ArrayList<SentryStackFrame>();
    @NotNull
    private Map<String, SentryThreadMetadata> threadMetadata = new HashMap<String, SentryThreadMetadata>();
    @Nullable
    private Map<String, Object> unknown;

    @Override
    public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
        writer.beginObject();
        writer.name("samples").value(logger, this.samples);
        writer.name("stacks").value(logger, this.stacks);
        writer.name("frames").value(logger, this.frames);
        writer.name("thread_metadata").value(logger, this.threadMetadata);
        if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
                Object value = this.unknown.get(key);
                writer.name(key).value(logger, value);
            }
        }
        writer.endObject();
    }

    @NotNull
    public List<SentrySample> getSamples() {
        return this.samples;
    }

    public void setSamples(@NotNull List<SentrySample> samples) {
        this.samples = samples;
    }

    @NotNull
    public List<List<Integer>> getStacks() {
        return this.stacks;
    }

    public void setStacks(@NotNull List<List<Integer>> stacks) {
        this.stacks = stacks;
    }

    @NotNull
    public List<SentryStackFrame> getFrames() {
        return this.frames;
    }

    public void setFrames(@NotNull List<SentryStackFrame> frames) {
        this.frames = frames;
    }

    @NotNull
    public Map<String, SentryThreadMetadata> getThreadMetadata() {
        return this.threadMetadata;
    }

    public void setThreadMetadata(@NotNull Map<String, SentryThreadMetadata> threadMetadata) {
        this.threadMetadata = threadMetadata;
    }

    @Override
    @Nullable
    public Map<String, Object> getUnknown() {
        return this.unknown;
    }

    @Override
    public void setUnknown(@Nullable Map<String, Object> unknown) {
        this.unknown = unknown;
    }

    public static final class JsonKeys {
        public static final String SAMPLES = "samples";
        public static final String STACKS = "stacks";
        public static final String FRAMES = "frames";
        public static final String THREAD_METADATA = "thread_metadata";
    }

    private static final class NestedIntegerListDeserializer
    implements JsonDeserializer<List<List<Integer>>> {
        private NestedIntegerListDeserializer() {
        }

        @Override
        @NotNull
        public List<List<Integer>> deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            ArrayList<List<Integer>> result = new ArrayList<List<Integer>>();
            reader.beginArray();
            while (reader.hasNext()) {
                ArrayList<Integer> innerList = new ArrayList<Integer>();
                reader.beginArray();
                while (reader.hasNext()) {
                    innerList.add(reader.nextInt());
                }
                reader.endArray();
                result.add(innerList);
            }
            reader.endArray();
            return result;
        }
    }

    public static final class Deserializer
    implements JsonDeserializer<SentryProfile> {
        @Override
        @NotNull
        public SentryProfile deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            SentryProfile data = new SentryProfile();
            ConcurrentHashMap<String, Object> unknown = null;
            block12: while (reader.peek() == JsonToken.NAME) {
                String nextName;
                switch (nextName = reader.nextName()) {
                    case "frames": {
                        List<SentryStackFrame> jfrFrame = reader.nextListOrNull(logger, new SentryStackFrame.Deserializer());
                        if (jfrFrame == null) continue block12;
                        data.frames = jfrFrame;
                        continue block12;
                    }
                    case "samples": {
                        List<SentrySample> sentrySamples = reader.nextListOrNull(logger, new SentrySample.Deserializer());
                        if (sentrySamples == null) continue block12;
                        data.samples = sentrySamples;
                        continue block12;
                    }
                    case "thread_metadata": {
                        Map<String, SentryThreadMetadata> threadMetadata = reader.nextMapOrNull(logger, new SentryThreadMetadata.Deserializer());
                        if (threadMetadata == null) continue block12;
                        data.threadMetadata = threadMetadata;
                        continue block12;
                    }
                    case "stacks": {
                        List<List<Integer>> jfrStacks = reader.nextOrNull(logger, new NestedIntegerListDeserializer());
                        if (jfrStacks == null) continue block12;
                        data.stacks = jfrStacks;
                        continue block12;
                    }
                }
                if (unknown == null) {
                    unknown = new ConcurrentHashMap<String, Object>();
                }
                reader.nextUnknown(logger, unknown, nextName);
            }
            data.setUnknown(unknown);
            reader.endObject();
            return data;
        }
    }
}

