/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.snova.GF16Utils;
import org.bouncycastle.pqc.crypto.snova.MapGroup1;
import org.bouncycastle.pqc.crypto.snova.SnovaEngine;
import org.bouncycastle.pqc.crypto.snova.SnovaKeyElements;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.snova.SnovaPublicKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.GF16;

public class SnovaSigner
implements MessageSigner {
    private SnovaParameters params;
    private SnovaEngine engine;
    private SecureRandom random;
    private final SHAKEDigest shake = new SHAKEDigest(256);
    private SnovaPublicKeyParameters pubKey;
    private SnovaPrivateKeyParameters privKey;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (bl) {
            this.pubKey = null;
            if (cipherParameters instanceof ParametersWithRandom) {
                ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
                this.privKey = (SnovaPrivateKeyParameters)parametersWithRandom.getParameters();
                this.random = parametersWithRandom.getRandom();
            } else {
                this.privKey = (SnovaPrivateKeyParameters)cipherParameters;
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.params = this.privKey.getParameters();
        } else {
            this.pubKey = (SnovaPublicKeyParameters)cipherParameters;
            this.params = this.pubKey.getParameters();
            this.privKey = null;
            this.random = null;
        }
        this.engine = new SnovaEngine(this.params);
    }

    @Override
    public byte[] generateSignature(byte[] byArray) {
        byte[] byArray2;
        byte[] byArray3;
        byte[] byArray4 = this.getMessageHash(byArray);
        byte[] byArray5 = new byte[this.params.getSaltLength()];
        this.random.nextBytes(byArray5);
        byte[] byArray6 = new byte[(this.params.getN() * this.params.getLsq() + 1 >>> 1) + this.params.getSaltLength()];
        SnovaKeyElements snovaKeyElements = new SnovaKeyElements(this.params);
        if (this.params.isSkIsSeed()) {
            byte[] byArray7 = this.privKey.getPrivateKey();
            byArray3 = Arrays.copyOfRange(byArray7, 0, 16);
            byArray2 = Arrays.copyOfRange(byArray7, 16, byArray7.length);
            this.engine.genMap1T12Map2(snovaKeyElements, byArray3, byArray2);
        } else {
            byte[] byArray8 = this.privKey.getPrivateKey();
            byte[] byArray9 = new byte[byArray8.length - 16 - 32 << 1];
            GF16Utils.decodeMergeInHalf(byArray8, byArray9, byArray9.length);
            int n = 0;
            n = SnovaKeyElements.copy3d(byArray9, n, snovaKeyElements.map1.aAlpha);
            n = SnovaKeyElements.copy3d(byArray9, n, snovaKeyElements.map1.bAlpha);
            n = SnovaKeyElements.copy3d(byArray9, n, snovaKeyElements.map1.qAlpha1);
            n = SnovaKeyElements.copy3d(byArray9, n, snovaKeyElements.map1.qAlpha2);
            n = SnovaKeyElements.copy3d(byArray9, n, snovaKeyElements.T12);
            n = SnovaKeyElements.copy4d(byArray9, n, snovaKeyElements.map2.f11);
            n = SnovaKeyElements.copy4d(byArray9, n, snovaKeyElements.map2.f12);
            SnovaKeyElements.copy4d(byArray9, n, snovaKeyElements.map2.f21);
            byArray3 = Arrays.copyOfRange(byArray8, byArray8.length - 16 - 32, byArray8.length - 32);
            byArray2 = Arrays.copyOfRange(byArray8, byArray8.length - 32, byArray8.length);
        }
        this.signDigestCore(byArray6, byArray4, byArray5, snovaKeyElements.map1.aAlpha, snovaKeyElements.map1.bAlpha, snovaKeyElements.map1.qAlpha1, snovaKeyElements.map1.qAlpha2, snovaKeyElements.T12, snovaKeyElements.map2.f11, snovaKeyElements.map2.f12, snovaKeyElements.map2.f21, byArray3, byArray2);
        return Arrays.concatenate(byArray6, byArray);
    }

    @Override
    public boolean verifySignature(byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = this.getMessageHash(byArray);
        MapGroup1 mapGroup1 = new MapGroup1(this.params);
        byte[] byArray4 = this.pubKey.getEncoded();
        byte[] byArray5 = Arrays.copyOf(byArray4, 16);
        byte[] byArray6 = Arrays.copyOfRange(byArray4, 16, byArray4.length);
        this.engine.genABQP(mapGroup1, byArray5);
        byte[][][][] byArray7 = new byte[this.params.getM()][this.params.getO()][this.params.getO()][this.params.getLsq()];
        if ((this.params.getLsq() & 1) == 0) {
            MapGroup1.decodeP(byArray6, 0, byArray7, byArray6.length << 1);
        } else {
            byte[] byArray8 = new byte[byArray6.length << 1];
            GF16.decode(byArray6, byArray8, byArray8.length);
            MapGroup1.fillP(byArray8, 0, byArray7, byArray8.length);
        }
        return this.verifySignatureCore(byArray3, byArray2, byArray5, mapGroup1, byArray7);
    }

    void createSignedHash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, int n3, int n4, byte[] byArray4, int n5) {
        this.shake.update(byArray, 0, n);
        this.shake.update(byArray2, 0, n2);
        this.shake.update(byArray3, n3, n4);
        this.shake.doFinal(byArray4, 0, n5);
    }

    void signDigestCore(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[][][] byArray4, byte[][][] byArray5, byte[][][] byArray6, byte[][][] byArray7, byte[][][] byArray8, byte[][][][] byArray9, byte[][][][] byArray10, byte[][][][] byArray11, byte[] byArray12, byte[] byArray13) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6 = this.params.getM();
        int n7 = this.params.getL();
        int n8 = this.params.getLsq();
        int n9 = this.params.getAlpha();
        int n10 = this.params.getV();
        int n11 = this.params.getO();
        int n12 = this.params.getN();
        int n13 = n6 * n8;
        int n14 = n11 * n8;
        int n15 = n10 * n8;
        int n16 = n14 + 1 >>> 1;
        byte[][] byArray14 = new byte[n13][n13 + 1];
        byte[][] byArray15 = new byte[n8][n8];
        byte[] byArray16 = new byte[n13];
        byte[][][] byArray17 = new byte[n9][n10][n8];
        byte[][][] byArray18 = new byte[n9][n10][n8];
        byte[] byArray19 = new byte[n8];
        byte[] byArray20 = new byte[n8];
        byte[] byArray21 = new byte[n8];
        byte[] byArray22 = new byte[n13];
        byte[] byArray23 = new byte[n12 * n8];
        byte[] byArray24 = new byte[n16];
        byte[] byArray25 = new byte[n15 + 1 >>> 1];
        byte[] byArray26 = new byte[n7];
        byte by = 0;
        this.createSignedHash(byArray12, byArray12.length, byArray2, byArray2.length, byArray3, 0, byArray3.length, byArray24, n16);
        GF16.decode(byArray24, 0, byArray22, 0, byArray22.length);
        do {
            for (n4 = 0; n4 < byArray14.length; ++n4) {
                Arrays.fill(byArray14[n4], (byte)0);
            }
            by = (byte)(by + 1);
            for (n4 = 0; n4 < n13; ++n4) {
                byArray14[n4][n13] = byArray22[n4];
            }
            this.shake.update(byArray13, 0, byArray13.length);
            this.shake.update(byArray2, 0, byArray2.length);
            this.shake.update(byArray3, 0, byArray3.length);
            this.shake.update(by);
            this.shake.doFinal(byArray25, 0, byArray25.length);
            GF16.decode(byArray25, byArray23, byArray25.length << 1);
            n4 = 0;
            n3 = 0;
            while (n4 < n6) {
                int n17;
                int n18;
                Arrays.fill(byArray21, (byte)0);
                n2 = 0;
                n = n4;
                while (n2 < n9) {
                    if (n >= n11) {
                        n -= n11;
                    }
                    n18 = 0;
                    n17 = 0;
                    while (n18 < n10) {
                        GF16Utils.gf16mTranMulMul(byArray23, n17, byArray4[n4][n2], byArray5[n4][n2], byArray6[n4][n2], byArray7[n4][n2], byArray26, byArray17[n2][n18], byArray18[n2][n18], n7);
                        ++n18;
                        n17 += n8;
                    }
                    for (n18 = 0; n18 < n10; ++n18) {
                        for (n17 = 0; n17 < n10; ++n17) {
                            GF16Utils.gf16mMulMulTo(byArray17[n2][n18], byArray9[n][n18][n17], byArray18[n2][n17], byArray26, byArray21, n7);
                        }
                    }
                    ++n2;
                    ++n;
                }
                n = 0;
                for (n2 = 0; n2 < n7; ++n2) {
                    for (n18 = 0; n18 < n7; ++n18) {
                        byte[] byArray27 = byArray14[n3 + n];
                        int n19 = n13;
                        byArray27[n19] = (byte)(byArray27[n19] ^ byArray21[n++]);
                    }
                }
                n2 = 0;
                n = 0;
                while (n2 < n11) {
                    n18 = 0;
                    n17 = n4;
                    while (n18 < n9) {
                        int n20;
                        int n21;
                        if (n17 >= n11) {
                            n17 -= n11;
                        }
                        for (n21 = 0; n21 < n8; ++n21) {
                            Arrays.fill(byArray15[n21], (byte)0);
                        }
                        for (n21 = 0; n21 < n10; ++n21) {
                            GF16Utils.gf16mMulMul(byArray17[n18][n21], byArray10[n17][n21][n2], byArray7[n4][n18], byArray26, byArray19, n7);
                            GF16Utils.gf16mMulMul(byArray6[n4][n18], byArray11[n17][n2][n21], byArray18[n18][n21], byArray26, byArray20, n7);
                            n20 = 0;
                            int n22 = 0;
                            int n23 = 0;
                            while (n20 < n8) {
                                if (n22 == n7) {
                                    n22 = 0;
                                    n23 += n7;
                                }
                                byte by2 = byArray19[n23];
                                byte by3 = byArray20[n22];
                                int n24 = 0;
                                int n25 = 0;
                                int n26 = 0;
                                int n27 = 0;
                                int n28 = 0;
                                while (n24 < n8) {
                                    if (n25 == n7) {
                                        n25 = 0;
                                        n28 = 0;
                                        by2 = byArray19[n23 + ++n26];
                                        by3 = byArray20[(n27 += n7) + n22];
                                    }
                                    byte by4 = byArray5[n4][n18][n28 + n22];
                                    byte by5 = byArray4[n4][n18][n23 + n25];
                                    byte[] byArray28 = byArray15[n20];
                                    int n29 = n24++;
                                    byArray28[n29] = (byte)(byArray28[n29] ^ (GF16.mul(by2, by4) ^ GF16.mul(by5, by3)));
                                    ++n25;
                                    n28 += n7;
                                }
                                ++n20;
                                ++n22;
                            }
                        }
                        for (n21 = 0; n21 < n8; ++n21) {
                            for (n20 = 0; n20 < n8; ++n20) {
                                byte[] byArray29 = byArray14[n3 + n21];
                                int n30 = n + n20;
                                byArray29[n30] = (byte)(byArray29[n30] ^ byArray15[n21][n20]);
                            }
                        }
                        ++n18;
                        ++n17;
                    }
                    ++n2;
                    n += n8;
                }
                ++n4;
                n3 += n8;
            }
        } while ((n5 = this.performGaussianElimination(byArray14, byArray16, n13)) != 0);
        n4 = 0;
        n3 = 0;
        while (n4 < n10) {
            n2 = 0;
            n = 0;
            while (n2 < n11) {
                GF16Utils.gf16mMulTo(byArray8[n4][n2], byArray16, n, byArray23, n3, n7);
                ++n2;
                n += n8;
            }
            ++n4;
            n3 += n8;
        }
        System.arraycopy(byArray16, 0, byArray23, n15, n14);
        GF16.encode(byArray23, byArray, byArray23.length);
        System.arraycopy(byArray3, 0, byArray, byArray.length - 16, 16);
    }

    boolean verifySignatureCore(byte[] byArray, byte[] byArray2, byte[] byArray3, MapGroup1 mapGroup1, byte[][][][] byArray4) {
        int n = this.params.getLsq();
        int n2 = this.params.getO();
        int n3 = n2 * n;
        int n4 = n3 + 1 >>> 1;
        int n5 = this.params.getSaltLength();
        int n6 = this.params.getM();
        int n7 = this.params.getN();
        int n8 = n7 * n;
        int n9 = n8 + 1 >>> 1;
        byte[] byArray5 = new byte[n4];
        this.createSignedHash(byArray3, byArray3.length, byArray, byArray.length, byArray2, n9, n5, byArray5, n4);
        if ((n3 & 1) != 0) {
            int n10 = n4 - 1;
            byArray5[n10] = (byte)(byArray5[n10] & 0xF);
        }
        byte[] byArray6 = new byte[n8];
        GF16.decode(byArray2, 0, byArray6, 0, byArray6.length);
        byte[] byArray7 = new byte[n6 * n];
        this.evaluation(byArray7, mapGroup1, byArray4, byArray6);
        byte[] byArray8 = new byte[n4];
        GF16.encode(byArray7, byArray8, byArray7.length);
        return Arrays.areEqual(byArray5, byArray8);
    }

    private void evaluation(byte[] byArray, MapGroup1 mapGroup1, byte[][][][] byArray2, byte[] byArray3) {
        int n = this.params.getM();
        int n2 = this.params.getAlpha();
        int n3 = this.params.getN();
        int n4 = this.params.getL();
        int n5 = this.params.getLsq();
        int n6 = this.params.getO();
        byte[][][] byArray4 = new byte[n2][n3][n5];
        byte[][][] byArray5 = new byte[n2][n3][n5];
        byte[] byArray6 = new byte[n5];
        int n7 = 0;
        int n8 = 0;
        while (n7 < n) {
            int n9;
            int n10 = 0;
            int n11 = 0;
            while (n10 < n3) {
                for (n9 = 0; n9 < n2; ++n9) {
                    GF16Utils.gf16mTranMulMul(byArray3, n11, mapGroup1.aAlpha[n7][n9], mapGroup1.bAlpha[n7][n9], mapGroup1.qAlpha1[n7][n9], mapGroup1.qAlpha2[n7][n9], byArray6, byArray4[n9][n10], byArray5[n9][n10], n4);
                }
                ++n10;
                n11 += n5;
            }
            n10 = 0;
            n11 = n7;
            while (n10 < n2) {
                if (n11 >= n6) {
                    n11 -= n6;
                }
                for (n9 = 0; n9 < n3; ++n9) {
                    byte[] byArray7 = this.getPMatrix(mapGroup1, byArray2, n11, n9, 0);
                    GF16Utils.gf16mMul(byArray7, byArray5[n10][0], byArray6, n4);
                    for (int i = 1; i < n3; ++i) {
                        byArray7 = this.getPMatrix(mapGroup1, byArray2, n11, n9, i);
                        GF16Utils.gf16mMulTo(byArray7, byArray5[n10][i], byArray6, n4);
                    }
                    GF16Utils.gf16mMulTo(byArray4[n10][n9], byArray6, byArray, n8, n4);
                }
                ++n10;
                ++n11;
            }
            ++n7;
            n8 += n5;
        }
    }

    private byte[] getPMatrix(MapGroup1 mapGroup1, byte[][][][] byArray, int n, int n2, int n3) {
        int n4 = this.params.getV();
        if (n2 < n4) {
            if (n3 < n4) {
                return mapGroup1.p11[n][n2][n3];
            }
            return mapGroup1.p12[n][n2][n3 - n4];
        }
        if (n3 < n4) {
            return mapGroup1.p21[n][n2 - n4][n3];
        }
        return byArray[n][n2 - n4][n3 - n4];
    }

    private int performGaussianElimination(byte[][] byArray, byte[] byArray2, int n) {
        byte by;
        int n2;
        int n3;
        int n4 = n + 1;
        for (n3 = 0; n3 < n; ++n3) {
            int n5;
            for (n2 = n3; n2 < n && byArray[n2][n3] == 0; ++n2) {
            }
            if (n2 >= n) {
                return 1;
            }
            if (n2 != n3) {
                byte[] byArray3 = byArray[n3];
                byArray[n3] = byArray[n2];
                byArray[n2] = byArray3;
            }
            by = GF16.inv(byArray[n3][n3]);
            for (n5 = n3; n5 < n4; ++n5) {
                byArray[n3][n5] = GF16.mul(byArray[n3][n5], by);
            }
            for (n5 = n3 + 1; n5 < n; ++n5) {
                byte by2 = byArray[n5][n3];
                if (by2 == 0) continue;
                for (int i = n3; i < n4; ++i) {
                    byte[] byArray4 = byArray[n5];
                    int n6 = i;
                    byArray4[n6] = (byte)(byArray4[n6] ^ GF16.mul(byArray[n3][i], by2));
                }
            }
        }
        for (n3 = n - 1; n3 >= 0; --n3) {
            n2 = byArray[n3][n];
            for (by = n3 + 1; by < n; ++by) {
                n2 = (byte)(n2 ^ GF16.mul(byArray[n3][by], byArray2[by]));
            }
            byArray2[n3] = n2;
        }
        return 0;
    }

    private byte[] getMessageHash(byte[] byArray) {
        byte[] byArray2 = new byte[this.shake.getDigestSize()];
        this.shake.update(byArray, 0, byArray.length);
        this.shake.doFinal(byArray2, 0);
        return byArray2;
    }
}

