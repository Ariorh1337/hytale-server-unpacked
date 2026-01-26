/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.bson.BsonInt32;
import org.bson.BsonValue;

@Deprecated
public class StringIntegerCodec
implements Codec<Integer> {
    public static final StringIntegerCodec INSTANCE = new StringIntegerCodec();
    private static final Pattern INTEGER_PATTERN = Pattern.compile("^[0-9]+$");

    @Override
    @Nonnull
    public Integer decode(@Nonnull BsonValue bsonValue, ExtraInfo extraInfo) {
        if (bsonValue.isString()) {
            return Integer.parseInt(bsonValue.asString().getValue());
        }
        return bsonValue.asNumber().intValue();
    }

    @Override
    @Nonnull
    public BsonValue encode(Integer t, ExtraInfo extraInfo) {
        return new BsonInt32(t);
    }

    @Override
    @Nonnull
    public Integer decodeJson(@Nonnull RawJsonReader reader, ExtraInfo extraInfo) throws IOException {
        int read = reader.peek();
        if (read == -1) {
            throw new IOException("Unexpected EOF!");
        }
        if (read == 34) {
            return Integer.parseInt(reader.readString());
        }
        return reader.readIntValue();
    }

    @Override
    @Nonnull
    public StringSchema toSchema(@Nonnull SchemaContext context) {
        StringSchema s = new StringSchema();
        s.setPattern(INTEGER_PATTERN);
        s.setMarkdownDescription("A string that contains any integer");
        return s;
    }
}

