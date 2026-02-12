package org.rocksdb;

public enum CompactionReason {
   kUnknown((byte)0),
   kLevelL0FilesNum((byte)1),
   kLevelMaxLevelSize((byte)2),
   kUniversalSizeAmplification((byte)3),
   kUniversalSizeRatio((byte)4),
   kUniversalSortedRunNum((byte)5),
   kFIFOMaxSize((byte)6),
   kFIFOReduceNumFiles((byte)7),
   kFIFOTtl((byte)8),
   kManualCompaction((byte)9),
   kFilesMarkedForCompaction((byte)16),
   kBottommostFiles((byte)10),
   kTtl((byte)11),
   kFlush((byte)12),
   kExternalSstIngestion((byte)13),
   kPeriodicCompaction((byte)14),
   kChangeTemperature((byte)15),
   kForcedBlobGC((byte)17),
   kRoundRobinTtl((byte)18),
   kRefitLevel((byte)19);

   private final byte value;

   CompactionReason(byte var3) {
      this.value = var3;
   }

   byte getValue() {
      return this.value;
   }

   static CompactionReason fromValue(byte var0) {
      for (CompactionReason var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for CompactionReason: " + var0);
   }
}
