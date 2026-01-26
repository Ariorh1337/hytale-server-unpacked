/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.ArrayList;
import java.util.Map;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;
import org.bson.codecs.BsonTypeCodecMap;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

public class BsonDocumentCodec
implements CollectibleCodec<BsonDocument> {
    private static final String ID_FIELD_NAME = "_id";
    private static final CodecRegistry DEFAULT_REGISTRY = CodecRegistries.fromProviders(new BsonValueCodecProvider());
    private static final BsonTypeCodecMap DEFAULT_BSON_TYPE_CODEC_MAP = new BsonTypeCodecMap(BsonValueCodecProvider.getBsonTypeClassMap(), DEFAULT_REGISTRY);
    private final CodecRegistry codecRegistry;
    private final BsonTypeCodecMap bsonTypeCodecMap;

    public BsonDocumentCodec() {
        this(DEFAULT_REGISTRY, DEFAULT_BSON_TYPE_CODEC_MAP);
    }

    public BsonDocumentCodec(CodecRegistry codecRegistry) {
        this(codecRegistry, new BsonTypeCodecMap(BsonValueCodecProvider.getBsonTypeClassMap(), codecRegistry));
    }

    private BsonDocumentCodec(CodecRegistry codecRegistry, BsonTypeCodecMap bsonTypeCodecMap) {
        this.codecRegistry = Assertions.notNull("Codec registry", codecRegistry);
        this.bsonTypeCodecMap = Assertions.notNull("bsonTypeCodecMap", bsonTypeCodecMap);
    }

    public CodecRegistry getCodecRegistry() {
        return this.codecRegistry;
    }

    @Override
    public BsonDocument decode(BsonReader reader, DecoderContext decoderContext) {
        ArrayList<BsonElement> keyValuePairs = new ArrayList<BsonElement>();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            keyValuePairs.add(new BsonElement(fieldName, this.readValue(reader, decoderContext)));
        }
        reader.readEndDocument();
        return new BsonDocument(keyValuePairs);
    }

    protected BsonValue readValue(BsonReader reader, DecoderContext decoderContext) {
        return (BsonValue)this.bsonTypeCodecMap.get(reader.getCurrentBsonType()).decode(reader, decoderContext);
    }

    @Override
    public void encode(BsonWriter writer, BsonDocument value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        this.beforeFields(writer, encoderContext, value);
        for (Map.Entry<String, BsonValue> entry : value.entrySet()) {
            if (this.skipField(encoderContext, entry.getKey())) continue;
            writer.writeName(entry.getKey());
            this.writeValue(writer, encoderContext, entry.getValue());
        }
        writer.writeEndDocument();
    }

    private void beforeFields(BsonWriter bsonWriter, EncoderContext encoderContext, BsonDocument value) {
        if (encoderContext.isEncodingCollectibleDocument() && value.containsKey(ID_FIELD_NAME)) {
            bsonWriter.writeName(ID_FIELD_NAME);
            this.writeValue(bsonWriter, encoderContext, value.get(ID_FIELD_NAME));
        }
    }

    private boolean skipField(EncoderContext encoderContext, String key) {
        return encoderContext.isEncodingCollectibleDocument() && key.equals(ID_FIELD_NAME);
    }

    private void writeValue(BsonWriter writer, EncoderContext encoderContext, BsonValue value) {
        Codec<?> codec = this.codecRegistry.get(value.getClass());
        encoderContext.encodeWithChildContext(codec, writer, value);
    }

    @Override
    public Class<BsonDocument> getEncoderClass() {
        return BsonDocument.class;
    }

    @Override
    public BsonDocument generateIdIfAbsentFromDocument(BsonDocument document) {
        if (!this.documentHasId(document)) {
            document.put(ID_FIELD_NAME, new BsonObjectId(new ObjectId()));
        }
        return document;
    }

    @Override
    public boolean documentHasId(BsonDocument document) {
        return document.containsKey(ID_FIELD_NAME);
    }

    @Override
    public BsonValue getDocumentId(BsonDocument document) {
        return document.get(ID_FIELD_NAME);
    }
}

