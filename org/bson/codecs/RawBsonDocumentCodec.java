/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.RawBsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.ByteBufferBsonInput;

public class RawBsonDocumentCodec
implements Codec<RawBsonDocument> {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void encode(BsonWriter writer, RawBsonDocument value, EncoderContext encoderContext) {
        try (BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(value.getByteBuffer()));){
            writer.pipe(reader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RawBsonDocument decode(BsonReader reader, DecoderContext decoderContext) {
        BasicOutputBuffer buffer = new BasicOutputBuffer(0);
        BsonBinaryWriter writer = new BsonBinaryWriter(buffer);
        try {
            writer.pipe(reader);
            RawBsonDocument rawBsonDocument = new RawBsonDocument(buffer.getInternalBuffer(), 0, buffer.getPosition());
            return rawBsonDocument;
        }
        finally {
            writer.close();
            buffer.close();
        }
    }

    @Override
    public Class<RawBsonDocument> getEncoderClass() {
        return RawBsonDocument.class;
    }
}

