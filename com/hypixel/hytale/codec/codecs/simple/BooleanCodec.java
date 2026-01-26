/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs.simple;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.PrimitiveCodec;
import com.hypixel.hytale.codec.RawJsonCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.BooleanSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonBoolean;
import org.bson.BsonValue;

public class BooleanCodec
implements Codec<Boolean>,
RawJsonCodec<Boolean>,
PrimitiveCodec {
    @Override
    @Nonnull
    public Boolean decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        return bsonValue.asBoolean().getValue();
    }

    @Override
    @Nonnull
    public BsonValue encode(Boolean t, ExtraInfo extraInfo) {
        return new BsonBoolean(t);
    }

    @Override
    @Nonnull
    public Boolean decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        return reader.readBooleanValue();
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        return new BooleanSchema();
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context, @Nullable Boolean def) {
        BooleanSchema s = new BooleanSchema();
        if (def != null) {
            s.setDefault(def);
        }
        return s;
    }
}

