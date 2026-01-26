/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.pqc.crypto.frodo.FrodoKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.util.Arrays;

public class FrodoPrivateKeyParameters
extends FrodoKeyParameters {
    private byte[] privateKey;

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public FrodoPrivateKeyParameters(FrodoParameters frodoParameters, byte[] byArray) {
        super(true, frodoParameters);
        this.privateKey = Arrays.clone(byArray);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.privateKey);
    }
}

