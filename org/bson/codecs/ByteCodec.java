/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.NumberCodecHelper;

public class ByteCodec
implements Codec<Byte> {
    @Override
    public void encode(BsonWriter writer, Byte value, EncoderContext encoderContext) {
        writer.writeInt32(value.byteValue());
    }

    @Override
    public Byte decode(BsonReader reader, DecoderContext decoderContext) {
        int value = NumberCodecHelper.decodeInt(reader);
        if (value < -128 || value > 127) {
            throw new BsonInvalidOperationException(String.format("%s can not be converted into a Byte.", value));
        }
        return (byte)value;
    }

    @Override
    public Class<Byte> getEncoderClass() {
        return Byte.class;
    }
}

