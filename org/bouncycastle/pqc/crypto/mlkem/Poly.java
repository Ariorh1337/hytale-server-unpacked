/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.mlkem.CBD;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;
import org.bouncycastle.pqc.crypto.mlkem.Ntt;
import org.bouncycastle.pqc.crypto.mlkem.Reduce;
import org.bouncycastle.pqc.crypto.mlkem.Symmetric;

class Poly {
    private short[] coeffs = new short[256];
    private MLKEMEngine engine;
    private int polyCompressedBytes;
    private int eta1;
    private int eta2;
    private Symmetric symmetric;

    public Poly(MLKEMEngine mLKEMEngine) {
        this.engine = mLKEMEngine;
        this.polyCompressedBytes = mLKEMEngine.getKyberPolyCompressedBytes();
        this.eta1 = mLKEMEngine.getKyberEta1();
        this.eta2 = MLKEMEngine.getKyberEta2();
        this.symmetric = mLKEMEngine.getSymmetric();
    }

    public short getCoeffIndex(int n) {
        return this.coeffs[n];
    }

    public short[] getCoeffs() {
        return this.coeffs;
    }

    public void setCoeffIndex(int n, short s) {
        this.coeffs[n] = s;
    }

    public void setCoeffs(short[] sArray) {
        this.coeffs = sArray;
    }

    public void polyNtt() {
        this.setCoeffs(Ntt.ntt(this.getCoeffs()));
        this.reduce();
    }

    public void polyInverseNttToMont() {
        this.setCoeffs(Ntt.invNtt(this.getCoeffs()));
    }

    public void reduce() {
        for (int i = 0; i < 256; ++i) {
            this.setCoeffIndex(i, Reduce.barretReduce(this.getCoeffIndex(i)));
        }
    }

    public static void baseMultMontgomery(Poly poly, Poly poly2, Poly poly3) {
        for (int i = 0; i < 64; ++i) {
            Ntt.baseMult(poly, 4 * i, poly2.getCoeffIndex(4 * i), poly2.getCoeffIndex(4 * i + 1), poly3.getCoeffIndex(4 * i), poly3.getCoeffIndex(4 * i + 1), Ntt.nttZetas[64 + i]);
            Ntt.baseMult(poly, 4 * i + 2, poly2.getCoeffIndex(4 * i + 2), poly2.getCoeffIndex(4 * i + 3), poly3.getCoeffIndex(4 * i + 2), poly3.getCoeffIndex(4 * i + 3), (short)(-1 * Ntt.nttZetas[64 + i]));
        }
    }

    public void addCoeffs(Poly poly) {
        for (int i = 0; i < 256; ++i) {
            this.setCoeffIndex(i, (short)(this.getCoeffIndex(i) + poly.getCoeffIndex(i)));
        }
    }

    public void convertToMont() {
        for (int i = 0; i < 256; ++i) {
            this.setCoeffIndex(i, Reduce.montgomeryReduce(this.getCoeffIndex(i) * 1353));
        }
    }

    public byte[] compressPoly() {
        byte[] byArray = new byte[8];
        byte[] byArray2 = new byte[this.polyCompressedBytes];
        int n = 0;
        this.conditionalSubQ();
        if (this.polyCompressedBytes == 128) {
            for (int i = 0; i < 32; ++i) {
                for (int j = 0; j < 8; ++j) {
                    int n2 = this.getCoeffIndex(8 * i + j);
                    n2 <<= 4;
                    n2 += 1665;
                    n2 *= 80635;
                    n2 >>= 28;
                    byArray[j] = (byte)(n2 &= 0xF);
                }
                byArray2[n + 0] = (byte)(byArray[0] | byArray[1] << 4);
                byArray2[n + 1] = (byte)(byArray[2] | byArray[3] << 4);
                byArray2[n + 2] = (byte)(byArray[4] | byArray[5] << 4);
                byArray2[n + 3] = (byte)(byArray[6] | byArray[7] << 4);
                n += 4;
            }
        } else if (this.polyCompressedBytes == 160) {
            for (int i = 0; i < 32; ++i) {
                for (int j = 0; j < 8; ++j) {
                    int n3 = this.getCoeffIndex(8 * i + j);
                    n3 <<= 5;
                    n3 += 1664;
                    n3 *= 40318;
                    n3 >>= 27;
                    byArray[j] = (byte)(n3 &= 0x1F);
                }
                byArray2[n + 0] = (byte)(byArray[0] >> 0 | byArray[1] << 5);
                byArray2[n + 1] = (byte)(byArray[1] >> 3 | byArray[2] << 2 | byArray[3] << 7);
                byArray2[n + 2] = (byte)(byArray[3] >> 1 | byArray[4] << 4);
                byArray2[n + 3] = (byte)(byArray[4] >> 4 | byArray[5] << 1 | byArray[6] << 6);
                byArray2[n + 4] = (byte)(byArray[6] >> 2 | byArray[7] << 3);
                n += 5;
            }
        } else {
            throw new RuntimeException("PolyCompressedBytes is neither 128 or 160!");
        }
        return byArray2;
    }

