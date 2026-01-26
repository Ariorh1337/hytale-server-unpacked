/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;

public abstract class NTRUKeyParameters
extends AsymmetricKeyParameter {
    private final NTRUParameters params;

    NTRUKeyParameters(boolean bl, NTRUParameters nTRUParameters) {
        super(bl);
        this.params = nTRUParameters;
    }

    public NTRUParameters getParameters() {
        return this.params;
    }
}

