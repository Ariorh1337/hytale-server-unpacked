package org.bson;

import java.util.Arrays;
import java.util.Objects;
import org.bson.assertions.Assertions;

public final class Int8BinaryVector extends BinaryVector {
   private byte[] data;

   Int8BinaryVector(byte[] data) {
      super(BinaryVector.DataType.INT8);
      this.data = Assertions.assertNotNull(data);
   }

   public byte[] getData() {
      return Assertions.assertNotNull(this.data);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Int8BinaryVector that = (Int8BinaryVector)o;
         return Objects.deepEquals(this.data, that.data);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   @Override
   public String toString() {
      return "Int8Vector{data=" + Arrays.toString(this.data) + ", dataType=" + this.getDataType() + '}';
   }
}
