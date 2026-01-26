/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.pqc.crypto.saber.SABERKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.util.Arrays;

public class SABERPublicKeyParameters
extends SABERKeyParameters {
    private final byte[] publicKey;

    public SABERPublicKeyParameters(SABERParameters sABERParameters, byte[] byArray) {
        super(false, sABERParameters);
        this.publicKey = Arrays.clone(byArray);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return this.getPublicKey();
    }
}

