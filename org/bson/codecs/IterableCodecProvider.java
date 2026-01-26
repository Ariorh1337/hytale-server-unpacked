/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.Transformer;
import org.bson.assertions.Assertions;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.Codec;
import org.bson.codecs.IterableCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class IterableCodecProvider
implements CodecProvider {
    private final BsonTypeClassMap bsonTypeClassMap;
    private final Transformer valueTransformer;

    public IterableCodecProvider() {
        this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP);
    }

    public IterableCodecProvider(Transformer valueTransformer) {
        this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP, valueTransformer);
    }

    public IterableCodecProvider(BsonTypeClassMap bsonTypeClassMap) {
        this(bsonTypeClassMap, null);
    }

    public IterableCodecProvider(BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
        this.bsonTypeClassMap = Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap);
        this.valueTransformer = valueTransformer;
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (Iterable.class.isAssignableFrom(clazz)) {
            return new IterableCodec(registry, this.bsonTypeClassMap, this.valueTransformer);
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IterableCodecProvider that = (IterableCodecProvider)o;
        if (!this.bsonTypeClassMap.equals(that.bsonTypeClassMap)) {
            return false;
        }
        return !(this.valueTransformer != null ? !this.valueTransformer.equals(that.valueTransformer) : that.valueTransformer != null);
    }

    public int hashCode() {
        int result = this.bsonTypeClassMap.hashCode();
        result = 31 * result + (this.valueTransformer != null ? this.valueTransformer.hashCode() : 0);
        return result;
    }
}

