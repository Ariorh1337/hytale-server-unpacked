/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.xmss;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;

class DigestUtil {
    DigestUtil() {
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

    static ASN1ObjectIdentifier getDigestOID(String string) {
        if (string.equals("SHA-256")) {
            return NISTObjectIdentifiers.id_sha256;
        }
        if (string.equals("SHA-512")) {
            return NISTObjectIdentifiers.id_sha512;
        }
        if (string.equals("SHAKE128")) {
            return NISTObjectIdentifiers.id_shake128;
        }
        if (string.equals("SHAKE256")) {
            return NISTObjectIdentifiers.id_shake256;
        }
        throw new IllegalArgumentException("unrecognized digest: " + string);
    }

    public static byte[] getDigestResult(Digest digest) {
        byte[] byArray = new byte[digest.getDigestSize()];
        digest.doFinal(byArray, 0);
        return byArray;
    }

    public static String getXMSSDigestName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha256)) {
            return "SHA256";
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_sha512)) {
            return "SHA512";
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake128)) {
            return "SHAKE128";
        }
        if (aSN1ObjectIdentifier.equals(NISTObjectIdentifiers.id_shake256)) {
            return "SHAKE256";
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + aSN1ObjectIdentifier);
    }

    static class DoubleDigest
    implements Digest {
        private SHAKEDigest digest;

        DoubleDigest(SHAKEDigest sHAKEDigest) {
            this.digest = sHAKEDigest;
        }

        @Override
        public String getAlgorithmName() {
            return this.digest.getAlgorithmName() + "/" + this.digest.getDigestSize() * 2 * 8;
        }

        @Override
        public int getDigestSize() {
            return this.digest.getDigestSize() * 2;
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
            return this.digest.doFinal(byArray, n, this.getDigestSize());
        }

        @Override
        public void reset() {
            this.digest.reset();
        }
    }
}

