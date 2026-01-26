/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.snova.SnovaParameters;
import org.bouncycastle.util.Arrays;

public class SnovaPublicKeyParameters
extends AsymmetricKeyParameter {
    private final byte[] publicKey;
    private final SnovaParameters parameters;

    public SnovaPublicKeyParameters(SnovaParameters snovaParameters, byte[] byArray) {
        super(false);
        this.publicKey = Arrays.clone(byArray);
        this.parameters = snovaParameters;
    }

    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }

    public byte[] getEncoded() {
        return Arrays.clone(this.publicKey);
    }

    public SnovaParameters getParameters() {
        return this.parameters;
    }
}

