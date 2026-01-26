/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.pqc.crypto.falcon.FalconKeyParameters;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;
import org.bouncycastle.util.Arrays;

public class FalconPublicKeyParameters
extends FalconKeyParameters {
    private final byte[] H;

    public FalconPublicKeyParameters(FalconParameters falconParameters, byte[] byArray) {
        super(false, falconParameters);
        this.H = Arrays.clone(byArray);
    }

    public byte[] getH() {
        return Arrays.clone(this.H);
    }
}

