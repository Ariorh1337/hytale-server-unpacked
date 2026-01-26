/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs.map;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.WrappedCodec;
import com.hypixel.hytale.codec.codecs.StringIntegerCodec;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.schema.config.StringSchema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMaps;
import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class Short2ObjectMapCodec<T>
implements Codec<Short2ObjectMap<T>>,
WrappedCodec<T> {
    private final Codec<T> valueCodec;
    private final Supplier<Short2ObjectMap<T>> supplier;
    private final boolean unmodifiable;

    public Short2ObjectMapCodec(Codec<T> valueCodec, Supplier<Short2ObjectMap<T>> supplier, boolean unmodifiable) {
        this.valueCodec = valueCodec;
        this.supplier = supplier;
        this.unmodifiable = unmodifiable;
    }

    public Short2ObjectMapCodec(Codec<T> valueCodec, Supplier<Short2ObjectMap<T>> supplier) {
        this(valueCodec, supplier, true);
    }

    @Override
    public Codec<T> getChildCodec() {
        return this.valueCodec;
    }

    @Override
    public Short2ObjectMap<T> decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
        BsonDocument bsonDocument = bsonValue.asDocument();
        Short2ObjectMap<T> map = this.supplier.get();
        for (Map.Entry<String, BsonValue> entry : bsonDocument.entrySet()) {
            String key = entry.getKey();
            BsonValue value = entry.getValue();
            extraInfo.pushKey(key);
            try {
                short decodedKey = Short.valueOf(key);
                map.put(decodedKey, this.valueCodec.decode(value, extraInfo));
            }
            catch (Exception e) {
                throw new CodecException("Failed to decode", value, extraInfo, (Throwable)e);
            }
            finally {
                extraInfo.popKey();
            }
        }
        if (this.unmodifiable) {
            map = Short2ObjectMaps.unmodifiable(map);
        }
        return map;
    }

    @Override
    @Nonnull
    public BsonValue encode(@Nonnull Short2ObjectMap<T> map, ExtraInfo extraInfo) {
        BsonDocument bsonDocument = new BsonDocument();
        for (Short2ObjectMap.Entry entry : map.short2ObjectEntrySet()) {
            bsonDocument.put(Short.toString(entry.getShortKey()), this.valueCodec.encode(entry.getValue(), extraInfo));
        }
        return bsonDocument;
    }

    @Override
    public Short2ObjectMap<T> decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.expect('{');
        reader.consumeWhiteSpace();
        Short2ObjectMap<T> map = this.supplier.get();
        if (reader.tryConsume('}')) {
            if (this.unmodifiable) {
                map = Short2ObjectMaps.unmodifiable(map);
            }
            return map;
        }
        while (true) {
            String key = reader.readString();
            reader.consumeWhiteSpace();
            reader.expect(':');
            reader.consumeWhiteSpace();
            extraInfo.pushKey(key, reader);
            try {
                short decodedKey = Short.valueOf(key);
                map.put(decodedKey, this.valueCodec.decodeJson(reader, extraInfo));
            }
            catch (Exception e) {
                throw new CodecException("Failed to decode", reader, extraInfo, (Throwable)e);
            }
            finally {
                extraInfo.popKey();
            }
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
                if (this.unmodifiable) {
                    map = Short2ObjectMaps.unmodifiable(map);
                }
                return map;
            }
            reader.consumeWhiteSpace();
        }
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        ObjectSchema s = new ObjectSchema();
        StringSchema name = StringIntegerCodec.INSTANCE.toSchema(context);
        s.setPropertyNames(name);
        s.setAdditionalProperties(context.refDefinition(this.valueCodec));
        return s;
    }
}

