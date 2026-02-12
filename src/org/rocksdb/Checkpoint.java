package org.rocksdb;

public class Checkpoint extends RocksObject {
   public static Checkpoint create(RocksDB var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("RocksDB instance shall not be null.");
      } else if (!var0.isOwningHandle()) {
         throw new IllegalStateException("RocksDB instance must be initialized.");
      } else {
         return new Checkpoint(var0);
      }
   }

   public void createCheckpoint(String var1) throws RocksDBException {
      createCheckpoint(this.nativeHandle_, var1);
   }

   public ExportImportFilesMetaData exportColumnFamily(ColumnFamilyHandle var1, String var2) throws RocksDBException {
      return new ExportImportFilesMetaData(this.exportColumnFamily(this.nativeHandle_, var1.nativeHandle_, var2));
   }

   private Checkpoint(RocksDB var1) {
      super(newCheckpoint(var1.nativeHandle_));
   }

   private static native long newCheckpoint(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void createCheckpoint(long var0, String var2) throws RocksDBException;

   private native long exportColumnFamily(long var1, long var3, String var5) throws RocksDBException;
}
