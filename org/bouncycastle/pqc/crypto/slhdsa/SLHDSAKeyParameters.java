/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;

public class SLHDSAKeyParameters
extends AsymmetricKeyParameter {
    private final SLHDSAParameters parameters;

    protected SLHDSAKeyParameters(boolean bl, SLHDSAParameters sLHDSAParameters) {
        super(bl);
        this.parameters = sLHDSAParameters;
    }

    public SLHDSAParameters getParameters() {
        return this.parameters;
    }
}

