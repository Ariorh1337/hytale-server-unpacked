package org.bson;

import org.bson.annotations.Beta;
import org.bson.annotations.Reason;
import org.bson.assertions.Assertions;

public abstract class BinaryVector {
   private final BinaryVector.DataType dataType;

   BinaryVector(BinaryVector.DataType dataType) {
      this.dataType = dataType;
   }

   @Beta(Reason.SERVER)
   public static PackedBitBinaryVector packedBitVector(byte[] data, byte padding) {
      Assertions.notNull("data", data);
      Assertions.isTrueArgument("Padding must be between 0 and 7 bits. Provided padding: " + padding, padding >= 0 && padding <= 7);
      Assertions.isTrueArgument("Padding must be 0 if vector is empty. Provided padding: " + padding, padding == 0 || data.length > 0);
      return new PackedBitBinaryVector(data, padding);
   }

   public static Int8BinaryVector int8Vector(byte[] data) {
      Assertions.notNull("data", data);
      return new Int8BinaryVector(data);
   }

   public static Float32BinaryVector floatVector(float[] data) {
      Assertions.notNull("data", data);
      return new Float32BinaryVector(data);
   }

   public PackedBitBinaryVector asPackedBitVector() {
      this.ensureType(BinaryVector.DataType.PACKED_BIT);
      return (PackedBitBinaryVector)this;
   }

   public Int8BinaryVector asInt8Vector() {
      this.ensureType(BinaryVector.DataType.INT8);
      return (Int8BinaryVector)this;
   }

   public Float32BinaryVector asFloat32Vector() {
      this.ensureType(BinaryVector.DataType.FLOAT32);
      return (Float32BinaryVector)this;
   }

   public BinaryVector.DataType getDataType() {
      return this.dataType;
   }

   private void ensureType(BinaryVector.DataType expected) {
      if (this.dataType != expected) {
         throw new IllegalStateException("Expected vector data type " + expected + ", but found " + this.dataType);
      }
   }

   public enum DataType {
      INT8((byte)3),
      FLOAT32((byte)39),
      PACKED_BIT((byte)16);

      private final byte value;

      DataType(byte value) {
         this.value = value;
      }

      public byte getValue() {
         return this.value;
      }
   }
}
