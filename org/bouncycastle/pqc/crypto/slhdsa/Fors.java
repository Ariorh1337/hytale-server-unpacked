/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import java.math.BigInteger;
import java.util.LinkedList;
import org.bouncycastle.pqc.crypto.slhdsa.ADRS;
import org.bouncycastle.pqc.crypto.slhdsa.NodeEntry;
import org.bouncycastle.pqc.crypto.slhdsa.SIG_FORS;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAEngine;
import org.bouncycastle.util.Arrays;

class Fors {
    SLHDSAEngine engine;

    public Fors(SLHDSAEngine sLHDSAEngine) {
        this.engine = sLHDSAEngine;
    }

    byte[] treehash(byte[] byArray, int n, int n2, byte[] byArray2, ADRS aDRS) {
        if (n >>> n2 << n2 != n) {
            return null;
        }
        LinkedList<NodeEntry> linkedList = new LinkedList<NodeEntry>();
        ADRS aDRS2 = new ADRS(aDRS);
        for (int i = 0; i < 1 << n2; ++i) {
            aDRS2.setTypeAndClear(6);
            aDRS2.setKeyPairAddress(aDRS.getKeyPairAddress());
            aDRS2.setTreeHeight(0);
            aDRS2.setTreeIndex(n + i);
            byte[] byArray3 = this.engine.PRF(byArray2, byArray, aDRS2);
            aDRS2.changeType(3);
            byte[] byArray4 = this.engine.F(byArray2, aDRS2, byArray3);
            aDRS2.setTreeHeight(1);
            int n3 = 1;
            int n4 = n + i;
            while (!linkedList.isEmpty() && ((NodeEntry)linkedList.get((int)0)).nodeHeight == n3) {
                n4 = (n4 - 1) / 2;
                aDRS2.setTreeIndex(n4);
                NodeEntry nodeEntry = (NodeEntry)linkedList.remove(0);
                byArray4 = this.engine.H(byArray2, aDRS2, nodeEntry.nodeValue, byArray4);
                aDRS2.setTreeHeight(++n3);
            }
            linkedList.add(0, new NodeEntry(byArray4, n3));
        }
        return ((NodeEntry)linkedList.get((int)0)).nodeValue;
    }

    public SIG_FORS[] sign(byte[] byArray, byte[] byArray2, byte[] byArray3, ADRS aDRS) {
        ADRS aDRS2 = new ADRS(aDRS);
        int[] nArray = Fors.base2B(byArray, this.engine.A, this.engine.K);
        SIG_FORS[] sIG_FORSArray = new SIG_FORS[this.engine.K];
        for (int i = 0; i < this.engine.K; ++i) {
            int n = nArray[i];
            aDRS2.setTypeAndClear(6);
            aDRS2.setKeyPairAddress(aDRS.getKeyPairAddress());
            aDRS2.setTreeHeight(0);
            aDRS2.setTreeIndex((i << this.engine.A) + n);
            byte[] byArray4 = this.engine.PRF(byArray3, byArray2, aDRS2);
            aDRS2.changeType(3);
            byte[][] byArrayArray = new byte[this.engine.A][];
            for (int j = 0; j < this.engine.A; ++j) {
                int n2 = n >>> j ^ 1;
                byArrayArray[j] = this.treehash(byArray2, (i << this.engine.A) + (n2 << j), j, byArray3, aDRS2);
            }
            sIG_FORSArray[i] = new SIG_FORS(byArray4, byArrayArray);
        }
        return sIG_FORSArray;
    }

    public byte[] pkFromSig(SIG_FORS[] sIG_FORSArray, byte[] byArray, byte[] byArray2, ADRS aDRS) {
        byte[][] byArrayArray = new byte[2][];
        byte[][] byArrayArray2 = new byte[this.engine.K][];
        int[] nArray = Fors.base2B(byArray, this.engine.A, this.engine.K);
        for (int i = 0; i < this.engine.K; ++i) {
            int n = nArray[i];
            byte[] byArray3 = sIG_FORSArray[i].getSK();
            aDRS.setTreeHeight(0);
            aDRS.setTreeIndex((i << this.engine.A) + n);
            byArrayArray[0] = this.engine.F(byArray2, aDRS, byArray3);
            byte[][] byArray4 = sIG_FORSArray[i].getAuthPath();
            aDRS.setTreeIndex((i << this.engine.A) + n);
            for (int j = 0; j < this.engine.A; ++j) {
                aDRS.setTreeHeight(j + 1);
                if ((n & 1 << j) == 0) {
                    aDRS.setTreeIndex(aDRS.getTreeIndex() / 2);
                    byArrayArray[1] = this.engine.H(byArray2, aDRS, byArrayArray[0], byArray4[j]);
                } else {
                    aDRS.setTreeIndex((aDRS.getTreeIndex() - 1) / 2);
                    byArrayArray[1] = this.engine.H(byArray2, aDRS, byArray4[j], byArrayArray[0]);
                }
                byArrayArray[0] = byArrayArray[1];
            }
            byArrayArray2[i] = byArrayArray[0];
        }
        ADRS aDRS2 = new ADRS(aDRS);
        aDRS2.setTypeAndClear(4);
        aDRS2.setKeyPairAddress(aDRS.getKeyPairAddress());
        return this.engine.T_l(byArray2, aDRS2, Arrays.concatenate(byArrayArray2));
    }

    static int[] base2B(byte[] byArray, int n, int n2) {
        int[] nArray = new int[n2];
        int n3 = 0;
        int n4 = 0;
        BigInteger bigInteger = BigInteger.ZERO;
        for (int i = 0; i < n2; ++i) {
            while (n4 < n) {
                bigInteger = bigInteger.shiftLeft(8).add(BigInteger.valueOf(byArray[n3] & 0xFF));
                ++n3;
                n4 += 8;
            }
            nArray[i] = bigInteger.shiftRight(n4 -= n).mod(BigInteger.valueOf(2L).pow(n)).intValue();
        }
        return nArray;
    }
}

