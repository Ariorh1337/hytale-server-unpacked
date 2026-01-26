/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

final class EnumPropertyCodecProvider
implements PropertyCodecProvider {
    private final CodecRegistry codecRegistry;

    EnumPropertyCodecProvider(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public <T> Codec<T> get(TypeWithTypeParameters<T> type, PropertyCodecRegistry propertyCodecRegistry) {
        Class<T> clazz = type.getType();
        if (Enum.class.isAssignableFrom(clazz)) {
            try {
                return this.codecRegistry.get(clazz);
            }
            catch (CodecConfigurationException e) {
                return new EnumCodec<T>(clazz);
            }
        }
        return null;
    }

    private static class EnumCodec<T extends Enum<T>>
    implements Codec<T> {
        private final Class<T> clazz;

        EnumCodec(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
            writer.writeString(((Enum)value).name());
        }

        @Override
        public Class<T> getEncoderClass() {
            return this.clazz;
        }

        @Override
        public T decode(BsonReader reader, DecoderContext decoderContext) {
            return Enum.valueOf(this.clazz, reader.readString());
        }
    }
}

