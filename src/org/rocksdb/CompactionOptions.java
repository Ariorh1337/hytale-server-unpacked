package org.rocksdb;

public class CompactionOptions extends RocksObject {
   public CompactionOptions() {
      super(newCompactionOptions());
   }

   public CompressionType compression() {
      return CompressionType.getCompressionType(compression(this.nativeHandle_));
   }

   public CompactionOptions setCompression(CompressionType var1) {
      setCompression(this.nativeHandle_, var1.getValue());
      return this;
   }

   public long outputFileSizeLimit() {
      return outputFileSizeLimit(this.nativeHandle_);
   }

   public CompactionOptions setOutputFileSizeLimit(long var1) {
      setOutputFileSizeLimit(this.nativeHandle_, var1);
      return this;
   }

   public int maxSubcompactions() {
      return maxSubcompactions(this.nativeHandle_);
   }

   public CompactionOptions setMaxSubcompactions(int var1) {
      setMaxSubcompactions(this.nativeHandle_, var1);
      return this;
   }

   private static native long newCompactionOptions();

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native byte compression(long var0);

   private static native void setCompression(long var0, byte var2);

   private static native long outputFileSizeLimit(long var0);

   private static native void setOutputFileSizeLimit(long var0, long var2);

   private static native int maxSubcompactions(long var0);

   private static native void setMaxSubcompactions(long var0, int var2);
}
