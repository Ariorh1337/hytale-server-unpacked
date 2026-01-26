/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecK;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecL;
import org.bouncycastle.util.Arrays;

class Packing {
    Packing() {
    }

    static byte[] packPublicKey(PolyVecK polyVecK, DilithiumEngine dilithiumEngine) {
        byte[] byArray = new byte[dilithiumEngine.getCryptoPublicKeyBytes() - 32];
        for (int i = 0; i < dilithiumEngine.getDilithiumK(); ++i) {
            System.arraycopy(polyVecK.getVectorIndex(i).polyt1Pack(), 0, byArray, i * 320, 320);
        }
        return byArray;
    }

    static PolyVecK unpackPublicKey(PolyVecK polyVecK, byte[] byArray, DilithiumEngine dilithiumEngine) {
        for (int i = 0; i < dilithiumEngine.getDilithiumK(); ++i) {
            polyVecK.getVectorIndex(i).polyt1Unpack(Arrays.copyOfRange(byArray, i * 320, (i + 1) * 320));
        }
        return polyVecK;
    }

    static byte[][] packSecretKey(byte[] byArray, byte[] byArray2, byte[] byArray3, PolyVecK polyVecK, PolyVecL polyVecL, PolyVecK polyVecK2, DilithiumEngine dilithiumEngine) {
        int n;
        byte[][] byArrayArray = new byte[6][];
        byArrayArray[0] = byArray;
        byArrayArray[1] = byArray3;
        byArrayArray[2] = byArray2;
        byArrayArray[3] = new byte[dilithiumEngine.getDilithiumL() * dilithiumEngine.getDilithiumPolyEtaPackedBytes()];
        for (n = 0; n < dilithiumEngine.getDilithiumL(); ++n) {
            polyVecL.getVectorIndex(n).polyEtaPack(byArrayArray[3], n * dilithiumEngine.getDilithiumPolyEtaPackedBytes());
        }
        byArrayArray[4] = new byte[dilithiumEngine.getDilithiumK() * dilithiumEngine.getDilithiumPolyEtaPackedBytes()];
        for (n = 0; n < dilithiumEngine.getDilithiumK(); ++n) {
            polyVecK2.getVectorIndex(n).polyEtaPack(byArrayArray[4], n * dilithiumEngine.getDilithiumPolyEtaPackedBytes());
        }
        byArrayArray[5] = new byte[dilithiumEngine.getDilithiumK() * 416];
        for (n = 0; n < dilithiumEngine.getDilithiumK(); ++n) {
            polyVecK.getVectorIndex(n).polyt0Pack(byArrayArray[5], n * 416);
        }
        return byArrayArray;
    }

    static void unpackSecretKey(PolyVecK polyVecK, PolyVecL polyVecL, PolyVecK polyVecK2, byte[] byArray, byte[] byArray2, byte[] byArray3, DilithiumEngine dilithiumEngine) {
        int n;
        for (n = 0; n < dilithiumEngine.getDilithiumL(); ++n) {
            polyVecL.getVectorIndex(n).polyEtaUnpack(byArray2, n * dilithiumEngine.getDilithiumPolyEtaPackedBytes());
        }
        for (n = 0; n < dilithiumEngine.getDilithiumK(); ++n) {
            polyVecK2.getVectorIndex(n).polyEtaUnpack(byArray3, n * dilithiumEngine.getDilithiumPolyEtaPackedBytes());
        }
        for (n = 0; n < dilithiumEngine.getDilithiumK(); ++n) {
            polyVecK.getVectorIndex(n).polyt0Unpack(byArray, n * 416);
        }
    }

    static byte[] packSignature(byte[] byArray, PolyVecL polyVecL, PolyVecK polyVecK, DilithiumEngine dilithiumEngine) {
        int n;
        int n2 = 0;
        byte[] byArray2 = new byte[dilithiumEngine.getCryptoBytes()];
        System.arraycopy(byArray, 0, byArray2, 0, dilithiumEngine.getDilithiumCTilde());
        n2 += dilithiumEngine.getDilithiumCTilde();
        for (n = 0; n < dilithiumEngine.getDilithiumL(); ++n) {
            System.arraycopy(polyVecL.getVectorIndex(n).zPack(), 0, byArray2, n2 + n * dilithiumEngine.getDilithiumPolyZPackedBytes(), dilithiumEngine.getDilithiumPolyZPackedBytes());
        }
        n2 += dilithiumEngine.getDilithiumL() * dilithiumEngine.getDilithiumPolyZPackedBytes();
        for (n = 0; n < dilithiumEngine.getDilithiumOmega() + dilithiumEngine.getDilithiumK(); ++n) {
            byArray2[n2 + n] = 0;
        }
        int n3 = 0;
        for (n = 0; n < dilithiumEngine.getDilithiumK(); ++n) {
            for (int i = 0; i < 256; ++i) {
                if (polyVecK.getVectorIndex(n).getCoeffIndex(i) == 0) continue;
                byArray2[n2 + n3++] = (byte)i;
            }
            byArray2[n2 + dilithiumEngine.getDilithiumOmega() + n] = (byte)n3;
        }
        return byArray2;
    }

    static boolean unpackSignature(PolyVecL polyVecL, PolyVecK polyVecK, byte[] byArray, DilithiumEngine dilithiumEngine) {
        int n;
        int n2;
        int n3 = dilithiumEngine.getDilithiumCTilde();
        for (n2 = 0; n2 < dilithiumEngine.getDilithiumL(); ++n2) {
            polyVecL.getVectorIndex(n2).zUnpack(Arrays.copyOfRange(byArray, n3 + n2 * dilithiumEngine.getDilithiumPolyZPackedBytes(), n3 + (n2 + 1) * dilithiumEngine.getDilithiumPolyZPackedBytes()));
        }
        n3 += dilithiumEngine.getDilithiumL() * dilithiumEngine.getDilithiumPolyZPackedBytes();
        int n4 = 0;
        for (n2 = 0; n2 < dilithiumEngine.getDilithiumK(); ++n2) {
            for (n = 0; n < 256; ++n) {
                polyVecK.getVectorIndex(n2).setCoeffIndex(n, 0);
            }
            if ((byArray[n3 + dilithiumEngine.getDilithiumOmega() + n2] & 0xFF) < n4 || (byArray[n3 + dilithiumEngine.getDilithiumOmega() + n2] & 0xFF) > dilithiumEngine.getDilithiumOmega()) {
                return false;
            }
            for (n = n4; n < (byArray[n3 + dilithiumEngine.getDilithiumOmega() + n2] & 0xFF); ++n) {
                if (n > n4 && (byArray[n3 + n] & 0xFF) <= (byArray[n3 + n - 1] & 0xFF)) {
                    return false;
                }
                polyVecK.getVectorIndex(n2).setCoeffIndex(byArray[n3 + n] & 0xFF, 1);
            }
            n4 = byArray[n3 + dilithiumEngine.getDilithiumOmega() + n2];
        }
        for (n = n4; n < dilithiumEngine.getDilithiumOmega(); ++n) {
            if ((byArray[n3 + n] & 0xFF) == 0) continue;
            return false;
        }
        return true;
    }
}

