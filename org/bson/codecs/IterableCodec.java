/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.ArrayList;
import java.util.UUID;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.BsonTypeCodecMap;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.OverridableUuidRepresentationCodec;
import org.bson.codecs.configuration.CodecRegistry;

public class IterableCodec
implements Codec<Iterable>,
OverridableUuidRepresentationCodec<Iterable> {
    private final CodecRegistry registry;
    private final BsonTypeCodecMap bsonTypeCodecMap;
    private final Transformer valueTransformer;
    private final UuidRepresentation uuidRepresentation;

    public IterableCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap) {
        this(registry, bsonTypeClassMap, null);
    }

    public IterableCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
        this(registry, new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry), valueTransformer, UuidRepresentation.UNSPECIFIED);
    }

    private IterableCodec(CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, Transformer valueTransformer, UuidRepresentation uuidRepresentation) {
        this.registry = Assertions.notNull("registry", registry);
        this.bsonTypeCodecMap = bsonTypeCodecMap;
        this.valueTransformer = valueTransformer != null ? valueTransformer : new Transformer(){

            @Override
            public Object transform(Object objectToTransform) {
                return objectToTransform;
            }
        };
        this.uuidRepresentation = uuidRepresentation;
    }

    @Override
    public Codec<Iterable> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
        return new IterableCodec(this.registry, this.bsonTypeCodecMap, this.valueTransformer, uuidRepresentation);
    }

    @Override
    public Iterable decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartArray();
        ArrayList<Object> list = new ArrayList<Object>();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            list.add(this.readValue(reader, decoderContext));
        }
        reader.readEndArray();
        return list;
    }

    @Override
    public void encode(BsonWriter writer, Iterable value, EncoderContext encoderContext) {
        writer.writeStartArray();
        for (Object cur : value) {
            this.writeValue(writer, encoderContext, cur);
        }
        writer.writeEndArray();
    }

    @Override
    public Class<Iterable> getEncoderClass() {
        return Iterable.class;
    }

    private void writeValue(BsonWriter writer, EncoderContext encoderContext, Object value) {
        if (value == null) {
            writer.writeNull();
        } else {
            Codec<?> codec = this.registry.get(value.getClass());
            encoderContext.encodeWithChildContext(codec, writer, value);
        }
    }

    private Object readValue(BsonReader reader, DecoderContext decoderContext) {
        BsonType bsonType = reader.getCurrentBsonType();
        if (bsonType == BsonType.NULL) {
            reader.readNull();
            return null;
        }
        Codec<Object> codec = this.bsonTypeCodecMap.get(bsonType);
        if (bsonType == BsonType.BINARY && reader.peekBinarySize() == 16) {
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
        }
        return this.valueTransformer.transform(codec.decode(reader, decoderContext));
    }
}

