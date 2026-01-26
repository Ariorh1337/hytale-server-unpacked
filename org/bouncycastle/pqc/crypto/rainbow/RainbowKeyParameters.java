/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;

public class RainbowKeyParameters
extends AsymmetricKeyParameter {
    private final RainbowParameters params;
    private final int docLength;

    public RainbowKeyParameters(boolean bl, RainbowParameters rainbowParameters) {
        super(bl);
        this.params = rainbowParameters;
        this.docLength = rainbowParameters.getM();
    }

    public RainbowParameters getParameters() {
        return this.params;
    }

    public int getDocLength() {
        return this.docLength;
    }
}

