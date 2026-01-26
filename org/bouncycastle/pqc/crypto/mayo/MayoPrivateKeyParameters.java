/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.pqc.crypto.mayo.MayoKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.util.Arrays;

public class MayoPrivateKeyParameters
extends MayoKeyParameters {
    private final byte[] seed_sk;

    public MayoPrivateKeyParameters(MayoParameters mayoParameters, byte[] byArray) {
        super(true, mayoParameters);
        this.seed_sk = Arrays.clone(byArray);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.seed_sk);
    }

    public byte[] getSeedSk() {
        return Arrays.clone(this.seed_sk);
    }
}

