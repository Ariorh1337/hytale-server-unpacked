/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonType;
import org.bson.assertions.Assertions;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;

public class BsonTypeCodecMap {
    private final BsonTypeClassMap bsonTypeClassMap;
    private final Codec<?>[] codecs = new Codec[256];

    public BsonTypeCodecMap(BsonTypeClassMap bsonTypeClassMap, CodecRegistry codecRegistry) {
        this.bsonTypeClassMap = Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap);
        Assertions.notNull("codecRegistry", codecRegistry);
        for (BsonType cur : bsonTypeClassMap.keys()) {
            Class<?> clazz = bsonTypeClassMap.get(cur);
            if (clazz == null) continue;
            try {
                this.codecs[cur.getValue()] = codecRegistry.get(clazz);
            }
            catch (CodecConfigurationException codecConfigurationException) {}
        }
    }

    public Codec<?> get(BsonType bsonType) {
        Codec<?> codec = this.codecs[bsonType.getValue()];
        if (codec == null) {
            Class<?> clazz = this.bsonTypeClassMap.get(bsonType);
            if (clazz == null) {
                throw new CodecConfigurationException(String.format("No class mapped for BSON type %s.", new Object[]{bsonType}));
            }
            throw new CodecConfigurationException(String.format("Can't find a codec for %s.", clazz));
        }
        return codec;
    }
}

