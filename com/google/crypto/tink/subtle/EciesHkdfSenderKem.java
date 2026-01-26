/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.Hkdf;
import com.google.crypto.tink.util.Bytes;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public final class EciesHkdfSenderKem {
    private final ECPublicKey recipientPublicKey;

    public EciesHkdfSenderKem(ECPublicKey recipientPublicKey) {
        this.recipientPublicKey = recipientPublicKey;
    }

    public KemKey generateKey(String hmacAlgo, byte[] hkdfSalt, byte[] hkdfInfo, int keySizeInBytes, EllipticCurves.PointFormatType pointFormat) throws GeneralSecurityException {
        KeyPair ephemeralKeyPair = EllipticCurves.generateKeyPair(this.recipientPublicKey.getParams());
        ECPublicKey ephemeralPublicKey = (ECPublicKey)ephemeralKeyPair.getPublic();
        ECPrivateKey ephemeralPrivateKey = (ECPrivateKey)ephemeralKeyPair.getPrivate();
        byte[] sharedSecret = EllipticCurves.computeSharedSecret(ephemeralPrivateKey, this.recipientPublicKey);
        byte[] kemBytes = EllipticCurves.pointEncode(ephemeralPublicKey.getParams().getCurve(), pointFormat, ephemeralPublicKey.getW());
        byte[] symmetricKey = Hkdf.computeEciesHkdfSymmetricKey(kemBytes, sharedSecret, hmacAlgo, hkdfSalt, hkdfInfo, keySizeInBytes);
        return new KemKey(kemBytes, symmetricKey);
    }

    public static final class KemKey {
        private final Bytes kemBytes;
        private final Bytes symmetricKey;

        public KemKey(byte[] kemBytes, byte[] symmetricKey) {
            if (kemBytes == null) {
                throw new NullPointerException("KemBytes must be non-null");
            }
            if (symmetricKey == null) {
                throw new NullPointerException("symmetricKey must be non-null");
            }
            this.kemBytes = Bytes.copyFrom(kemBytes);
            this.symmetricKey = Bytes.copyFrom(symmetricKey);
        }

        public byte[] getKemBytes() {
            return this.kemBytes.toByteArray();
        }

        public byte[] getSymmetricKey() {
            return this.symmetricKey.toByteArray();
        }
    }
}

