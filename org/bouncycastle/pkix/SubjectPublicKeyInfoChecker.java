/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class SubjectPublicKeyInfoChecker {
    private static final Cache validatedQs = new Cache();
    private static final Cache validatedMods = new Cache();
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);

    public static void checkInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (X9ObjectIdentifiers.id_ecPublicKey.equals(aSN1ObjectIdentifier)) {
            X962Parameters x962Parameters = X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            if (x962Parameters.isImplicitlyCA() || x962Parameters.isNamedCurve()) {
                return;
            }
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(x962Parameters.getParameters());
            X9FieldID x9FieldID = X9FieldID.getInstance(aSN1Sequence.getObjectAt(1));
            if (x9FieldID.getIdentifier().equals(X9FieldID.prime_field)) {
                BigInteger bigInteger = ASN1Integer.getInstance(x9FieldID.getParameters()).getValue();
                if (validatedQs.contains(bigInteger)) {
                    return;
                }
                int n = Properties.asInteger("org.bouncycastle.ec.fp_max_size", 1042);
                int n2 = Properties.asInteger("org.bouncycastle.ec.fp_certainty", 100);
                int n3 = bigInteger.bitLength();
                if (n < n3) {
                    throw new IllegalArgumentException("Fp q value out of range");
                }
                if (Primes.hasAnySmallFactors(bigInteger) || !Primes.isMRProbablePrime(bigInteger, CryptoServicesRegistrar.getSecureRandom(), SubjectPublicKeyInfoChecker.getNumberOfIterations(n3, n2))) {
                    throw new IllegalArgumentException("Fp q value not prime");
                }
                validatedQs.add(bigInteger);
            }
        } else if (PKCSObjectIdentifiers.rsaEncryption.equals(aSN1ObjectIdentifier) || X509ObjectIdentifiers.id_ea_rsa.equals(aSN1ObjectIdentifier) || PKCSObjectIdentifiers.id_RSAES_OAEP.equals(aSN1ObjectIdentifier) || PKCSObjectIdentifiers.id_RSASSA_PSS.equals(aSN1ObjectIdentifier)) {
            RSAPublicKey rSAPublicKey;
            try {
                rSAPublicKey = RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("unable to parse RSA key");
            }
            if ((rSAPublicKey.getPublicExponent().intValue() & 1) == 0) {
                throw new IllegalArgumentException("RSA publicExponent is even");
            }
            if (!validatedMods.contains(rSAPublicKey.getModulus())) {
                SubjectPublicKeyInfoChecker.validate(rSAPublicKey.getModulus());
                validatedMods.add(rSAPublicKey.getModulus());
            }
        }
    }

    private static boolean hasAnySmallFactors(BigInteger bigInteger) {
        BigInteger bigInteger2 = bigInteger;
        BigInteger bigInteger3 = SMALL_PRIMES_PRODUCT;
        if (bigInteger.compareTo(SMALL_PRIMES_PRODUCT) < 0) {
            bigInteger2 = SMALL_PRIMES_PRODUCT;
            bigInteger3 = bigInteger;
        }
        return !BigIntegers.modOddIsCoprimeVar(bigInteger2, bigInteger3);
    }

    private static void validate(BigInteger bigInteger) {
        if ((bigInteger.intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA modulus is even");
        }
        if (Properties.isOverrideSet("org.bouncycastle.rsa.allow_unsafe_mod")) {
            return;
        }
        int n = Properties.asInteger("org.bouncycastle.rsa.max_size", 16384);
        if (n < bigInteger.bitLength()) {
            throw new IllegalArgumentException("RSA modulus out of range");
        }
        if (SubjectPublicKeyInfoChecker.hasAnySmallFactors(bigInteger)) {
            throw new IllegalArgumentException("RSA modulus has a small prime factor");
        }
        int n2 = bigInteger.bitLength() / 2;
        int n3 = n2 >= 1536 ? 3 : (n2 >= 1024 ? 4 : (n2 >= 512 ? 7 : 50));
        Primes.MROutput mROutput = Primes.enhancedMRProbablePrimeTest(bigInteger, CryptoServicesRegistrar.getSecureRandom(), n3);
        if (!mROutput.isProvablyComposite()) {
            throw new IllegalArgumentException("RSA modulus is not composite");
        }
    }

    private static int getNumberOfIterations(int n, int n2) {
        if (n >= 1536) {
            return n2 <= 100 ? 3 : (n2 <= 128 ? 4 : 4 + (n2 - 128 + 1) / 2);
        }
        if (n >= 1024) {
            return n2 <= 100 ? 4 : (n2 <= 112 ? 5 : 5 + (n2 - 112 + 1) / 2);
        }
        if (n >= 512) {
            return n2 <= 80 ? 5 : (n2 <= 100 ? 7 : 7 + (n2 - 100 + 1) / 2);
        }
        return n2 <= 80 ? 40 : 40 + (n2 - 80 + 1) / 2;
    }

    public static boolean setThreadOverride(String string, boolean bl) {
        return Properties.setThreadOverride(string, bl);
    }

    public static boolean removeThreadOverride(String string) {
        return Properties.removeThreadOverride(string);
    }

    private static class Cache {
        private final Map<BigInteger, Boolean> values = new WeakHashMap<BigInteger, Boolean>();
        private final BigInteger[] preserve = new BigInteger[8];
        private int preserveCounter = 0;

        private Cache() {
        }

        public synchronized void add(BigInteger bigInteger) {
            this.values.put(bigInteger, Boolean.TRUE);
            this.preserve[this.preserveCounter] = bigInteger;
            this.preserveCounter = (this.preserveCounter + 1) % this.preserve.length;
        }

        public synchronized boolean contains(BigInteger bigInteger) {
            return this.values.containsKey(bigInteger);
        }

        public synchronized int size() {
            return this.values.size();
        }

        public synchronized void clear() {
            this.values.clear();
            for (int i = 0; i != this.preserve.length; ++i) {
                this.preserve[i] = null;
            }
        }
    }

    private static class Properties {
        private static final ThreadLocal threadProperties = new ThreadLocal();

        private Properties() {
        }

        static boolean isOverrideSet(String string) {
            try {
                return Properties.isSetTrue(Properties.getPropertyValue(string));
            }
            catch (AccessControlException accessControlException) {
                return false;
            }
        }

        static boolean setThreadOverride(String string, boolean bl) {
            boolean bl2 = Properties.isOverrideSet(string);
            HashMap<String, String> hashMap = (HashMap<String, String>)threadProperties.get();
            if (hashMap == null) {
                hashMap = new HashMap<String, String>();
                threadProperties.set(hashMap);
            }
            hashMap.put(string, bl ? "true" : "false");
            return bl2;
        }

        static boolean removeThreadOverride(String string) {
            String string2;
            Map map = (Map)threadProperties.get();
            if (map != null && (string2 = (String)map.remove(string)) != null) {
                if (map.isEmpty()) {
                    threadProperties.remove();
                }
                return "true".equals(Strings.toLowerCase(string2));
            }
            return false;
        }

        static int asInteger(String string, int n) {
            String string2 = Properties.getPropertyValue(string);
            if (string2 != null) {
                return Integer.parseInt(string2);
            }
            return n;
        }

        static String getPropertyValue(final String string) {
            String string2;
            String string3 = (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return Security.getProperty(string);
                }
            });
            if (string3 != null) {
                return string3;
            }
            Map map = (Map)threadProperties.get();
            if (map != null && (string2 = (String)map.get(string)) != null) {
                return string2;
            }
            return (String)AccessController.doPrivileged(new PrivilegedAction(){

                public Object run() {
                    return System.getProperty(string);
                }
            });
        }

        private static boolean isSetTrue(String string) {
            if (string == null || string.length() != 4) {
                return false;
            }
            return !(string.charAt(0) != 't' && string.charAt(0) != 'T' || string.charAt(1) != 'r' && string.charAt(1) != 'R' || string.charAt(2) != 'u' && string.charAt(2) != 'U' || string.charAt(3) != 'e' && string.charAt(3) != 'E');
        }
    }
}

