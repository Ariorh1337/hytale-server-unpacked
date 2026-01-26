/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

class RainbowUtil {
    RainbowUtil() {
    }

    public static short[] convertArray(byte[] byArray) {
        short[] sArray = new short[byArray.length];
        for (int i = 0; i < byArray.length; ++i) {
            sArray[i] = (short)(byArray[i] & 0xFF);
        }
        return sArray;
    }

    public static byte[] convertArray(short[] sArray) {
        byte[] byArray = new byte[sArray.length];
        for (int i = 0; i < sArray.length; ++i) {
            byArray[i] = (byte)sArray[i];
        }
        return byArray;
    }

    public static boolean equals(short[] sArray, short[] sArray2) {
        if (sArray.length != sArray2.length) {
            return false;
        }
        boolean bl = true;
        for (int i = sArray.length - 1; i >= 0; --i) {
            bl &= sArray[i] == sArray2[i];
        }
        return bl;
    }

    public static boolean equals(short[][] sArray, short[][] sArray2) {
        if (sArray.length != sArray2.length) {
            return false;
        }
        boolean bl = true;
        for (int i = sArray.length - 1; i >= 0; --i) {
            bl &= RainbowUtil.equals(sArray[i], sArray2[i]);
        }
        return bl;
    }

    public static boolean equals(short[][][] sArray, short[][][] sArray2) {
        if (sArray.length != sArray2.length) {
            return false;
        }
        boolean bl = true;
        for (int i = sArray.length - 1; i >= 0; --i) {
            bl &= RainbowUtil.equals(sArray[i], sArray2[i]);
        }
        return bl;
    }

    public static short[][] cloneArray(short[][] sArray) {
        short[][] sArrayArray = new short[sArray.length][];
        for (int i = 0; i < sArray.length; ++i) {
            sArrayArray[i] = Arrays.clone(sArray[i]);
        }
        return sArrayArray;
    }

    public static short[][][] cloneArray(short[][][] sArray) {
        short[][][] sArray2 = new short[sArray.length][sArray[0].length][];
        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray[0].length; ++j) {
                sArray2[i][j] = Arrays.clone(sArray[i][j]);
            }
        }
        return sArray2;
    }

    public static byte[] hash(Digest digest, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n = digest.getDigestSize();
        digest.update(byArray, 0, byArray.length);
        digest.update(byArray2, 0, byArray2.length);
        if (byArray3.length == n) {
            digest.doFinal(byArray3, 0);
            return byArray3;
        }
        byte[] byArray4 = new byte[n];
        digest.doFinal(byArray4, 0);
        if (byArray3.length < n) {
            System.arraycopy(byArray4, 0, byArray3, 0, byArray3.length);
            return byArray3;
        }
        System.arraycopy(byArray4, 0, byArray3, 0, byArray4.length);
        int n2 = byArray3.length - n;
        int n3 = n;
        while (n2 >= byArray4.length) {
            digest.update(byArray4, 0, byArray4.length);
            digest.doFinal(byArray4, 0);
            System.arraycopy(byArray4, 0, byArray3, n3, byArray4.length);
            n2 -= byArray4.length;
            n3 += byArray4.length;
        }
        if (n2 > 0) {
            digest.update(byArray4, 0, byArray4.length);
            digest.doFinal(byArray4, 0);
            System.arraycopy(byArray4, 0, byArray3, n3, n2);
        }
        return byArray3;
    }

    public static byte[] hash(Digest digest, byte[] byArray, int n) {
        int n2;
        int n3 = digest.getDigestSize();
        digest.update(byArray, 0, byArray.length);
        byte[] byArray2 = new byte[n3];
        digest.doFinal(byArray2, 0);
        if (n == n3) {
            return byArray2;
        }
        if (n < n3) {
            return Arrays.copyOf(byArray2, n);
        }
        byte[] byArray3 = Arrays.copyOf(byArray2, n3);
        for (n2 = n - n3; n2 >= n3; n2 -= n3) {
            digest.update(byArray2, 0, n3);
            byArray2 = new byte[n3];
            digest.doFinal(byArray2, 0);
            byArray3 = Arrays.concatenate(byArray3, byArray2);
        }
        if (n2 > 0) {
            digest.update(byArray2, 0, n3);
            byArray2 = new byte[n3];
            digest.doFinal(byArray2, 0);
            int n4 = byArray3.length;
            byArray3 = Arrays.copyOf(byArray3, n4 + n2);
            System.arraycopy(byArray2, 0, byArray3, n4, n2);
        }
        return byArray3;
    }

    public static short[][] generate_random_2d(SecureRandom secureRandom, int n, int n2) {
        byte[] byArray = new byte[n * n2];
        secureRandom.nextBytes(byArray);
        short[][] sArray = new short[n][n2];
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n; ++j) {
                sArray[j][i] = (short)(byArray[i * n + j] & 0xFF);
            }
        }
        return sArray;
    }

    public static short[][][] generate_random(SecureRandom secureRandom, int n, int n2, int n3, boolean bl) {
        int n4 = bl ? n * (n2 * (n2 + 1) / 2) : n * n2 * n3;
        byte[] byArray = new byte[n4];
        secureRandom.nextBytes(byArray);
        int n5 = 0;
        short[][][] sArray = new short[n][n2][n3];
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n3; ++j) {
                for (int k = 0; k < n; ++k) {
                    if (bl && i > j) continue;
                    sArray[k][i][j] = (short)(byArray[n5++] & 0xFF);
                }
            }
        }
        return sArray;
    }

    public static byte[] getEncoded(short[][] sArray) {
        int n = sArray.length;
        int n2 = sArray[0].length;
        byte[] byArray = new byte[n * n2];
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n; ++j) {
                byArray[i * n + j] = (byte)sArray[j][i];
            }
        }
        return byArray;
    }

    public static byte[] getEncoded(short[][][] sArray, boolean bl) {
        int n = sArray.length;
        int n2 = sArray[0].length;
        int n3 = sArray[0][0].length;
        int n4 = bl ? n * (n2 * (n2 + 1) / 2) : n * n2 * n3;
        byte[] byArray = new byte[n4];
        int n5 = 0;
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < n3; ++j) {
                for (int k = 0; k < n; ++k) {
                    if (bl && i > j) continue;
                    byArray[n5] = (byte)sArray[k][i][j];
                    ++n5;
                }
            }
        }
        return byArray;
    }

    public static int loadEncoded(short[][] sArray, byte[] byArray, int n) {
        int n2 = sArray.length;
        int n3 = sArray[0].length;
        for (int i = 0; i < n3; ++i) {
            for (int j = 0; j < n2; ++j) {
                sArray[j][i] = (short)(byArray[n + i * n2 + j] & 0xFF);
            }
        }
        return n2 * n3;
    }

    public static int loadEncoded(short[][][] sArray, byte[] byArray, int n, boolean bl) {
        int n2 = sArray.length;
        int n3 = sArray[0].length;
        int n4 = sArray[0][0].length;
        int n5 = 0;
        for (int i = 0; i < n3; ++i) {
            for (int j = 0; j < n4; ++j) {
                for (int k = 0; k < n2; ++k) {
                    if (bl && i > j) continue;
                    sArray[k][i][j] = (short)(byArray[n + n5++] & 0xFF);
                }
            }
        }
        return n5;
    }
}

