package org.rocksdb;

public class PersistentCache extends RocksObject {
   public PersistentCache(Env var1, String var2, long var3, Logger var5, boolean var6) throws RocksDBException {
      super(newPersistentCache(var1.nativeHandle_, var2, var3, var5.nativeHandle_, var6));
   }

   private static native long newPersistentCache(long var0, String var2, long var3, long var5, boolean var7) throws RocksDBException;

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
