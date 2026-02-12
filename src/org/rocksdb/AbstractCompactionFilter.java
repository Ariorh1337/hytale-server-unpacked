package org.rocksdb;

public abstract class AbstractCompactionFilter<T extends AbstractSlice<?>> extends RocksObject {
   protected AbstractCompactionFilter(long var1) {
      super(var1);
   }

   @Override
   protected final native void disposeInternal(long var1);

   public static class Context {
      private final boolean fullCompaction;
      private final boolean manualCompaction;

      public Context(boolean var1, boolean var2) {
         this.fullCompaction = var1;
         this.manualCompaction = var2;
      }

      public boolean isFullCompaction() {
         return this.fullCompaction;
      }

      public boolean isManualCompaction() {
         return this.manualCompaction;
      }
   }
}
