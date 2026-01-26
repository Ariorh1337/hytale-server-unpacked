/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec;

import com.hypixel.hytale.codec.EmptyExtraInfo;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.RawJsonCodec;
import com.hypixel.hytale.codec.codecs.BsonDocumentCodec;
import com.hypixel.hytale.codec.codecs.UUIDBinaryCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.array.DoubleArrayCodec;
import com.hypixel.hytale.codec.codecs.array.FloatArrayCodec;
import com.hypixel.hytale.codec.codecs.array.IntArrayCodec;
import com.hypixel.hytale.codec.codecs.array.LongArrayCodec;
import com.hypixel.hytale.codec.codecs.simple.BooleanCodec;
import com.hypixel.hytale.codec.codecs.simple.ByteCodec;
import com.hypixel.hytale.codec.codecs.simple.DoubleCodec;
import com.hypixel.hytale.codec.codecs.simple.FloatCodec;
import com.hypixel.hytale.codec.codecs.simple.IntegerCodec;
import com.hypixel.hytale.codec.codecs.simple.LongCodec;
import com.hypixel.hytale.codec.codecs.simple.ShortCodec;
import com.hypixel.hytale.codec.codecs.simple.StringCodec;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.function.FunctionCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.SchemaConvertable;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonBinary;
import org.bson.BsonValue;

public interface Codec<T>
extends RawJsonCodec<T>,
SchemaConvertable<T> {
    @Deprecated
    public static final BsonDocumentCodec BSON_DOCUMENT = new BsonDocumentCodec();
    public static final StringCodec STRING = new StringCodec();
    public static final BooleanCodec BOOLEAN = new BooleanCodec();
    public static final DoubleCodec DOUBLE = new DoubleCodec();
    public static final FloatCodec FLOAT = new FloatCodec();
    public static final ByteCodec BYTE = new ByteCodec();
    public static final ShortCodec SHORT = new ShortCodec();
    public static final IntegerCodec INTEGER = new IntegerCodec();
    public static final LongCodec LONG = new LongCodec();
    public static final Pattern BASE64_PATTERN = Pattern.compile("^[0-9a-zA-Z+/]+$");
    @Deprecated
    public static final Codec<byte[]> BYTE_ARRAY = new Codec<byte[]>(){

        @Override
        public byte[] decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
            return bsonValue.asBinary().getData();
        }

        @Override
        @Nonnull
        public BsonValue encode(@Nonnull byte[] bytes, ExtraInfo extraInfo) {
            return new BsonBinary(bytes);
        }

        @Override
        @Nullable
        public byte[] decodeJson(RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
            reader.expect('[');
            reader.consumeWhiteSpace();
            if (reader.tryConsume(']')) {
                return new byte[0];
            }
            int i = 0;
            byte[] arr = new byte[10];
            while (true) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, i + 1 + (i >> 1));
                }
                extraInfo.pushIntKey(i, reader);
                try {
                    arr[i] = reader.readByteValue();
                    ++i;
                }
                catch (Exception e) {
                    throw new CodecException("Failed to decode", reader, extraInfo, (Throwable)e);
                }
                finally {
                    extraInfo.popKey();
                }
                reader.consumeWhiteSpace();
                if (reader.tryConsumeOrExpect(']', ',')) {
                    if (arr.length == i) {
                        return arr;
                    }
                    return Arrays.copyOf(arr, i);
                }
                reader.consumeWhiteSpace();
            }
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            StringSchema base64 = new StringSchema();
            base64.setPattern(BASE64_PATTERN);
            base64.setTitle("Binary");
            return base64;
        }
    };
    public static final DoubleArrayCodec DOUBLE_ARRAY = new DoubleArrayCodec();
    public static final FloatArrayCodec FLOAT_ARRAY = new FloatArrayCodec();
    public static final IntArrayCodec INT_ARRAY = new IntArrayCodec();
    public static final LongArrayCodec LONG_ARRAY = new LongArrayCodec();
    public static final ArrayCodec<String> STRING_ARRAY = new ArrayCodec<String>(STRING, String[]::new);
    public static final FunctionCodec<String, Path> PATH = new FunctionCodec<String, Path>(STRING, x$0 -> Paths.get(x$0, new String[0]), Path::toString);
    public static final FunctionCodec<String, Instant> INSTANT = new FunctionCodec<String, Instant>(STRING, Instant::parse, Instant::toString);
    public static final FunctionCodec<String, Duration> DURATION = new FunctionCodec<String, Duration>(STRING, Duration::parse, Duration::toString);
    public static final FunctionCodec<Double, Duration> DURATION_SECONDS = new FunctionCodec<Double, Duration>(DOUBLE, v -> Duration.ofNanos((long)(v * (double)TimeUnit.SECONDS.toNanos(1L))), v -> v == null ? null : Double.valueOf((double)v.toNanos() / (double)TimeUnit.SECONDS.toNanos(1L)));
    public static final FunctionCodec<String, Level> LOG_LEVEL = new FunctionCodec<String, Level>(STRING, Level::parse, Level::toString);
    public static final UUIDBinaryCodec UUID_BINARY = new UUIDBinaryCodec();
    public static final FunctionCodec<String, UUID> UUID_STRING = new FunctionCodec<String, UUID>(STRING, UUID::fromString, UUID::toString);

    @Nullable
    @Deprecated
    default public T decode(BsonValue bsonValue) {
        return this.decode(bsonValue, EmptyExtraInfo.EMPTY);
    }

    @Nullable
    public T decode(BsonValue var1, ExtraInfo var2);

    @Deprecated
    default public BsonValue encode(T t) {
        return this.encode(t, EmptyExtraInfo.EMPTY);
    }

    public BsonValue encode(T var1, ExtraInfo var2);

    @Override
    @Nullable
    default public T decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        System.err.println("decodeJson: " + String.valueOf(this.getClass()));
        BsonValue bsonValue = RawJsonReader.readBsonValue(reader);
        return this.decode(bsonValue, extraInfo);
    }

    public static boolean isNullBsonValue(@Nullable BsonValue bsonValue) {
        return bsonValue == null || bsonValue.isNull();
    }
}

