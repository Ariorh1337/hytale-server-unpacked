package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP256K1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F"));
   protected int[] x;

   public SecP256K1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP256K1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP256K1FieldElement");
      }
   }

   public SecP256K1FieldElement() {
      this.x = Nat256.create();
   }

   protected SecP256K1FieldElement(int[] var1) {
      this.x = var1;
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
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256K1Field.add(this.x, ((SecP256K1FieldElement)var1).x, var2);
      return new SecP256K1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat256.create();
      SecP256K1Field.addOne(this.x, var1);
      return new SecP256K1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256K1Field.subtract(this.x, ((SecP256K1FieldElement)var1).x, var2);
      return new SecP256K1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256K1Field.multiply(this.x, ((SecP256K1FieldElement)var1).x, var2);
      return new SecP256K1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SecP256K1Field.inv(((SecP256K1FieldElement)var1).x, var2);
      SecP256K1Field.multiply(var2, this.x, var2);
      return new SecP256K1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat256.create();
      SecP256K1Field.negate(this.x, var1);
      return new SecP256K1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat256.create();
      SecP256K1Field.square(this.x, var1);
      return new SecP256K1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat256.create();
      SecP256K1Field.inv(this.x, var1);
      return new SecP256K1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat256.isZero(var1) && !Nat256.isOne(var1)) {
         int[] var2 = Nat256.createExt();
         int[] var3 = Nat256.create();
         SecP256K1Field.square(var1, var3, var2);
         SecP256K1Field.multiply(var3, var1, var3, var2);
         int[] var4 = Nat256.create();
         SecP256K1Field.square(var3, var4, var2);
         SecP256K1Field.multiply(var4, var1, var4, var2);
         int[] var5 = Nat256.create();
         SecP256K1Field.squareN(var4, 3, var5, var2);
         SecP256K1Field.multiply(var5, var4, var5, var2);
         int[] var6 = var5;
         SecP256K1Field.squareN(var5, 3, var6, var2);
         SecP256K1Field.multiply(var6, var4, var6, var2);
         int[] var7 = var6;
         SecP256K1Field.squareN(var6, 2, var7, var2);
         SecP256K1Field.multiply(var7, var3, var7, var2);
         int[] var8 = Nat256.create();
         SecP256K1Field.squareN(var7, 11, var8, var2);
         SecP256K1Field.multiply(var8, var7, var8, var2);
         int[] var9 = var7;
         SecP256K1Field.squareN(var8, 22, var9, var2);
         SecP256K1Field.multiply(var9, var8, var9, var2);
         int[] var10 = Nat256.create();
         SecP256K1Field.squareN(var9, 44, var10, var2);
         SecP256K1Field.multiply(var10, var9, var10, var2);
         int[] var11 = Nat256.create();
         SecP256K1Field.squareN(var10, 88, var11, var2);
         SecP256K1Field.multiply(var11, var10, var11, var2);
         int[] var12 = var10;
         SecP256K1Field.squareN(var11, 44, var12, var2);
         SecP256K1Field.multiply(var12, var9, var12, var2);
         int[] var13 = var9;
         SecP256K1Field.squareN(var12, 3, var13, var2);
         SecP256K1Field.multiply(var13, var4, var13, var2);
         int[] var14 = var13;
         SecP256K1Field.squareN(var14, 23, var14, var2);
         SecP256K1Field.multiply(var14, var8, var14, var2);
         SecP256K1Field.squareN(var14, 6, var14, var2);
         SecP256K1Field.multiply(var14, var3, var14, var2);
         SecP256K1Field.squareN(var14, 2, var14, var2);
         int[] var15 = var3;
         SecP256K1Field.square(var14, var15, var2);
         return Nat256.eq(var1, var15) ? new SecP256K1FieldElement(var14) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof SecP256K1FieldElement)) {
         return false;
      }

      SecP256K1FieldElement var2 = (SecP256K1FieldElement)var1;
      return Nat256.eq(this.x, var2.x);
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
   }
}
