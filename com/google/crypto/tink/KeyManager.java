/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.proto.KeyData;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import java.security.GeneralSecurityException;

public interface KeyManager<P> {
    public P getPrimitive(ByteString var1) throws GeneralSecurityException;

    @Deprecated
    default public P getPrimitive(MessageLite key) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public MessageLite newKey(ByteString serializedKeyFormat) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public MessageLite newKey(MessageLite keyFormat) throws GeneralSecurityException {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public boolean doesSupport(String typeUrl) {
        throw new UnsupportedOperationException();
    }

    public String getKeyType();

    @Deprecated
    default public int getVersion() {
        throw new UnsupportedOperationException();
    }

    public Class<P> getPrimitiveClass();

    public KeyData newKeyData(ByteString var1) throws GeneralSecurityException;
}

