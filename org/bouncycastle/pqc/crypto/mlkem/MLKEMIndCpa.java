/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;
import org.bouncycastle.pqc.crypto.mlkem.Poly;
import org.bouncycastle.pqc.crypto.mlkem.PolyVec;
import org.bouncycastle.pqc.crypto.mlkem.Symmetric;
import org.bouncycastle.util.Arrays;

class MLKEMIndCpa {
    private final MLKEMEngine engine;
    private final int kyberK;
    private final int indCpaPublicKeyBytes;
    private final int polyVecBytes;
    private final int indCpaBytes;
    private final int polyVecCompressedBytes;
    private final int polyCompressedBytes;
    private Symmetric symmetric;
    public final int KyberGenerateMatrixNBlocks;

    public MLKEMIndCpa(MLKEMEngine mLKEMEngine) {
        this.engine = mLKEMEngine;
        this.kyberK = mLKEMEngine.getKyberK();
        this.indCpaPublicKeyBytes = mLKEMEngine.getKyberPublicKeyBytes();
        this.polyVecBytes = mLKEMEngine.getKyberPolyVecBytes();
        this.indCpaBytes = mLKEMEngine.getKyberIndCpaBytes();
        this.polyVecCompressedBytes = mLKEMEngine.getKyberPolyVecCompressedBytes();
        this.polyCompressedBytes = mLKEMEngine.getKyberPolyCompressedBytes();
        this.symmetric = mLKEMEngine.getSymmetric();
        this.KyberGenerateMatrixNBlocks = (472 + this.symmetric.xofBlockBytes) / this.symmetric.xofBlockBytes;
    }

    byte[][] generateKeyPair(byte[] byArray) {
        int n;
        PolyVec polyVec = new PolyVec(this.engine);
        PolyVec polyVec2 = new PolyVec(this.engine);
        PolyVec polyVec3 = new PolyVec(this.engine);
        byte[] byArray2 = new byte[64];
        this.symmetric.hash_g(byArray2, Arrays.append(byArray, (byte)this.kyberK));
        byte[] byArray3 = new byte[32];
        byte[] byArray4 = new byte[32];
        System.arraycopy(byArray2, 0, byArray3, 0, 32);
        System.arraycopy(byArray2, 32, byArray4, 0, 32);
        byte by = 0;
        PolyVec[] polyVecArray = new PolyVec[this.kyberK];
        for (n = 0; n < this.kyberK; ++n) {
            polyVecArray[n] = new PolyVec(this.engine);
        }
        this.generateMatrix(polyVecArray, byArray3, false);
        for (n = 0; n < this.kyberK; ++n) {
            polyVec.getVectorIndex(n).getEta1Noise(byArray4, by);
            by = (byte)(by + 1);
        }
        for (n = 0; n < this.kyberK; ++n) {
            polyVec3.getVectorIndex(n).getEta1Noise(byArray4, by);
            by = (byte)(by + 1);
        }
        polyVec.polyVecNtt();
        polyVec3.polyVecNtt();
        for (n = 0; n < this.kyberK; ++n) {
            PolyVec.pointwiseAccountMontgomery(polyVec2.getVectorIndex(n), polyVecArray[n], polyVec, this.engine);
            polyVec2.getVectorIndex(n).convertToMont();
        }
        polyVec2.addPoly(polyVec3);
        polyVec2.reducePoly();
        return new byte[][]{this.packPublicKey(polyVec2, byArray3), this.packSecretKey(polyVec)};
    }

    public byte[] encrypt(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int n;
        byte by = 0;
        PolyVec polyVec = new PolyVec(this.engine);
        PolyVec polyVec2 = new PolyVec(this.engine);
        PolyVec polyVec3 = new PolyVec(this.engine);
        PolyVec polyVec4 = new PolyVec(this.engine);
        PolyVec[] polyVecArray = new PolyVec[this.engine.getKyberK()];
        Poly poly = new Poly(this.engine);
        Poly poly2 = new Poly(this.engine);
        Poly poly3 = new Poly(this.engine);
        byte[] byArray4 = this.unpackPublicKey(polyVec2, byArray);
        poly3.fromMsg(byArray2);
        for (n = 0; n < this.kyberK; ++n) {
            polyVecArray[n] = new PolyVec(this.engine);
        }
        this.generateMatrix(polyVecArray, byArray4, true);
        for (n = 0; n < this.kyberK; ++n) {
            polyVec.getVectorIndex(n).getEta1Noise(byArray3, by);
            by = (byte)(by + 1);
        }
        for (n = 0; n < this.kyberK; ++n) {
            polyVec3.getVectorIndex(n).getEta2Noise(byArray3, by);
            by = (byte)(by + 1);
        }
        poly.getEta2Noise(byArray3, by);
        polyVec.polyVecNtt();
        for (n = 0; n < this.kyberK; ++n) {
            PolyVec.pointwiseAccountMontgomery(polyVec4.getVectorIndex(n), polyVecArray[n], polyVec, this.engine);
        }
        PolyVec.pointwiseAccountMontgomery(poly2, polyVec2, polyVec, this.engine);
        polyVec4.polyVecInverseNttToMont();
        poly2.polyInverseNttToMont();
        polyVec4.addPoly(polyVec3);
        poly2.addCoeffs(poly);
        poly2.addCoeffs(poly3);
        polyVec4.reducePoly();
        poly2.reduce();
        byte[] byArray5 = this.packCipherText(polyVec4, poly2);
        return byArray5;
    }

