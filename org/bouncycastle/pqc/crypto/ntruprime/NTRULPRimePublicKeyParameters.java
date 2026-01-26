/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.util.Arrays;

public class NTRULPRimePublicKeyParameters
extends NTRULPRimeKeyParameters {
    private final byte[] seed;
    private final byte[] roundEncA;

    public NTRULPRimePublicKeyParameters(NTRULPRimeParameters nTRULPRimeParameters, byte[] byArray) {
        super(false, nTRULPRimeParameters);
        this.seed = Arrays.copyOfRange(byArray, 0, 32);
        this.roundEncA = Arrays.copyOfRange(byArray, this.seed.length, byArray.length);
    }

    NTRULPRimePublicKeyParameters(NTRULPRimeParameters nTRULPRimeParameters, byte[] byArray, byte[] byArray2) {
        super(false, nTRULPRimeParameters);
        this.seed = Arrays.clone(byArray);
        this.roundEncA = Arrays.clone(byArray2);
    }

    byte[] getSeed() {
        return this.seed;
    }

    byte[] getRoundEncA() {
        return this.roundEncA;
    }

    public byte[] getEncoded() {
        byte[] byArray = new byte[this.getParameters().getPublicKeyBytes()];
        System.arraycopy(this.seed, 0, byArray, 0, this.seed.length);
        System.arraycopy(this.roundEncA, 0, byArray, this.seed.length, this.roundEncA.length);
        return byArray;
    }
}

