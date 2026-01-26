/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.DiscriminatorLookup;
import org.bson.codecs.pojo.PojoCodec;

final class AutomaticPojoCodec<T>
extends PojoCodec<T> {
    private final PojoCodec<T> pojoCodec;

    AutomaticPojoCodec(PojoCodec<T> pojoCodec) {
        this.pojoCodec = pojoCodec;
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        try {
            return this.pojoCodec.decode(reader, decoderContext);
        }
        catch (CodecConfigurationException e) {
            throw new CodecConfigurationException(String.format("An exception occurred when decoding using the AutomaticPojoCodec.%nDecoding into a '%s' failed with the following exception:%n%n%s%n%nA custom Codec or PojoCodec may need to be explicitly configured and registered to handle this type.", this.pojoCodec.getEncoderClass().getSimpleName(), e.getMessage()), e);
        }
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        try {
            this.pojoCodec.encode(writer, value, encoderContext);
        }
        catch (CodecConfigurationException e) {
            throw new CodecConfigurationException(String.format("An exception occurred when encoding using the AutomaticPojoCodec.%nEncoding a %s: '%s' failed with the following exception:%n%n%s%n%nA custom Codec or PojoCodec may need to be explicitly configured and registered to handle this type.", this.getEncoderClass().getSimpleName(), value, e.getMessage()), e);
        }
    }

    @Override
    public Class<T> getEncoderClass() {
        return this.pojoCodec.getEncoderClass();
    }

    @Override
    ClassModel<T> getClassModel() {
        return this.pojoCodec.getClassModel();
    }

    @Override
    DiscriminatorLookup getDiscriminatorLookup() {
        return this.pojoCodec.getDiscriminatorLookup();
    }
}

