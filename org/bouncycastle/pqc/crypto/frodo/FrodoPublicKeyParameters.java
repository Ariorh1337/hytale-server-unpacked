/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.pqc.crypto.frodo.FrodoKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.util.Arrays;

public class FrodoPublicKeyParameters
extends FrodoKeyParameters {
    public byte[] publicKey;

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return this.getPublicKey();
    }

    public FrodoPublicKeyParameters(FrodoParameters frodoParameters, byte[] byArray) {
        super(false, frodoParameters);
        this.publicKey = Arrays.clone(byArray);
    }
}

