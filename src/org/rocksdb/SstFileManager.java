package org.rocksdb;

import java.util.Map;

public final class SstFileManager extends RocksObject {
   public static final long RATE_BYTES_PER_SEC_DEFAULT = 0L;
   public static final boolean DELETE_EXISTING_TRASH_DEFAULT = true;
   public static final double MAX_TRASH_DB_RATION_DEFAULT = 0.25;
   public static final long BYTES_MAX_DELETE_CHUNK_DEFAULT = 67108864L;

   public SstFileManager(Env var1) throws RocksDBException {
      this(var1, null);
   }

   public SstFileManager(Env var1, Logger var2) throws RocksDBException {
      this(var1, var2, 0L);
   }

   public SstFileManager(Env var1, Logger var2, long var3) throws RocksDBException {
      this(var1, var2, var3, 0.25);
   }

   public SstFileManager(Env var1, Logger var2, long var3, double var5) throws RocksDBException {
      this(var1, var2, var3, var5, 67108864L);
   }

   public SstFileManager(Env var1, Logger var2, long var3, double var5, long var7) throws RocksDBException {
      super(newSstFileManager(var1.nativeHandle_, var2 != null ? var2.nativeHandle_ : 0L, var3, var5, var7));
   }

   public void setMaxAllowedSpaceUsage(long var1) {
      setMaxAllowedSpaceUsage(this.nativeHandle_, var1);
   }

   public void setCompactionBufferSize(long var1) {
      setCompactionBufferSize(this.nativeHandle_, var1);
   }

   public boolean isMaxAllowedSpaceReached() {
      return isMaxAllowedSpaceReached(this.nativeHandle_);
   }

   public boolean isMaxAllowedSpaceReachedIncludingCompactions() {
      return isMaxAllowedSpaceReachedIncludingCompactions(this.nativeHandle_);
   }

   public long getTotalSize() {
      return getTotalSize(this.nativeHandle_);
   }

   public Map<String, Long> getTrackedFiles() {
      return getTrackedFiles(this.nativeHandle_);
   }

   public long getDeleteRateBytesPerSecond() {
      return getDeleteRateBytesPerSecond(this.nativeHandle_);
   }

   public void setDeleteRateBytesPerSecond(long var1) {
      setDeleteRateBytesPerSecond(this.nativeHandle_, var1);
   }

   public double getMaxTrashDBRatio() {
      return getMaxTrashDBRatio(this.nativeHandle_);
   }

   public void setMaxTrashDBRatio(double var1) {
      setMaxTrashDBRatio(this.nativeHandle_, var1);
   }

   private static native long newSstFileManager(long var0, long var2, long var4, double var6, long var8) throws RocksDBException;

   private static native void setMaxAllowedSpaceUsage(long var0, long var2);

   private static native void setCompactionBufferSize(long var0, long var2);

   private static native boolean isMaxAllowedSpaceReached(long var0);

   private static native boolean isMaxAllowedSpaceReachedIncludingCompactions(long var0);

   private static native long getTotalSize(long var0);

   private static native Map<String, Long> getTrackedFiles(long var0);

   private static native long getDeleteRateBytesPerSecond(long var0);

   private static native void setDeleteRateBytesPerSecond(long var0, long var2);

   private static native double getMaxTrashDBRatio(long var0);

   private static native void setMaxTrashDBRatio(long var0, double var2);

   @Override
   protected void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);
}
