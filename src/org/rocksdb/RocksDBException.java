package org.rocksdb;

public class RocksDBException extends Exception {
   private static final long serialVersionUID = -5187634878466267120L;
   private final Status status;

   public RocksDBException(String var1) {
      this(var1, null);
   }

   public RocksDBException(String var1, Status var2) {
      super(var1);
      this.status = var2;
   }

   public RocksDBException(Status var1) {
      super(var1.getState() != null ? var1.getState() : var1.getCodeString());
      this.status = var1;
   }

   public Status getStatus() {
      return this.status;
   }
}
