package org.rocksdb;

public class TraceOptions {
   private final long maxTraceFileSize;

   public TraceOptions() {
      this.maxTraceFileSize = 68719476736L;
   }

   public TraceOptions(long var1) {
      this.maxTraceFileSize = var1;
   }

   public long getMaxTraceFileSize() {
      return this.maxTraceFileSize;
   }
}
