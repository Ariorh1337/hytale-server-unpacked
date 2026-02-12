package org.rocksdb;

public class OptimisticTransactionOptions extends RocksObject implements TransactionalOptions<OptimisticTransactionOptions> {
   public OptimisticTransactionOptions() {
      super(newOptimisticTransactionOptions());
   }

   @Override
   public boolean isSetSnapshot() {
      assert this.isOwningHandle();
      return isSetSnapshot(this.nativeHandle_);
   }

   public OptimisticTransactionOptions setSetSnapshot(boolean var1) {
      assert this.isOwningHandle();
      setSetSnapshot(this.nativeHandle_, var1);
      return this;
   }

   public OptimisticTransactionOptions setComparator(AbstractComparator var1) {
      assert this.isOwningHandle();
      setComparator(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   private static native long newOptimisticTransactionOptions();

   private static native boolean isSetSnapshot(long var0);

   private static native void setSetSnapshot(long var0, boolean var2);

   private static native void setComparator(long var0, long var2);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
