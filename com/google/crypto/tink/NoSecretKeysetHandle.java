/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.KeysetReader;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.Keyset;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Deprecated
public final class NoSecretKeysetHandle {
    @Deprecated
    public static final KeysetHandle parseFrom(byte[] serialized) throws GeneralSecurityException {
        try {
            Keyset keyset = Keyset.parseFrom(serialized, ExtensionRegistryLite.getEmptyRegistry());
            NoSecretKeysetHandle.validate(keyset);
            return KeysetHandle.fromKeyset(keyset);
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("invalid keyset");
        }
    }

    public static final KeysetHandle read(KeysetReader reader) throws GeneralSecurityException, IOException {
        Keyset keyset = reader.read();
        NoSecretKeysetHandle.validate(keyset);
        return KeysetHandle.fromKeyset(keyset);
    }

    private static void validate(Keyset keyset) throws GeneralSecurityException {
        for (Keyset.Key key : keyset.getKeyList()) {
            if (key.getKeyData().getKeyMaterialType() != KeyData.KeyMaterialType.UNKNOWN_KEYMATERIAL && key.getKeyData().getKeyMaterialType() != KeyData.KeyMaterialType.SYMMETRIC && key.getKeyData().getKeyMaterialType() != KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE) continue;
            throw new GeneralSecurityException("keyset contains secret key material");
        }
    }

    private NoSecretKeysetHandle() {
    }
}

