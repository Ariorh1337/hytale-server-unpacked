/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.metrics;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.backend.HytaleFileHandler;
import com.hypixel.hytale.metrics.MetricProvider;
import com.hypixel.hytale.metrics.MetricResults;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

public class MetricsRegistry<T>
implements Codec<T> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static final JsonWriterSettings JSON_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.STRICT).indent(false).newLineCharacters("\n").int64Converter((value, writer) -> writer.writeNumber(Long.toString(value))).build();
    private static final EncoderContext ENCODER_CONTEXT = EncoderContext.builder().build();
    private static final BsonDocumentCodec BSON_DOCUMENT_CODEC = new BsonDocumentCodec();
    @Nullable
    private final Function<T, MetricProvider> appendFunc;
    private final StampedLock lock = new StampedLock();
    private final Map<String, Metric<T, ?>> map = new Object2ObjectLinkedOpenHashMap();

    public MetricsRegistry() {
        this.appendFunc = null;
    }

    public MetricsRegistry(Function<T, MetricProvider> appendFunc) {
        this.appendFunc = appendFunc;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MetricsRegistry<T> register(String id, MetricsRegistry<Void> metricsRegistry) {
        long stamp = this.lock.writeLock();
        try {
            if (this.map.putIfAbsent(id, new Metric(null, metricsRegistry)) != null) {
                throw new IllegalArgumentException("Metric already registered: " + id);
            }
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <R> MetricsRegistry<T> register(String id, Function<T, R> func, Codec<R> codec) {
        long stamp = this.lock.writeLock();
        try {
            if (this.map.putIfAbsent(id, new Metric<T, R>(func, codec)) != null) {
                throw new IllegalArgumentException("Metric already registered: " + id);
            }
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
        return this;
    }

    public <R extends MetricProvider> MetricsRegistry<T> register(String id, @Nonnull Function<T, R> func) {
        return this.register(id, func.andThen(r -> r == null ? null : r.toMetricResults()), MetricResults.CODEC);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public <R> MetricsRegistry<T> register(String id, Function<T, R> func, Function<R, MetricsRegistry<R>> codecFunc) {
        long stamp = this.lock.writeLock();
        try {
            if (this.map.putIfAbsent(id, new Metric<T, R>(func, codecFunc)) != null) {
                throw new IllegalArgumentException("Metric already registered: " + id);
            }
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
        return this;
    }

    @Override
    public T decode(BsonValue bsonValue, ExtraInfo extraInfo) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BsonValue encode(T t, ExtraInfo extraInfo) {
        MetricResults metricResults;
        MetricProvider metricProvider;
        BsonDocument document = new BsonDocument();
        long stamp = this.lock.readLock();
        try {
            for (Map.Entry<String, Metric<T, ?>> entry : this.map.entrySet()) {
                String key = entry.getKey();
                BsonValue value = entry.getValue().encode(t, extraInfo);
                if (value == null) continue;
                document.put(key, value);
            }
        }
        finally {
            this.lock.unlockRead(stamp);
        }
        if (this.appendFunc != null && (metricProvider = this.appendFunc.apply(t)) != null && (metricResults = metricProvider.toMetricResults()) != null) {
            document.putAll(metricResults.getBson());
        }
        return document;
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nonnull
    public MetricResults toMetricResults(T t) {
        return new MetricResults(this.dumpToBson(t).asDocument());
    }

    public BsonValue dumpToBson(T t) {
        ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
        BsonDocument bson = this.encode(t, extraInfo).asDocument();
        extraInfo.getValidationResults().logOrThrowValidatorExceptions(LOGGER);
        return bson;
    }

    @Nonnull
    public Path dumpToJson(T t) throws IOException {
        Path path = MetricsRegistry.createDumpPath(".dump.json");
        this.dumpToJson(path, t);
        return path;
    }

    public void dumpToJson(@Nonnull Path path, T t) throws IOException {
        BsonValue bson = this.dumpToBson(t);
        try (BufferedWriter writer = Files.newBufferedWriter(path, new OpenOption[0]);){
            BSON_DOCUMENT_CODEC.encode((BsonWriter)new JsonWriter(writer, JSON_SETTINGS), bson.asDocument(), ENCODER_CONTEXT);
        }
    }

    @Nonnull
    public static Path createDumpPath(@Nullable String ext) throws IOException {
        return MetricsRegistry.createDumpPath((String)null, ext);
    }

    @Nonnull
    public static Path createDumpPath(@Nonnull Path dir, @Nullable String ext) {
        return MetricsRegistry.createDatePath(dir, null, ext);
    }

    @Nonnull
    public static Path createDumpPath(@Nullable String prefix, @Nullable String ext) throws IOException {
        Path path = Paths.get("dumps", new String[0]);
        if (!Files.exists(path, new LinkOption[0])) {
            Files.createDirectories(path, new FileAttribute[0]);
        }
        return MetricsRegistry.createDatePath(path, prefix, ext);
    }

    @Nonnull
    public static Path createDatePath(@Nonnull Path dir, @Nullable String prefix, @Nullable String suffix) {
        Object name = HytaleFileHandler.LOG_FILE_DATE_FORMAT.format(LocalDateTime.now());
        if (prefix != null) {
            name = prefix + (String)name;
        }
        Path file = suffix != null ? dir.resolve((String)name + suffix) : dir.resolve((String)name);
        int i = 0;
        while (Files.exists(file, new LinkOption[0])) {
            if (suffix != null) {
                file = dir.resolve((String)name + "_" + i++ + suffix);
                continue;
            }
            file = dir.resolve((String)name + "_" + i++);
        }
        return file;
    }

    private static class Metric<T, R> {
        @Nullable
        private final Function<T, R> func;
        @CheckForNull
        private final Codec<R> codec;
        @CheckForNull
        private final Function<R, MetricsRegistry<R>> codecFunc;

        public Metric(@Nullable Function<T, R> func, @Nullable Codec<R> codec) {
            this.func = func;
            this.codec = codec;
            this.codecFunc = null;
        }

        public Metric(@Nullable Function<T, R> func, @Nullable Function<R, MetricsRegistry<R>> codecFunc) {
            this.func = func;
            this.codec = null;
            this.codecFunc = codecFunc;
        }

        @Nullable
        public BsonValue encode(T t, ExtraInfo extraInfo) {
            if (this.func == null) {
                assert (this.codec != null);
                return this.codec.encode(null, extraInfo);
            }
            R value = this.func.apply(t);
            return value == null ? null : this.getCodec(value).encode(value, extraInfo);
        }

        @Nonnull
        public Codec<R> getCodec(R value) {
            if (this.codec != null) {
                return this.codec;
            }
            assert (this.codecFunc != null);
            return this.codecFunc.apply(value);
        }
    }
}

