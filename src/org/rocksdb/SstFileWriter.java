package org.rocksdb;

import java.nio.ByteBuffer;

public class SstFileWriter extends RocksObject {
   public SstFileWriter(EnvOptions var1, Options var2) {
      super(newSstFileWriter(var1.nativeHandle_, var2.nativeHandle_));
   }

   public void open(String var1) throws RocksDBException {
      open(this.nativeHandle_, var1);
   }

   public void put(Slice var1, Slice var2) throws RocksDBException {
      put(this.nativeHandle_, var1.getNativeHandle(), var2.getNativeHandle());
   }

   public void put(DirectSlice var1, DirectSlice var2) throws RocksDBException {
      put(this.nativeHandle_, var1.getNativeHandle(), var2.getNativeHandle());
   }

   public void put(ByteBuffer var1, ByteBuffer var2) throws RocksDBException {
      assert var1.isDirect() && var2.isDirect();
      putDirect(this.nativeHandle_, var1, var1.position(), var1.remaining(), var2, var2.position(), var2.remaining());
      var1.position(var1.limit());
      var2.position(var2.limit());
   }

   public void put(byte[] var1, byte[] var2) throws RocksDBException {
      put(this.nativeHandle_, var1, var2);
   }

   public void merge(Slice var1, Slice var2) throws RocksDBException {
      merge(this.nativeHandle_, var1.getNativeHandle(), var2.getNativeHandle());
   }

   public void merge(byte[] var1, byte[] var2) throws RocksDBException {
      merge(this.nativeHandle_, var1, var2);
   }

   public void merge(DirectSlice var1, DirectSlice var2) throws RocksDBException {
      merge(this.nativeHandle_, var1.getNativeHandle(), var2.getNativeHandle());
   }

   public void delete(Slice var1) throws RocksDBException {
      delete(this.nativeHandle_, var1.getNativeHandle());
   }

   public void delete(DirectSlice var1) throws RocksDBException {
      delete(this.nativeHandle_, var1.getNativeHandle());
   }

   public void delete(byte[] var1) throws RocksDBException {
      delete(this.nativeHandle_, var1);
   }

   public void finish() throws RocksDBException {
      finish(this.nativeHandle_);
   }

   public long fileSize() throws RocksDBException {
      return fileSize(this.nativeHandle_);
   }

   private static native long newSstFileWriter(long var0, long var2, long var4, byte var6);

   private static native long newSstFileWriter(long var0, long var2);

   private static native void open(long var0, String var2) throws RocksDBException;

   private static native void put(long var0, long var2, long var4) throws RocksDBException;

   private static native void put(long var0, byte[] var2, byte[] var3) throws RocksDBException;

   private static native void putDirect(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7) throws RocksDBException;

   private static native long fileSize(long var0) throws RocksDBException;

   private static native void merge(long var0, long var2, long var4) throws RocksDBException;

   private static native void merge(long var0, byte[] var2, byte[] var3) throws RocksDBException;

   private static native void delete(long var0, long var2) throws RocksDBException;

   private static native void delete(long var0, byte[] var2) throws RocksDBException;

   private static native void finish(long var0) throws RocksDBException;

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
