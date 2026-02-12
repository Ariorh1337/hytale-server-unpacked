package org.rocksdb;

public class GetStatus {
   public final Status status;
   public final int requiredSize;

   GetStatus(Status var1, int var2) {
      this.status = var1;
      this.requiredSize = var2;
   }

   static GetStatus fromStatusCode(Status.Code var0, int var1) {
      return new GetStatus(new Status(var0, Status.SubCode.getSubCode((byte)0), null), var1);
   }
}
