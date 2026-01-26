/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.proto.KeyData;
import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;

public interface PrivateKeyManager<P>
extends KeyManager<P> {
    public KeyData getPublicKeyData(ByteString var1) throws GeneralSecurityException;
}

