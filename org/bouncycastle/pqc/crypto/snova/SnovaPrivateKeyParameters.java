/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.util.Arrays;

public class SnovaPrivateKeyParameters
extends AsymmetricKeyParameter {
    private final byte[] privateKey;
    private final SnovaParameters parameters;

    public SnovaPrivateKeyParameters(SnovaParameters snovaParameters, byte[] byArray) {
        super(true);
        this.privateKey = Arrays.clone(byArray);
        this.parameters = snovaParameters;
    }

    public byte[] getPrivateKey() {
        return Arrays.clone(this.privateKey);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.privateKey);
    }

    public SnovaParameters getParameters() {
        return this.parameters;
    }
}

