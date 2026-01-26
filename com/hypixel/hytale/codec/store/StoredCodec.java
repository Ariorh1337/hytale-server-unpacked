/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.store;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.store.CodecKey;
import com.hypixel.hytale.codec.store.CodecStore;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonValue;

public class StoredCodec<T>
implements Codec<T> {
    private final CodecKey<T> key;

    public StoredCodec(CodecKey<T> key) {
        this.key = key;
    }

    @Override
    public T decode(BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
        Codec<T> codec = extraInfo.getCodecStore().getCodec(this.key);
        if (codec == null) {
            throw new IllegalArgumentException("Failed to find codec for " + String.valueOf(this.key));
        }
        return codec.decode(bsonValue, extraInfo);
    }

    @Override
    public BsonValue encode(T t, @Nonnull ExtraInfo extraInfo) {
        Codec<T> codec = extraInfo.getCodecStore().getCodec(this.key);
        if (codec == null) {
            throw new IllegalArgumentException("Failed to find codec for " + String.valueOf(this.key));
        }
        return codec.encode(t, extraInfo);
    }

    @Override
    @Nullable
    public T decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        Codec<T> codec = extraInfo.getCodecStore().getCodec(this.key);
        if (codec == null) {
            throw new IllegalArgumentException("Failed to find codec for " + String.valueOf(this.key));
        }
        return codec.decodeJson(reader, extraInfo);
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        Codec<T> codec = CodecStore.STATIC.getCodec(this.key);
        if (codec == null) {
            throw new IllegalArgumentException("Failed to find codec for " + String.valueOf(this.key));
        }
        return context.refDefinition(codec);
    }
}

