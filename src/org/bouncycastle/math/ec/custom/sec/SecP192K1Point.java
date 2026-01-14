package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecP192K1Point extends ECPoint.AbstractFp {
   SecP192K1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecP192K1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecP192K1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
   }

   @Override
   public ECPoint add(ECPoint var1) {
      if (this.isInfinity()) {
         return var1;
      }

      if (var1.isInfinity()) {
         return this;
      }

      if (this == var1) {
         return this.twice();
      }

      ECCurve var2 = this.getCurve();
      SecP192K1FieldElement var3 = (SecP192K1FieldElement)this.x;
      SecP192K1FieldElement var4 = (SecP192K1FieldElement)this.y;
      SecP192K1FieldElement var5 = (SecP192K1FieldElement)var1.getXCoord();
      SecP192K1FieldElement var6 = (SecP192K1FieldElement)var1.getYCoord();
      SecP192K1FieldElement var7 = (SecP192K1FieldElement)this.zs[0];
      SecP192K1FieldElement var8 = (SecP192K1FieldElement)var1.getZCoord(0);
      int[] var10 = Nat192.createExt();
      int[] var11 = Nat192.create();
      int[] var12 = Nat192.create();
      int[] var13 = Nat192.create();
      boolean var14 = var7.isOne();
      int[] var15;
      int[] var16;
      if (var14) {
         var15 = var5.x;
         var16 = var6.x;
      } else {
         var16 = var12;
         SecP192K1Field.square(var7.x, var16);
         var15 = var11;
         SecP192K1Field.multiply(var16, var5.x, var15);
         SecP192K1Field.multiply(var16, var7.x, var16);
         SecP192K1Field.multiply(var16, var6.x, var16);
      }

      boolean var17 = var8.isOne();
      int[] var18;
      int[] var19;
      if (var17) {
         var18 = var3.x;
         var19 = var4.x;
      } else {
         var19 = var13;
         SecP192K1Field.square(var8.x, var19);
         var18 = var10;
         SecP192K1Field.multiply(var19, var3.x, var18);
         SecP192K1Field.multiply(var19, var8.x, var19);
         SecP192K1Field.multiply(var19, var4.x, var19);
      }

      int[] var20 = Nat192.create();
      SecP192K1Field.subtract(var18, var15, var20);
      int[] var21 = var11;
      SecP192K1Field.subtract(var19, var16, var21);
      if (Nat192.isZero(var20)) {
         return Nat192.isZero(var21) ? this.twice() : var2.getInfinity();
      }

      int[] var22 = var12;
      SecP192K1Field.square(var20, var22);
      int[] var23 = Nat192.create();
      SecP192K1Field.multiply(var22, var20, var23);
      int[] var24 = var12;
      SecP192K1Field.multiply(var22, var18, var24);
      SecP192K1Field.negate(var23, var23);
      Nat192.mul(var19, var23, var10);
      int var9 = Nat192.addBothTo(var24, var24, var23);
      SecP192K1Field.reduce32(var9, var23);
      SecP192K1FieldElement var25 = new SecP192K1FieldElement(var13);
      SecP192K1Field.square(var21, var25.x);
      SecP192K1Field.subtract(var25.x, var23, var25.x);
      SecP192K1FieldElement var26 = new SecP192K1FieldElement(var23);
      SecP192K1Field.subtract(var24, var25.x, var26.x);
      SecP192K1Field.multiplyAddToExt(var26.x, var21, var10);
      SecP192K1Field.reduce(var10, var26.x);
      SecP192K1FieldElement var27 = new SecP192K1FieldElement(var20);
      if (!var14) {
         SecP192K1Field.multiply(var27.x, var7.x, var27.x);
      }

      if (!var17) {
         SecP192K1Field.multiply(var27.x, var8.x, var27.x);
      }

      ECFieldElement[] var28 = new ECFieldElement[]{var27};
      return new SecP192K1Point(var2, var25, var26, var28);
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      }

      ECCurve var1 = this.getCurve();
      SecP192K1FieldElement var2 = (SecP192K1FieldElement)this.y;
      if (var2.isZero()) {
         return var1.getInfinity();
      }

      SecP192K1FieldElement var3 = (SecP192K1FieldElement)this.x;
      SecP192K1FieldElement var4 = (SecP192K1FieldElement)this.zs[0];
      int[] var6 = Nat192.create();
      SecP192K1Field.square(var2.x, var6);
      int[] var7 = Nat192.create();
      SecP192K1Field.square(var6, var7);
      int[] var8 = Nat192.create();
      SecP192K1Field.square(var3.x, var8);
      int var5 = Nat192.addBothTo(var8, var8, var8);
      SecP192K1Field.reduce32(var5, var8);
      int[] var9 = var6;
      SecP192K1Field.multiply(var6, var3.x, var9);
      var5 = Nat.shiftUpBits(6, var9, 2, 0);
      SecP192K1Field.reduce32(var5, var9);
      int[] var10 = Nat192.create();
      var5 = Nat.shiftUpBits(6, var7, 3, 0, var10);
      SecP192K1Field.reduce32(var5, var10);
      SecP192K1FieldElement var11 = new SecP192K1FieldElement(var7);
      SecP192K1Field.square(var8, var11.x);
      SecP192K1Field.subtract(var11.x, var9, var11.x);
      SecP192K1Field.subtract(var11.x, var9, var11.x);
      SecP192K1FieldElement var12 = new SecP192K1FieldElement(var9);
      SecP192K1Field.subtract(var9, var11.x, var12.x);
      SecP192K1Field.multiply(var12.x, var8, var12.x);
      SecP192K1Field.subtract(var12.x, var10, var12.x);
      SecP192K1FieldElement var13 = new SecP192K1FieldElement(var8);
      SecP192K1Field.twice(var2.x, var13.x);
      if (!var4.isOne()) {
         SecP192K1Field.multiply(var13.x, var4.x, var13.x);
      }

      return new SecP192K1Point(var1, var11, var12, new ECFieldElement[]{var13});
   }

   @Override
   public ECPoint twicePlus(ECPoint var1) {
      if (this == var1) {
         return this.threeTimes();
      }

      if (this.isInfinity()) {
         return var1;
      }

      if (var1.isInfinity()) {
         return this.twice();
      }

      ECFieldElement var2 = this.y;
      return var2.isZero() ? var1 : this.twice().add(var1);
   }

   @Override
   public ECPoint threeTimes() {
      return !this.isInfinity() && !this.y.isZero() ? this.twice().add(this) : this;
   }

   @Override
   public ECPoint negate() {
      return this.isInfinity() ? this : new SecP192K1Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
