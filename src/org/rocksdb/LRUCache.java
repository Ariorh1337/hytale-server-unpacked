package org.rocksdb;

public class LRUCache extends Cache {
   public LRUCache(long var1) {
      this(var1, -1, false, 0.0, 0.0);
   }

   public LRUCache(long var1, int var3) {
      super(newLRUCache(var1, var3, false, 0.0, 0.0));
   }

   public LRUCache(long var1, int var3, boolean var4) {
      super(newLRUCache(var1, var3, var4, 0.0, 0.0));
   }

   public LRUCache(long var1, int var3, boolean var4, double var5) {
      super(newLRUCache(var1, var3, var4, var5, 0.0));
   }

   public LRUCache(long var1, int var3, boolean var4, double var5, double var7) {
      super(newLRUCache(var1, var3, var4, var5, var7));
   }

   private static native long newLRUCache(long var0, int var2, boolean var3, double var4, double var6);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
