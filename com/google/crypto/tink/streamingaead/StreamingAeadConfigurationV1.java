/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.Configuration;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.InternalConfiguration;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveRegistry;
import com.google.crypto.tink.streamingaead.AesCtrHmacStreamingKey;
import com.google.crypto.tink.streamingaead.AesGcmHkdfStreamingKey;
import com.google.crypto.tink.streamingaead.StreamingAeadWrapper;
import com.google.crypto.tink.subtle.AesCtrHmacStreaming;
import com.google.crypto.tink.subtle.AesGcmHkdfStreaming;
import java.security.GeneralSecurityException;

class StreamingAeadConfigurationV1 {
    private static final InternalConfiguration INTERNAL_CONFIGURATION = StreamingAeadConfigurationV1.create();

    private StreamingAeadConfigurationV1() {
    }

    private static InternalConfiguration create() {
        try {
            PrimitiveRegistry.Builder builder = PrimitiveRegistry.builder();
            StreamingAeadWrapper.registerToInternalPrimitiveRegistry(builder);
            builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AesGcmHkdfStreaming::create, AesGcmHkdfStreamingKey.class, StreamingAead.class));
            builder.registerPrimitiveConstructor(PrimitiveConstructor.create(AesCtrHmacStreaming::create, AesCtrHmacStreamingKey.class, StreamingAead.class));
            return InternalConfiguration.createFromPrimitiveRegistry(builder.build());
        }
        catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Configuration get() throws GeneralSecurityException {
        if (TinkFipsUtil.useOnlyFips()) {
            throw new GeneralSecurityException("Cannot use non-FIPS-compliant StreamingAead in FIPS mode");
        }
        return INTERNAL_CONFIGURATION;
    }
}