    private byte[] packCipherText(PolyVec polyVec, Poly poly) {
        byte[] byArray = new byte[this.indCpaBytes];
        System.arraycopy(polyVec.compressPolyVec(), 0, byArray, 0, this.polyVecCompressedBytes);
        System.arraycopy(poly.compressPoly(), 0, byArray, this.polyVecCompressedBytes, this.polyCompressedBytes);
        return byArray;
    }

    private void unpackCipherText(PolyVec polyVec, Poly poly, byte[] byArray) {
        byte[] byArray2 = Arrays.copyOfRange(byArray, 0, this.engine.getKyberPolyVecCompressedBytes());
        polyVec.decompressPolyVec(byArray2);
        byte[] byArray3 = Arrays.copyOfRange(byArray, this.engine.getKyberPolyVecCompressedBytes(), byArray.length);
        poly.decompressPoly(byArray3);
    }

    public byte[] packPublicKey(PolyVec polyVec, byte[] byArray) {
        byte[] byArray2 = new byte[this.indCpaPublicKeyBytes];
        System.arraycopy(polyVec.toBytes(), 0, byArray2, 0, this.polyVecBytes);
        System.arraycopy(byArray, 0, byArray2, this.polyVecBytes, 32);
        return byArray2;
    }

    public byte[] unpackPublicKey(PolyVec polyVec, byte[] byArray) {
        byte[] byArray2 = new byte[32];
        polyVec.fromBytes(byArray);
        System.arraycopy(byArray, this.polyVecBytes, byArray2, 0, 32);
        return byArray2;
    }

    public byte[] packSecretKey(PolyVec polyVec) {
        return polyVec.toBytes();
    }

    public void unpackSecretKey(PolyVec polyVec, byte[] byArray) {
        polyVec.fromBytes(byArray);
    }

    public void generateMatrix(PolyVec[] polyVecArray, byte[] byArray, boolean bl) {
        byte[] byArray2 = new byte[this.KyberGenerateMatrixNBlocks * this.symmetric.xofBlockBytes + 2];
        for (int i = 0; i < this.kyberK; ++i) {
            for (int j = 0; j < this.kyberK; ++j) {
                if (bl) {
                    this.symmetric.xofAbsorb(byArray, (byte)i, (byte)j);
                } else {
                    this.symmetric.xofAbsorb(byArray, (byte)j, (byte)i);
                }
                this.symmetric.xofSqueezeBlocks(byArray2, 0, this.symmetric.xofBlockBytes * this.KyberGenerateMatrixNBlocks);
                int n = this.KyberGenerateMatrixNBlocks * this.symmetric.xofBlockBytes;
                for (int k = MLKEMIndCpa.rejectionSampling(polyVecArray[i].getVectorIndex(j), 0, 256, byArray2, n); k < 256; k += MLKEMIndCpa.rejectionSampling(polyVecArray[i].getVectorIndex(j), k, 256 - k, byArray2, n)) {
                    int n2 = n % 3;
                    for (int i2 = 0; i2 < n2; ++i2) {
                        byArray2[i2] = byArray2[n - n2 + i2];
                    }
                    this.symmetric.xofSqueezeBlocks(byArray2, n2, this.symmetric.xofBlockBytes * 2);
                    n = n2 + this.symmetric.xofBlockBytes;
                }
            }
        }
    }

    private static int rejectionSampling(Poly poly, int n, int n2, byte[] byArray, int n3) {
        int n4 = 0;
        int n5 = 0;
        while (n5 < n2 && n4 + 3 <= n3) {
            short s = (short)(((short)(byArray[n4] & 0xFF) >> 0 | (short)(byArray[n4 + 1] & 0xFF) << 8) & 0xFFF);
            short s2 = (short)(((short)(byArray[n4 + 1] & 0xFF) >> 4 | (short)(byArray[n4 + 2] & 0xFF) << 4) & 0xFFF);
            n4 += 3;
            if (s < 3329) {
                poly.setCoeffIndex(n + n5, s);
                ++n5;
            }
            if (n5 >= n2 || s2 >= 3329) continue;
            poly.setCoeffIndex(n + n5, s2);
            ++n5;
        }
        return n5;
    }

    public byte[] decrypt(byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = new byte[MLKEMEngine.getKyberIndCpaMsgBytes()];
        PolyVec polyVec = new PolyVec(this.engine);
        PolyVec polyVec2 = new PolyVec(this.engine);
        Poly poly = new Poly(this.engine);
        Poly poly2 = new Poly(this.engine);
        this.unpackCipherText(polyVec, poly, byArray2);
        this.unpackSecretKey(polyVec2, byArray);
        polyVec.polyVecNtt();
        PolyVec.pointwiseAccountMontgomery(poly2, polyVec2, polyVec, this.engine);
        poly2.polyInverseNttToMont();
        poly2.polySubtract(poly);
        poly2.reduce();
        byArray3 = poly2.toMsg();
        return byArray3;
    }
}

