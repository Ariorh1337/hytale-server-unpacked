/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.HarakaBase;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class Haraka256Digest
extends HarakaBase {
    private final byte[] buffer;
    private int off;
    private final CryptoServicePurpose purpose;

    private void mix256(byte[][] byArray, byte[][] byArray2) {
        System.arraycopy(byArray[0], 0, byArray2[0], 0, 4);
        System.arraycopy(byArray[1], 0, byArray2[0], 4, 4);
        System.arraycopy(byArray[0], 4, byArray2[0], 8, 4);
        System.arraycopy(byArray[1], 4, byArray2[0], 12, 4);
        System.arraycopy(byArray[0], 8, byArray2[1], 0, 4);
        System.arraycopy(byArray[1], 8, byArray2[1], 4, 4);
        System.arraycopy(byArray[0], 12, byArray2[1], 8, 4);
        System.arraycopy(byArray[1], 12, byArray2[1], 12, 4);
    }

    private int haraka256256(byte[] byArray, byte[] byArray2, int n) {
        byte[][] byArray3 = new byte[2][16];
        byte[][] byArray4 = new byte[2][16];
        System.arraycopy(byArray, 0, byArray3[0], 0, 16);
        System.arraycopy(byArray, 16, byArray3[1], 0, 16);
        byArray3[0] = Haraka256Digest.aesEnc(byArray3[0], RC[0]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray3[1], RC[1]);
        byArray3[0] = Haraka256Digest.aesEnc(byArray3[0], RC[2]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray3[1], RC[3]);
        this.mix256(byArray3, byArray4);
        byArray3[0] = Haraka256Digest.aesEnc(byArray4[0], RC[4]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray4[1], RC[5]);
        byArray3[0] = Haraka256Digest.aesEnc(byArray3[0], RC[6]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray3[1], RC[7]);
        this.mix256(byArray3, byArray4);
        byArray3[0] = Haraka256Digest.aesEnc(byArray4[0], RC[8]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray4[1], RC[9]);
        byArray3[0] = Haraka256Digest.aesEnc(byArray3[0], RC[10]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray3[1], RC[11]);
        this.mix256(byArray3, byArray4);
        byArray3[0] = Haraka256Digest.aesEnc(byArray4[0], RC[12]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray4[1], RC[13]);
        byArray3[0] = Haraka256Digest.aesEnc(byArray3[0], RC[14]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray3[1], RC[15]);
        this.mix256(byArray3, byArray4);
        byArray3[0] = Haraka256Digest.aesEnc(byArray4[0], RC[16]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray4[1], RC[17]);
        byArray3[0] = Haraka256Digest.aesEnc(byArray3[0], RC[18]);
        byArray3[1] = Haraka256Digest.aesEnc(byArray3[1], RC[19]);
        this.mix256(byArray3, byArray4);
        Bytes.xor(16, byArray4[0], 0, byArray, 0, byArray2, n);
        Bytes.xor(16, byArray4[1], 0, byArray, 16, byArray2, n + 16);
        return 32;
    }

    public Haraka256Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public Haraka256Digest(CryptoServicePurpose cryptoServicePurpose) {
        this.purpose = cryptoServicePurpose;
        this.buffer = new byte[32];
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 4, cryptoServicePurpose));
    }

    public Haraka256Digest(Haraka256Digest haraka256Digest) {
        this.purpose = haraka256Digest.purpose;
        this.buffer = Arrays.clone(haraka256Digest.buffer);
        this.off = haraka256Digest.off;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 4, this.purpose));
    }

    @Override
    public String getAlgorithmName() {
        return "Haraka-256";
    }

    @Override
    public void update(byte by) {
        if (this.off > 31) {
            throw new IllegalArgumentException("total input cannot be more than 32 bytes");
        }
        this.buffer[this.off++] = by;
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        if (this.off > 32 - n2) {
            throw new IllegalArgumentException("total input cannot be more than 32 bytes");
        }
        System.arraycopy(byArray, n, this.buffer, this.off, n2);
        this.off += n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        if (this.off != 32) {
            throw new IllegalStateException("input must be exactly 32 bytes");
        }
        if (byArray.length - n < 32) {
            throw new IllegalArgumentException("output too short to receive digest");
        }
        int n2 = this.haraka256256(this.buffer, byArray, n);
        this.reset();
        return n2;
    }

    @Override
    public void reset() {
        this.off = 0;
        Arrays.clear(this.buffer);
    }
}

