package org.rocksdb;

import java.nio.ByteBuffer;

public class DirectSlice extends AbstractSlice<ByteBuffer> {
   public static final DirectSlice NONE = new DirectSlice();
   private final boolean internalBuffer;
   private volatile boolean cleared = false;
   private volatile long internalBufferOffset = 0L;

   DirectSlice() {
      this.internalBuffer = false;
   }

   public DirectSlice(String var1) {
      super(createNewSliceFromString(var1));
      this.internalBuffer = true;
   }

   public DirectSlice(ByteBuffer var1, int var2) {
      super(createNewDirectSlice0(ensureDirect(var1), var2));
      this.internalBuffer = false;
   }

   public DirectSlice(ByteBuffer var1) {
      super(createNewDirectSlice1(ensureDirect(var1)));
      this.internalBuffer = false;
   }

   private static ByteBuffer ensureDirect(ByteBuffer var0) {
      if (!var0.isDirect()) {
         throw new IllegalArgumentException("The ByteBuffer must be direct");
      } else {
         return var0;
      }
   }

   public byte get(int var1) {
      return get0(this.getNativeHandle(), var1);
   }

   @Override
   public void clear() {
      clear0(this.getNativeHandle(), !this.cleared && this.internalBuffer, this.internalBufferOffset);
      this.cleared = true;
   }

   @Override
   public void removePrefix(int var1) {
      removePrefix0(this.getNativeHandle(), var1);
      this.internalBufferOffset += var1;
   }

   public void setLength(int var1) {
      setLength0(this.getNativeHandle(), var1);
   }

   @Override
   protected void disposeInternal() {
      long var1 = this.getNativeHandle();
      if (!this.cleared && this.internalBuffer) {
         disposeInternalBuf(var1, this.internalBufferOffset);
      }

      this.disposeInternal(var1);
   }

   private static native long createNewDirectSlice0(ByteBuffer var0, int var1);

   private static native long createNewDirectSlice1(ByteBuffer var0);

   protected final native ByteBuffer data0(long var1);

   private static native byte get0(long var0, int var2);

   private static native void clear0(long var0, boolean var2, long var3);

   private static native void removePrefix0(long var0, int var2);

   private static native void setLength0(long var0, int var2);

   private static native void disposeInternalBuf(long var0, long var2);
}
