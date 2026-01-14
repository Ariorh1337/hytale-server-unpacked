package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;

public class SecP521R1Point extends ECPoint.AbstractFp {
   SecP521R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecP521R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecP521R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
      SecP521R1FieldElement var3 = (SecP521R1FieldElement)this.x;
      SecP521R1FieldElement var4 = (SecP521R1FieldElement)this.y;
      SecP521R1FieldElement var5 = (SecP521R1FieldElement)var1.getXCoord();
      SecP521R1FieldElement var6 = (SecP521R1FieldElement)var1.getYCoord();
      SecP521R1FieldElement var7 = (SecP521R1FieldElement)this.zs[0];
      SecP521R1FieldElement var8 = (SecP521R1FieldElement)var1.getZCoord(0);
      int[] var9 = Nat.create(33);
      int[] var10 = Nat.create(17);
      int[] var11 = Nat.create(17);
      int[] var12 = Nat.create(17);
      int[] var13 = Nat.create(17);
      boolean var14 = var7.isOne();
      int[] var15;
      int[] var16;
      if (var14) {
         var15 = var5.x;
         var16 = var6.x;
      } else {
         var16 = var12;
         SecP521R1Field.square(var7.x, var16, var9);
         var15 = var11;
         SecP521R1Field.multiply(var16, var5.x, var15, var9);
         SecP521R1Field.multiply(var16, var7.x, var16, var9);
         SecP521R1Field.multiply(var16, var6.x, var16, var9);
      }

      boolean var17 = var8.isOne();
      int[] var18;
      int[] var19;
      if (var17) {
         var18 = var3.x;
         var19 = var4.x;
      } else {
         var19 = var13;
         SecP521R1Field.square(var8.x, var19, var9);
         var18 = var10;
         SecP521R1Field.multiply(var19, var3.x, var18, var9);
         SecP521R1Field.multiply(var19, var8.x, var19, var9);
         SecP521R1Field.multiply(var19, var4.x, var19, var9);
      }

      int[] var20 = Nat.create(17);
      SecP521R1Field.subtract(var18, var15, var20);
      int[] var21 = var11;
      SecP521R1Field.subtract(var19, var16, var21);
      if (Nat.isZero(17, var20)) {
         return Nat.isZero(17, var21) ? this.twice() : var2.getInfinity();
      }

      int[] var22 = var12;
      SecP521R1Field.square(var20, var22, var9);
      int[] var23 = Nat.create(17);
      SecP521R1Field.multiply(var22, var20, var23, var9);
      int[] var24 = var12;
      SecP521R1Field.multiply(var22, var18, var24, var9);
      SecP521R1Field.multiply(var19, var23, var10, var9);
      SecP521R1FieldElement var25 = new SecP521R1FieldElement(var13);
      SecP521R1Field.square(var21, var25.x, var9);
      SecP521R1Field.add(var25.x, var23, var25.x);
      SecP521R1Field.subtract(var25.x, var24, var25.x);
      SecP521R1Field.subtract(var25.x, var24, var25.x);
      SecP521R1FieldElement var26 = new SecP521R1FieldElement(var23);
      SecP521R1Field.subtract(var24, var25.x, var26.x);
      SecP521R1Field.multiply(var26.x, var21, var11, var9);
      SecP521R1Field.subtract(var11, var10, var26.x);
      SecP521R1FieldElement var27 = new SecP521R1FieldElement(var20);
      if (!var14) {
         SecP521R1Field.multiply(var27.x, var7.x, var27.x, var9);
      }

      if (!var17) {
         SecP521R1Field.multiply(var27.x, var8.x, var27.x, var9);
      }

      ECFieldElement[] var28 = new ECFieldElement[]{var27};
      return new SecP521R1Point(var2, var25, var26, var28);
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      }

      ECCurve var1 = this.getCurve();
      SecP521R1FieldElement var2 = (SecP521R1FieldElement)this.y;
      if (var2.isZero()) {
         return var1.getInfinity();
      }

      SecP521R1FieldElement var3 = (SecP521R1FieldElement)this.x;
      SecP521R1FieldElement var4 = (SecP521R1FieldElement)this.zs[0];
      int[] var5 = Nat.create(33);
      int[] var6 = Nat.create(17);
      int[] var7 = Nat.create(17);
      int[] var8 = Nat.create(17);
      SecP521R1Field.square(var2.x, var8, var5);
      int[] var9 = Nat.create(17);
      SecP521R1Field.square(var8, var9, var5);
      boolean var10 = var4.isOne();
      int[] var11 = var4.x;
      if (!var10) {
         var11 = var7;
         SecP521R1Field.square(var4.x, var11, var5);
      }

      SecP521R1Field.subtract(var3.x, var11, var6);
      int[] var12 = var7;
      SecP521R1Field.add(var3.x, var11, var12);
      SecP521R1Field.multiply(var12, var6, var12, var5);
      Nat.addBothTo(17, var12, var12, var12);
      SecP521R1Field.reduce23(var12);
      int[] var13 = var8;
      SecP521R1Field.multiply(var8, var3.x, var13, var5);
      Nat.shiftUpBits(17, var13, 2, 0);
      SecP521R1Field.reduce23(var13);
      Nat.shiftUpBits(17, var9, 3, 0, var6);
      SecP521R1Field.reduce23(var6);
      SecP521R1FieldElement var14 = new SecP521R1FieldElement(var9);
      SecP521R1Field.square(var12, var14.x, var5);
      SecP521R1Field.subtract(var14.x, var13, var14.x);
      SecP521R1Field.subtract(var14.x, var13, var14.x);
      SecP521R1FieldElement var15 = new SecP521R1FieldElement(var13);
      SecP521R1Field.subtract(var13, var14.x, var15.x);
      SecP521R1Field.multiply(var15.x, var12, var15.x, var5);
      SecP521R1Field.subtract(var15.x, var6, var15.x);
      SecP521R1FieldElement var16 = new SecP521R1FieldElement(var12);
      SecP521R1Field.twice(var2.x, var16.x);
      if (!var10) {
         SecP521R1Field.multiply(var16.x, var4.x, var16.x, var5);
      }

      return new SecP521R1Point(var1, var14, var15, new ECFieldElement[]{var16});
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

   protected ECFieldElement two(ECFieldElement var1) {
      return var1.add(var1);
   }

   protected ECFieldElement three(ECFieldElement var1) {
      return this.two(var1).add(var1);
   }

   protected ECFieldElement four(ECFieldElement var1) {
      return this.two(this.two(var1));
   }

   protected ECFieldElement eight(ECFieldElement var1) {
      return this.four(this.two(var1));
   }

   protected ECFieldElement doubleProductFromSquares(ECFieldElement var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement var4) {
      return var1.add(var2).square().subtract(var3).subtract(var4);
   }

   @Override
   public ECPoint negate() {
      return this.isInfinity() ? this : new SecP521R1Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
