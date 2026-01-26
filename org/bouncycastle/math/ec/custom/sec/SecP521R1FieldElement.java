/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP521R1Field;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP521R1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"));
    protected int[] x;

    public SecP521R1FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP521R1FieldElement");
        }
        this.x = SecP521R1Field.fromBigInteger(bigInteger);
    }

    public SecP521R1FieldElement() {
        this.x = Nat.create(17);
    }

    protected SecP521R1FieldElement(int[] nArray) {
        this.x = nArray;
    }

    @Override
    public boolean isZero() {
        return Nat.isZero(17, this.x);
    }

    @Override
    public boolean isOne() {
        return Nat.isOne(17, this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat.toBigInteger(17, this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP521R1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        int[] nArray = Nat.create(17);
        SecP521R1Field.add(this.x, ((SecP521R1FieldElement)eCFieldElement).x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement addOne() {
        int[] nArray = Nat.create(17);
        SecP521R1Field.addOne(this.x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        int[] nArray = Nat.create(17);
        SecP521R1Field.subtract(this.x, ((SecP521R1FieldElement)eCFieldElement).x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        int[] nArray = Nat.create(17);
        SecP521R1Field.multiply(this.x, ((SecP521R1FieldElement)eCFieldElement).x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        int[] nArray = Nat.create(17);
        SecP521R1Field.inv(((SecP521R1FieldElement)eCFieldElement).x, nArray);
        SecP521R1Field.multiply(nArray, this.x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement negate() {
        int[] nArray = Nat.create(17);
        SecP521R1Field.negate(this.x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement square() {
        int[] nArray = Nat.create(17);
        SecP521R1Field.square(this.x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement invert() {
        int[] nArray = Nat.create(17);
        SecP521R1Field.inv(this.x, nArray);
        return new SecP521R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] nArray = this.x;
        if (Nat.isZero(17, nArray) || Nat.isOne(17, nArray)) {
            return this;
        }
        int[] nArray2 = Nat.create(33);
        int[] nArray3 = Nat.create(17);
        int[] nArray4 = Nat.create(17);
        SecP521R1Field.squareN(nArray, 519, nArray3, nArray2);
        SecP521R1Field.square(nArray3, nArray4, nArray2);
        return Nat.eq(17, nArray, nArray4) ? new SecP521R1FieldElement(nArray3) : null;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof SecP521R1FieldElement)) {
            return false;
        }
        SecP521R1FieldElement secP521R1FieldElement = (SecP521R1FieldElement)object;
        return Nat.eq(17, this.x, secP521R1FieldElement.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 17);
    }
}

