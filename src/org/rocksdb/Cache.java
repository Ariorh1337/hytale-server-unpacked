package org.rocksdb;

public abstract class Cache extends RocksObject {
   protected Cache(long var1) {
      super(var1);
   }

   public long getUsage() {
      assert this.isOwningHandle();
      return getUsage(this.nativeHandle_);
   }

   public long getPinnedUsage() {
      assert this.isOwningHandle();
      return getPinnedUsage(this.nativeHandle_);
   }

   private static native long getUsage(long var0);

   private static native long getPinnedUsage(long var0);
}
