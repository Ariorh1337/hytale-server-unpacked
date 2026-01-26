/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.spec.CompositeSignatureSpec;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.encoders.Hex;

public class SignatureSpi
extends java.security.SignatureSpi {
    private static final byte[] prefix = Hex.decode("436f6d706f73697465416c676f726974686d5369676e61747572657332303235");
    private static final Map<String, String> canonicalNames = new HashMap<String, String>();
    private static final HashMap<ASN1ObjectIdentifier, byte[]> domainSeparators = new LinkedHashMap<ASN1ObjectIdentifier, byte[]>();
    private static final HashMap<ASN1ObjectIdentifier, AlgorithmParameterSpec> algorithmsParameterSpecs = new HashMap();
    private static final String ML_DSA_44 = "ML-DSA-44";
    private static final String ML_DSA_65 = "ML-DSA-65";
    private static final String ML_DSA_87 = "ML-DSA-87";
    private final SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    private Key compositeKey;
    private final boolean isPrehash;
    private ASN1ObjectIdentifier algorithm;
    private String[] algs;
    private Signature[] componentSignatures;
    private byte[] domain;
    private Digest baseDigest;
    private JcaJceHelper helper = new BCJcaJceHelper();
    private Digest preHashDigest;
    private ContextParameterSpec contextSpec;
    private AlgorithmParameters engineParams = null;
    private boolean unprimed = true;

    SignatureSpi(ASN1ObjectIdentifier aSN1ObjectIdentifier, Digest digest) {
        this(aSN1ObjectIdentifier, digest, false);
    }

    SignatureSpi(ASN1ObjectIdentifier aSN1ObjectIdentifier, Digest digest, boolean bl) {
        this.algorithm = aSN1ObjectIdentifier;
        this.isPrehash = bl;
        if (aSN1ObjectIdentifier != null) {
            this.baseDigest = digest;
            this.preHashDigest = bl ? new NullDigest(digest.getDigestSize()) : digest;
            this.domain = domainSeparators.get(aSN1ObjectIdentifier);
            this.algs = CompositeIndex.getPairing(aSN1ObjectIdentifier);
            this.componentSignatures = new Signature[this.algs.length];
        }
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        if (!(publicKey instanceof CompositePublicKey)) {
            throw new InvalidKeyException("public key is not composite");
        }
        this.compositeKey = publicKey;
        CompositePublicKey compositePublicKey = (CompositePublicKey)this.compositeKey;
        if (this.algorithm != null) {
            if (!compositePublicKey.getAlgorithmIdentifier().getAlgorithm().equals(this.algorithm)) {
                throw new InvalidKeyException("provided composite public key cannot be used with the composite signature algorithm");
            }
        } else {
            ASN1ObjectIdentifier aSN1ObjectIdentifier;
            this.algorithm = aSN1ObjectIdentifier = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm().getAlgorithm();
            this.baseDigest = CompositeIndex.getDigest(aSN1ObjectIdentifier);
            this.preHashDigest = this.isPrehash ? new NullDigest(this.baseDigest.getDigestSize()) : this.baseDigest;
            this.domain = domainSeparators.get(aSN1ObjectIdentifier);
            this.algs = CompositeIndex.getPairing(aSN1ObjectIdentifier);
            this.componentSignatures = new Signature[this.algs.length];
        }
        this.createComponentSignatures(compositePublicKey.getPublicKeys(), compositePublicKey.getProviders());
        this.sigInitVerify();
    }

    private void sigInitVerify() throws InvalidKeyException {
        CompositePublicKey compositePublicKey = (CompositePublicKey)this.compositeKey;
        for (int i = 0; i < this.componentSignatures.length; ++i) {
            this.componentSignatures[i].initVerify(compositePublicKey.getPublicKeys().get(i));
        }
        this.unprimed = true;
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        if (!(privateKey instanceof CompositePrivateKey)) {
            throw new InvalidKeyException("Private key is not composite.");
        }
        this.compositeKey = privateKey;
        CompositePrivateKey compositePrivateKey = (CompositePrivateKey)privateKey;
        if (this.algorithm != null) {
            if (!compositePrivateKey.getAlgorithmIdentifier().getAlgorithm().equals(this.algorithm)) {
                throw new InvalidKeyException("provided composite public key cannot be used with the composite signature algorithm");
            }
        } else {
            ASN1ObjectIdentifier aSN1ObjectIdentifier;
            this.algorithm = aSN1ObjectIdentifier = compositePrivateKey.getAlgorithmIdentifier().getAlgorithm();
            this.baseDigest = CompositeIndex.getDigest(aSN1ObjectIdentifier);
            this.preHashDigest = this.isPrehash ? new NullDigest(this.baseDigest.getDigestSize()) : this.baseDigest;
            this.domain = domainSeparators.get(aSN1ObjectIdentifier);
            this.algs = CompositeIndex.getPairing(aSN1ObjectIdentifier);
            this.componentSignatures = new Signature[this.algs.length];
        }
        this.createComponentSignatures(compositePrivateKey.getPrivateKeys(), compositePrivateKey.getProviders());
        this.sigInitSign();
    }

    private void createComponentSignatures(List list, List<Provider> list2) {
        try {
            if (list2 == null) {
                for (int i = 0; i != this.componentSignatures.length; ++i) {
                    this.componentSignatures[i] = this.getDefaultSignature(this.algs[i], list.get(i));
                }
            } else {
                for (int i = 0; i != this.componentSignatures.length; ++i) {
                    Provider provider = list2.get(i);
                    this.componentSignatures[i] = provider == null ? this.getDefaultSignature(this.algs[i], list.get(i)) : Signature.getInstance(this.algs[i], list2.get(i));
                }
            }
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw Exceptions.illegalStateException(generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    private Signature getDefaultSignature(String string, Object object) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (object instanceof BCKey) {
            return this.helper.createSignature(string);
        }
        return Signature.getInstance(string);
    }

    private void sigInitSign() throws InvalidKeyException {
        CompositePrivateKey compositePrivateKey = (CompositePrivateKey)this.compositeKey;
        for (int i = 0; i < this.componentSignatures.length; ++i) {
            this.componentSignatures[i].initSign(compositePrivateKey.getPrivateKeys().get(i));
        }
        this.unprimed = true;
    }

    private void baseSigInit() throws SignatureException {
        try {
            this.componentSignatures[0].setParameter(new ContextParameterSpec(this.domain));
            AlgorithmParameterSpec algorithmParameterSpec = algorithmsParameterSpecs.get(this.algorithm);
            if (algorithmParameterSpec != null) {
                this.componentSignatures[1].setParameter(algorithmParameterSpec);
            }
        }
        catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
            throw new IllegalStateException("unable to set context on ML-DSA");
        }
        this.unprimed = false;
    }

    @Override
    protected void engineUpdate(byte by) throws SignatureException {
        if (this.unprimed) {
            this.baseSigInit();
        }
        if (this.preHashDigest != null) {
            this.preHashDigest.update(by);
        } else {
            for (int i = 0; i < this.componentSignatures.length; ++i) {
                Signature signature = this.componentSignatures[i];
                signature.update(by);
            }
        }
    }

    @Override
    protected void engineUpdate(byte[] byArray, int n, int n2) throws SignatureException {
        if (this.unprimed) {
            this.baseSigInit();
        }
        if (this.preHashDigest != null) {
            this.preHashDigest.update(byArray, n, n2);
        } else {
            for (int i = 0; i < this.componentSignatures.length; ++i) {
                Signature signature = this.componentSignatures[i];
                signature.update(byArray, n, n2);
            }
        }
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        byte[] byArray = new byte[32];
        this.random.nextBytes(byArray);
        if (this.preHashDigest != null) {
            this.processPreHashedMessage(null);
        }
        byte[] byArray2 = this.componentSignatures[0].sign();
        byte[] byArray3 = this.componentSignatures[1].sign();
        byte[] byArray4 = new byte[byArray2.length + byArray3.length];
        System.arraycopy(byArray2, 0, byArray4, 0, byArray2.length);
        System.arraycopy(byArray3, 0, byArray4, byArray2.length, byArray3.length);
        return byArray4;
    }

    private void processPreHashedMessage(byte[] byArray) throws SignatureException {
        byte[] byArray2 = new byte[this.baseDigest.getDigestSize()];
        try {
            this.preHashDigest.doFinal(byArray2, 0);
        }
        catch (IllegalStateException illegalStateException) {
            throw new SignatureException(illegalStateException.getMessage());
        }
        for (int i = 0; i < this.componentSignatures.length; ++i) {
            Signature signature = this.componentSignatures[i];
            signature.update(prefix);
            signature.update(this.domain);
            if (this.contextSpec == null) {
                signature.update((byte)0);
            } else {
                byte[] byArray3 = this.contextSpec.getContext();
                signature.update((byte)byArray3.length);
                signature.update(byArray3);
            }
            if (byArray != null) {
                signature.update(byArray, 0, byArray.length);
            }
            signature.update(byArray2, 0, byArray2.length);
        }
    }

    public static byte[][] splitCompositeSignature(byte[] byArray, int n) {
        byte[] byArray2 = new byte[n];
        byte[] byArray3 = new byte[byArray.length - n];
        System.arraycopy(byArray, 0, byArray2, 0, n);
        System.arraycopy(byArray, n, byArray3, 0, byArray3.length);
        return new byte[][]{byArray2, byArray3};
    }

    @Override
    protected boolean engineVerify(byte[] byArray) throws SignatureException {
        int n = 0;
        if (this.algs[0].indexOf("44") > 0) {
            n = 2420;
        } else if (this.algs[0].indexOf("65") > 0) {
            n = 3309;
        } else if (this.algs[0].indexOf("87") > 0) {
            n = 4627;
        }
        byte[][] byArray2 = SignatureSpi.splitCompositeSignature(byArray, n);
        if (this.preHashDigest != null) {
            this.processPreHashedMessage(null);
        }
        boolean bl = false;
        for (int i = 0; i < this.componentSignatures.length; ++i) {
            if (this.componentSignatures[i].verify(byArray2[i])) continue;
            bl = true;
        }
        return !bl;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void engineSetParameter(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (!this.unprimed) {
            throw new InvalidAlgorithmParameterException("attempt to set parameter after update");
        }
        if (algorithmParameterSpec instanceof ContextParameterSpec) {
            this.contextSpec = (ContextParameterSpec)algorithmParameterSpec;
            try {
                if (this.compositeKey instanceof PublicKey) {
                    this.sigInitVerify();
                    return;
                }
                this.sigInitSign();
                return;
            }
            catch (InvalidKeyException invalidKeyException) {
                throw new InvalidAlgorithmParameterException("keys invalid on reset: " + invalidKeyException.getMessage(), invalidKeyException);
            }
        }
        if (algorithmParameterSpec instanceof CompositeSignatureSpec) {
            CompositeSignatureSpec compositeSignatureSpec = (CompositeSignatureSpec)algorithmParameterSpec;
            this.preHashDigest = compositeSignatureSpec.isPrehashMode() ? new NullDigest(this.baseDigest.getDigestSize()) : this.baseDigest;
            AlgorithmParameterSpec algorithmParameterSpec2 = compositeSignatureSpec.getSecondarySpec();
            if (algorithmParameterSpec2 == null || algorithmParameterSpec2 instanceof ContextParameterSpec) {
                this.contextSpec = (ContextParameterSpec)compositeSignatureSpec.getSecondarySpec();
                return;
            } else {
                byte[] byArray = SpecUtil.getContextFrom(algorithmParameterSpec2);
                if (byArray == null) throw new InvalidAlgorithmParameterException("unknown parameterSpec passed to composite signature");
                this.contextSpec = new ContextParameterSpec(byArray);
            }
            return;
        }
        byte[] byArray = SpecUtil.getContextFrom(algorithmParameterSpec);
        if (byArray == null) throw new InvalidAlgorithmParameterException("unknown parameterSpec passed to composite signature");
        this.contextSpec = new ContextParameterSpec(byArray);
        try {
            if (this.compositeKey instanceof PublicKey) {
                this.sigInitVerify();
                throw new InvalidAlgorithmParameterException("unknown parameterSpec passed to composite signature");
            } else {
                this.sigInitSign();
            }
            throw new InvalidAlgorithmParameterException("unknown parameterSpec passed to composite signature");
        }
        catch (InvalidKeyException invalidKeyException) {
            throw new InvalidAlgorithmParameterException("keys invalid on reset: " + invalidKeyException.getMessage(), invalidKeyException);
        }
    }

    private String getCanonicalName(String string) {
        String string2 = canonicalNames.get(string);
        if (string2 != null) {
            return string2;
        }
        return string;
    }

    @Override
    protected void engineSetParameter(String string, Object object) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected Object engineGetParameter(String string) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    @Override
    protected final AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.contextSpec != null) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("CONTEXT");
                this.engineParams.init(this.contextSpec);
            }
            catch (Exception exception) {
                throw Exceptions.illegalStateException(exception.toString(), exception);
            }
        }
        return this.engineParams;
    }

    static {
        canonicalNames.put("MLDSA44", ML_DSA_44);
        canonicalNames.put("MLDSA65", ML_DSA_65);
        canonicalNames.put("MLDSA87", ML_DSA_87);
        canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_44.getId(), ML_DSA_44);
        canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_65.getId(), ML_DSA_65);
        canonicalNames.put(NISTObjectIdentifiers.id_ml_dsa_87.getId(), ML_DSA_87);
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, Hex.decode("434f4d505349472d4d4c44534134342d525341323034382d5053532d534841323536"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, Hex.decode("434f4d505349472d4d4c44534134342d525341323034382d504b435331352d534841323536"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, Hex.decode("434f4d505349472d4d4c44534134342d456432353531392d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, Hex.decode("434f4d505349472d4d4c44534134342d45434453412d503235362d534841323536"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341333037322d5053532d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341333037322d504b435331352d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341343039362d5053532d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d525341343039362d504b435331352d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d45434453412d503235362d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d45434453412d503338342d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d45434453412d42503235362d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, Hex.decode("434f4d505349472d4d4c44534136352d456432353531392d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d45434453412d42503338342d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, Hex.decode("434f4d505349472d4d4c44534138372d45643434382d5348414b45323536"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d525341333037322d5053532d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d525341343039362d5053532d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d45434453412d503338342d534841353132"));
        domainSeparators.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, Hex.decode("434f4d505349472d4d4c44534138372d45434453412d503532312d534841353132"));
        algorithmsParameterSpecs.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1));
        algorithmsParameterSpecs.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1));
        algorithmsParameterSpecs.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
        algorithmsParameterSpecs.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
        algorithmsParameterSpecs.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 32, 1));
    }

    public static final class COMPOSITE
    extends SignatureSpi {
        public COMPOSITE() {
            super(null, null, false);
        }
    }

    private static final class ErasableOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuf() {
            return this.buf;
        }
    }

    public static final class MLDSA44_ECDSA_P256_SHA256
    extends SignatureSpi {
        public MLDSA44_ECDSA_P256_SHA256() {
            super(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new SHA256Digest());
        }
    }

    public static final class MLDSA44_ECDSA_P256_SHA256_PREHASH
    extends SignatureSpi {
        public MLDSA44_ECDSA_P256_SHA256_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new SHA256Digest(), true);
        }
    }

    public static final class MLDSA44_Ed25519_SHA512
    extends SignatureSpi {
        public MLDSA44_Ed25519_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA44_Ed25519_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA44_Ed25519_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA44_RSA2048_PKCS15_SHA256
    extends SignatureSpi {
        public MLDSA44_RSA2048_PKCS15_SHA256() {
            super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new SHA256Digest());
        }
    }

    public static final class MLDSA44_RSA2048_PKCS15_SHA256_PREHASH
    extends SignatureSpi {
        public MLDSA44_RSA2048_PKCS15_SHA256_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new SHA256Digest(), true);
        }
    }

    public static final class MLDSA44_RSA2048_PSS_SHA256
    extends SignatureSpi {
        public MLDSA44_RSA2048_PSS_SHA256() {
            super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new SHA256Digest());
        }
    }

    public static final class MLDSA44_RSA2048_PSS_SHA256_PREHASH
    extends SignatureSpi {
        public MLDSA44_RSA2048_PSS_SHA256_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new SHA256Digest(), true);
        }
    }

    public static final class MLDSA65_ECDSA_P256_SHA512
    extends SignatureSpi {
        public MLDSA65_ECDSA_P256_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_ECDSA_P256_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_ECDSA_P256_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA65_ECDSA_P384_SHA512
    extends SignatureSpi {
        public MLDSA65_ECDSA_P384_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_ECDSA_P384_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_ECDSA_P384_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA65_ECDSA_brainpoolP256r1_SHA512
    extends SignatureSpi {
        public MLDSA65_ECDSA_brainpoolP256r1_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_ECDSA_brainpoolP256r1_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_ECDSA_brainpoolP256r1_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA65_Ed25519_SHA512
    extends SignatureSpi {
        public MLDSA65_Ed25519_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_Ed25519_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_Ed25519_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA65_RSA3072_PKCS15_SHA512
    extends SignatureSpi {
        public MLDSA65_RSA3072_PKCS15_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_RSA3072_PKCS15_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_RSA3072_PKCS15_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA65_RSA3072_PSS_SHA512
    extends SignatureSpi {
        public MLDSA65_RSA3072_PSS_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_RSA3072_PSS_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_RSA3072_PSS_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA65_RSA4096_PKCS15_SHA512
    extends SignatureSpi {
        public MLDSA65_RSA4096_PKCS15_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_RSA4096_PKCS15_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_RSA4096_PKCS15_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA65_RSA4096_PSS_SHA512
    extends SignatureSpi {
        public MLDSA65_RSA4096_PSS_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA65_RSA4096_PSS_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA65_RSA4096_PSS_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA87_ECDSA_P384_SHA512
    extends SignatureSpi {
        public MLDSA87_ECDSA_P384_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA87_ECDSA_P384_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA87_ECDSA_P384_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA87_ECDSA_P521_SHA512
    extends SignatureSpi {
        public MLDSA87_ECDSA_P521_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA87_ECDSA_P521_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA87_ECDSA_P521_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA87_ECDSA_brainpoolP384r1_SHA512
    extends SignatureSpi {
        public MLDSA87_ECDSA_brainpoolP384r1_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA87_ECDSA_brainpoolP384r1_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA87_ECDSA_brainpoolP384r1_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA87_Ed448_SHAKE256
    extends SignatureSpi {
        public MLDSA87_Ed448_SHAKE256() {
            super(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new SHAKEDigest(256));
        }
    }

    public static final class MLDSA87_Ed448_SHAKE256_PREHASH
    extends SignatureSpi {
        public MLDSA87_Ed448_SHAKE256_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new SHAKEDigest(256), true);
        }
    }

    public static final class MLDSA87_RSA3072_PSS_SHA512
    extends SignatureSpi {
        public MLDSA87_RSA3072_PSS_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA87_RSA3072_PSS_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA87_RSA3072_PSS_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new SHA512Digest(), true);
        }
    }

    public static final class MLDSA87_RSA4096_PSS_SHA512
    extends SignatureSpi {
        public MLDSA87_RSA4096_PSS_SHA512() {
            super(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new SHA512Digest());
        }
    }

    public static final class MLDSA87_RSA4096_PSS_SHA512_PREHASH
    extends SignatureSpi {
        public MLDSA87_RSA4096_PSS_SHA512_PREHASH() {
            super(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new SHA512Digest(), true);
        }
    }

    private static class NullDigest
    implements Digest {
        private final int expectedSize;
        private final OpenByteArrayOutputStream bOut = new OpenByteArrayOutputStream();

        NullDigest(int n) {
            this.expectedSize = n;
        }

        @Override
        public String getAlgorithmName() {
            return "NULL";
        }

        @Override
        public int getDigestSize() {
            return this.bOut.size();
        }

        @Override
        public void update(byte by) {
            this.bOut.write(by);
        }

        @Override
        public void update(byte[] byArray, int n, int n2) {
            this.bOut.write(byArray, n, n2);
        }

        @Override
        public int doFinal(byte[] byArray, int n) {
            int n2 = this.bOut.size();
            if (n2 != this.expectedSize) {
                throw new IllegalStateException("provided pre-hash digest is the wrong length");
            }
            this.bOut.copy(byArray, n);
            this.reset();
            return n2;
        }

        @Override
        public void reset() {
            this.bOut.reset();
        }

        private static class OpenByteArrayOutputStream
        extends ByteArrayOutputStream {
            private OpenByteArrayOutputStream() {
            }

            @Override
            public void reset() {
                super.reset();
                Arrays.clear(this.buf);
            }

            void copy(byte[] byArray, int n) {
                System.arraycopy(this.buf, 0, byArray, n, this.size());
            }
        }
    }
}

