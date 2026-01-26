/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.EncodableDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

class HKDF {
    private static final byte[] VERSION_LABEL = HKDF.getBytes("HPKE-v1");
    private final HKDFBytesGenerator kdf;
    private final int hashLength;

    HKDF(short s) {
        EncodableDigest encodableDigest;
        switch (s) {
            case 1: {
                encodableDigest = new SHA256Digest();
                break;
            }
            case 2: {
                encodableDigest = new SHA384Digest();
                break;
            }
            case 3: {
                encodableDigest = new SHA512Digest();
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid kdf id");
            }
        }
        this.kdf = new HKDFBytesGenerator((Digest)((Object)encodableDigest));
        this.hashLength = encodableDigest.getDigestSize();
    }

    int getHashSize() {
        return this.hashLength;
    }

    protected byte[] LabeledExtract(byte[] byArray, byte[] byArray2, String string, byte[] byArray3) {
        if (byArray == null) {
            byArray = new byte[this.hashLength];
        }
        byte[] byArray4 = Arrays.concatenate(VERSION_LABEL, byArray2, HKDF.getBytes(string), byArray3);
        return this.kdf.extractPRK(byArray, byArray4);
    }

    protected byte[] LabeledExpand(byte[] byArray, byte[] byArray2, String string, byte[] byArray3, int n) {
        if (n > 65536) {
            throw new IllegalArgumentException("Expand length cannot be larger than 2^16");
        }
        byte[] byArray4 = Arrays.concatenate(Pack.shortToBigEndian((short)n), VERSION_LABEL, byArray2, HKDF.getBytes(string));
        this.kdf.init(HKDFParameters.skipExtractParameters(byArray, Arrays.concatenate(byArray4, byArray3)));
        byte[] byArray5 = new byte[n];
        this.kdf.generateBytes(byArray5, 0, byArray5.length);
        return byArray5;
    }

    protected byte[] Extract(byte[] byArray, byte[] byArray2) {
        if (byArray == null) {
            byArray = new byte[this.hashLength];
        }
        return this.kdf.extractPRK(byArray, byArray2);
    }

    protected byte[] Expand(byte[] byArray, byte[] byArray2, int n) {
        if (n > 65536) {
            throw new IllegalArgumentException("Expand length cannot be larger than 2^16");
        }
        this.kdf.init(HKDFParameters.skipExtractParameters(byArray, byArray2));
        byte[] byArray3 = new byte[n];
        this.kdf.generateBytes(byArray3, 0, byArray3.length);
        return byArray3;
    }

    private static byte[] getBytes(String string) {
        return Strings.toByteArray(string);
    }
}

