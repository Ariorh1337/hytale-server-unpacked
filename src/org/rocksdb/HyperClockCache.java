package org.rocksdb;

public class HyperClockCache extends Cache {
   public HyperClockCache(long var1, long var3, int var5, boolean var6) {
      super(newHyperClockCache(var1, var3, var5, var6));
   }

   @Override
   protected void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native long newHyperClockCache(long var0, long var2, int var4, boolean var5);
}
