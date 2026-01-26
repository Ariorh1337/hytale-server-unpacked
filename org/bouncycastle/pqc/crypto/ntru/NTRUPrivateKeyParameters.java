/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.crypto.ntru.NTRUKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.util.Arrays;

public class NTRUPrivateKeyParameters
extends NTRUKeyParameters {
    final byte[] privateKey;

    public NTRUPrivateKeyParameters(NTRUParameters nTRUParameters, byte[] byArray) {
        super(true, nTRUParameters);
        this.privateKey = Arrays.clone(byArray);
    }

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public byte[] getEncoded() {
        return this.getPrivateKey();
    }
}

