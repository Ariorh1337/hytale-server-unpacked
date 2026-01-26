/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs.simple;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.PrimitiveCodec;
import com.hypixel.hytale.codec.RawJsonCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.NumberSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDouble;
import org.bson.BsonValue;

public class FloatCodec
implements Codec<Float>,
RawJsonCodec<Float>,
PrimitiveCodec {
    public static final String STRING_SCHEMA_PATTERN = "^(-?Infinity|NaN)$";

    @Override
    @Nonnull
    public Float decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        return Float.valueOf(FloatCodec.decodeFloat(bsonValue));
    }

    @Override
    @Nonnull
    public BsonValue encode(Float t, ExtraInfo extraInfo) {
        return new BsonDouble(t.floatValue());
    }

    @Override
    @Nonnull
    public Float decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        return Float.valueOf(FloatCodec.readFloat(reader));
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        StringSchema stringSchema = new StringSchema();
        stringSchema.setPattern(STRING_SCHEMA_PATTERN);
        return Schema.anyOf(new NumberSchema(), stringSchema);
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context, @Nullable Float def) {
        StringSchema stringSchema = new StringSchema();
        stringSchema.setPattern(STRING_SCHEMA_PATTERN);
        NumberSchema numberSchema = new NumberSchema();
        if (def != null) {
            if (def.isNaN() || def.isInfinite()) {
                stringSchema.setDefault(def.toString());
            } else {
                numberSchema.setDefault(def.doubleValue());
            }
        }
        Schema schema = Schema.anyOf(numberSchema, stringSchema);
        schema.getHytale().setType("Number");
        return schema;
    }

    public static float decodeFloat(@Nonnull BsonValue value) {
        if (value.isString()) {
            switch (value.asString().getValue()) {
                case "NaN": {
                    return Float.NaN;
                }
                case "Infinity": {
                    return Float.POSITIVE_INFINITY;
                }
                case "-Infinity": {
                    return Float.NEGATIVE_INFINITY;
                }
            }
        }
        return (float)value.asNumber().doubleValue();
    }

    public static float readFloat(@Nonnull RawJsonReader reader) throws IOException {
        if (reader.peekFor('\"')) {
            String str;
            return switch (str = reader.readString()) {
                case "NaN" -> Float.NaN;
                case "Infinity" -> Float.POSITIVE_INFINITY;
                case "-Infinity" -> Float.NEGATIVE_INFINITY;
                default -> throw new IOException("Unexpected string: \"" + str + "\", expected NaN, Infinity, -Infinity");
            };
        }
        return (float)reader.readDoubleValue();
    }
}

