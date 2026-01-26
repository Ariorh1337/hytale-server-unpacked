/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public class BouncyCastlePQCProvider
extends Provider
implements ConfigurableProvider {
    private static String info = "BouncyCastle Post-Quantum Security Provider v1.83";
    public static String PROVIDER_NAME = "BCPQC";
    public static final ProviderConfiguration CONFIGURATION = null;
    private static final Map keyInfoConverters = new HashMap();
    private static final String ALGORITHM_PACKAGE = "org.bouncycastle.pqc.jcajce.provider.";
    private static final String[] ALGORITHMS = new String[]{"SPHINCS", "LMS", "NH", "XMSS", "SPHINCSPlus", "CMCE", "Frodo", "SABER", "Picnic", "NTRU", "Falcon", "Kyber", "Dilithium", "NTRUPrime", "BIKE", "HQC", "Rainbow", "Mayo", "Snova"};

    public BouncyCastlePQCProvider() {
        super(PROVIDER_NAME, 1.83, info);
        AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                BouncyCastlePQCProvider.this.setup();
                return null;
            }
        });
    }

    private void setup() {
        this.loadAlgorithms(ALGORITHM_PACKAGE, ALGORITHMS);
    }

    private void loadAlgorithms(String string, String[] stringArray) {
        for (int i = 0; i != stringArray.length; ++i) {
            Class clazz = BouncyCastlePQCProvider.loadClass(BouncyCastlePQCProvider.class, string + stringArray[i] + "$Mappings");
            if (clazz == null) continue;
            try {
                ((AlgorithmProvider)clazz.newInstance()).configure(this);
                continue;
            }
            catch (Exception exception) {
                throw new InternalError("cannot create instance of " + string + stringArray[i] + "$Mappings : " + exception);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setParameter(String string, Object object) {
        ProviderConfiguration providerConfiguration = CONFIGURATION;
        synchronized (providerConfiguration) {
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
        if (!this.containsKey(string + "." + string2)) {
            throw new IllegalStateException("primary key (" + string + "." + string2 + ") not found");
        }
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
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = BouncyCastlePQCProvider.getAsymmetricKeyInfoConverter(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePublic(subjectPublicKeyInfo);
    }

    public static PrivateKey getPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        AsymmetricKeyInfoConverter asymmetricKeyInfoConverter = BouncyCastlePQCProvider.getAsymmetricKeyInfoConverter(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
        if (asymmetricKeyInfoConverter == null) {
            return null;
        }
        return asymmetricKeyInfoConverter.generatePrivate(privateKeyInfo);
    }

    static Class loadClass(Class clazz, String string) {
        try {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                return classLoader.loadClass(string);
            }
            return (Class)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    try {
                        return Class.forName(string);
                    }
                    catch (Exception exception) {
                        return null;
                    }
                }
            });
        }
        catch (ClassNotFoundException classNotFoundException) {
            return null;
        }
    }
}

