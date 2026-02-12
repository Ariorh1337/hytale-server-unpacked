package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CompactionJobInfo extends RocksObject {
   public CompactionJobInfo() {
      super(newCompactionJobInfo());
   }

   private CompactionJobInfo(long var1) {
      super(var1);
      this.disOwnNativeHandle();
   }

   public byte[] columnFamilyName() {
      return columnFamilyName(this.nativeHandle_);
   }

   public Status status() {
      return status(this.nativeHandle_);
   }

   public long threadId() {
      return threadId(this.nativeHandle_);
   }

   public int jobId() {
      return jobId(this.nativeHandle_);
   }

   public int baseInputLevel() {
      return baseInputLevel(this.nativeHandle_);
   }

   public int outputLevel() {
      return outputLevel(this.nativeHandle_);
   }

   public List<String> inputFiles() {
      return Arrays.asList(inputFiles(this.nativeHandle_));
   }

   public List<String> outputFiles() {
      return Arrays.asList(outputFiles(this.nativeHandle_));
   }

   public Map<String, TableProperties> tableProperties() {
      return tableProperties(this.nativeHandle_);
   }

   public CompactionReason compactionReason() {
      return CompactionReason.fromValue(compactionReason(this.nativeHandle_));
   }

   public CompressionType compression() {
      return CompressionType.getCompressionType(compression(this.nativeHandle_));
   }

   public CompactionJobStats stats() {
      long var1 = stats(this.nativeHandle_);
      return var1 == 0L ? null : new CompactionJobStats(var1);
   }

   private static native long newCompactionJobInfo();

   @Override
   protected void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native byte[] columnFamilyName(long var0);

   private static native Status status(long var0);

   private static native long threadId(long var0);

   private static native int jobId(long var0);

   private static native int baseInputLevel(long var0);

   private static native int outputLevel(long var0);

   private static native String[] inputFiles(long var0);

   private static native String[] outputFiles(long var0);

   private static native Map<String, TableProperties> tableProperties(long var0);

   private static native byte compactionReason(long var0);

   private static native byte compression(long var0);

   private static native long stats(long var0);
}
