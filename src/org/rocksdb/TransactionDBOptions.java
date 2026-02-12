package org.rocksdb;

public class TransactionDBOptions extends RocksObject {
   public TransactionDBOptions() {
      super(newTransactionDBOptions());
   }

   public long getMaxNumLocks() {
      assert this.isOwningHandle();
      return getMaxNumLocks(this.nativeHandle_);
   }

   public TransactionDBOptions setMaxNumLocks(long var1) {
      assert this.isOwningHandle();
      setMaxNumLocks(this.nativeHandle_, var1);
      return this;
   }

   public long getNumStripes() {
      assert this.isOwningHandle();
      return getNumStripes(this.nativeHandle_);
   }

   public TransactionDBOptions setNumStripes(long var1) {
      assert this.isOwningHandle();
      setNumStripes(this.nativeHandle_, var1);
      return this;
   }

   public long getTransactionLockTimeout() {
      assert this.isOwningHandle();
      return getTransactionLockTimeout(this.nativeHandle_);
   }

   public TransactionDBOptions setTransactionLockTimeout(long var1) {
      assert this.isOwningHandle();
      setTransactionLockTimeout(this.nativeHandle_, var1);
      return this;
   }

   public long getDefaultLockTimeout() {
      assert this.isOwningHandle();
      return getDefaultLockTimeout(this.nativeHandle_);
   }

   public TransactionDBOptions setDefaultLockTimeout(long var1) {
      assert this.isOwningHandle();
      setDefaultLockTimeout(this.nativeHandle_, var1);
      return this;
   }

   public TxnDBWritePolicy getWritePolicy() {
      assert this.isOwningHandle();
      return TxnDBWritePolicy.getTxnDBWritePolicy(getWritePolicy(this.nativeHandle_));
   }

   public TransactionDBOptions setWritePolicy(TxnDBWritePolicy var1) {
      assert this.isOwningHandle();
      setWritePolicy(this.nativeHandle_, var1.getValue());
      return this;
   }

   private static native long newTransactionDBOptions();

   private static native long getMaxNumLocks(long var0);

   private static native void setMaxNumLocks(long var0, long var2);

   private static native long getNumStripes(long var0);

   private static native void setNumStripes(long var0, long var2);

   private static native long getTransactionLockTimeout(long var0);

   private static native void setTransactionLockTimeout(long var0, long var2);

   private static native long getDefaultLockTimeout(long var0);

   private static native void setDefaultLockTimeout(long var0, long var2);

   private static native byte getWritePolicy(long var0);

   private static native void setWritePolicy(long var0, byte var2);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
