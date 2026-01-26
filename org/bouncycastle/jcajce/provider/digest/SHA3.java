/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.digest;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.digests.ParallelHash;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.TupleHash;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.digest.BCMessageDigest;
import org.bouncycastle.jcajce.provider.digest.DigestAlgorithmProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;

public class SHA3 {
    private SHA3() {
    }

    public static class Digest224
    extends DigestSHA3 {
        public Digest224() {
            super(224);
        }
    }

    public static class Digest256
    extends DigestSHA3 {
        public Digest256() {
            super(256);
        }
    }

    public static class Digest384
    extends DigestSHA3 {
        public Digest384() {
            super(384);
        }
    }

    public static class Digest512
    extends DigestSHA3 {
        public Digest512() {
            super(512);
        }
    }

    public static class DigestParallelHash
    extends BCMessageDigest
    implements Cloneable {
        public DigestParallelHash(int n, int n2) {
            super(new ParallelHash(n, null, 128, n2));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
            bCMessageDigest.digest = new ParallelHash((ParallelHash)this.digest);
            return bCMessageDigest;
        }
    }

    public static class DigestParallelHash128_256
    extends DigestParallelHash {
        public DigestParallelHash128_256() {
            super(128, 256);
        }
    }

    public static class DigestParallelHash256_512
    extends DigestParallelHash {
        public DigestParallelHash256_512() {
            super(256, 512);
        }
    }

    public static class DigestSHA3
    extends BCMessageDigest
    implements Cloneable {
        public DigestSHA3(int n) {
            super(new SHA3Digest(n));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
            bCMessageDigest.digest = new SHA3Digest((SHA3Digest)this.digest);
            return bCMessageDigest;
        }
    }

    public static class DigestSHAKE
    extends BCMessageDigest
    implements Cloneable {
        public DigestSHAKE(int n, int n2) {
            super(new SHAKEDigest(n));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
            bCMessageDigest.digest = new SHAKEDigest((SHAKEDigest)this.digest);
            return bCMessageDigest;
        }
    }

    public static class DigestShake128_256
    extends DigestSHAKE {
        public DigestShake128_256() {
            super(128, 256);
        }
    }

    public static class DigestShake256_512
    extends DigestSHAKE {
        public DigestShake256_512() {
            super(256, 512);
        }
    }

    public static class DigestTupleHash
    extends BCMessageDigest
    implements Cloneable {
        public DigestTupleHash(int n, int n2) {
            super(new TupleHash(n, null, n2));
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            BCMessageDigest bCMessageDigest = (BCMessageDigest)super.clone();
            bCMessageDigest.digest = new TupleHash((TupleHash)this.digest);
            return bCMessageDigest;
        }
    }

    public static class DigestTupleHash128_256
    extends DigestTupleHash {
        public DigestTupleHash128_256() {
            super(128, 256);
        }
    }

    public static class DigestTupleHash256_512
    extends DigestTupleHash {
        public DigestTupleHash256_512() {
            super(256, 512);
        }
    }

    public static class HashMac224
    extends HashMacSHA3 {
        public HashMac224() {
            super(224);
        }
    }

    public static class HashMac256
    extends HashMacSHA3 {
        public HashMac256() {
            super(256);
        }
    }

    public static class HashMac384
    extends HashMacSHA3 {
        public HashMac384() {
            super(384);
        }
    }

    public static class HashMac512
    extends HashMacSHA3 {
        public HashMac512() {
            super(512);
        }
    }

    public static class HashMacSHA3
    extends BaseMac {
        public HashMacSHA3(int n) {
            super(new HMac(new SHA3Digest(n)));
        }
    }

    public static class KMac128
    extends BaseMac {
        public KMac128() {
            super(new KMAC(128, new byte[0]));
        }
    }

    public static class KMac256
    extends BaseMac {
        public KMac256() {
            super(new KMAC(256, new byte[0]));
        }
    }

    public static class KeyFactory224
    extends KeyFactorySHA3 {
        public KeyFactory224() {
            super(224, NISTObjectIdentifiers.id_hmacWithSHA3_224);
        }
    }

    public static class KeyFactory256
    extends KeyFactorySHA3 {
        public KeyFactory256() {
            super(256, NISTObjectIdentifiers.id_hmacWithSHA3_256);
        }
    }

    public static class KeyFactory384
    extends KeyFactorySHA3 {
        public KeyFactory384() {
            super(384, NISTObjectIdentifiers.id_hmacWithSHA3_384);
        }
    }

    public static class KeyFactory512
    extends KeyFactorySHA3 {
        public KeyFactory512() {
            super(512, NISTObjectIdentifiers.id_hmacWithSHA3_512);
        }
    }

    public static class KeyFactoryKMAC
    extends BaseSecretKeyFactory {
        public KeyFactoryKMAC(int n, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
            super("KMAC" + n, aSN1ObjectIdentifier);
        }
    }

    public static class KeyFactoryKMAC128
    extends KeyFactoryKMAC {
        public KeyFactoryKMAC128() {
            super(128, NISTObjectIdentifiers.id_KmacWithSHAKE128);
        }
    }

    public static class KeyFactoryKMAC256
    extends KeyFactoryKMAC {
        public KeyFactoryKMAC256() {
            super(256, NISTObjectIdentifiers.id_KmacWithSHAKE256);
        }
    }

    public static class KeyFactorySHA3
    extends BaseSecretKeyFactory {
        public KeyFactorySHA3(int n, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
            super("HmacSHA3-" + n, aSN1ObjectIdentifier);
        }
    }

