/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public class ObjectIdCodec
implements Codec<ObjectId> {
    @Override
    public void encode(BsonWriter writer, ObjectId value, EncoderContext encoderContext) {
        writer.writeObjectId(value);
    }

    @Override
    public ObjectId decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readObjectId();
    }

    @Override
    public Class<ObjectId> getEncoderClass() {
        return ObjectId.class;
    }
}

