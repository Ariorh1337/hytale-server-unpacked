/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Ntt;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecL;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Reduce;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Rounding;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Symmetric;

class Poly {
    private final int polyUniformNBlocks;
    private int[] coeffs;
    private final DilithiumEngine engine;
    private final int dilithiumN;
    private final Symmetric symmetric;

    public Poly(DilithiumEngine dilithiumEngine) {
        this.dilithiumN = 256;
        this.coeffs = new int[this.dilithiumN];
        this.engine = dilithiumEngine;
        this.symmetric = dilithiumEngine.GetSymmetric();
        this.polyUniformNBlocks = (768 + this.symmetric.stream128BlockBytes - 1) / this.symmetric.stream128BlockBytes;
    }

    public int getCoeffIndex(int n) {
        return this.coeffs[n];
    }

    public int[] getCoeffs() {
        return this.coeffs;
    }

    public void setCoeffIndex(int n, int n2) {
        this.coeffs[n] = n2;
    }

    public void setCoeffs(int[] nArray) {
        this.coeffs = nArray;
    }

    public void uniformBlocks(byte[] byArray, short s) {
        int n = this.polyUniformNBlocks * this.symmetric.stream128BlockBytes;
        byte[] byArray2 = new byte[n + 2];
        this.symmetric.stream128init(byArray, s);
        this.symmetric.stream128squeezeBlocks(byArray2, 0, n);
        for (int i = Poly.rejectUniform(this, 0, this.dilithiumN, byArray2, n); i < this.dilithiumN; i += Poly.rejectUniform(this, i, this.dilithiumN - i, byArray2, n)) {
            int n2 = n % 3;
            for (int j = 0; j < n2; ++j) {
                byArray2[j] = byArray2[n - n2 + j];
            }
            this.symmetric.stream128squeezeBlocks(byArray2, n2, this.symmetric.stream128BlockBytes);
            n = this.symmetric.stream128BlockBytes + n2;
        }
    }

    private static int rejectUniform(Poly poly, int n, int n2, byte[] byArray, int n3) {
        int n4 = 0;
        int n5 = 0;
        while (n5 < n2 && n4 + 3 <= n3) {
            int n6 = byArray[n4++] & 0xFF;
            n6 |= (byArray[n4++] & 0xFF) << 8;
            n6 |= (byArray[n4++] & 0xFF) << 16;
            if ((n6 &= 0x7FFFFF) >= 8380417) continue;
            poly.setCoeffIndex(n + n5, n6);
            ++n5;
        }
        return n5;
    }

    public void uniformEta(byte[] byArray, short s) {
        int n;
        int n2 = this.engine.getDilithiumEta();
        if (this.engine.getDilithiumEta() == 2) {
            n = (136 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
        } else if (this.engine.getDilithiumEta() == 4) {
            n = (227 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
        } else {
            throw new RuntimeException("Wrong Dilithium Eta!");
        }
        int n3 = n * this.symmetric.stream256BlockBytes;
        byte[] byArray2 = new byte[n3];
        this.symmetric.stream256init(byArray, s);
        this.symmetric.stream256squeezeBlocks(byArray2, 0, n3);
        for (int i = Poly.rejectEta(this, 0, this.dilithiumN, byArray2, n3, n2); i < 256; i += Poly.rejectEta(this, i, this.dilithiumN - i, byArray2, this.symmetric.stream256BlockBytes, n2)) {
            this.symmetric.stream256squeezeBlocks(byArray2, 0, this.symmetric.stream256BlockBytes);
        }
    }

    private static int rejectEta(Poly poly, int n, int n2, byte[] byArray, int n3, int n4) {
        int n5 = 0;
        int n6 = 0;
        while (n6 < n2 && n5 < n3) {
            int n7 = byArray[n5] & 0xFF & 0xF;
            int n8 = (byArray[n5++] & 0xFF) >> 4;
            if (n4 == 2) {
                if (n7 < 15) {
                    n7 -= (205 * n7 >> 10) * 5;
                    poly.setCoeffIndex(n + n6, 2 - n7);
                    ++n6;
                }
                if (n8 >= 15 || n6 >= n2) continue;
                n8 -= (205 * n8 >> 10) * 5;
                poly.setCoeffIndex(n + n6, 2 - n8);
                ++n6;
                continue;
            }
            if (n4 != 4) continue;
            if (n7 < 9) {
                poly.setCoeffIndex(n + n6, 4 - n7);
                ++n6;
            }
            if (n8 >= 9 || n6 >= n2) continue;
            poly.setCoeffIndex(n + n6, 4 - n8);
            ++n6;
        }
        return n6;
    }

    public void polyNtt() {
        this.setCoeffs(Ntt.ntt(this.coeffs));
    }

    public void pointwiseMontgomery(Poly poly, Poly poly2) {
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, Reduce.montgomeryReduce((long)poly.getCoeffIndex(i) * (long)poly2.getCoeffIndex(i)));
        }
    }

