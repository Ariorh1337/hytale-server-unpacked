/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class MLKEMPrivateKeyParameters
extends MLKEMKeyParameters {
    public static final int BOTH = 0;
    public static final int SEED_ONLY = 1;
    public static final int EXPANDED_KEY = 2;
    final byte[] s;
    final byte[] hpk;
    final byte[] nonce;
    final byte[] t;
    final byte[] rho;
    final byte[] seed;
    private final int prefFormat;

    public MLKEMPrivateKeyParameters(MLKEMParameters mLKEMParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
        this(mLKEMParameters, byArray, byArray2, byArray3, byArray4, byArray5, null);
    }

    public MLKEMPrivateKeyParameters(MLKEMParameters mLKEMParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6) {
        super(true, mLKEMParameters);
        this.s = Arrays.clone(byArray);
        this.hpk = Arrays.clone(byArray2);
        this.nonce = Arrays.clone(byArray3);
        this.t = Arrays.clone(byArray4);
        this.rho = Arrays.clone(byArray5);
        this.seed = Arrays.clone(byArray6);
        this.prefFormat = 0;
    }

    public MLKEMPrivateKeyParameters(MLKEMParameters mLKEMParameters, byte[] byArray) {
        this(mLKEMParameters, byArray, null);
    }

    public MLKEMPrivateKeyParameters(MLKEMParameters mLKEMParameters, byte[] byArray, MLKEMPublicKeyParameters mLKEMPublicKeyParameters) {
        super(true, mLKEMParameters);
        MLKEMEngine mLKEMEngine = mLKEMParameters.getEngine();
        if (byArray.length == 64) {
            byte[][] byArray2 = mLKEMEngine.generateKemKeyPairInternal(Arrays.copyOfRange(byArray, 0, 32), Arrays.copyOfRange(byArray, 32, byArray.length));
            this.s = byArray2[2];
            this.hpk = byArray2[3];
            this.nonce = byArray2[4];
            this.t = byArray2[0];
            this.rho = byArray2[1];
            this.seed = byArray2[5];
        } else {
            int n = 0;
            this.s = Arrays.copyOfRange(byArray, 0, mLKEMEngine.getKyberIndCpaSecretKeyBytes());
            this.t = Arrays.copyOfRange(byArray, n += mLKEMEngine.getKyberIndCpaSecretKeyBytes(), n + mLKEMEngine.getKyberIndCpaPublicKeyBytes() - 32);
            this.rho = Arrays.copyOfRange(byArray, n += mLKEMEngine.getKyberIndCpaPublicKeyBytes() - 32, n + 32);
            this.hpk = Arrays.copyOfRange(byArray, n += 32, n + 32);
            this.nonce = Arrays.copyOfRange(byArray, n += 32, n + 32);
            this.seed = null;
        }
        if (!(mLKEMPublicKeyParameters == null || Arrays.constantTimeAreEqual(this.t, mLKEMPublicKeyParameters.t) && Arrays.constantTimeAreEqual(this.rho, mLKEMPublicKeyParameters.rho))) {
            throw new IllegalArgumentException("passed in public key does not match private values");
        }
        this.prefFormat = this.seed == null ? 2 : 0;
    }

    private MLKEMPrivateKeyParameters(MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters, int n) {
        super(true, mLKEMPrivateKeyParameters.getParameters());
        this.s = mLKEMPrivateKeyParameters.s;
        this.t = mLKEMPrivateKeyParameters.t;
        this.rho = mLKEMPrivateKeyParameters.rho;
        this.hpk = mLKEMPrivateKeyParameters.hpk;
        this.nonce = mLKEMPrivateKeyParameters.nonce;
        this.seed = mLKEMPrivateKeyParameters.seed;
        this.prefFormat = n;
    }

    public MLKEMPrivateKeyParameters getParametersWithFormat(int n) {
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
        return new MLKEMPrivateKeyParameters(this, n);
    }

    public int getPreferredFormat() {
        return this.prefFormat;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(new byte[][]{this.s, this.t, this.rho, this.hpk, this.nonce});
    }

    public byte[] getHPK() {
        return Arrays.clone(this.hpk);
    }

    public byte[] getNonce() {
        return Arrays.clone(this.nonce);
    }

    public byte[] getPublicKey() {
        return MLKEMPublicKeyParameters.getEncoded(this.t, this.rho);
    }

    public MLKEMPublicKeyParameters getPublicKeyParameters() {
        return new MLKEMPublicKeyParameters(this.getParameters(), this.t, this.rho);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getS() {
        return Arrays.clone(this.s);
    }

    public byte[] getT() {
        return Arrays.clone(this.t);
    }

    public byte[] getSeed() {
        return Arrays.clone(this.seed);
    }
}

