/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.pqc.crypto.bike.BIKEKeyParameters;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;
import org.bouncycastle.util.Arrays;

public class BIKEPublicKeyParameters
extends BIKEKeyParameters {
    byte[] publicKey;

    public BIKEPublicKeyParameters(BIKEParameters bIKEParameters, byte[] byArray) {
        super(false, bIKEParameters);
        this.publicKey = Arrays.clone(byArray);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.publicKey);
    }
}

