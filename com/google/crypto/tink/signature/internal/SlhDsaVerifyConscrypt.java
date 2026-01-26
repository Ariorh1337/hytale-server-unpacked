/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.SlhDsaParameters;
import com.google.crypto.tink.signature.SlhDsaPublicKey;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;

@Immutable
public class SlhDsaVerifyConscrypt
implements PublicKeyVerify {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    static final int SLH_DSA_SHA2_128S_SIG_LENGTH = 7856;
    static final String SLH_DSA_SHA2_128S_ALGORITHM = "SLH-DSA-SHA2-128S";
    private final byte[] outputPrefix;
    private final PublicKey publicKey;
    private final String algorithm;
    private final int signatureLength;
    private final Provider provider;

    public SlhDsaVerifyConscrypt(byte[] outputPrefix, PublicKey publicKey, String algorithm, int signatureLength, Provider provider) {
        this.outputPrefix = outputPrefix;
        this.publicKey = publicKey;
        this.algorithm = algorithm;
        this.signatureLength = signatureLength;
        this.provider = provider;
    }

    @AccessesPartialKey
    public static PublicKeyVerify createWithProvider(SlhDsaPublicKey slhDsaPublicKey, Provider provider) throws GeneralSecurityException {
        if (provider == null) {
            throw new NullPointerException("provider must not be null");
        }
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use SLH-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
        }
        SlhDsaParameters parameters = slhDsaPublicKey.getParameters();
        if (parameters.getPrivateKeySize() != 64 || parameters.getHashType() != SlhDsaParameters.HashType.SHA2 || parameters.getSignatureType() != SlhDsaParameters.SignatureType.SMALL_SIGNATURE) {
            throw new GeneralSecurityException("Unsupported SLH-DSA parameters");
        }
        PublicKey publicKey = KeyFactory.getInstance(SLH_DSA_SHA2_128S_ALGORITHM, provider).generatePublic(new RawKeySpec(slhDsaPublicKey.getSerializedPublicKey().toByteArray()));
        return new SlhDsaVerifyConscrypt(slhDsaPublicKey.getOutputPrefix().toByteArray(), publicKey, SLH_DSA_SHA2_128S_ALGORITHM, 7856, provider);
    }

    @AccessesPartialKey
    public static PublicKeyVerify create(SlhDsaPublicKey slhDsaPublicKey) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use SLH-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
        }
        Provider provider = ConscryptUtil.providerOrNull();
        if (provider == null) {
            throw new GeneralSecurityException("Obtaining Conscrypt provider failed");
        }
        return SlhDsaVerifyConscrypt.createWithProvider(slhDsaPublicKey, provider);
    }

    @Override
    public void verify(byte[] signature, byte[] data) throws GeneralSecurityException {
        if (!Util.isPrefix(this.outputPrefix, signature)) {
            throw new GeneralSecurityException("Invalid signature (output prefix mismatch)");
        }
        if (signature.length != this.outputPrefix.length + this.signatureLength) {
            throw new GeneralSecurityException("Invalid signature length");
        }
        Signature verifier = Signature.getInstance(this.algorithm, this.provider);
        verifier.initVerify(this.publicKey);
        verifier.update(data);
        if (!verifier.verify(signature, this.outputPrefix.length, this.signatureLength)) {
            throw new GeneralSecurityException("Invalid signature");
        }
    }

    public static boolean isSupported() {
        if (!FIPS.isCompatible()) {
            return false;
        }
        Provider provider = ConscryptUtil.providerOrNull();
        if (provider == null) {
            return false;
        }
        try {
            KeyFactory unusedKeyFactory = KeyFactory.getInstance(SLH_DSA_SHA2_128S_ALGORITHM, provider);
            Signature unusedSignature = Signature.getInstance(SLH_DSA_SHA2_128S_ALGORITHM, provider);
            return true;
        }
        catch (GeneralSecurityException e) {
            return false;
        }
    }

    public static final class RawKeySpec
    extends EncodedKeySpec {
        public RawKeySpec(byte[] encoded) {
            super(encoded);
        }

        @Override
        public String getFormat() {
            return "raw";
        }
    }
}

