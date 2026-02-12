package org.rocksdb;

public abstract class AbstractTransactionNotifier extends RocksCallbackObject {
   protected AbstractTransactionNotifier() {
      super();
   }

   public abstract void snapshotCreated(Snapshot var1);

   private void snapshotCreated(long var1) {
      this.snapshotCreated(new Snapshot(var1));
   }

   @Override
   protected long initializeNative(long... var1) {
      return this.createNewTransactionNotifier();
   }

   private native long createNewTransactionNotifier();

   @Override
   protected void disposeInternal() {
      this.disposeInternal(this.nativeHandle_);
   }

   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
