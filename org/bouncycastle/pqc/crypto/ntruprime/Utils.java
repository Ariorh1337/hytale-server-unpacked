/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

class Utils {
    Utils() {
    }

    protected static int getRandomUnsignedInteger(SecureRandom secureRandom) {
        byte[] byArray = new byte[4];
        secureRandom.nextBytes(byArray);
        return Utils.bToUnsignedInt(byArray[0]) + (Utils.bToUnsignedInt(byArray[1]) << 8) + (Utils.bToUnsignedInt(byArray[2]) << 16) + (Utils.bToUnsignedInt(byArray[3]) << 24);
    }

    protected static void getRandomSmallPolynomial(SecureRandom secureRandom, byte[] byArray) {
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(((Utils.getRandomUnsignedInteger(secureRandom) & 0x3FFFFFFF) * 3 >>> 30) - 1);
        }
    }

    protected static int getModFreeze(int n, int n2) {
        return Utils.getSignedDivMod(n + (n2 - 1) / 2, n2)[1] - (n2 - 1) / 2;
    }

    protected static boolean isInvertiblePolynomialInR3(byte[] byArray, byte[] byArray2, int n) {
        int n2;
        int n3;
        byte[] byArray3 = new byte[n + 1];
        byte[] byArray4 = new byte[n + 1];
        byte[] byArray5 = new byte[n + 1];
        byte[] byArray6 = new byte[n + 1];
        byArray5[0] = 1;
        byArray3[0] = 1;
        byArray3[n - 1] = -1;
        byArray3[n] = -1;
        for (n3 = 0; n3 < n; ++n3) {
            byArray4[n - 1 - n3] = byArray[n3];
        }
        byArray4[n] = 0;
        int n4 = 1;
        for (int i = 0; i < 2 * n - 1; ++i) {
            System.arraycopy(byArray6, 0, byArray6, 1, n);
            byArray6[0] = 0;
            n2 = -byArray4[0] * byArray3[0];
            int n5 = Utils.checkLessThanZero(-n4) & Utils.checkNotEqualToZero(byArray4[0]);
            n4 ^= n5 & (n4 ^ -n4);
            ++n4;
            n3 = 0;
            while (n3 < n + 1) {
                int n6 = n5 & (byArray3[n3] ^ byArray4[n3]);
                int n7 = n3;
                byArray3[n7] = (byte)(byArray3[n7] ^ n6);
                int n8 = n3;
                byArray4[n8] = (byte)(byArray4[n8] ^ n6);
                n6 = n5 & (byArray6[n3] ^ byArray5[n3]);
                int n9 = n3;
                byArray6[n9] = (byte)(byArray6[n9] ^ n6);
                int n10 = n3++;
                byArray5[n10] = (byte)(byArray5[n10] ^ n6);
            }
            for (n3 = 0; n3 < n + 1; ++n3) {
                byArray4[n3] = (byte)Utils.getModFreeze(byArray4[n3] + n2 * byArray3[n3], 3);
            }
            for (n3 = 0; n3 < n + 1; ++n3) {
                byArray5[n3] = (byte)Utils.getModFreeze(byArray5[n3] + n2 * byArray6[n3], 3);
            }
            for (n3 = 0; n3 < n; ++n3) {
                byArray4[n3] = byArray4[n3 + 1];
            }
            byArray4[n] = 0;
        }
        n2 = byArray3[0];
        for (n3 = 0; n3 < n; ++n3) {
            byArray2[n3] = (byte)(n2 * byArray6[n - 1 - n3]);
        }
        return n4 == 0;
    }

    protected static void minmax(int[] nArray, int n, int n2) {
        int n3 = nArray[n];
        int n4 = nArray[n2];
        int n5 = n3 ^ n4;
        int n6 = n4 - n3;
        n6 ^= n5 & (n6 ^ n4 ^ Integer.MIN_VALUE);
        n6 >>>= 31;
        n6 = -n6;
        nArray[n] = n3 ^ (n6 &= n5);
        nArray[n2] = n4 ^ n6;
    }

    protected static void cryptoSort(int[] nArray, int n) {
        if (n < 2) {
            return;
        }
        for (int i = 1; i < n - i; i += i) {
        }
        for (int i = i; i > 0; i >>>= 1) {
            int n2;
            for (n2 = 0; n2 < n - i; ++n2) {
                if ((n2 & i) != 0) continue;
                Utils.minmax(nArray, n2, n2 + i);
            }
            for (int j = i; j > i; j >>>= 1) {
                for (n2 = 0; n2 < n - j; ++n2) {
                    if ((n2 & i) != 0) continue;
                    Utils.minmax(nArray, n2 + i, n2 + j);
                }
            }
        }
    }

    protected static void sortGenerateShortPolynomial(byte[] byArray, int[] nArray, int n, int n2) {
        int n3;
        for (n3 = 0; n3 < n2; ++n3) {
            nArray[n3] = nArray[n3] & 0xFFFFFFFE;
        }
        for (n3 = n2; n3 < n; ++n3) {
            nArray[n3] = nArray[n3] & 0xFFFFFFFD | 1;
        }
        Utils.cryptoSort(nArray, n);
        for (n3 = 0; n3 < n; ++n3) {
            byArray[n3] = (byte)((nArray[n3] & 3) - 1);
        }
    }

    protected static void getRandomShortPolynomial(SecureRandom secureRandom, byte[] byArray, int n, int n2) {
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = Utils.getRandomUnsignedInteger(secureRandom);
        }
        Utils.sortGenerateShortPolynomial(byArray, nArray, n, n2);
    }

    protected static int getInverseInRQ(int n, int n2) {
        int n3 = n;
        for (int i = 1; i < n2 - 2; ++i) {
            n3 = Utils.getModFreeze(n * n3, n2);
        }
        return n3;
    }

    protected static void getOneThirdInverseInRQ(short[] sArray, byte[] byArray, int n, int n2) {
        int n3;
        short[] sArray2 = new short[n + 1];
        short[] sArray3 = new short[n + 1];
        short[] sArray4 = new short[n + 1];
        short[] sArray5 = new short[n + 1];
        sArray4[0] = (short)Utils.getInverseInRQ(3, n2);
        sArray2[0] = 1;
        sArray2[n - 1] = -1;
        sArray2[n] = -1;
        for (n3 = 0; n3 < n; ++n3) {
            sArray3[n - 1 - n3] = byArray[n3];
        }
        sArray3[n] = 0;
        int n4 = 1;
        for (int i = 0; i < 2 * n - 1; ++i) {
            System.arraycopy(sArray5, 0, sArray5, 1, n);
            sArray5[0] = 0;
            int n5 = Utils.checkLessThanZero(-n4) & Utils.checkNotEqualToZero(sArray3[0]);
            n4 ^= n5 & (n4 ^ -n4);
            ++n4;
            n3 = 0;
            while (n3 < n + 1) {
                int n6 = n5 & (sArray2[n3] ^ sArray3[n3]);
                int n7 = n3;
                sArray2[n7] = (short)(sArray2[n7] ^ n6);
                int n8 = n3;
                sArray3[n8] = (short)(sArray3[n8] ^ n6);
                n6 = n5 & (sArray5[n3] ^ sArray4[n3]);
                int n9 = n3;
                sArray5[n9] = (short)(sArray5[n9] ^ n6);
                int n10 = n3++;
                sArray4[n10] = (short)(sArray4[n10] ^ n6);
            }
            short s = sArray2[0];
            short s2 = sArray3[0];
            for (n3 = 0; n3 < n + 1; ++n3) {
                sArray3[n3] = (short)Utils.getModFreeze(s * sArray3[n3] - s2 * sArray2[n3], n2);
            }
            for (n3 = 0; n3 < n + 1; ++n3) {
                sArray4[n3] = (short)Utils.getModFreeze(s * sArray4[n3] - s2 * sArray5[n3], n2);
            }
            for (n3 = 0; n3 < n; ++n3) {
                sArray3[n3] = sArray3[n3 + 1];
            }
            sArray3[n] = 0;
        }
        int n11 = Utils.getInverseInRQ(sArray2[0], n2);
        for (n3 = 0; n3 < n; ++n3) {
            sArray[n3] = (short)Utils.getModFreeze(n11 * sArray5[n - 1 - n3], n2);
        }
    }

    protected static void multiplicationInRQ(short[] sArray, short[] sArray2, byte[] byArray, int n, int n2) {
        int n3;
        short s;
        int n4;
        short[] sArray3 = new short[n + n - 1];
        for (n4 = 0; n4 < n; ++n4) {
            s = 0;
            for (n3 = 0; n3 <= n4; ++n3) {
                s = (short)Utils.getModFreeze(s + sArray2[n3] * byArray[n4 - n3], n2);
            }
            sArray3[n4] = s;
        }
        for (n4 = n; n4 < n + n - 1; ++n4) {
            s = 0;
            for (n3 = n4 - n + 1; n3 < n; ++n3) {
                s = (short)Utils.getModFreeze(s + sArray2[n3] * byArray[n4 - n3], n2);
            }
            sArray3[n4] = s;
        }
        for (n4 = n + n - 2; n4 >= n; --n4) {
            sArray3[n4 - n] = (short)Utils.getModFreeze(sArray3[n4 - n] + sArray3[n4], n2);
            sArray3[n4 - n + 1] = (short)Utils.getModFreeze(sArray3[n4 - n + 1] + sArray3[n4], n2);
        }
        for (n4 = 0; n4 < n; ++n4) {
            sArray[n4] = sArray3[n4];
        }
    }

    private static void encode(byte[] byArray, short[] sArray, short[] sArray2, int n, int n2) {
        if (n == 1) {
            short s = sArray[0];
            short s2 = sArray2[0];
            while (s2 > 1) {
                byArray[n2++] = (byte)s;
                s = (short)(s >>> 8);
                s2 = (short)(s2 + 255 >>> 8);
            }
        }
        if (n > 1) {
            int n3;
            short[] sArray3 = new short[(n + 1) / 2];
            short[] sArray4 = new short[(n + 1) / 2];
            for (n3 = 0; n3 < n - 1; n3 += 2) {
                short s = sArray2[n3];
                int n4 = sArray[n3] + sArray[n3 + 1] * s;
                int n5 = sArray2[n3 + 1] * s;
                while (n5 >= 16384) {
                    byArray[n2++] = (byte)n4;
                    n4 >>>= 8;
                    n5 = n5 + 255 >>> 8;
                }
                sArray3[n3 / 2] = (short)n4;
                sArray4[n3 / 2] = (short)n5;
            }
            if (n3 < n) {
                sArray3[n3 / 2] = sArray[n3];
                sArray4[n3 / 2] = sArray2[n3];
            }
            Utils.encode(byArray, sArray3, sArray4, (n + 1) / 2, n2);
        }
    }

    protected static void getEncodedPolynomial(byte[] byArray, short[] sArray, int n, int n2) {
        int n3;
        short[] sArray2 = new short[n];
        short[] sArray3 = new short[n];
        for (n3 = 0; n3 < n; ++n3) {
            sArray2[n3] = (short)(sArray[n3] + (n2 - 1) / 2);
        }
        for (n3 = 0; n3 < n; ++n3) {
            sArray3[n3] = (short)n2;
        }
        Utils.encode(byArray, sArray2, sArray3, n, 0);
    }

    protected static void getEncodedSmallPolynomial(byte[] byArray, byte[] byArray2, int n) {
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < n / 4; ++i) {
            byte by = (byte)(byArray2[n2++] + 1);
            by = (byte)(by + ((byte)(byArray2[n2++] + 1) << 2));
            by = (byte)(by + ((byte)(byArray2[n2++] + 1) << 4));
            by = (byte)(by + ((byte)(byArray2[n2++] + 1) << 6));
            byArray[n3++] = by;
        }
        byArray[n3] = (byte)(byArray2[n2] + 1);
    }

    private static void generateAES256CTRStream(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        CTRModeCipher cTRModeCipher = SICBlockCipher.newInstance(AESEngine.newInstance());
        cTRModeCipher.init(true, new ParametersWithIV(new KeyParameter(byArray4), byArray3));
        cTRModeCipher.processBytes(byArray, 0, byArray2.length, byArray2, 0);
    }

    protected static void expand(int[] nArray, byte[] byArray) {
        byte[] byArray2 = new byte[nArray.length * 4];
        byte[] byArray3 = new byte[nArray.length * 4];
        byte[] byArray4 = new byte[16];
        Utils.generateAES256CTRStream(byArray2, byArray3, byArray4, byArray);
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = Utils.bToUnsignedInt(byArray3[i * 4]) + (Utils.bToUnsignedInt(byArray3[i * 4 + 1]) << 8) + (Utils.bToUnsignedInt(byArray3[i * 4 + 2]) << 16) + (Utils.bToUnsignedInt(byArray3[i * 4 + 3]) << 24);
        }
    }

    private static int getUnsignedMod(int n, int n2) {
        return Utils.getUnsignedDivMod(n, n2)[1];
    }

    protected static void generatePolynomialInRQFromSeed(short[] sArray, byte[] byArray, int n, int n2) {
        int[] nArray = new int[n];
        Utils.expand(nArray, byArray);
        for (int i = 0; i < n; ++i) {
            sArray[i] = (short)(Utils.getUnsignedMod(nArray[i], n2) - (n2 - 1) / 2);
        }
    }

    protected static void roundPolynomial(short[] sArray, short[] sArray2) {
        for (int i = 0; i < sArray.length; ++i) {
            sArray[i] = (short)(sArray2[i] - Utils.getModFreeze(sArray2[i], 3));
        }
    }

    protected static void getRoundedEncodedPolynomial(byte[] byArray, short[] sArray, int n, int n2) {
        short[] sArray2 = new short[n];
        short[] sArray3 = new short[n];
        for (int i = 0; i < n; ++i) {
            sArray2[i] = (short)((sArray[i] + (n2 - 1) / 2) * 10923 >>> 15);
            sArray3[i] = (short)((n2 + 2) / 3);
        }
        Utils.encode(byArray, sArray2, sArray3, n, 0);
    }

    protected static byte[] getHashWithPrefix(byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = new byte[64];
        byte[] byArray4 = new byte[byArray.length + byArray2.length];
        System.arraycopy(byArray, 0, byArray4, 0, byArray.length);
        System.arraycopy(byArray2, 0, byArray4, byArray.length, byArray2.length);
        SHA512Digest sHA512Digest = new SHA512Digest();
        sHA512Digest.update(byArray4, 0, byArray4.length);
        sHA512Digest.doFinal(byArray3, 0);
        return byArray3;
    }

    private static void decode(short[] sArray, byte[] byArray, short[] sArray2, int n, int n2, int n3) {
        if (n == 1) {
            sArray[n2] = sArray2[0] == 1 ? (short)0 : (sArray2[0] <= 256 ? (short)Utils.getUnsignedMod(Utils.bToUnsignedInt(byArray[n3]), sArray2[0]) : (short)Utils.getUnsignedMod(Utils.bToUnsignedInt(byArray[n3]) + (byArray[n3 + 1] << 8), sArray2[0]));
        }
        if (n > 1) {
            int n4;
            int n5;
            short[] sArray3 = new short[(n + 1) / 2];
            short[] sArray4 = new short[(n + 1) / 2];
            short[] sArray5 = new short[n / 2];
            int[] nArray = new int[n / 2];
            for (n5 = 0; n5 < n - 1; n5 += 2) {
                n4 = sArray2[n5] * sArray2[n5 + 1];
                if (n4 > 0x3FFF00) {
                    nArray[n5 / 2] = 65536;
                    sArray5[n5 / 2] = (short)(Utils.bToUnsignedInt(byArray[n3]) + 256 * Utils.bToUnsignedInt(byArray[n3 + 1]));
                    n3 += 2;
                    sArray4[n5 / 2] = (short)((n4 + 255 >>> 8) + 255 >>> 8);
                    continue;
                }
                if (n4 >= 16384) {
                    nArray[n5 / 2] = 256;
                    sArray5[n5 / 2] = (short)Utils.bToUnsignedInt(byArray[n3]);
                    ++n3;
                    sArray4[n5 / 2] = (short)(n4 + 255 >>> 8);
                    continue;
                }
                nArray[n5 / 2] = 1;
                sArray5[n5 / 2] = 0;
                sArray4[n5 / 2] = (short)n4;
            }
            if (n5 < n) {
                sArray4[n5 / 2] = sArray2[n5];
            }
            Utils.decode(sArray3, byArray, sArray4, (n + 1) / 2, n2, n3);
            for (n5 = 0; n5 < n - 1; n5 += 2) {
                n4 = Utils.sToUnsignedInt(sArray5[n5 / 2]);
                int[] nArray2 = Utils.getUnsignedDivMod(n4 += nArray[n5 / 2] * Utils.sToUnsignedInt(sArray3[n5 / 2]), sArray2[n5]);
                sArray[n2++] = (short)nArray2[1];
                sArray[n2++] = (short)Utils.getUnsignedMod(nArray2[0], sArray2[n5 + 1]);
            }
            if (n5 < n) {
                sArray[n2] = sArray3[n5 / 2];
            }
        }
    }

    protected static void getDecodedPolynomial(short[] sArray, byte[] byArray, int n, int n2) {
        int n3;
        short[] sArray2 = new short[n];
        short[] sArray3 = new short[n];
        for (n3 = 0; n3 < n; ++n3) {
            sArray3[n3] = (short)n2;
        }
        Utils.decode(sArray2, byArray, sArray3, n, 0, 0);
        for (n3 = 0; n3 < n; ++n3) {
            sArray[n3] = (short)(sArray2[n3] - (n2 - 1) / 2);
        }
    }

    protected static void getRandomInputs(SecureRandom secureRandom, byte[] byArray) {
        byte[] byArray2 = new byte[byArray.length / 8];
        secureRandom.nextBytes(byArray2);
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(1 & byArray2[i >>> 3] >>> (i & 7));
        }
    }

    protected static void getEncodedInputs(byte[] byArray, byte[] byArray2) {
        for (int i = 0; i < byArray2.length; ++i) {
            int n = i >>> 3;
            byArray[n] = (byte)(byArray[n] | byArray2[i] << (i & 7));
        }
    }

    protected static void getRoundedDecodedPolynomial(short[] sArray, byte[] byArray, int n, int n2) {
        int n3;
        short[] sArray2 = new short[n];
        short[] sArray3 = new short[n];
        for (n3 = 0; n3 < n; ++n3) {
            sArray3[n3] = (short)((n2 + 2) / 3);
        }
        Utils.decode(sArray2, byArray, sArray3, n, 0, 0);
        for (n3 = 0; n3 < n; ++n3) {
            sArray[n3] = (short)(sArray2[n3] * 3 - (n2 - 1) / 2);
        }
    }

    protected static void top(byte[] byArray, short[] sArray, byte[] byArray2, int n, int n2, int n3) {
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(n3 * (Utils.getModFreeze(sArray[i] + byArray2[i] * ((n - 1) / 2), n) + n2) + 16384 >>> 15);
        }
    }

    protected static void getTopEncodedPolynomial(byte[] byArray, byte[] byArray2) {
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(byArray2[2 * i] + (byArray2[2 * i + 1] << 4));
        }
    }

    protected static void getDecodedSmallPolynomial(byte[] byArray, byte[] byArray2, int n) {
        byte by;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < n / 4; ++i) {
            by = byArray2[n3++];
            byArray[n2++] = (byte)((Utils.bToUnsignedInt(by) & 3) - 1);
            by = (byte)(by >>> 2);
            byArray[n2++] = (byte)((Utils.bToUnsignedInt(by) & 3) - 1);
            by = (byte)(by >>> 2);
            byArray[n2++] = (byte)((Utils.bToUnsignedInt(by) & 3) - 1);
            by = (byte)(by >>> 2);
            byArray[n2++] = (byte)((Utils.bToUnsignedInt(by) & 3) - 1);
        }
        by = byArray2[n3];
        byArray[n2] = (byte)((Utils.bToUnsignedInt(by) & 3) - 1);
    }

    protected static void scalarMultiplicationInRQ(short[] sArray, short[] sArray2, int n, int n2) {
        for (int i = 0; i < sArray2.length; ++i) {
            sArray[i] = (short)Utils.getModFreeze(n * sArray2[i], n2);
        }
    }

    protected static void transformRQToR3(byte[] byArray, short[] sArray) {
        for (int i = 0; i < sArray.length; ++i) {
            byArray[i] = (byte)Utils.getModFreeze(sArray[i], 3);
        }
    }

    protected static void multiplicationInR3(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        int n2;
        byte by;
        int n3;
        byte[] byArray4 = new byte[n + n - 1];
        for (n3 = 0; n3 < n; ++n3) {
            by = 0;
            for (n2 = 0; n2 <= n3; ++n2) {
                by = (byte)Utils.getModFreeze(by + byArray2[n2] * byArray3[n3 - n2], 3);
            }
            byArray4[n3] = by;
        }
        for (n3 = n; n3 < n + n - 1; ++n3) {
            by = 0;
            for (n2 = n3 - n + 1; n2 < n; ++n2) {
                by = (byte)Utils.getModFreeze(by + byArray2[n2] * byArray3[n3 - n2], 3);
            }
            byArray4[n3] = by;
        }
        for (n3 = n + n - 2; n3 >= n; --n3) {
            byArray4[n3 - n] = (byte)Utils.getModFreeze(byArray4[n3 - n] + byArray4[n3], 3);
            byArray4[n3 - n + 1] = (byte)Utils.getModFreeze(byArray4[n3 - n + 1] + byArray4[n3], 3);
        }
        for (n3 = 0; n3 < n; ++n3) {
            byArray[n3] = byArray4[n3];
        }
    }

    protected static void checkForSmallPolynomial(byte[] byArray, byte[] byArray2, int n, int n2) {
        int n3;
        int n4;
        int n5 = 0;
        for (n4 = 0; n4 != byArray2.length; ++n4) {
            n5 += byArray2[n4] & 1;
        }
        n4 = Utils.checkNotEqualToZero(n5 - n2);
        for (n3 = 0; n3 < n2; ++n3) {
            byArray[n3] = (byte)((byArray2[n3] ^ 1) & ~n4 ^ 1);
        }
        for (n3 = n2; n3 < n; ++n3) {
            byArray[n3] = (byte)(byArray2[n3] & ~n4);
        }
    }

    protected static void updateDiffMask(byte[] byArray, byte[] byArray2, int n) {
        for (int i = 0; i < byArray.length; ++i) {
            int n2 = i;
            byArray[n2] = (byte)(byArray[n2] ^ n & (byArray[i] ^ byArray2[i]));
        }
    }

    protected static void getTopDecodedPolynomial(byte[] byArray, byte[] byArray2) {
        for (int i = 0; i < byArray2.length; ++i) {
            byArray[2 * i] = (byte)(byArray2[i] & 0xF);
            byArray[2 * i + 1] = (byte)(byArray2[i] >>> 4);
        }
    }

    protected static void right(byte[] byArray, short[] sArray, byte[] byArray2, int n, int n2, int n3, int n4) {
        for (int i = 0; i < byArray.length; ++i) {
            byArray[i] = (byte)(-Utils.checkLessThanZero(Utils.getModFreeze(Utils.getModFreeze(n4 * byArray2[i] - n3, n) - sArray[i] + 4 * n2 + 1, n)));
        }
    }

    private static int[] getUnsignedDivMod(int n, int n2) {
        long l = Utils.iToUnsignedLong(n);
        long l2 = Utils.iToUnsignedLong(Integer.MIN_VALUE);
        long l3 = 0L;
        long l4 = l * (l2 /= (long)n2) >>> 31;
        l -= l4 * (long)n2;
        l3 += l4;
        l4 = l * l2 >>> 31;
        l -= l4 * (long)n2;
        l3 += l4;
        ++l3;
        long l5 = -((l -= (long)n2) >>> 63);
        return new int[]{Utils.toIntExact(l3 += l5), Utils.toIntExact(l += l5 & (long)n2)};
    }

    private static int[] getSignedDivMod(int n, int n2) {
        int[] nArray = Utils.getUnsignedDivMod(Utils.toIntExact(Integer.MIN_VALUE + Utils.iToUnsignedLong(n)), n2);
        int[] nArray2 = Utils.getUnsignedDivMod(Integer.MIN_VALUE, n2);
        int n3 = Utils.toIntExact(Utils.iToUnsignedLong(nArray[0]) - Utils.iToUnsignedLong(nArray2[0]));
        int n4 = Utils.toIntExact(Utils.iToUnsignedLong(nArray[1]) - Utils.iToUnsignedLong(nArray2[1]));
        int n5 = -(n4 >>> 31);
        return new int[]{n3 += n5, n4 += n5 & n2};
    }

    private static int checkLessThanZero(int n) {
        return -(n >>> 31);
    }

    private static int checkNotEqualToZero(int n) {
        long l = Utils.iToUnsignedLong(n);
        l = -l;
        return -((int)(l >>> 63));
    }

    static int bToUnsignedInt(byte by) {
        return by & 0xFF;
    }

    static int sToUnsignedInt(short s) {
        return s & 0xFFFF;
    }

    static long iToUnsignedLong(int n) {
        return (long)n & 0xFFFFFFFFL;
    }

    static int toIntExact(long l) {
        int n = (int)l;
        if ((long)n != l) {
            throw new IllegalStateException("value out of integer range");
        }
        return n;
    }
}

