/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.lookup;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.exception.CodecException;
import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.ObjectSchema;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.codec.validation.ValidatableCodec;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.bson.BsonDocument;
import org.bson.BsonValue;

public abstract class AMapProvidedMapCodec<K, V, P, M extends Map<K, V>>
implements Codec<M>,
ValidatableCodec<M> {
    protected final Map<K, P> codecProvider;
    protected final Function<P, Codec<V>> mapper;
    protected final boolean unmodifiable;

    public AMapProvidedMapCodec(Map<K, P> codecProvider, Function<P, Codec<V>> mapper) {
        this(codecProvider, mapper, true);
    }

    public AMapProvidedMapCodec(Map<K, P> codecProvider, Function<P, Codec<V>> mapper, boolean unmodifiable) {
        this.codecProvider = codecProvider;
        this.mapper = mapper;
        this.unmodifiable = unmodifiable;
    }

    public abstract M createMap();

    public void handleUnknown(M map, @Nonnull String key, BsonValue value, @Nonnull ExtraInfo extraInfo) {
        extraInfo.addUnknownKey(key);
    }

    public void handleUnknown(M map, @Nonnull String key, @Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        extraInfo.addUnknownKey(key);
        reader.skipValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public M decode(@Nonnull BsonValue bsonValue, @Nonnull ExtraInfo extraInfo) {
        BsonDocument bsonDocument = bsonValue.asDocument();
        M map = this.createMap();
        for (Map.Entry<String, BsonValue> entry : bsonDocument.entrySet()) {
            extraInfo.pushKey(entry.getKey());
            try {
                K key = this.getKeyForId(entry.getKey());
                if (key == null) {
                    this.handleUnknown(map, entry.getKey(), entry.getValue(), extraInfo);
                    continue;
                }
                Codec<V> codecFor = this.getCodecFor(key);
                map.put(key, codecFor.decode(entry.getValue(), extraInfo));
            }
            finally {
                extraInfo.popKey();
            }
        }
        if (this.unmodifiable) {
            map = this.unmodifiableMap(map);
        }
        return map;
    }

    @Override
    @Nonnull
    public BsonValue encode(@Nonnull M map, ExtraInfo extraInfo) {
        BsonDocument document = new BsonDocument();
        for (Map.Entry entry : map.entrySet()) {
            Codec<V> codecFor = this.getCodecFor(entry.getKey());
            document.put(this.getIdForKey(entry.getKey()), codecFor.encode(entry.getValue(), extraInfo));
        }
        this.encodeExtra(document, map, extraInfo);
        return document;
    }

    protected void encodeExtra(BsonDocument document, M map, ExtraInfo extraInfo) {
    }

    @Override
    public M decodeJson(@Nonnull RawJsonReader reader, @Nonnull ExtraInfo extraInfo) throws IOException {
        reader.expect('{');
        reader.consumeWhiteSpace();
        if (reader.tryConsume('}')) {
            return this.unmodifiable ? this.emptyMap() : this.createMap();
        }
        M map = this.createMap();
        while (true) {
            String id = reader.readString();
            reader.consumeWhiteSpace();
            reader.expect(':');
            reader.consumeWhiteSpace();
            extraInfo.pushKey(id, reader);
            try {
                K key = this.getKeyForId(id);
                if (key == null) {
                    this.handleUnknown(map, id, reader, extraInfo);
                } else {
                    Codec<V> codec = this.getCodecFor(key);
                    map.put(key, codec.decodeJson(reader, extraInfo));
                }
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
                    map = this.unmodifiableMap(map);
                }
                return map;
            }
            reader.consumeWhiteSpace();
        }
    }

    @Override
    @Nonnull
    public Schema toSchema(@Nonnull SchemaContext context) {
        ObjectSchema obj = new ObjectSchema();
        obj.setAdditionalProperties(false);
        LinkedHashMap props = this.codecProvider.keySet().stream().map(key -> {
            Codec<V> codec = this.getCodecFor(key);
            return Map.entry(this.getIdForKey(key), codec.toSchema(context));
        }).sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        obj.setProperties(props);
        return obj;
    }

    @Override
    public void validate(@Nonnull M map, ExtraInfo extraInfo) {
        for (Map.Entry entry : map.entrySet()) {
            Codec<V> codec = this.getCodecFor(entry.getKey());
            if (!(codec instanceof ValidatableCodec)) continue;
            ((ValidatableCodec)codec).validate(entry.getValue(), extraInfo);
        }
    }

    @Override
    public void validateDefaults(ExtraInfo extraInfo, @Nonnull Set<Codec<?>> tested) {
        if (!tested.add(this)) {
            return;
        }
        for (P value : this.codecProvider.values()) {
            Codec<V> codec = this.mapper.apply(value);
            ValidatableCodec.validateDefaults(codec, extraInfo, tested);
        }
    }

    private Codec<V> getCodecFor(K key) {
        return this.mapper.apply(this.codecProvider.get(key));
    }

    protected abstract String getIdForKey(K var1);

    protected abstract K getKeyForId(String var1);

    protected abstract M emptyMap();

    protected abstract M unmodifiableMap(M var1);
}

