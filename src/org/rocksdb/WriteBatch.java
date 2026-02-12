package org.rocksdb;

import java.nio.ByteBuffer;

public class WriteBatch extends AbstractWriteBatch {
   public WriteBatch() {
      this(0);
   }

   public WriteBatch(int var1) {
      super(newWriteBatch(var1));
   }

   public WriteBatch(byte[] var1) {
      super(newWriteBatch(var1, var1.length));
   }

   public void iterate(WriteBatch.Handler var1) throws RocksDBException {
      iterate(this.nativeHandle_, var1.nativeHandle_);
   }

   public byte[] data() throws RocksDBException {
      return data(this.nativeHandle_);
   }

   public long getDataSize() {
      return getDataSize(this.nativeHandle_);
   }

   public boolean hasPut() {
      return hasPut(this.nativeHandle_);
   }

   public boolean hasDelete() {
      return hasDelete(this.nativeHandle_);
   }

   public boolean hasSingleDelete() {
      return hasSingleDelete(this.nativeHandle_);
   }

   public boolean hasDeleteRange() {
      return hasDeleteRange(this.nativeHandle_);
   }

   public boolean hasMerge() {
      return hasMerge(this.nativeHandle_);
   }

   public boolean hasBeginPrepare() {
      return hasBeginPrepare(this.nativeHandle_);
   }

   public boolean hasEndPrepare() {
      return hasEndPrepare(this.nativeHandle_);
   }

   public boolean hasCommit() {
      return hasCommit(this.nativeHandle_);
   }

   public boolean hasRollback() {
      return hasRollback(this.nativeHandle_);
   }

   @Override
   public WriteBatch getWriteBatch() {
      return this;
   }

   public void markWalTerminationPoint() {
      markWalTerminationPoint(this.nativeHandle_);
   }

   public WriteBatch.SavePoint getWalTerminationPoint() {
      return getWalTerminationPoint(this.nativeHandle_);
   }

   @Override
   WriteBatch getWriteBatch(long var1) {
      return this;
   }

   WriteBatch(long var1) {
      this(var1, false);
   }

