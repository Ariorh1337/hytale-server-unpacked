/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.codecs.map;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.WrappedCodec;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public class MapCodec<V, M extends Map<String, V>>
implements Codec<Map<String, V>>,
WrappedCodec<V> {
    public static final MapCodec<String, Map<String, String>> STRING_HASH_MAP_CODEC = new MapCodec<String, Map>(Codec.STRING, Object2ObjectOpenHashMap::new);
    private final Codec<V> codec;
    private final Supplier<M> supplier;
    private final boolean unmodifiable;

    public MapCodec(Codec<V> codec, Supplier<M> supplier) {
        this(codec, supplier, true);
    }

    public MapCodec(Codec<V> codec, Supplier<M> supplier, boolean unmodifiable) {
        this.codec = codec;
        this.supplier = supplier;
        this.unmodifiable = unmodifiable;
    }

    @Override
    public Codec<V> getChildCodec() {
        return this.codec;
    }

    @Override
    public Map<String, V> decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
        BsonDocument bsonDocument = bsonValue.asDocument();
        if (bsonDocument.isEmpty()) {
            return this.unmodifiable ? Collections.emptyMap() : (Map)this.supplier.get();
        }
        Map<String, V> map = (Map<String, V>)this.supplier.get();
        for (Map.Entry<String, BsonValue> entry : bsonDocument.entrySet()) {
            String key = entry.getKey();
            BsonValue value = entry.getValue();
            extraInfo.pushKey(key);
            try {
                map.put(key, this.codec.decode(value, extraInfo));
            }
            catch (Exception e) {
                throw new CodecException("Failed to decode", value, extraInfo, (Throwable)e);
            }
            finally {
                extraInfo.popKey();
            }
        }
        if (this.unmodifiable) {
            map = Collections.unmodifiableMap(map);
        }
        return map;
    }

    @Override
    @Nonnull
    public BsonValue encode(@Nonnull Map<String, V> map, ExtraInfo extraInfo) {
        BsonDocument bsonDocument = new BsonDocument();
        for (Map.Entry<String, V> entry : map.entrySet()) {
            BsonValue value = this.codec.encode(entry.getValue(), extraInfo);
            if (value == null || value.isNull() || value.isDocument() && value.asDocument().isEmpty() || value.isArray() && value.asArray().isEmpty()) continue;
            bsonDocument.put(entry.getKey(), value);
        }
        return bsonDocument;
    }

    @Override
    public Map<String, V> decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.expect('{');
        reader.consumeWhiteSpace();
        if (reader.tryConsume('}')) {
            return this.unmodifiable ? Collections.emptyMap() : (Map)this.supplier.get();
        }
        Map<String, V> map = (Map<String, V>)this.supplier.get();
        while (true) {
            String key = reader.readString();
            reader.consumeWhiteSpace();
            reader.expect(':');
            reader.consumeWhiteSpace();
            extraInfo.pushKey(key, reader);
            try {
                map.put(key, this.codec.decodeJson(reader, extraInfo));
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
                    map = Collections.unmodifiableMap(map);
                }
                return map;
            }
            reader.consumeWhiteSpace();
        }
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        ObjectSchema schema = new ObjectSchema();
        schema.setTitle("Map");
        Schema childSchema = context.refDefinition(this.codec);
        schema.setAdditionalProperties(childSchema);
        return schema;
    }
}

