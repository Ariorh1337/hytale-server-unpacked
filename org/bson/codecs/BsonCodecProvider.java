/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.codecs.BsonCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

public class BsonCodecProvider
implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (Bson.class.isAssignableFrom(clazz)) {
            return new BsonCodec(registry);
        }
        return null;
    }
}

