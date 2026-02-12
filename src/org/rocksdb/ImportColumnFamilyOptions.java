package org.rocksdb;

public class ImportColumnFamilyOptions extends RocksObject {
   public ImportColumnFamilyOptions() {
      super(newImportColumnFamilyOptions());
   }

   public boolean moveFiles() {
      return this.moveFiles(this.nativeHandle_);
   }

   public ImportColumnFamilyOptions setMoveFiles(boolean var1) {
      this.setMoveFiles(this.nativeHandle_, var1);
      return this;
   }

   private static native long newImportColumnFamilyOptions();

   private native boolean moveFiles(long var1);

   private native void setMoveFiles(long var1, boolean var3);

   @Override
   protected final native void disposeInternal(long var1);
}
