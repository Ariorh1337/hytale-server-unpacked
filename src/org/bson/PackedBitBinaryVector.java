package org.bson;

import java.util.Arrays;
import java.util.Objects;
import org.bson.annotations.Beta;
import org.bson.annotations.Reason;
import org.bson.assertions.Assertions;

@Beta(Reason.SERVER)
public final class PackedBitBinaryVector extends BinaryVector {
   private final byte padding;
   private final byte[] data;

   PackedBitBinaryVector(byte[] data, byte padding) {
      super(BinaryVector.DataType.PACKED_BIT);
      this.data = Assertions.assertNotNull(data);
      this.padding = padding;
   }

   public byte[] getData() {
      return Assertions.assertNotNull(this.data);
   }

   public byte getPadding() {
      return this.padding;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PackedBitBinaryVector that = (PackedBitBinaryVector)o;
         return this.padding == that.padding && Arrays.equals(this.data, that.data);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.padding, Arrays.hashCode(this.data));
   }

   @Override
   public String toString() {
      return "PackedBitVector{padding=" + this.padding + ", data=" + Arrays.toString(this.data) + ", dataType=" + this.getDataType() + '}';
   }
}
