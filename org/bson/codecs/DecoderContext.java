/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.assertions.Assertions;
import org.bson.codecs.Decoder;

public final class DecoderContext {
    private static final DecoderContext DEFAULT_CONTEXT = DecoderContext.builder().build();
    private final boolean checkedDiscriminator;

    public boolean hasCheckedDiscriminator() {
        return this.checkedDiscriminator;
    }

    public static Builder builder() {
        return new Builder();
    }

    public <T> T decodeWithChildContext(Decoder<T> decoder, BsonReader reader) {
        Assertions.notNull("decoder", decoder);
        return decoder.decode(reader, DEFAULT_CONTEXT);
    }

    private DecoderContext(Builder builder) {
        this.checkedDiscriminator = builder.hasCheckedDiscriminator();
    }

    public static final class Builder {
        private boolean checkedDiscriminator;

        private Builder() {
        }

        public boolean hasCheckedDiscriminator() {
            return this.checkedDiscriminator;
        }

        public Builder checkedDiscriminator(boolean checkedDiscriminator) {
            this.checkedDiscriminator = checkedDiscriminator;
            return this;
        }

        public DecoderContext build() {
            return new DecoderContext(this);
        }
    }
}

