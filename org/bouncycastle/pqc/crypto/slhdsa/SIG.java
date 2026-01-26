/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.pqc.crypto.slhdsa.SIG_FORS;
import org.bouncycastle.pqc.crypto.slhdsa.SIG_XMSS;

class SIG {
    private final byte[] r;
    private final SIG_FORS[] sig_fors;
    private final SIG_XMSS[] sig_ht;

    public SIG(int n, int n2, int n3, int n4, int n5, int n6, byte[] byArray) {
        int n7;
        byte[][] byArrayArray;
        byte[] byArray2;
        int n8;
        this.r = new byte[n];
        System.arraycopy(byArray, 0, this.r, 0, n);
        this.sig_fors = new SIG_FORS[n2];
        int n9 = n;
        for (n8 = 0; n8 != n2; ++n8) {
            byArray2 = new byte[n];
            System.arraycopy(byArray, n9, byArray2, 0, n);
            n9 += n;
            byArrayArray = new byte[n3][];
            for (n7 = 0; n7 != n3; ++n7) {
                byArrayArray[n7] = new byte[n];
                System.arraycopy(byArray, n9, byArrayArray[n7], 0, n);
                n9 += n;
            }
            this.sig_fors[n8] = new SIG_FORS(byArray2, byArrayArray);
        }
        this.sig_ht = new SIG_XMSS[n4];
        for (n8 = 0; n8 != n4; ++n8) {
            byArray2 = new byte[n6 * n];
            System.arraycopy(byArray, n9, byArray2, 0, byArray2.length);
            n9 += byArray2.length;
            byArrayArray = new byte[n5][];
            for (n7 = 0; n7 != n5; ++n7) {
                byArrayArray[n7] = new byte[n];
                System.arraycopy(byArray, n9, byArrayArray[n7], 0, n);
                n9 += n;
            }
            this.sig_ht[n8] = new SIG_XMSS(byArray2, byArrayArray);
        }
        if (n9 != byArray.length) {
            throw new IllegalArgumentException("signature wrong length");
        }
    }

    public byte[] getR() {
        return this.r;
    }

    public SIG_FORS[] getSIG_FORS() {
        return this.sig_fors;
    }

    public SIG_XMSS[] getSIG_HT() {
        return this.sig_ht;
    }
}

