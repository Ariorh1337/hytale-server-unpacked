package org.bouncycastle.math.ec.custom.sec;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.raw.Nat;
import org.bouncycastle.math.raw.Nat256;

public class SecP256K1Point extends ECPoint.AbstractFp {
   SecP256K1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3) {
      super(var1, var2, var3);
   }

   SecP256K1Point(ECCurve var1, ECFieldElement var2, ECFieldElement var3, ECFieldElement[] var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected ECPoint detach() {
      return new SecP256K1Point(null, this.getAffineXCoord(), this.getAffineYCoord());
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
      SecP256K1FieldElement var3 = (SecP256K1FieldElement)this.x;
      SecP256K1FieldElement var4 = (SecP256K1FieldElement)this.y;
      SecP256K1FieldElement var5 = (SecP256K1FieldElement)var1.getXCoord();
      SecP256K1FieldElement var6 = (SecP256K1FieldElement)var1.getYCoord();
      SecP256K1FieldElement var7 = (SecP256K1FieldElement)this.zs[0];
      SecP256K1FieldElement var8 = (SecP256K1FieldElement)var1.getZCoord(0);
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
         SecP256K1Field.square(var7.x, var17, var10);
         var16 = var12;
         SecP256K1Field.multiply(var17, var5.x, var16, var10);
         SecP256K1Field.multiply(var17, var7.x, var17, var10);
         SecP256K1Field.multiply(var17, var6.x, var17, var10);
      }

      boolean var18 = var8.isOne();
      int[] var19;
      int[] var20;
      if (var18) {
         var19 = var3.x;
         var20 = var4.x;
      } else {
         var20 = var14;
         SecP256K1Field.square(var8.x, var20, var10);
         var19 = var11;
         SecP256K1Field.multiply(var20, var3.x, var19, var10);
         SecP256K1Field.multiply(var20, var8.x, var20, var10);
         SecP256K1Field.multiply(var20, var4.x, var20, var10);
      }

      int[] var21 = Nat256.create();
      SecP256K1Field.subtract(var19, var16, var21);
      int[] var22 = var12;
      SecP256K1Field.subtract(var20, var17, var22);
      if (Nat256.isZero(var21)) {
         return Nat256.isZero(var22) ? this.twice() : var2.getInfinity();
      }

      int[] var23 = var13;
      SecP256K1Field.square(var21, var23, var10);
      int[] var24 = Nat256.create();
      SecP256K1Field.multiply(var23, var21, var24, var10);
      int[] var25 = var13;
      SecP256K1Field.multiply(var23, var19, var25, var10);
      SecP256K1Field.negate(var24, var24);
      Nat256.mul(var20, var24, var11);
      int var9 = Nat256.addBothTo(var25, var25, var24);
      SecP256K1Field.reduce32(var9, var24);
      SecP256K1FieldElement var26 = new SecP256K1FieldElement(var14);
      SecP256K1Field.square(var22, var26.x, var10);
      SecP256K1Field.subtract(var26.x, var24, var26.x);
      SecP256K1FieldElement var27 = new SecP256K1FieldElement(var24);
      SecP256K1Field.subtract(var25, var26.x, var27.x);
      SecP256K1Field.multiplyAddToExt(var27.x, var22, var11);
      SecP256K1Field.reduce(var11, var27.x);
      SecP256K1FieldElement var28 = new SecP256K1FieldElement(var21);
      if (!var15) {
         SecP256K1Field.multiply(var28.x, var7.x, var28.x, var10);
      }

      if (!var18) {
         SecP256K1Field.multiply(var28.x, var8.x, var28.x, var10);
      }

      ECFieldElement[] var29 = new ECFieldElement[]{var28};
      return new SecP256K1Point(var2, var26, var27, var29);
   }

   @Override
   public ECPoint twice() {
      if (this.isInfinity()) {
         return this;
      }

      ECCurve var1 = this.getCurve();
      SecP256K1FieldElement var2 = (SecP256K1FieldElement)this.y;
      if (var2.isZero()) {
         return var1.getInfinity();
      }

      SecP256K1FieldElement var3 = (SecP256K1FieldElement)this.x;
      SecP256K1FieldElement var4 = (SecP256K1FieldElement)this.zs[0];
      int[] var6 = Nat256.createExt();
      int[] var7 = Nat256.create();
      SecP256K1Field.square(var2.x, var7, var6);
      int[] var8 = Nat256.create();
      SecP256K1Field.square(var7, var8, var6);
      int[] var9 = Nat256.create();
      SecP256K1Field.square(var3.x, var9, var6);
      int var5 = Nat256.addBothTo(var9, var9, var9);
      SecP256K1Field.reduce32(var5, var9);
      int[] var10 = var7;
      SecP256K1Field.multiply(var7, var3.x, var10, var6);
      var5 = Nat.shiftUpBits(8, var10, 2, 0);
      SecP256K1Field.reduce32(var5, var10);
      int[] var11 = Nat256.create();
      var5 = Nat.shiftUpBits(8, var8, 3, 0, var11);
      SecP256K1Field.reduce32(var5, var11);
      SecP256K1FieldElement var12 = new SecP256K1FieldElement(var8);
      SecP256K1Field.square(var9, var12.x, var6);
      SecP256K1Field.subtract(var12.x, var10, var12.x);
      SecP256K1Field.subtract(var12.x, var10, var12.x);
      SecP256K1FieldElement var13 = new SecP256K1FieldElement(var10);
      SecP256K1Field.subtract(var10, var12.x, var13.x);
      SecP256K1Field.multiply(var13.x, var9, var13.x, var6);
      SecP256K1Field.subtract(var13.x, var11, var13.x);
      SecP256K1FieldElement var14 = new SecP256K1FieldElement(var9);
      SecP256K1Field.twice(var2.x, var14.x);
      if (!var4.isOne()) {
         SecP256K1Field.multiply(var14.x, var4.x, var14.x, var6);
      }

      return new SecP256K1Point(var1, var12, var13, new ECFieldElement[]{var14});
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
      return this.isInfinity() ? this : new SecP256K1Point(this.curve, this.x, this.y.negate(), this.zs);
   }
}
