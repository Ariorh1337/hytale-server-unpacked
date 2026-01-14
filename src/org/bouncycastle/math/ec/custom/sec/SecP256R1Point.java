package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SecP256R1Point extends ECPoint.AbstractFp {
   SecP256R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecP256R1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecP256R1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
      SecP256R1FieldElement var3 = (SecP256R1FieldElement)this.x;
      SecP256R1FieldElement var4 = (SecP256R1FieldElement)this.y;
      SecP256R1FieldElement var5 = (SecP256R1FieldElement)var1.getXCoord();
      SecP256R1FieldElement var6 = (SecP256R1FieldElement)var1.getYCoord();
      SecP256R1FieldElement var7 = (SecP256R1FieldElement)this.zs[0];
      SecP256R1FieldElement var8 = (SecP256R1FieldElement)var1.getZCoord(0);
      int[] var10 = Nat256.createExt();
      int[] var11 = Nat256.createExt();
      int[] var12 = Nat256.create();
      int[] var13 = Nat256.create();
      int[] var14 = Nat256.create();
      boolean var15 = var7.isOne();
      int[] var16;
      int[] var17;
      if (var15) {
         var16 = var5.x;
         var17 = var6.x;
      } else {
         var17 = var13;
         SecP256R1Field.square(var7.x, var17, var10);
         var16 = var12;
         SecP256R1Field.multiply(var17, var5.x, var16, var10);
         SecP256R1Field.multiply(var17, var7.x, var17, var10);
         SecP256R1Field.multiply(var17, var6.x, var17, var10);
      }

      boolean var18 = var8.isOne();
      int[] var19;
      int[] var20;
      if (var18) {
         var19 = var3.x;
         var20 = var4.x;
      } else {
         var20 = var14;
         SecP256R1Field.square(var8.x, var20, var10);
         var19 = var11;
         SecP256R1Field.multiply(var20, var3.x, var19, var10);
         SecP256R1Field.multiply(var20, var8.x, var20, var10);
         SecP256R1Field.multiply(var20, var4.x, var20, var10);
      }

      int[] var21 = Nat256.create();
      SecP256R1Field.subtract(var19, var16, var21);
      int[] var22 = var12;
      SecP256R1Field.subtract(var20, var17, var22);
      if (Nat256.isZero(var21)) {
         return Nat256.isZero(var22) ? this.twice() : var2.getInfinity();
      }

      int[] var23 = var13;
      SecP256R1Field.square(var21, var23, var10);
      int[] var24 = Nat256.create();
      SecP256R1Field.multiply(var23, var21, var24, var10);
      int[] var25 = var13;
      SecP256R1Field.multiply(var23, var19, var25, var10);
      SecP256R1Field.negate(var24, var24);
      Nat256.mul(var20, var24, var11);
      int var9 = Nat256.addBothTo(var25, var25, var24);
      SecP256R1Field.reduce32(var9, var24);
      SecP256R1FieldElement var26 = new SecP256R1FieldElement(var14);
      SecP256R1Field.square(var22, var26.x, var10);
      SecP256R1Field.subtract(var26.x, var24, var26.x);
      SecP256R1FieldElement var27 = new SecP256R1FieldElement(var24);
      SecP256R1Field.subtract(var25, var26.x, var27.x);
      SecP256R1Field.multiplyAddToExt(var27.x, var22, var11);
      SecP256R1Field.reduce(var11, var27.x);
      SecP256R1FieldElement var28 = new SecP256R1FieldElement(var21);
      if (!var15) {
         SecP256R1Field.multiply(var28.x, var7.x, var28.x, var10);
      }

      if (!var18) {
         SecP256R1Field.multiply(var28.x, var8.x, var28.x, var10);
      }

      ECFieldElement[] var29 = new ECFieldElement[]{var28};
      return new SecP256R1Point(var2, var26, var27, var29);
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      }

      ECCurve var1 = this.getCurve();
      SecP256R1FieldElement var2 = (SecP256R1FieldElement)this.y;
      if (var2.isZero()) {
         return var1.getInfinity();
      }

      SecP256R1FieldElement var3 = (SecP256R1FieldElement)this.x;
      SecP256R1FieldElement var4 = (SecP256R1FieldElement)this.zs[0];
      int[] var6 = Nat256.createExt();
      int[] var7 = Nat256.create();
      int[] var8 = Nat256.create();
      int[] var9 = Nat256.create();
      SecP256R1Field.square(var2.x, var9, var6);
      int[] var10 = Nat256.create();
      SecP256R1Field.square(var9, var10, var6);
      boolean var11 = var4.isOne();
      int[] var12 = var4.x;
      if (!var11) {
         var12 = var8;
         SecP256R1Field.square(var4.x, var12, var6);
      }

      SecP256R1Field.subtract(var3.x, var12, var7);
      int[] var13 = var8;
      SecP256R1Field.add(var3.x, var12, var13);
      SecP256R1Field.multiply(var13, var7, var13, var6);
      int var5 = Nat256.addBothTo(var13, var13, var13);
      SecP256R1Field.reduce32(var5, var13);
      int[] var14 = var9;
      SecP256R1Field.multiply(var9, var3.x, var14, var6);
      var5 = Nat.shiftUpBits(8, var14, 2, 0);
      SecP256R1Field.reduce32(var5, var14);
      var5 = Nat.shiftUpBits(8, var10, 3, 0, var7);
      SecP256R1Field.reduce32(var5, var7);
      SecP256R1FieldElement var15 = new SecP256R1FieldElement(var10);
      SecP256R1Field.square(var13, var15.x, var6);
      SecP256R1Field.subtract(var15.x, var14, var15.x);
      SecP256R1Field.subtract(var15.x, var14, var15.x);
      SecP256R1FieldElement var16 = new SecP256R1FieldElement(var14);
      SecP256R1Field.subtract(var14, var15.x, var16.x);
      SecP256R1Field.multiply(var16.x, var13, var16.x, var6);
      SecP256R1Field.subtract(var16.x, var7, var16.x);
      SecP256R1FieldElement var17 = new SecP256R1FieldElement(var13);
      SecP256R1Field.twice(var2.x, var17.x);
      if (!var11) {
         SecP256R1Field.multiply(var17.x, var4.x, var17.x, var6);
      }

      return new SecP256R1Point(var1, var15, var16, new ECFieldElement[]{var17});
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
      return this.isInfinity() ? this : new SecP256R1Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
