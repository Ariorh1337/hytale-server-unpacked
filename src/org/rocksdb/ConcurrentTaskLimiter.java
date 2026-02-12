package org.rocksdb;

public abstract class ConcurrentTaskLimiter extends RocksObject {
   protected ConcurrentTaskLimiter(long var1) {
      super(var1);
   }

   public abstract String name();

   public abstract ConcurrentTaskLimiter setMaxOutstandingTask(int var1);

   public abstract ConcurrentTaskLimiter resetMaxOutstandingTask();

   public abstract int outstandingTask();
}
