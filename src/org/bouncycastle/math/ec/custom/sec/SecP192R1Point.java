package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat192;

public class SecP192R1Point extends ECPoint.AbstractFp {
   SecP192R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecP192R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecP192R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
      SecP192R1FieldElement var3 = (SecP192R1FieldElement)this.x;
      SecP192R1FieldElement var4 = (SecP192R1FieldElement)this.y;
      SecP192R1FieldElement var5 = (SecP192R1FieldElement)var1.getXCoord();
      SecP192R1FieldElement var6 = (SecP192R1FieldElement)var1.getYCoord();
      SecP192R1FieldElement var7 = (SecP192R1FieldElement)this.zs[0];
      SecP192R1FieldElement var8 = (SecP192R1FieldElement)var1.getZCoord(0);
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
         SecP192R1Field.square(var7.x, var16);
         var15 = var11;
         SecP192R1Field.multiply(var16, var5.x, var15);
         SecP192R1Field.multiply(var16, var7.x, var16);
         SecP192R1Field.multiply(var16, var6.x, var16);
      }

      boolean var17 = var8.isOne();
      int[] var18;
      int[] var19;
      if (var17) {
         var18 = var3.x;
         var19 = var4.x;
      } else {
         var19 = var13;
         SecP192R1Field.square(var8.x, var19);
         var18 = var10;
         SecP192R1Field.multiply(var19, var3.x, var18);
         SecP192R1Field.multiply(var19, var8.x, var19);
         SecP192R1Field.multiply(var19, var4.x, var19);
      }

      int[] var20 = Nat192.create();
      SecP192R1Field.subtract(var18, var15, var20);
      int[] var21 = var11;
      SecP192R1Field.subtract(var19, var16, var21);
      if (Nat192.isZero(var20)) {
         return Nat192.isZero(var21) ? this.twice() : var2.getInfinity();
      }

      int[] var22 = var12;
      SecP192R1Field.square(var20, var22);
      int[] var23 = Nat192.create();
      SecP192R1Field.multiply(var22, var20, var23);
      int[] var24 = var12;
      SecP192R1Field.multiply(var22, var18, var24);
      SecP192R1Field.negate(var23, var23);
      Nat192.mul(var19, var23, var10);
      int var9 = Nat192.addBothTo(var24, var24, var23);
      SecP192R1Field.reduce32(var9, var23);
      SecP192R1FieldElement var25 = new SecP192R1FieldElement(var13);
      SecP192R1Field.square(var21, var25.x);
      SecP192R1Field.subtract(var25.x, var23, var25.x);
      SecP192R1FieldElement var26 = new SecP192R1FieldElement(var23);
      SecP192R1Field.subtract(var24, var25.x, var26.x);
      SecP192R1Field.multiplyAddToExt(var26.x, var21, var10);
      SecP192R1Field.reduce(var10, var26.x);
      SecP192R1FieldElement var27 = new SecP192R1FieldElement(var20);
      if (!var14) {
         SecP192R1Field.multiply(var27.x, var7.x, var27.x);
      }

      if (!var17) {
         SecP192R1Field.multiply(var27.x, var8.x, var27.x);
      }

      ECFieldElement[] var28 = new ECFieldElement[]{var27};
      return new SecP192R1Point(var2, var25, var26, var28);
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      }

      ECCurve var1 = this.getCurve();
      SecP192R1FieldElement var2 = (SecP192R1FieldElement)this.y;
      if (var2.isZero()) {
         return var1.getInfinity();
      }

      SecP192R1FieldElement var3 = (SecP192R1FieldElement)this.x;
      SecP192R1FieldElement var4 = (SecP192R1FieldElement)this.zs[0];
      int[] var6 = Nat192.create();
      int[] var7 = Nat192.create();
      int[] var8 = Nat192.create();
      SecP192R1Field.square(var2.x, var8);
      int[] var9 = Nat192.create();
      SecP192R1Field.square(var8, var9);
      boolean var10 = var4.isOne();
      int[] var11 = var4.x;
      if (!var10) {
         var11 = var7;
         SecP192R1Field.square(var4.x, var11);
      }

      SecP192R1Field.subtract(var3.x, var11, var6);
      int[] var12 = var7;
      SecP192R1Field.add(var3.x, var11, var12);
      SecP192R1Field.multiply(var12, var6, var12);
      int var5 = Nat192.addBothTo(var12, var12, var12);
      SecP192R1Field.reduce32(var5, var12);
      int[] var13 = var8;
      SecP192R1Field.multiply(var8, var3.x, var13);
      var5 = Nat.shiftUpBits(6, var13, 2, 0);
      SecP192R1Field.reduce32(var5, var13);
      var5 = Nat.shiftUpBits(6, var9, 3, 0, var6);
      SecP192R1Field.reduce32(var5, var6);
      SecP192R1FieldElement var14 = new SecP192R1FieldElement(var9);
      SecP192R1Field.square(var12, var14.x);
      SecP192R1Field.subtract(var14.x, var13, var14.x);
      SecP192R1Field.subtract(var14.x, var13, var14.x);
      SecP192R1FieldElement var15 = new SecP192R1FieldElement(var13);
      SecP192R1Field.subtract(var13, var14.x, var15.x);
      SecP192R1Field.multiply(var15.x, var12, var15.x);
      SecP192R1Field.subtract(var15.x, var6, var15.x);
      SecP192R1FieldElement var16 = new SecP192R1FieldElement(var12);
      SecP192R1Field.twice(var2.x, var16.x);
      if (!var10) {
         SecP192R1Field.multiply(var16.x, var4.x, var16.x);
      }

      return new SecP192R1Point(var1, var14, var15, new ECFieldElement[]{var16});
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
      return this.isInfinity() ? this : new SecP192R1Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
