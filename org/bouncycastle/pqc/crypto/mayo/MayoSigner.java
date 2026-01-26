/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.mayo.GF16Utils;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.GF16;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

public class MayoSigner
implements MessageSigner {
    private SecureRandom random;
    private MayoParameters params;
    private MayoPublicKeyParameters pubKey;
    private MayoPrivateKeyParameters privKey;
    private static final int F_TAIL_LEN = 4;
    private static final long EVEN_BYTES = 0xFF00FF00FF00FFL;
    private static final long EVEN_2BYTES = 0xFFFF0000FFFFL;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            this.pubKey = null;
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.privKey = (MayoPrivateKeyParameters)parametersWithRandom.getParameters();
                this.random = parametersWithRandom.getRandom();
            } else {
                this.privKey = (MayoPrivateKeyParameters)cipherParameters;
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.params = this.privKey.getParameters();
        } else {
            this.pubKey = (MayoPublicKeyParameters)cipherParameters;
            this.params = this.pubKey.getParameters();
            this.privKey = null;
            this.random = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] generateSignature(byte[] byArray) {
        int n = this.params.getK();
        int n2 = this.params.getV();
        int n3 = this.params.getO();
        int n4 = this.params.getN();
        int n5 = this.params.getM();
        int n6 = this.params.getVBytes();
        int n7 = this.params.getOBytes();
        int n8 = this.params.getSaltBytes();
        int n9 = this.params.getMVecLimbs();
        int n10 = this.params.getP1Limbs();
        int n11 = this.params.getPkSeedBytes();
        int n12 = this.params.getDigestBytes();
        int n13 = this.params.getSkSeedBytes();
        byte[] byArray2 = new byte[this.params.getMBytes()];
        byte[] byArray3 = new byte[n5];
        byte[] byArray4 = new byte[n5];
        byte[] byArray5 = new byte[n8];
        byte[] byArray6 = new byte[n * n6 + this.params.getRBytes()];
        byte[] byArray7 = new byte[n2 * n];
        int n14 = n * n3;
        int n15 = n * n4;
        byte[] byArray8 = new byte[(n5 + 7) / 8 * 8 * (n14 + 1)];
        byte[] byArray9 = new byte[n15];
        byte[] byArray10 = new byte[n14 + 1];
        byte[] byArray11 = new byte[n15];
        byte[] byArray12 = new byte[n12 + n8 + n13 + 1];
        byte[] byArray13 = new byte[this.params.getSigBytes()];
        long[] lArray = new long[n10 + this.params.getP2Limbs()];
        byte[] byArray14 = new byte[n2 * n3];
        long[] lArray2 = new long[n14 * n9];
        long[] lArray3 = new long[n * n * n9];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        try {
            int n16;
            int n17;
            int n18;
            int n19;
            byte[] byArray15 = this.privKey.getSeedSk();
            int n20 = n11 + n7;
            byte[] byArray16 = new byte[n20];
            sHAKEDigest.update(byArray15, 0, byArray15.length);
            sHAKEDigest.doFinal(byArray16, 0, n20);
            GF16.decode(byArray16, n11, byArray14, 0, byArray14.length);
            Utils.expandP1P2(this.params, lArray, byArray16);
            int n21 = 0;
            int n22 = n3 * n9;
            int n23 = 0;
            int n24 = 0;
            int n25 = 0;
            while (n23 < n2) {
                n19 = n23;
                n18 = n24;
                n17 = n25;
                while (n19 < n2) {
                    if (n19 == n23) {
                        n21 += n9;
                    } else {
                        n16 = 0;
                        int n26 = n10;
                        while (n16 < n3) {
                            GF16Utils.mVecMulAdd(n9, lArray, n21, byArray14[n18 + n16], lArray, n25 + n26);
                            GF16Utils.mVecMulAdd(n9, lArray, n21, byArray14[n24 + n16], lArray, n17 + n26);
                            ++n16;
                            n26 += n9;
                        }
                        n21 += n9;
                    }
                    ++n19;
                    n18 += n3;
                    n17 += n22;
                }
                ++n23;
                n24 += n3;
                n25 += n22;
            }
            Arrays.fill(byArray16, (byte)0);
            sHAKEDigest.update(byArray, 0, byArray.length);
            sHAKEDigest.doFinal(byArray12, 0, n12);
            this.random.nextBytes(byArray5);
            System.arraycopy(byArray5, 0, byArray12, n12, byArray5.length);
            System.arraycopy(byArray15, 0, byArray12, n12 + n8, n13);
            sHAKEDigest.update(byArray12, 0, n12 + n8 + n13);
            sHAKEDigest.doFinal(byArray5, 0, n8);
            System.arraycopy(byArray5, 0, byArray12, n12, n8);
            sHAKEDigest.update(byArray12, 0, n12 + n8);
            sHAKEDigest.doFinal(byArray2, 0, this.params.getMBytes());
            GF16.decode(byArray2, byArray3, n5);
            n23 = n2 * n * n9;
            long[] lArray4 = new long[n23];
            byte[] byArray17 = new byte[n2];
            for (n19 = 0; n19 <= 255; ++n19) {
                byArray12[byArray12.length - 1] = (byte)n19;
                sHAKEDigest.update(byArray12, 0, byArray12.length);
                sHAKEDigest.doFinal(byArray6, 0, byArray6.length);
                for (n18 = 0; n18 < n; ++n18) {
                    GF16.decode(byArray6, n18 * n6, byArray7, n18 * n2, n2);
                }
                GF16Utils.mulAddMatXMMat(n9, byArray7, lArray, n10, lArray2, n, n2, n3);
                GF16Utils.mulAddMUpperTriangularMatXMatTrans(n9, lArray, byArray7, lArray4, n2, n);
                GF16Utils.mulAddMatXMMat(n9, byArray7, lArray4, lArray3, n, n2);
                this.computeRHS(lArray3, byArray3, byArray4);
                this.computeA(lArray2, byArray8);
                GF16.decode(byArray6, n * n6, byArray10, 0, n14);
                if (this.sampleSolution(byArray8, byArray4, byArray10, byArray9)) break;
                Arrays.fill(lArray2, 0L);
                Arrays.fill(lArray3, 0L);
            }
            n19 = 0;
            n18 = 0;
            n17 = 0;
            n16 = 0;
            while (n19 < n) {
                GF16Utils.matMul(byArray14, byArray9, n18, byArray17, n3, n2);
                Bytes.xor(n2, byArray7, n16, byArray17, byArray11, n17);
                System.arraycopy(byArray9, n18, byArray11, n17 + n2, n3);
                ++n19;
                n18 += n3;
                n17 += n4;
                n16 += n2;
            }
            GF16.encode(byArray11, byArray13, n15);
            System.arraycopy(byArray5, 0, byArray13, byArray13.length - n8, n8);
            byte[] byArray18 = Arrays.concatenate(byArray13, byArray);
            return byArray18;
        }
        finally {
            Arrays.fill(byArray2, (byte)0);
            Arrays.fill(byArray3, (byte)0);
            Arrays.fill(byArray4, (byte)0);
            Arrays.fill(byArray5, (byte)0);
            Arrays.fill(byArray6, (byte)0);
            Arrays.fill(byArray7, (byte)0);
            Arrays.fill(byArray8, (byte)0);
            Arrays.fill(byArray9, (byte)0);
            Arrays.fill(byArray10, (byte)0);
            Arrays.fill(byArray11, (byte)0);
            Arrays.fill(byArray12, (byte)0);
        }
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        int n = this.params.getM();
        int n2 = this.params.getN();
        int n3 = this.params.getK();
        int n4 = n3 * n2;
        int n5 = this.params.getP1Limbs();
        int n6 = this.params.getP2Limbs();
        int n7 = this.params.getP3Limbs();
        int n8 = this.params.getMBytes();
        int n9 = this.params.getSigBytes();
        int n10 = this.params.getDigestBytes();
        int n11 = this.params.getSaltBytes();
        int n12 = this.params.getMVecLimbs();
        byte[] byArray3 = new byte[n8];
        byte[] byArray4 = new byte[n];
        byte[] byArray5 = new byte[n << 1];
        byte[] byArray6 = new byte[n4];
        long[] lArray = new long[n5 + n6 + n7];
        byte[] byArray7 = new byte[n10 + n11];
        byte[] byArray8 = this.pubKey.getEncoded();
        Utils.expandP1P2(this.params, lArray, byArray8);
        Utils.unpackMVecs(byArray8, this.params.getPkSeedBytes(), lArray, n5 + n6, n7 / n12, n);
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray, 0, byArray.length);
        sHAKEDigest.doFinal(byArray7, 0, n10);
        sHAKEDigest.update(byArray7, 0, n10);
        sHAKEDigest.update(byArray2, n9 - n11, n11);
        sHAKEDigest.doFinal(byArray3, 0, n8);
        GF16.decode(byArray3, byArray4, n);
        GF16.decode(byArray2, byArray6, n4);
        long[] lArray2 = new long[n3 * n3 * n12];
        long[] lArray3 = new long[n4 * n12];
        MayoSigner.mayoGenericMCalculatePS(this.params, lArray, n5, n5 + n6, byArray6, this.params.getV(), this.params.getO(), n3, lArray3);
        MayoSigner.mayoGenericMCalculateSPS(lArray3, byArray6, n12, n3, n2, lArray2);
        byte[] byArray9 = new byte[n];
        this.computeRHS(lArray2, byArray9, byArray5);
        return Arrays.constantTimeAreEqual(n, byArray5, 0, byArray4, 0);
    }

    void computeRHS(long[] lArray, byte[] byArray, byte[] byArray2) {
        int n;
        int n2;
        int n3;
        int n4 = this.params.getM();
        int n5 = this.params.getMVecLimbs();
        int n6 = this.params.getK();
        int[] nArray = this.params.getFTail();
        int n7 = (n4 - 1 & 0xF) << 2;
        if ((n4 & 0xF) != 0) {
            long l = (1L << ((n4 & 0xF) << 2)) - 1L;
            n3 = n6 * n6;
            n2 = 0;
            n = n5 - 1;
            while (n2 < n3) {
                int n8 = n;
                lArray[n8] = lArray[n8] & l;
                ++n2;
                n += n5;
            }
        }
        long[] lArray2 = new long[n5];
        byte[] byArray3 = new byte[n5 << 3];
        n3 = n6 * n5;
        n2 = n6 - 1;
        n = n2 * n5;
        int n9 = n * n6;
        while (n2 >= 0) {
            int n10 = n2;
            int n11 = n;
            int n12 = n9;
            while (n10 < n6) {
                int n13;
                int n14 = (int)(lArray2[n5 - 1] >>> n7 & 0xFL);
                int n15 = n5 - 1;
                lArray2[n15] = lArray2[n15] << 4;
                int n16 = n5 - 2;
                while (n16 >= 0) {
                    int n17 = n16 + 1;
                    lArray2[n17] = lArray2[n17] ^ lArray2[n16] >>> 60;
                    int n18 = n16--;
                    lArray2[n18] = lArray2[n18] << 4;
                }
                Pack.longToLittleEndian(lArray2, byArray3, 0);
                for (n16 = 0; n16 < 4; ++n16) {
                    n13 = nArray[n16];
                    if (n13 == 0) continue;
                    long l = GF16.mul(n14, n13);
                    if ((n16 & 1) == 0) {
                        int n19 = n16 >> 1;
                        byArray3[n19] = (byte)(byArray3[n19] ^ (byte)(l & 0xFL));
                        continue;
                    }
                    int n20 = n16 >> 1;
                    byArray3[n20] = (byte)(byArray3[n20] ^ (byte)((l & 0xFL) << 4));
                }
                Pack.littleEndianToLong(byArray3, 0, lArray2);
                n16 = n9 + n11;
                n13 = n12 + n;
                boolean bl = n2 == n10;
                int n21 = 0;
                while (n21 < n5) {
                    long l = lArray[n16 + n21];
                    if (!bl) {
                        l ^= lArray[n13 + n21];
                    }
                    int n22 = n21++;
                    lArray2[n22] = lArray2[n22] ^ l;
                }
                ++n10;
                n11 += n5;
                n12 += n3;
            }
            --n2;
            n -= n5;
            n9 -= n3;
        }
        Pack.longToLittleEndian(lArray2, byArray3, 0);
        for (n2 = 0; n2 < n4; n2 += 2) {
            n = n2 >> 1;
            byArray2[n2] = (byte)(byArray[n2] ^ byArray3[n] & 0xF);
            byArray2[n2 + 1] = (byte)(byArray[n2 + 1] ^ byArray3[n] >>> 4 & 0xF);
        }
    }

    void computeA(long[] lArray, byte[] byArray) {
        int n;
        long l;
        int n2;
        int n3;
        int n4 = this.params.getK();
        int n5 = this.params.getO();
        int n6 = this.params.getM();
        int n7 = this.params.getMVecLimbs();
        int n8 = this.params.getACols();
        int[] nArray = this.params.getFTail();
        int n9 = 0;
        int n10 = 0;
        int n11 = n6 + 7 >>> 3;
        int n12 = n5 * n4;
        int n13 = n5 * n7;
        int n14 = n12 + 15 >> 4 << 4;
        long[] lArray2 = new long[n14 * n11 << 4];
        if ((n6 & 0xF) != 0) {
            long l2 = 1L << ((n6 & 0xF) << 2);
            --l2;
            n3 = 0;
            n2 = n7 - 1;
            while (n3 < n12) {
                int n15 = n2;
                lArray[n15] = lArray[n15] & l2;
                ++n3;
                n2 += n7;
            }
        }
        int n16 = 0;
        int n17 = 0;
        n3 = 0;
        while (n16 < n4) {
            n2 = n4 - 1;
            int n18 = n2 * n13;
            int n19 = n2 * n5;
            while (n2 >= n16) {
                int n20;
                int n21;
                int n22 = 0;
                int n23 = 0;
                while (n22 < n5) {
                    n21 = 0;
                    n20 = 0;
                    while (n21 < n7) {
                        l = lArray[n18 + n21 + n23];
                        int n24 = n = n17 + n22 + n10 + n20;
                        lArray2[n24] = lArray2[n24] ^ l << n9;
                        if (n9 > 0) {
                            int n25 = n + n14;
                            lArray2[n25] = lArray2[n25] ^ l >>> 64 - n9;
                        }
                        ++n21;
                        n20 += n14;
                    }
                    ++n22;
                    n23 += n7;
                }
                if (n16 != n2) {
                    n22 = 0;
                    n23 = 0;
                    while (n22 < n5) {
                        n21 = 0;
                        n20 = 0;
                        while (n21 < n7) {
                            l = lArray[n3 + n21 + n23];
                            int n26 = n = n19 + n22 + n10 + n20;
                            lArray2[n26] = lArray2[n26] ^ l << n9;
                            if (n9 > 0) {
                                int n27 = n + n14;
                                lArray2[n27] = lArray2[n27] ^ l >>> 64 - n9;
                            }
                            ++n21;
                            n20 += n14;
                        }
                        ++n22;
                        n23 += n7;
                    }
                }
                if ((n9 += 4) == 64) {
                    n10 += n14;
                    n9 = 0;
                }
                --n2;
                n18 -= n13;
                n19 -= n5;
            }
            ++n16;
            n17 += n5;
            n3 += n13;
        }
        for (n16 = 0; n16 < n14 * (n6 + ((n4 + 1) * n4 >> 1) + 15 >>> 4); n16 += 16) {
            MayoSigner.transpose16x16Nibbles(lArray2, n16);
        }
        byte[] byArray2 = new byte[16];
        n3 = 0;
        for (n17 = 0; n17 < 4; ++n17) {
            n2 = nArray[n17];
            byArray2[n3++] = (byte)GF16.mul(n2, 1);
            byArray2[n3++] = (byte)GF16.mul(n2, 2);
            byArray2[n3++] = (byte)GF16.mul(n2, 4);
            byArray2[n3++] = (byte)GF16.mul(n2, 8);
        }
        for (n17 = 0; n17 < n14; n17 += 16) {
            for (n3 = n6; n3 < n6 + ((n4 + 1) * n4 >>> 1); ++n3) {
                n2 = (n3 >>> 4) * n14 + n17 + (n3 & 0xF);
                long l3 = lArray2[n2] & 0x1111111111111111L;
                long l4 = lArray2[n2] >>> 1 & 0x1111111111111111L;
                long l5 = lArray2[n2] >>> 2 & 0x1111111111111111L;
                l = lArray2[n2] >>> 3 & 0x1111111111111111L;
                n = 0;
                int n28 = 0;
                while (n < 4) {
                    int n29;
                    int n30 = n3 + n - n6;
                    int n31 = n29 = (n30 >> 4) * n14 + n17 + (n30 & 0xF);
                    lArray2[n31] = lArray2[n31] ^ (l3 * (long)byArray2[n28] ^ l4 * (long)byArray2[n28 + 1] ^ l5 * (long)byArray2[n28 + 2] ^ l * (long)byArray2[n28 + 3]);
                    ++n;
                    n28 += 4;
                }
            }
        }
        byte[] byArray3 = Pack.longToLittleEndian(lArray2);
        for (n3 = 0; n3 < n6; n3 += 16) {
            for (n2 = 0; n2 < n8 - 1; n2 += 16) {
                int n32 = 0;
                while (n32 + n3 < n6) {
                    GF16.decode(byArray3, (n3 * n14 >> 4) + n2 + n32 << 3, byArray, (n3 + n32) * n8 + n2, Math.min(16, n8 - 1 - n2));
                    ++n32;
                }
            }
        }
    }

    private static void transpose16x16Nibbles(long[] lArray, int n) {
        long l;
        int n2;
        int n3;
        for (n3 = 0; n3 < 16; n3 += 2) {
            n2 = n + n3;
            int n4 = n2 + 1;
            long l2 = (lArray[n2] >>> 4 ^ lArray[n4]) & 0xF0F0F0F0F0F0F0FL;
            int n5 = n2;
            lArray[n5] = lArray[n5] ^ l2 << 4;
            int n6 = n4;
            lArray[n6] = lArray[n6] ^ l2;
        }
        n2 = n;
        for (n3 = 0; n3 < 16; n3 += 4) {
            long l3 = (lArray[n2] >>> 8 ^ lArray[n2 + 2]) & 0xFF00FF00FF00FFL;
            l = (lArray[n2 + 1] >>> 8 ^ lArray[n2 + 3]) & 0xFF00FF00FF00FFL;
            int n7 = n2++;
            lArray[n7] = lArray[n7] ^ l3 << 8;
            int n8 = n2++;
            lArray[n8] = lArray[n8] ^ l << 8;
            int n9 = n2++;
            lArray[n9] = lArray[n9] ^ l3;
            int n10 = n2++;
            lArray[n10] = lArray[n10] ^ l;
        }
        for (n3 = 0; n3 < 4; ++n3) {
            n2 = n + n3;
            long l4 = (lArray[n2] >>> 16 ^ lArray[n2 + 4]) & 0xFFFF0000FFFFL;
            l = (lArray[n2 + 8] >>> 16 ^ lArray[n2 + 12]) & 0xFFFF0000FFFFL;
            int n11 = n2;
            lArray[n11] = lArray[n11] ^ l4 << 16;
            int n12 = n2 + 8;
            lArray[n12] = lArray[n12] ^ l << 16;
            int n13 = n2 + 4;
            lArray[n13] = lArray[n13] ^ l4;
            int n14 = n2 + 12;
            lArray[n14] = lArray[n14] ^ l;
        }
        for (n3 = 0; n3 < 8; ++n3) {
            n2 = n + n3;
            long l5 = (lArray[n2] >>> 32 ^ lArray[n2 + 8]) & 0xFFFFFFFFL;
            int n15 = n2;
            lArray[n15] = lArray[n15] ^ l5 << 32;
            int n16 = n2 + 8;
            lArray[n16] = lArray[n16] ^ l5;
        }
    }

    boolean sampleSolution(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        int n = this.params.getK();
        int n2 = this.params.getO();
        int n3 = this.params.getM();
        int n4 = this.params.getACols();
        int n5 = n * n2;
        System.arraycopy(byArray3, 0, byArray4, 0, n5);
        byte[] byArray5 = new byte[n3];
        GF16Utils.matMul(byArray, byArray3, 0, byArray5, n5 + 1, n3);
        int n6 = 0;
        int n7 = n5;
        while (n6 < n3) {
            byArray[n7] = (byte)(byArray2[n6] ^ byArray5[n6]);
            ++n6;
            n7 += n5 + 1;
        }
        this.ef(byArray, n3, n4);
        n6 = 0;
        n7 = 0;
        int n8 = (n3 - 1) * n4;
        while (n7 < n4 - 1) {
            n6 |= byArray[n8] != 0 ? 1 : 0;
            ++n7;
            ++n8;
        }
        if (n6 == 0) {
            return false;
        }
        n7 = n3 - 1;
        n8 = n7 * n4;
        while (n7 >= 0) {
            int n9 = 0;
            int n10 = Math.min(n7 + 32 / (n3 - n7), n5);
            for (int i = n7; i <= n10; ++i) {
                byte by = (byte)(-(byArray[n8 + i] & 0xFF) >> 31);
                byte by2 = (byte)(by & ~n9 & byArray[n8 + n4 - 1]);
                int n11 = i;
                byArray4[n11] = (byte)(byArray4[n11] ^ by2);
                int n12 = 0;
                int n13 = i;
                int n14 = n4 - 1;
                while (n12 < n7) {
                    long l = 0L;
                    int n15 = 0;
                    int n16 = 0;
                    while (n15 < 8) {
                        l ^= (long)(byArray[n13 + n16] & 0xFF) << (n15 << 3);
                        ++n15;
                        n16 += n4;
                    }
                    l = GF16Utils.mulFx8(by2, l);
                    n15 = 0;
                    n16 = 0;
                    while (n15 < 8) {
                        int n17 = n14 + n16;
                        byArray[n17] = (byte)(byArray[n17] ^ (byte)(l >> (n15 << 3) & 0xFL));
                        ++n15;
                        n16 += n4;
                    }
                    n12 += 8;
                    n13 += n4 << 3;
                    n14 += n4 << 3;
                }
                n9 = (byte)(n9 | by);
            }
            --n7;
            n8 -= n4;
        }
        return true;
    }

    void ef(byte[] byArray, int n, int n2) {
        int n3;
        int n4;
        int n5 = n2 + 15 >> 4;
        long[] lArray = new long[n5];
        long[] lArray2 = new long[n5];
        long[] lArray3 = new long[n * n5];
        int n6 = this.params.getO() * this.params.getK() + 16;
        byte[] byArray2 = new byte[n6 >> 1];
        int n7 = n6 >> 4;
        int n8 = 0;
        int n9 = 0;
        int n10 = 0;
        while (n8 < n) {
            for (n4 = 0; n4 < n5; ++n4) {
                long l = 0L;
                for (int i = 0; i < 16; ++i) {
                    n3 = (n4 << 4) + i;
                    if (n3 >= n2) continue;
                    l |= ((long)byArray[n9 + n3] & 0xFL) << (i << 2);
                }
                lArray3[n4 + n10] = l;
            }
            ++n8;
            n9 += n2;
            n10 += n5;
        }
        n8 = 0;
        for (n9 = 0; n9 < n2; ++n9) {
            int n11;
            long l;
            long l2;
            n10 = Math.max(0, n9 + n - n2);
            n4 = Math.min(n - 1, n9);
            Arrays.clear(lArray);
            Arrays.clear(lArray2);
            int n12 = 0;
            long l3 = -1L;
            n3 = Math.min(n - 1, n4 + 32);
            int n13 = n10;
            int n14 = n10 * n5;
            while (n13 <= n3) {
                l2 = MayoSigner.ctCompare64(n13, n8) ^ 0xFFFFFFFFFFFFFFFFL;
                l = (long)n8 - (long)n13 >> 63;
                for (n11 = 0; n11 < n5; ++n11) {
                    int n15 = n11;
                    lArray[n15] = lArray[n15] ^ (l2 | l & l3) & lArray3[n14 + n11];
                }
                n12 = (int)(lArray[n9 >>> 4] >>> ((n9 & 0xF) << 2) & 0xFL);
                l3 = -((long)n12) >> 63 ^ 0xFFFFFFFFFFFFFFFFL;
                ++n13;
                n14 += n5;
            }
            MayoSigner.vecMulAddU64(n5, lArray, GF16.inv((byte)n12), lArray2);
            n13 = n10;
            n14 = n10 * n5;
            while (n13 <= n4) {
                l2 = (MayoSigner.ctCompare64(n13, n8) ^ 0xFFFFFFFFFFFFFFFFL) & (l3 ^ 0xFFFFFFFFFFFFFFFFL);
                l = l2 ^ 0xFFFFFFFFFFFFFFFFL;
                n11 = 0;
                int n16 = n14;
                while (n11 < n5) {
                    lArray3[n16] = l & lArray3[n16] | l2 & lArray2[n11];
                    ++n11;
                    ++n16;
                }
                ++n13;
                n14 += n5;
            }
            n13 = n10;
            n14 = n10 * n5;
            while (n13 < n) {
                int n17 = n13 > n8 ? -1 : 0;
                int n18 = (int)(lArray3[n14 + (n9 >>> 4)] >>> ((n9 & 0xF) << 2) & 0xFL);
                MayoSigner.vecMulAddU64(n5, lArray2, (byte)(n17 & n18), lArray3, n14);
                ++n13;
                n14 += n5;
            }
            if (n12 == 0) continue;
            ++n8;
        }
        n9 = 0;
        n10 = 0;
        n4 = 0;
        while (n10 < n) {
            Pack.longToLittleEndian(lArray3, n4, n7, byArray2, 0);
            GF16.decode(byArray2, 0, byArray, n9, n2);
            n9 += n2;
            ++n10;
            n4 += n5;
        }
    }

    private static long ctCompare64(int n, int n2) {
        return -((long)(n ^ n2)) >> 63;
    }

    private static void vecMulAddU64(int n, long[] lArray, byte by, long[] lArray2) {
        int n2 = MayoSigner.mulTable(by & 0xFF);
        int n3 = 0;
        while (n3 < n) {
            long l = (lArray[n3] & 0x1111111111111111L) * (long)(n2 & 0xFF) ^ (lArray[n3] >>> 1 & 0x1111111111111111L) * (long)(n2 >>> 8 & 0xF) ^ (lArray[n3] >>> 2 & 0x1111111111111111L) * (long)(n2 >>> 16 & 0xF) ^ (lArray[n3] >>> 3 & 0x1111111111111111L) * (long)(n2 >>> 24 & 0xF);
            int n4 = n3++;
            lArray2[n4] = lArray2[n4] ^ l;
        }
    }

    private static void vecMulAddU64(int n, long[] lArray, byte by, long[] lArray2, int n2) {
        int n3 = MayoSigner.mulTable(by & 0xFF);
        for (int i = 0; i < n; ++i) {
            long l = (lArray[i] & 0x1111111111111111L) * (long)(n3 & 0xFF) ^ (lArray[i] >>> 1 & 0x1111111111111111L) * (long)(n3 >>> 8 & 0xF) ^ (lArray[i] >>> 2 & 0x1111111111111111L) * (long)(n3 >>> 16 & 0xF) ^ (lArray[i] >>> 3 & 0x1111111111111111L) * (long)(n3 >>> 24 & 0xF);
            int n4 = n2 + i;
            lArray2[n4] = lArray2[n4] ^ l;
        }
    }

    private static int mulTable(int n) {
        int n2 = n * 134480385;
        int n3 = n2 & 0xF0F0F0F0;
        return n2 ^ n3 >>> 4 ^ n3 >>> 3;
    }

    private static void mayoGenericMCalculatePS(MayoParameters mayoParameters, long[] lArray, int n, int n2, byte[] byArray, int n3, int n4, int n5, long[] lArray2) {
        int n6;
        int n7;
        int n8 = n4 + n3;
        int n9 = mayoParameters.getMVecLimbs();
        long[] lArray3 = new long[n9 * mayoParameters.getK() * mayoParameters.getN() * n9 << 4];
        int n10 = n4 * n9;
        int n11 = 0;
        int n12 = 0;
        int n13 = 0;
        int n14 = 0;
        while (n12 < n3) {
            int n15;
            for (n7 = n12; n7 < n3; ++n7) {
                n6 = 0;
                n15 = 0;
                while (n6 < n5) {
                    Longs.xorTo(n9, lArray, n11, lArray3, ((n13 + n6 << 4) + (byArray[n15 + n7] & 0xFF)) * n9);
                    ++n6;
                    n15 += n8;
                }
                n11 += n9;
            }
            n7 = 0;
            n6 = n14;
            while (n7 < n4) {
                n15 = 0;
                int n16 = 0;
                while (n15 < n5) {
                    Longs.xorTo(n9, lArray, n + n6, lArray3, ((n13 + n15 << 4) + (byArray[n16 + n7 + n3] & 0xFF)) * n9);
                    ++n15;
                    n16 += n8;
                }
                ++n7;
                n6 += n9;
            }
            ++n12;
            n13 += n5;
            n14 += n10;
        }
        n11 = 0;
        n12 = n3;
        n13 = n3 * n5;
        while (n12 < n8) {
            for (n14 = n12; n14 < n8; ++n14) {
                n7 = 0;
                n6 = 0;
                while (n7 < n5) {
                    Longs.xorTo(n9, lArray, n2 + n11, lArray3, ((n13 + n7 << 4) + (byArray[n6 + n14] & 0xFF)) * n9);
                    ++n7;
                    n6 += n8;
                }
                n11 += n9;
            }
            ++n12;
            n13 += n5;
        }
        MayoSigner.mVecMultiplyBins(n9, n8 * n5, lArray3, lArray2);
    }

    private static void mayoGenericMCalculateSPS(long[] lArray, byte[] byArray, int n, int n2, int n3, long[] lArray2) {
        int n4 = n2 * n2;
        int n5 = n * n4 << 4;
        long[] lArray3 = new long[n5];
        int n6 = n2 * n;
        int n7 = 0;
        int n8 = 0;
        int n9 = 0;
        while (n7 < n2) {
            int n10 = 0;
            int n11 = 0;
            while (n10 < n3) {
                int n12 = (byArray[n8 + n10] & 0xFF) * n + n9;
                int n13 = 0;
                int n14 = 0;
                while (n13 < n2) {
                    Longs.xorTo(n, lArray, n11 + n14, lArray3, n12 + (n14 << 4));
                    ++n13;
                    n14 += n;
                }
                ++n10;
                n11 += n6;
            }
            ++n7;
            n8 += n3;
            n9 += n6 << 4;
        }
        MayoSigner.mVecMultiplyBins(n, n4, lArray3, lArray2);
    }

    private static void mVecMultiplyBins(int n, int n2, long[] lArray, long[] lArray2) {
        int n3 = n + n;
        int n4 = n3 + n;
        int n5 = n4 + n;
        int n6 = n5 + n;
        int n7 = n6 + n;
        int n8 = n7 + n;
        int n9 = n8 + n;
        int n10 = n9 + n;
        int n11 = n10 + n;
        int n12 = n11 + n;
        int n13 = n12 + n;
        int n14 = n13 + n;
        int n15 = n14 + n;
        int n16 = n15 + n;
        int n17 = 0;
        int n18 = 0;
        while (n17 < n2) {
            int n19 = 0;
            int n20 = n18;
            while (n19 < n) {
                long l = lArray[n20 + n6];
                long l2 = l & 0x1111111111111111L;
                l = lArray[n20 + n11] ^ (l & 0xEEEEEEEEEEEEEEEEL) >>> 1 ^ (l2 << 3) + l2;
                long l3 = lArray[n20 + n12];
                l2 = (l3 & 0x8888888888888888L) >>> 3;
                l3 = lArray[n20 + n13] ^ (l3 & 0x7777777777777777L) << 1 ^ (l2 << 1) + l2;
                l2 = l & 0x1111111111111111L;
                l = lArray[n20 + n8] ^ (l & 0xEEEEEEEEEEEEEEEEL) >>> 1 ^ (l2 << 3) + l2;
                l2 = (l3 & 0x8888888888888888L) >>> 3;
                l3 = lArray[n20 + n7] ^ (l3 & 0x7777777777777777L) << 1 ^ (l2 << 1) + l2;
                l2 = l & 0x1111111111111111L;
                l = lArray[n20 + n15] ^ (l & 0xEEEEEEEEEEEEEEEEL) >>> 1 ^ (l2 << 3) + l2;
                l2 = (l3 & 0x8888888888888888L) >>> 3;
                l3 = lArray[n20 + n4] ^ (l3 & 0x7777777777777777L) << 1 ^ (l2 << 1) + l2;
                l2 = l & 0x1111111111111111L;
                l = lArray[n20 + n16] ^ (l & 0xEEEEEEEEEEEEEEEEL) >>> 1 ^ (l2 << 3) + l2;
                l2 = (l3 & 0x8888888888888888L) >>> 3;
                l3 = lArray[n20 + n9] ^ (l3 & 0x7777777777777777L) << 1 ^ (l2 << 1) + l2;
                l2 = l & 0x1111111111111111L;
                l = lArray[n20 + n14] ^ (l & 0xEEEEEEEEEEEEEEEEL) >>> 1 ^ (l2 << 3) + l2;
                l2 = (l3 & 0x8888888888888888L) >>> 3;
                l3 = lArray[n20 + n5] ^ (l3 & 0x7777777777777777L) << 1 ^ (l2 << 1) + l2;
                l2 = l & 0x1111111111111111L;
                l = lArray[n20 + n10] ^ (l & 0xEEEEEEEEEEEEEEEEL) >>> 1 ^ (l2 << 3) + l2;
                l2 = (l3 & 0x8888888888888888L) >>> 3;
                l3 = lArray[n20 + n3] ^ (l3 & 0x7777777777777777L) << 1 ^ (l2 << 1) + l2;
                l2 = l & 0x1111111111111111L;
                l = lArray[n20 + n] ^ (l & 0xEEEEEEEEEEEEEEEEL) >>> 1 ^ (l2 << 3) + l2;
                l2 = (l3 & 0x8888888888888888L) >>> 3;
                lArray2[(n18 >> 4) + n19] = l ^ (l3 & 0x7777777777777777L) << 1 ^ (l2 << 1) + l2;
                ++n19;
                ++n20;
            }
            ++n17;
            n18 += n << 4;
        }
    }
}

