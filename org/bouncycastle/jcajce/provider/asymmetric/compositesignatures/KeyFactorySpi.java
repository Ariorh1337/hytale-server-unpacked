/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

public class KeyFactorySpi
extends BaseKeyFactorySpi
implements AsymmetricKeyInfoConverter {
    private static final AlgorithmIdentifier mlDsa44 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_44);
    private static final AlgorithmIdentifier mlDsa65 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_65);
    private static final AlgorithmIdentifier mlDsa87 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_87);
    private static final AlgorithmIdentifier falcon512Identifier = new AlgorithmIdentifier(BCObjectIdentifiers.falcon_512);
    private static final AlgorithmIdentifier ed25519 = new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519);
    private static final AlgorithmIdentifier ecDsaP256 = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(SECObjectIdentifiers.secp256r1));
    private static final AlgorithmIdentifier ecDsaBrainpoolP256r1 = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(TeleTrusTObjectIdentifiers.brainpoolP256r1));
    private static final AlgorithmIdentifier rsa = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption);
    private static final AlgorithmIdentifier ed448 = new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448);
    private static final AlgorithmIdentifier ecDsaP384 = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(SECObjectIdentifiers.secp384r1));
    private static final AlgorithmIdentifier ecDsaP521 = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(SECObjectIdentifiers.secp521r1));
    private static final AlgorithmIdentifier ecDsaBrainpoolP384r1 = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(TeleTrusTObjectIdentifiers.brainpoolP384r1));
    private static Map<ASN1ObjectIdentifier, AlgorithmIdentifier[]> pairings = new HashMap<ASN1ObjectIdentifier, AlgorithmIdentifier[]>();
    private static Map<ASN1ObjectIdentifier, int[]> componentKeySizes = new HashMap<ASN1ObjectIdentifier, int[]>();
    private JcaJceHelper helper;

    public KeyFactorySpi() {
        this(null);
    }

    public KeyFactorySpi(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
    }

    @Override
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (this.helper == null) {
            this.helper = new BCJcaJceHelper();
        }
        try {
            if (key instanceof PrivateKey) {
                return this.generatePrivate(PrivateKeyInfo.getInstance(key.getEncoded()));
            }
            if (key instanceof PublicKey) {
                return this.generatePublic(SubjectPublicKeyInfo.getInstance(key.getEncoded()));
            }
        }
        catch (IOException iOException) {
            throw new InvalidKeyException("Key could not be parsed: " + iOException.getMessage());
        }
        throw new InvalidKeyException("Key not recognized");
    }

    @Override
    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        if (this.helper == null) {
            this.helper = new BCJcaJceHelper();
        }
        if (MiscObjectIdentifiers.id_alg_composite.equals(aSN1ObjectIdentifier = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm()) || MiscObjectIdentifiers.id_composite_key.equals(aSN1ObjectIdentifier)) {
            ASN1Sequence aSN1Sequence = DERSequence.getInstance(privateKeyInfo.parsePrivateKey());
            PrivateKey[] privateKeyArray = new PrivateKey[aSN1Sequence.size()];
            for (int i = 0; i != aSN1Sequence.size(); ++i) {
                ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(aSN1Sequence.getObjectAt(i));
                PrivateKeyInfo privateKeyInfo2 = PrivateKeyInfo.getInstance(aSN1Sequence2);
                try {
                    privateKeyArray[i] = this.helper.createKeyFactory(privateKeyInfo2.getPrivateKeyAlgorithm().getAlgorithm().getId()).generatePrivate(new PKCS8EncodedKeySpec(privateKeyInfo2.getEncoded()));
                    continue;
                }
                catch (Exception exception) {
                    throw new IOException("cannot decode generic composite: " + exception.getMessage(), exception);
                }
            }
            return new CompositePrivateKey(privateKeyArray);
        }
        try {
            byte[] byArray;
            List<KeyFactory> list = this.getKeyFactoriesFromIdentifier(aSN1ObjectIdentifier);
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            try {
                byArray = DEROctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            }
            catch (Exception exception) {
                byArray = privateKeyInfo.getPrivateKey().getOctets();
            }
            aSN1EncodableVector.add(new DEROctetString(Arrays.copyOfRange(byArray, 0, 32)));
            String string = list.get(1).getAlgorithm();
            if (string.equals("Ed25519")) {
                aSN1EncodableVector.add(new DEROctetString(Arrays.concatenate(new byte[]{4, 32}, Arrays.copyOfRange(byArray, 32, byArray.length))));
            } else if (string.equals("Ed448")) {
                aSN1EncodableVector.add(new DEROctetString(Arrays.concatenate(new byte[]{4, 57}, Arrays.copyOfRange(byArray, 32, byArray.length))));
            } else {
                aSN1EncodableVector.add(new DEROctetString(Arrays.copyOfRange(byArray, 32, byArray.length)));
            }
            DERSequence dERSequence = new DERSequence(aSN1EncodableVector);
            PrivateKey[] privateKeyArray = new PrivateKey[dERSequence.size()];
            AlgorithmIdentifier[] algorithmIdentifierArray = pairings.get(aSN1ObjectIdentifier);
            for (int i = 0; i < dERSequence.size(); ++i) {
                Object object;
                if (dERSequence.getObjectAt(i) instanceof ASN1OctetString) {
                    aSN1EncodableVector = new ASN1EncodableVector(3);
                    aSN1EncodableVector.add(privateKeyInfo.getVersion());
                    aSN1EncodableVector.add(algorithmIdentifierArray[i]);
                    aSN1EncodableVector.add(dERSequence.getObjectAt(i));
                    object = new PKCS8EncodedKeySpec(PrivateKeyInfo.getInstance(new DERSequence(aSN1EncodableVector)).getEncoded());
                    privateKeyArray[i] = list.get(i).generatePrivate((KeySpec)object);
                    continue;
                }
                object = ASN1Sequence.getInstance(dERSequence.getObjectAt(i));
                PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(PrivateKeyInfo.getInstance(object).getEncoded());
                privateKeyArray[i] = list.get(i).generatePrivate(pKCS8EncodedKeySpec);
            }
            return new CompositePrivateKey(aSN1ObjectIdentifier, privateKeyArray);
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw Exceptions.ioException(generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    @Override
    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        if (this.helper == null) {
            this.helper = new BCJcaJceHelper();
        }
        ASN1ObjectIdentifier aSN1ObjectIdentifier = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        ASN1Sequence aSN1Sequence = null;
        Object object = new byte[2][];
        try {
            aSN1Sequence = DERSequence.getInstance(subjectPublicKeyInfo.getPublicKeyData().getBytes());
        }
        catch (Exception exception) {
            object = this.split(aSN1ObjectIdentifier, subjectPublicKeyInfo.getPublicKeyData());
        }
        if (MiscObjectIdentifiers.id_alg_composite.equals(aSN1ObjectIdentifier) || MiscObjectIdentifiers.id_composite_key.equals(aSN1ObjectIdentifier)) {
            ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(subjectPublicKeyInfo.getPublicKeyData().getBytes());
            PublicKey[] publicKeyArray = new PublicKey[aSN1Sequence2.size()];
            for (int i = 0; i != aSN1Sequence2.size(); ++i) {
                SubjectPublicKeyInfo subjectPublicKeyInfo2 = SubjectPublicKeyInfo.getInstance(aSN1Sequence2.getObjectAt(i));
                try {
                    publicKeyArray[i] = this.helper.createKeyFactory(subjectPublicKeyInfo2.getAlgorithm().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo2.getEncoded()));
                    continue;
                }
                catch (Exception exception) {
                    throw new IOException("cannot decode generic composite: " + exception.getMessage(), exception);
                }
            }
            return new CompositePublicKey(publicKeyArray);
        }
        try {
            int n = aSN1Sequence == null ? ((byte[][])object).length : aSN1Sequence.size();
            List<KeyFactory> list = this.getKeyFactoriesFromIdentifier(aSN1ObjectIdentifier);
            ASN1BitString[] aSN1BitStringArray = new ASN1BitString[n];
            for (int i = 0; i < n; ++i) {
                if (aSN1Sequence != null) {
                    if (aSN1Sequence.getObjectAt(i) instanceof DEROctetString) {
                        aSN1BitStringArray[i] = new DERBitString(((DEROctetString)aSN1Sequence.getObjectAt(i)).getOctets());
                        continue;
                    }
                    aSN1BitStringArray[i] = (DERBitString)aSN1Sequence.getObjectAt(i);
                    continue;
                }
                aSN1BitStringArray[i] = new DERBitString(object[i]);
            }
            X509EncodedKeySpec[] x509EncodedKeySpecArray = this.getKeysSpecs(aSN1ObjectIdentifier, aSN1BitStringArray);
            PublicKey[] publicKeyArray = new PublicKey[n];
            for (int i = 0; i < n; ++i) {
                publicKeyArray[i] = list.get(i).generatePublic(x509EncodedKeySpecArray[i]);
            }
            return new CompositePublicKey(aSN1ObjectIdentifier, publicKeyArray);
        }
        catch (GeneralSecurityException generalSecurityException) {
            throw Exceptions.ioException(generalSecurityException.getMessage(), generalSecurityException);
        }
    }

    byte[][] split(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1BitString aSN1BitString) {
        int[] nArray = componentKeySizes.get(aSN1ObjectIdentifier);
        byte[] byArray = aSN1BitString.getOctets();
        byte[][] byArrayArray = new byte[][]{new byte[nArray[0]], new byte[byArray.length - nArray[0]]};
        System.arraycopy(byArray, 0, byArrayArray[0], 0, nArray[0]);
        System.arraycopy(byArray, nArray[0], byArrayArray[1], 0, byArrayArray[1].length);
        return byArrayArray;
    }

    private List<KeyFactory> getKeyFactoriesFromIdentifier(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws NoSuchAlgorithmException, NoSuchProviderException {
        ArrayList<KeyFactory> arrayList = new ArrayList<KeyFactory>();
        ArrayList arrayList2 = new ArrayList();
        String[] stringArray = CompositeIndex.getPairing(aSN1ObjectIdentifier);
        if (stringArray == null) {
            throw new NoSuchAlgorithmException("Cannot create KeyFactories. Unsupported algorithm identifier.");
        }
        arrayList.add(this.helper.createKeyFactory(CompositeIndex.getBaseName(stringArray[0])));
        arrayList.add(this.helper.createKeyFactory(CompositeIndex.getBaseName(stringArray[1])));
        return Collections.unmodifiableList(arrayList);
    }

    private X509EncodedKeySpec[] getKeysSpecs(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1BitString[] aSN1BitStringArray) throws IOException {
        X509EncodedKeySpec[] x509EncodedKeySpecArray = new X509EncodedKeySpec[aSN1BitStringArray.length];
        SubjectPublicKeyInfo[] subjectPublicKeyInfoArray = new SubjectPublicKeyInfo[aSN1BitStringArray.length];
        AlgorithmIdentifier[] algorithmIdentifierArray = pairings.get(aSN1ObjectIdentifier);
        if (algorithmIdentifierArray == null) {
            throw new IOException("Cannot create key specs. Unsupported algorithm identifier.");
        }
        subjectPublicKeyInfoArray[0] = new SubjectPublicKeyInfo(algorithmIdentifierArray[0], aSN1BitStringArray[0]);
        subjectPublicKeyInfoArray[1] = new SubjectPublicKeyInfo(algorithmIdentifierArray[1], aSN1BitStringArray[1]);
        x509EncodedKeySpecArray[0] = new X509EncodedKeySpec(subjectPublicKeyInfoArray[0].getEncoded());
        x509EncodedKeySpecArray[1] = new X509EncodedKeySpec(subjectPublicKeyInfoArray[1].getEncoded());
        return x509EncodedKeySpecArray;
    }

    static {
        pairings.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new AlgorithmIdentifier[]{mlDsa44, rsa});
        pairings.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new AlgorithmIdentifier[]{mlDsa44, rsa});
        pairings.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new AlgorithmIdentifier[]{mlDsa44, ed25519});
        pairings.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new AlgorithmIdentifier[]{mlDsa44, ecDsaP256});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new AlgorithmIdentifier[]{mlDsa65, ecDsaP256});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new AlgorithmIdentifier[]{mlDsa65, ecDsaP384});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new AlgorithmIdentifier[]{mlDsa65, ecDsaBrainpoolP256r1});
        pairings.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new AlgorithmIdentifier[]{mlDsa65, ed25519});
        pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new AlgorithmIdentifier[]{mlDsa87, ecDsaP384});
        pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new AlgorithmIdentifier[]{mlDsa87, ecDsaBrainpoolP384r1});
        pairings.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new AlgorithmIdentifier[]{mlDsa87, ed448});
        pairings.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa87, rsa});
        pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new AlgorithmIdentifier[]{mlDsa87, ecDsaP521});
        pairings.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa87, rsa});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new int[]{1312, 268});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new int[]{1312, 284});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new int[]{1312, 32});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new int[]{1312, 76});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new int[]{1952, 256});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new int[]{1952, 256});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new int[]{1952, 542});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new int[]{1952, 542});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new int[]{1952, 76});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new int[]{1952, 87});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new int[]{1952, 76});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new int[]{1952, 32});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new int[]{2592, 87});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new int[]{2592, 87});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new int[]{2592, 57});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new int[]{2592, 542});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new int[]{2592, 256});
        componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new int[]{2592, 93});
    }
}

