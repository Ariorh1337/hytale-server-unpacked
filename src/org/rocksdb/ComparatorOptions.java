package org.rocksdb;

public class ComparatorOptions extends RocksObject {
   public ComparatorOptions() {
      super(newComparatorOptions());
   }

   public ReusedSynchronisationType reusedSynchronisationType() {
      assert this.isOwningHandle();
      return ReusedSynchronisationType.getReusedSynchronisationType(reusedSynchronisationType(this.nativeHandle_));
   }

   public ComparatorOptions setReusedSynchronisationType(ReusedSynchronisationType var1) {
      assert this.isOwningHandle();
      setReusedSynchronisationType(this.nativeHandle_, var1.getValue());
      return this;
   }

   public boolean useDirectBuffer() {
      assert this.isOwningHandle();
      return useDirectBuffer(this.nativeHandle_);
   }

   public ComparatorOptions setUseDirectBuffer(boolean var1) {
      assert this.isOwningHandle();
      setUseDirectBuffer(this.nativeHandle_, var1);
      return this;
   }

   public int maxReusedBufferSize() {
      assert this.isOwningHandle();
      return maxReusedBufferSize(this.nativeHandle_);
   }

   public ComparatorOptions setMaxReusedBufferSize(int var1) {
      assert this.isOwningHandle();
      setMaxReusedBufferSize(this.nativeHandle_, var1);
      return this;
   }

   private static native long newComparatorOptions();

   private static native byte reusedSynchronisationType(long var0);

   private static native void setReusedSynchronisationType(long var0, byte var2);

   private static native boolean useDirectBuffer(long var0);

   private static native void setUseDirectBuffer(long var0, boolean var2);

   private static native int maxReusedBufferSize(long var0);

   private static native void setMaxReusedBufferSize(long var0, int var2);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
