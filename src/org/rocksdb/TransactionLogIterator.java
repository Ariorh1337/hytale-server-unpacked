package org.rocksdb;

public class TransactionLogIterator extends RocksObject {
   public boolean isValid() {
      return isValid(this.nativeHandle_);
   }

   public void next() {
      next(this.nativeHandle_);
   }

   public void status() throws RocksDBException {
      status(this.nativeHandle_);
   }

   public TransactionLogIterator.BatchResult getBatch() {
      assert this.isValid();
      return getBatch(this.nativeHandle_);
   }

   TransactionLogIterator(long var1) {
      super(var1);
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native boolean isValid(long var0);

   private static native void next(long var0);

   private static native void status(long var0) throws RocksDBException;

   private static native TransactionLogIterator.BatchResult getBatch(long var0);

   public static final class BatchResult {
      private final long sequenceNumber_;
      private final WriteBatch writeBatch_;

      public BatchResult(long var1, long var3) {
         this.sequenceNumber_ = var1;
         this.writeBatch_ = new WriteBatch(var3, true);
      }

      public long sequenceNumber() {
         return this.sequenceNumber_;
      }

      public WriteBatch writeBatch() {
         return this.writeBatch_;
      }
   }
}
