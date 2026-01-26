/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;

public class FrodoKeyParameters
extends AsymmetricKeyParameter {
    private FrodoParameters params;

    public FrodoKeyParameters(boolean bl, FrodoParameters frodoParameters) {
        super(bl);
        this.params = frodoParameters;
    }

    public FrodoParameters getParameters() {
        return this.params;
    }
}

