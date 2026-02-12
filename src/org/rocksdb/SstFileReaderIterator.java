package org.rocksdb;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class SstFileReaderIterator extends AbstractRocksIterator<SstFileReader> {
   protected SstFileReaderIterator(SstFileReader var1, long var2) {
      super(var1, var2);
   }

   public byte[] key() {
      assert this.isOwningHandle();
      return key0(this.nativeHandle_);
   }

   public int key(ByteBuffer var1) {
      assert this.isOwningHandle();
      int var2;
      if (var1.isDirect()) {
         var2 = keyDirect0(this.nativeHandle_, var1, var1.position(), var1.remaining());
      } else {
         var2 = keyByteArray0(this.nativeHandle_, var1.array(), var1.arrayOffset() + var1.position(), var1.remaining());
      }

      ((Buffer)var1).limit(Math.min(var1.position() + var2, var1.limit()));
      return var2;
   }

   public byte[] value() {
      assert this.isOwningHandle();
      return value0(this.nativeHandle_);
   }

   public int value(ByteBuffer var1) {
      assert this.isOwningHandle();
      int var2;
      if (var1.isDirect()) {
         var2 = valueDirect0(this.nativeHandle_, var1, var1.position(), var1.remaining());
      } else {
         var2 = valueByteArray0(this.nativeHandle_, var1.array(), var1.arrayOffset() + var1.position(), var1.remaining());
      }

      ((Buffer)var1).limit(Math.min(var1.position() + var2, var1.limit()));
      return var2;
   }

   @Override
   final native void refresh1(long var1, long var3);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   @Override
   final boolean isValid0(long var1) {
      return isValid0Jni(var1);
   }

   private static native boolean isValid0Jni(long var0);

   @Override
   final void seekToFirst0(long var1) {
      seekToFirst0Jni(var1);
   }

   private static native void seekToFirst0Jni(long var0);

   @Override
   final void seekToLast0(long var1) {
      seekToLast0Jni(var1);
   }

   private static native void seekToLast0Jni(long var0);

   @Override
   final void next0(long var1) {
      next0Jni(var1);
   }

   private static native void next0Jni(long var0);

   @Override
   final void prev0(long var1) {
      prev0Jni(var1);
   }

   private static native void prev0Jni(long var0);

   @Override
   final void refresh0(long var1) throws RocksDBException {
      refresh0Jni(var1);
   }

   private static native void refresh0Jni(long var0) throws RocksDBException;

   @Override
   final void seek0(long var1, byte[] var3, int var4) {
      seek0Jni(var1, var3, var4);
   }

   private static native void seek0Jni(long var0, byte[] var2, int var3);

   @Override
   final void seekForPrev0(long var1, byte[] var3, int var4) {
      seekForPrev0Jni(var1, var3, var4);
   }

   private static native void seekForPrev0Jni(long var0, byte[] var2, int var3);

   @Override
   final void status0(long var1) throws RocksDBException {
      status0Jni(var1);
   }

   private static native void status0Jni(long var0) throws RocksDBException;

   @Override
   final void seekDirect0(long var1, ByteBuffer var3, int var4, int var5) {
      seekDirect0Jni(var1, var3, var4, var5);
   }

   private static native void seekDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

   @Override
   final void seekForPrevDirect0(long var1, ByteBuffer var3, int var4, int var5) {
      seekForPrevDirect0Jni(var1, var3, var4, var5);
   }

   private static native void seekForPrevDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

   @Override
   final void seekByteArray0(long var1, byte[] var3, int var4, int var5) {
      seekByteArray0Jni(var1, var3, var4, var5);
   }

   private static native void seekByteArray0Jni(long var0, byte[] var2, int var3, int var4);

   @Override
   final void seekForPrevByteArray0(long var1, byte[] var3, int var4, int var5) {
      seekForPrevByteArray0Jni(var1, var3, var4, var5);
   }

   private static native void seekForPrevByteArray0Jni(long var0, byte[] var2, int var3, int var4);

   private static native byte[] key0(long var0);

   private static native byte[] value0(long var0);

   private static native int keyDirect0(long var0, ByteBuffer var2, int var3, int var4);

   private static native int keyByteArray0(long var0, byte[] var2, int var3, int var4);

   private static native int valueDirect0(long var0, ByteBuffer var2, int var3, int var4);

   private static native int valueByteArray0(long var0, byte[] var2, int var3, int var4);
}
