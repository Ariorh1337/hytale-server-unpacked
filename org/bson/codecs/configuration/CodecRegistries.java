/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.configuration;

import java.util.Arrays;
import java.util.List;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.MapOfCodecsProvider;
import org.bson.internal.ProvidersCodecRegistry;

public final class CodecRegistries {
    public static CodecRegistry fromCodecs(Codec<?> ... codecs) {
        return CodecRegistries.fromCodecs(Arrays.asList(codecs));
    }

    public static CodecRegistry fromCodecs(List<? extends Codec<?>> codecs) {
        return CodecRegistries.fromProviders(new MapOfCodecsProvider(codecs));
    }

    public static CodecRegistry fromProviders(CodecProvider ... providers) {
        return CodecRegistries.fromProviders(Arrays.asList(providers));
    }

    public static CodecRegistry fromProviders(List<? extends CodecProvider> providers) {
        return new ProvidersCodecRegistry(providers);
    }

    public static CodecRegistry fromRegistries(CodecRegistry ... registries) {
        return CodecRegistries.fromRegistries(Arrays.asList(registries));
    }

    public static CodecRegistry fromRegistries(List<? extends CodecRegistry> registries) {
        return new ProvidersCodecRegistry(registries);
    }

    private CodecRegistries() {
    }
}

