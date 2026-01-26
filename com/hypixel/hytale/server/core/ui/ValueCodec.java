/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.PatchStyle;
import com.hypixel.hytale.server.core.ui.Value;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;

public class ValueCodec<T>
implements Codec<Value<T>> {
    public static final ValueCodec<Object> REFERENCE_ONLY = new ValueCodec(null);
    public static final ValueCodec<String> STRING = new ValueCodec<String>(Codec.STRING);
    public static final ValueCodec<LocalizableString> LOCALIZABLE_STRING = new ValueCodec<LocalizableString>(LocalizableString.CODEC);
    public static final ValueCodec<Integer> INTEGER = new ValueCodec<Integer>(Codec.INTEGER);
    public static final ValueCodec<PatchStyle> PATCH_STYLE = new ValueCodec<PatchStyle>(PatchStyle.CODEC);
    protected Codec<T> codec;

    ValueCodec(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public Value<T> decode(BsonValue bsonValue, ExtraInfo extraInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BsonValue encode(@Nonnull Value<T> r, ExtraInfo extraInfo) {
        if (r.getValue() != null) {
            return this.codec.encode(r.getValue(), extraInfo);
        }
        return new BsonDocument().append("$Document", new BsonString(r.getDocumentPath())).append("@Value", new BsonString(r.getValueName()));
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        return this.codec.toSchema(context);
    }
}

