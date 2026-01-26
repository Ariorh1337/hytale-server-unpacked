/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.JsonObjectCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonObject;

public final class JsonObjectCodecProvider
implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz.equals(JsonObject.class)) {
            return new JsonObjectCodec();
        }
        return null;
    }
}

