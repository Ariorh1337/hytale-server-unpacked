package org.rocksdb;

public interface TraceWriter {
   void write(Slice var1) throws RocksDBException;

   void closeWriter() throws RocksDBException;

   long getFileSize();
}
