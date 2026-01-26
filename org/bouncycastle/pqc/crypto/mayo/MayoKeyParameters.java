/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;

public class MayoKeyParameters
extends AsymmetricKeyParameter {
    private final MayoParameters params;

    public MayoKeyParameters(boolean bl, MayoParameters mayoParameters) {
        super(bl);
        this.params = mayoParameters;
    }

    public MayoParameters getParameters() {
        return this.params;
    }
}

