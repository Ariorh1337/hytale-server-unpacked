/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.EngineFactory;
import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class Hkdf {
    public static byte[] computeHkdf(String macAlgorithm, byte[] ikm, byte[] salt, byte[] info, int size) throws GeneralSecurityException {
        Mac mac = EngineFactory.MAC.getInstance(macAlgorithm);
        if (size > 255 * mac.getMacLength()) {
            throw new GeneralSecurityException("size too large");
        }
        if (salt == null || salt.length == 0) {
            mac.init(new SecretKeySpec(new byte[mac.getMacLength()], macAlgorithm));
        } else {
            mac.init(new SecretKeySpec(salt, macAlgorithm));
        }
        byte[] prk = mac.doFinal(ikm);
        byte[] result = new byte[size];
        int ctr = 1;
        int pos = 0;
        mac.init(new SecretKeySpec(prk, macAlgorithm));
        byte[] digest = new byte[]{};
        while (true) {
            mac.update(digest);
            mac.update(info);
            mac.update((byte)ctr);
            digest = mac.doFinal();
            if (pos + digest.length >= size) break;
            System.arraycopy(digest, 0, result, pos, digest.length);
            pos += digest.length;
            ++ctr;
        }
        System.arraycopy(digest, 0, result, pos, size - pos);
        return result;
    }

    public static byte[] computeEciesHkdfSymmetricKey(byte[] ephemeralPublicKeyBytes, byte[] sharedSecret, String hmacAlgo, byte[] hkdfSalt, byte[] hkdfInfo, int keySizeInBytes) throws GeneralSecurityException {
        byte[] hkdfInput = Bytes.concat(ephemeralPublicKeyBytes, sharedSecret);
        return Hkdf.computeHkdf(hmacAlgo, hkdfInput, hkdfSalt, hkdfInfo, keySizeInBytes);
    }

    private Hkdf() {
    }
}

