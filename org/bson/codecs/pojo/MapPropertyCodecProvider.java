/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.HashMap;
import java.util.Map;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeData;
import org.bson.codecs.pojo.TypeWithTypeParameters;

final class MapPropertyCodecProvider
implements PropertyCodecProvider {
    MapPropertyCodecProvider() {
    }

    @Override
    public <T> Codec<T> get(TypeWithTypeParameters<T> type, PropertyCodecRegistry registry) {
        if (Map.class.isAssignableFrom(type.getType()) && type.getTypeParameters().size() == 2) {
            Class<?> keyType = type.getTypeParameters().get(0).getType();
            if (!keyType.equals(String.class)) {
                throw new CodecConfigurationException(String.format("Invalid Map type. Maps MUST have string keys, found %s instead.", keyType));
            }
            try {
                return new MapCodec(type.getType(), registry.get(type.getTypeParameters().get(1)));
            }
            catch (CodecConfigurationException e) {
                if (type.getTypeParameters().get(1).getType() == Object.class) {
                    try {
                        return registry.get(TypeData.builder(Map.class).build());
                    }
                    catch (CodecConfigurationException codecConfigurationException) {
                        // empty catch block
                    }
                }
                throw e;
            }
        }
        return null;
    }

    private static class MapCodec<T>
    implements Codec<Map<String, T>> {
        private final Class<Map<String, T>> encoderClass;
        private final Codec<T> codec;

        MapCodec(Class<Map<String, T>> encoderClass, Codec<T> codec) {
            this.encoderClass = encoderClass;
            this.codec = codec;
        }

        @Override
        public void encode(BsonWriter writer, Map<String, T> map, EncoderContext encoderContext) {
            writer.writeStartDocument();
            for (Map.Entry<String, T> entry : map.entrySet()) {
                writer.writeName(entry.getKey());
                if (entry.getValue() == null) {
                    writer.writeNull();
                    continue;
                }
                this.codec.encode(writer, entry.getValue(), encoderContext);
            }
            writer.writeEndDocument();
        }

        @Override
        public Map<String, T> decode(BsonReader reader, DecoderContext context) {
            reader.readStartDocument();
            Map<String, T> map = this.getInstance();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                if (reader.getCurrentBsonType() == BsonType.NULL) {
                    map.put(reader.readName(), null);
                    reader.readNull();
                    continue;
                }
                map.put(reader.readName(), this.codec.decode(reader, context));
            }
            reader.readEndDocument();
            return map;
        }

        @Override
        public Class<Map<String, T>> getEncoderClass() {
            return this.encoderClass;
        }

        private Map<String, T> getInstance() {
            if (this.encoderClass.isInterface()) {
                return new HashMap();
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

