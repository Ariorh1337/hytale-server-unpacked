package org.rocksdb;

public class CompactionOptionsUniversal extends RocksObject {
   public CompactionOptionsUniversal() {
      super(newCompactionOptionsUniversal());
   }

   public CompactionOptionsUniversal setSizeRatio(int var1) {
      setSizeRatio(this.nativeHandle_, var1);
      return this;
   }

   public int sizeRatio() {
      return sizeRatio(this.nativeHandle_);
   }

   public CompactionOptionsUniversal setMinMergeWidth(int var1) {
      setMinMergeWidth(this.nativeHandle_, var1);
      return this;
   }

   public int minMergeWidth() {
      return minMergeWidth(this.nativeHandle_);
   }

   public CompactionOptionsUniversal setMaxMergeWidth(int var1) {
      setMaxMergeWidth(this.nativeHandle_, var1);
      return this;
   }

   public int maxMergeWidth() {
      return maxMergeWidth(this.nativeHandle_);
   }

   public CompactionOptionsUniversal setMaxSizeAmplificationPercent(int var1) {
      setMaxSizeAmplificationPercent(this.nativeHandle_, var1);
      return this;
   }

   public int maxSizeAmplificationPercent() {
      return maxSizeAmplificationPercent(this.nativeHandle_);
   }

   public CompactionOptionsUniversal setCompressionSizePercent(int var1) {
      setCompressionSizePercent(this.nativeHandle_, var1);
      return this;
   }

   public int compressionSizePercent() {
      return compressionSizePercent(this.nativeHandle_);
   }

   public CompactionOptionsUniversal setStopStyle(CompactionStopStyle var1) {
      setStopStyle(this.nativeHandle_, var1.getValue());
      return this;
   }

   public CompactionStopStyle stopStyle() {
      return CompactionStopStyle.getCompactionStopStyle(stopStyle(this.nativeHandle_));
   }

   public CompactionOptionsUniversal setAllowTrivialMove(boolean var1) {
      setAllowTrivialMove(this.nativeHandle_, var1);
      return this;
   }

   public boolean allowTrivialMove() {
      return allowTrivialMove(this.nativeHandle_);
   }

   private static native long newCompactionOptionsUniversal();

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setSizeRatio(long var0, int var2);

   private static native int sizeRatio(long var0);

   private static native void setMinMergeWidth(long var0, int var2);

   private static native int minMergeWidth(long var0);

   private static native void setMaxMergeWidth(long var0, int var2);

   private static native int maxMergeWidth(long var0);

   private static native void setMaxSizeAmplificationPercent(long var0, int var2);

   private static native int maxSizeAmplificationPercent(long var0);

   private static native void setCompressionSizePercent(long var0, int var2);

   private static native int compressionSizePercent(long var0);

   private static native void setStopStyle(long var0, byte var2);

   private static native byte stopStyle(long var0);

   private static native void setAllowTrivialMove(long var0, boolean var2);

   private static native boolean allowTrivialMove(long var0);
}
