/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonDocument;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BsonJavaScriptWithScopeCodec
implements Codec<BsonJavaScriptWithScope> {
    private final Codec<BsonDocument> documentCodec;

    public BsonJavaScriptWithScopeCodec(Codec<BsonDocument> documentCodec) {
        this.documentCodec = documentCodec;
    }

    @Override
    public BsonJavaScriptWithScope decode(BsonReader bsonReader, DecoderContext decoderContext) {
        String code = bsonReader.readJavaScriptWithScope();
        BsonDocument scope = (BsonDocument)this.documentCodec.decode(bsonReader, decoderContext);
        return new BsonJavaScriptWithScope(code, scope);
    }

    @Override
    public void encode(BsonWriter writer, BsonJavaScriptWithScope codeWithScope, EncoderContext encoderContext) {
        writer.writeJavaScriptWithScope(codeWithScope.getCode());
        this.documentCodec.encode(writer, codeWithScope.getScope(), encoderContext);
    }

    @Override
    public Class<BsonJavaScriptWithScope> getEncoderClass() {
        return BsonJavaScriptWithScope.class;
    }
}

