/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Field;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP256R1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF"));
    protected int[] x;

    public SecP256R1FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256R1FieldElement");
        }
        this.x = SecP256R1Field.fromBigInteger(bigInteger);
    }

    public SecP256R1FieldElement() {
        this.x = Nat256.create();
    }

    protected SecP256R1FieldElement(int[] nArray) {
        this.x = nArray;
    }

    @Override
    public boolean isZero() {
        return Nat256.isZero(this.x);
    }

    @Override
    public boolean isOne() {
        return Nat256.isOne(this.x);
    }

    @Override
    public boolean testBitZero() {
        return Nat256.getBit(this.x, 0) == 1;
    }

    @Override
    public BigInteger toBigInteger() {
        return Nat256.toBigInteger(this.x);
    }

    @Override
    public String getFieldName() {
        return "SecP256R1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256R1Field.add(this.x, ((SecP256R1FieldElement)eCFieldElement).x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement addOne() {
        int[] nArray = Nat256.create();
        SecP256R1Field.addOne(this.x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256R1Field.subtract(this.x, ((SecP256R1FieldElement)eCFieldElement).x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256R1Field.multiply(this.x, ((SecP256R1FieldElement)eCFieldElement).x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256R1Field.inv(((SecP256R1FieldElement)eCFieldElement).x, nArray);
        SecP256R1Field.multiply(nArray, this.x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement negate() {
        int[] nArray = Nat256.create();
        SecP256R1Field.negate(this.x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement square() {
        int[] nArray = Nat256.create();
        SecP256R1Field.square(this.x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement invert() {
        int[] nArray = Nat256.create();
        SecP256R1Field.inv(this.x, nArray);
        return new SecP256R1FieldElement(nArray);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] nArray = this.x;
        if (Nat256.isZero(nArray) || Nat256.isOne(nArray)) {
            return this;
        }
        int[] nArray2 = Nat256.createExt();
        int[] nArray3 = Nat256.create();
        int[] nArray4 = Nat256.create();
        SecP256R1Field.square(nArray, nArray3, nArray2);
        SecP256R1Field.multiply(nArray3, nArray, nArray3, nArray2);
        SecP256R1Field.squareN(nArray3, 2, nArray4, nArray2);
        SecP256R1Field.multiply(nArray4, nArray3, nArray4, nArray2);
        SecP256R1Field.squareN(nArray4, 4, nArray3, nArray2);
        SecP256R1Field.multiply(nArray3, nArray4, nArray3, nArray2);
        SecP256R1Field.squareN(nArray3, 8, nArray4, nArray2);
        SecP256R1Field.multiply(nArray4, nArray3, nArray4, nArray2);
        SecP256R1Field.squareN(nArray4, 16, nArray3, nArray2);
        SecP256R1Field.multiply(nArray3, nArray4, nArray3, nArray2);
        SecP256R1Field.squareN(nArray3, 32, nArray3, nArray2);
        SecP256R1Field.multiply(nArray3, nArray, nArray3, nArray2);
        SecP256R1Field.squareN(nArray3, 96, nArray3, nArray2);
        SecP256R1Field.multiply(nArray3, nArray, nArray3, nArray2);
        SecP256R1Field.squareN(nArray3, 94, nArray3, nArray2);
        SecP256R1Field.square(nArray3, nArray4, nArray2);
        return Nat256.eq(nArray, nArray4) ? new SecP256R1FieldElement(nArray3) : null;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof SecP256R1FieldElement)) {
            return false;
        }
        SecP256R1FieldElement secP256R1FieldElement = (SecP256R1FieldElement)object;
        return Nat256.eq(this.x, secP256R1FieldElement.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
}

