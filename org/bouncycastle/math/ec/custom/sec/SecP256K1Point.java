/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Field;
import org.bouncycastle.math.ec.custom.sec.SecP256K1FieldElement;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SecP256K1Point
extends ECPoint.AbstractFp {
    SecP256K1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        super(eCCurve, eCFieldElement, eCFieldElement2);
    }

    SecP256K1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArray) {
        super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArray);
    }

    @Override
    protected ECPoint detach() {
        return new SecP256K1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
    }

    @Override
    public ECPoint add(ECPoint eCPoint) {
        int[] nArray;
        int[] nArray2;
        int[] nArray3;
        int[] nArray4;
        if (this.isInfinity()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return this;
        }
        if (this == eCPoint) {
            return this.twice();
        }
        ECCurve eCCurve = this.getCurve();
        SecP256K1FieldElement secP256K1FieldElement = (SecP256K1FieldElement)this.x;
        SecP256K1FieldElement secP256K1FieldElement2 = (SecP256K1FieldElement)this.y;
        SecP256K1FieldElement secP256K1FieldElement3 = (SecP256K1FieldElement)eCPoint.getXCoord();
        SecP256K1FieldElement secP256K1FieldElement4 = (SecP256K1FieldElement)eCPoint.getYCoord();
        SecP256K1FieldElement secP256K1FieldElement5 = (SecP256K1FieldElement)this.zs[0];
        SecP256K1FieldElement secP256K1FieldElement6 = (SecP256K1FieldElement)eCPoint.getZCoord(0);
        int[] nArray5 = Nat256.createExt();
        int[] nArray6 = Nat256.createExt();
        int[] nArray7 = Nat256.create();
        int[] nArray8 = Nat256.create();
        int[] nArray9 = Nat256.create();
        boolean bl = secP256K1FieldElement5.isOne();
        if (bl) {
            nArray4 = secP256K1FieldElement3.x;
            nArray3 = secP256K1FieldElement4.x;
        } else {
            nArray3 = nArray8;
            SecP256K1Field.square(secP256K1FieldElement5.x, nArray3, nArray5);
            nArray4 = nArray7;
            SecP256K1Field.multiply(nArray3, secP256K1FieldElement3.x, nArray4, nArray5);
            SecP256K1Field.multiply(nArray3, secP256K1FieldElement5.x, nArray3, nArray5);
            SecP256K1Field.multiply(nArray3, secP256K1FieldElement4.x, nArray3, nArray5);
        }
        boolean bl2 = secP256K1FieldElement6.isOne();
        if (bl2) {
            nArray2 = secP256K1FieldElement.x;
            nArray = secP256K1FieldElement2.x;
        } else {
            nArray = nArray9;
            SecP256K1Field.square(secP256K1FieldElement6.x, nArray, nArray5);
            nArray2 = nArray6;
            SecP256K1Field.multiply(nArray, secP256K1FieldElement.x, nArray2, nArray5);
            SecP256K1Field.multiply(nArray, secP256K1FieldElement6.x, nArray, nArray5);
            SecP256K1Field.multiply(nArray, secP256K1FieldElement2.x, nArray, nArray5);
        }
        int[] nArray10 = Nat256.create();
        SecP256K1Field.subtract(nArray2, nArray4, nArray10);
        int[] nArray11 = nArray7;
        SecP256K1Field.subtract(nArray, nArray3, nArray11);
        if (Nat256.isZero(nArray10)) {
            if (Nat256.isZero(nArray11)) {
                return this.twice();
            }
            return eCCurve.getInfinity();
        }
        int[] nArray12 = nArray8;
        SecP256K1Field.square(nArray10, nArray12, nArray5);
        int[] nArray13 = Nat256.create();
        SecP256K1Field.multiply(nArray12, nArray10, nArray13, nArray5);
        int[] nArray14 = nArray8;
        SecP256K1Field.multiply(nArray12, nArray2, nArray14, nArray5);
        SecP256K1Field.negate(nArray13, nArray13);
        Nat256.mul(nArray, nArray13, nArray6);
        int n = Nat256.addBothTo(nArray14, nArray14, nArray13);
        SecP256K1Field.reduce32(n, nArray13);
        SecP256K1FieldElement secP256K1FieldElement7 = new SecP256K1FieldElement(nArray9);
        SecP256K1Field.square(nArray11, secP256K1FieldElement7.x, nArray5);
        SecP256K1Field.subtract(secP256K1FieldElement7.x, nArray13, secP256K1FieldElement7.x);
        SecP256K1FieldElement secP256K1FieldElement8 = new SecP256K1FieldElement(nArray13);
        SecP256K1Field.subtract(nArray14, secP256K1FieldElement7.x, secP256K1FieldElement8.x);
        SecP256K1Field.multiplyAddToExt(secP256K1FieldElement8.x, nArray11, nArray6);
        SecP256K1Field.reduce(nArray6, secP256K1FieldElement8.x);
        SecP256K1FieldElement secP256K1FieldElement9 = new SecP256K1FieldElement(nArray10);
        if (!bl) {
            SecP256K1Field.multiply(secP256K1FieldElement9.x, secP256K1FieldElement5.x, secP256K1FieldElement9.x, nArray5);
        }
        if (!bl2) {
            SecP256K1Field.multiply(secP256K1FieldElement9.x, secP256K1FieldElement6.x, secP256K1FieldElement9.x, nArray5);
        }
        ECFieldElement[] eCFieldElementArray = new ECFieldElement[]{secP256K1FieldElement9};
        return new SecP256K1Point(eCCurve, secP256K1FieldElement7, secP256K1FieldElement8, eCFieldElementArray);
    }

    @Override
    public ECPoint twice() {
        if (this.isInfinity()) {
            return this;
        }
        ECCurve eCCurve = this.getCurve();
        SecP256K1FieldElement secP256K1FieldElement = (SecP256K1FieldElement)this.y;
        if (secP256K1FieldElement.isZero()) {
            return eCCurve.getInfinity();
        }
        SecP256K1FieldElement secP256K1FieldElement2 = (SecP256K1FieldElement)this.x;
        SecP256K1FieldElement secP256K1FieldElement3 = (SecP256K1FieldElement)this.zs[0];
        int[] nArray = Nat256.createExt();
        int[] nArray2 = Nat256.create();
        SecP256K1Field.square(secP256K1FieldElement.x, nArray2, nArray);
        int[] nArray3 = Nat256.create();
        SecP256K1Field.square(nArray2, nArray3, nArray);
        int[] nArray4 = Nat256.create();
        SecP256K1Field.square(secP256K1FieldElement2.x, nArray4, nArray);
        int n = Nat256.addBothTo(nArray4, nArray4, nArray4);
        SecP256K1Field.reduce32(n, nArray4);
        int[] nArray5 = nArray2;
        SecP256K1Field.multiply(nArray2, secP256K1FieldElement2.x, nArray5, nArray);
        n = Nat.shiftUpBits(8, nArray5, 2, 0);
        SecP256K1Field.reduce32(n, nArray5);
        int[] nArray6 = Nat256.create();
        n = Nat.shiftUpBits(8, nArray3, 3, 0, nArray6);
        SecP256K1Field.reduce32(n, nArray6);
        SecP256K1FieldElement secP256K1FieldElement4 = new SecP256K1FieldElement(nArray3);
        SecP256K1Field.square(nArray4, secP256K1FieldElement4.x, nArray);
        SecP256K1Field.subtract(secP256K1FieldElement4.x, nArray5, secP256K1FieldElement4.x);
        SecP256K1Field.subtract(secP256K1FieldElement4.x, nArray5, secP256K1FieldElement4.x);
        SecP256K1FieldElement secP256K1FieldElement5 = new SecP256K1FieldElement(nArray5);
        SecP256K1Field.subtract(nArray5, secP256K1FieldElement4.x, secP256K1FieldElement5.x);
        SecP256K1Field.multiply(secP256K1FieldElement5.x, nArray4, secP256K1FieldElement5.x, nArray);
        SecP256K1Field.subtract(secP256K1FieldElement5.x, nArray6, secP256K1FieldElement5.x);
        SecP256K1FieldElement secP256K1FieldElement6 = new SecP256K1FieldElement(nArray4);
        SecP256K1Field.twice(secP256K1FieldElement.x, secP256K1FieldElement6.x);
        if (!secP256K1FieldElement3.isOne()) {
            SecP256K1Field.multiply(secP256K1FieldElement6.x, secP256K1FieldElement3.x, secP256K1FieldElement6.x, nArray);
        }
        return new SecP256K1Point(eCCurve, secP256K1FieldElement4, secP256K1FieldElement5, new ECFieldElement[]{secP256K1FieldElement6});
    }

    @Override
    public ECPoint twicePlus(ECPoint eCPoint) {
        if (this == eCPoint) {
            return this.threeTimes();
        }
        if (this.isInfinity()) {
            return eCPoint;
        }
        if (eCPoint.isInfinity()) {
            return this.twice();
        }
        ECFieldElement eCFieldElement = this.y;
        if (eCFieldElement.isZero()) {
            return eCPoint;
        }
        return this.twice().add(eCPoint);
    }

    @Override
    public ECPoint threeTimes() {
        if (this.isInfinity() || this.y.isZero()) {
            return this;
        }
        return this.twice().add(this);
    }

    @Override
    public ECPoint negate() {
        if (this.isInfinity()) {
            return this;
        }
        return new SecP256K1Point(this.curve, this.x, this.y.negate(), this.zs);
    }
}

