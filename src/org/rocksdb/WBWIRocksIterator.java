package org.rocksdb;

import java.nio.ByteBuffer;

public class WBWIRocksIterator extends AbstractRocksIterator<WriteBatchWithIndex> {
   private final WBWIRocksIterator.WriteEntry entry = new WBWIRocksIterator.WriteEntry();

   protected WBWIRocksIterator(WriteBatchWithIndex var1, long var2) {
      super(var1, var2);
   }

   public WBWIRocksIterator.WriteEntry entry() {
      assert this.isOwningHandle();
      long[] var1 = entry1(this.nativeHandle_);
      this.entry.type = WBWIRocksIterator.WriteType.fromId((byte)var1[0]);
      this.entry.key.resetNativeHandle(var1[1], var1[1] != 0L);
      this.entry.value.resetNativeHandle(var1[2], var1[2] != 0L);
      return this.entry;
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

   private static native long[] entry1(long var0);

   @Override
   public void close() {
      this.entry.close();
      super.close();
   }

   public static class WriteEntry implements AutoCloseable {
      WBWIRocksIterator.WriteType type = null;
      final DirectSlice key;
      final DirectSlice value;

      private WriteEntry() {
         this.key = new DirectSlice();
         this.value = new DirectSlice();
      }

      public WriteEntry(WBWIRocksIterator.WriteType var1, DirectSlice var2, DirectSlice var3) {
         this.type = var1;
         this.key = var2;
         this.value = var3;
      }

      public WBWIRocksIterator.WriteType getType() {
         return this.type;
      }

      public DirectSlice getKey() {
         return this.key;
      }

      public DirectSlice getValue() {
         return this.value.isOwningHandle() ? this.value : null;
      }

      @Override
      public int hashCode() {
         return this.key == null ? 0 : this.key.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == null) {
            return false;
         }

         if (this == var1) {
            return true;
         }

         if (!(var1 instanceof WBWIRocksIterator.WriteEntry)) {
            return false;
         }

         WBWIRocksIterator.WriteEntry var2 = (WBWIRocksIterator.WriteEntry)var1;
         return this.type.equals(var2.type) && this.key.equals(var2.key) && this.value.equals(var2.value);
      }

      @Override
      public void close() {
         this.value.close();
         this.key.close();
      }
   }

   public enum WriteType {
      PUT((byte)0),
      MERGE((byte)1),
      DELETE((byte)2),
      SINGLE_DELETE((byte)3),
      DELETE_RANGE((byte)4),
      LOG((byte)5),
      XID((byte)6);

      final byte id;

      WriteType(byte var3) {
         this.id = var3;
      }

      public static WBWIRocksIterator.WriteType fromId(byte var0) {
         for (WBWIRocksIterator.WriteType var4 : values()) {
            if (var0 == var4.id) {
               return var4;
            }
         }

         throw new IllegalArgumentException("No WriteType with id=" + var0);
      }
   }
}
