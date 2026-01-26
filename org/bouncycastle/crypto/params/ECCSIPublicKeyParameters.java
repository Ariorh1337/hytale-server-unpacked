/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.math.ec.ECPoint;

public class ECCSIPublicKeyParameters
extends AsymmetricKeyParameter {
    private final ECPoint pvt;

    public ECCSIPublicKeyParameters(ECPoint eCPoint) {
        super(false);
        this.pvt = eCPoint;
    }

    public final ECPoint getPVT() {
        return this.pvt;
    }
}

