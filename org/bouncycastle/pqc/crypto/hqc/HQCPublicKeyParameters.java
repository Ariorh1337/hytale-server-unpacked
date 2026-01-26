/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.pqc.crypto.hqc.HQCKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.util.Arrays;

public class HQCPublicKeyParameters
extends HQCKeyParameters {
    private final byte[] pk;

    public HQCPublicKeyParameters(HQCParameters hQCParameters, byte[] byArray) {
        super(true, hQCParameters);
        this.pk = Arrays.clone(byArray);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.pk);
    }

    public byte[] getEncoded() {
        return this.getPublicKey();
    }
}

