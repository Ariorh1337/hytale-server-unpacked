package org.rocksdb;

import java.util.Objects;

public class WriteStallInfo {
   private final String columnFamilyName;
   private final WriteStallCondition currentCondition;
   private final WriteStallCondition previousCondition;

   WriteStallInfo(String var1, byte var2, byte var3) {
      this.columnFamilyName = var1;
      this.currentCondition = WriteStallCondition.fromValue(var2);
      this.previousCondition = WriteStallCondition.fromValue(var3);
   }

   public String getColumnFamilyName() {
      return this.columnFamilyName;
   }

   public WriteStallCondition getCurrentCondition() {
      return this.currentCondition;
   }

   public WriteStallCondition getPreviousCondition() {
      return this.previousCondition;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         WriteStallInfo var2 = (WriteStallInfo)var1;
         return Objects.equals(this.columnFamilyName, var2.columnFamilyName)
            && this.currentCondition == var2.currentCondition
            && this.previousCondition == var2.previousCondition;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.columnFamilyName, this.currentCondition, this.previousCondition);
   }

   @Override
   public String toString() {
      return "WriteStallInfo{columnFamilyName='"
         + this.columnFamilyName
         + '\''
         + ", currentCondition="
         + this.currentCondition
         + ", previousCondition="
         + this.previousCondition
         + '}';
   }
}