    public void pointwiseAccountMontgomery(PolyVecL polyVecL, PolyVecL polyVecL2) {
        Poly poly = new Poly(this.engine);
        this.pointwiseMontgomery(polyVecL.getVectorIndex(0), polyVecL2.getVectorIndex(0));
        for (int i = 1; i < this.engine.getDilithiumL(); ++i) {
            poly.pointwiseMontgomery(polyVecL.getVectorIndex(i), polyVecL2.getVectorIndex(i));
            this.addPoly(poly);
        }
    }

    public void addPoly(Poly poly) {
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, this.getCoeffIndex(i) + poly.getCoeffIndex(i));
        }
    }

    public void reduce() {
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, Reduce.reduce32(this.getCoeffIndex(i)));
        }
    }

    public void invNttToMont() {
        this.setCoeffs(Ntt.invNttToMont(this.getCoeffs()));
    }

    public void conditionalAddQ() {
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, Reduce.conditionalAddQ(this.getCoeffIndex(i)));
        }
    }

    public void power2Round(Poly poly) {
        for (int i = 0; i < this.dilithiumN; ++i) {
            int[] nArray = Rounding.power2Round(this.getCoeffIndex(i));
            this.setCoeffIndex(i, nArray[0]);
            poly.setCoeffIndex(i, nArray[1]);
        }
    }

    public byte[] polyt1Pack() {
        byte[] byArray = new byte[320];
        for (int i = 0; i < this.dilithiumN / 4; ++i) {
            byArray[5 * i + 0] = (byte)(this.coeffs[4 * i + 0] >> 0);
            byArray[5 * i + 1] = (byte)(this.coeffs[4 * i + 0] >> 8 | this.coeffs[4 * i + 1] << 2);
            byArray[5 * i + 2] = (byte)(this.coeffs[4 * i + 1] >> 6 | this.coeffs[4 * i + 2] << 4);
            byArray[5 * i + 3] = (byte)(this.coeffs[4 * i + 2] >> 4 | this.coeffs[4 * i + 3] << 6);
            byArray[5 * i + 4] = (byte)(this.coeffs[4 * i + 3] >> 2);
        }
        return byArray;
    }

    public void polyt1Unpack(byte[] byArray) {
        for (int i = 0; i < this.dilithiumN / 4; ++i) {
            this.setCoeffIndex(4 * i + 0, ((byArray[5 * i + 0] & 0xFF) >> 0 | (byArray[5 * i + 1] & 0xFF) << 8) & 0x3FF);
            this.setCoeffIndex(4 * i + 1, ((byArray[5 * i + 1] & 0xFF) >> 2 | (byArray[5 * i + 2] & 0xFF) << 6) & 0x3FF);
            this.setCoeffIndex(4 * i + 2, ((byArray[5 * i + 2] & 0xFF) >> 4 | (byArray[5 * i + 3] & 0xFF) << 4) & 0x3FF);
            this.setCoeffIndex(4 * i + 3, ((byArray[5 * i + 3] & 0xFF) >> 6 | (byArray[5 * i + 4] & 0xFF) << 2) & 0x3FF);
        }
    }

    public byte[] polyEtaPack(byte[] byArray, int n) {
        byte[] byArray2 = new byte[8];
        if (this.engine.getDilithiumEta() == 2) {
            for (int i = 0; i < this.dilithiumN / 8; ++i) {
                byArray2[0] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 0));
                byArray2[1] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 1));
                byArray2[2] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 2));
                byArray2[3] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 3));
                byArray2[4] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 4));
                byArray2[5] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 5));
                byArray2[6] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 6));
                byArray2[7] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * i + 7));
                byArray[n + 3 * i + 0] = (byte)(byArray2[0] >> 0 | byArray2[1] << 3 | byArray2[2] << 6);
                byArray[n + 3 * i + 1] = (byte)(byArray2[2] >> 2 | byArray2[3] << 1 | byArray2[4] << 4 | byArray2[5] << 7);
                byArray[n + 3 * i + 2] = (byte)(byArray2[5] >> 1 | byArray2[6] << 2 | byArray2[7] << 5);
            }
        } else if (this.engine.getDilithiumEta() == 4) {
            for (int i = 0; i < this.dilithiumN / 2; ++i) {
                byArray2[0] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(2 * i + 0));
                byArray2[1] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(2 * i + 1));
                byArray[n + i] = (byte)(byArray2[0] | byArray2[1] << 4);
            }
        } else {
            throw new RuntimeException("Eta needs to be 2 or 4!");
        }
        return byArray;
    }

    public void polyEtaUnpack(byte[] byArray, int n) {
        block3: {
            int n2;
            block2: {
                n2 = this.engine.getDilithiumEta();
                if (this.engine.getDilithiumEta() != 2) break block2;
                for (int i = 0; i < this.dilithiumN / 8; ++i) {
                    int n3 = n + 3 * i;
                    this.setCoeffIndex(8 * i + 0, (byArray[n3 + 0] & 0xFF) >> 0 & 7);
                    this.setCoeffIndex(8 * i + 1, (byArray[n3 + 0] & 0xFF) >> 3 & 7);
                    this.setCoeffIndex(8 * i + 2, (byArray[n3 + 0] & 0xFF) >> 6 | (byArray[n3 + 1] & 0xFF) << 2 & 7);
                    this.setCoeffIndex(8 * i + 3, (byArray[n3 + 1] & 0xFF) >> 1 & 7);
                    this.setCoeffIndex(8 * i + 4, (byArray[n3 + 1] & 0xFF) >> 4 & 7);
                    this.setCoeffIndex(8 * i + 5, (byArray[n3 + 1] & 0xFF) >> 7 | (byArray[n3 + 2] & 0xFF) << 1 & 7);
                    this.setCoeffIndex(8 * i + 6, (byArray[n3 + 2] & 0xFF) >> 2 & 7);
                    this.setCoeffIndex(8 * i + 7, (byArray[n3 + 2] & 0xFF) >> 5 & 7);
                    this.setCoeffIndex(8 * i + 0, n2 - this.getCoeffIndex(8 * i + 0));
                    this.setCoeffIndex(8 * i + 1, n2 - this.getCoeffIndex(8 * i + 1));
                    this.setCoeffIndex(8 * i + 2, n2 - this.getCoeffIndex(8 * i + 2));
                    this.setCoeffIndex(8 * i + 3, n2 - this.getCoeffIndex(8 * i + 3));
                    this.setCoeffIndex(8 * i + 4, n2 - this.getCoeffIndex(8 * i + 4));
                    this.setCoeffIndex(8 * i + 5, n2 - this.getCoeffIndex(8 * i + 5));
                    this.setCoeffIndex(8 * i + 6, n2 - this.getCoeffIndex(8 * i + 6));
                    this.setCoeffIndex(8 * i + 7, n2 - this.getCoeffIndex(8 * i + 7));
                }
                break block3;
            }
            if (this.engine.getDilithiumEta() != 4) break block3;
            for (int i = 0; i < this.dilithiumN / 2; ++i) {
                this.setCoeffIndex(2 * i + 0, byArray[n + i] & 0xF);
                this.setCoeffIndex(2 * i + 1, (byArray[n + i] & 0xFF) >> 4);
                this.setCoeffIndex(2 * i + 0, n2 - this.getCoeffIndex(2 * i + 0));
                this.setCoeffIndex(2 * i + 1, n2 - this.getCoeffIndex(2 * i + 1));
            }
        }
    }

    public byte[] polyt0Pack(byte[] byArray, int n) {
        int[] nArray = new int[8];
        for (int i = 0; i < this.dilithiumN / 8; ++i) {
            nArray[0] = 4096 - this.getCoeffIndex(8 * i + 0);
            nArray[1] = 4096 - this.getCoeffIndex(8 * i + 1);
            nArray[2] = 4096 - this.getCoeffIndex(8 * i + 2);
            nArray[3] = 4096 - this.getCoeffIndex(8 * i + 3);
            nArray[4] = 4096 - this.getCoeffIndex(8 * i + 4);
            nArray[5] = 4096 - this.getCoeffIndex(8 * i + 5);
            nArray[6] = 4096 - this.getCoeffIndex(8 * i + 6);
            nArray[7] = 4096 - this.getCoeffIndex(8 * i + 7);
            int n2 = n + 13 * i;
            byArray[n2 + 0] = (byte)nArray[0];
            byArray[n2 + 1] = (byte)(nArray[0] >> 8);
            byArray[n2 + 1] = (byte)(byArray[n2 + 1] | (byte)(nArray[1] << 5));
            byArray[n2 + 2] = (byte)(nArray[1] >> 3);
            byArray[n2 + 3] = (byte)(nArray[1] >> 11);
            byArray[n2 + 3] = (byte)(byArray[n2 + 3] | (byte)(nArray[2] << 2));
            byArray[n2 + 4] = (byte)(nArray[2] >> 6);
            byArray[n2 + 4] = (byte)(byArray[n2 + 4] | (byte)(nArray[3] << 7));
            byArray[n2 + 5] = (byte)(nArray[3] >> 1);
            byArray[n2 + 6] = (byte)(nArray[3] >> 9);
            byArray[n2 + 6] = (byte)(byArray[n2 + 6] | (byte)(nArray[4] << 4));
            byArray[n2 + 7] = (byte)(nArray[4] >> 4);
            byArray[n2 + 8] = (byte)(nArray[4] >> 12);
            byArray[n2 + 8] = (byte)(byArray[n2 + 8] | (byte)(nArray[5] << 1));
            byArray[n2 + 9] = (byte)(nArray[5] >> 7);
            byArray[n2 + 9] = (byte)(byArray[n2 + 9] | (byte)(nArray[6] << 6));
            byArray[n2 + 10] = (byte)(nArray[6] >> 2);
            byArray[n2 + 11] = (byte)(nArray[6] >> 10);
            byArray[n2 + 11] = (byte)(byArray[n2 + 11] | (byte)(nArray[7] << 3));
            byArray[n2 + 12] = (byte)(nArray[7] >> 5);
        }
        return byArray;
    }

    public void polyt0Unpack(byte[] byArray, int n) {
        for (int i = 0; i < this.dilithiumN / 8; ++i) {
            int n2 = n + 13 * i;
            this.setCoeffIndex(8 * i + 0, (byArray[n2 + 0] & 0xFF | (byArray[n2 + 1] & 0xFF) << 8) & 0x1FFF);
            this.setCoeffIndex(8 * i + 1, ((byArray[n2 + 1] & 0xFF) >> 5 | (byArray[n2 + 2] & 0xFF) << 3 | (byArray[n2 + 3] & 0xFF) << 11) & 0x1FFF);
            this.setCoeffIndex(8 * i + 2, ((byArray[n2 + 3] & 0xFF) >> 2 | (byArray[n2 + 4] & 0xFF) << 6) & 0x1FFF);
            this.setCoeffIndex(8 * i + 3, ((byArray[n2 + 4] & 0xFF) >> 7 | (byArray[n2 + 5] & 0xFF) << 1 | (byArray[n2 + 6] & 0xFF) << 9) & 0x1FFF);
            this.setCoeffIndex(8 * i + 4, ((byArray[n2 + 6] & 0xFF) >> 4 | (byArray[n2 + 7] & 0xFF) << 4 | (byArray[n2 + 8] & 0xFF) << 12) & 0x1FFF);
            this.setCoeffIndex(8 * i + 5, ((byArray[n2 + 8] & 0xFF) >> 1 | (byArray[n2 + 9] & 0xFF) << 7) & 0x1FFF);
            this.setCoeffIndex(8 * i + 6, ((byArray[n2 + 9] & 0xFF) >> 6 | (byArray[n2 + 10] & 0xFF) << 2 | (byArray[n2 + 11] & 0xFF) << 10) & 0x1FFF);
            this.setCoeffIndex(8 * i + 7, ((byArray[n2 + 11] & 0xFF) >> 3 | (byArray[n2 + 12] & 0xFF) << 5) & 0x1FFF);
            this.setCoeffIndex(8 * i + 0, 4096 - this.getCoeffIndex(8 * i + 0));
            this.setCoeffIndex(8 * i + 1, 4096 - this.getCoeffIndex(8 * i + 1));
            this.setCoeffIndex(8 * i + 2, 4096 - this.getCoeffIndex(8 * i + 2));
            this.setCoeffIndex(8 * i + 3, 4096 - this.getCoeffIndex(8 * i + 3));
            this.setCoeffIndex(8 * i + 4, 4096 - this.getCoeffIndex(8 * i + 4));
            this.setCoeffIndex(8 * i + 5, 4096 - this.getCoeffIndex(8 * i + 5));
            this.setCoeffIndex(8 * i + 6, 4096 - this.getCoeffIndex(8 * i + 6));
            this.setCoeffIndex(8 * i + 7, 4096 - this.getCoeffIndex(8 * i + 7));
        }
    }

    public void uniformGamma1(byte[] byArray, short s) {
        byte[] byArray2 = new byte[this.engine.getPolyUniformGamma1NBlocks() * this.symmetric.stream256BlockBytes];
        this.symmetric.stream256init(byArray, s);
        this.symmetric.stream256squeezeBlocks(byArray2, 0, this.engine.getPolyUniformGamma1NBlocks() * this.symmetric.stream256BlockBytes);
        this.unpackZ(byArray2);
    }

    private void unpackZ(byte[] byArray) {
        if (this.engine.getDilithiumGamma1() == 131072) {
            for (int i = 0; i < this.dilithiumN / 4; ++i) {
                this.setCoeffIndex(4 * i + 0, (byArray[9 * i + 0] & 0xFF | (byArray[9 * i + 1] & 0xFF) << 8 | (byArray[9 * i + 2] & 0xFF) << 16) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 1, ((byArray[9 * i + 2] & 0xFF) >> 2 | (byArray[9 * i + 3] & 0xFF) << 6 | (byArray[9 * i + 4] & 0xFF) << 14) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 2, ((byArray[9 * i + 4] & 0xFF) >> 4 | (byArray[9 * i + 5] & 0xFF) << 4 | (byArray[9 * i + 6] & 0xFF) << 12) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 3, ((byArray[9 * i + 6] & 0xFF) >> 6 | (byArray[9 * i + 7] & 0xFF) << 2 | (byArray[9 * i + 8] & 0xFF) << 10) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 0));
                this.setCoeffIndex(4 * i + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 1));
                this.setCoeffIndex(4 * i + 2, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 2));
                this.setCoeffIndex(4 * i + 3, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 3));
            }
        } else if (this.engine.getDilithiumGamma1() == 524288) {
            for (int i = 0; i < this.dilithiumN / 2; ++i) {
                this.setCoeffIndex(2 * i + 0, (byArray[5 * i + 0] & 0xFF | (byArray[5 * i + 1] & 0xFF) << 8 | (byArray[5 * i + 2] & 0xFF) << 16) & 0xFFFFF);
                this.setCoeffIndex(2 * i + 1, ((byArray[5 * i + 2] & 0xFF) >> 4 | (byArray[5 * i + 3] & 0xFF) << 4 | (byArray[5 * i + 4] & 0xFF) << 12) & 0xFFFFF);
                this.setCoeffIndex(2 * i + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * i + 0));
                this.setCoeffIndex(2 * i + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * i + 1));
            }
        } else {
            throw new RuntimeException("Wrong Dilithiumn Gamma1!");
        }
    }

    public void decompose(Poly poly) {
        for (int i = 0; i < this.dilithiumN; ++i) {
            int[] nArray = Rounding.decompose(this.getCoeffIndex(i), this.engine.getDilithiumGamma2());
            this.setCoeffIndex(i, nArray[1]);
            poly.setCoeffIndex(i, nArray[0]);
        }
    }

    public byte[] w1Pack() {
        byte[] byArray;
        block3: {
            block2: {
                byArray = new byte[this.engine.getDilithiumPolyW1PackedBytes()];
                if (this.engine.getDilithiumGamma2() != 95232) break block2;
                for (int i = 0; i < this.dilithiumN / 4; ++i) {
                    byArray[3 * i + 0] = (byte)((byte)this.getCoeffIndex(4 * i + 0) | this.getCoeffIndex(4 * i + 1) << 6);
                    byArray[3 * i + 1] = (byte)((byte)(this.getCoeffIndex(4 * i + 1) >> 2) | this.getCoeffIndex(4 * i + 2) << 4);
                    byArray[3 * i + 2] = (byte)((byte)(this.getCoeffIndex(4 * i + 2) >> 4) | this.getCoeffIndex(4 * i + 3) << 2);
                }
                break block3;
            }
            if (this.engine.getDilithiumGamma2() != 261888) break block3;
            for (int i = 0; i < this.dilithiumN / 2; ++i) {
                byArray[i] = (byte)(this.getCoeffIndex(2 * i + 0) | this.getCoeffIndex(2 * i + 1) << 4);
            }
        }
        return byArray;
    }

    public void challenge(byte[] byArray) {
        int n;
        int n2 = 0;
        byte[] byArray2 = new byte[this.symmetric.stream256BlockBytes];
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray, 0, this.engine.getDilithiumCTilde());
        sHAKEDigest.doOutput(byArray2, 0, this.symmetric.stream256BlockBytes);
        long l = 0L;
        for (n = 0; n < 8; ++n) {
            l |= (long)(byArray2[n] & 0xFF) << 8 * n;
        }
        int n3 = 8;
        for (n = 0; n < this.dilithiumN; ++n) {
            this.setCoeffIndex(n, 0);
        }
        for (n = this.dilithiumN - this.engine.getDilithiumTau(); n < this.dilithiumN; ++n) {
            do {
                if (n3 < this.symmetric.stream256BlockBytes) continue;
                sHAKEDigest.doOutput(byArray2, 0, this.symmetric.stream256BlockBytes);
                n3 = 0;
            } while ((n2 = byArray2[n3++] & 0xFF) > n);
            this.setCoeffIndex(n, this.getCoeffIndex(n2));
            this.setCoeffIndex(n2, (int)(1L - 2L * (l & 1L)));
            l >>= 1;
        }
    }

    public boolean checkNorm(int n) {
        if (n > 1047552) {
            return true;
        }
        for (int i = 0; i < this.dilithiumN; ++i) {
            int n2 = this.getCoeffIndex(i) >> 31;
            n2 = this.getCoeffIndex(i) - (n2 & 2 * this.getCoeffIndex(i));
            if (n2 < n) continue;
            return true;
        }
        return false;
    }

    public void subtract(Poly poly) {
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, this.getCoeffIndex(i) - poly.getCoeffIndex(i));
        }
    }

    public int polyMakeHint(Poly poly, Poly poly2) {
        int n = 0;
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, Rounding.makeHint(poly.getCoeffIndex(i), poly2.getCoeffIndex(i), this.engine));
            n += this.getCoeffIndex(i);
        }
        return n;
    }

    public void polyUseHint(Poly poly, Poly poly2) {
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, Rounding.useHint(poly.getCoeffIndex(i), poly2.getCoeffIndex(i), this.engine.getDilithiumGamma2()));
        }
    }

    public byte[] zPack() {
        byte[] byArray = new byte[this.engine.getDilithiumPolyZPackedBytes()];
        int[] nArray = new int[4];
        if (this.engine.getDilithiumGamma1() == 131072) {
            for (int i = 0; i < this.dilithiumN / 4; ++i) {
                nArray[0] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 0);
                nArray[1] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 1);
                nArray[2] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 2);
                nArray[3] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 3);
                byArray[9 * i + 0] = (byte)nArray[0];
                byArray[9 * i + 1] = (byte)(nArray[0] >> 8);
                byArray[9 * i + 2] = (byte)((byte)(nArray[0] >> 16) | nArray[1] << 2);
                byArray[9 * i + 3] = (byte)(nArray[1] >> 6);
                byArray[9 * i + 4] = (byte)((byte)(nArray[1] >> 14) | nArray[2] << 4);
                byArray[9 * i + 5] = (byte)(nArray[2] >> 4);
                byArray[9 * i + 6] = (byte)((byte)(nArray[2] >> 12) | nArray[3] << 6);
                byArray[9 * i + 7] = (byte)(nArray[3] >> 2);
                byArray[9 * i + 8] = (byte)(nArray[3] >> 10);
            }
        } else if (this.engine.getDilithiumGamma1() == 524288) {
            for (int i = 0; i < this.dilithiumN / 2; ++i) {
                nArray[0] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * i + 0);
                nArray[1] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * i + 1);
                byArray[5 * i + 0] = (byte)nArray[0];
                byArray[5 * i + 1] = (byte)(nArray[0] >> 8);
                byArray[5 * i + 2] = (byte)((byte)(nArray[0] >> 16) | nArray[1] << 4);
                byArray[5 * i + 3] = (byte)(nArray[1] >> 4);
                byArray[5 * i + 4] = (byte)(nArray[1] >> 12);
            }
        } else {
            throw new RuntimeException("Wrong Dilithium Gamma1!");
        }
        return byArray;
    }

    void zUnpack(byte[] byArray) {
        if (this.engine.getDilithiumGamma1() == 131072) {
            for (int i = 0; i < this.dilithiumN / 4; ++i) {
                this.setCoeffIndex(4 * i + 0, (byArray[9 * i + 0] & 0xFF | (byArray[9 * i + 1] & 0xFF) << 8 | (byArray[9 * i + 2] & 0xFF) << 16) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 1, ((byArray[9 * i + 2] & 0xFF) >>> 2 | (byArray[9 * i + 3] & 0xFF) << 6 | (byArray[9 * i + 4] & 0xFF) << 14) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 2, ((byArray[9 * i + 4] & 0xFF) >>> 4 | (byArray[9 * i + 5] & 0xFF) << 4 | (byArray[9 * i + 6] & 0xFF) << 12) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 3, ((byArray[9 * i + 6] & 0xFF) >>> 6 | (byArray[9 * i + 7] & 0xFF) << 2 | (byArray[9 * i + 8] & 0xFF) << 10) & 0x3FFFF);
                this.setCoeffIndex(4 * i + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 0));
                this.setCoeffIndex(4 * i + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 1));
                this.setCoeffIndex(4 * i + 2, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 2));
                this.setCoeffIndex(4 * i + 3, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * i + 3));
            }
        } else if (this.engine.getDilithiumGamma1() == 524288) {
            for (int i = 0; i < this.dilithiumN / 2; ++i) {
                this.setCoeffIndex(2 * i + 0, (byArray[5 * i + 0] & 0xFF | (byArray[5 * i + 1] & 0xFF) << 8 | (byArray[5 * i + 2] & 0xFF) << 16) & 0xFFFFF);
                this.setCoeffIndex(2 * i + 1, ((byArray[5 * i + 2] & 0xFF) >>> 4 | (byArray[5 * i + 3] & 0xFF) << 4 | (byArray[5 * i + 4] & 0xFF) << 12) & 0xFFFFF);
                this.setCoeffIndex(2 * i + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * i + 0));
                this.setCoeffIndex(2 * i + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * i + 1));
            }
        } else {
            throw new RuntimeException("Wrong Dilithium Gamma1!");
        }
    }

    public void shiftLeft() {
        for (int i = 0; i < this.dilithiumN; ++i) {
            this.setCoeffIndex(i, this.getCoeffIndex(i) << 13);
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
}

