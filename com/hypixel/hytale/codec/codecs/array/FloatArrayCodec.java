/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs.array;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.RawJsonCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ArraySchema;
import com.hypixel.hytale.codec.schema.config.NumberSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import java.util.Arrays;
import javax.annotation.Nonnull;
import org.bson.BsonArray;
import org.bson.BsonDouble;
import org.bson.BsonValue;

public class FloatArrayCodec
implements Codec<float[]>,
RawJsonCodec<float[]> {
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];

    @Override
    public float[] decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        BsonArray array = bsonValue.asArray();
        float[] floats = new float[array.size()];
        for (int i = 0; i < floats.length; ++i) {
            floats[i] = (float)array.get(i).asNumber().doubleValue();
        }
        return floats;
    }

    @Override
    @Nonnull
    public BsonValue encode(@Nonnull float[] floats, ExtraInfo extraInfo) {
        BsonArray array = new BsonArray();
        for (int i = 0; i < floats.length; ++i) {
            array.add(new BsonDouble(floats[i]));
        }
        return array;
    }

    @Override
    public float[] decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        reader.expect('[');
        reader.consumeWhiteSpace();
        if (reader.tryConsume(']')) {
            return EMPTY_FLOAT_ARRAY;
        }
        int i = 0;
        float[] arr = new float[10];
        while (true) {
            if (i == arr.length) {
                float[] temp = new float[i + 1 + (i >> 1)];
                System.arraycopy(arr, 0, temp, 0, i);
                arr = temp;
            }
            arr[i++] = reader.readFloatValue();
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
        ArraySchema s = new ArraySchema();
        s.setItem(new NumberSchema());
        return s;
    }
}

