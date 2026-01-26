/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonJavaScript;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonJavaScriptCodec
implements Codec<BsonJavaScript> {
    @Override
    public BsonJavaScript decode(BsonReader reader, DecoderContext decoderContext) {
        return new BsonJavaScript(reader.readJavaScript());
    }

    @Override
    public void encode(BsonWriter writer, BsonJavaScript value, EncoderContext encoderContext) {
        writer.writeJavaScript(value.getCode());
    }

    @Override
    public Class<BsonJavaScript> getEncoderClass() {
        return BsonJavaScript.class;
    }
}

