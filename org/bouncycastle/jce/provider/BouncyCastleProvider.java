/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServiceConstraintsException;
import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jce.provider.BouncyCastleProviderConfiguration;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.lms.LMSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.snova.SnovaKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;
import org.bouncycastle.util.Strings;

public final class BouncyCastleProvider
extends Provider
implements ConfigurableProvider {
    private static final Logger LOG = Logger.getLogger(BouncyCastleProvider.class.getName());
    private static String info = "BouncyCastle Security Provider v1.83";
    public static final String PROVIDER_NAME = "BC";
    public static final ProviderConfiguration CONFIGURATION = new BouncyCastleProviderConfiguration();
    private static final Map keyInfoConverters = new HashMap();
    private static final Class revChkClass = ClassUtil.loadClass(BouncyCastleProvider.class, "java.security.cert.PKIXRevocationChecker");
    private static final String SYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.symmetric.";
    private static final String[] SYMMETRIC_GENERIC = new String[]{"PBEPBKDF1", "PBEPBKDF2", "PBEPKCS12", "TLSKDF", "SCRYPT", "HKDF"};
    private static final String[] SYMMETRIC_MACS = new String[]{"SipHash", "SipHash128", "Poly1305"};
    private static final CryptoServiceProperties[] SYMMETRIC_CIPHERS = new CryptoServiceProperties[]{BouncyCastleProvider.service("AES", 256), BouncyCastleProvider.service("ARC4", 20), BouncyCastleProvider.service("ARIA", 256), BouncyCastleProvider.service("Blowfish", 128), BouncyCastleProvider.service("Camellia", 256), BouncyCastleProvider.service("CAST5", 128), BouncyCastleProvider.service("CAST6", 256), BouncyCastleProvider.service("ChaCha", 128), BouncyCastleProvider.service("DES", 56), BouncyCastleProvider.service("DESede", 112), BouncyCastleProvider.service("GOST28147", 128), BouncyCastleProvider.service("Grainv1", 128), BouncyCastleProvider.service("Grain128", 128), BouncyCastleProvider.service("HC128", 128), BouncyCastleProvider.service("HC256", 256), BouncyCastleProvider.service("IDEA", 128), BouncyCastleProvider.service("Noekeon", 128), BouncyCastleProvider.service("RC2", 128), BouncyCastleProvider.service("RC5", 128), BouncyCastleProvider.service("RC6", 256), BouncyCastleProvider.service("Rijndael", 256), BouncyCastleProvider.service("Salsa20", 128), BouncyCastleProvider.service("SEED", 128), BouncyCastleProvider.service("Serpent", 256), BouncyCastleProvider.service("Shacal2", 128), BouncyCastleProvider.service("Skipjack", 80), BouncyCastleProvider.service("SM4", 128), BouncyCastleProvider.service("TEA", 128), BouncyCastleProvider.service("Twofish", 256), BouncyCastleProvider.service("Threefish", 128), BouncyCastleProvider.service("VMPC", 128), BouncyCastleProvider.service("VMPCKSA3", 128), BouncyCastleProvider.service("XTEA", 128), BouncyCastleProvider.service("XSalsa20", 128), BouncyCastleProvider.service("OpenSSLPBKDF", 128), BouncyCastleProvider.service("DSTU7624", 256), BouncyCastleProvider.service("GOST3412_2015", 256), BouncyCastleProvider.service("Zuc", 128)};
    private static final String ASYMMETRIC_PACKAGE = "org.bouncycastle.jcajce.provider.asymmetric.";
    private static final String[] ASYMMETRIC_GENERIC = new String[]{"X509", "IES", "COMPOSITE", "EXTERNAL", "CompositeSignatures", "NoSig"};
    private static final String[] ASYMMETRIC_CIPHERS = new String[]{"DSA", "DH", "EC", "RSA", "GOST", "ECGOST", "ElGamal", "DSTU4145", "GM", "EdEC", "LMS", "SPHINCSPlus", "Dilithium", "Falcon", "NTRU", "CONTEXT", "SLHDSA", "MLDSA", "MLKEM"};
    private static final String DIGEST_PACKAGE = "org.bouncycastle.jcajce.provider.digest.";
    private static final String[] DIGESTS = new String[]{"GOST3411", "Keccak", "MD2", "MD4", "MD5", "SHA1", "RIPEMD128", "RIPEMD160", "RIPEMD256", "RIPEMD320", "SHA224", "SHA256", "SHA384", "SHA512", "SHA3", "Skein", "SM3", "Tiger", "Whirlpool", "Blake2b", "Blake2s", "DSTU7564", "Haraka", "Blake3"};
    private static final String KEYSTORE_PACKAGE = "org.bouncycastle.jcajce.provider.keystore.";
    private static final String[] KEYSTORES = new String[]{"BC", "BCFKS", "PKCS12"};
    private static final String SECURE_RANDOM_PACKAGE = "org.bouncycastle.jcajce.provider.drbg.";
    private static final String[] SECURE_RANDOMS = new String[]{"DRBG"};
    private static final String KDF_PACKAGE = "org.bouncycastle.jcajce.provider.kdf.";
    private static final String[] KDFS = new String[]{"HKDF", "PBEPBKDF2", "SCRYPT"};
    private Map<String, Provider.Service> serviceMap = new ConcurrentHashMap<String, Provider.Service>();

    public BouncyCastleProvider() {
        super(PROVIDER_NAME, 1.83, info);
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                BouncyCastleProvider.this.setup();
                return null;
            }
        });
    }

    private void setup() {
        this.loadAlgorithms(DIGEST_PACKAGE, DIGESTS);
        this.loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_GENERIC);
        this.loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_MACS);
        this.loadAlgorithms(SYMMETRIC_PACKAGE, SYMMETRIC_CIPHERS);
        this.loadAlgorithms(ASYMMETRIC_PACKAGE, ASYMMETRIC_GENERIC);
        this.loadAlgorithms(ASYMMETRIC_PACKAGE, ASYMMETRIC_CIPHERS);
        this.loadAlgorithms(KEYSTORE_PACKAGE, KEYSTORES);
        this.loadAlgorithms(SECURE_RANDOM_PACKAGE, SECURE_RANDOMS);
        this.loadAlgorithms(KDF_PACKAGE, KDFS);
        this.loadPQCKeys();
        this.put("X509Store.CERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertCollection");
        this.put("X509Store.ATTRIBUTECERTIFICATE/COLLECTION", "org.bouncycastle.jce.provider.X509StoreAttrCertCollection");
        this.put("X509Store.CRL/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCRLCollection");
        this.put("X509Store.CERTIFICATEPAIR/COLLECTION", "org.bouncycastle.jce.provider.X509StoreCertPairCollection");
        this.put("X509Store.CERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCerts");
        this.put("X509Store.CRL/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCRLs");
        this.put("X509Store.ATTRIBUTECERTIFICATE/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPAttrCerts");
        this.put("X509Store.CERTIFICATEPAIR/LDAP", "org.bouncycastle.jce.provider.X509StoreLDAPCertPairs");
        this.put("X509StreamParser.CERTIFICATE", "org.bouncycastle.jce.provider.X509CertParser");
        this.put("X509StreamParser.ATTRIBUTECERTIFICATE", "org.bouncycastle.jce.provider.X509AttrCertParser");
        this.put("X509StreamParser.CRL", "org.bouncycastle.jce.provider.X509CRLParser");
        this.put("X509StreamParser.CERTIFICATEPAIR", "org.bouncycastle.jce.provider.X509CertPairParser");
        this.put("Cipher.BROKENPBEWITHMD5ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithMD5AndDES");
        this.put("Cipher.BROKENPBEWITHSHA1ANDDES", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$BrokePBEWithSHA1AndDES");
        this.put("Cipher.OLDPBEWITHSHAANDTWOFISH-CBC", "org.bouncycastle.jce.provider.BrokenJCEBlockCipher$OldPBEWithSHAAndTwofish");
        if (revChkClass != null) {
            this.put("CertPathValidator.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathValidatorSpi");
            this.put("CertPathBuilder.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathBuilderSpi");
            this.put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8");
            this.put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8");
            this.put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8");
            this.put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi_8");
        } else {
            this.put("CertPathValidator.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathValidatorSpi");
            this.put("CertPathBuilder.RFC3281", "org.bouncycastle.jce.provider.PKIXAttrCertPathBuilderSpi");
            this.put("CertPathValidator.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
            this.put("CertPathBuilder.RFC3280", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
            this.put("CertPathValidator.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi");
            this.put("CertPathBuilder.PKIX", "org.bouncycastle.jce.provider.PKIXCertPathBuilderSpi");
        }
        this.put("CertStore.Collection", "org.bouncycastle.jce.provider.CertStoreCollectionSpi");
        this.put("CertStore.LDAP", "org.bouncycastle.jce.provider.X509LDAPCertStoreSpi");
        this.put("CertStore.Multi", "org.bouncycastle.jce.provider.MultiCertStoreSpi");
        this.put("Alg.Alias.CertStore.X509LDAP", "LDAP");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Provider.Service getService(String string, String string2) {
        String string3 = Strings.toUpperCase(string2);
        String string4 = string + "." + string3;
        Provider.Service service = this.serviceMap.get(string4);
        if (service == null) {
            BouncyCastleProvider bouncyCastleProvider = this;
            synchronized (bouncyCastleProvider) {
                service = !this.serviceMap.containsKey(string4) ? AccessController.doPrivileged(new PrivilegedAction<Provider.Service>(){
                    final /* synthetic */ BouncyCastleProvider this$0;
                    {
                        this.this$0 = bouncyCastleProvider;
                    }

                    @Override
                    public Provider.Service run() {
                        Provider.Service service = BouncyCastleProvider.super.getService(string, string2);
                        if (service == null || service.getClassName() == null) {
                            return null;
                        }
                        this.this$0.serviceMap.put(string4, service);
                        BouncyCastleProvider.super.remove(service.getType() + "." + service.getAlgorithm());
                        BouncyCastleProvider.super.putService(service);
                        return service;
                    }
                }) : this.serviceMap.get(string4);
            }
        }
        return service;
    }

    private void loadAlgorithms(String string, String[] stringArray) {
        for (int i = 0; i != stringArray.length; ++i) {
            this.loadServiceClass(string, stringArray[i]);
        }
    }

    private void loadAlgorithms(String string, CryptoServiceProperties[] cryptoServicePropertiesArray) {
        for (int i = 0; i != cryptoServicePropertiesArray.length; ++i) {
            CryptoServiceProperties cryptoServiceProperties = cryptoServicePropertiesArray[i];
            try {
                CryptoServicesRegistrar.checkConstraints(cryptoServiceProperties);
                this.loadServiceClass(string, cryptoServiceProperties.getServiceName());
                continue;
            }
            catch (CryptoServiceConstraintsException cryptoServiceConstraintsException) {
                if (!LOG.isLoggable(Level.FINE)) continue;
                LOG.fine("service for " + cryptoServiceProperties.getServiceName() + " ignored due to constraints");
            }
        }
    }

    private void loadServiceClass(String string, String string2) {
        Class clazz = ClassUtil.loadClass(BouncyCastleProvider.class, string + string2 + "$Mappings");
        if (clazz != null) {
            try {
                ((AlgorithmProvider)clazz.newInstance()).configure(this);
            }
            catch (Exception exception) {
                throw new InternalError("cannot create instance of " + string + string2 + "$Mappings : " + exception);
            }
        }
    }

    private void loadPQCKeys() {
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_128f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_192f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256s_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_haraka_256f_r3_simple, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_128s, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_192s, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_sha2_256s, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(new ASN1ObjectIdentifier("1.3.9999.6.4.10"), new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_128f, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_192f, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.sphincsPlus_shake_256f, new SPHINCSPlusKeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.sphincs256, new Sphincs256KeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.newHope, new NHKeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.xmss, new XMSSKeyFactorySpi());
        this.addKeyInfoConverter(IsaraObjectIdentifiers.id_alg_xmss, new XMSSKeyFactorySpi());
        this.addKeyInfoConverter(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyFactorySpi());
        this.addKeyInfoConverter(IsaraObjectIdentifiers.id_alg_xmssmt, new XMSSMTKeyFactorySpi());
        this.addKeyInfoConverter(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, new LMSKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.picnic_key, new PicnicKeyFactorySpi());
        this.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_512, new MLKEMKeyFactorySpi());
        this.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_768, new MLKEMKeyFactorySpi());
        this.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_1024, new MLKEMKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.old_falcon_512, new FalconKeyFactorySpi(BCObjectIdentifiers.old_falcon_512));
        this.addKeyInfoConverter(BCObjectIdentifiers.old_falcon_1024, new FalconKeyFactorySpi(BCObjectIdentifiers.old_falcon_1024));
        this.addKeyInfoConverter(BCObjectIdentifiers.falcon_512, new FalconKeyFactorySpi(BCObjectIdentifiers.falcon_512));
        this.addKeyInfoConverter(BCObjectIdentifiers.falcon_1024, new FalconKeyFactorySpi(BCObjectIdentifiers.falcon_1024));
        this.addKeyInfoConverter(BCObjectIdentifiers.dilithium2, new DilithiumKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.dilithium3, new DilithiumKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.dilithium5, new DilithiumKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.dilithium2_aes, new DilithiumKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.dilithium3_aes, new DilithiumKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.dilithium5_aes, new DilithiumKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mceliece348864_r3, new CMCEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mceliece460896_r3, new CMCEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mceliece6688128_r3, new CMCEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mceliece6960119_r3, new CMCEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mceliece8192128_r3, new CMCEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.bike128, new BIKEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.bike192, new BIKEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.bike256, new BIKEKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.hqc128, new HQCKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.hqc192, new HQCKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.hqc256, new HQCKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.kyber512_aes, new KyberKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.kyber768_aes, new KyberKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.kyber1024_aes, new KyberKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.ntruhps2048509, new NTRUKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.ntruhps2048677, new NTRUKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.ntruhps4096821, new NTRUKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.ntruhrss701, new NTRUKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mayo1, new MayoKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mayo2, new MayoKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mayo3, new MayoKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.mayo5, new MayoKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_4_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_24_5_5_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_25_8_3_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_8_4_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_37_17_2_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_49_11_3_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_56_25_2_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_60_10_4_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_66_15_3_shake_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_esk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_shake_ssk, new SnovaKeyFactorySpi());
        this.addKeyInfoConverter(BCObjectIdentifiers.snova_75_33_2_shake_esk, new SnovaKeyFactorySpi());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setParameter(String string, Object object) {
        ProviderConfiguration providerConfiguration = CONFIGURATION;
        synchronized (providerConfiguration) {
            ((BouncyCastleProviderConfiguration)CONFIGURATION).setParameter(string, object);
        }
    }

    @Override
    public boolean hasAlgorithm(String string, String string2) {
        return this.containsKey(string + "." + string2) || this.containsKey("Alg.Alias." + string + "." + string2);
    }

    @Override
    public void addAlgorithm(String string, String string2) {
        if (this.containsKey(string)) {
            throw new IllegalStateException("duplicate provider key (" + string + ") found");
        }
        this.put(string, string2);
    }

    @Override
    public void addAlgorithm(String string, String string2, Map<String, String> map) {
        this.addAlgorithm(string, string2);
        this.addAttributes(string, map);
    }

    @Override
    public void addAlgorithm(String string, ASN1ObjectIdentifier aSN1ObjectIdentifier, String string2) {
        this.addAlgorithm(string + "." + aSN1ObjectIdentifier, string2);
        this.addAlgorithm(string + ".OID." + aSN1ObjectIdentifier, string2);
    }

    @Override
    public void addAlgorithm(String string, ASN1ObjectIdentifier aSN1ObjectIdentifier, String string2, Map<String, String> map) {
        this.addAlgorithm(string, aSN1ObjectIdentifier, string2);
        this.addAttributes(string + "." + aSN1ObjectIdentifier, map);
        this.addAttributes(string + ".OID." + aSN1ObjectIdentifier, map);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addKeyInfoConverter(ASN1ObjectIdentifier aSN1ObjectIdentifier, AsymmetricKeyInfoConverter asymmetricKeyInfoConverter) {
        Map map = keyInfoConverters;
        synchronized (map) {
            keyInfoConverters.put(aSN1ObjectIdentifier, asymmetricKeyInfoConverter);
        }
    }

    @Override
    public AsymmetricKeyInfoConverter getKeyInfoConverter(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (AsymmetricKeyInfoConverter)keyInfoConverters.get(aSN1ObjectIdentifier);
    }

    @Override
    public void addAttributes(String string, Map<String, String> map) {
        this.put(string + " ImplementedIn", "Software");
        for (String string2 : map.keySet()) {
            String string3 = string + " " + string2;
            if (this.containsKey(string3)) {
                throw new IllegalStateException("duplicate provider attribute key (" + string3 + ") found");
            }
            this.put(string3, map.get(string2));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static AsymmetricKeyInfoConverter getAsymmetricKeyInfoConverter(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Map map = keyInfoConverters;
        synchronized (map) {
            return (AsymmetricKeyInfoConverter)keyInfoConverters.get(aSN1ObjectIdentifier);
        }
    }

    public static PublicKey getPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().on(BCObjectIdentifiers.picnic_key)) {
            return new PicnicKeyFactorySpi().generatePublic(subjectPublicKeyInfo);
        }
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = BouncyCastleProvider.getAsymmetricKeyInfoConverter(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePublic(subjectPublicKeyInfo);
    }

    public static PrivateKey getPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = BouncyCastleProvider.getAsymmetricKeyInfoConverter(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePrivate(privateKeyInfo);
    }

    private static CryptoServiceProperties service(String string, int n) {
        return new JcaCryptoService(string, n);
    }

    private static class JcaCryptoService
    implements CryptoServiceProperties {
        private final String name;
        private final int bitsOfSecurity;

        JcaCryptoService(String string, int n) {
            this.name = string;
            this.bitsOfSecurity = n;
        }

        @Override
        public int bitsOfSecurity() {
            return this.bitsOfSecurity;
        }

        @Override
        public String getServiceName() {
            return this.name;
        }

        @Override
        public CryptoServicePurpose getPurpose() {
            return CryptoServicePurpose.ANY;
        }

        @Override
        public Object getParams() {
            return null;
        }
    }
}

