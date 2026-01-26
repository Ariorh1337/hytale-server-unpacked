/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.bike;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.bike.BIKEParameters;

public class BIKEKeyParameters
extends AsymmetricKeyParameter {
    private BIKEParameters params;

    public BIKEKeyParameters(boolean bl, BIKEParameters bIKEParameters) {
        super(bl);
        this.params = bIKEParameters;
    }

    public BIKEParameters getParameters() {
        return this.params;
    }
}

