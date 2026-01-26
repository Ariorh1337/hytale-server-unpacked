/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.pqc.crypto.bike.BIKEKeyParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.util.Arrays;

public class BIKEPrivateKeyParameters
extends BIKEKeyParameters {
    private byte[] h0;
    private byte[] h1;
    private byte[] sigma;

    public BIKEPrivateKeyParameters(BIKEParameters bIKEParameters, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        super(true, bIKEParameters);
        this.h0 = Arrays.clone(byArray);
        this.h1 = Arrays.clone(byArray2);
        this.sigma = Arrays.clone(byArray3);
    }

    byte[] getH0() {
        return this.h0;
    }

    byte[] getH1() {
        return this.h1;
    }

    byte[] getSigma() {
        return this.sigma;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.h0, this.h1, this.sigma);
    }
}

