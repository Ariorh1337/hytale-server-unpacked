/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.hybrid.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.hybrid.HpkeParameters;
import com.google.crypto.tink.hybrid.HpkePrivateKey;
import com.google.crypto.tink.hybrid.internal.HpkeAead;
import com.google.crypto.tink.hybrid.internal.HpkeContext;
import com.google.crypto.tink.hybrid.internal.HpkeKdf;
import com.google.crypto.tink.hybrid.internal.HpkeKem;
import com.google.crypto.tink.hybrid.internal.HpkeKemPrivateKey;
import com.google.crypto.tink.hybrid.internal.HpkePrimitiveFactory;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Immutable
public final class HpkeDecrypt
implements HybridDecrypt {
    private static final byte[] EMPTY_ASSOCIATED_DATA = new byte[0];
    private final HpkeKemPrivateKey recipientPrivateKey;
    private final HpkeKem kem;
    private final HpkeKdf kdf;
    private final HpkeAead aead;
    private final int encapsulatedKeyLength;
    private final byte[] outputPrefix;

    private HpkeDecrypt(HpkeKemPrivateKey recipientPrivateKey, HpkeKem kem, HpkeKdf kdf, HpkeAead aead, int encapsulatedKeyLength, Bytes outputPrefix) {
        this.recipientPrivateKey = recipientPrivateKey;
        this.kem = kem;
        this.kdf = kdf;
        this.aead = aead;
        this.encapsulatedKeyLength = encapsulatedKeyLength;
        this.outputPrefix = outputPrefix.toByteArray();
    }

    private static int encodingSizeInBytes(HpkeParameters.KemId kemId) throws GeneralSecurityException {
        if (kemId.equals(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256)) {
            return 32;
        }
        if (kemId.equals(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256)) {
            return 65;
        }
        if (kemId.equals(HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384)) {
            return 97;
        }
        if (kemId.equals(HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)) {
            return 133;
        }
        throw new GeneralSecurityException("Unrecognized HPKE KEM identifier");
    }

    @AccessesPartialKey
    private static HpkeKemPrivateKey createHpkeKemPrivateKey(HpkePrivateKey privateKey) throws GeneralSecurityException {
        HpkeParameters.KemId kemId = privateKey.getParameters().getKemId();
        if (kemId.equals(HpkeParameters.KemId.DHKEM_X25519_HKDF_SHA256) || kemId.equals(HpkeParameters.KemId.DHKEM_P256_HKDF_SHA256) || kemId.equals(HpkeParameters.KemId.DHKEM_P384_HKDF_SHA384) || kemId.equals(HpkeParameters.KemId.DHKEM_P521_HKDF_SHA512)) {
            Bytes convertedPrivateKeyBytes = Bytes.copyFrom(privateKey.getPrivateKeyBytes().toByteArray(InsecureSecretKeyAccess.get()));
            return new HpkeKemPrivateKey(convertedPrivateKeyBytes, privateKey.getPublicKey().getPublicKeyBytes());
        }
        throw new GeneralSecurityException("Unrecognized HPKE KEM identifier");
    }

    public static HybridDecrypt create(HpkePrivateKey privateKey) throws GeneralSecurityException {
        HpkeParameters parameters = privateKey.getParameters();
        HpkeKem kem = HpkePrimitiveFactory.createKem(parameters.getKemId());
        HpkeKdf kdf = HpkePrimitiveFactory.createKdf(parameters.getKdfId());
        HpkeAead aead = HpkePrimitiveFactory.createAead(parameters.getAeadId());
        int encapsulatedKeyLength = HpkeDecrypt.encodingSizeInBytes(parameters.getKemId());
        HpkeKemPrivateKey recipientKemPrivateKey = HpkeDecrypt.createHpkeKemPrivateKey(privateKey);
        return new HpkeDecrypt(recipientKemPrivateKey, kem, kdf, aead, encapsulatedKeyLength, privateKey.getOutputPrefix());
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] contextInfo) throws GeneralSecurityException {
        int prefixAndHeaderLength = this.outputPrefix.length + this.encapsulatedKeyLength;
        if (ciphertext.length < prefixAndHeaderLength) {
            throw new GeneralSecurityException("Ciphertext is too short.");
        }
        if (!Util.isPrefix(this.outputPrefix, ciphertext)) {
            throw new GeneralSecurityException("Invalid ciphertext (output prefix mismatch)");
        }
        byte[] info = contextInfo;
        if (info == null) {
            info = new byte[]{};
        }
        byte[] encapsulatedKey = Arrays.copyOfRange(ciphertext, this.outputPrefix.length, prefixAndHeaderLength);
        HpkeContext context = HpkeContext.createRecipientContext(encapsulatedKey, this.recipientPrivateKey, this.kem, this.kdf, this.aead, info);
        return context.open(ciphertext, prefixAndHeaderLength, EMPTY_ASSOCIATED_DATA);
    }
}

