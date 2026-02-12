package org.rocksdb;

public class CompactionJobStats extends RocksObject {
   public CompactionJobStats() {
      super(newCompactionJobStats());
   }

   CompactionJobStats(long var1) {
      super(var1);
   }

   public void reset() {
      reset(this.nativeHandle_);
   }

   public void add(CompactionJobStats var1) {
      add(this.nativeHandle_, var1.nativeHandle_);
   }

   public long elapsedMicros() {
      return elapsedMicros(this.nativeHandle_);
   }

   public long numInputRecords() {
      return numInputRecords(this.nativeHandle_);
   }

   public long numInputFiles() {
      return numInputFiles(this.nativeHandle_);
   }

   public long numInputFilesAtOutputLevel() {
      return numInputFilesAtOutputLevel(this.nativeHandle_);
   }

   public long numOutputRecords() {
      return numOutputRecords(this.nativeHandle_);
   }

   public long numOutputFiles() {
      return numOutputFiles(this.nativeHandle_);
   }

   public boolean isManualCompaction() {
      return isManualCompaction(this.nativeHandle_);
   }

   public long totalInputBytes() {
      return totalInputBytes(this.nativeHandle_);
   }

   public long totalOutputBytes() {
      return totalOutputBytes(this.nativeHandle_);
   }

   public long numRecordsReplaced() {
      return numRecordsReplaced(this.nativeHandle_);
   }

   public long totalInputRawKeyBytes() {
      return totalInputRawKeyBytes(this.nativeHandle_);
   }

   public long totalInputRawValueBytes() {
      return totalInputRawValueBytes(this.nativeHandle_);
   }

   public long numInputDeletionRecords() {
      return numInputDeletionRecords(this.nativeHandle_);
   }

   public long numExpiredDeletionRecords() {
      return numExpiredDeletionRecords(this.nativeHandle_);
   }

   public long numCorruptKeys() {
      return numCorruptKeys(this.nativeHandle_);
   }

   public long fileWriteNanos() {
      return fileWriteNanos(this.nativeHandle_);
   }

   public long fileRangeSyncNanos() {
      return fileRangeSyncNanos(this.nativeHandle_);
   }

   public long fileFsyncNanos() {
      return fileFsyncNanos(this.nativeHandle_);
   }

   public long filePrepareWriteNanos() {
      return filePrepareWriteNanos(this.nativeHandle_);
   }

   public byte[] smallestOutputKeyPrefix() {
      return smallestOutputKeyPrefix(this.nativeHandle_);
   }

   public byte[] largestOutputKeyPrefix() {
      return largestOutputKeyPrefix(this.nativeHandle_);
   }

   public long numSingleDelFallthru() {
      return numSingleDelFallthru(this.nativeHandle_);
   }

   public long numSingleDelMismatch() {
      return numSingleDelMismatch(this.nativeHandle_);
   }

   private static native long newCompactionJobStats();

   @Override
   protected void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void reset(long var0);

   private static native void add(long var0, long var2);

   private static native long elapsedMicros(long var0);

   private static native long numInputRecords(long var0);

   private static native long numInputFiles(long var0);

   private static native long numInputFilesAtOutputLevel(long var0);

   private static native long numOutputRecords(long var0);

   private static native long numOutputFiles(long var0);

   private static native boolean isManualCompaction(long var0);

   private static native long totalInputBytes(long var0);

   private static native long totalOutputBytes(long var0);

   private static native long numRecordsReplaced(long var0);

   private static native long totalInputRawKeyBytes(long var0);

   private static native long totalInputRawValueBytes(long var0);

   private static native long numInputDeletionRecords(long var0);

   private static native long numExpiredDeletionRecords(long var0);

   private static native long numCorruptKeys(long var0);

   private static native long fileWriteNanos(long var0);

   private static native long fileRangeSyncNanos(long var0);

   private static native long fileFsyncNanos(long var0);

   private static native long filePrepareWriteNanos(long var0);

   private static native byte[] smallestOutputKeyPrefix(long var0);

   private static native byte[] largestOutputKeyPrefix(long var0);

   private static native long numSingleDelFallthru(long var0);

   private static native long numSingleDelMismatch(long var0);
}
