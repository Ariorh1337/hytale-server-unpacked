/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs.array;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.RawJsonCodec;
import com.hypixel.hytale.codec.WrappedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.metadata.Metadata;
import com.hypixel.hytale.codec.util.RawJsonReader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonArray;
import org.bson.BsonNull;
import org.bson.BsonValue;

public class ArrayCodec<T>
implements Codec<T[]>,
RawJsonCodec<T[]>,
WrappedCodec<T> {
    private final Codec<T> codec;
    private final IntFunction<T[]> arrayConstructor;
    @Nullable
    private final Supplier<T> defaultValue;
    private List<Metadata> metadata;
    private T[] emptyArray;

    public ArrayCodec(Codec<T> codec, IntFunction<T[]> arrayConstructor) {
        this(codec, arrayConstructor, null);
    }

    public ArrayCodec(Codec<T> codec, IntFunction<T[]> arrayConstructor, @Nullable Supplier<T> defaultValue) {
        this.codec = codec;
        this.arrayConstructor = arrayConstructor;
        this.defaultValue = defaultValue;
    }

    @Override
    public Codec<T> getChildCodec() {
        return this.codec;
    }

    @Override
    public T[] decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
        BsonArray bsonArray = bsonValue.asArray();
        T[] array = this.arrayConstructor.apply(bsonArray.size());
        int size = bsonArray.size();
        for (int i = 0; i < size; ++i) {
            BsonValue value = bsonArray.get(i);
            extraInfo.pushIntKey(i);
            try {
                array[i] = this.decodeElement(value, extraInfo);
                continue;
            }
            catch (Exception e) {
                throw new CodecException("Failed to decode", value, extraInfo, (Throwable)e);
            }
            finally {
                extraInfo.popKey();
            }
        }
        return array;
    }

    @Override
    @Nonnull
    public BsonValue encode(@Nonnull T[] array, ExtraInfo extraInfo) {
        BsonArray bsonArray = new BsonArray();
        for (T t : array) {
            if (t == null) {
                bsonArray.add(new BsonNull());
                continue;
            }
            bsonArray.add(this.codec.encode(t, extraInfo));
        }
        return bsonArray;
    }

    @Override
    public T[] decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.expect('[');
        reader.consumeWhiteSpace();
        if (reader.tryConsume(']')) {
            if (this.emptyArray == null) {
                this.emptyArray = this.arrayConstructor.apply(0);
            }
            return this.emptyArray;
        }
        int i = 0;
        T[] arr = this.arrayConstructor.apply(10);
        while (true) {
            if (i == arr.length) {
                arr = Arrays.copyOf(arr, i + 1 + (i >> 1));
            }
            extraInfo.pushIntKey(i, reader);
            try {
                arr[i] = this.decodeJsonElement(reader, extraInfo);
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

    @Nonnull
    public ArrayCodec<T> metadata(Metadata metadata) {
        if (this.metadata == null) {
            this.metadata = new ObjectArrayList<Metadata>();
        }
        this.metadata.add(metadata);
        return this;
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        ArraySchema arraySchema = new ArraySchema();
        Schema childSchema = context.refDefinition(this.codec);
        if (this.metadata != null) {
            for (int i = 0; i < this.metadata.size(); ++i) {
                Metadata meta = this.metadata.get(i);
                meta.modify(childSchema);
            }
        }
        arraySchema.setItem(childSchema);
        return arraySchema;
    }

    @Nullable
    public Supplier<T> getDefaultSupplier() {
        return this.defaultValue;
    }

    @Nullable
    protected T decodeElement(@Nonnull BsonValue value, ExtraInfo extraInfo) {
        if (!value.isNull()) {
            return this.codec.decode(value, extraInfo);
        }
        return this.defaultValue == null ? null : (T)this.defaultValue.get();
    }

    @Nullable
    protected T decodeJsonElement(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        if (!reader.tryConsume("null")) {
            return this.codec.decodeJson(reader, extraInfo);
        }
        return this.defaultValue == null ? null : (T)this.defaultValue.get();
    }

    @Nonnull
    public static <T> ArrayCodec<T> ofBuilderCodec(@Nonnull BuilderCodec<T> codec, IntFunction<T[]> arrayConstructor) {
        return new ArrayCodec<T>(codec, arrayConstructor, codec.getSupplier());
    }
}

