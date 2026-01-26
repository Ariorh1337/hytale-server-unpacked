/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.ECPoint;

public class ECPair {
    private final ECPoint x;
    private final ECPoint y;

    public ECPair(ECPoint eCPoint, ECPoint eCPoint2) {
        this.x = eCPoint;
        this.y = eCPoint2;
    }

    public ECPoint getX() {
        return this.x;
    }

    public ECPoint getY() {
        return this.y;
    }

    public boolean equals(ECPair eCPair) {
        return eCPair.getX().equals(this.getX()) && eCPair.getY().equals(this.getY());
    }

    public boolean equals(Object object) {
        return object instanceof ECPair ? this.equals((ECPair)object) : false;
    }

    public int hashCode() {
        return this.x.hashCode() + 37 * this.y.hashCode();
    }
}

