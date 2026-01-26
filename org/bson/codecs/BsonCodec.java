/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class BsonCodec
implements Codec<Bson> {
    private static final Codec<BsonDocument> BSON_DOCUMENT_CODEC = new BsonDocumentCodec();
    private final CodecRegistry registry;

    public BsonCodec(CodecRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Bson decode(BsonReader reader, DecoderContext decoderContext) {
        throw new UnsupportedOperationException("The BsonCodec can only encode to Bson");
    }

    @Override
    public void encode(BsonWriter writer, Bson value, EncoderContext encoderContext) {
        try {
            BsonDocument bsonDocument = value.toBsonDocument(BsonDocument.class, this.registry);
            BSON_DOCUMENT_CODEC.encode(writer, bsonDocument, encoderContext);
        }
        catch (Exception e) {
            throw new CodecConfigurationException(String.format("Unable to encode a Bson implementation: %s", value), e);
        }
    }

    @Override
    public Class<Bson> getEncoderClass() {
        return Bson.class;
    }
}

