/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.util.Arrays;

public class MLDSAPublicKeyParameters
extends MLDSAKeyParameters {
    final byte[] rho;
    final byte[] t1;

    static byte[] getEncoded(byte[] byArray, byte[] byArray2) {
        return Arrays.concatenate(byArray, byArray2);
    }

    public MLDSAPublicKeyParameters(MLDSAParameters mLDSAParameters, byte[] byArray) {
        super(false, mLDSAParameters);
        this.rho = Arrays.copyOfRange(byArray, 0, 32);
        this.t1 = Arrays.copyOfRange(byArray, 32, byArray.length);
        if (this.t1.length == 0) {
            throw new IllegalArgumentException("encoding too short");
        }
    }

    public MLDSAPublicKeyParameters(MLDSAParameters mLDSAParameters, byte[] byArray, byte[] byArray2) {
        super(false, mLDSAParameters);
        if (byArray == null) {
            throw new NullPointerException("rho cannot be null");
        }
        if (byArray2 == null) {
            throw new NullPointerException("t1 cannot be null");
        }
        this.rho = Arrays.clone(byArray);
        this.t1 = Arrays.clone(byArray2);
    }

    public byte[] getEncoded() {
        return MLDSAPublicKeyParameters.getEncoded(this.rho, this.t1);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getT1() {
        return Arrays.clone(this.t1);
    }
}

