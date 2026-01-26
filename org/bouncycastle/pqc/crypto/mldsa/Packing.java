/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.PolyVecK;
import org.bouncycastle.pqc.crypto.mldsa.PolyVecL;
import org.bouncycastle.util.Arrays;

class Packing {
    Packing() {
    }

    static byte[] packPublicKey(PolyVecK polyVecK, MLDSAEngine mLDSAEngine) {
        byte[] byArray = new byte[mLDSAEngine.getCryptoPublicKeyBytes() - 32];
        for (int i = 0; i < mLDSAEngine.getDilithiumK(); ++i) {
            System.arraycopy(polyVecK.getVectorIndex(i).polyt1Pack(), 0, byArray, i * 320, 320);
        }
        return byArray;
    }

    static PolyVecK unpackPublicKey(PolyVecK polyVecK, byte[] byArray, MLDSAEngine mLDSAEngine) {
        for (int i = 0; i < mLDSAEngine.getDilithiumK(); ++i) {
            polyVecK.getVectorIndex(i).polyt1Unpack(Arrays.copyOfRange(byArray, i * 320, (i + 1) * 320));
        }
        return polyVecK;
    }

    static byte[][] packSecretKey(byte[] byArray, byte[] byArray2, byte[] byArray3, PolyVecK polyVecK, PolyVecL polyVecL, PolyVecK polyVecK2, MLDSAEngine mLDSAEngine) {
        int n;
        byte[][] byArrayArray = new byte[6][];
        byArrayArray[0] = byArray;
        byArrayArray[1] = byArray3;
        byArrayArray[2] = byArray2;
        byArrayArray[3] = new byte[mLDSAEngine.getDilithiumL() * mLDSAEngine.getDilithiumPolyEtaPackedBytes()];
        for (n = 0; n < mLDSAEngine.getDilithiumL(); ++n) {
            polyVecL.getVectorIndex(n).polyEtaPack(byArrayArray[3], n * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        byArrayArray[4] = new byte[mLDSAEngine.getDilithiumK() * mLDSAEngine.getDilithiumPolyEtaPackedBytes()];
        for (n = 0; n < mLDSAEngine.getDilithiumK(); ++n) {
            polyVecK2.getVectorIndex(n).polyEtaPack(byArrayArray[4], n * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        byArrayArray[5] = new byte[mLDSAEngine.getDilithiumK() * 416];
        for (n = 0; n < mLDSAEngine.getDilithiumK(); ++n) {
            polyVecK.getVectorIndex(n).polyt0Pack(byArrayArray[5], n * 416);
        }
        return byArrayArray;
    }

    static void unpackSecretKey(PolyVecK polyVecK, PolyVecL polyVecL, PolyVecK polyVecK2, byte[] byArray, byte[] byArray2, byte[] byArray3, MLDSAEngine mLDSAEngine) {
        int n;
        for (n = 0; n < mLDSAEngine.getDilithiumL(); ++n) {
            polyVecL.getVectorIndex(n).polyEtaUnpack(byArray2, n * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        for (n = 0; n < mLDSAEngine.getDilithiumK(); ++n) {
            polyVecK2.getVectorIndex(n).polyEtaUnpack(byArray3, n * mLDSAEngine.getDilithiumPolyEtaPackedBytes());
        }
        for (n = 0; n < mLDSAEngine.getDilithiumK(); ++n) {
            polyVecK.getVectorIndex(n).polyt0Unpack(byArray, n * 416);
        }
    }

    static void packSignature(byte[] byArray, PolyVecL polyVecL, PolyVecK polyVecK, MLDSAEngine mLDSAEngine) {
        int n;
        int n2 = mLDSAEngine.getDilithiumCTilde();
        for (n = 0; n < mLDSAEngine.getDilithiumL(); ++n) {
            polyVecL.getVectorIndex(n).zPack(byArray, n2);
            n2 += mLDSAEngine.getDilithiumPolyZPackedBytes();
        }
        for (n = 0; n < mLDSAEngine.getDilithiumOmega() + mLDSAEngine.getDilithiumK(); ++n) {
            byArray[n2 + n] = 0;
        }
        n = 0;
        for (int i = 0; i < mLDSAEngine.getDilithiumK(); ++i) {
            for (int j = 0; j < 256; ++j) {
                if (polyVecK.getVectorIndex(i).getCoeffIndex(j) == 0) continue;
                byArray[n2 + n++] = (byte)j;
            }
            byArray[n2 + mLDSAEngine.getDilithiumOmega() + i] = (byte)n;
        }
    }

    static boolean unpackSignature(PolyVecL polyVecL, PolyVecK polyVecK, byte[] byArray, MLDSAEngine mLDSAEngine) {
        int n;
        int n2;
        int n3 = mLDSAEngine.getDilithiumCTilde();
        for (n2 = 0; n2 < mLDSAEngine.getDilithiumL(); ++n2) {
            polyVecL.getVectorIndex(n2).zUnpack(Arrays.copyOfRange(byArray, n3 + n2 * mLDSAEngine.getDilithiumPolyZPackedBytes(), n3 + (n2 + 1) * mLDSAEngine.getDilithiumPolyZPackedBytes()));
        }
        n3 += mLDSAEngine.getDilithiumL() * mLDSAEngine.getDilithiumPolyZPackedBytes();
        int n4 = 0;
        for (n2 = 0; n2 < mLDSAEngine.getDilithiumK(); ++n2) {
            for (n = 0; n < 256; ++n) {
                polyVecK.getVectorIndex(n2).setCoeffIndex(n, 0);
            }
            if ((byArray[n3 + mLDSAEngine.getDilithiumOmega() + n2] & 0xFF) < n4 || (byArray[n3 + mLDSAEngine.getDilithiumOmega() + n2] & 0xFF) > mLDSAEngine.getDilithiumOmega()) {
                return false;
            }
            for (n = n4; n < (byArray[n3 + mLDSAEngine.getDilithiumOmega() + n2] & 0xFF); ++n) {
                if (n > n4 && (byArray[n3 + n] & 0xFF) <= (byArray[n3 + n - 1] & 0xFF)) {
                    return false;
                }
                polyVecK.getVectorIndex(n2).setCoeffIndex(byArray[n3 + n] & 0xFF, 1);
            }
            n4 = byArray[n3 + mLDSAEngine.getDilithiumOmega() + n2];
        }
        for (n = n4; n < mLDSAEngine.getDilithiumOmega(); ++n) {
            if ((byArray[n3 + n] & 0xFF) == 0) continue;
            return false;
        }
        return true;
    }
}

