/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;
import org.bouncycastle.pqc.crypto.mlkem.Poly;
import org.bouncycastle.util.Arrays;

class PolyVec {
    Poly[] vec;
    private MLKEMEngine engine;
    private int kyberK;
    private int polyVecBytes;

    public PolyVec(MLKEMEngine mLKEMEngine) {
        this.engine = mLKEMEngine;
        this.kyberK = mLKEMEngine.getKyberK();
        this.polyVecBytes = mLKEMEngine.getKyberPolyVecBytes();
        this.vec = new Poly[this.kyberK];
        for (int i = 0; i < this.kyberK; ++i) {
            this.vec[i] = new Poly(mLKEMEngine);
        }
    }

    public PolyVec() throws Exception {
        throw new Exception("Requires Parameter");
    }

    public Poly getVectorIndex(int n) {
        return this.vec[n];
    }

    public void polyVecNtt() {
        for (int i = 0; i < this.kyberK; ++i) {
            this.getVectorIndex(i).polyNtt();
        }
    }

    public void polyVecInverseNttToMont() {
        for (int i = 0; i < this.kyberK; ++i) {
            this.getVectorIndex(i).polyInverseNttToMont();
        }
    }

    public byte[] compressPolyVec() {
        this.conditionalSubQ();
        byte[] byArray = new byte[this.engine.getKyberPolyVecCompressedBytes()];
        int n = 0;
        if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 320) {
            short[] sArray = new short[4];
            for (int i = 0; i < this.kyberK; ++i) {
                for (int j = 0; j < 64; ++j) {
                    for (int k = 0; k < 4; ++k) {
                        long l = this.getVectorIndex(i).getCoeffIndex(4 * j + k);
                        l <<= 10;
                        l += 1665L;
                        l *= 1290167L;
                        l >>= 32;
                        sArray[k] = (short)(l &= 0x3FFL);
                    }
                    byArray[n + 0] = (byte)(sArray[0] >> 0);
                    byArray[n + 1] = (byte)(sArray[0] >> 8 | sArray[1] << 2);
                    byArray[n + 2] = (byte)(sArray[1] >> 6 | sArray[2] << 4);
                    byArray[n + 3] = (byte)(sArray[2] >> 4 | sArray[3] << 6);
                    byArray[n + 4] = (byte)(sArray[3] >> 2);
                    n += 5;
                }
            }
        } else if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 352) {
            short[] sArray = new short[8];
            for (int i = 0; i < this.kyberK; ++i) {
                for (int j = 0; j < 32; ++j) {
                    for (int k = 0; k < 8; ++k) {
                        long l = this.getVectorIndex(i).getCoeffIndex(8 * j + k);
                        l <<= 11;
                        l += 1664L;
                        l *= 645084L;
                        l >>= 31;
                        sArray[k] = (short)(l &= 0x7FFL);
                    }
                    byArray[n + 0] = (byte)(sArray[0] >> 0);
                    byArray[n + 1] = (byte)(sArray[0] >> 8 | sArray[1] << 3);
                    byArray[n + 2] = (byte)(sArray[1] >> 5 | sArray[2] << 6);
                    byArray[n + 3] = (byte)(sArray[2] >> 2);
                    byArray[n + 4] = (byte)(sArray[2] >> 10 | sArray[3] << 1);
                    byArray[n + 5] = (byte)(sArray[3] >> 7 | sArray[4] << 4);
                    byArray[n + 6] = (byte)(sArray[4] >> 4 | sArray[5] << 7);
                    byArray[n + 7] = (byte)(sArray[5] >> 1);
                    byArray[n + 8] = (byte)(sArray[5] >> 9 | sArray[6] << 2);
                    byArray[n + 9] = (byte)(sArray[6] >> 6 | sArray[7] << 5);
                    byArray[n + 10] = (byte)(sArray[7] >> 3);
                    n += 11;
                }
            }
        } else {
            throw new RuntimeException("Kyber PolyVecCompressedBytes neither 320 * KyberK or 352 * KyberK!");
        }
        return byArray;
    }

    public void decompressPolyVec(byte[] byArray) {
        int n = 0;
        if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 320) {
            short[] sArray = new short[4];
            for (int i = 0; i < this.kyberK; ++i) {
                for (int j = 0; j < 64; ++j) {
                    sArray[0] = (short)((byArray[n] & 0xFF) >> 0 | (short)((byArray[n + 1] & 0xFF) << 8));
                    sArray[1] = (short)((byArray[n + 1] & 0xFF) >> 2 | (short)((byArray[n + 2] & 0xFF) << 6));
                    sArray[2] = (short)((byArray[n + 2] & 0xFF) >> 4 | (short)((byArray[n + 3] & 0xFF) << 4));
                    sArray[3] = (short)((byArray[n + 3] & 0xFF) >> 6 | (short)((byArray[n + 4] & 0xFF) << 2));
                    n += 5;
                    for (int k = 0; k < 4; ++k) {
                        this.vec[i].setCoeffIndex(4 * j + k, (short)((sArray[k] & 0x3FF) * 3329 + 512 >> 10));
                    }
                }
            }
        } else if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 352) {
            short[] sArray = new short[8];
            for (int i = 0; i < this.kyberK; ++i) {
                for (int j = 0; j < 32; ++j) {
                    sArray[0] = (short)((byArray[n] & 0xFF) >> 0 | (short)(byArray[n + 1] & 0xFF) << 8);
                    sArray[1] = (short)((byArray[n + 1] & 0xFF) >> 3 | (short)(byArray[n + 2] & 0xFF) << 5);
                    sArray[2] = (short)((byArray[n + 2] & 0xFF) >> 6 | (short)(byArray[n + 3] & 0xFF) << 2 | (short)((byArray[n + 4] & 0xFF) << 10));
                    sArray[3] = (short)((byArray[n + 4] & 0xFF) >> 1 | (short)(byArray[n + 5] & 0xFF) << 7);
                    sArray[4] = (short)((byArray[n + 5] & 0xFF) >> 4 | (short)(byArray[n + 6] & 0xFF) << 4);
                    sArray[5] = (short)((byArray[n + 6] & 0xFF) >> 7 | (short)(byArray[n + 7] & 0xFF) << 1 | (short)((byArray[n + 8] & 0xFF) << 9));
                    sArray[6] = (short)((byArray[n + 8] & 0xFF) >> 2 | (short)(byArray[n + 9] & 0xFF) << 6);
                    sArray[7] = (short)((byArray[n + 9] & 0xFF) >> 5 | (short)(byArray[n + 10] & 0xFF) << 3);
                    n += 11;
                    for (int k = 0; k < 8; ++k) {
                        this.vec[i].setCoeffIndex(8 * j + k, (short)((sArray[k] & 0x7FF) * 3329 + 1024 >> 11));
                    }
                }
            }
        } else {
            throw new RuntimeException("Kyber PolyVecCompressedBytes neither 320 * KyberK or 352 * KyberK!");
        }
    }

    public static void pointwiseAccountMontgomery(Poly poly, PolyVec polyVec, PolyVec polyVec2, MLKEMEngine mLKEMEngine) {
        Poly poly2 = new Poly(mLKEMEngine);
        Poly.baseMultMontgomery(poly, polyVec.getVectorIndex(0), polyVec2.getVectorIndex(0));
        for (int i = 1; i < mLKEMEngine.getKyberK(); ++i) {
            Poly.baseMultMontgomery(poly2, polyVec.getVectorIndex(i), polyVec2.getVectorIndex(i));
            poly.addCoeffs(poly2);
        }
        poly.reduce();
    }

    public void reducePoly() {
        for (int i = 0; i < this.kyberK; ++i) {
            this.getVectorIndex(i).reduce();
        }
    }

    public void addPoly(PolyVec polyVec) {
        for (int i = 0; i < this.kyberK; ++i) {
            this.getVectorIndex(i).addCoeffs(polyVec.getVectorIndex(i));
        }
    }

    public byte[] toBytes() {
        byte[] byArray = new byte[this.polyVecBytes];
        for (int i = 0; i < this.kyberK; ++i) {
            System.arraycopy(this.vec[i].toBytes(), 0, byArray, i * 384, 384);
        }
        return byArray;
    }

    public void fromBytes(byte[] byArray) {
        for (int i = 0; i < this.kyberK; ++i) {
            this.getVectorIndex(i).fromBytes(Arrays.copyOfRange(byArray, i * 384, (i + 1) * 384));
        }
    }

    public void conditionalSubQ() {
        for (int i = 0; i < this.kyberK; ++i) {
            this.getVectorIndex(i).conditionalSubQ();
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < this.kyberK; ++i) {
            stringBuilder.append(this.vec[i].toString());
            if (i == this.kyberK - 1) continue;
            stringBuilder.append(", ");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    static int checkModulus(MLKEMEngine mLKEMEngine, byte[] byArray) {
        int n = -1;
        int n2 = mLKEMEngine.getKyberK();
        for (int i = 0; i < n2; ++i) {
            n &= Poly.checkModulus(byArray, i * 384);
        }
        return n;
    }
}

