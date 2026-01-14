package org.bouncycastle.math.ec.custom.gm;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.raw.Nat256;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class SM2P256V1FieldElement extends ECFieldElement.AbstractFp {
   public static final BigInteger Q = new BigInteger(1, Hex.decodeStrict("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF"));
   protected int[] x;

   public SM2P256V1FieldElement(BigInteger var1) {
      if (var1 != null && var1.signum() >= 0 && var1.compareTo(Q) < 0) {
         this.x = SM2P256V1Field.fromBigInteger(var1);
      } else {
         throw new IllegalArgumentException("x value invalid for SM2P256V1FieldElement");
      }
   }

   public SM2P256V1FieldElement() {
      this.x = Nat256.create();
   }

   protected SM2P256V1FieldElement(int[] var1) {
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
      return "SM2P256V1Field";
   }

   @Override
   public int getFieldSize() {
      return Q.bitLength();
   }

   @Override
   public ECFieldElement add(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SM2P256V1Field.add(this.x, ((SM2P256V1FieldElement)var1).x, var2);
      return new SM2P256V1FieldElement(var2);
   }

   @Override
   public ECFieldElement addOne() {
      int[] var1 = Nat256.create();
      SM2P256V1Field.addOne(this.x, var1);
      return new SM2P256V1FieldElement(var1);
   }

   @Override
   public ECFieldElement subtract(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SM2P256V1Field.subtract(this.x, ((SM2P256V1FieldElement)var1).x, var2);
      return new SM2P256V1FieldElement(var2);
   }

   @Override
   public ECFieldElement multiply(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SM2P256V1Field.multiply(this.x, ((SM2P256V1FieldElement)var1).x, var2);
      return new SM2P256V1FieldElement(var2);
   }

   @Override
   public ECFieldElement divide(ECFieldElement var1) {
      int[] var2 = Nat256.create();
      SM2P256V1Field.inv(((SM2P256V1FieldElement)var1).x, var2);
      SM2P256V1Field.multiply(var2, this.x, var2);
      return new SM2P256V1FieldElement(var2);
   }

   @Override
   public ECFieldElement negate() {
      int[] var1 = Nat256.create();
      SM2P256V1Field.negate(this.x, var1);
      return new SM2P256V1FieldElement(var1);
   }

   @Override
   public ECFieldElement square() {
      int[] var1 = Nat256.create();
      SM2P256V1Field.square(this.x, var1);
      return new SM2P256V1FieldElement(var1);
   }

   @Override
   public ECFieldElement invert() {
      int[] var1 = Nat256.create();
      SM2P256V1Field.inv(this.x, var1);
      return new SM2P256V1FieldElement(var1);
   }

   @Override
   public ECFieldElement sqrt() {
      int[] var1 = this.x;
      if (!Nat256.isZero(var1) && !Nat256.isOne(var1)) {
         int[] var2 = Nat256.create();
         SM2P256V1Field.square(var1, var2);
         SM2P256V1Field.multiply(var2, var1, var2);
         int[] var3 = Nat256.create();
         SM2P256V1Field.squareN(var2, 2, var3);
         SM2P256V1Field.multiply(var3, var2, var3);
         int[] var4 = Nat256.create();
         SM2P256V1Field.squareN(var3, 2, var4);
         SM2P256V1Field.multiply(var4, var2, var4);
         int[] var5 = var2;
         SM2P256V1Field.squareN(var4, 6, var5);
         SM2P256V1Field.multiply(var5, var4, var5);
         int[] var6 = Nat256.create();
         SM2P256V1Field.squareN(var5, 12, var6);
         SM2P256V1Field.multiply(var6, var5, var6);
         int[] var7 = var5;
         SM2P256V1Field.squareN(var6, 6, var7);
         SM2P256V1Field.multiply(var7, var4, var7);
         int[] var8 = var4;
         SM2P256V1Field.square(var7, var8);
         SM2P256V1Field.multiply(var8, var1, var8);
         int[] var9 = var6;
         SM2P256V1Field.squareN(var8, 31, var9);
         int[] var10 = var7;
         SM2P256V1Field.multiply(var9, var8, var10);
         SM2P256V1Field.squareN(var9, 32, var9);
         SM2P256V1Field.multiply(var9, var10, var9);
         SM2P256V1Field.squareN(var9, 62, var9);
         SM2P256V1Field.multiply(var9, var10, var9);
         SM2P256V1Field.squareN(var9, 4, var9);
         SM2P256V1Field.multiply(var9, var3, var9);
         SM2P256V1Field.squareN(var9, 32, var9);
         SM2P256V1Field.multiply(var9, var1, var9);
         SM2P256V1Field.squareN(var9, 62, var9);
         int[] var11 = var3;
         SM2P256V1Field.square(var9, var11);
         return Nat256.eq(var1, var11) ? new SM2P256V1FieldElement(var9) : null;
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof SM2P256V1FieldElement)) {
         return false;
      }

      SM2P256V1FieldElement var2 = (SM2P256V1FieldElement)var1;
      return Nat256.eq(this.x, var2.x);
   }

   @Override
   public int hashCode() {
      return Q.hashCode() ^ Arrays.hashCode(this.x, 0, 8);
   }
}
