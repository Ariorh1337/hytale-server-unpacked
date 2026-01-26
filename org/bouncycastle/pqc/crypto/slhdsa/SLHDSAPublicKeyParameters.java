/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.pqc.crypto.slhdsa.PK;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.util.Arrays;

public class SLHDSAPublicKeyParameters
extends SLHDSAKeyParameters {
    private final PK pk;

    public SLHDSAPublicKeyParameters(SLHDSAParameters sLHDSAParameters, byte[] byArray) {
        super(false, sLHDSAParameters);
        int n = sLHDSAParameters.getN();
        if (byArray.length != 2 * n) {
            throw new IllegalArgumentException("public key encoding does not match parameters");
        }
        this.pk = new PK(Arrays.copyOfRange(byArray, 0, n), Arrays.copyOfRange(byArray, n, 2 * n));
    }

    SLHDSAPublicKeyParameters(SLHDSAParameters sLHDSAParameters, PK pK) {
        super(false, sLHDSAParameters);
        this.pk = pK;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.pk.seed);
    }

    public byte[] getRoot() {
        return Arrays.clone(this.pk.root);
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.pk.seed, this.pk.root);
    }
}

