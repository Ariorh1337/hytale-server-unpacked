package org.rocksdb;

public class CompactionOptionsFIFO extends RocksObject {
   public CompactionOptionsFIFO() {
      super(newCompactionOptionsFIFO());
   }

   public CompactionOptionsFIFO setMaxTableFilesSize(long var1) {
      setMaxTableFilesSize(this.nativeHandle_, var1);
      return this;
   }

   public long maxTableFilesSize() {
      return maxTableFilesSize(this.nativeHandle_);
   }

   public CompactionOptionsFIFO setAllowCompaction(boolean var1) {
      setAllowCompaction(this.nativeHandle_, var1);
      return this;
   }

   public boolean allowCompaction() {
      return allowCompaction(this.nativeHandle_);
   }

   private static native long newCompactionOptionsFIFO();

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setMaxTableFilesSize(long var0, long var2);

   private static native long maxTableFilesSize(long var0);

   private static native void setAllowCompaction(long var0, boolean var2);

   private static native boolean allowCompaction(long var0);
}
