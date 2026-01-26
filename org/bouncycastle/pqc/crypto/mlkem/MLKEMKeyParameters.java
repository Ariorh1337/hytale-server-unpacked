/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;

public class MLKEMKeyParameters
extends AsymmetricKeyParameter {
    private MLKEMParameters params;

    public MLKEMKeyParameters(boolean bl, MLKEMParameters mLKEMParameters) {
        super(bl);
        this.params = mLKEMParameters;
    }

    public MLKEMParameters getParameters() {
        return this.params;
    }
}

