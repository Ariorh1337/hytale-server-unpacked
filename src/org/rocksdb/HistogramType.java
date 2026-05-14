package org.rocksdb;

public enum HistogramType {
   DB_GET((byte)0),
   DB_WRITE((byte)1),
   COMPACTION_TIME((byte)2),
   COMPACTION_CPU_TIME((byte)3),
   SUBCOMPACTION_SETUP_TIME((byte)4),
   TABLE_SYNC_MICROS((byte)5),
   COMPACTION_OUTFILE_SYNC_MICROS((byte)6),
   WAL_FILE_SYNC_MICROS((byte)7),
   MANIFEST_FILE_SYNC_MICROS((byte)8),
   TABLE_OPEN_IO_MICROS((byte)9),
   DB_MULTIGET((byte)10),
   READ_BLOCK_COMPACTION_MICROS((byte)11),
   READ_BLOCK_GET_MICROS((byte)12),
   WRITE_RAW_BLOCK_MICROS((byte)13),
   NUM_FILES_IN_SINGLE_COMPACTION((byte)14),
   DB_SEEK((byte)15),
   WRITE_STALL((byte)16),
   SST_READ_MICROS((byte)17),
   FILE_READ_FLUSH_MICROS((byte)18),
   FILE_READ_COMPACTION_MICROS((byte)19),
   FILE_READ_DB_OPEN_MICROS((byte)20),
   FILE_READ_GET_MICROS((byte)21),
   FILE_READ_MULTIGET_MICROS((byte)22),
   FILE_READ_DB_ITERATOR_MICROS((byte)23),
   FILE_READ_VERIFY_DB_CHECKSUM_MICROS((byte)24),
   FILE_READ_VERIFY_FILE_CHECKSUMS_MICROS((byte)25),
   SST_WRITE_MICROS((byte)26),
   FILE_WRITE_FLUSH_MICROS((byte)27),
   FILE_WRITE_COMPACTION_MICROS((byte)28),
   FILE_WRITE_DB_OPEN_MICROS((byte)29),
   NUM_SUBCOMPACTIONS_SCHEDULED((byte)30),
   BYTES_PER_READ((byte)31),
   BYTES_PER_WRITE((byte)32),
   BYTES_PER_MULTIGET((byte)33),
   COMPRESSION_TIMES_NANOS((byte)34),
   DECOMPRESSION_TIMES_NANOS((byte)35),
   READ_NUM_MERGE_OPERANDS((byte)36),
   BLOB_DB_KEY_SIZE((byte)37),
   BLOB_DB_VALUE_SIZE((byte)38),
   BLOB_DB_WRITE_MICROS((byte)39),
   BLOB_DB_GET_MICROS((byte)40),
   BLOB_DB_MULTIGET_MICROS((byte)41),
   BLOB_DB_SEEK_MICROS((byte)42),
   BLOB_DB_NEXT_MICROS((byte)43),
   BLOB_DB_PREV_MICROS((byte)44),
   BLOB_DB_BLOB_FILE_WRITE_MICROS((byte)45),
   BLOB_DB_BLOB_FILE_READ_MICROS((byte)46),
   BLOB_DB_BLOB_FILE_SYNC_MICROS((byte)47),
   BLOB_DB_COMPRESSION_MICROS((byte)48),
   BLOB_DB_DECOMPRESSION_MICROS((byte)49),
   FLUSH_TIME((byte)50),
   SST_BATCH_SIZE((byte)51),
   MULTIGET_IO_BATCH_SIZE((byte)52),
   NUM_INDEX_AND_FILTER_BLOCKS_READ_PER_LEVEL((byte)53),
   NUM_SST_READ_PER_LEVEL((byte)54),
   NUM_LEVEL_READ_PER_MULTIGET((byte)55),
   ERROR_HANDLER_AUTORESUME_RETRY_COUNT((byte)56),
   ASYNC_READ_BYTES((byte)57),
   POLL_WAIT_MICROS((byte)58),
   PREFETCHED_BYTES_DISCARDED((byte)59),
   ASYNC_PREFETCH_ABORT_MICROS((byte)60),
   TABLE_OPEN_PREFETCH_TAIL_READ_BYTES((byte)61),
   COMPACTION_PREFETCH_BYTES((byte)63),
   MULTISCAN_PREPARE_MICROS((byte)64),
   MULTISCAN_BLOCKS_PER_PREPARE((byte)65),
   BLOCK_KEY_DISTRIBUTION_CV((byte)66),
   HISTOGRAM_ENUM_MAX((byte)62);

   private final byte value;

   HistogramType(final byte nullxx) {
      this.value = nullxx;
   }

   public byte getValue() {
      return this.value;
   }

   public static HistogramType getHistogramType(byte var0) {
      for (HistogramType var4 : values()) {
         if (var4.getValue() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("Illegal value provided for HistogramType.");
   }
}
