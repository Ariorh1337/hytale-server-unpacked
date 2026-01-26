/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.pqc.crypto.picnic.PicnicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.util.Arrays;

public class PicnicPrivateKeyParameters
extends PicnicKeyParameters {
    private final byte[] privateKey;

    public PicnicPrivateKeyParameters(PicnicParameters picnicParameters, byte[] byArray) {
        super(true, picnicParameters);
        this.privateKey = Arrays.clone(byArray);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.privateKey);
    }
}