    public static class KeyGenerator224
    extends KeyGeneratorSHA3 {
        public KeyGenerator224() {
            super(224);
        }
    }

    public static class KeyGenerator256
    extends KeyGeneratorSHA3 {
        public KeyGenerator256() {
            super(256);
        }
    }

    public static class KeyGenerator384
    extends KeyGeneratorSHA3 {
        public KeyGenerator384() {
            super(384);
        }
    }

    public static class KeyGenerator512
    extends KeyGeneratorSHA3 {
        public KeyGenerator512() {
            super(512);
        }
    }

    public static class KeyGeneratorSHA3
    extends BaseKeyGenerator {
        public KeyGeneratorSHA3(int n) {
            super("HMACSHA3-" + n, n, new CipherKeyGenerator());
        }
    }

    public static class Mappings
    extends DigestAlgorithmProvider {
        private static final String PREFIX = SHA3.class.getName();

        @Override
        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("MessageDigest.SHA3-224", PREFIX + "$Digest224");
            configurableProvider.addAlgorithm("MessageDigest.SHA3-256", PREFIX + "$Digest256");
            configurableProvider.addAlgorithm("MessageDigest.SHA3-384", PREFIX + "$Digest384");
            configurableProvider.addAlgorithm("MessageDigest.SHA3-512", PREFIX + "$Digest512");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_224, PREFIX + "$Digest224");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_256, PREFIX + "$Digest256");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_384, PREFIX + "$Digest384");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_sha3_512, PREFIX + "$Digest512");
            configurableProvider.addAlgorithm("MessageDigest.SHAKE256-512", PREFIX + "$DigestShake256_512");
            configurableProvider.addAlgorithm("MessageDigest.SHAKE128-256", PREFIX + "$DigestShake128_256");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_shake256, PREFIX + "$DigestShake256_512");
            configurableProvider.addAlgorithm("MessageDigest", NISTObjectIdentifiers.id_shake128, PREFIX + "$DigestShake128_256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHAKE256", "SHAKE256-512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.SHAKE128", "SHAKE128-256");
            this.addHMACAlgorithm(configurableProvider, "SHA3-224", PREFIX + "$HashMac224", PREFIX + "$KeyGenerator224");
            this.addHMACAlias(configurableProvider, "SHA3-224", NISTObjectIdentifiers.id_hmacWithSHA3_224);
            configurableProvider.addAlgorithm("SecretKeyFactory.HMACSHA3-224", PREFIX + "$KeyFactory224");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_224, "HMACSHA3-224");
            this.addHMACAlgorithm(configurableProvider, "SHA3-256", PREFIX + "$HashMac256", PREFIX + "$KeyGenerator256");
            this.addHMACAlias(configurableProvider, "SHA3-256", NISTObjectIdentifiers.id_hmacWithSHA3_256);
            configurableProvider.addAlgorithm("SecretKeyFactory.HMACSHA3-256", PREFIX + "$KeyFactory256");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_256, "HMACSHA3-256");
            this.addHMACAlgorithm(configurableProvider, "SHA3-384", PREFIX + "$HashMac384", PREFIX + "$KeyGenerator384");
            this.addHMACAlias(configurableProvider, "SHA3-384", NISTObjectIdentifiers.id_hmacWithSHA3_384);
            configurableProvider.addAlgorithm("SecretKeyFactory.HMACSHA3-384", PREFIX + "$KeyFactory384");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_384, "HMACSHA3-384");
            this.addHMACAlgorithm(configurableProvider, "SHA3-512", PREFIX + "$HashMac512", PREFIX + "$KeyGenerator512");
            this.addHMACAlias(configurableProvider, "SHA3-512", NISTObjectIdentifiers.id_hmacWithSHA3_512);
            configurableProvider.addAlgorithm("SecretKeyFactory.HMACSHA3-512", PREFIX + "$KeyFactory512");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_hmacWithSHA3_512, "HMACSHA3-512");
            this.addKMACAlgorithm(configurableProvider, "128", PREFIX + "$KMac128", PREFIX + "$KeyGenerator256");
            configurableProvider.addAlgorithm("SecretKeyFactory.KMAC128", PREFIX + "$KeyFactoryKMAC128");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_Kmac128, "KMAC128");
            this.addKMACAlgorithm(configurableProvider, "256", PREFIX + "$KMac256", PREFIX + "$KeyGenerator512");
            configurableProvider.addAlgorithm("SecretKeyFactory.KMAC256", PREFIX + "$KeyFactoryKMAC256");
            configurableProvider.addAlgorithm("Alg.Alias.SecretKeyFactory." + NISTObjectIdentifiers.id_Kmac256, "KMAC256");
            configurableProvider.addAlgorithm("MessageDigest.TUPLEHASH256-512", PREFIX + "$DigestTupleHash256_512");
            configurableProvider.addAlgorithm("MessageDigest.TUPLEHASH128-256", PREFIX + "$DigestTupleHash128_256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.TUPLEHASH256", "TUPLEHASH256-512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.TUPLEHASH128", "TUPLEHASH128-256");
            configurableProvider.addAlgorithm("MessageDigest.PARALLELHASH256-512", PREFIX + "$DigestParallelHash256_512");
            configurableProvider.addAlgorithm("MessageDigest.PARALLELHASH128-256", PREFIX + "$DigestParallelHash128_256");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.PARALLELHASH256", "PARALLELHASH256-512");
            configurableProvider.addAlgorithm("Alg.Alias.MessageDigest.PARALLELHASH128", "PARALLELHASH128-256");
        }
    }
}

