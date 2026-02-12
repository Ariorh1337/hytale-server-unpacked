package org.rocksdb;

public class EnvOptions extends RocksObject {
   private RateLimiter rateLimiter;

   public EnvOptions() {
      super(newEnvOptionsInstance());
   }

   public EnvOptions(DBOptions var1) {
      super(newEnvOptions(var1.nativeHandle_));
   }

   public EnvOptions setUseMmapReads(boolean var1) {
      setUseMmapReads(this.nativeHandle_, var1);
      return this;
   }

   public boolean useMmapReads() {
      assert this.isOwningHandle();
      return useMmapReads(this.nativeHandle_);
   }

   public EnvOptions setUseMmapWrites(boolean var1) {
      setUseMmapWrites(this.nativeHandle_, var1);
      return this;
   }

   public boolean useMmapWrites() {
      assert this.isOwningHandle();
      return useMmapWrites(this.nativeHandle_);
   }

   public EnvOptions setUseDirectReads(boolean var1) {
      setUseDirectReads(this.nativeHandle_, var1);
      return this;
   }

   public boolean useDirectReads() {
      assert this.isOwningHandle();
      return useDirectReads(this.nativeHandle_);
   }

   public EnvOptions setUseDirectWrites(boolean var1) {
      setUseDirectWrites(this.nativeHandle_, var1);
      return this;
   }

   public boolean useDirectWrites() {
      assert this.isOwningHandle();
      return useDirectWrites(this.nativeHandle_);
   }

   public EnvOptions setAllowFallocate(boolean var1) {
      setAllowFallocate(this.nativeHandle_, var1);
      return this;
   }

   public boolean allowFallocate() {
      assert this.isOwningHandle();
      return allowFallocate(this.nativeHandle_);
   }

   public EnvOptions setSetFdCloexec(boolean var1) {
      setSetFdCloexec(this.nativeHandle_, var1);
      return this;
   }

   public boolean setFdCloexec() {
      assert this.isOwningHandle();
      return setFdCloexec(this.nativeHandle_);
   }

   public EnvOptions setBytesPerSync(long var1) {
      setBytesPerSync(this.nativeHandle_, var1);
      return this;
   }

   public long bytesPerSync() {
      assert this.isOwningHandle();
      return bytesPerSync(this.nativeHandle_);
   }

   public EnvOptions setFallocateWithKeepSize(boolean var1) {
      setFallocateWithKeepSize(this.nativeHandle_, var1);
      return this;
   }

   public boolean fallocateWithKeepSize() {
      assert this.isOwningHandle();
      return fallocateWithKeepSize(this.nativeHandle_);
   }

   public EnvOptions setCompactionReadaheadSize(long var1) {
      setCompactionReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   public long compactionReadaheadSize() {
      assert this.isOwningHandle();
      return compactionReadaheadSize(this.nativeHandle_);
   }

   public EnvOptions setWritableFileMaxBufferSize(long var1) {
      setWritableFileMaxBufferSize(this.nativeHandle_, var1);
      return this;
   }

   public long writableFileMaxBufferSize() {
      assert this.isOwningHandle();
      return writableFileMaxBufferSize(this.nativeHandle_);
   }

   public EnvOptions setRateLimiter(RateLimiter var1) {
      this.rateLimiter = var1;
      setRateLimiter(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public RateLimiter rateLimiter() {
      assert this.isOwningHandle();
      return this.rateLimiter;
   }

   private static long newEnvOptionsInstance() {
      RocksDB.loadLibrary();
      return newEnvOptions();
   }

   private static native long newEnvOptions();

   private static native long newEnvOptions(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setUseMmapReads(long var0, boolean var2);

   private static native boolean useMmapReads(long var0);

   private static native void setUseMmapWrites(long var0, boolean var2);

   private static native boolean useMmapWrites(long var0);

   private static native void setUseDirectReads(long var0, boolean var2);

   private static native boolean useDirectReads(long var0);

   private static native void setUseDirectWrites(long var0, boolean var2);

   private static native boolean useDirectWrites(long var0);

   private static native void setAllowFallocate(long var0, boolean var2);

   private static native boolean allowFallocate(long var0);

   private static native void setSetFdCloexec(long var0, boolean var2);

   private static native boolean setFdCloexec(long var0);

   private static native void setBytesPerSync(long var0, long var2);

   private static native long bytesPerSync(long var0);

   private static native void setFallocateWithKeepSize(long var0, boolean var2);

   private static native boolean fallocateWithKeepSize(long var0);

   private static native void setCompactionReadaheadSize(long var0, long var2);

   private static native long compactionReadaheadSize(long var0);

   private static native void setWritableFileMaxBufferSize(long var0, long var2);

   private static native long writableFileMaxBufferSize(long var0);

   private static native void setRateLimiter(long var0, long var2);
}
