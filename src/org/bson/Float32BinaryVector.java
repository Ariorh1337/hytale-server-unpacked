package org.bson;

import java.util.Arrays;
import org.bson.assertions.Assertions;

public final class Float32BinaryVector extends BinaryVector {
   private final float[] data;

   Float32BinaryVector(float[] vectorData) {
      super(BinaryVector.DataType.FLOAT32);
      this.data = Assertions.assertNotNull(vectorData);
   }

   public float[] getData() {
      return Assertions.assertNotNull(this.data);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Float32BinaryVector that = (Float32BinaryVector)o;
         return Arrays.equals(this.data, that.data);
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
      return "Float32Vector{data=" + Arrays.toString(this.data) + ", dataType=" + this.getDataType() + '}';
   }
}
