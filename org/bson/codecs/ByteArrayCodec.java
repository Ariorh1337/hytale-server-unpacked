/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ByteArrayCodec
implements Codec<byte[]> {
    @Override
    public void encode(BsonWriter writer, byte[] value, EncoderContext encoderContext) {
        writer.writeBinaryData(new BsonBinary(value));
    }

    @Override
    public byte[] decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readBinaryData().getData();
    }

    @Override
    public Class<byte[]> getEncoderClass() {
        return byte[].class;
    }
}

