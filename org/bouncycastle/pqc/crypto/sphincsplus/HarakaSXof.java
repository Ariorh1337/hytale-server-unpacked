/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.pqc.crypto.sphincsplus.HarakaSBase;

class HarakaSXof
extends HarakaSBase {
    public String getAlgorithmName() {
        return "Haraka-S";
    }

    public HarakaSXof(byte[] byArray) {
        byte[] byArray2 = new byte[640];
        this.update(byArray, 0, byArray.length);
        this.doFinal(byArray2, 0, byArray2.length);
        this.haraka512_rc = new long[10][8];
        this.haraka256_rc = new int[10][8];
        for (int i = 0; i < 10; ++i) {
            this.interleaveConstant32(this.haraka256_rc[i], byArray2, i << 5);
            this.interleaveConstant(this.haraka512_rc[i], byArray2, i << 6);
        }
    }

    public void update(byte[] byArray, int n, int n2) {
        int n3 = n;
        int n4 = n2 + this.off >> 5;
        for (int i = 0; i < n4; ++i) {
            while (this.off < 32) {
                int n5 = this.off++;
                this.buffer[n5] = (byte)(this.buffer[n5] ^ byArray[n3++]);
            }
            this.haraka512Perm(this.buffer);
            this.off = 0;
        }
        while (n3 < n + n2) {
            int n6 = this.off++;
            this.buffer[n6] = (byte)(this.buffer[n6] ^ byArray[n3++]);
        }
    }

    public void update(byte by) {
        int n = this.off++;
        this.buffer[n] = (byte)(this.buffer[n] ^ by);
        if (this.off == 32) {
            this.haraka512Perm(this.buffer);
            this.off = 0;
        }
    }

    public int doFinal(byte[] byArray, int n, int n2) {
        int n3 = n2;
        int n4 = this.off;
        this.buffer[n4] = (byte)(this.buffer[n4] ^ 0x1F);
        this.buffer[31] = (byte)(this.buffer[31] ^ 0x80);
        while (n2 >= 32) {
            this.haraka512Perm(this.buffer);
            System.arraycopy(this.buffer, 0, byArray, n, 32);
            n += 32;
            n2 -= 32;
        }
        if (n2 > 0) {
            this.haraka512Perm(this.buffer);
            System.arraycopy(this.buffer, 0, byArray, n, n2);
        }
        this.reset();
        return n3;
    }
}

