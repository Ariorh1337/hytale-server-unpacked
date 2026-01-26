/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.digests.HarakaBase;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class Haraka512Digest
extends HarakaBase {
    private final byte[] buffer;
    private int off;
    private final CryptoServicePurpose purpose;

    public Haraka512Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public Haraka512Digest(CryptoServicePurpose cryptoServicePurpose) {
        this.purpose = cryptoServicePurpose;
        this.buffer = new byte[64];
    }

    public Haraka512Digest(Haraka512Digest haraka512Digest) {
        this.purpose = haraka512Digest.purpose;
        this.buffer = Arrays.clone(haraka512Digest.buffer);
        this.off = haraka512Digest.off;
    }

    private void mix512(byte[][] byArray, byte[][] byArray2) {
        System.arraycopy(byArray[0], 12, byArray2[0], 0, 4);
        System.arraycopy(byArray[2], 12, byArray2[0], 4, 4);
        System.arraycopy(byArray[1], 12, byArray2[0], 8, 4);
        System.arraycopy(byArray[3], 12, byArray2[0], 12, 4);
        System.arraycopy(byArray[2], 0, byArray2[1], 0, 4);
        System.arraycopy(byArray[0], 0, byArray2[1], 4, 4);
        System.arraycopy(byArray[3], 0, byArray2[1], 8, 4);
        System.arraycopy(byArray[1], 0, byArray2[1], 12, 4);
        System.arraycopy(byArray[2], 4, byArray2[2], 0, 4);
        System.arraycopy(byArray[0], 4, byArray2[2], 4, 4);
        System.arraycopy(byArray[3], 4, byArray2[2], 8, 4);
        System.arraycopy(byArray[1], 4, byArray2[2], 12, 4);
        System.arraycopy(byArray[0], 8, byArray2[3], 0, 4);
        System.arraycopy(byArray[2], 8, byArray2[3], 4, 4);
        System.arraycopy(byArray[1], 8, byArray2[3], 8, 4);
        System.arraycopy(byArray[3], 8, byArray2[3], 12, 4);
    }

    private int haraka512256(byte[] byArray, byte[] byArray2, int n) {
        byte[][] byArray3 = new byte[4][16];
        byte[][] byArray4 = new byte[4][16];
        System.arraycopy(byArray, 0, byArray3[0], 0, 16);
        System.arraycopy(byArray, 16, byArray3[1], 0, 16);
        System.arraycopy(byArray, 32, byArray3[2], 0, 16);
        System.arraycopy(byArray, 48, byArray3[3], 0, 16);
        byArray3[0] = Haraka512Digest.aesEnc(byArray3[0], RC[0]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray3[1], RC[1]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray3[2], RC[2]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray3[3], RC[3]);
        byArray3[0] = Haraka512Digest.aesEnc(byArray3[0], RC[4]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray3[1], RC[5]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray3[2], RC[6]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray3[3], RC[7]);
        this.mix512(byArray3, byArray4);
        byArray3[0] = Haraka512Digest.aesEnc(byArray4[0], RC[8]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray4[1], RC[9]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray4[2], RC[10]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray4[3], RC[11]);
        byArray3[0] = Haraka512Digest.aesEnc(byArray3[0], RC[12]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray3[1], RC[13]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray3[2], RC[14]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray3[3], RC[15]);
        this.mix512(byArray3, byArray4);
        byArray3[0] = Haraka512Digest.aesEnc(byArray4[0], RC[16]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray4[1], RC[17]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray4[2], RC[18]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray4[3], RC[19]);
        byArray3[0] = Haraka512Digest.aesEnc(byArray3[0], RC[20]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray3[1], RC[21]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray3[2], RC[22]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray3[3], RC[23]);
        this.mix512(byArray3, byArray4);
        byArray3[0] = Haraka512Digest.aesEnc(byArray4[0], RC[24]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray4[1], RC[25]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray4[2], RC[26]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray4[3], RC[27]);
        byArray3[0] = Haraka512Digest.aesEnc(byArray3[0], RC[28]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray3[1], RC[29]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray3[2], RC[30]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray3[3], RC[31]);
        this.mix512(byArray3, byArray4);
        byArray3[0] = Haraka512Digest.aesEnc(byArray4[0], RC[32]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray4[1], RC[33]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray4[2], RC[34]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray4[3], RC[35]);
        byArray3[0] = Haraka512Digest.aesEnc(byArray3[0], RC[36]);
        byArray3[1] = Haraka512Digest.aesEnc(byArray3[1], RC[37]);
        byArray3[2] = Haraka512Digest.aesEnc(byArray3[2], RC[38]);
        byArray3[3] = Haraka512Digest.aesEnc(byArray3[3], RC[39]);
        this.mix512(byArray3, byArray4);
        Bytes.xor(16, byArray4[0], 0, byArray, 0, byArray3[0], 0);
        Bytes.xor(16, byArray4[1], 0, byArray, 16, byArray3[1], 0);
        Bytes.xor(16, byArray4[2], 0, byArray, 32, byArray3[2], 0);
        Bytes.xor(16, byArray4[3], 0, byArray, 48, byArray3[3], 0);
        System.arraycopy(byArray3[0], 8, byArray2, n, 8);
        System.arraycopy(byArray3[1], 8, byArray2, n + 8, 8);
        System.arraycopy(byArray3[2], 0, byArray2, n + 16, 8);
        System.arraycopy(byArray3[3], 0, byArray2, n + 24, 8);
        return 32;
    }

    @Override
    public String getAlgorithmName() {
        return "Haraka-512";
    }

    @Override
    public void update(byte by) {
        if (this.off > 63) {
            throw new IllegalArgumentException("total input cannot be more than 64 bytes");
        }
        this.buffer[this.off++] = by;
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        if (this.off > 64 - n2) {
            throw new IllegalArgumentException("total input cannot be more than 64 bytes");
        }
        System.arraycopy(byArray, n, this.buffer, this.off, n2);
        this.off += n2;
    }

    @Override
    public int doFinal(byte[] byArray, int n) {
        if (this.off != 64) {
            throw new IllegalStateException("input must be exactly 64 bytes");
        }
        if (byArray.length - n < 32) {
            throw new IllegalArgumentException("output too short to receive digest");
        }
        int n2 = this.haraka512256(this.buffer, byArray, n);
        this.reset();
        return n2;
    }

    @Override
    public void reset() {
        this.off = 0;
        Arrays.clear(this.buffer);
    }
}

