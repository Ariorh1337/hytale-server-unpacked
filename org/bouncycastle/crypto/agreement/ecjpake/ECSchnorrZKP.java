/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

public class ECSchnorrZKP {
    private final ECPoint V;
    private final BigInteger r;

    ECSchnorrZKP(ECPoint eCPoint, BigInteger bigInteger) {
        this.V = eCPoint;
        this.r = bigInteger;
    }

    public ECPoint getV() {
        return this.V;
    }

    public BigInteger getr() {
        return this.r;
    }
}

