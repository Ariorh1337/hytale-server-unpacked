package org.rocksdb;

public class WriteBufferManager extends RocksObject {
   private final boolean allowStall_;

   public WriteBufferManager(long var1, Cache var3, boolean var4) {
      super(newWriteBufferManagerInstance(var1, var3.nativeHandle_, var4));
      this.allowStall_ = var4;
   }

   public WriteBufferManager(long var1, Cache var3) {
      this(var1, var3, false);
   }

   public boolean allowStall() {
      return this.allowStall_;
   }

   private static long newWriteBufferManagerInstance(long var0, long var2, boolean var4) {
      RocksDB.loadLibrary();
      return newWriteBufferManager(var0, var2, var4);
   }

   private static native long newWriteBufferManager(long var0, long var2, boolean var4);

   @Override
   protected void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
