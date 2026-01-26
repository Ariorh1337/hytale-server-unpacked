/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;
import org.bouncycastle.crypto.agreement.XDHUnifiedAgreement;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.XDHUPrivateParameters;
import org.bouncycastle.crypto.params.XDHUPublicParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.EdECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.util.Properties;

public class KeyAgreementSpi
extends BaseAgreementSpi {
    private RawAgreement agreement;
    private DHUParameterSpec dhuSpec;
    private byte[] result;

    KeyAgreementSpi(String string) {
        super(Properties.isOverrideSet("org.bouncycastle.emulate.oracle") ? "XDH" : string, null);
    }

    KeyAgreementSpi(String string, DerivationFunction derivationFunction) {
        super(Properties.isOverrideSet("org.bouncycastle.emulate.oracle") ? "XDH" : string, derivationFunction);
    }

    @Override
    protected byte[] doCalcSecret() {
        return this.result;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void doInitFromKey(Key key, AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("private XDH key required");
        }
        AsymmetricKeyParameter asymmetricKeyParameter = EdECUtil.generatePrivateKeyParameter((PrivateKey)key);
        if (asymmetricKeyParameter instanceof X25519PrivateKeyParameters) {
            this.agreement = this.getAgreement("X25519");
        } else {
            if (!(asymmetricKeyParameter instanceof X448PrivateKeyParameters)) throw new InvalidKeyException("unsupported private key type");
            this.agreement = this.getAgreement("X448");
        }
        this.ukmParameters = null;
        this.ukmParametersSalt = null;
        if (algorithmParameterSpec instanceof DHUParameterSpec) {
            if (this.kaAlgorithm.indexOf(85) < 0) {
                throw new InvalidAlgorithmParameterException("agreement algorithm not DHU based");
            }
            this.dhuSpec = (DHUParameterSpec)algorithmParameterSpec;
            this.ukmParameters = this.dhuSpec.getUserKeyingMaterial();
            this.agreement.init(new XDHUPrivateParameters(asymmetricKeyParameter, ((BCXDHPrivateKey)this.dhuSpec.getEphemeralPrivateKey()).engineGetKeyParameters(), ((BCXDHPublicKey)this.dhuSpec.getEphemeralPublicKey()).engineGetKeyParameters()));
        } else if (algorithmParameterSpec != null) {
            this.agreement.init(asymmetricKeyParameter);
            if (!(algorithmParameterSpec instanceof UserKeyingMaterialSpec)) throw new InvalidAlgorithmParameterException("unknown ParameterSpec");
            if (this.kdf == null) {
                throw new InvalidAlgorithmParameterException("no KDF specified for UserKeyingMaterialSpec");
            }
            this.ukmParameters = ((UserKeyingMaterialSpec)algorithmParameterSpec).getUserKeyingMaterial();
            this.ukmParametersSalt = ((UserKeyingMaterialSpec)algorithmParameterSpec).getSalt();
        } else {
            this.agreement.init(asymmetricKeyParameter);
        }
        if (this.kdf == null || this.ukmParameters != null) return;
        this.ukmParameters = new byte[0];
    }

    @Override
    protected Key engineDoPhase(Key key, boolean bl) throws InvalidKeyException, IllegalStateException {
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException("public XDH key required");
        }
        if (this.agreement == null) {
            throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
        }
        if (!bl) {
            throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
        }
        AsymmetricKeyParameter asymmetricKeyParameter = EdECUtil.generatePublicKeyParameter((PublicKey)key);
        this.result = new byte[this.agreement.getAgreementSize()];
        if (this.dhuSpec != null) {
            this.agreement.calculateAgreement(new XDHUPublicParameters(asymmetricKeyParameter, ((BCXDHPublicKey)this.dhuSpec.getOtherPartyEphemeralKey()).engineGetKeyParameters()), this.result, 0);
        } else {
            this.agreement.calculateAgreement(asymmetricKeyParameter, this.result, 0);
        }
        return null;
    }

    private RawAgreement getAgreement(String string) throws InvalidKeyException {
        if (!this.kaAlgorithm.equals("XDH") && !this.kaAlgorithm.startsWith(string)) {
            throw new InvalidKeyException("inappropriate key for " + this.kaAlgorithm);
        }
        if (this.kaAlgorithm.indexOf(85) > 0) {
            if (string.startsWith("X448")) {
                return new XDHUnifiedAgreement(new X448Agreement());
            }
            return new XDHUnifiedAgreement(new X25519Agreement());
        }
        if (string.startsWith("X448")) {
            return new X448Agreement();
        }
        return new X25519Agreement();
    }

    public static final class X25519
    extends KeyAgreementSpi {
        public X25519() {
            super("X25519");
        }
    }

    public static class X25519UwithSHA256CKDF
    extends KeyAgreementSpi {
        public X25519UwithSHA256CKDF() {
            super("X25519UwithSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    public static class X25519UwithSHA256KDF
    extends KeyAgreementSpi {
        public X25519UwithSHA256KDF() {
            super("X25519UwithSHA256KDF", new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }

    public static final class X25519withSHA256CKDF
    extends KeyAgreementSpi {
        public X25519withSHA256CKDF() {
            super("X25519withSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    public static final class X25519withSHA256HKDF
    extends KeyAgreementSpi {
        public X25519withSHA256HKDF() {
            super("X25519withSHA256HKDF", new HKDFBytesGenerator(DigestFactory.createSHA256()));
        }
    }

    public static final class X25519withSHA256KDF
    extends KeyAgreementSpi {
        public X25519withSHA256KDF() {
            super("X25519withSHA256KDF", new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }

    public static class X25519withSHA384CKDF
    extends KeyAgreementSpi {
        public X25519withSHA384CKDF() {
            super("X25519withSHA384CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }

    public static class X25519withSHA512CKDF
    extends KeyAgreementSpi {
        public X25519withSHA512CKDF() {
            super("X25519withSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    public static final class X448
    extends KeyAgreementSpi {
        public X448() {
            super("X448");
        }
    }

    public static class X448UwithSHA512CKDF
    extends KeyAgreementSpi {
        public X448UwithSHA512CKDF() {
            super("X448UwithSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    public static class X448UwithSHA512KDF
    extends KeyAgreementSpi {
        public X448UwithSHA512KDF() {
            super("X448UwithSHA512KDF", new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }

    public static final class X448withSHA256CKDF
    extends KeyAgreementSpi {
        public X448withSHA256CKDF() {
            super("X448withSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    public static class X448withSHA384CKDF
    extends KeyAgreementSpi {
        public X448withSHA384CKDF() {
            super("X448withSHA384CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }

    public static final class X448withSHA512CKDF
    extends KeyAgreementSpi {
        public X448withSHA512CKDF() {
            super("X448withSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    public static final class X448withSHA512HKDF
    extends KeyAgreementSpi {
        public X448withSHA512HKDF() {
            super("X448withSHA512HKDF", new HKDFBytesGenerator(DigestFactory.createSHA512()));
        }
    }

    public static final class X448withSHA512KDF
    extends KeyAgreementSpi {
        public X448withSHA512KDF() {
            super("X448withSHA512KDF", new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }

    public static final class XDH
    extends KeyAgreementSpi {
        public XDH() {
            super("XDH");
        }
    }
}

