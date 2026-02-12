package org.rocksdb;

/** @deprecated */
public class ClockCache extends Cache {
   /** @deprecated */
   public ClockCache(long var1) {
      super(newClockCache(var1, -1, false));
   }

   /** @deprecated */
   public ClockCache(long var1, int var3) {
      super(newClockCache(var1, var3, false));
   }

   /** @deprecated */
   public ClockCache(long var1, int var3, boolean var4) {
      super(newClockCache(var1, var3, var4));
   }

   private static native long newClockCache(long var0, int var2, boolean var3);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
