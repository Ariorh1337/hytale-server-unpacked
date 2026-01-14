package org.bouncycastle.math.ec.custom.sec;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat160;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SecP160R1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFFFF"));
   protected int[] x;

   public SecP160R1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SecP160R1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SecP160R1FieldElement");
      }
   }

   public SecP160R1FieldElement() {
      this.x = Nat160.create();
   }

   protected SecP160R1FieldElement(int[] var1) {
      this.x = var1;
   }

   @Override
   public boolean isZero() {
      return Nat160.isZero(this.x);
   }

   @Override
   public boolean isOne() {
      return Nat160.isOne(this.x);
   }

   @Override
   public boolean testBitZero() {
      return Nat160.getBit(this.x, 0) == 1;
   }

   @Override
   public BigInteger toBigInteger() {
      return Nat160.toBigInteger(this.x);
   }

   @Override
   public String getFieldName() {
      return "SecP160R1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.add(this.x, ((SecP160R1FieldElement)var1).x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat160.create();
      SecP160R1Field.addOne(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.subtract(this.x, ((SecP160R1FieldElement)var1).x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.multiply(this.x, ((SecP160R1FieldElement)var1).x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat160.create();
      SecP160R1Field.inv(((SecP160R1FieldElement)var1).x, var2);
      SecP160R1Field.multiply(var2, this.x, var2);
      return new SecP160R1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat160.create();
      SecP160R1Field.negate(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat160.create();
      SecP160R1Field.square(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat160.create();
      SecP160R1Field.inv(this.x, var1);
      return new SecP160R1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat160.isZero(var1) && !Nat160.isOne(var1)) {
         int[] var2 = Nat160.create();
         SecP160R1Field.square(var1, var2);
         SecP160R1Field.multiply(var2, var1, var2);
         int[] var3 = Nat160.create();
         SecP160R1Field.squareN(var2, 2, var3);
         SecP160R1Field.multiply(var3, var2, var3);
         int[] var4 = var2;
         SecP160R1Field.squareN(var3, 4, var4);
         SecP160R1Field.multiply(var4, var3, var4);
         int[] var5 = var3;
         SecP160R1Field.squareN(var4, 8, var5);
         SecP160R1Field.multiply(var5, var4, var5);
         int[] var6 = var4;
         SecP160R1Field.squareN(var5, 16, var6);
         SecP160R1Field.multiply(var6, var5, var6);
         int[] var7 = var5;
         SecP160R1Field.squareN(var6, 32, var7);
         SecP160R1Field.multiply(var7, var6, var7);
         int[] var8 = var6;
         SecP160R1Field.squareN(var7, 64, var8);
         SecP160R1Field.multiply(var8, var7, var8);
         int[] var9 = var7;
         SecP160R1Field.square(var8, var9);
         SecP160R1Field.multiply(var9, var1, var9);
         int[] var10 = var9;
         SecP160R1Field.squareN(var10, 29, var10);
         int[] var11 = var8;
         SecP160R1Field.square(var10, var11);
         return Nat160.eq(var1, var11) ? new SecP160R1FieldElement(var10) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof SecP160R1FieldElement)) {
         return false;
      }

      SecP160R1FieldElement var2 = (SecP160R1FieldElement)var1;
      return Nat160.eq(this.x, var2.x);
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 5);
   }
}
