package org.rocksdb;

import java.util.Arrays;

public class ColumnFamilyDescriptor {
   private final byte[] columnFamilyName_;
   private final ColumnFamilyOptions columnFamilyOptions_;

   public ColumnFamilyDescriptor(byte[] var1) {
      this(var1, new ColumnFamilyOptions());
   }

   public ColumnFamilyDescriptor(byte[] var1, ColumnFamilyOptions var2) {
      this.columnFamilyName_ = var1;
      this.columnFamilyOptions_ = var2;
   }

   public byte[] getName() {
      return this.columnFamilyName_;
   }

   public ColumnFamilyOptions getOptions() {
      return this.columnFamilyOptions_;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ColumnFamilyDescriptor var2 = (ColumnFamilyDescriptor)var1;
         return Arrays.equals(this.columnFamilyName_, var2.columnFamilyName_)
            && this.columnFamilyOptions_.nativeHandle_ == var2.columnFamilyOptions_.nativeHandle_;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = (int)(this.columnFamilyOptions_.nativeHandle_ ^ this.columnFamilyOptions_.nativeHandle_ >>> 32);
      return 31 * var1 + Arrays.hashCode(this.columnFamilyName_);
   }
}
