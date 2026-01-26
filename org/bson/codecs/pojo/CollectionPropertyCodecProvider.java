/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

final class CollectionPropertyCodecProvider
implements PropertyCodecProvider {
    CollectionPropertyCodecProvider() {
    }

    @Override
    public <T> Codec<T> get(TypeWithTypeParameters<T> type, PropertyCodecRegistry registry) {
        if (Collection.class.isAssignableFrom(type.getType()) && type.getTypeParameters().size() == 1) {
            return new CollectionCodec(type.getType(), registry.get(type.getTypeParameters().get(0)));
        }
        return null;
    }

    private static class CollectionCodec<T>
    implements Codec<Collection<T>> {
        private final Class<Collection<T>> encoderClass;
        private final Codec<T> codec;

        CollectionCodec(Class<Collection<T>> encoderClass, Codec<T> codec) {
            this.encoderClass = encoderClass;
            this.codec = codec;
        }

        @Override
        public void encode(BsonWriter writer, Collection<T> collection, EncoderContext encoderContext) {
            writer.writeStartArray();
            for (T value : collection) {
                if (value == null) {
                    writer.writeNull();
                    continue;
                }
                this.codec.encode(writer, value, encoderContext);
            }
            writer.writeEndArray();
        }

        @Override
        public Collection<T> decode(BsonReader reader, DecoderContext context) {
            Collection<T> collection = this.getInstance();
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (reader.getCurrentBsonType() == BsonType.NULL) {
                    collection.add(null);
                    reader.readNull();
                    continue;
                }
                collection.add(this.codec.decode(reader, context));
            }
            reader.readEndArray();
            return collection;
        }

        @Override
        public Class<Collection<T>> getEncoderClass() {
            return this.encoderClass;
        }

        private Collection<T> getInstance() {
            if (this.encoderClass.isInterface()) {
                if (this.encoderClass.isAssignableFrom(ArrayList.class)) {
                    return new ArrayList();
                }
                if (this.encoderClass.isAssignableFrom(HashSet.class)) {
                    return new HashSet();
                }
                throw new CodecConfigurationException(String.format("Unsupported Collection interface of %s!", this.encoderClass.getName()));
            }
            try {
                return this.encoderClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new CodecConfigurationException(e.getMessage(), e);
            }
        }
    }
}

