/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.mldsa.Packing;
import org.bouncycastle.pqc.crypto.mldsa.Poly;
import org.bouncycastle.pqc.crypto.mldsa.PolyVecK;
import org.bouncycastle.pqc.crypto.mldsa.PolyVecL;
import org.bouncycastle.pqc.crypto.mldsa.PolyVecMatrix;
import org.bouncycastle.pqc.crypto.mldsa.Symmetric;
import org.bouncycastle.util.Arrays;

class MLDSAEngine {
    private final SecureRandom random;
    final SHAKEDigest shake256Digest = new SHAKEDigest(256);
    public static final int DilithiumN = 256;
    public static final int DilithiumQ = 8380417;
    public static final int DilithiumQinv = 58728449;
    public static final int DilithiumD = 13;
    public static final int SeedBytes = 32;
    public static final int CrhBytes = 64;
    public static final int RndBytes = 32;
    public static final int TrBytes = 64;
    public static final int DilithiumPolyT1PackedBytes = 320;
    public static final int DilithiumPolyT0PackedBytes = 416;
    private final int DilithiumPolyVecHPackedBytes;
    private final int DilithiumPolyZPackedBytes;
    private final int DilithiumPolyW1PackedBytes;
    private final int DilithiumPolyEtaPackedBytes;
    private final int DilithiumK;
    private final int DilithiumL;
    private final int DilithiumEta;
    private final int DilithiumTau;
    private final int DilithiumBeta;
    private final int DilithiumGamma1;
    private final int DilithiumGamma2;
    private final int DilithiumOmega;
    private final int DilithiumCTilde;
    private final int CryptoPublicKeyBytes;
    private final int CryptoBytes;
    private final int PolyUniformGamma1NBlocks;
    private final Symmetric symmetric;

    protected Symmetric GetSymmetric() {
        return this.symmetric;
    }

    int getDilithiumPolyZPackedBytes() {
        return this.DilithiumPolyZPackedBytes;
    }

    int getDilithiumPolyW1PackedBytes() {
        return this.DilithiumPolyW1PackedBytes;
    }

    int getDilithiumPolyEtaPackedBytes() {
        return this.DilithiumPolyEtaPackedBytes;
    }

    int getDilithiumK() {
        return this.DilithiumK;
    }

    int getDilithiumL() {
        return this.DilithiumL;
    }

    int getDilithiumEta() {
        return this.DilithiumEta;
    }

    int getDilithiumTau() {
        return this.DilithiumTau;
    }

    int getDilithiumBeta() {
        return this.DilithiumBeta;
    }

    int getDilithiumGamma1() {
        return this.DilithiumGamma1;
    }

    int getDilithiumGamma2() {
        return this.DilithiumGamma2;
    }

    int getDilithiumOmega() {
        return this.DilithiumOmega;
    }

    int getDilithiumCTilde() {
        return this.DilithiumCTilde;
    }

    int getCryptoPublicKeyBytes() {
        return this.CryptoPublicKeyBytes;
    }

    int getPolyUniformGamma1NBlocks() {
        return this.PolyUniformGamma1NBlocks;
    }

