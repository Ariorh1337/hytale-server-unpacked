/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.field;

import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.util.Arrays;

class GF2Polynomial
implements Polynomial {
    protected final int[] exponents;

    GF2Polynomial(int[] nArray) {
        this.exponents = Arrays.clone(nArray);
    }

    @Override
    public int getDegree() {
        return this.exponents[this.exponents.length - 1];
    }

    @Override
    public int[] getExponentsPresent() {
        return Arrays.clone(this.exponents);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof GF2Polynomial)) {
            return false;
        }
        GF2Polynomial gF2Polynomial = (GF2Polynomial)object;
        return Arrays.areEqual(this.exponents, gF2Polynomial.exponents);
    }

    public int hashCode() {
        return Arrays.hashCode(this.exponents);
    }
}

