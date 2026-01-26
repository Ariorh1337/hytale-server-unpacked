/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.Ed25519;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.Ed25519Parameters;
import com.google.crypto.tink.signature.Ed25519PublicKey;
import com.google.crypto.tink.signature.internal.Ed25519VerifyJce;
import com.google.crypto.tink.subtle.Bytes;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Arrays;

@Immutable
public final class Ed25519Verify
implements PublicKeyVerify {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    public static final int PUBLIC_KEY_LEN = 32;
    public static final int SIGNATURE_LEN = 64;
    private final com.google.crypto.tink.util.Bytes publicKey;
    private final byte[] outputPrefix;
    private final byte[] messageSuffix;

    @AccessesPartialKey
    public static PublicKeyVerify create(Ed25519PublicKey key) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use Ed25519 in FIPS-mode.");
        }
        try {
            return Ed25519VerifyJce.create(key);
        }
        catch (GeneralSecurityException generalSecurityException) {
            byte[] byArray;
            byte[] byArray2 = key.getPublicKeyBytes().toByteArray();
            byte[] byArray3 = key.getOutputPrefix().toByteArray();
            if (key.getParameters().getVariant().equals(Ed25519Parameters.Variant.LEGACY)) {
                byte[] byArray4 = new byte[1];
                byArray = byArray4;
                byArray4[0] = 0;
            } else {
                byArray = new byte[]{};
            }
            return new Ed25519Verify(byArray2, byArray3, byArray);
        }
    }

    public Ed25519Verify(byte[] publicKey) {
        this(publicKey, new byte[0], new byte[0]);
    }

    private Ed25519Verify(byte[] publicKey, byte[] outputPrefix, byte[] messageSuffix) {
        if (!FIPS.isCompatible()) {
            throw new IllegalStateException(new GeneralSecurityException("Can not use Ed25519 in FIPS-mode."));
        }
        if (publicKey.length != 32) {
            throw new IllegalArgumentException(String.format("Given public key's length is not %s.", 32));
        }
        this.publicKey = com.google.crypto.tink.util.Bytes.copyFrom(publicKey);
        this.outputPrefix = outputPrefix;
        this.messageSuffix = messageSuffix;
        Ed25519.init();
    }

    private void noPrefixVerify(byte[] signature, byte[] data) throws GeneralSecurityException {
        if (signature.length != 64) {
            throw new GeneralSecurityException(String.format("The length of the signature is not %s.", 64));
        }
        if (!Ed25519.verify(data, signature, this.publicKey.toByteArray())) {
            throw new GeneralSecurityException("Signature check failed.");
        }
    }

    @Override
    public void verify(byte[] signature, byte[] data) throws GeneralSecurityException {
        if (this.outputPrefix.length == 0 && this.messageSuffix.length == 0) {
            this.noPrefixVerify(signature, data);
            return;
        }
        if (!Util.isPrefix(this.outputPrefix, signature)) {
            throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
        }
        byte[] dataCopy = data;
        if (this.messageSuffix.length != 0) {
            dataCopy = Bytes.concat(data, this.messageSuffix);
        }
        byte[] signatureNoPrefix = Arrays.copyOfRange(signature, this.outputPrefix.length, signature.length);
        this.noPrefixVerify(signatureNoPrefix, dataCopy);
    }
}

