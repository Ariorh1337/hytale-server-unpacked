/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.crypto.ntru.NTRUKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.util.Arrays;

public class NTRUPublicKeyParameters
extends NTRUKeyParameters {
    final byte[] publicKey;

    public NTRUPublicKeyParameters(NTRUParameters nTRUParameters, byte[] byArray) {
        super(false, nTRUParameters);
        this.publicKey = Arrays.clone(byArray);
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return this.getPublicKey();
    }
}

