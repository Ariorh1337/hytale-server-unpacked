/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.CodeWithScope;

public class CodeWithScopeCodec
implements Codec<CodeWithScope> {
    private final Codec<Document> documentCodec;

    public CodeWithScopeCodec(Codec<Document> documentCodec) {
        this.documentCodec = documentCodec;
    }

    @Override
    public CodeWithScope decode(BsonReader bsonReader, DecoderContext decoderContext) {
        String code = bsonReader.readJavaScriptWithScope();
        Document scope = (Document)this.documentCodec.decode(bsonReader, decoderContext);
        return new CodeWithScope(code, scope);
    }

    @Override
    public void encode(BsonWriter writer, CodeWithScope codeWithScope, EncoderContext encoderContext) {
        writer.writeJavaScriptWithScope(codeWithScope.getCode());
        this.documentCodec.encode(writer, codeWithScope.getScope(), encoderContext);
    }

    @Override
    public Class<CodeWithScope> getEncoderClass() {
        return CodeWithScope.class;
    }
}