    public void decompressPoly(byte[] byArray) {
        int n = 0;
        if (this.engine.getKyberPolyCompressedBytes() == 128) {
            for (int i = 0; i < 128; ++i) {
                this.setCoeffIndex(2 * i + 0, (short)((short)(byArray[n] & 0xFF & 0xF) * 3329 + 8 >> 4));
                this.setCoeffIndex(2 * i + 1, (short)((short)((byArray[n] & 0xFF) >> 4) * 3329 + 8 >> 4));
                ++n;
            }
        } else if (this.engine.getKyberPolyCompressedBytes() == 160) {
            byte[] byArray2 = new byte[8];
            for (int i = 0; i < 32; ++i) {
                byArray2[0] = (byte)((byArray[n + 0] & 0xFF) >> 0);
                byArray2[1] = (byte)((byArray[n + 0] & 0xFF) >> 5 | (byArray[n + 1] & 0xFF) << 3);
                byArray2[2] = (byte)((byArray[n + 1] & 0xFF) >> 2);
                byArray2[3] = (byte)((byArray[n + 1] & 0xFF) >> 7 | (byArray[n + 2] & 0xFF) << 1);
                byArray2[4] = (byte)((byArray[n + 2] & 0xFF) >> 4 | (byArray[n + 3] & 0xFF) << 4);
                byArray2[5] = (byte)((byArray[n + 3] & 0xFF) >> 1);
                byArray2[6] = (byte)((byArray[n + 3] & 0xFF) >> 6 | (byArray[n + 4] & 0xFF) << 2);
                byArray2[7] = (byte)((byArray[n + 4] & 0xFF) >> 3);
                n += 5;
                for (int j = 0; j < 8; ++j) {
                    this.setCoeffIndex(8 * i + j, (short)((byArray2[j] & 0x1F) * 3329 + 16 >> 5));
                }
            }
        } else {
            throw new RuntimeException("PolyCompressedBytes is neither 128 or 160!");
        }
    }

    public byte[] toBytes() {
        this.conditionalSubQ();
        byte[] byArray = new byte[384];
        for (int i = 0; i < 128; ++i) {
            short s = this.coeffs[2 * i + 0];
            short s2 = this.coeffs[2 * i + 1];
            byArray[3 * i + 0] = (byte)(s >> 0);
            byArray[3 * i + 1] = (byte)(s >> 8 | s2 << 4);
            byArray[3 * i + 2] = (byte)(s2 >> 4);
        }
        return byArray;
    }

    public void fromBytes(byte[] byArray) {
        for (int i = 0; i < 128; ++i) {
            int n = byArray[3 * i + 0] & 0xFF;
            int n2 = byArray[3 * i + 1] & 0xFF;
            int n3 = byArray[3 * i + 2] & 0xFF;
            this.coeffs[2 * i + 0] = (short)((n >> 0 | n2 << 8) & 0xFFF);
            this.coeffs[2 * i + 1] = (short)((n2 >> 4 | n3 << 4) & 0xFFF);
        }
    }

    public byte[] toMsg() {
        int n = 832;
        int n2 = 3329 - n;
        byte[] byArray = new byte[MLKEMEngine.getKyberIndCpaMsgBytes()];
        this.conditionalSubQ();
        for (int i = 0; i < 32; ++i) {
            byArray[i] = 0;
            for (int j = 0; j < 8; ++j) {
                short s = this.getCoeffIndex(8 * i + j);
                int n3 = (n - s & s - n2) >>> 31;
                int n4 = i;
                byArray[n4] = (byte)(byArray[n4] | (byte)(n3 << j));
            }
        }
        return byArray;
    }

    public void fromMsg(byte[] byArray) {
        if (byArray.length != 32) {
            throw new RuntimeException("KYBER_INDCPA_MSGBYTES must be equal to KYBER_N/8 bytes!");
        }
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 8; ++j) {
                short s = (short)(-1 * (short)((byArray[i] & 0xFF) >> j & 1));
                this.setCoeffIndex(8 * i + j, (short)(s & 0x681));
            }
        }
    }

    public void conditionalSubQ() {
        for (int i = 0; i < 256; ++i) {
            this.setCoeffIndex(i, Reduce.conditionalSubQ(this.getCoeffIndex(i)));
        }
    }

    public void getEta1Noise(byte[] byArray, byte by) {
        byte[] byArray2 = new byte[256 * this.eta1 / 4];
        this.symmetric.prf(byArray2, byArray, by);
        CBD.mlkemCBD(this, byArray2, this.eta1);
    }

    public void getEta2Noise(byte[] byArray, byte by) {
        byte[] byArray2 = new byte[256 * this.eta2 / 4];
        this.symmetric.prf(byArray2, byArray, by);
        CBD.mlkemCBD(this, byArray2, this.eta2);
    }

    public void polySubtract(Poly poly) {
        for (int i = 0; i < 256; ++i) {
            this.setCoeffIndex(i, (short)(poly.getCoeffIndex(i) - this.getCoeffIndex(i)));
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < this.coeffs.length; ++i) {
            stringBuilder.append(this.coeffs[i]);
            if (i == this.coeffs.length - 1) continue;
            stringBuilder.append(", ");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    static int checkModulus(byte[] byArray, int n) {
        int n2 = -1;
        for (int i = 0; i < 128; ++i) {
            int n3 = byArray[n + 3 * i + 0] & 0xFF;
            int n4 = byArray[n + 3 * i + 1] & 0xFF;
            int n5 = byArray[n + 3 * i + 2] & 0xFF;
            short s = (short)((n3 >> 0 | n4 << 8) & 0xFFF);
            short s2 = (short)((n4 >> 4 | n5 << 4) & 0xFFF);
            n2 &= Reduce.checkModulus(s);
            n2 &= Reduce.checkModulus(s2);
        }
        return n2;
    }
}

