/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class Utils {
    Utils() {
    }

    public static void unpackMVecs(byte[] byArray, int n, long[] lArray, int n2, int n3, int n4) {
        int n5 = n4 + 15 >> 4;
        int n6 = n4 >> 1;
        int n7 = 8 - (n5 << 3) + n6;
        int n8 = n3 - 1;
        n2 += n8 * n5;
        n += n8 * n6;
        while (n8 >= 0) {
            int n9;
            for (n9 = 0; n9 < n5 - 1; ++n9) {
                lArray[n2 + n9] = Pack.littleEndianToLong(byArray, n + (n9 << 3));
            }
            lArray[n2 + n9] = Pack.littleEndianToLong(byArray, n + (n9 << 3), n7);
            --n8;
            n2 -= n5;
            n -= n6;
        }
    }

    public static void packMVecs(long[] lArray, byte[] byArray, int n, int n2, int n3) {
        int n4 = n3 + 15 >> 4;
        int n5 = n3 >> 1;
        int n6 = 8 - (n4 << 3) + n5;
        int n7 = 0;
        int n8 = 0;
        while (n7 < n2) {
            int n9;
            for (n9 = 0; n9 < n4 - 1; ++n9) {
                Pack.longToLittleEndian(lArray[n8 + n9], byArray, n + (n9 << 3));
            }
            Pack.longToLittleEndian(lArray[n8 + n9], byArray, n + (n9 << 3), n6);
            ++n7;
            n += n5;
            n8 += n4;
        }
    }

    public static void expandP1P2(MayoParameters mayoParameters, long[] lArray, byte[] byArray) {
        int n;
        int n2 = mayoParameters.getP1Bytes() + mayoParameters.getP2Bytes();
        byte[] byArray2 = new byte[n2];
        byte[] byArray3 = new byte[16];
        MultiBlockCipher multiBlockCipher = AESEngine.newInstance();
        CTRModeCipher cTRModeCipher = SICBlockCipher.newInstance(multiBlockCipher);
        ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(Arrays.copyOf(byArray, mayoParameters.getPkSeedBytes())), byArray3);
        cTRModeCipher.init(true, parametersWithIV);
        int n3 = cTRModeCipher.getBlockSize();
        byte[] byArray4 = new byte[n3];
        byte[] byArray5 = new byte[n3];
        int n4 = 0;
        while (n4 + n3 <= n2) {
            cTRModeCipher.processBlock(byArray4, 0, byArray5, 0);
            System.arraycopy(byArray5, 0, byArray2, n4, n3);
            n4 += n3;
        }
        if (n4 < n2) {
            cTRModeCipher.processBlock(byArray4, 0, byArray5, 0);
            n = n2 - n4;
            System.arraycopy(byArray5, 0, byArray2, n4, n);
        }
        n = (mayoParameters.getP1Limbs() + mayoParameters.getP2Limbs()) / mayoParameters.getMVecLimbs();
        Utils.unpackMVecs(byArray2, 0, lArray, 0, n, mayoParameters.getM());
    }
}

