/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.pqc.crypto.slhdsa.ADRS;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class WotsPlus {
    private final SLHDSAEngine engine;
    private final int w;

    WotsPlus(SLHDSAEngine sLHDSAEngine) {
        this.engine = sLHDSAEngine;
        this.w = this.engine.WOTS_W;
    }

    byte[] pkGen(byte[] byArray, byte[] byArray2, ADRS aDRS) {
        ADRS aDRS2 = new ADRS(aDRS);
        byte[][] byArrayArray = new byte[this.engine.WOTS_LEN][];
        for (int i = 0; i < this.engine.WOTS_LEN; ++i) {
            ADRS aDRS3 = new ADRS(aDRS);
            aDRS3.setTypeAndClear(5);
            aDRS3.setKeyPairAddress(aDRS.getKeyPairAddress());
            aDRS3.setChainAddress(i);
            aDRS3.setHashAddress(0);
            byte[] byArray3 = this.engine.PRF(byArray2, byArray, aDRS3);
            aDRS3.setTypeAndClear(0);
            aDRS3.setKeyPairAddress(aDRS.getKeyPairAddress());
            aDRS3.setChainAddress(i);
            aDRS3.setHashAddress(0);
            byArrayArray[i] = this.chain(byArray3, 0, this.w - 1, byArray2, aDRS3);
        }
        aDRS2.setTypeAndClear(1);
        aDRS2.setKeyPairAddress(aDRS.getKeyPairAddress());
        return this.engine.T_l(byArray2, aDRS2, Arrays.concatenate(byArrayArray));
    }

    byte[] chain(byte[] byArray, int n, int n2, byte[] byArray2, ADRS aDRS) {
        if (n2 == 0) {
            return Arrays.clone(byArray);
        }
        if (n + n2 > this.w - 1) {
            return null;
        }
        byte[] byArray3 = byArray;
        for (int i = 0; i < n2; ++i) {
            aDRS.setHashAddress(n + i);
            byArray3 = this.engine.F(byArray2, aDRS, byArray3);
        }
        return byArray3;
    }

    public byte[] sign(byte[] byArray, byte[] byArray2, byte[] byArray3, ADRS aDRS) {
        int n;
        ADRS aDRS2 = new ADRS(aDRS);
        int[] nArray = new int[this.engine.WOTS_LEN];
        this.base_w(byArray, 0, this.w, nArray, 0, this.engine.WOTS_LEN1);
        int n2 = 0;
        for (n = 0; n < this.engine.WOTS_LEN1; ++n) {
            n2 += this.w - 1 - nArray[n];
        }
        if (this.engine.WOTS_LOGW % 8 != 0) {
            n2 <<= 8 - this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW % 8;
        }
        n = (this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW + 7) / 8;
        byte[] byArray4 = Pack.intToBigEndian(n2);
        this.base_w(byArray4, 4 - n, this.w, nArray, this.engine.WOTS_LEN1, this.engine.WOTS_LEN2);
        byte[][] byArrayArray = new byte[this.engine.WOTS_LEN][];
        for (int i = 0; i < this.engine.WOTS_LEN; ++i) {
            aDRS2.setTypeAndClear(5);
            aDRS2.setKeyPairAddress(aDRS.getKeyPairAddress());
            aDRS2.setChainAddress(i);
            aDRS2.setHashAddress(0);
            byte[] byArray5 = this.engine.PRF(byArray3, byArray2, aDRS2);
            aDRS2.setTypeAndClear(0);
            aDRS2.setKeyPairAddress(aDRS.getKeyPairAddress());
            aDRS2.setChainAddress(i);
            aDRS2.setHashAddress(0);
            byArrayArray[i] = this.chain(byArray5, 0, nArray[i], byArray3, aDRS2);
        }
        return Arrays.concatenate(byArrayArray);
    }

    void base_w(byte[] byArray, int n, int n2, int[] nArray, int n3, int n4) {
        byte by = 0;
        int n5 = 0;
        for (int i = 0; i < n4; ++i) {
            if (n5 == 0) {
                by = byArray[n++];
                n5 += 8;
            }
            nArray[n3++] = by >>> (n5 -= this.engine.WOTS_LOGW) & n2 - 1;
        }
    }

    public byte[] pkFromSig(byte[] byArray, byte[] byArray2, byte[] byArray3, ADRS aDRS) {
        int n;
        ADRS aDRS2 = new ADRS(aDRS);
        int[] nArray = new int[this.engine.WOTS_LEN];
        this.base_w(byArray2, 0, this.w, nArray, 0, this.engine.WOTS_LEN1);
        int n2 = 0;
        for (n = 0; n < this.engine.WOTS_LEN1; ++n) {
            n2 += this.w - 1 - nArray[n];
        }
        n = (this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW + 7) / 8;
        byte[] byArray4 = Pack.intToBigEndian(n2 <<= 8 - this.engine.WOTS_LEN2 * this.engine.WOTS_LOGW % 8);
        this.base_w(byArray4, 4 - n, this.w, nArray, this.engine.WOTS_LEN1, this.engine.WOTS_LEN2);
        byte[] byArray5 = new byte[this.engine.N];
        byte[][] byArrayArray = new byte[this.engine.WOTS_LEN][];
        for (int i = 0; i < this.engine.WOTS_LEN; ++i) {
            aDRS.setChainAddress(i);
            System.arraycopy(byArray, i * this.engine.N, byArray5, 0, this.engine.N);
            byArrayArray[i] = this.chain(byArray5, nArray[i], this.w - 1 - nArray[i], byArray3, aDRS);
        }
        aDRS2.setTypeAndClear(1);
        aDRS2.setKeyPairAddress(aDRS.getKeyPairAddress());
        return this.engine.T_l(byArray3, aDRS2, Arrays.concatenate(byArrayArray));
    }
}

