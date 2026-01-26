/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Field;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP256K1FieldElement
extends ECFieldElement.AbstractFp {
    public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F"));
    protected int[] x;

    public SecP256K1FieldElement(BigInteger bigInteger) {
        if (bigInteger == null || bigInteger.signum() < 0 || bigInteger.compareTo(Q) >= 0) {
            throw new IllegalArgumentException("x value invalid for SecP256K1FieldElement");
        }
        this.x = SecP256K1Field.fromBigInteger(bigInteger);
    }

    public SecP256K1FieldElement() {
        this.x = Nat256.create();
    }

    protected SecP256K1FieldElement(int[] nArray) {
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
        return "SecP256K1Field";
    }

    @Override
    public int getFieldSize() {
        return Q.bitLength();
    }

    @Override
    public ECFieldElement add(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256K1Field.add(this.x, ((SecP256K1FieldElement)eCFieldElement).x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement addOne() {
        int[] nArray = Nat256.create();
        SecP256K1Field.addOne(this.x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement subtract(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256K1Field.subtract(this.x, ((SecP256K1FieldElement)eCFieldElement).x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement multiply(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256K1Field.multiply(this.x, ((SecP256K1FieldElement)eCFieldElement).x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement divide(ECFieldElement eCFieldElement) {
        int[] nArray = Nat256.create();
        SecP256K1Field.inv(((SecP256K1FieldElement)eCFieldElement).x, nArray);
        SecP256K1Field.multiply(nArray, this.x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement negate() {
        int[] nArray = Nat256.create();
        SecP256K1Field.negate(this.x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement square() {
        int[] nArray = Nat256.create();
        SecP256K1Field.square(this.x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement invert() {
        int[] nArray = Nat256.create();
        SecP256K1Field.inv(this.x, nArray);
        return new SecP256K1FieldElement(nArray);
    }

    @Override
    public ECFieldElement sqrt() {
        int[] nArray = this.x;
        if (Nat256.isZero(nArray) || Nat256.isOne(nArray)) {
            return this;
        }
        int[] nArray2 = Nat256.createExt();
        int[] nArray3 = Nat256.create();
        SecP256K1Field.square(nArray, nArray3, nArray2);
        SecP256K1Field.multiply(nArray3, nArray, nArray3, nArray2);
        int[] nArray4 = Nat256.create();
        SecP256K1Field.square(nArray3, nArray4, nArray2);
        SecP256K1Field.multiply(nArray4, nArray, nArray4, nArray2);
        int[] nArray5 = Nat256.create();
        SecP256K1Field.squareN(nArray4, 3, nArray5, nArray2);
        SecP256K1Field.multiply(nArray5, nArray4, nArray5, nArray2);
        int[] nArray6 = nArray5;
        SecP256K1Field.squareN(nArray5, 3, nArray6, nArray2);
        SecP256K1Field.multiply(nArray6, nArray4, nArray6, nArray2);
        int[] nArray7 = nArray6;
        SecP256K1Field.squareN(nArray6, 2, nArray7, nArray2);
        SecP256K1Field.multiply(nArray7, nArray3, nArray7, nArray2);
        int[] nArray8 = Nat256.create();
        SecP256K1Field.squareN(nArray7, 11, nArray8, nArray2);
        SecP256K1Field.multiply(nArray8, nArray7, nArray8, nArray2);
        int[] nArray9 = nArray7;
        SecP256K1Field.squareN(nArray8, 22, nArray9, nArray2);
        SecP256K1Field.multiply(nArray9, nArray8, nArray9, nArray2);
        int[] nArray10 = Nat256.create();
        SecP256K1Field.squareN(nArray9, 44, nArray10, nArray2);
        SecP256K1Field.multiply(nArray10, nArray9, nArray10, nArray2);
        int[] nArray11 = Nat256.create();
        SecP256K1Field.squareN(nArray10, 88, nArray11, nArray2);
        SecP256K1Field.multiply(nArray11, nArray10, nArray11, nArray2);
        int[] nArray12 = nArray10;
        SecP256K1Field.squareN(nArray11, 44, nArray12, nArray2);
        SecP256K1Field.multiply(nArray12, nArray9, nArray12, nArray2);
        int[] nArray13 = nArray9;
        SecP256K1Field.squareN(nArray12, 3, nArray13, nArray2);
        SecP256K1Field.multiply(nArray13, nArray4, nArray13, nArray2);
        int[] nArray14 = nArray13;
        SecP256K1Field.squareN(nArray14, 23, nArray14, nArray2);
        SecP256K1Field.multiply(nArray14, nArray8, nArray14, nArray2);
        SecP256K1Field.squareN(nArray14, 6, nArray14, nArray2);
        SecP256K1Field.multiply(nArray14, nArray3, nArray14, nArray2);
        SecP256K1Field.squareN(nArray14, 2, nArray14, nArray2);
        int[] nArray15 = nArray3;
        SecP256K1Field.square(nArray14, nArray15, nArray2);
        return Nat256.eq(nArray, nArray15) ? new SecP256K1FieldElement(nArray14) : null;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof SecP256K1FieldElement)) {
            return false;
        }
        SecP256K1FieldElement secP256K1FieldElement = (SecP256K1FieldElement)object;
        return Nat256.eq(this.x, secP256K1FieldElement.x);
    }

    public int hashCode() {
        return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
    }
}

