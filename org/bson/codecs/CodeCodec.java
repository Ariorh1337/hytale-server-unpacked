/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Code;

public class CodeCodec
implements Codec<Code> {
    @Override
    public void encode(BsonWriter writer, Code value, EncoderContext encoderContext) {
        writer.writeJavaScript(value.getCode());
    }

    @Override
    public Code decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return new Code(bsonReader.readJavaScript());
    }

    @Override
    public Class<Code> getEncoderClass() {
        return Code.class;
    }
}

