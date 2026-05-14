package org.rocksdb;

public enum OperationStage {
   STAGE_UNKNOWN((byte)0),
   STAGE_FLUSH_RUN((byte)1),
   STAGE_FLUSH_WRITE_L0((byte)2),
   STAGE_COMPACTION_PREPARE((byte)3),
   STAGE_COMPACTION_RUN((byte)4),
   STAGE_COMPACTION_PROCESS_KV((byte)5),
   STAGE_COMPACTION_INSTALL((byte)6),
   STAGE_COMPACTION_SYNC_FILE((byte)7),
   STAGE_PICK_MEMTABLES_TO_FLUSH((byte)8),
   STAGE_MEMTABLE_ROLLBACK((byte)9),
   STAGE_MEMTABLE_INSTALL_FLUSH_RESULTS((byte)10);

   private final byte value;

   OperationStage(final byte nullxx) {
      this.value = nullxx;
   }

   byte getValue() {
      return this.value;
   }

   static OperationStage fromValue(byte var0) throws IllegalArgumentException {
      for (OperationStage var4 : values()) {
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Unknown value for OperationStage: " + var0);
   }
}
