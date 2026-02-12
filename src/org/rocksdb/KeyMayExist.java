package org.rocksdb;

import java.util.Objects;

public class KeyMayExist {
   public final KeyMayExist.KeyMayExistEnum exists;
   public final int valueLength;

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         KeyMayExist var2 = (KeyMayExist)var1;
         return this.valueLength == var2.valueLength && this.exists == var2.exists;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.exists, this.valueLength);
   }

   public KeyMayExist(KeyMayExist.KeyMayExistEnum var1, int var2) {
      this.exists = var1;
      this.valueLength = var2;
   }

   public enum KeyMayExistEnum {
      kNotExist,
      kExistsWithoutValue,
      kExistsWithValue;
   }
}
