package org.rocksdb;

public class WriteOptions extends RocksObject {
   public WriteOptions() {
      super(newWriteOptions());
   }

   WriteOptions(long var1) {
      super(var1);
      this.disOwnNativeHandle();
   }

   public WriteOptions(WriteOptions var1) {
      super(copyWriteOptions(var1.nativeHandle_));
   }

   public WriteOptions setSync(boolean var1) {
      setSync(this.nativeHandle_, var1);
      return this;
   }

   public boolean sync() {
      return sync(this.nativeHandle_);
   }

   public WriteOptions setDisableWAL(boolean var1) {
      setDisableWAL(this.nativeHandle_, var1);
      return this;
   }

   public boolean disableWAL() {
      return disableWAL(this.nativeHandle_);
   }

   public WriteOptions setIgnoreMissingColumnFamilies(boolean var1) {
      setIgnoreMissingColumnFamilies(this.nativeHandle_, var1);
      return this;
   }

   public boolean ignoreMissingColumnFamilies() {
      return ignoreMissingColumnFamilies(this.nativeHandle_);
   }

   public WriteOptions setNoSlowdown(boolean var1) {
      setNoSlowdown(this.nativeHandle_, var1);
      return this;
   }

   public boolean noSlowdown() {
      return noSlowdown(this.nativeHandle_);
   }

   public WriteOptions setLowPri(boolean var1) {
      setLowPri(this.nativeHandle_, var1);
      return this;
   }

   public boolean lowPri() {
      return lowPri(this.nativeHandle_);
   }

   public boolean memtableInsertHintPerBatch() {
      return memtableInsertHintPerBatch(this.nativeHandle_);
   }

   public WriteOptions setMemtableInsertHintPerBatch(boolean var1) {
      setMemtableInsertHintPerBatch(this.nativeHandle_, var1);
      return this;
   }

   private static native long newWriteOptions();

   private static native long copyWriteOptions(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setSync(long var0, boolean var2);

   private static native boolean sync(long var0);

   private static native void setDisableWAL(long var0, boolean var2);

   private static native boolean disableWAL(long var0);

   private static native void setIgnoreMissingColumnFamilies(long var0, boolean var2);

   private static native boolean ignoreMissingColumnFamilies(long var0);

   private static native void setNoSlowdown(long var0, boolean var2);

   private static native boolean noSlowdown(long var0);

   private static native void setLowPri(long var0, boolean var2);

   private static native boolean lowPri(long var0);

   private static native boolean memtableInsertHintPerBatch(long var0);

   private static native void setMemtableInsertHintPerBatch(long var0, boolean var2);
}
