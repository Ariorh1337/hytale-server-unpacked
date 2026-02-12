package org.rocksdb;

public abstract class RocksObject extends AbstractImmutableNativeReference {
   protected final long nativeHandle_;

   protected RocksObject(long var1) {
      super(true);
      this.nativeHandle_ = var1;
   }

   @Override
   protected void disposeInternal() {
      this.disposeInternal(this.nativeHandle_);
   }

   protected abstract void disposeInternal(long var1);

   public long getNativeHandle() {
      return this.nativeHandle_;
   }
}
