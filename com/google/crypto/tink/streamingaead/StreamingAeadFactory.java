/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.streamingaead;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.streamingaead.StreamingAeadWrapper;
import java.security.GeneralSecurityException;

@Deprecated
public final class StreamingAeadFactory {
    public static StreamingAead getPrimitive(KeysetHandle keysetHandle) throws GeneralSecurityException {
        StreamingAeadWrapper.register();
        return keysetHandle.getPrimitive(RegistryConfiguration.get(), StreamingAead.class);
    }

    private StreamingAeadFactory() {
    }
}

