package org.rocksdb;

public class TransactionOptions extends RocksObject implements TransactionalOptions<TransactionOptions> {
   public TransactionOptions() {
      super(newTransactionOptions());
   }

   @Override
   public boolean isSetSnapshot() {
      assert this.isOwningHandle();
      return isSetSnapshot(this.nativeHandle_);
   }

   public TransactionOptions setSetSnapshot(boolean var1) {
      assert this.isOwningHandle();
      setSetSnapshot(this.nativeHandle_, var1);
      return this;
   }

   public boolean isDeadlockDetect() {
      assert this.isOwningHandle();
      return isDeadlockDetect(this.nativeHandle_);
   }

   public TransactionOptions setDeadlockDetect(boolean var1) {
      assert this.isOwningHandle();
      setDeadlockDetect(this.nativeHandle_, var1);
      return this;
   }

   public long getLockTimeout() {
      assert this.isOwningHandle();
      return getLockTimeout(this.nativeHandle_);
   }

   public TransactionOptions setLockTimeout(long var1) {
      assert this.isOwningHandle();
      setLockTimeout(this.nativeHandle_, var1);
      return this;
   }

   public long getExpiration() {
      assert this.isOwningHandle();
      return getExpiration(this.nativeHandle_);
   }

   public TransactionOptions setExpiration(long var1) {
      assert this.isOwningHandle();
      setExpiration(this.nativeHandle_, var1);
      return this;
   }

   public long getDeadlockDetectDepth() {
      return getDeadlockDetectDepth(this.nativeHandle_);
   }

   public TransactionOptions setDeadlockDetectDepth(long var1) {
      setDeadlockDetectDepth(this.nativeHandle_, var1);
      return this;
   }

   public long getMaxWriteBatchSize() {
      return getMaxWriteBatchSize(this.nativeHandle_);
   }

   public TransactionOptions setMaxWriteBatchSize(long var1) {
      setMaxWriteBatchSize(this.nativeHandle_, var1);
      return this;
   }

   private static native long newTransactionOptions();

   private static native boolean isSetSnapshot(long var0);

   private static native void setSetSnapshot(long var0, boolean var2);

   private static native boolean isDeadlockDetect(long var0);

   private static native void setDeadlockDetect(long var0, boolean var2);

   private static native long getLockTimeout(long var0);

   private static native void setLockTimeout(long var0, long var2);

   private static native long getExpiration(long var0);

   private static native void setExpiration(long var0, long var2);

   private static native long getDeadlockDetectDepth(long var0);

   private static native void setDeadlockDetectDepth(long var0, long var2);

   private static native long getMaxWriteBatchSize(long var0);

   private static native void setMaxWriteBatchSize(long var0, long var2);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
