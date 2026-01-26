/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.BsonTypeCodecMap;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.IterableCodecProvider;
import org.bson.codecs.MapCodecProvider;
import org.bson.codecs.OverridableUuidRepresentationCodec;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

public class MapCodec
implements Codec<Map<String, Object>>,
OverridableUuidRepresentationCodec<Map<String, Object>> {
    private static final CodecRegistry DEFAULT_REGISTRY = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(), new BsonValueCodecProvider(), new DocumentCodecProvider(), new IterableCodecProvider(), new MapCodecProvider()));
    private static final BsonTypeClassMap DEFAULT_BSON_TYPE_CLASS_MAP = new BsonTypeClassMap();
    private final BsonTypeCodecMap bsonTypeCodecMap;
    private final CodecRegistry registry;
    private final Transformer valueTransformer;
    private final UuidRepresentation uuidRepresentation;

    public MapCodec() {
        this(DEFAULT_REGISTRY);
    }

    public MapCodec(CodecRegistry registry) {
        this(registry, DEFAULT_BSON_TYPE_CLASS_MAP);
    }

    public MapCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap) {
        this(registry, bsonTypeClassMap, null);
    }

    public MapCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
        this(registry, new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry), valueTransformer, UuidRepresentation.UNSPECIFIED);
    }

    private MapCodec(CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, Transformer valueTransformer, UuidRepresentation uuidRepresentation) {
        this.registry = Assertions.notNull("registry", registry);
        this.bsonTypeCodecMap = bsonTypeCodecMap;
        this.valueTransformer = valueTransformer != null ? valueTransformer : new Transformer(){

            @Override
            public Object transform(Object value) {
                return value;
            }
        };
        this.uuidRepresentation = uuidRepresentation;
    }

    @Override
    public Codec<Map<String, Object>> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
        return new MapCodec(this.registry, this.bsonTypeCodecMap, this.valueTransformer, uuidRepresentation);
    }

    @Override
    public void encode(BsonWriter writer, Map<String, Object> map, EncoderContext encoderContext) {
        writer.writeStartDocument();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            writer.writeName(entry.getKey());
            this.writeValue(writer, encoderContext, entry.getValue());
        }
        writer.writeEndDocument();
    }

    @Override
    public Map<String, Object> decode(BsonReader reader, DecoderContext decoderContext) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            map.put(fieldName, this.readValue(reader, decoderContext));
        }
        reader.readEndDocument();
        return map;
    }

    @Override
    public Class<Map<String, Object>> getEncoderClass() {
        return Map.class;
    }

    private Object readValue(BsonReader reader, DecoderContext decoderContext) {
        BsonType bsonType = reader.getCurrentBsonType();
        if (bsonType == BsonType.NULL) {
            reader.readNull();
            return null;
        }
        if (bsonType == BsonType.ARRAY) {
            return decoderContext.decodeWithChildContext(this.registry.get(List.class), reader);
        }
        if (bsonType == BsonType.BINARY && reader.peekBinarySize() == 16) {
            Codec<Object> codec = this.bsonTypeCodecMap.get(bsonType);
            switch (reader.peekBinarySubType()) {
                case 3: {
                    if (this.uuidRepresentation != UuidRepresentation.JAVA_LEGACY && this.uuidRepresentation != UuidRepresentation.C_SHARP_LEGACY && this.uuidRepresentation != UuidRepresentation.PYTHON_LEGACY) break;
                    codec = this.registry.get(UUID.class);
                    break;
                }
                case 4: {
                    if (this.uuidRepresentation != UuidRepresentation.STANDARD) break;
                    codec = this.registry.get(UUID.class);
                    break;
                }
            }
            return decoderContext.decodeWithChildContext(codec, reader);
        }
        return this.valueTransformer.transform(this.bsonTypeCodecMap.get(bsonType).decode(reader, decoderContext));
    }

    private void writeValue(BsonWriter writer, EncoderContext encoderContext, Object value) {
        if (value == null) {
            writer.writeNull();
        } else {
            Codec<?> codec = this.registry.get(value.getClass());
            encoderContext.encodeWithChildContext(codec, writer, value);
        }
    }
}

