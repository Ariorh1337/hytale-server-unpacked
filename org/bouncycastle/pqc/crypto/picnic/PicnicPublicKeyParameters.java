/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.pqc.crypto.picnic.PicnicKeyParameters;
import org.bouncycastle.pqc.crypto.picnic.PicnicParameters;
import org.bouncycastle.util.Arrays;

public class PicnicPublicKeyParameters
extends PicnicKeyParameters {
    private final byte[] publicKey;

    public PicnicPublicKeyParameters(PicnicParameters picnicParameters, byte[] byArray) {
        super(false, picnicParameters);
        this.publicKey = Arrays.clone(byArray);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.publicKey);
    }
}

