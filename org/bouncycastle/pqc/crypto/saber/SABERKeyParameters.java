/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;

public class SABERKeyParameters
extends AsymmetricKeyParameter {
    private SABERParameters params;

    public SABERKeyParameters(boolean bl, SABERParameters sABERParameters) {
        super(bl);
        this.params = sABERParameters;
    }

    public SABERParameters getParameters() {
        return this.params;
    }
}

