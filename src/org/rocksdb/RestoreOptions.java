package org.rocksdb;

public class RestoreOptions extends RocksObject {
   public RestoreOptions(boolean var1) {
      super(newRestoreOptions(var1));
   }

   private static native long newRestoreOptions(boolean var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
