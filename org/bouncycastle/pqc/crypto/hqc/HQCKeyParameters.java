/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;

public class HQCKeyParameters
extends AsymmetricKeyParameter {
    private final HQCParameters params;

    public HQCKeyParameters(boolean bl, HQCParameters hQCParameters) {
        super(bl);
        this.params = hQCParameters;
    }

    public HQCParameters getParameters() {
        return this.params;
    }
}

