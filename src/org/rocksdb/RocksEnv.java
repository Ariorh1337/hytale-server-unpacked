package org.rocksdb;

public class RocksEnv extends Env {
   RocksEnv(long var1) {
      super(var1);
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
