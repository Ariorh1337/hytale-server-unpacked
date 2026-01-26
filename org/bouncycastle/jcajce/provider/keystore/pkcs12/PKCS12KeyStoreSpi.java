/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.keystore.pkcs12;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1BMPString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.EncryptedData;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12StoreParameter;
import org.bouncycastle.jcajce.provider.keystore.util.AdaptingKeyStoreSpi;
import org.bouncycastle.jcajce.provider.keystore.util.ParameterUtil;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.JDKPKCS12StoreParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Exceptions;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class PKCS12KeyStoreSpi
extends KeyStoreSpi
implements PKCSObjectIdentifiers,
X509ObjectIdentifiers,
BCKeyStore {
    static final String PKCS12_MAX_IT_COUNT_PROPERTY = "org.bouncycastle.pkcs12.max_it_count";
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private static final int SALT_SIZE = 20;
    private static final int MIN_ITERATIONS = 51200;
    private static final DefaultSecretKeyProvider keySizeProvider = new DefaultSecretKeyProvider();
    private IgnoresCaseHashtable keys = new IgnoresCaseHashtable();
    private IgnoresCaseHashtable localIds = new IgnoresCaseHashtable();
    private IgnoresCaseHashtable certs = new IgnoresCaseHashtable();
    private Hashtable chainCerts = new Hashtable();
    private Hashtable keyCerts = new Hashtable();
    static final int NULL = 0;
    static final int CERTIFICATE = 1;
    static final int KEY = 2;
    static final int SECRET = 3;
    static final int SEALED = 4;
    static final int KEY_PRIVATE = 0;
    static final int KEY_PUBLIC = 1;
    static final int KEY_SECRET = 2;
    protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    private CertificateFactory certFact;
    private ASN1ObjectIdentifier keyAlgorithm;
    private ASN1ObjectIdentifier certAlgorithm;
    private AlgorithmIdentifier macAlgorithm = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
    private int itCount = 102400;
    private int saltLength = 20;

    private static boolean isPBKDF2(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes256_CBC) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes256_GCM) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes128_CBC) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes128_GCM);
    }

    private static int getKeyLength(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes256_CBC) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes256_GCM)) {
            return 32;
        }
        return 16;
    }

    public PKCS12KeyStoreSpi(JcaJceHelper jcaJceHelper, ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        this.keyAlgorithm = aSN1ObjectIdentifier;
        this.certAlgorithm = aSN1ObjectIdentifier2;
        try {
            this.certFact = jcaJceHelper.createCertificateFactory("X.509");
        }
        catch (Exception exception) {
            throw new IllegalArgumentException("can't create cert factory - " + exception.toString());
        }
    }

    private SubjectKeyIdentifier createSubjectKeyId(PublicKey publicKey) {
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
            return new SubjectKeyIdentifier(PKCS12KeyStoreSpi.getDigest(subjectPublicKeyInfo));
        }
        catch (Exception exception) {
            throw new RuntimeException("error creating key");
        }
    }

    private static byte[] getDigest(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        Digest digest = DigestFactory.createSHA1();
        byte[] byArray = new byte[digest.getDigestSize()];
        byte[] byArray2 = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        digest.update(byArray2, 0, byArray2.length);
        digest.doFinal(byArray, 0);
        return byArray;
    }

    @Override
    public void setRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    @Override
    public boolean engineProbe(InputStream inputStream) throws IOException {
        return false;
    }

    public Enumeration engineAliases() {
        Hashtable<Object, String> hashtable = new Hashtable<Object, String>();
        Enumeration enumeration = this.certs.keys();
        while (enumeration.hasMoreElements()) {
            hashtable.put(enumeration.nextElement(), "cert");
        }
        enumeration = this.keys.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            if (hashtable.get(string) != null) continue;
            hashtable.put(string, "key");
        }
        return hashtable.keys();
    }

    @Override
    public boolean engineContainsAlias(String string) {
        return this.certs.get(string) != null || this.keys.get(string) != null;
    }

    @Override
    public void engineDeleteEntry(String string) throws KeyStoreException {
        Certificate certificate;
        String string2;
        Key key;
        Certificate certificate2 = (Certificate)this.certs.remove(string);
        if (certificate2 != null) {
            this.chainCerts.remove(new CertId(certificate2.getPublicKey()));
        }
        if ((key = (Key)this.keys.remove(string)) != null && (string2 = (String)this.localIds.remove(string)) != null && (certificate = (Certificate)this.keyCerts.remove(string2)) != null) {
            this.chainCerts.remove(new CertId(certificate.getPublicKey()));
        }
    }

    @Override
    public Certificate engineGetCertificate(String string) {
        if (string == null) {
            throw new IllegalArgumentException("null alias passed to getCertificate.");
        }
        Certificate certificate = (Certificate)this.certs.get(string);
        if (certificate == null) {
            String string2 = (String)this.localIds.get(string);
            certificate = string2 != null ? (Certificate)this.keyCerts.get(string2) : (Certificate)this.keyCerts.get(string);
        }
        return certificate;
    }

    @Override
    public String engineGetCertificateAlias(Certificate certificate) {
        String string;
        Certificate certificate2;
        Enumeration enumeration = this.certs.elements();
        Enumeration enumeration2 = this.certs.keys();
        while (enumeration.hasMoreElements()) {
            certificate2 = (Certificate)enumeration.nextElement();
            string = (String)enumeration2.nextElement();
            if (!certificate2.equals(certificate)) continue;
            return string;
        }
        enumeration = this.keyCerts.elements();
        enumeration2 = this.keyCerts.keys();
        while (enumeration.hasMoreElements()) {
            certificate2 = (Certificate)enumeration.nextElement();
            string = (String)enumeration2.nextElement();
            if (!certificate2.equals(certificate)) continue;
            return string;
        }
        return null;
    }

    @Override
    public Certificate[] engineGetCertificateChain(String string) {
        if (string == null) {
            throw new IllegalArgumentException("null alias passed to getCertificateChain.");
        }
        if (!this.engineIsKeyEntry(string)) {
            return null;
        }
        Certificate certificate = this.engineGetCertificate(string);
        if (certificate != null) {
            Certificate[] certificateArray;
            Vector<Certificate> vector = new Vector<Certificate>();
            while (certificate != null) {
                Object object;
                Object object2;
                certificateArray = (Certificate[])certificate;
                Certificate certificate2 = null;
                byte[] byArray = certificateArray.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (byArray != null && null != (object2 = ((AuthorityKeyIdentifier)(object = AuthorityKeyIdentifier.getInstance(ASN1OctetString.getInstance(byArray).getOctets()))).getKeyIdentifierOctets())) {
                    certificate2 = (Certificate)this.chainCerts.get(new CertId((byte[])object2));
                }
                if (certificate2 == null && !(object = certificateArray.getIssuerDN()).equals(object2 = (Object)certificateArray.getSubjectDN())) {
                    Enumeration enumeration = this.chainCerts.keys();
                    while (enumeration.hasMoreElements()) {
                        X509Certificate x509Certificate = (X509Certificate)this.chainCerts.get(enumeration.nextElement());
                        Principal principal = x509Certificate.getSubjectDN();
                        if (!principal.equals(object)) continue;
                        try {
                            certificateArray.verify(x509Certificate.getPublicKey());
                            certificate2 = x509Certificate;
                            break;
                        }
                        catch (Exception exception) {
                        }
                    }
                }
                if (vector.contains(certificate)) {
                    certificate = null;
                    continue;
                }
                vector.addElement(certificate);
                if (certificate2 != certificate) {
                    certificate = certificate2;
                    continue;
                }
                certificate = null;
            }
            certificateArray = new Certificate[vector.size()];
            for (int i = 0; i != certificateArray.length; ++i) {
                certificateArray[i] = (Certificate)vector.elementAt(i);
            }
            return certificateArray;
        }
        return null;
    }

    @Override
    public Date engineGetCreationDate(String string) {
        if (string == null) {
            throw new NullPointerException("alias == null");
        }
        if (this.keys.get(string) == null && this.certs.get(string) == null) {
            return null;
        }
        return new Date();
    }

    @Override
    public Key engineGetKey(String string, char[] cArray) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (string == null) {
            throw new IllegalArgumentException("null alias passed to getKey.");
        }
        return (Key)this.keys.get(string);
    }

    @Override
    public boolean engineIsCertificateEntry(String string) {
        return this.certs.get(string) != null && this.keys.get(string) == null;
    }

    @Override
    public boolean engineIsKeyEntry(String string) {
        return this.keys.get(string) != null;
    }

    @Override
    public void engineSetCertificateEntry(String string, Certificate certificate) throws KeyStoreException {
        if (this.keys.get(string) != null) {
            throw new KeyStoreException("There is a key entry with the name " + string + ".");
        }
        this.certs.put(string, certificate);
        this.chainCerts.put(new CertId(certificate.getPublicKey()), certificate);
    }

    @Override
    public void engineSetKeyEntry(String string, byte[] byArray, Certificate[] certificateArray) throws KeyStoreException {
        throw new RuntimeException("operation not supported");
    }

    @Override
    public void engineSetKeyEntry(String string, Key key, char[] cArray, Certificate[] certificateArray) throws KeyStoreException {
        if (!(key instanceof PrivateKey)) {
            throw new KeyStoreException("PKCS12 does not support non-PrivateKeys");
        }
        if (key instanceof PrivateKey && certificateArray == null) {
            throw new KeyStoreException("no certificate chain for private key");
        }
        if (this.keys.get(string) != null) {
            this.engineDeleteEntry(string);
        }
        this.keys.put(string, key);
        if (certificateArray != null) {
            this.certs.put(string, certificateArray[0]);
            for (int i = 0; i != certificateArray.length; ++i) {
                this.chainCerts.put(new CertId(certificateArray[i].getPublicKey()), certificateArray[i]);
            }
        }
    }

    @Override
    public int engineSize() {
        Hashtable<Object, String> hashtable = new Hashtable<Object, String>();
        Enumeration enumeration = this.certs.keys();
        while (enumeration.hasMoreElements()) {
            hashtable.put(enumeration.nextElement(), "cert");
        }
        enumeration = this.keys.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            if (hashtable.get(string) != null) continue;
            hashtable.put(string, "key");
        }
        return hashtable.size();
    }

    protected PrivateKey unwrapKey(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, char[] cArray, boolean bl) throws IOException {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        try {
            if (aSN1ObjectIdentifier.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
                PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), this.validateIterationCount(pKCS12PBEParams.getIterations()));
                Cipher cipher = this.helper.createCipher(aSN1ObjectIdentifier.getId());
                PKCS12Key pKCS12Key = new PKCS12Key(cArray, bl);
                cipher.init(4, (Key)pKCS12Key, pBEParameterSpec);
                return (PrivateKey)cipher.unwrap(byArray, "", 2);
            }
            if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_PBES2)) {
                Cipher cipher = this.createCipher(4, cArray, algorithmIdentifier);
                return (PrivateKey)cipher.unwrap(byArray, "", 2);
            }
        }
        catch (InvalidKeyException invalidKeyException) {
            throw Exceptions.ioException("exception unwrapping private key:" + invalidKeyException.getMessage(), new UnrecoverableKeyException(invalidKeyException.toString()));
        }
        catch (Exception exception) {
            throw Exceptions.ioException("exception unwrapping private key: " + exception.getMessage(), exception);
        }
        throw new IOException("exception unwrapping private key - cannot recognise: " + aSN1ObjectIdentifier);
    }

    protected byte[] wrapKey(String string, Key key, PKCS12PBEParams pKCS12PBEParams, char[] cArray) throws IOException {
        byte[] byArray;
        PBEKeySpec pBEKeySpec = new PBEKeySpec(cArray);
        try {
            SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory(string);
            PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), BigIntegers.intValueExact(pKCS12PBEParams.getIterations()));
            Cipher cipher = this.helper.createCipher(string);
            cipher.init(3, (Key)secretKeyFactory.generateSecret(pBEKeySpec), pBEParameterSpec);
            byArray = cipher.wrap(key);
        }
        catch (Exception exception) {
            throw new IOException("exception encrypting data - " + exception.toString());
        }
        return byArray;
    }

    protected byte[] wrapKey(EncryptionScheme encryptionScheme, Key key, PBKDF2Params pBKDF2Params, char[] cArray) throws IOException {
        byte[] byArray;
        PBEKeySpec pBEKeySpec = new PBEKeySpec(cArray, pBKDF2Params.getSalt(), BigIntegers.intValueExact(pBKDF2Params.getIterationCount()), BigIntegers.intValueExact(pBKDF2Params.getKeyLength()) * 8);
        try {
            SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory("PBKDF2withHMacSHA256");
            Cipher cipher = this.helper.createCipher(encryptionScheme.getAlgorithm().getId());
            AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(encryptionScheme.getAlgorithm().getId());
            algorithmParameters.init(encryptionScheme.getParameters().toASN1Primitive().getEncoded());
            cipher.init(3, (Key)secretKeyFactory.generateSecret(pBEKeySpec), algorithmParameters);
            byArray = cipher.wrap(key);
        }
        catch (Exception exception) {
            throw new IOException("exception encrypting data - " + exception.toString());
        }
        return byArray;
    }

    protected byte[] cryptData(boolean bl, AlgorithmIdentifier algorithmIdentifier, char[] cArray, boolean bl2, byte[] byArray) throws IOException {
        int n;
        ASN1ObjectIdentifier aSN1ObjectIdentifier = algorithmIdentifier.getAlgorithm();
        int n2 = n = bl ? 1 : 2;
        if (aSN1ObjectIdentifier.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
            PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
            PKCS12Key pKCS12Key = new PKCS12Key(cArray, bl2);
            try {
                PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(pKCS12PBEParams.getIV(), BigIntegers.intValueExact(pKCS12PBEParams.getIterations()));
                Cipher cipher = this.helper.createCipher(aSN1ObjectIdentifier.getId());
                cipher.init(n, (Key)pKCS12Key, pBEParameterSpec);
                byte[] byArray2 = cipher.doFinal(byArray);
                return byArray2;
            }
            catch (Exception exception) {
                throw new IOException("exception decrypting data - " + exception.toString());
            }
            finally {
                Arrays.clear(pKCS12Key.getPassword());
            }
        }
        if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_PBES2)) {
            try {
                Cipher cipher = this.createCipher(n, cArray, algorithmIdentifier);
                return cipher.doFinal(byArray);
            }
            catch (Exception exception) {
                throw new IOException("exception decrypting data - " + exception.toString());
            }
        }
        throw new IOException("unknown PBE algorithm: " + aSN1ObjectIdentifier);
    }

    private Cipher createCipher(int n, char[] cArray, AlgorithmIdentifier algorithmIdentifier) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException {
        PBES2Parameters pBES2Parameters = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
        PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(pBES2Parameters.getKeyDerivationFunc().getParameters());
        AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(pBES2Parameters.getEncryptionScheme());
        SecretKeyFactory secretKeyFactory = this.helper.createSecretKeyFactory(pBES2Parameters.getKeyDerivationFunc().getAlgorithm().getId());
        SecretKey secretKey = pBKDF2Params.isDefaultPrf() ? secretKeyFactory.generateSecret(new PBEKeySpec(cArray, pBKDF2Params.getSalt(), this.validateIterationCount(pBKDF2Params.getIterationCount()), keySizeProvider.getKeySize(algorithmIdentifier2))) : secretKeyFactory.generateSecret(new PBKDF2KeySpec(cArray, pBKDF2Params.getSalt(), this.validateIterationCount(pBKDF2Params.getIterationCount()), keySizeProvider.getKeySize(algorithmIdentifier2), pBKDF2Params.getPrf()));
        Cipher cipher = this.helper.createCipher(pBES2Parameters.getEncryptionScheme().getAlgorithm().getId());
        ASN1Encodable aSN1Encodable = pBES2Parameters.getEncryptionScheme().getParameters();
        if (aSN1Encodable instanceof ASN1OctetString) {
            cipher.init(n, (Key)secretKey, new IvParameterSpec(ASN1OctetString.getInstance(aSN1Encodable).getOctets()));
        } else {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Encodable);
            if (aSN1Sequence.getObjectAt(1) instanceof ASN1ObjectIdentifier) {
                GOST28147Parameters gOST28147Parameters = GOST28147Parameters.getInstance(aSN1Encodable);
                cipher.init(n, (Key)secretKey, new GOST28147ParameterSpec(gOST28147Parameters.getEncryptionParamSet(), gOST28147Parameters.getIV()));
            } else {
                AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(algorithmIdentifier2.getAlgorithm().getId(), "BC");
                try {
                    algorithmParameters.init(aSN1Sequence.getEncoded());
                }
                catch (IOException iOException) {
                    throw new InvalidKeySpecException(iOException.getMessage());
                }
                cipher.init(n, (Key)secretKey, algorithmParameters);
            }
        }
        return cipher;
    }

    @Override
    public void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (loadStoreParameter == null) {
            this.engineLoad(null, null);
        } else if (loadStoreParameter instanceof BCLoadStoreParameter) {
            BCLoadStoreParameter bCLoadStoreParameter = (BCLoadStoreParameter)loadStoreParameter;
            this.engineLoad(bCLoadStoreParameter.getInputStream(), ParameterUtil.extractPassword(loadStoreParameter));
        } else {
            throw new IllegalArgumentException("no support for 'param' of type " + loadStoreParameter.getClass().getName());
        }
    }

    @Override
    public void engineLoad(InputStream inputStream, char[] cArray) throws IOException {
        ASN1Object aSN1Object;
        Object object;
        Object object2;
        Object object3;
        ASN1Object aSN1Object2;
        ASN1Object aSN1Object3;
        Pfx pfx;
        if (inputStream == null) {
            return;
        }
        boolean bl = true;
        boolean bl2 = true;
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(10);
        int n = bufferedInputStream.read();
        if (n < 0) {
            throw new EOFException("no data in keystore stream");
        }
        if (n != 48) {
            throw new IOException("stream does not represent a PKCS12 key store");
        }
        bufferedInputStream.reset();
        ASN1InputStream aSN1InputStream = new ASN1InputStream(bufferedInputStream);
        try {
            pfx = Pfx.getInstance(aSN1InputStream.readObject());
        }
        catch (Exception exception) {
            throw new IOException(exception.getMessage());
        }
        ContentInfo contentInfo = pfx.getAuthSafe();
        Vector<SafeBag> vector = new Vector<SafeBag>();
        boolean bl3 = false;
        boolean bl4 = false;
        if (pfx.getMacData() != null) {
            if (cArray == null) {
                throw new NullPointerException("no password supplied when one expected");
            }
            bl = false;
            aSN1Object3 = pfx.getMacData();
            aSN1Object2 = ((MacData)aSN1Object3).getMac();
            this.macAlgorithm = ((DigestInfo)aSN1Object2).getAlgorithmId();
            object3 = ((MacData)aSN1Object3).getSalt();
            this.itCount = this.validateIterationCount(((MacData)aSN1Object3).getIterationCount());
            this.saltLength = ((byte[])object3).length;
            byte[] byArray = ((ASN1OctetString)contentInfo.getContent()).getOctets();
            try {
                object2 = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), (byte[])object3, this.itCount, cArray, false, byArray);
                object = ((DigestInfo)aSN1Object2).getDigest();
                if (!Arrays.constantTimeAreEqual((byte[])object2, object)) {
                    if (cArray.length > 0) {
                        throw Exceptions.ioException("PKCS12 key store mac invalid - wrong password or corrupted file", new UnrecoverableKeyException("PKCS12 key store mac invalid"));
                    }
                    object2 = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), (byte[])object3, this.itCount, cArray, true, byArray);
                    if (!Arrays.constantTimeAreEqual((byte[])object2, object)) {
                        throw Exceptions.ioException("PKCS12 key store mac invalid - wrong password or corrupted file", new UnrecoverableKeyException("PKCS12 key store mac invalid"));
                    }
                    bl4 = true;
                }
            }
            catch (IOException iOException) {
                throw iOException;
            }
            catch (Exception exception) {
                throw new IOException("error constructing MAC: " + exception.toString());
            }
        }
        this.keys = new IgnoresCaseHashtable();
        this.localIds = new IgnoresCaseHashtable();
        if (contentInfo.getContentType().equals(data)) {
            aSN1Object3 = ASN1OctetString.getInstance(contentInfo.getContent());
            aSN1Object2 = AuthenticatedSafe.getInstance(((ASN1OctetString)aSN1Object3).getOctets());
            object3 = ((AuthenticatedSafe)aSN1Object2).getContentInfo();
            for (int i = 0; i != ((byte[])object3).length; ++i) {
                if (object3[i].getContentType().equals(data)) {
                    object2 = ASN1OctetString.getInstance(object3[i].getContent());
                    object = ASN1Sequence.getInstance(((ASN1OctetString)object2).getOctets());
                    for (int j = 0; j != object.size(); ++j) {
                        SafeBag safeBag = SafeBag.getInstance(object.getObjectAt(j));
                        if (safeBag.getBagId().equals(pkcs8ShroudedKeyBag)) {
                            bl3 = this.processShroudedKeyBag(safeBag, cArray, bl4);
                            bl2 = false;
                            continue;
                        }
                        if (safeBag.getBagId().equals(certBag)) {
                            vector.addElement(safeBag);
                            continue;
                        }
                        if (safeBag.getBagId().equals(keyBag)) {
                            this.processKeyBag(safeBag);
                            continue;
                        }
                        System.out.println("extra in data " + safeBag.getBagId());
                        System.out.println(ASN1Dump.dumpAsString(safeBag));
                    }
                    continue;
                }
                if (object3[i].getContentType().equals(encryptedData)) {
                    object2 = EncryptedData.getInstance(((ContentInfo)object3[i]).getContent());
                    object = this.cryptData(false, ((EncryptedData)object2).getEncryptionAlgorithm(), cArray, bl4, ((EncryptedData)object2).getContent().getOctets());
                    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(object);
                    bl2 = false;
                    for (int j = 0; j != aSN1Sequence.size(); ++j) {
                        aSN1Object = SafeBag.getInstance(aSN1Sequence.getObjectAt(j));
                        if (((SafeBag)aSN1Object).getBagId().equals(certBag)) {
                            vector.addElement((SafeBag)aSN1Object);
                            continue;
                        }
                        if (((SafeBag)aSN1Object).getBagId().equals(pkcs8ShroudedKeyBag)) {
                            bl3 = this.processShroudedKeyBag((SafeBag)aSN1Object, cArray, bl4);
                            continue;
                        }
                        if (((SafeBag)aSN1Object).getBagId().equals(keyBag)) {
                            this.processKeyBag((SafeBag)aSN1Object);
                            continue;
                        }
                        System.out.println("extra in encryptedData " + ((SafeBag)aSN1Object).getBagId());
                        System.out.println(ASN1Dump.dumpAsString(aSN1Object));
                    }
                    continue;
                }
                System.out.println("extra " + ((ContentInfo)object3[i]).getContentType().getId());
                System.out.println("extra " + ASN1Dump.dumpAsString(((ContentInfo)object3[i]).getContent()));
            }
        }
        this.certs = new IgnoresCaseHashtable();
        this.chainCerts = new Hashtable();
        this.keyCerts = new Hashtable();
        for (int i = 0; i != vector.size(); ++i) {
            Object object4;
            Certificate certificate;
            aSN1Object2 = (SafeBag)vector.elementAt(i);
            object3 = CertBag.getInstance(((SafeBag)aSN1Object2).getBagValue());
            if (!((CertBag)object3).getCertId().equals(x509Certificate)) {
                throw new RuntimeException("Unsupported certificate type: " + ((CertBag)object3).getCertId());
            }
            try {
                object2 = new ByteArrayInputStream(((ASN1OctetString)((CertBag)object3).getCertValue()).getOctets());
                certificate = this.certFact.generateCertificate((InputStream)object2);
            }
            catch (Exception exception) {
                throw new RuntimeException(exception.toString());
            }
            object2 = null;
            object = null;
            if (((SafeBag)aSN1Object2).getBagAttributes() != null) {
                object4 = ((SafeBag)aSN1Object2).getBagAttributes().getObjects();
                while (object4.hasMoreElements()) {
                    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(object4.nextElement());
                    aSN1Object = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
                    ASN1Set aSN1Set = ASN1Set.getInstance(aSN1Sequence.getObjectAt(1));
                    if (aSN1Set.size() <= 0) continue;
                    ASN1Primitive aSN1Primitive = (ASN1Primitive)aSN1Set.getObjectAt(0);
                    PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier = null;
                    if (certificate instanceof PKCS12BagAttributeCarrier) {
                        pKCS12BagAttributeCarrier = (PKCS12BagAttributeCarrier)((Object)certificate);
                        ASN1Encodable aSN1Encodable = pKCS12BagAttributeCarrier.getBagAttribute((ASN1ObjectIdentifier)aSN1Object);
                        if (aSN1Encodable != null) {
                            if (((ASN1Primitive)aSN1Object).equals(pkcs_9_at_localKeyId)) {
                                String string = Hex.toHexString(((ASN1OctetString)aSN1Primitive).getOctets());
                                if (!this.keys.keys.containsKey(string) && !this.localIds.keys.containsKey(string)) continue;
                            }
                            if (!aSN1Encodable.toASN1Primitive().equals(aSN1Primitive)) {
                                throw new IOException("attempt to add existing attribute with different value");
                            }
                        } else if (aSN1Set.size() > 1) {
                            pKCS12BagAttributeCarrier.setBagAttribute((ASN1ObjectIdentifier)aSN1Object, aSN1Set);
                        } else {
                            pKCS12BagAttributeCarrier.setBagAttribute((ASN1ObjectIdentifier)aSN1Object, aSN1Primitive);
                        }
                    }
                    if (((ASN1Primitive)aSN1Object).equals(pkcs_9_at_friendlyName)) {
                        object = ((ASN1BMPString)aSN1Primitive).getString();
                        continue;
                    }
                    if (!((ASN1Primitive)aSN1Object).equals(pkcs_9_at_localKeyId)) continue;
                    object2 = (ASN1OctetString)aSN1Primitive;
                }
            }
            this.chainCerts.put(new CertId(certificate.getPublicKey()), certificate);
            if (bl3) {
                if (!this.keyCerts.isEmpty()) continue;
                object4 = new String(Hex.encode(this.createSubjectKeyId(certificate.getPublicKey()).getKeyIdentifier()));
                this.keyCerts.put(object4, certificate);
                this.keys.put((String)object4, this.keys.remove("unmarked"));
                continue;
            }
            if (object2 != null) {
                object4 = new String(Hex.encode(((ASN1OctetString)object2).getOctets()));
                this.keyCerts.put(object4, certificate);
            }
            if (object == null) continue;
            this.certs.put((String)object, certificate);
        }
        if (bl && bl2 && cArray != null && cArray.length != 0 && !Properties.isOverrideSet("org.bouncycastle.pkcs12.ignore_useless_passwd")) {
            throw new IOException("password supplied for keystore that does not require one");
        }
    }

    private boolean processShroudedKeyBag(SafeBag safeBag, char[] cArray, boolean bl) throws IOException {
        Object object;
        EncryptedPrivateKeyInfo encryptedPrivateKeyInfo = EncryptedPrivateKeyInfo.getInstance(safeBag.getBagValue());
        PrivateKey privateKey = this.unwrapKey(encryptedPrivateKeyInfo.getEncryptionAlgorithm(), encryptedPrivateKeyInfo.getEncryptedData(), cArray, bl);
        String string = null;
        ASN1OctetString aSN1OctetString = null;
        if (safeBag.getBagAttributes() != null) {
            object = safeBag.getBagAttributes().getObjects();
            while (object.hasMoreElements()) {
                ASN1Sequence aSN1Sequence = (ASN1Sequence)object.nextElement();
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(0);
                ASN1Set aSN1Set = (ASN1Set)aSN1Sequence.getObjectAt(1);
                ASN1Primitive aSN1Primitive = null;
                if (aSN1Set.size() > 0) {
                    aSN1Primitive = (ASN1Primitive)aSN1Set.getObjectAt(0);
                    if (privateKey instanceof PKCS12BagAttributeCarrier) {
                        PKCS12BagAttributeCarrier pKCS12BagAttributeCarrier = (PKCS12BagAttributeCarrier)((Object)privateKey);
                        ASN1Encodable aSN1Encodable = pKCS12BagAttributeCarrier.getBagAttribute(aSN1ObjectIdentifier);
                        if (aSN1Encodable != null) {
                            if (!aSN1Encodable.toASN1Primitive().equals(aSN1Primitive)) {
                                throw new IOException("attempt to add existing attribute with different value");
                            }
                        } else {
                            pKCS12BagAttributeCarrier.setBagAttribute(aSN1ObjectIdentifier, aSN1Primitive);
                        }
                    }
                }
                if (aSN1ObjectIdentifier.equals(pkcs_9_at_friendlyName)) {
                    string = ((ASN1BMPString)aSN1Primitive).getString();
                    this.keys.put(string, privateKey);
                    continue;
                }
                if (!aSN1ObjectIdentifier.equals(pkcs_9_at_localKeyId)) continue;
                aSN1OctetString = (ASN1OctetString)aSN1Primitive;
            }
        }
        if (aSN1OctetString != null) {
            object = new String(Hex.encode(aSN1OctetString.getOctets()));
            if (string == null) {
                this.keys.put((String)object, privateKey);
            } else {
                this.localIds.put(string, object);
            }
            return false;
        }
        this.keys.put("unmarked", privateKey);
        return true;
    }

    private void processKeyBag(SafeBag safeBag) throws IOException {
        Object object;
        PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(safeBag.getBagValue());
        PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(privateKeyInfo);
        String string = null;
        ASN1OctetString aSN1OctetString = null;
        if (privateKey instanceof PKCS12BagAttributeCarrier) {
            object = (PKCS12BagAttributeCarrier)((Object)privateKey);
            Enumeration enumeration = safeBag.getBagAttributes().getObjects();
            while (enumeration.hasMoreElements()) {
                ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(enumeration.nextElement());
                ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
                ASN1Set aSN1Set = ASN1Set.getInstance(aSN1Sequence.getObjectAt(1));
                ASN1Primitive aSN1Primitive = null;
                if (aSN1Set.size() <= 0) continue;
                aSN1Primitive = (ASN1Primitive)aSN1Set.getObjectAt(0);
                ASN1Encodable aSN1Encodable = object.getBagAttribute(aSN1ObjectIdentifier);
                if (aSN1Encodable != null) {
                    if (!aSN1Encodable.toASN1Primitive().equals(aSN1Primitive)) {
                        throw new IOException("attempt to add existing attribute with different value");
                    }
                } else {
                    object.setBagAttribute(aSN1ObjectIdentifier, aSN1Primitive);
                }
                if (aSN1ObjectIdentifier.equals(pkcs_9_at_friendlyName)) {
                    string = ((ASN1BMPString)aSN1Primitive).getString();
                    this.keys.put(string, privateKey);
                    continue;
                }
                if (!aSN1ObjectIdentifier.equals(pkcs_9_at_localKeyId)) continue;
                aSN1OctetString = (ASN1OctetString)aSN1Primitive;
            }
        }
        object = new String(Hex.encode(aSN1OctetString.getOctets()));
        if (string == null) {
            this.keys.put((String)object, privateKey);
        } else {
            this.localIds.put(string, object);
        }
    }

    private int validateIterationCount(BigInteger bigInteger) {
        int n = BigIntegers.intValueExact(bigInteger);
        if (n < 0) {
            throw new IllegalStateException("negative iteration count found");
        }
        BigInteger bigInteger2 = Properties.asBigInteger(PKCS12_MAX_IT_COUNT_PROPERTY);
        if (bigInteger2 != null && BigIntegers.intValueExact(bigInteger2) < n) {
            throw new IllegalStateException("iteration count " + n + " greater than " + BigIntegers.intValueExact(bigInteger2));
        }
        return n;
    }

    private ASN1Primitive getAlgParams(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes128_CBC) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes256_CBC)) {
            byte[] byArray = new byte[16];
            this.random.nextBytes(byArray);
            return new DEROctetString(byArray);
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes128_GCM) || aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_aes256_GCM)) {
            byte[] byArray = new byte[12];
            this.random.nextBytes(byArray);
            return new GCMParameters(byArray, 16).toASN1Primitive();
        }
        throw new IllegalStateException("unknown encryption OID in getAlgParams()");
    }

    @Override
    public void engineStore(KeyStore.LoadStoreParameter loadStoreParameter) throws IOException, NoSuchAlgorithmException, CertificateException {
        char[] cArray;
        if (loadStoreParameter == null) {
            throw new IllegalArgumentException("'param' arg cannot be null");
        }
        if (!(loadStoreParameter instanceof PKCS12StoreParameter) && !(loadStoreParameter instanceof JDKPKCS12StoreParameter)) {
            throw new IllegalArgumentException("No support for 'param' of type " + loadStoreParameter.getClass().getName());
        }
        PKCS12StoreParameter pKCS12StoreParameter = loadStoreParameter instanceof PKCS12StoreParameter ? (PKCS12StoreParameter)loadStoreParameter : new PKCS12StoreParameter(((JDKPKCS12StoreParameter)loadStoreParameter).getOutputStream(), loadStoreParameter.getProtectionParameter(), ((JDKPKCS12StoreParameter)loadStoreParameter).isUseDEREncoding(), ((JDKPKCS12StoreParameter)loadStoreParameter).isOverwriteFriendlyName());
        KeyStore.ProtectionParameter protectionParameter = loadStoreParameter.getProtectionParameter();
        if (protectionParameter == null) {
            cArray = null;
        } else if (protectionParameter instanceof KeyStore.PasswordProtection) {
            cArray = ((KeyStore.PasswordProtection)protectionParameter).getPassword();
        } else {
            throw new IllegalArgumentException("No support for protection parameter of type " + protectionParameter.getClass().getName());
        }
        this.doStore(pKCS12StoreParameter.getOutputStream(), cArray, pKCS12StoreParameter.isForDEREncoding(), pKCS12StoreParameter.isOverwriteFriendlyName());
    }

    @Override
    public void engineStore(OutputStream outputStream, char[] cArray) throws IOException {
        this.doStore(outputStream, cArray, false, true);
    }

    private void syncFriendlyName() {
        ASN1Encodable aSN1Encodable;
        Serializable serializable;
        String string;
        Enumeration enumeration = this.keys.keys();
        while (enumeration.hasMoreElements()) {
            string = (String)enumeration.nextElement();
            serializable = (PrivateKey)this.keys.get(string);
            if (!(serializable instanceof PKCS12BagAttributeCarrier) || (aSN1Encodable = ((PKCS12BagAttributeCarrier)((Object)serializable)).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName)) == null || string.equals(aSN1Encodable.toString())) continue;
            this.keys.put(aSN1Encodable.toString(), serializable);
            this.keys.remove(string);
        }
        enumeration = this.certs.keys();
        while (enumeration.hasMoreElements()) {
            string = (String)enumeration.nextElement();
            serializable = (Certificate)this.certs.get(string);
            if (!(serializable instanceof PKCS12BagAttributeCarrier) || (aSN1Encodable = ((PKCS12BagAttributeCarrier)((Object)serializable)).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName)) == null || string.equals(aSN1Encodable.toString())) continue;
            this.certs.put(aSN1Encodable.toString(), serializable);
            this.certs.remove(string);
        }
        enumeration = this.keyCerts.keys();
        while (enumeration.hasMoreElements()) {
            string = (String)enumeration.nextElement();
            serializable = (Certificate)this.keyCerts.get(string);
            if (!(serializable instanceof PKCS12BagAttributeCarrier) || (aSN1Encodable = ((PKCS12BagAttributeCarrier)((Object)serializable)).getBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName)) == null || string.equals(aSN1Encodable.toString())) continue;
            this.keyCerts.put(aSN1Encodable.toString(), serializable);
            this.keyCerts.remove(string);
        }
    }

    private void doStore(OutputStream outputStream, char[] cArray, boolean bl, boolean bl2) throws IOException {
        Object object;
        MacData macData;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        ContentInfo[] contentInfoArray;
        Object object7;
        Object object8;
        Object object9;
        Object object10;
        Object object11;
        Object object12;
        Object object13;
        Object object14;
        byte[] byArray;
        if (!bl2) {
            this.syncFriendlyName();
        }
        if (this.keys.size() == 0) {
            if (cArray == null) {
                Object object15;
                Object object16;
                Enumeration enumeration = this.certs.keys();
                ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
                while (enumeration.hasMoreElements()) {
                    try {
                        object16 = (String)enumeration.nextElement();
                        object15 = (Certificate)this.certs.get((String)object16);
                        SafeBag safeBag = this.createSafeBag((String)object16, (Certificate)object15, bl2);
                        aSN1EncodableVector.add(safeBag);
                    }
                    catch (CertificateEncodingException certificateEncodingException) {
                        throw new IOException("Error encoding certificate: " + certificateEncodingException.toString());
                    }
                }
                if (bl) {
                    object16 = new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence(aSN1EncodableVector).getEncoded()));
                    object15 = new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DERSequence((ASN1Encodable)object16).getEncoded())), null);
                    ((ASN1Object)object15).encodeTo(outputStream, "DER");
                } else {
                    object16 = new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence(aSN1EncodableVector).getEncoded()));
                    object15 = new Pfx(new ContentInfo(PKCSObjectIdentifiers.data, new BEROctetString(new BERSequence((ASN1Encodable)object16).getEncoded())), null);
                    ((ASN1Object)object15).encodeTo(outputStream, "BER");
                }
                return;
            }
        } else if (cArray == null) {
            throw new NullPointerException("no password supplied for PKCS#12 KeyStore");
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Enumeration enumeration = this.keys.keys();
        while (enumeration.hasMoreElements()) {
            Object object17;
            byArray = new byte[20];
            this.random.nextBytes(byArray);
            object14 = (String)enumeration.nextElement();
            object13 = (PrivateKey)this.keys.get((String)object14);
            if (PKCS12KeyStoreSpi.isPBKDF2(this.keyAlgorithm)) {
                object12 = new PBKDF2Params(byArray, 51200, PKCS12KeyStoreSpi.getKeyLength(this.keyAlgorithm), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE));
                EncryptionScheme encryptionScheme = new EncryptionScheme(this.keyAlgorithm, this.getAlgParams(this.keyAlgorithm));
                object11 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)object12), encryptionScheme));
                object10 = this.wrapKey(encryptionScheme, (Key)object13, (PBKDF2Params)object12, cArray);
            } else {
                object12 = new PKCS12PBEParams(byArray, 51200);
                object10 = this.wrapKey(this.keyAlgorithm.getId(), (Key)object13, (PKCS12PBEParams)object12, cArray);
                object11 = new AlgorithmIdentifier(this.keyAlgorithm, ((PKCS12PBEParams)object12).toASN1Primitive());
            }
            object12 = new EncryptedPrivateKeyInfo((AlgorithmIdentifier)object11, (byte[])object10);
            boolean bl3 = false;
            object9 = new ASN1EncodableVector();
            if (object13 instanceof PKCS12BagAttributeCarrier) {
                object8 = (PKCS12BagAttributeCarrier)object13;
                object17 = (ASN1BMPString)object8.getBagAttribute(pkcs_9_at_friendlyName);
                if (bl2 && (object17 == null || !((ASN1BMPString)object17).getString().equals(object14))) {
                    object8.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString((String)object14));
                }
                if (object8.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                    object7 = this.engineGetCertificate((String)object14);
                    object8.setBagAttribute(pkcs_9_at_localKeyId, this.createSubjectKeyId(((Certificate)object7).getPublicKey()));
                }
                object7 = object8.getBagAttributeKeys();
                while (object7.hasMoreElements()) {
                    contentInfoArray = (ASN1ObjectIdentifier)object7.nextElement();
                    object6 = new ASN1EncodableVector();
                    ((ASN1EncodableVector)object6).add((ASN1Encodable)contentInfoArray);
                    ((ASN1EncodableVector)object6).add(new DERSet(object8.getBagAttribute((ASN1ObjectIdentifier)contentInfoArray)));
                    bl3 = true;
                    ((ASN1EncodableVector)object9).add(new DERSequence((ASN1EncodableVector)object6));
                }
            }
            if (!bl3) {
                object8 = new ASN1EncodableVector();
                object17 = this.engineGetCertificate((String)object14);
                ((ASN1EncodableVector)object8).add(pkcs_9_at_localKeyId);
                ((ASN1EncodableVector)object8).add(new DERSet(this.createSubjectKeyId(((Certificate)object17).getPublicKey())));
                ((ASN1EncodableVector)object9).add(new DERSequence((ASN1EncodableVector)object8));
                object8 = new ASN1EncodableVector();
                ((ASN1EncodableVector)object8).add(pkcs_9_at_friendlyName);
                ((ASN1EncodableVector)object8).add(new DERSet(new DERBMPString((String)object14)));
                ((ASN1EncodableVector)object9).add(new DERSequence((ASN1EncodableVector)object8));
            }
            object8 = new SafeBag(pkcs8ShroudedKeyBag, ((EncryptedPrivateKeyInfo)object12).toASN1Primitive(), new DERSet((ASN1EncodableVector)object9));
            aSN1EncodableVector.add((ASN1Encodable)object8);
        }
        byArray = new DERSequence(aSN1EncodableVector).getEncoded("DER");
        object14 = new BEROctetString(byArray);
        object13 = new byte[20];
        this.random.nextBytes((byte[])object13);
        object11 = new ASN1EncodableVector();
        if (PKCS12KeyStoreSpi.isPBKDF2(this.certAlgorithm)) {
            object12 = new PBKDF2Params((byte[])object13, 51200, PKCS12KeyStoreSpi.getKeyLength(this.certAlgorithm), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE));
            object10 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBES2Parameters(new KeyDerivationFunc(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)object12), new EncryptionScheme(this.certAlgorithm, this.getAlgParams(this.certAlgorithm))));
        } else {
            object12 = new PKCS12PBEParams((byte[])object13, 51200);
            object10 = new AlgorithmIdentifier(this.certAlgorithm, ((PKCS12PBEParams)object12).toASN1Primitive());
        }
        object12 = new Hashtable();
        Enumeration enumeration2 = this.keys.keys();
        while (enumeration2.hasMoreElements()) {
            try {
                object9 = (String)enumeration2.nextElement();
                object8 = this.engineGetCertificate((String)object9);
                boolean bl4 = false;
                object7 = new CertBag(x509Certificate, new DEROctetString(((Certificate)object8).getEncoded()));
                contentInfoArray = new ASN1EncodableVector();
                if (object8 instanceof PKCS12BagAttributeCarrier) {
                    object6 = (PKCS12BagAttributeCarrier)object8;
                    object5 = (ASN1BMPString)object6.getBagAttribute(pkcs_9_at_friendlyName);
                    if (bl2 && (object5 == null || !((ASN1BMPString)object5).getString().equals(object9))) {
                        object6.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString((String)object9));
                    }
                    if (object6.getBagAttribute(pkcs_9_at_localKeyId) == null) {
                        object6.setBagAttribute(pkcs_9_at_localKeyId, this.createSubjectKeyId(((Certificate)object8).getPublicKey()));
                    }
                    object4 = object6.getBagAttributeKeys();
                    while (object4.hasMoreElements()) {
                        object3 = (ASN1ObjectIdentifier)object4.nextElement();
                        object2 = new ASN1EncodableVector();
                        ((ASN1EncodableVector)object2).add((ASN1Encodable)object3);
                        ((ASN1EncodableVector)object2).add(new DERSet(object6.getBagAttribute((ASN1ObjectIdentifier)object3)));
                        contentInfoArray.add(new DERSequence((ASN1EncodableVector)object2));
                        bl4 = true;
                    }
                }
                if (!bl4) {
                    object6 = new ASN1EncodableVector();
                    ((ASN1EncodableVector)object6).add(pkcs_9_at_localKeyId);
                    ((ASN1EncodableVector)object6).add(new DERSet(this.createSubjectKeyId(((Certificate)object8).getPublicKey())));
                    contentInfoArray.add(new DERSequence((ASN1EncodableVector)object6));
                    object6 = new ASN1EncodableVector();
                    ((ASN1EncodableVector)object6).add(pkcs_9_at_friendlyName);
                    ((ASN1EncodableVector)object6).add(new DERSet(new DERBMPString((String)object9)));
                    contentInfoArray.add(new DERSequence((ASN1EncodableVector)object6));
                }
                object6 = new SafeBag(certBag, ((CertBag)object7).toASN1Primitive(), new DERSet((ASN1EncodableVector)contentInfoArray));
                ((ASN1EncodableVector)object11).add((ASN1Encodable)object6);
                ((Hashtable)object12).put(object8, object8);
            }
            catch (CertificateEncodingException certificateEncodingException) {
                throw new IOException("Error encoding certificate: " + certificateEncodingException.toString());
            }
        }
        enumeration2 = this.certs.keys();
        while (enumeration2.hasMoreElements()) {
            try {
                object9 = (String)enumeration2.nextElement();
                object8 = (Certificate)this.certs.get((String)object9);
                if (this.keys.get((String)object9) != null) continue;
                SafeBag safeBag = this.createSafeBag((String)object9, (Certificate)object8, bl2);
                ((ASN1EncodableVector)object11).add(safeBag);
                ((Hashtable)object12).put(object8, object8);
            }
            catch (CertificateEncodingException certificateEncodingException) {
                throw new IOException("Error encoding certificate: " + certificateEncodingException.toString());
            }
        }
        object9 = this.getUsedCertificateSet();
        enumeration2 = this.chainCerts.keys();
        while (enumeration2.hasMoreElements()) {
            try {
                object8 = (CertId)enumeration2.nextElement();
                Certificate certificate = (Certificate)this.chainCerts.get(object8);
                if (!object9.contains(certificate) || ((Hashtable)object12).get(certificate) != null) continue;
                object7 = new CertBag(x509Certificate, new DEROctetString(certificate.getEncoded()));
                contentInfoArray = new ASN1EncodableVector();
                if (certificate instanceof PKCS12BagAttributeCarrier) {
                    object6 = (PKCS12BagAttributeCarrier)((Object)certificate);
                    object5 = object6.getBagAttributeKeys();
                    while (object5.hasMoreElements()) {
                        object4 = (ASN1ObjectIdentifier)object5.nextElement();
                        if (((ASN1Primitive)object4).equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId)) continue;
                        object3 = new ASN1EncodableVector();
                        ((ASN1EncodableVector)object3).add((ASN1Encodable)object4);
                        ((ASN1EncodableVector)object3).add(new DERSet(object6.getBagAttribute((ASN1ObjectIdentifier)object4)));
                        contentInfoArray.add(new DERSequence((ASN1EncodableVector)object3));
                    }
                }
                object6 = new SafeBag(certBag, ((CertBag)object7).toASN1Primitive(), new DERSet((ASN1EncodableVector)contentInfoArray));
                ((ASN1EncodableVector)object11).add((ASN1Encodable)object6);
            }
            catch (CertificateEncodingException certificateEncodingException) {
                throw new IOException("Error encoding certificate: " + certificateEncodingException.toString());
            }
        }
        object8 = new DERSequence((ASN1EncodableVector)object11).getEncoded("DER");
        byte[] byArray2 = this.cryptData(true, (AlgorithmIdentifier)object10, cArray, false, (byte[])object8);
        object7 = new EncryptedData(data, (AlgorithmIdentifier)object10, new BEROctetString(byArray2));
        contentInfoArray = new ContentInfo[]{new ContentInfo(data, (ASN1Encodable)object14), new ContentInfo(encryptedData, ((EncryptedData)object7).toASN1Primitive())};
        object6 = new AuthenticatedSafe(contentInfoArray);
        object5 = ((ASN1Object)object6).getEncoded(bl ? "DER" : "BER");
        object4 = new ContentInfo(data, new BEROctetString((byte[])object5));
        object3 = new byte[this.saltLength];
        this.random.nextBytes((byte[])object3);
        object2 = ((ASN1OctetString)((ContentInfo)object4).getContent()).getOctets();
        if (this.keyAlgorithm.equals(NISTObjectIdentifiers.id_aes256_GCM)) {
            macData = null;
        } else {
            try {
                object = this.calculatePbeMac(this.macAlgorithm.getAlgorithm(), (byte[])object3, this.itCount, cArray, false, (byte[])object2);
                DigestInfo digestInfo = new DigestInfo(this.macAlgorithm, (byte[])object);
                macData = new MacData(digestInfo, (byte[])object3, this.itCount);
            }
            catch (Exception exception) {
                throw new IOException("error constructing MAC: " + exception.toString());
            }
        }
        object = new Pfx((ContentInfo)object4, macData);
        ((ASN1Object)object).encodeTo(outputStream, bl ? "DER" : "BER");
    }

    private SafeBag createSafeBag(String string, Certificate certificate, boolean bl) throws CertificateEncodingException {
        Object object;
        ASN1Primitive aSN1Primitive;
        Object object2;
        CertBag certBag = new CertBag(x509Certificate, new DEROctetString(certificate.getEncoded()));
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        boolean bl2 = false;
        if (certificate instanceof PKCS12BagAttributeCarrier) {
            object2 = (PKCS12BagAttributeCarrier)((Object)certificate);
            aSN1Primitive = (ASN1BMPString)object2.getBagAttribute(pkcs_9_at_friendlyName);
            if (bl && (aSN1Primitive == null || !((ASN1BMPString)aSN1Primitive).getString().equals(string)) && string != null) {
                object2.setBagAttribute(pkcs_9_at_friendlyName, new DERBMPString(string));
            }
            object = object2.getBagAttributeKeys();
            while (object.hasMoreElements()) {
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)object.nextElement();
                if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.pkcs_9_at_localKeyId) || aSN1ObjectIdentifier.equals(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage)) continue;
                ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
                aSN1EncodableVector2.add(aSN1ObjectIdentifier);
                aSN1EncodableVector2.add(new DERSet(object2.getBagAttribute(aSN1ObjectIdentifier)));
                aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector2));
                bl2 = true;
            }
        }
        if (!bl2) {
            object2 = new ASN1EncodableVector();
            ((ASN1EncodableVector)object2).add(pkcs_9_at_friendlyName);
            ((ASN1EncodableVector)object2).add(new DERSet(new DERBMPString(string)));
            aSN1EncodableVector.add(new DERSequence((ASN1EncodableVector)object2));
        }
        if (certificate instanceof X509Certificate) {
            object2 = TBSCertificate.getInstance(((X509Certificate)certificate).getTBSCertificate());
            aSN1Primitive = Extensions.getExtensionValue(((TBSCertificate)object2).getExtensions(), Extension.extendedKeyUsage);
            object = aSN1Primitive != null ? new DERSet(ExtendedKeyUsage.getInstance(((ASN1OctetString)aSN1Primitive).getOctets()).getUsages()) : new DERSet(KeyPurposeId.anyExtendedKeyUsage);
            aSN1EncodableVector.add(new DERSequence(MiscObjectIdentifiers.id_oracle_pkcs12_trusted_key_usage, (ASN1Encodable)object));
        }
        return new SafeBag(PKCS12KeyStoreSpi.certBag, certBag.toASN1Primitive(), new DERSet(aSN1EncodableVector));
    }

    private Set getUsedCertificateSet() {
        Object object;
        String string;
        HashSet<Object> hashSet = new HashSet<Object>();
        Enumeration enumeration = this.keys.keys();
        while (enumeration.hasMoreElements()) {
            string = (String)enumeration.nextElement();
            object = this.engineGetCertificateChain(string);
            for (int i = 0; i != ((Certificate[])object).length; ++i) {
                hashSet.add(object[i]);
            }
        }
        enumeration = this.certs.keys();
        while (enumeration.hasMoreElements()) {
            string = (String)enumeration.nextElement();
            object = this.engineGetCertificate(string);
            hashSet.add(object);
        }
        return hashSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] calculatePbeMac(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] byArray, int n, char[] cArray, boolean bl, byte[] byArray2) throws Exception {
        Object object;
        if (PKCSObjectIdentifiers.id_PBMAC1.equals(aSN1ObjectIdentifier)) {
            object = PBMAC1Params.getInstance(this.macAlgorithm.getParameters());
            if (object == null) {
                throw new IOException("If the DigestAlgorithmIdentifier is id-PBMAC1, then the parameters field must contain valid PBMAC1-params parameters.");
            }
            if (PKCSObjectIdentifiers.id_PBKDF2.equals(((PBMAC1Params)object).getKeyDerivationFunc().getAlgorithm())) {
                PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(((PBMAC1Params)object).getKeyDerivationFunc().getParameters());
                if (pBKDF2Params.getKeyLength() == null) {
                    throw new IOException("Key length must be present when using PBMAC1.");
                }
                HMac hMac = new HMac(PKCS12KeyStoreSpi.getPrf(((PBMAC1Params)object).getMessageAuthScheme().getAlgorithm()));
                PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator(PKCS12KeyStoreSpi.getPrf(pBKDF2Params.getPrf().getAlgorithm()));
                pKCS5S2ParametersGenerator.init(Strings.toUTF8ByteArray(cArray), pBKDF2Params.getSalt(), BigIntegers.intValueExact(pBKDF2Params.getIterationCount()));
                CipherParameters cipherParameters = ((PBEParametersGenerator)pKCS5S2ParametersGenerator).generateDerivedParameters(BigIntegers.intValueExact(pBKDF2Params.getKeyLength()) * 8);
                Arrays.clear(pKCS5S2ParametersGenerator.getPassword());
                hMac.init(cipherParameters);
                hMac.update(byArray2, 0, byArray2.length);
                byte[] byArray3 = new byte[hMac.getMacSize()];
                hMac.doFinal(byArray3, 0);
                return byArray3;
            }
        }
        object = new PBEParameterSpec(byArray, n);
        PKCS12Key pKCS12Key = new PKCS12Key(cArray, bl);
        try {
            Mac mac = this.helper.createMac(aSN1ObjectIdentifier.getId());
            mac.init(pKCS12Key, (AlgorithmParameterSpec)object);
            mac.update(byArray2);
            byte[] byArray4 = mac.doFinal();
            return byArray4;
        }
        finally {
            Arrays.clear(pKCS12Key.getPassword());
        }
    }

    private static Digest getPrf(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (PKCSObjectIdentifiers.id_hmacWithSHA256.equals(aSN1ObjectIdentifier)) {
            return new SHA256Digest();
        }
        if (PKCSObjectIdentifiers.id_hmacWithSHA512.equals(aSN1ObjectIdentifier)) {
            return new SHA512Digest();
        }
        throw new IllegalArgumentException("unknown prf id " + aSN1ObjectIdentifier);
    }

    public static class BCPKCS12KeyStore
    extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStore() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC));
        }
    }

    public static class BCPKCS12KeyStore3DES
    extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStore3DES() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC));
        }
    }

    public static class BCPKCS12KeyStoreAES256
    extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStoreAES256() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_CBC, NISTObjectIdentifiers.id_aes128_CBC));
        }
    }

    public static class BCPKCS12KeyStoreAES256GCM
    extends AdaptingKeyStoreSpi {
        public BCPKCS12KeyStoreAES256GCM() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_GCM, NISTObjectIdentifiers.id_aes128_GCM));
        }
    }

    private class CertId {
        byte[] id;

        CertId(PublicKey publicKey) {
            this.id = PKCS12KeyStoreSpi.this.createSubjectKeyId(publicKey).getKeyIdentifier();
        }

        CertId(byte[] byArray) {
            this.id = byArray;
        }

        public int hashCode() {
            return Arrays.hashCode(this.id);
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof CertId)) {
                return false;
            }
            CertId certId = (CertId)object;
            return Arrays.areEqual(this.id, certId.id);
        }
    }

    public static class DefPKCS12KeyStore
    extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStore() {
            super(new DefaultJcaJceHelper(), new PKCS12KeyStoreSpi(new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC));
        }
    }

    public static class DefPKCS12KeyStore3DES
    extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStore3DES() {
            super(new DefaultJcaJceHelper(), new PKCS12KeyStoreSpi(new DefaultJcaJceHelper(), PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC));
        }
    }

    public static class DefPKCS12KeyStoreAES256
    extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStoreAES256() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_CBC, NISTObjectIdentifiers.id_aes128_CBC));
        }
    }

    public static class DefPKCS12KeyStoreAES256GCM
    extends AdaptingKeyStoreSpi {
        public DefPKCS12KeyStoreAES256GCM() {
            super(new BCJcaJceHelper(), new PKCS12KeyStoreSpi(new BCJcaJceHelper(), NISTObjectIdentifiers.id_aes256_GCM, NISTObjectIdentifiers.id_aes128_GCM));
        }
    }

    private static class DefaultSecretKeyProvider {
        private final Map KEY_SIZES;

        DefaultSecretKeyProvider() {
            HashMap<ASN1ObjectIdentifier, Integer> hashMap = new HashMap<ASN1ObjectIdentifier, Integer>();
            hashMap.put(new ASN1ObjectIdentifier("1.2.840.113533.7.66.10"), Integers.valueOf(128));
            hashMap.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
            hashMap.put(NISTObjectIdentifiers.id_aes128_CBC, Integers.valueOf(128));
            hashMap.put(NISTObjectIdentifiers.id_aes192_CBC, Integers.valueOf(192));
            hashMap.put(NISTObjectIdentifiers.id_aes256_CBC, Integers.valueOf(256));
            hashMap.put(NISTObjectIdentifiers.id_aes128_GCM, Integers.valueOf(128));
            hashMap.put(NISTObjectIdentifiers.id_aes256_GCM, Integers.valueOf(256));
            hashMap.put(NTTObjectIdentifiers.id_camellia128_cbc, Integers.valueOf(128));
            hashMap.put(NTTObjectIdentifiers.id_camellia192_cbc, Integers.valueOf(192));
            hashMap.put(NTTObjectIdentifiers.id_camellia256_cbc, Integers.valueOf(256));
            hashMap.put(CryptoProObjectIdentifiers.gostR28147_gcfb, Integers.valueOf(256));
            this.KEY_SIZES = Collections.unmodifiableMap(hashMap);
        }

        public int getKeySize(AlgorithmIdentifier algorithmIdentifier) {
            Integer n = (Integer)this.KEY_SIZES.get(algorithmIdentifier.getAlgorithm());
            if (n != null) {
                return n;
            }
            return -1;
        }
    }

    private static class IgnoresCaseHashtable {
        private Hashtable orig = new Hashtable();
        private Hashtable keys = new Hashtable();

        private IgnoresCaseHashtable() {
        }

        public void put(String string, Object object) {
            String string2 = string == null ? null : Strings.toLowerCase(string);
            String string3 = (String)this.keys.get(string2);
            if (string3 != null) {
                this.orig.remove(string3);
            }
            this.keys.put(string2, string);
            this.orig.put(string, object);
        }

        public Enumeration keys() {
            return new Hashtable(this.orig).keys();
        }

        public Object remove(String string) {
            String string2 = (String)this.keys.remove(string == null ? null : Strings.toLowerCase(string));
            if (string2 == null) {
                return null;
            }
            return this.orig.remove(string2);
        }

        public Object get(String string) {
            String string2 = (String)this.keys.get(string == null ? null : Strings.toLowerCase(string));
            if (string2 == null) {
                return null;
            }
            return this.orig.get(string2);
        }

        public Enumeration elements() {
            return this.orig.elements();
        }

        public int size() {
            return this.orig.size();
        }
    }
}

