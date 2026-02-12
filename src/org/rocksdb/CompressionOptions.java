package org.rocksdb;

public class CompressionOptions extends RocksObject {
   public CompressionOptions() {
      super(newCompressionOptions());
   }

   public CompressionOptions setWindowBits(int var1) {
      setWindowBits(this.nativeHandle_, var1);
      return this;
   }

   public int windowBits() {
      return windowBits(this.nativeHandle_);
   }

   public CompressionOptions setLevel(int var1) {
      setLevel(this.nativeHandle_, var1);
      return this;
   }

   public int level() {
      return level(this.nativeHandle_);
   }

   public CompressionOptions setStrategy(int var1) {
      setStrategy(this.nativeHandle_, var1);
      return this;
   }

   public int strategy() {
      return strategy(this.nativeHandle_);
   }

   public CompressionOptions setMaxDictBytes(int var1) {
      setMaxDictBytes(this.nativeHandle_, var1);
      return this;
   }

   public int maxDictBytes() {
      return maxDictBytes(this.nativeHandle_);
   }

   public CompressionOptions setZStdMaxTrainBytes(int var1) {
      setZstdMaxTrainBytes(this.nativeHandle_, var1);
      return this;
   }

   public int zstdMaxTrainBytes() {
      return zstdMaxTrainBytes(this.nativeHandle_);
   }

   public CompressionOptions setEnabled(boolean var1) {
      setEnabled(this.nativeHandle_, var1);
      return this;
   }

   public boolean enabled() {
      return enabled(this.nativeHandle_);
   }

   private static native long newCompressionOptions();

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setWindowBits(long var0, int var2);

   private static native int windowBits(long var0);

   private static native void setLevel(long var0, int var2);

   private static native int level(long var0);

   private static native void setStrategy(long var0, int var2);

   private static native int strategy(long var0);

   private static native void setMaxDictBytes(long var0, int var2);

   private static native int maxDictBytes(long var0);

   private static native void setZstdMaxTrainBytes(long var0, int var2);

   private static native int zstdMaxTrainBytes(long var0);

   private static native void setEnabled(long var0, boolean var2);

   private static native boolean enabled(long var0);
}
