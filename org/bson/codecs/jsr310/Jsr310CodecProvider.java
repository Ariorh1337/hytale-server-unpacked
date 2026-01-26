/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.jsr310;

import java.util.HashMap;
import java.util.Map;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.InstantCodec;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.bson.codecs.jsr310.LocalDateTimeCodec;
import org.bson.codecs.jsr310.LocalTimeCodec;

public class Jsr310CodecProvider
implements CodecProvider {
    private static final Map<Class<?>, Codec<?>> JSR310_CODEC_MAP = new HashMap();

    private static void putCodec(Codec<?> codec) {
        JSR310_CODEC_MAP.put(codec.getEncoderClass(), codec);
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        return JSR310_CODEC_MAP.get(clazz);
    }

    static {
        try {
            Class.forName("java.time.Instant");
            Jsr310CodecProvider.putCodec(new InstantCodec());
            Jsr310CodecProvider.putCodec(new LocalDateCodec());
            Jsr310CodecProvider.putCodec(new LocalDateTimeCodec());
            Jsr310CodecProvider.putCodec(new LocalTimeCodec());
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }
}

