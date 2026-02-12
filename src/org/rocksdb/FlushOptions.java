package org.rocksdb;

public class FlushOptions extends RocksObject {
   public FlushOptions() {
      super(newFlushOptionsInance());
   }

   public FlushOptions setWaitForFlush(boolean var1) {
      assert this.isOwningHandle();
      setWaitForFlush(this.nativeHandle_, var1);
      return this;
   }

   public boolean waitForFlush() {
      assert this.isOwningHandle();
      return waitForFlush(this.nativeHandle_);
   }

   public FlushOptions setAllowWriteStall(boolean var1) {
      assert this.isOwningHandle();
      setAllowWriteStall(this.nativeHandle_, var1);
      return this;
   }

   public boolean allowWriteStall() {
      assert this.isOwningHandle();
      return allowWriteStall(this.nativeHandle_);
   }

   private static long newFlushOptionsInance() {
      RocksDB.loadLibrary();
      return newFlushOptions();
   }

   private static native long newFlushOptions();

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setWaitForFlush(long var0, boolean var2);

   private static native boolean waitForFlush(long var0);

   private static native void setAllowWriteStall(long var0, boolean var2);

   private static native boolean allowWriteStall(long var0);
}
