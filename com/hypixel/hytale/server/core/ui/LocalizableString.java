/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bson.BsonString;
import org.bson.BsonValue;

public class LocalizableString {
    public static final LocalizableStringCodec CODEC = new LocalizableStringCodec();
    public static final BuilderCodec<LocalizableString> MESSAGE_OBJECT_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LocalizableString.class, LocalizableString::new).addField(new KeyedCodec<String>("MessageId", Codec.STRING), (p, t) -> {
        p.messageId = t;
    }, p -> p.messageId)).addField(new KeyedCodec("MessageParams", MapCodec.STRING_HASH_MAP_CODEC), (p, t) -> {
        p.messageParams = t;
    }, p -> p.messageParams)).build();
    private String stringValue;
    private String messageId;
    private Map<String, String> messageParams;

    @Nonnull
    public static LocalizableString fromString(String str) {
        LocalizableString instance = new LocalizableString();
        instance.stringValue = str;
        return instance;
    }

    @Nonnull
    public static LocalizableString fromMessageId(String messageId) {
        return LocalizableString.fromMessageId(messageId, null);
    }

    @Nonnull
    public static LocalizableString fromMessageId(String messageId, Map<String, String> params) {
        LocalizableString instance = new LocalizableString();
        instance.messageId = messageId;
        instance.messageParams = params;
        return instance;
    }

    public static class LocalizableStringCodec
    implements Codec<LocalizableString> {
        @Override
        public LocalizableString decode(BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
            if (bsonValue instanceof BsonString) {
                return LocalizableString.fromString(bsonValue.asString().getValue());
            }
            return MESSAGE_OBJECT_CODEC.decode(bsonValue, extraInfo);
        }

        @Override
        @Nonnull
        public BsonValue encode(@Nonnull LocalizableString t, @Nonnull ExtraInfo extraInfo) {
            if (t.stringValue != null) {
                return new BsonString(t.stringValue);
            }
            return MESSAGE_OBJECT_CODEC.encode((Object)t, extraInfo);
        }

        @Override
        @Nonnull
        public Schema toSchema(@Nonnull SchemaContext context) {
            return Schema.anyOf(new StringSchema(), MESSAGE_OBJECT_CODEC.toSchema(context));
        }
    }
}

