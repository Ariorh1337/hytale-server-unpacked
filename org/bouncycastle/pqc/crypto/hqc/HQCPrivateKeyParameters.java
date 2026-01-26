/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.pqc.crypto.hqc.HQCKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.util.Arrays;

public class HQCPrivateKeyParameters
extends HQCKeyParameters {
    private final byte[] sk;

    public HQCPrivateKeyParameters(HQCParameters hQCParameters, byte[] byArray) {
        super(true, hQCParameters);
        this.sk = Arrays.clone(byArray);
    }

    public byte[] getPrivateKey() {
        return Arrays.clone(this.sk);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.sk);
    }
}

