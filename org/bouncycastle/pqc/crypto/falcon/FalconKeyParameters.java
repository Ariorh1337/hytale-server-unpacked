/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.falcon;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.falcon.FalconParameters;

public class FalconKeyParameters
extends AsymmetricKeyParameter {
    private final FalconParameters params;

    public FalconKeyParameters(boolean bl, FalconParameters falconParameters) {
        super(bl);
        this.params = falconParameters;
    }

    public FalconParameters getParameters() {
        return this.params;
    }
}

