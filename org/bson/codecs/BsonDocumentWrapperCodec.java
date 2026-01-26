/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;

public class BsonDocumentWrapperCodec
implements Codec<BsonDocumentWrapper> {
    private final Codec<BsonDocument> bsonDocumentCodec;

    public BsonDocumentWrapperCodec(Codec<BsonDocument> bsonDocumentCodec) {
        this.bsonDocumentCodec = bsonDocumentCodec;
    }

    @Override
    public BsonDocumentWrapper decode(BsonReader reader, DecoderContext decoderContext) {
        throw new UnsupportedOperationException("Decoding into a BsonDocumentWrapper is not allowed");
    }

    @Override
    public void encode(BsonWriter writer, BsonDocumentWrapper value, EncoderContext encoderContext) {
        if (value.isUnwrapped()) {
            this.bsonDocumentCodec.encode(writer, value, encoderContext);
        } else {
            Encoder encoder = value.getEncoder();
            encoder.encode(writer, value.getWrappedDocument(), encoderContext);
        }
    }

    @Override
    public Class<BsonDocumentWrapper> getEncoderClass() {
        return BsonDocumentWrapper.class;
    }
}

