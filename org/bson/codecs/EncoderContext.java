/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.codecs.Encoder;

public final class EncoderContext {
    private static final EncoderContext DEFAULT_CONTEXT = EncoderContext.builder().build();
    private final boolean encodingCollectibleDocument;

    public static Builder builder() {
        return new Builder();
    }

    public boolean isEncodingCollectibleDocument() {
        return this.encodingCollectibleDocument;
    }

    public <T> void encodeWithChildContext(Encoder<T> encoder, BsonWriter writer, T value) {
        encoder.encode(writer, value, DEFAULT_CONTEXT);
    }

    public EncoderContext getChildContext() {
        return DEFAULT_CONTEXT;
    }

    private EncoderContext(Builder builder) {
        this.encodingCollectibleDocument = builder.encodingCollectibleDocument;
    }

    public static final class Builder {
        private boolean encodingCollectibleDocument;

        private Builder() {
        }

        public Builder isEncodingCollectibleDocument(boolean encodingCollectibleDocument) {
            this.encodingCollectibleDocument = encodingCollectibleDocument;
            return this;
        }

        public EncoderContext build() {
            return new EncoderContext(this);
        }
    }
}

