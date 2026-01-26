/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Packing;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Poly;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecK;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecL;
import org.bouncycastle.pqc.crypto.crystals.dilithium.PolyVecMatrix;
import org.bouncycastle.pqc.crypto.crystals.dilithium.Symmetric;
import org.bouncycastle.util.Arrays;

class DilithiumEngine {
    private final SecureRandom random;
    private final SHAKEDigest shake256Digest = new SHAKEDigest(256);
    public static final int DilithiumN = 256;
    public static final int DilithiumQ = 8380417;
    public static final int DilithiumQinv = 58728449;
    public static final int DilithiumD = 13;
    public static final int DilithiumRootOfUnity = 1753;
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
    private final int DilithiumMode;
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
    private final int CryptoSecretKeyBytes;
    private final int CryptoBytes;
    private final int PolyUniformGamma1NBlocks;
    private final Symmetric symmetric;

    protected Symmetric GetSymmetric() {
        return this.symmetric;
    }

    int getDilithiumPolyVecHPackedBytes() {
        return this.DilithiumPolyVecHPackedBytes;
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

    int getDilithiumMode() {
        return this.DilithiumMode;
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

    int getCryptoSecretKeyBytes() {
        return this.CryptoSecretKeyBytes;
    }

    int getCryptoBytes() {
        return this.CryptoBytes;
    }

    int getPolyUniformGamma1NBlocks() {
        return this.PolyUniformGamma1NBlocks;
    }

    SHAKEDigest getShake256Digest() {
        return this.shake256Digest;
    }

    DilithiumEngine(int n, SecureRandom secureRandom, boolean bl) {
        this.DilithiumMode = n;
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
        this.symmetric = bl ? new Symmetric.AesSymmetric() : new Symmetric.ShakeSymmetric();
        this.random = secureRandom;
        this.DilithiumPolyVecHPackedBytes = this.DilithiumOmega + this.DilithiumK;
        this.CryptoPublicKeyBytes = 32 + this.DilithiumK * 320;
        this.CryptoSecretKeyBytes = 128 + this.DilithiumL * this.DilithiumPolyEtaPackedBytes + this.DilithiumK * this.DilithiumPolyEtaPackedBytes + this.DilithiumK * 416;
        this.CryptoBytes = this.DilithiumCTilde + this.DilithiumL * this.DilithiumPolyZPackedBytes + this.DilithiumPolyVecHPackedBytes;
        if (this.DilithiumGamma1 == 131072) {
            this.PolyUniformGamma1NBlocks = (576 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
        } else if (this.DilithiumGamma1 == 524288) {
            this.PolyUniformGamma1NBlocks = (640 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
        } else {
            throw new RuntimeException("Wrong Dilithium Gamma1!");
        }
    }

    public byte[][] generateKeyPairInternal(byte[] byArray) {
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
        polyVecL.copyPolyVecL(polyVecL2);
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
        return new byte[][]{byArray8[0], byArray8[1], byArray8[2], byArray8[3], byArray8[4], byArray8[5], byArray7};
    }

    public byte[] signSignatureInternal(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7, byte[] byArray8) {
        byte[] byArray9 = new byte[this.CryptoBytes + n];
        byte[] byArray10 = new byte[64];
        byte[] byArray11 = new byte[64];
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
        Packing.unpackSecretKey(polyVecK, polyVecL, polyVecK2, byArray5, byArray6, byArray7, this);
        this.shake256Digest.update(byArray4, 0, 64);
        this.shake256Digest.update(byArray, 0, n);
        this.shake256Digest.doFinal(byArray10, 0, 64);
        byte[] byArray12 = Arrays.copyOf(byArray3, 128);
        System.arraycopy(byArray8, 0, byArray12, 32, 32);
        System.arraycopy(byArray10, 0, byArray12, 64, 64);
        this.shake256Digest.update(byArray12, 0, 128);
        this.shake256Digest.doFinal(byArray11, 0, 64);
        polyVecMatrix.expandMatrix(byArray2);
        polyVecL.polyVecNtt();
        polyVecK2.polyVecNtt();
        polyVecK.polyVecNtt();
        int n2 = 0;
        while (n2 < 1000) {
            ++n2;
            short s2 = s;
            s = (short)(s + 1);
            polyVecL2.uniformGamma1(byArray11, s2);
            polyVecL2.copyPolyVecL(polyVecL3);
            polyVecL3.polyVecNtt();
            polyVecMatrix.pointwiseMontgomery(polyVecK3, polyVecL3);
            polyVecK3.reduce();
            polyVecK3.invNttToMont();
            polyVecK3.conditionalAddQ();
            polyVecK3.decompose(polyVecK4);
            System.arraycopy(polyVecK3.packW1(), 0, byArray9, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
            this.shake256Digest.update(byArray10, 0, 64);
            this.shake256Digest.update(byArray9, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
            this.shake256Digest.doFinal(byArray9, 0, this.DilithiumCTilde);
            poly.challenge(Arrays.copyOfRange(byArray9, 0, this.DilithiumCTilde));
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
            int n3 = polyVecK5.makeHint(polyVecK4, polyVecK3);
            if (n3 > this.DilithiumOmega) continue;
            return Packing.packSignature(byArray9, polyVecL3, polyVecK5, this);
        }
        return null;
    }

    public boolean signVerifyInternal(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4) {
        byte[] byArray5 = new byte[64];
        byte[] byArray6 = new byte[this.DilithiumCTilde];
        Poly poly = new Poly(this);
        PolyVecMatrix polyVecMatrix = new PolyVecMatrix(this);
        PolyVecL polyVecL = new PolyVecL(this);
        PolyVecK polyVecK = new PolyVecK(this);
        PolyVecK polyVecK2 = new PolyVecK(this);
        PolyVecK polyVecK3 = new PolyVecK(this);
        if (n != this.CryptoBytes) {
            return false;
        }
        polyVecK = Packing.unpackPublicKey(polyVecK, byArray4, this);
        if (!Packing.unpackSignature(polyVecL, polyVecK3, byArray, this)) {
            return false;
        }
        byte[] byArray7 = Arrays.copyOfRange(byArray, 0, this.DilithiumCTilde);
        if (polyVecL.checkNorm(this.getDilithiumGamma1() - this.getDilithiumBeta())) {
            return false;
        }
        this.shake256Digest.update(byArray3, 0, byArray3.length);
        this.shake256Digest.update(byArray4, 0, byArray4.length);
        this.shake256Digest.doFinal(byArray5, 0, 64);
        this.shake256Digest.update(byArray5, 0, 64);
        this.shake256Digest.update(byArray2, 0, n2);
        this.shake256Digest.doFinal(byArray5, 0);
        poly.challenge(Arrays.copyOfRange(byArray7, 0, this.DilithiumCTilde));
        polyVecMatrix.expandMatrix(byArray3);
        polyVecL.polyVecNtt();
        polyVecMatrix.pointwiseMontgomery(polyVecK2, polyVecL);
        poly.polyNtt();
        polyVecK.shiftLeft();
        polyVecK.polyVecNtt();
        polyVecK.pointwisePolyMontgomery(poly, polyVecK);
        polyVecK2.subtract(polyVecK);
        polyVecK2.reduce();
        polyVecK2.invNttToMont();
        polyVecK2.conditionalAddQ();
        polyVecK2.useHint(polyVecK2, polyVecK3);
        byte[] byArray8 = polyVecK2.packW1();
        SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
        sHAKEDigest.update(byArray5, 0, 64);
        sHAKEDigest.update(byArray8, 0, this.DilithiumK * this.DilithiumPolyW1PackedBytes);
        sHAKEDigest.doFinal(byArray6, 0, this.DilithiumCTilde);
        return Arrays.constantTimeAreEqual(byArray7, byArray6);
    }

    public byte[][] generateKeyPair() {
        byte[] byArray = new byte[32];
        this.random.nextBytes(byArray);
        return this.generateKeyPairInternal(byArray);
    }

    public byte[] signSignature(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7) {
        byte[] byArray8 = new byte[32];
        if (this.random != null) {
            this.random.nextBytes(byArray8);
        }
        return this.signSignatureInternal(byArray, n, byArray2, byArray3, byArray4, byArray5, byArray6, byArray7, byArray8);
    }

    public byte[] sign(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7) {
        return this.signSignature(byArray, n, byArray2, byArray3, byArray4, byArray5, byArray6, byArray7);
    }

    public boolean signVerify(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4) {
        return this.signVerifyInternal(byArray, n, byArray2, n2, byArray3, byArray4);
    }

    public boolean signOpen(byte[] byArray, byte[] byArray2, int n, byte[] byArray3, byte[] byArray4) {
        return this.signVerify(byArray2, n, byArray, byArray.length, byArray3, byArray4);
    }
}

