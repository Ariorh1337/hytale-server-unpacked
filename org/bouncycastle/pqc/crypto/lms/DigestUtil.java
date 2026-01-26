/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

class DigestUtil {
    DigestUtil() {
    }

    static Digest getDigest(LMOtsParameters lMOtsParameters) {
        return DigestUtil.createDigest(lMOtsParameters.getDigestOID(), lMOtsParameters.getN());
    }

    static Digest getDigest(LMSigParameters lMSigParameters) {
        return DigestUtil.createDigest(lMSigParameters.getDigestOID(), lMSigParameters.getM());
    }

    private static Digest createDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier, int n) {
        Digest digest = DigestUtil.createDigest(aSN1ObjectIdentifier);
        if (NISTObjectIdentifiers.id_shake256_len.equals(aSN1ObjectIdentifier) || digest.getDigestSize() != n) {
            return new WrapperDigest(digest, n);
        }
        return digest;
    }

    private static Digest createDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
            return new SHA256Digest();
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256_len)) {
            return new SHAKEDigest(256);
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + aSN1ObjectIdentifier);
    }

    static class WrapperDigest
    implements Digest {
        private final Digest digest;
        private final int length;

        WrapperDigest(Digest digest, int n) {
            this.digest = digest;
            this.length = n;
        }

        @Override
        public String getAlgorithmName() {
            return this.digest.getAlgorithmName() + "/" + this.length * 8;
        }

        @Override
        public int getDigestSize() {
            return this.length;
        }

        @Override
        public void update(byte by) {
            this.digest.update(by);
        }

        @Override
        public void update(byte[] byArray, int n, int n2) {
            this.digest.update(byArray, n, n2);
        }

        @Override
        public int doFinal(byte[] byArray, int n) {
            byte[] byArray2 = new byte[this.digest.getDigestSize()];
            this.digest.doFinal(byArray2, 0);
            System.arraycopy(byArray2, 0, byArray, n, this.length);
            return this.length;
        }

        @Override
        public void reset() {
            this.digest.reset();
        }
    }
}

