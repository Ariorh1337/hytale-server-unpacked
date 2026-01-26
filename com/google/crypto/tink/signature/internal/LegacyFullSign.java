/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.internal.KeyManagerRegistry;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.signature.internal.LegacyFullVerify;
import com.google.crypto.tink.subtle.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public final class LegacyFullSign
implements PublicKeySign {
    private final PublicKeySign rawSigner;
    private final byte[] outputPrefix;
    private final byte[] messageSuffix;

    public static PublicKeySign create(LegacyProtoKey key) throws GeneralSecurityException {
        ProtoKeySerialization protoKeySerialization = key.getSerialization(InsecureSecretKeyAccess.get());
        KeyManager<PublicKeySign> manager = KeyManagerRegistry.globalInstance().getKeyManager(protoKeySerialization.getTypeUrl(), PublicKeySign.class);
        PublicKeySign rawSigner = manager.getPrimitive(protoKeySerialization.getValue());
        return new LegacyFullSign(rawSigner, LegacyFullVerify.getOutputPrefix(protoKeySerialization), LegacyFullVerify.getMessageSuffix(protoKeySerialization));
    }

    private LegacyFullSign(PublicKeySign rawSigner, byte[] outputPrefix, byte[] messageSuffix) {
        this.rawSigner = rawSigner;
        this.outputPrefix = outputPrefix;
        this.messageSuffix = messageSuffix;
    }

    @Override
    public byte[] sign(byte[] data) throws GeneralSecurityException {
        byte[] signature = this.messageSuffix.length == 0 ? this.rawSigner.sign(data) : this.rawSigner.sign(Bytes.concat(data, this.messageSuffix));
        if (this.outputPrefix.length == 0) {
            return signature;
        }
        return Bytes.concat(this.outputPrefix, signature);
    }
}