   WriteBatch(long var1, boolean var3) {
      super(var1);
      if (!var3) {
         this.disOwnNativeHandle();
      }
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   @Override
   final int count0(long var1) {
      return count0Jni(var1);
   }

   private static native int count0Jni(long var0);

   @Override
   final void put(long var1, byte[] var3, int var4, byte[] var5, int var6) {
      putJni(var1, var3, var4, var5, var6);
   }

   private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

   @Override
   final void put(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) {
      putJni(var1, var3, var4, var5, var6, var7);
   }

   private static native void putJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

   @Override
   final void putDirect(long var1, ByteBuffer var3, int var4, int var5, ByteBuffer var6, int var7, int var8, long var9) {
      putDirectJni(var1, var3, var4, var5, var6, var7, var8, var9);
   }

   private static native void putDirectJni(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, long var8);

   @Override
   final void merge(long var1, byte[] var3, int var4, byte[] var5, int var6) {
      mergeJni(var1, var3, var4, var5, var6);
   }

   private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

   @Override
   final void merge(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) {
      mergeJni(var1, var3, var4, var5, var6, var7);
   }

   private static native void mergeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

   @Override
   final void delete(long var1, byte[] var3, int var4) throws RocksDBException {
      deleteJni(var1, var3, var4);
   }

   private static native void deleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

   @Override
   final void delete(long var1, byte[] var3, int var4, long var5) throws RocksDBException {
      deleteJni(var1, var3, var4, var5);
   }

   private static native void deleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

   @Override
   final void singleDelete(long var1, byte[] var3, int var4) throws RocksDBException {
      singleDeleteJni(var1, var3, var4);
   }

   private static native void singleDeleteJni(long var0, byte[] var2, int var3) throws RocksDBException;

   @Override
   final void singleDelete(long var1, byte[] var3, int var4, long var5) throws RocksDBException {
      singleDeleteJni(var1, var3, var4, var5);
   }

   private static native void singleDeleteJni(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

   @Override
   final void deleteDirect(long var1, ByteBuffer var3, int var4, int var5, long var6) throws RocksDBException {
      deleteDirectJni(var1, var3, var4, var5, var6);
   }

   private static native void deleteDirectJni(long var0, ByteBuffer var2, int var3, int var4, long var5) throws RocksDBException;

   @Override
   final void deleteRange(long var1, byte[] var3, int var4, byte[] var5, int var6) {
      deleteRangeJni(var1, var3, var4, var5, var6);
   }

   private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5);

   @Override
   final void deleteRange(long var1, byte[] var3, int var4, byte[] var5, int var6, long var7) {
      deleteRangeJni(var1, var3, var4, var5, var6, var7);
   }

   private static native void deleteRangeJni(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6);

   @Override
   final void putLogData(long var1, byte[] var3, int var4) throws RocksDBException {
      putLogDataJni(var1, var3, var4);
   }

   private static native void putLogDataJni(long var0, byte[] var2, int var3) throws RocksDBException;

   @Override
   final void clear0(long var1) {
      clear0Jni(var1);
   }

   private static native void clear0Jni(long var0);

   @Override
   final void setSavePoint0(long var1) {
      setSavePoint0Jni(var1);
   }

   private static native void setSavePoint0Jni(long var0);

   @Override
   final void rollbackToSavePoint0(long var1) {
      rollbackToSavePoint0Jni(var1);
   }

   private static native void rollbackToSavePoint0Jni(long var0);

   @Override
   final void popSavePoint(long var1) throws RocksDBException {
      popSavePointJni(var1);
   }

   private static native void popSavePointJni(long var0) throws RocksDBException;

   @Override
   final void setMaxBytes(long var1, long var3) {
      setMaxBytesJni(var1, var3);
   }

   private static native void setMaxBytesJni(long var0, long var2);

   private static native long newWriteBatch(int var0);

   private static native long newWriteBatch(byte[] var0, int var1);

   private static native void iterate(long var0, long var2) throws RocksDBException;

   private static native byte[] data(long var0) throws RocksDBException;

   private static native long getDataSize(long var0);

   private static native boolean hasPut(long var0);

   private static native boolean hasDelete(long var0);

   private static native boolean hasSingleDelete(long var0);

   private static native boolean hasDeleteRange(long var0);

   private static native boolean hasMerge(long var0);

   private static native boolean hasBeginPrepare(long var0);

   private static native boolean hasEndPrepare(long var0);

   private static native boolean hasCommit(long var0);

   private static native boolean hasRollback(long var0);

   private static native void markWalTerminationPoint(long var0);

   private static native WriteBatch.SavePoint getWalTerminationPoint(long var0);

   public abstract static class Handler extends RocksCallbackObject {
      public Handler() {
         super(0L);
      }

      @Override
      protected long initializeNative(long... var1) {
         return this.createNewHandler0();
      }

      public abstract void put(int var1, byte[] var2, byte[] var3) throws RocksDBException;

      public abstract void put(byte[] var1, byte[] var2);

      public abstract void merge(int var1, byte[] var2, byte[] var3) throws RocksDBException;

      public abstract void merge(byte[] var1, byte[] var2);

      public abstract void delete(int var1, byte[] var2) throws RocksDBException;

      public abstract void delete(byte[] var1);

      public abstract void singleDelete(int var1, byte[] var2) throws RocksDBException;

      public abstract void singleDelete(byte[] var1);

      public abstract void deleteRange(int var1, byte[] var2, byte[] var3) throws RocksDBException;

      public abstract void deleteRange(byte[] var1, byte[] var2);

      public abstract void logData(byte[] var1);

      public abstract void putBlobIndex(int var1, byte[] var2, byte[] var3) throws RocksDBException;

      public abstract void markBeginPrepare() throws RocksDBException;

      public abstract void markEndPrepare(byte[] var1) throws RocksDBException;

      public abstract void markNoop(boolean var1) throws RocksDBException;

      public abstract void markRollback(byte[] var1) throws RocksDBException;

      public abstract void markCommit(byte[] var1) throws RocksDBException;

      public abstract void markCommitWithTimestamp(byte[] var1, byte[] var2) throws RocksDBException;

      public boolean shouldContinue() {
         return true;
      }

      private native long createNewHandler0();
   }

   public static class SavePoint {
      private long size;
      private long count;
      private long contentFlags;

      public SavePoint(long var1, long var3, long var5) {
         this.size = var1;
         this.count = var3;
         this.contentFlags = var5;
      }

      public void clear() {
         this.size = 0L;
         this.count = 0L;
         this.contentFlags = 0L;
      }

      public long getSize() {
         return this.size;
      }

      public long getCount() {
         return this.count;
      }

      public long getContentFlags() {
         return this.contentFlags;
      }

      public boolean isCleared() {
         return (this.size | this.count | this.contentFlags) == 0L;
      }
   }
}
