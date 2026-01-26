/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.pqc.crypto.mayo.MayoKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.util.Arrays;

public class MayoPublicKeyParameters
extends MayoKeyParameters {
    private final byte[] p;

    public MayoPublicKeyParameters(MayoParameters mayoParameters, byte[] byArray) {
        super(false, mayoParameters);
        this.p = Arrays.clone(byArray);
    }

    public byte[] getP() {
        return Arrays.clone(this.p);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.p);
    }
}

