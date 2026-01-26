/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.pqc.crypto.sphincsplus.PK;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusKeyParameters;
import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusParameters;
import org.bouncycastle.util.Arrays;

public class SPHINCSPlusPublicKeyParameters
extends SPHINCSPlusKeyParameters {
    private final PK pk;

    public SPHINCSPlusPublicKeyParameters(SPHINCSPlusParameters sPHINCSPlusParameters, byte[] byArray) {
        super(false, sPHINCSPlusParameters);
        int n = sPHINCSPlusParameters.getN();
        if (byArray.length != 2 * n) {
            throw new IllegalArgumentException("public key encoding does not match parameters");
        }
        this.pk = new PK(Arrays.copyOfRange(byArray, 0, n), Arrays.copyOfRange(byArray, n, 2 * n));
    }

    SPHINCSPlusPublicKeyParameters(SPHINCSPlusParameters sPHINCSPlusParameters, PK pK) {
        super(false, sPHINCSPlusParameters);
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

