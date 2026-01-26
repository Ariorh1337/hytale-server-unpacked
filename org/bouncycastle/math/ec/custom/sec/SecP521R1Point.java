/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP521R1Field;
import org.bouncycastle.math.ec.custom.sec.SecP521R1FieldElement;
import org.bouncycastle.math.raw.Nat;

public class SecP521R1Point
extends ECPoint.AbstractFp {
    SecP521R1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2) {
        super(eCCurve, eCFieldElement, eCFieldElement2);
    }

    SecP521R1Point(ECCurve eCCurve, ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement[] eCFieldElementArray) {
        super(eCCurve, eCFieldElement, eCFieldElement2, eCFieldElementArray);
    }

    @Override
    protected ECPoint detach() {
        return new SecP521R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
        SecP521R1FieldElement secP521R1FieldElement = (SecP521R1FieldElement)this.x;
        SecP521R1FieldElement secP521R1FieldElement2 = (SecP521R1FieldElement)this.y;
        SecP521R1FieldElement secP521R1FieldElement3 = (SecP521R1FieldElement)eCPoint.getXCoord();
        SecP521R1FieldElement secP521R1FieldElement4 = (SecP521R1FieldElement)eCPoint.getYCoord();
        SecP521R1FieldElement secP521R1FieldElement5 = (SecP521R1FieldElement)this.zs[0];
        SecP521R1FieldElement secP521R1FieldElement6 = (SecP521R1FieldElement)eCPoint.getZCoord(0);
        int[] nArray5 = Nat.create(33);
        int[] nArray6 = Nat.create(17);
        int[] nArray7 = Nat.create(17);
        int[] nArray8 = Nat.create(17);
        int[] nArray9 = Nat.create(17);
        boolean bl = secP521R1FieldElement5.isOne();
        if (bl) {
            nArray4 = secP521R1FieldElement3.x;
            nArray3 = secP521R1FieldElement4.x;
        } else {
            nArray3 = nArray8;
            SecP521R1Field.square(secP521R1FieldElement5.x, nArray3, nArray5);
            nArray4 = nArray7;
            SecP521R1Field.multiply(nArray3, secP521R1FieldElement3.x, nArray4, nArray5);
            SecP521R1Field.multiply(nArray3, secP521R1FieldElement5.x, nArray3, nArray5);
            SecP521R1Field.multiply(nArray3, secP521R1FieldElement4.x, nArray3, nArray5);
        }
        boolean bl2 = secP521R1FieldElement6.isOne();
        if (bl2) {
            nArray2 = secP521R1FieldElement.x;
            nArray = secP521R1FieldElement2.x;
        } else {
            nArray = nArray9;
            SecP521R1Field.square(secP521R1FieldElement6.x, nArray, nArray5);
            nArray2 = nArray6;
            SecP521R1Field.multiply(nArray, secP521R1FieldElement.x, nArray2, nArray5);
            SecP521R1Field.multiply(nArray, secP521R1FieldElement6.x, nArray, nArray5);
            SecP521R1Field.multiply(nArray, secP521R1FieldElement2.x, nArray, nArray5);
        }
        int[] nArray10 = Nat.create(17);
        SecP521R1Field.subtract(nArray2, nArray4, nArray10);
        int[] nArray11 = nArray7;
        SecP521R1Field.subtract(nArray, nArray3, nArray11);
        if (Nat.isZero(17, nArray10)) {
            if (Nat.isZero(17, nArray11)) {
                return this.twice();
            }
            return eCCurve.getInfinity();
        }
        int[] nArray12 = nArray8;
        SecP521R1Field.square(nArray10, nArray12, nArray5);
        int[] nArray13 = Nat.create(17);
        SecP521R1Field.multiply(nArray12, nArray10, nArray13, nArray5);
        int[] nArray14 = nArray8;
        SecP521R1Field.multiply(nArray12, nArray2, nArray14, nArray5);
        SecP521R1Field.multiply(nArray, nArray13, nArray6, nArray5);
        SecP521R1FieldElement secP521R1FieldElement7 = new SecP521R1FieldElement(nArray9);
        SecP521R1Field.square(nArray11, secP521R1FieldElement7.x, nArray5);
        SecP521R1Field.add(secP521R1FieldElement7.x, nArray13, secP521R1FieldElement7.x);
        SecP521R1Field.subtract(secP521R1FieldElement7.x, nArray14, secP521R1FieldElement7.x);
        SecP521R1Field.subtract(secP521R1FieldElement7.x, nArray14, secP521R1FieldElement7.x);
        SecP521R1FieldElement secP521R1FieldElement8 = new SecP521R1FieldElement(nArray13);
        SecP521R1Field.subtract(nArray14, secP521R1FieldElement7.x, secP521R1FieldElement8.x);
        SecP521R1Field.multiply(secP521R1FieldElement8.x, nArray11, nArray7, nArray5);
        SecP521R1Field.subtract(nArray7, nArray6, secP521R1FieldElement8.x);
        SecP521R1FieldElement secP521R1FieldElement9 = new SecP521R1FieldElement(nArray10);
        if (!bl) {
            SecP521R1Field.multiply(secP521R1FieldElement9.x, secP521R1FieldElement5.x, secP521R1FieldElement9.x, nArray5);
        }
        if (!bl2) {
            SecP521R1Field.multiply(secP521R1FieldElement9.x, secP521R1FieldElement6.x, secP521R1FieldElement9.x, nArray5);
        }
        ECFieldElement[] eCFieldElementArray = new ECFieldElement[]{secP521R1FieldElement9};
        return new SecP521R1Point(eCCurve, secP521R1FieldElement7, secP521R1FieldElement8, eCFieldElementArray);
    }

    @Override
    public ECPoint twice() {
        if (this.isInfinity()) {
            return this;
        }
        ECCurve eCCurve = this.getCurve();
        SecP521R1FieldElement secP521R1FieldElement = (SecP521R1FieldElement)this.y;
        if (secP521R1FieldElement.isZero()) {
            return eCCurve.getInfinity();
        }
        SecP521R1FieldElement secP521R1FieldElement2 = (SecP521R1FieldElement)this.x;
        SecP521R1FieldElement secP521R1FieldElement3 = (SecP521R1FieldElement)this.zs[0];
        int[] nArray = Nat.create(33);
        int[] nArray2 = Nat.create(17);
        int[] nArray3 = Nat.create(17);
        int[] nArray4 = Nat.create(17);
        SecP521R1Field.square(secP521R1FieldElement.x, nArray4, nArray);
        int[] nArray5 = Nat.create(17);
        SecP521R1Field.square(nArray4, nArray5, nArray);
        boolean bl = secP521R1FieldElement3.isOne();
        int[] nArray6 = secP521R1FieldElement3.x;
        if (!bl) {
            nArray6 = nArray3;
            SecP521R1Field.square(secP521R1FieldElement3.x, nArray6, nArray);
        }
        SecP521R1Field.subtract(secP521R1FieldElement2.x, nArray6, nArray2);
        int[] nArray7 = nArray3;
        SecP521R1Field.add(secP521R1FieldElement2.x, nArray6, nArray7);
        SecP521R1Field.multiply(nArray7, nArray2, nArray7, nArray);
        Nat.addBothTo(17, nArray7, nArray7, nArray7);
        SecP521R1Field.reduce23(nArray7);
        int[] nArray8 = nArray4;
        SecP521R1Field.multiply(nArray4, secP521R1FieldElement2.x, nArray8, nArray);
        Nat.shiftUpBits(17, nArray8, 2, 0);
        SecP521R1Field.reduce23(nArray8);
        Nat.shiftUpBits(17, nArray5, 3, 0, nArray2);
        SecP521R1Field.reduce23(nArray2);
        SecP521R1FieldElement secP521R1FieldElement4 = new SecP521R1FieldElement(nArray5);
        SecP521R1Field.square(nArray7, secP521R1FieldElement4.x, nArray);
        SecP521R1Field.subtract(secP521R1FieldElement4.x, nArray8, secP521R1FieldElement4.x);
        SecP521R1Field.subtract(secP521R1FieldElement4.x, nArray8, secP521R1FieldElement4.x);
        SecP521R1FieldElement secP521R1FieldElement5 = new SecP521R1FieldElement(nArray8);
        SecP521R1Field.subtract(nArray8, secP521R1FieldElement4.x, secP521R1FieldElement5.x);
        SecP521R1Field.multiply(secP521R1FieldElement5.x, nArray7, secP521R1FieldElement5.x, nArray);
        SecP521R1Field.subtract(secP521R1FieldElement5.x, nArray2, secP521R1FieldElement5.x);
        SecP521R1FieldElement secP521R1FieldElement6 = new SecP521R1FieldElement(nArray7);
        SecP521R1Field.twice(secP521R1FieldElement.x, secP521R1FieldElement6.x);
        if (!bl) {
            SecP521R1Field.multiply(secP521R1FieldElement6.x, secP521R1FieldElement3.x, secP521R1FieldElement6.x, nArray);
        }
        return new SecP521R1Point(eCCurve, secP521R1FieldElement4, secP521R1FieldElement5, new ECFieldElement[]{secP521R1FieldElement6});
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

    protected ECFieldElement two(ECFieldElement eCFieldElement) {
        return eCFieldElement.add(eCFieldElement);
    }

    protected ECFieldElement three(ECFieldElement eCFieldElement) {
        return this.two(eCFieldElement).add(eCFieldElement);
    }

    protected ECFieldElement four(ECFieldElement eCFieldElement) {
        return this.two(this.two(eCFieldElement));
    }

    protected ECFieldElement eight(ECFieldElement eCFieldElement) {
        return this.four(this.two(eCFieldElement));
    }

    protected ECFieldElement doubleProductFromSquares(ECFieldElement eCFieldElement, ECFieldElement eCFieldElement2, ECFieldElement eCFieldElement3, ECFieldElement eCFieldElement4) {
        return eCFieldElement.add(eCFieldElement2).square().subtract(eCFieldElement3).subtract(eCFieldElement4);
    }

    @Override
    public ECPoint negate() {
        if (this.isInfinity()) {
            return this;
        }
        return new SecP521R1Point(this.curve, this.x, this.y.negate(), this.zs);
    }
}

