package org.rocksdb;

import java.nio.ByteBuffer;

public interface RocksIteratorInterface {
   boolean isValid();

   void seekToFirst();

   void seekToLast();

   void seek(byte[] var1);

   void seekForPrev(byte[] var1);

   void seek(ByteBuffer var1);

   void seekForPrev(ByteBuffer var1);

   void next();

   void prev();

   void status() throws RocksDBException;

   void refresh() throws RocksDBException;

   void refresh(Snapshot var1) throws RocksDBException;
}
