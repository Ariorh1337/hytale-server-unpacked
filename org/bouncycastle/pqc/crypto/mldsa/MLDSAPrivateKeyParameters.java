/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.pqc.crypto.mldsa.MLDSAEngine;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class MLDSAPrivateKeyParameters
extends MLDSAKeyParameters {
    public static final int BOTH = 0;
    public static final int SEED_ONLY = 1;
    public static final int EXPANDED_KEY = 2;
    final byte[] rho;
    final byte[] k;
    final byte[] tr;
    final byte[] s1;
    final byte[] s2;
    final byte[] t0;
    private final byte[] t1;
    private final byte[] seed;
    private final int prefFormat;

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] byArray) {
        this(mLDSAParameters, byArray, null);
    }

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7) {
        this(mLDSAParameters, byArray, byArray2, byArray3, byArray4, byArray5, byArray6, byArray7, null);
    }

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7, byte[] byArray8) {
        super(true, mLDSAParameters);
        this.rho = Arrays.clone(byArray);
        this.k = Arrays.clone(byArray2);
        this.tr = Arrays.clone(byArray3);
        this.s1 = Arrays.clone(byArray4);
        this.s2 = Arrays.clone(byArray5);
        this.t0 = Arrays.clone(byArray6);
        this.t1 = Arrays.clone(byArray7);
        this.seed = Arrays.clone(byArray8);
        this.prefFormat = byArray8 != null ? 0 : 2;
    }

    public MLDSAPrivateKeyParameters(MLDSAParameters mLDSAParameters, byte[] byArray, MLDSAPublicKeyParameters mLDSAPublicKeyParameters) {
        super(true, mLDSAParameters);
        MLDSAEngine mLDSAEngine = mLDSAParameters.getEngine(null);
        if (byArray.length == 32) {
            byte[][] byArray2 = mLDSAEngine.generateKeyPairInternal(byArray);
            this.rho = byArray2[0];
            this.k = byArray2[1];
            this.tr = byArray2[2];
            this.s1 = byArray2[3];
            this.s2 = byArray2[4];
            this.t0 = byArray2[5];
            this.t1 = byArray2[6];
            this.seed = byArray2[7];
        } else {
            int n = 0;
            this.rho = Arrays.copyOfRange(byArray, 0, 32);
            this.k = Arrays.copyOfRange(byArray, n += 32, n + 32);
            this.tr = Arrays.copyOfRange(byArray, n += 32, n + 64);
            int n2 = mLDSAEngine.getDilithiumL() * mLDSAEngine.getDilithiumPolyEtaPackedBytes();
            this.s1 = Arrays.copyOfRange(byArray, n += 64, n + n2);
            n += n2;
            n2 = mLDSAEngine.getDilithiumK() * mLDSAEngine.getDilithiumPolyEtaPackedBytes();
            this.s2 = Arrays.copyOfRange(byArray, n, n + n2);
            n += n2;
            n2 = mLDSAEngine.getDilithiumK() * 416;
            this.t0 = Arrays.copyOfRange(byArray, n, n + n2);
            n += n2;
            this.t1 = mLDSAEngine.deriveT1(this.rho, this.k, this.tr, this.s1, this.s2, this.t0);
            this.seed = null;
        }
        if (mLDSAPublicKeyParameters != null && !Arrays.constantTimeAreEqual(this.t1, mLDSAPublicKeyParameters.getT1())) {
            throw new IllegalArgumentException("passed in public key does not match private values");
        }
        this.prefFormat = this.seed != null ? 0 : 2;
    }

    private MLDSAPrivateKeyParameters(MLDSAPrivateKeyParameters mLDSAPrivateKeyParameters, int n) {
        super(true, mLDSAPrivateKeyParameters.getParameters());
        this.rho = mLDSAPrivateKeyParameters.rho;
        this.k = mLDSAPrivateKeyParameters.k;
        this.tr = mLDSAPrivateKeyParameters.tr;
        this.s1 = mLDSAPrivateKeyParameters.s1;
        this.s2 = mLDSAPrivateKeyParameters.s2;
        this.t0 = mLDSAPrivateKeyParameters.t0;
        this.t1 = mLDSAPrivateKeyParameters.t1;
        this.seed = mLDSAPrivateKeyParameters.seed;
        this.prefFormat = n;
    }

    public MLDSAPrivateKeyParameters getParametersWithFormat(int n) {
        if (this.prefFormat == n) {
            return this;
        }
        switch (n) {
            case 0: 
            case 1: {
                if (this.seed != null) break;
                throw new IllegalStateException("no seed available");
            }
            case 2: {
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown format");
            }
        }
        return new MLDSAPrivateKeyParameters(this, n);
    }

    public int getPreferredFormat() {
        return this.prefFormat;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(new byte[][]{this.rho, this.k, this.tr, this.s1, this.s2, this.t0});
    }

    public byte[] getK() {
        return Arrays.clone(this.k);
    }

    @Deprecated
    public byte[] getPrivateKey() {
        return this.getEncoded();
    }

    public byte[] getPublicKey() {
        return MLDSAPublicKeyParameters.getEncoded(this.rho, this.t1);
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }

    public MLDSAPublicKeyParameters getPublicKeyParameters() {
        if (this.t1 == null) {
            return null;
        }
        return new MLDSAPublicKeyParameters(this.getParameters(), this.rho, this.t1);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getS1() {
        return Arrays.clone(this.s1);
    }

    public byte[] getS2() {
        return Arrays.clone(this.s2);
    }

    public byte[] getT0() {
        return Arrays.clone(this.t0);
    }

    public byte[] getT1() {
        return Arrays.clone(this.t1);
    }

    public byte[] getTr() {
        return Arrays.clone(this.tr);
    }
}