    MLDSAEngine(int n, SecureRandom secureRandom) {
        switch (n) {
            case 2: {
                this.DilithiumK = 4;
                this.DilithiumL = 4;
                this.DilithiumEta = 2;
                this.DilithiumTau = 39;
                this.DilithiumBeta = 78;
                this.DilithiumGamma1 = 131072;
                this.DilithiumGamma2 = 95232;
                this.DilithiumOmega = 80;
                this.DilithiumPolyZPackedBytes = 576;
                this.DilithiumPolyW1PackedBytes = 192;
                this.DilithiumPolyEtaPackedBytes = 96;
                this.DilithiumCTilde = 32;
                break;
            }
            case 3: {
                this.DilithiumK = 6;
                this.DilithiumL = 5;
                this.DilithiumEta = 4;
                this.DilithiumTau = 49;
                this.DilithiumBeta = 196;
                this.DilithiumGamma1 = 524288;
                this.DilithiumGamma2 = 261888;
                this.DilithiumOmega = 55;
                this.DilithiumPolyZPackedBytes = 640;
                this.DilithiumPolyW1PackedBytes = 128;
                this.DilithiumPolyEtaPackedBytes = 128;
                this.DilithiumCTilde = 48;
                break;
            }
            case 5: {
                this.DilithiumK = 8;
                this.DilithiumL = 7;
                this.DilithiumEta = 2;
                this.DilithiumTau = 60;
                this.DilithiumBeta = 120;
                this.DilithiumGamma1 = 524288;
                this.DilithiumGamma2 = 261888;
                this.DilithiumOmega = 75;
                this.DilithiumPolyZPackedBytes = 640;
                this.DilithiumPolyW1PackedBytes = 128;
                this.DilithiumPolyEtaPackedBytes = 96;
                this.DilithiumCTilde = 64;
                break;
            }
            default: {
                throw new IllegalArgumentException("The mode " + n + "is not supported by Crystals Dilithium!");
            }
        }
        this.symmetric = new Symmetric.ShakeSymmetric();
        this.random = secureRandom;
        this.DilithiumPolyVecHPackedBytes = this.DilithiumOmega + this.DilithiumK;
        this.CryptoPublicKeyBytes = 32 + this.DilithiumK * 320;
        this.CryptoBytes = this.DilithiumCTilde + this.DilithiumL * this.DilithiumPolyZPackedBytes + this.DilithiumPolyVecHPackedBytes;
        if (this.DilithiumGamma1 == 131072) {
            this.PolyUniformGamma1NBlocks = (576 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
        } else if (this.DilithiumGamma1 == 524288) {
            this.PolyUniformGamma1NBlocks = (640 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
        } else {
            throw new RuntimeException("Wrong Dilithium Gamma1!");
        }
    }

    byte[][] generateKeyPairInternal(byte[] byArray) {
        byte[] byArray2 = new byte[128];
        byte[] byArray3 = new byte[64];
        byte[] byArray4 = new byte[32];
        byte[] byArray5 = new byte[64];
        byte[] byArray6 = new byte[32];
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        this.shake256Digest.update(byArray, 0, 32);
        this.shake256Digest.update((byte)this.DilithiumK);
        this.shake256Digest.update((byte)this.DilithiumL);
        this.shake256Digest.doFinal(byArray2, 0, 128);
        System.arraycopy(byArray2, 0, byArray4, 0, 32);
        System.arraycopy(byArray2, 32, byArray5, 0, 64);
        System.arraycopy(byArray2, 96, byArray6, 0, 32);
        polyVecMatrix.expandMatrix(byArray4);
        polyVecL.uniformEta(byArray5, (short)0);
        polyVecK.uniformEta(byArray5, (short)this.DilithiumL);
        PolyVecL polyVecL2 = new PolyVecL(this);
        polyVecL.copyTo(polyVecL2);
        polyVecL2.polyVecNtt();
        polyVecMatrix.pointwiseMontgomery(polyVecK2, polyVecL2);
        polyVecK2.reduce();
        polyVecK2.invNttToMont();
        polyVecK2.addPolyVecK(polyVecK);
        polyVecK2.conditionalAddQ();
        polyVecK2.power2Round(polyVecK3);
        byte[] byArray7 = Packing.packPublicKey(polyVecK2, this);
        this.shake256Digest.update(byArray4, 0, byArray4.length);
        this.shake256Digest.update(byArray7, 0, byArray7.length);
        this.shake256Digest.doFinal(byArray3, 0, 64);
        byte[][] byArray8 = Packing.packSecretKey(byArray4, byArray3, byArray6, polyVecK3, polyVecL, polyVecK, this);
        return new byte[][]{byArray8[0], byArray8[1], byArray8[2], byArray8[3], byArray8[4], byArray8[5], byArray7, byArray};
    }

    byte[] deriveT1(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6) {
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        Packing.unpackSecretKey(polyVecK3, polyVecL, polyVecK, byArray6, byArray4, byArray5, this);
        polyVecMatrix.expandMatrix(byArray);
        PolyVecL polyVecL2 = new PolyVecL(this);
        polyVecL.copyTo(polyVecL2);
        polyVecL2.polyVecNtt();
        polyVecMatrix.pointwiseMontgomery(polyVecK2, polyVecL2);
        polyVecK2.reduce();
        polyVecK2.invNttToMont();
        polyVecK2.addPolyVecK(polyVecK);
        polyVecK2.conditionalAddQ();
        polyVecK2.power2Round(polyVecK3);
        byte[] byArray7 = Packing.packPublicKey(polyVecK2, this);
        return byArray7;
    }

    SHAKEDigest getShake256Digest() {
        return new SHAKEDigest(this.shake256Digest);
    }

    void initSign(byte[] byArray, boolean bl, byte[] byArray2) {
        this.shake256Digest.update(byArray, 0, 64);
        this.absorbCtx(bl, byArray2);
    }

    void initVerify(byte[] byArray, byte[] byArray2, boolean bl, byte[] byArray3) {
        byte[] byArray4 = new byte[64];
        this.shake256Digest.update(byArray, 0, byArray.length);
        this.shake256Digest.update(byArray2, 0, byArray2.length);
        this.shake256Digest.doFinal(byArray4, 0, 64);
        this.shake256Digest.update(byArray4, 0, 64);
        this.absorbCtx(bl, byArray3);
    }

    void absorbCtx(boolean bl, byte[] byArray) {
        if (byArray != null) {
            this.shake256Digest.update(bl ? (byte)1 : 0);
            this.shake256Digest.update((byte)byArray.length);
            this.shake256Digest.update(byArray, 0, byArray.length);
        }
    }

    byte[] signInternal(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7) {
        SHAKEDigest sHAKEDigest = new SHAKEDigest(this.shake256Digest);
        sHAKEDigest.update(byArray, 0, n);
        return this.generateSignature(this.generateMu(sHAKEDigest), sHAKEDigest, byArray2, byArray3, byArray4, byArray5, byArray6, byArray7);
    }

    byte[] generateMu(SHAKEDigest sHAKEDigest) {
        byte[] byArray = new byte[64];
        sHAKEDigest.doFinal(byArray, 0, 64);
        return byArray;
    }

    byte[] generateSignature(byte[] byArray, SHAKEDigest sHAKEDigest, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7) {
        byte[] byArray8 = new byte[this.CryptoBytes];
        byte[] byArray9 = new byte[64];
        short s = 0;
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecL polyVecL2 = new PolyVecL(this);
        PolyVecL polyVecL3 = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        PolyVecK polyVecK4 = new PolyVecK(this);
        PolyVecK polyVecK5 = new PolyVecK(this);
        Poly poly = new Poly(this);
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        Packing.unpackSecretKey(polyVecK, polyVecL, polyVecK2, byArray4, byArray5, byArray6, this);
        byte[] byArray10 = Arrays.copyOf(byArray3, 128);
        System.arraycopy(byArray7, 0, byArray10, 32, 32);
        System.arraycopy(byArray, 0, byArray10, 64, 64);
        sHAKEDigest.update(byArray10, 0, 128);
        sHAKEDigest.doFinal(byArray9, 0, 64);
        polyVecMatrix.expandMatrix(byArray2);
        polyVecL.polyVecNtt();
        polyVecK2.polyVecNtt();
        polyVecK.polyVecNtt();
        int n = 0;
        while (n < 1000) {
            ++n;
            short s2 = s;
            s = (short)(s + 1);
            polyVecL2.uniformGamma1(byArray9, s2);
            polyVecL2.copyTo(polyVecL3);
            polyVecL3.polyVecNtt();
            polyVecMatrix.pointwiseMontgomery(polyVecK3, polyVecL3);
            polyVecK3.reduce();
            polyVecK3.invNttToMont();
            polyVecK3.conditionalAddQ();
            polyVecK3.decompose(polyVecK4);
            polyVecK3.packW1(this, byArray8, 0);
            sHAKEDigest.update(byArray, 0, 64);
            sHAKEDigest.update(byArray8, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
            sHAKEDigest.doFinal(byArray8, 0, this.DilithiumCTilde);
            poly.challenge(byArray8, 0, this.DilithiumCTilde);
            poly.polyNtt();
            polyVecL3.pointwisePolyMontgomery(poly, polyVecL);
            polyVecL3.invNttToMont();
            polyVecL3.addPolyVecL(polyVecL2);
            polyVecL3.reduce();
            if (polyVecL3.checkNorm(this.DilithiumGamma1 - this.DilithiumBeta)) continue;
            polyVecK5.pointwisePolyMontgomery(poly, polyVecK2);
            polyVecK5.invNttToMont();
            polyVecK4.subtract(polyVecK5);
            polyVecK4.reduce();
            if (polyVecK4.checkNorm(this.DilithiumGamma2 - this.DilithiumBeta)) continue;
            polyVecK5.pointwisePolyMontgomery(poly, polyVecK);
            polyVecK5.invNttToMont();
            polyVecK5.reduce();
            if (polyVecK5.checkNorm(this.DilithiumGamma2)) continue;
            polyVecK4.addPolyVecK(polyVecK5);
            polyVecK4.conditionalAddQ();
            int n2 = polyVecK5.makeHint(polyVecK4, polyVecK3);
            if (n2 > this.DilithiumOmega) continue;
            Packing.packSignature(byArray8, polyVecL3, polyVecK5, this);
            return byArray8;
        }
        return null;
    }

    boolean verifyInternalMu(byte[] byArray) {
        byte[] byArray2 = new byte[64];
        this.shake256Digest.doFinal(byArray2, 0);
        return Arrays.constantTimeAreEqual(byArray2, byArray);
    }

    boolean verifyInternalMuSignature(byte[] byArray, byte[] byArray2, int n, SHAKEDigest sHAKEDigest, byte[] byArray3, byte[] byArray4) {
        byte[] byArray5 = new byte[Math.max(64 + this.DilithiumK * this.DilithiumPolyW1PackedBytes, this.DilithiumCTilde)];
        System.arraycopy(byArray, 0, byArray5, 0, byArray.length);
        return this.doVerifyInternal(byArray5, byArray2, n, sHAKEDigest, byArray3, byArray4);
    }

    boolean verifyInternal(byte[] byArray, int n, SHAKEDigest sHAKEDigest, byte[] byArray2, byte[] byArray3) {
        byte[] byArray4 = new byte[Math.max(64 + this.DilithiumK * this.DilithiumPolyW1PackedBytes, this.DilithiumCTilde)];
        sHAKEDigest.doFinal(byArray4, 0);
        return this.doVerifyInternal(byArray4, byArray, n, sHAKEDigest, byArray2, byArray3);
    }

    private boolean doVerifyInternal(byte[] byArray, byte[] byArray2, int n, SHAKEDigest sHAKEDigest, byte[] byArray3, byte[] byArray4) {
        if (n != this.CryptoBytes) {
            return false;
        }
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        if (!Packing.unpackSignature(polyVecL, polyVecK, byArray2, this)) {
            return false;
        }
        if (polyVecL.checkNorm(this.getDilithiumGamma1() - this.getDilithiumBeta())) {
            return false;
        }
        Poly poly = new Poly(this);
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        polyVecK2 = Packing.unpackPublicKey(polyVecK2, byArray4, this);
        poly.challenge(byArray2, 0, this.DilithiumCTilde);
        polyVecMatrix.expandMatrix(byArray3);
        polyVecL.polyVecNtt();
        polyVecMatrix.pointwiseMontgomery(polyVecK3, polyVecL);
        poly.polyNtt();
        polyVecK2.shiftLeft();
        polyVecK2.polyVecNtt();
        polyVecK2.pointwisePolyMontgomery(poly, polyVecK2);
        polyVecK3.subtract(polyVecK2);
        polyVecK3.reduce();
        polyVecK3.invNttToMont();
        polyVecK3.conditionalAddQ();
        polyVecK3.useHint(polyVecK3, polyVecK);
        polyVecK3.packW1(this, byArray, 64);
        sHAKEDigest.update(byArray, 0, 64 + this.DilithiumK * this.DilithiumPolyW1PackedBytes);
        sHAKEDigest.doFinal(byArray, 0, this.DilithiumCTilde);
        return Arrays.constantTimeAreEqual(this.DilithiumCTilde, byArray2, 0, byArray, 0);
    }

    byte[][] generateKeyPair() {
        byte[] byArray = new byte[32];
        this.random.nextBytes(byArray);
        return this.generateKeyPairInternal(byArray);
    }
}

