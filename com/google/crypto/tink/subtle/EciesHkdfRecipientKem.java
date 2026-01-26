/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.Hkdf;
import java.security.GeneralSecurityException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public final class EciesHkdfRecipientKem {
    private ECPrivateKey recipientPrivateKey;

    public EciesHkdfRecipientKem(ECPrivateKey recipientPrivateKey) {
        this.recipientPrivateKey = recipientPrivateKey;
    }

    public byte[] generateKey(byte[] kemBytes, String hmacAlgo, byte[] hkdfSalt, byte[] hkdfInfo, int keySizeInBytes, EllipticCurves.PointFormatType pointFormat) throws GeneralSecurityException {
        ECPublicKey ephemeralPublicKey = EllipticCurves.getEcPublicKey(this.recipientPrivateKey.getParams(), pointFormat, kemBytes);
        byte[] sharedSecret = EllipticCurves.computeSharedSecret(this.recipientPrivateKey, ephemeralPublicKey);
        return Hkdf.computeEciesHkdfSymmetricKey(kemBytes, sharedSecret, hmacAlgo, hkdfSalt, hkdfInfo, keySizeInBytes);
    }
}

