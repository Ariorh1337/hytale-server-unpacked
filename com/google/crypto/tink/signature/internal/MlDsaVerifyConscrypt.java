/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.config.internal.TinkFipsUtil;
import com.google.crypto.tink.internal.ConscryptUtil;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.signature.MlDsaParameters;
import com.google.crypto.tink.signature.MlDsaPublicKey;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;

@Immutable
public final class MlDsaVerifyConscrypt
implements PublicKeyVerify {
    public static final TinkFipsUtil.AlgorithmFipsCompatibility FIPS = TinkFipsUtil.AlgorithmFipsCompatibility.ALGORITHM_NOT_FIPS;
    static final int ML_DSA_65_SIG_LENGTH = 3309;
    static final String ML_DSA_65_ALGORITHM = "ML-DSA-65";
    private final byte[] outputPrefix;
    private final PublicKey publicKey;
    private final String algorithm;
    private final int signatureLength;
    private final Provider provider;

    private MlDsaVerifyConscrypt(byte[] outputPrefix, PublicKey publicKey, String algorithm, int signatureLength, Provider provider) {
        this.outputPrefix = outputPrefix;
        this.publicKey = publicKey;
        this.algorithm = algorithm;
        this.signatureLength = signatureLength;
        this.provider = provider;
    }

    @AccessesPartialKey
    public static PublicKeyVerify createWithProvider(MlDsaPublicKey mlDsaPublicKey, Provider provider) throws GeneralSecurityException {
        if (provider == null) {
            throw new NullPointerException("provider must not be null");
        }
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use ML-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
        }
        MlDsaParameters.MlDsaInstance mlDsaInstance = mlDsaPublicKey.getParameters().getMlDsaInstance();
        if (mlDsaInstance != MlDsaParameters.MlDsaInstance.ML_DSA_65) {
            throw new GeneralSecurityException("Only ML-DSA-65 currently supported");
        }
        PublicKey publicKey = KeyFactory.getInstance(ML_DSA_65_ALGORITHM, provider).generatePublic(new RawKeySpec(mlDsaPublicKey.getSerializedPublicKey().toByteArray()));
        return new MlDsaVerifyConscrypt(mlDsaPublicKey.getOutputPrefix().toByteArray(), publicKey, ML_DSA_65_ALGORITHM, 3309, provider);
    }

    @AccessesPartialKey
    public static PublicKeyVerify create(MlDsaPublicKey mlDsaPublicKey) throws GeneralSecurityException {
        if (!FIPS.isCompatible()) {
            throw new GeneralSecurityException("Can not use ML-DSA in FIPS-mode, as it is not yet certified in Conscrypt.");
        }
        Provider provider = ConscryptUtil.providerOrNull();
        if (provider == null) {
            throw new GeneralSecurityException("Obtaining Conscrypt provider failed");
        }
        return MlDsaVerifyConscrypt.createWithProvider(mlDsaPublicKey, provider);
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
            KeyFactory unusedKeyFactory = KeyFactory.getInstance(ML_DSA_65_ALGORITHM, provider);
            Signature unusedSignature = Signature.getInstance(ML_DSA_65_ALGORITHM, provider);
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

