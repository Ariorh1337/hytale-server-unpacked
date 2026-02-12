package org.rocksdb;

public abstract class RocksMutableObject extends AbstractNativeReference {
   private long nativeHandle_;
   private boolean owningHandle_;

   protected RocksMutableObject() {
   }

   protected RocksMutableObject(long var1) {
      this.nativeHandle_ = var1;
      this.owningHandle_ = true;
   }

   public synchronized void resetNativeHandle(long var1, boolean var3) {
      this.close();
      this.setNativeHandle(var1, var3);
   }

   public synchronized void setNativeHandle(long var1, boolean var3) {
      this.nativeHandle_ = var1;
      this.owningHandle_ = var3;
   }

   @Override
   protected synchronized boolean isOwningHandle() {
      return this.owningHandle_;
   }

   protected synchronized long getNativeHandle() {
      assert this.nativeHandle_ != 0L;
      return this.nativeHandle_;
   }

   @Override
   public final synchronized void close() {
      if (this.isOwningHandle()) {
         this.disposeInternal();
         this.owningHandle_ = false;
         this.nativeHandle_ = 0L;
      }
   }

   protected void disposeInternal() {
      this.disposeInternal(this.nativeHandle_);
   }

   protected abstract void disposeInternal(long var1);
}
