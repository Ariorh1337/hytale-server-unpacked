/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.pqc.crypto.falcon.FalconKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.util.Arrays;

public class FalconPrivateKeyParameters
extends FalconKeyParameters {
    private final byte[] pk;
    private final byte[] f;
    private final byte[] g;
    private final byte[] F;

    public FalconPrivateKeyParameters(FalconParameters falconParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        super(true, falconParameters);
        this.f = Arrays.clone(byArray);
        this.g = Arrays.clone(byArray2);
        this.F = Arrays.clone(byArray3);
        this.pk = Arrays.clone(byArray4);
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.f, this.g, this.F);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.pk);
    }

    public byte[] getSpolyf() {
        return Arrays.clone(this.f);
    }

    public byte[] getG() {
        return Arrays.clone(this.g);
    }

    public byte[] getSpolyF() {
        return Arrays.clone(this.F);
    }
}

