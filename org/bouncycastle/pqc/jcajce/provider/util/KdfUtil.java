/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KEMKDFSpec;
import org.bouncycastle.util.Arrays;

public class KdfUtil {
    public static byte[] makeKeyBytes(KEMKDFSpec kEMKDFSpec, byte[] byArray) {
        byte[] byArray2;
        try {
            if (kEMKDFSpec == null) {
                byArray2 = new byte[byArray.length];
                System.arraycopy(byArray, 0, byArray2, 0, byArray2.length);
            } else {
                byArray2 = KdfUtil.makeKeyBytes(kEMKDFSpec.getKdfAlgorithm(), byArray, kEMKDFSpec.getOtherInfo(), kEMKDFSpec.getKeySize());
            }
        }
        finally {
            Arrays.clear(byArray);
        }
        return byArray2;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static byte[] makeKeyBytes(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, byte[] byArray2, int n) {
        byte[] byArray3 = new byte[(n + 7) / 8];
        if (algorithmIdentifier == null) {
            System.arraycopy(byArray, 0, byArray3, 0, byArray3.length);
            return byArray3;
        } else if (X9ObjectIdentifiers.id_kdf_kdf2.equals(algorithmIdentifier.getAlgorithm())) {
            AlgorithmIdentifier algorithmIdentifier2 = AlgorithmIdentifier.getInstance(algorithmIdentifier.getParameters());
            KDF2BytesGenerator kDF2BytesGenerator = new KDF2BytesGenerator(KdfUtil.getDigest(algorithmIdentifier2.getAlgorithm()));
            kDF2BytesGenerator.init(new KDFParameters(byArray, byArray2));
            kDF2BytesGenerator.generateBytes(byArray3, 0, byArray3.length);
            return byArray3;
        } else if (X9ObjectIdentifiers.id_kdf_kdf3.equals(algorithmIdentifier.getAlgorithm())) {
            AlgorithmIdentifier algorithmIdentifier3 = AlgorithmIdentifier.getInstance(algorithmIdentifier.getParameters());
            ConcatenationKDFGenerator concatenationKDFGenerator = new ConcatenationKDFGenerator(KdfUtil.getDigest(algorithmIdentifier3.getAlgorithm()));
            concatenationKDFGenerator.init(new KDFParameters(byArray, byArray2));
            concatenationKDFGenerator.generateBytes(byArray3, 0, byArray3.length);
            return byArray3;
        } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha256.equals(algorithmIdentifier.getAlgorithm())) {
            if (algorithmIdentifier.getParameters() != null) throw new IllegalStateException("HDKF parameter support not added");
            HKDFBytesGenerator hKDFBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
            hKDFBytesGenerator.init(new HKDFParameters(byArray, null, byArray2));
            hKDFBytesGenerator.generateBytes(byArray3, 0, byArray3.length);
            return byArray3;
        } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha384.equals(algorithmIdentifier.getAlgorithm())) {
            if (algorithmIdentifier.getParameters() != null) throw new IllegalStateException("HDKF parameter support not added");
            HKDFBytesGenerator hKDFBytesGenerator = new HKDFBytesGenerator(new SHA384Digest());
            hKDFBytesGenerator.init(new HKDFParameters(byArray, null, byArray2));
            hKDFBytesGenerator.generateBytes(byArray3, 0, byArray3.length);
            return byArray3;
        } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha512.equals(algorithmIdentifier.getAlgorithm())) {
            if (algorithmIdentifier.getParameters() != null) throw new IllegalStateException("HDKF parameter support not added");
            HKDFBytesGenerator hKDFBytesGenerator = new HKDFBytesGenerator(new SHA512Digest());
            hKDFBytesGenerator.init(new HKDFParameters(byArray, null, byArray2));
            hKDFBytesGenerator.generateBytes(byArray3, 0, byArray3.length);
            return byArray3;
        } else if (NISTObjectIdentifiers.id_Kmac128.equals(algorithmIdentifier.getAlgorithm())) {
            byte[] byArray4 = new byte[]{};
            if (algorithmIdentifier.getParameters() != null) {
                byArray4 = ASN1OctetString.getInstance(algorithmIdentifier.getParameters()).getOctets();
            }
            KMAC kMAC = new KMAC(128, byArray4);
            kMAC.init(new KeyParameter(byArray, 0, byArray.length));
            kMAC.update(byArray2, 0, byArray2.length);
            kMAC.doFinal(byArray3, 0, byArray3.length);
            return byArray3;
        } else if (NISTObjectIdentifiers.id_Kmac256.equals(algorithmIdentifier.getAlgorithm())) {
            byte[] byArray5 = new byte[]{};
            if (algorithmIdentifier.getParameters() != null) {
                byArray5 = ASN1OctetString.getInstance(algorithmIdentifier.getParameters()).getOctets();
            }
            KMAC kMAC = new KMAC(256, byArray5);
            kMAC.init(new KeyParameter(byArray, 0, byArray.length));
            kMAC.update(byArray2, 0, byArray2.length);
            kMAC.doFinal(byArray3, 0, byArray3.length);
            return byArray3;
        } else {
            if (!NISTObjectIdentifiers.id_shake256.equals(algorithmIdentifier.getAlgorithm())) throw new IllegalArgumentException("Unrecognized KDF: " + algorithmIdentifier.getAlgorithm());
            SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
            sHAKEDigest.update(byArray, 0, byArray.length);
            sHAKEDigest.update(byArray2, 0, byArray2.length);
            sHAKEDigest.doFinal(byArray3, 0, byArray3.length);
        }
        return byArray3;
    }

    static Digest getDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
            return new SHA256Digest();
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha512)) {
            return new SHA512Digest();
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake128)) {
            return new SHAKEDigest(128);
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256)) {
            return new SHAKEDigest(256);
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + aSN1ObjectIdentifier);
    }
}

