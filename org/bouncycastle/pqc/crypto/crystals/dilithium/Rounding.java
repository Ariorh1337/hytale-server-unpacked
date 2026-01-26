/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;

class Rounding {
    Rounding() {
    }

    public static int[] power2Round(int n) {
        int[] nArray;
        nArray = new int[]{n + 4096 - 1 >> 13, n - (nArray[0] << 13)};
        return nArray;
    }

    public static int[] decompose(int n, int n2) {
        int n3 = n + 127 >> 7;
        if (n2 == 261888) {
            n3 = n3 * 1025 + 0x200000 >> 22;
            n3 &= 0xF;
        } else if (n2 == 95232) {
            n3 = n3 * 11275 + 0x800000 >> 24;
            n3 ^= 43 - n3 >> 31 & n3;
        } else {
            throw new RuntimeException("Wrong Gamma2!");
        }
        int n4 = n - n3 * 2 * n2;
        n4 -= 0x3FF000 - n4 >> 31 & 0x7FE001;
        return new int[]{n4, n3};
    }

    public static int makeHint(int n, int n2, DilithiumEngine dilithiumEngine) {
        int n3 = dilithiumEngine.getDilithiumGamma2();
        int n4 = 8380417;
        if (n <= n3 || n > n4 - n3 || n == n4 - n3 && n2 == 0) {
            return 0;
        }
        return 1;
    }

    public static int useHint(int n, int n2, int n3) {
        int[] nArray = Rounding.decompose(n, n3);
        int n4 = nArray[0];
        int n5 = nArray[1];
        if (n2 == 0) {
            return n5;
        }
        if (n3 == 261888) {
            if (n4 > 0) {
                return n5 + 1 & 0xF;
            }
            return n5 - 1 & 0xF;
        }
        if (n3 == 95232) {
            if (n4 > 0) {
                return n5 == 43 ? 0 : n5 + 1;
            }
            return n5 == 0 ? 43 : n5 - 1;
        }
        throw new RuntimeException("Wrong Gamma2!");
    }
}

