package org.rocksdb;

public class SstFileReader extends RocksObject {
   public SstFileReader(Options var1) {
      super(newSstFileReader(var1.nativeHandle_));
   }

   public SstFileReaderIterator newIterator(ReadOptions var1) {
      assert this.isOwningHandle();
      long var2 = newIterator(this.nativeHandle_, var1.nativeHandle_);
      return new SstFileReaderIterator(this, var2);
   }

   public void open(String var1) throws RocksDBException {
      open(this.nativeHandle_, var1);
   }

   public void verifyChecksum() throws RocksDBException {
      verifyChecksum(this.nativeHandle_);
   }

   public TableProperties getTableProperties() throws RocksDBException {
      return getTableProperties(this.nativeHandle_);
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native long newIterator(long var0, long var2);

   private static native void open(long var0, String var2) throws RocksDBException;

   private static native long newSstFileReader(long var0);

   private static native void verifyChecksum(long var0) throws RocksDBException;

   private static native TableProperties getTableProperties(long var0) throws RocksDBException;
}
