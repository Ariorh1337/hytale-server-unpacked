package org.rocksdb;

import java.util.List;

public abstract class RocksCallbackObject extends AbstractImmutableNativeReference {
   protected final long nativeHandle_;

   protected RocksCallbackObject(long... var1) {
      super(true);
      this.nativeHandle_ = this.initializeNative(var1);
   }

   static long[] toNativeHandleList(List<? extends RocksCallbackObject> var0) {
      if (var0 == null) {
         return new long[0];
      }

      int var1 = var0.size();
      long[] var2 = new long[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = ((RocksCallbackObject)var0.get(var3)).nativeHandle_;
      }

      return var2;
   }

   protected abstract long initializeNative(long... var1);

   @Override
   protected void disposeInternal() {
      disposeInternal(this.nativeHandle_);
   }

   private static native void disposeInternal(long var0);
}
