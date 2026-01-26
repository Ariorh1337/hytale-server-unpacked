/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;

public class MLDSAKeyParameters
extends AsymmetricKeyParameter {
    private final MLDSAParameters params;

    public MLDSAKeyParameters(boolean bl, MLDSAParameters mLDSAParameters) {
        super(bl);
        this.params = mLDSAParameters;
    }

    public MLDSAParameters getParameters() {
        return this.params;
    }
}

