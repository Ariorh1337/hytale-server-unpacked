/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.pqc.crypto.slhdsa.PK;
import org.bouncycastle.pqc.crypto.slhdsa.SK;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAKeyParameters;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;
import org.bouncycastle.util.Arrays;

public class SLHDSAPrivateKeyParameters
extends SLHDSAKeyParameters {
    final SK sk;
    final PK pk;

    public SLHDSAPrivateKeyParameters(SLHDSAParameters sLHDSAParameters, byte[] byArray) {
        super(true, sLHDSAParameters);
        int n = sLHDSAParameters.getN();
        if (byArray.length != 4 * n) {
            throw new IllegalArgumentException("private key encoding does not match parameters");
        }
        this.sk = new SK(Arrays.copyOfRange(byArray, 0, n), Arrays.copyOfRange(byArray, n, 2 * n));
        this.pk = new PK(Arrays.copyOfRange(byArray, 2 * n, 3 * n), Arrays.copyOfRange(byArray, 3 * n, 4 * n));
    }

    public SLHDSAPrivateKeyParameters(SLHDSAParameters sLHDSAParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
        super(true, sLHDSAParameters);
        this.sk = new SK(byArray, byArray2);
        this.pk = new PK(byArray3, byArray4);
    }

    SLHDSAPrivateKeyParameters(SLHDSAParameters sLHDSAParameters, SK sK, PK pK) {
        super(true, sLHDSAParameters);
        this.sk = sK;
        this.pk = pK;
    }

    public byte[] getSeed() {
        return Arrays.clone(this.sk.seed);
    }

    public byte[] getPrf() {
        return Arrays.clone(this.sk.prf);
    }

    public byte[] getPublicSeed() {
        return Arrays.clone(this.pk.seed);
    }

    public byte[] getRoot() {
        return Arrays.clone(this.pk.root);
    }

    public byte[] getPublicKey() {
        return Arrays.concatenate(this.pk.seed, this.pk.root);
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(new byte[][]{this.sk.seed, this.sk.prf, this.pk.seed, this.pk.root});
    }

    public byte[] getEncodedPublicKey() {
        return Arrays.concatenate(this.pk.seed, this.pk.root);
    }
}

