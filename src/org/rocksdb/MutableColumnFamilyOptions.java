package org.rocksdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MutableColumnFamilyOptions extends AbstractMutableOptions {
   private MutableColumnFamilyOptions(String[] var1, String[] var2) {
      super(var1, var2);
   }

   public static MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder builder() {
      return new MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder();
   }

   public static MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder parse(String var0, boolean var1) {
      Objects.requireNonNull(var0);
      List var2 = OptionString.Parser.parse(var0);
      return new MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder().fromParsed(var2, var1);
   }

   public static MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder parse(String var0) {
      return parse(var0, false);
   }

   public enum BlobOption implements MutableColumnFamilyOptions.MutableColumnFamilyOptionKey {
      enable_blob_files(MutableOptionKey.ValueType.BOOLEAN),
      min_blob_size(MutableOptionKey.ValueType.LONG),
      blob_file_size(MutableOptionKey.ValueType.LONG),
      blob_compression_type(MutableOptionKey.ValueType.ENUM),
      enable_blob_garbage_collection(MutableOptionKey.ValueType.BOOLEAN),
      blob_garbage_collection_age_cutoff(MutableOptionKey.ValueType.DOUBLE),
      blob_garbage_collection_force_threshold(MutableOptionKey.ValueType.DOUBLE),
      blob_compaction_readahead_size(MutableOptionKey.ValueType.LONG),
      blob_file_starting_level(MutableOptionKey.ValueType.INT),
      prepopulate_blob_cache(MutableOptionKey.ValueType.ENUM);

      private final MutableOptionKey.ValueType valueType;

      BlobOption(MutableOptionKey.ValueType var3) {
         this.valueType = var3;
      }

      @Override
      public MutableOptionKey.ValueType getValueType() {
         return this.valueType;
      }
   }

   public enum CompactionOption implements MutableColumnFamilyOptions.MutableColumnFamilyOptionKey {
      disable_auto_compactions(MutableOptionKey.ValueType.BOOLEAN),
      soft_pending_compaction_bytes_limit(MutableOptionKey.ValueType.LONG),
      hard_pending_compaction_bytes_limit(MutableOptionKey.ValueType.LONG),
      level0_file_num_compaction_trigger(MutableOptionKey.ValueType.INT),
      level0_slowdown_writes_trigger(MutableOptionKey.ValueType.INT),
      level0_stop_writes_trigger(MutableOptionKey.ValueType.INT),
      max_compaction_bytes(MutableOptionKey.ValueType.LONG),
      target_file_size_base(MutableOptionKey.ValueType.LONG),
      target_file_size_multiplier(MutableOptionKey.ValueType.INT),
      max_bytes_for_level_base(MutableOptionKey.ValueType.LONG),
      max_bytes_for_level_multiplier(MutableOptionKey.ValueType.INT),
      max_bytes_for_level_multiplier_additional(MutableOptionKey.ValueType.INT_ARRAY),
      ttl(MutableOptionKey.ValueType.LONG),
      periodic_compaction_seconds(MutableOptionKey.ValueType.LONG);

      private final MutableOptionKey.ValueType valueType;

      CompactionOption(MutableOptionKey.ValueType var3) {
         this.valueType = var3;
      }

      @Override
      public MutableOptionKey.ValueType getValueType() {
         return this.valueType;
      }
   }

   public enum MemtableOption implements MutableColumnFamilyOptions.MutableColumnFamilyOptionKey {
      write_buffer_size(MutableOptionKey.ValueType.LONG),
      arena_block_size(MutableOptionKey.ValueType.LONG),
      memtable_prefix_bloom_size_ratio(MutableOptionKey.ValueType.DOUBLE),
      memtable_whole_key_filtering(MutableOptionKey.ValueType.BOOLEAN),
      @Deprecated
      memtable_prefix_bloom_bits(MutableOptionKey.ValueType.INT),
      @Deprecated
      memtable_prefix_bloom_probes(MutableOptionKey.ValueType.INT),
      memtable_huge_page_size(MutableOptionKey.ValueType.LONG),
      max_successive_merges(MutableOptionKey.ValueType.LONG),
      @Deprecated
      filter_deletes(MutableOptionKey.ValueType.BOOLEAN),
      max_write_buffer_number(MutableOptionKey.ValueType.INT),
      inplace_update_num_locks(MutableOptionKey.ValueType.LONG),
      experimental_mempurge_threshold(MutableOptionKey.ValueType.DOUBLE);

      private final MutableOptionKey.ValueType valueType;

      MemtableOption(MutableOptionKey.ValueType var3) {
         this.valueType = var3;
      }

      @Override
      public MutableOptionKey.ValueType getValueType() {
         return this.valueType;
      }
   }

   public enum MiscOption implements MutableColumnFamilyOptions.MutableColumnFamilyOptionKey {
      max_sequential_skip_in_iterations(MutableOptionKey.ValueType.LONG),
      paranoid_file_checks(MutableOptionKey.ValueType.BOOLEAN),
      report_bg_io_stats(MutableOptionKey.ValueType.BOOLEAN),
      compression(MutableOptionKey.ValueType.ENUM);

      private final MutableOptionKey.ValueType valueType;

      MiscOption(MutableOptionKey.ValueType var3) {
         this.valueType = var3;
      }

      @Override
      public MutableOptionKey.ValueType getValueType() {
         return this.valueType;
      }
   }

   private interface MutableColumnFamilyOptionKey extends MutableOptionKey {
   }

   public static class MutableColumnFamilyOptionsBuilder
      extends AbstractMutableOptions.AbstractMutableOptionsBuilder<MutableColumnFamilyOptions, MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder, MutableColumnFamilyOptions.MutableColumnFamilyOptionKey>
      implements MutableColumnFamilyOptionsInterface<MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder> {
      private static final Map<String, MutableColumnFamilyOptions.MutableColumnFamilyOptionKey> ALL_KEYS_LOOKUP = new HashMap<>();

      private MutableColumnFamilyOptionsBuilder() {
      }

      protected MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder self() {
         return this;
      }

      @Override
      protected Map<String, MutableColumnFamilyOptions.MutableColumnFamilyOptionKey> allKeys() {
         return ALL_KEYS_LOOKUP;
      }

      protected MutableColumnFamilyOptions build(String[] var1, String[] var2) {
         return new MutableColumnFamilyOptions(var1, var2);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setWriteBufferSize(long var1) {
         return this.setLong(MutableColumnFamilyOptions.MemtableOption.write_buffer_size, var1);
      }

      @Override
      public long writeBufferSize() {
         return this.getLong(MutableColumnFamilyOptions.MemtableOption.write_buffer_size);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setArenaBlockSize(long var1) {
         return this.setLong(MutableColumnFamilyOptions.MemtableOption.arena_block_size, var1);
      }

      @Override
      public long arenaBlockSize() {
         return this.getLong(MutableColumnFamilyOptions.MemtableOption.arena_block_size);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMemtablePrefixBloomSizeRatio(double var1) {
         return this.setDouble(MutableColumnFamilyOptions.MemtableOption.memtable_prefix_bloom_size_ratio, var1);
      }

      @Override
      public double memtablePrefixBloomSizeRatio() {
         return this.getDouble(MutableColumnFamilyOptions.MemtableOption.memtable_prefix_bloom_size_ratio);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMemtableWholeKeyFiltering(boolean var1) {
         return this.setBoolean(MutableColumnFamilyOptions.MemtableOption.memtable_whole_key_filtering, var1);
      }

      @Override
      public boolean memtableWholeKeyFiltering() {
         return this.getBoolean(MutableColumnFamilyOptions.MemtableOption.memtable_whole_key_filtering);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMemtableHugePageSize(long var1) {
         return this.setLong(MutableColumnFamilyOptions.MemtableOption.memtable_huge_page_size, var1);
      }

      @Override
      public long memtableHugePageSize() {
         return this.getLong(MutableColumnFamilyOptions.MemtableOption.memtable_huge_page_size);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMaxSuccessiveMerges(long var1) {
         return this.setLong(MutableColumnFamilyOptions.MemtableOption.max_successive_merges, var1);
      }

      @Override
      public long maxSuccessiveMerges() {
         return this.getLong(MutableColumnFamilyOptions.MemtableOption.max_successive_merges);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMaxWriteBufferNumber(int var1) {
         return this.setInt(MutableColumnFamilyOptions.MemtableOption.max_write_buffer_number, var1);
      }

      @Override
      public int maxWriteBufferNumber() {
         return this.getInt(MutableColumnFamilyOptions.MemtableOption.max_write_buffer_number);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setInplaceUpdateNumLocks(long var1) {
         return this.setLong(MutableColumnFamilyOptions.MemtableOption.inplace_update_num_locks, var1);
      }

      @Override
      public long inplaceUpdateNumLocks() {
         return this.getLong(MutableColumnFamilyOptions.MemtableOption.inplace_update_num_locks);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setExperimentalMempurgeThreshold(double var1) {
         return this.setDouble(MutableColumnFamilyOptions.MemtableOption.experimental_mempurge_threshold, var1);
      }

      @Override
      public double experimentalMempurgeThreshold() {
         return this.getDouble(MutableColumnFamilyOptions.MemtableOption.experimental_mempurge_threshold);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setDisableAutoCompactions(boolean var1) {
         return this.setBoolean(MutableColumnFamilyOptions.CompactionOption.disable_auto_compactions, var1);
      }

      @Override
      public boolean disableAutoCompactions() {
         return this.getBoolean(MutableColumnFamilyOptions.CompactionOption.disable_auto_compactions);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setSoftPendingCompactionBytesLimit(long var1) {
         return this.setLong(MutableColumnFamilyOptions.CompactionOption.soft_pending_compaction_bytes_limit, var1);
      }

      @Override
      public long softPendingCompactionBytesLimit() {
         return this.getLong(MutableColumnFamilyOptions.CompactionOption.soft_pending_compaction_bytes_limit);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setHardPendingCompactionBytesLimit(long var1) {
         return this.setLong(MutableColumnFamilyOptions.CompactionOption.hard_pending_compaction_bytes_limit, var1);
      }

      @Override
      public long hardPendingCompactionBytesLimit() {
         return this.getLong(MutableColumnFamilyOptions.CompactionOption.hard_pending_compaction_bytes_limit);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setLevel0FileNumCompactionTrigger(int var1) {
         return this.setInt(MutableColumnFamilyOptions.CompactionOption.level0_file_num_compaction_trigger, var1);
      }

      @Override
      public int level0FileNumCompactionTrigger() {
         return this.getInt(MutableColumnFamilyOptions.CompactionOption.level0_file_num_compaction_trigger);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setLevel0SlowdownWritesTrigger(int var1) {
         return this.setInt(MutableColumnFamilyOptions.CompactionOption.level0_slowdown_writes_trigger, var1);
      }

      @Override
      public int level0SlowdownWritesTrigger() {
         return this.getInt(MutableColumnFamilyOptions.CompactionOption.level0_slowdown_writes_trigger);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setLevel0StopWritesTrigger(int var1) {
         return this.setInt(MutableColumnFamilyOptions.CompactionOption.level0_stop_writes_trigger, var1);
      }

      @Override
      public int level0StopWritesTrigger() {
         return this.getInt(MutableColumnFamilyOptions.CompactionOption.level0_stop_writes_trigger);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMaxCompactionBytes(long var1) {
         return this.setLong(MutableColumnFamilyOptions.CompactionOption.max_compaction_bytes, var1);
      }

      @Override
      public long maxCompactionBytes() {
         return this.getLong(MutableColumnFamilyOptions.CompactionOption.max_compaction_bytes);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setTargetFileSizeBase(long var1) {
         return this.setLong(MutableColumnFamilyOptions.CompactionOption.target_file_size_base, var1);
      }

      @Override
      public long targetFileSizeBase() {
         return this.getLong(MutableColumnFamilyOptions.CompactionOption.target_file_size_base);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setTargetFileSizeMultiplier(int var1) {
         return this.setInt(MutableColumnFamilyOptions.CompactionOption.target_file_size_multiplier, var1);
      }

      @Override
      public int targetFileSizeMultiplier() {
         return this.getInt(MutableColumnFamilyOptions.CompactionOption.target_file_size_multiplier);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMaxBytesForLevelBase(long var1) {
         return this.setLong(MutableColumnFamilyOptions.CompactionOption.max_bytes_for_level_base, var1);
      }

      @Override
      public long maxBytesForLevelBase() {
         return this.getLong(MutableColumnFamilyOptions.CompactionOption.max_bytes_for_level_base);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMaxBytesForLevelMultiplier(double var1) {
         return this.setDouble(MutableColumnFamilyOptions.CompactionOption.max_bytes_for_level_multiplier, var1);
      }

      @Override
      public double maxBytesForLevelMultiplier() {
         return this.getDouble(MutableColumnFamilyOptions.CompactionOption.max_bytes_for_level_multiplier);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMaxBytesForLevelMultiplierAdditional(int[] var1) {
         return this.setIntArray(MutableColumnFamilyOptions.CompactionOption.max_bytes_for_level_multiplier_additional, var1);
      }

      @Override
      public int[] maxBytesForLevelMultiplierAdditional() {
         return this.getIntArray(MutableColumnFamilyOptions.CompactionOption.max_bytes_for_level_multiplier_additional);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMaxSequentialSkipInIterations(long var1) {
         return this.setLong(MutableColumnFamilyOptions.MiscOption.max_sequential_skip_in_iterations, var1);
      }

      @Override
      public long maxSequentialSkipInIterations() {
         return this.getLong(MutableColumnFamilyOptions.MiscOption.max_sequential_skip_in_iterations);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setParanoidFileChecks(boolean var1) {
         return this.setBoolean(MutableColumnFamilyOptions.MiscOption.paranoid_file_checks, var1);
      }

      @Override
      public boolean paranoidFileChecks() {
         return this.getBoolean(MutableColumnFamilyOptions.MiscOption.paranoid_file_checks);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setCompressionType(CompressionType var1) {
         return this.setEnum(MutableColumnFamilyOptions.MiscOption.compression, var1);
      }

      @Override
      public CompressionType compressionType() {
         return this.getEnum(MutableColumnFamilyOptions.MiscOption.compression);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setReportBgIoStats(boolean var1) {
         return this.setBoolean(MutableColumnFamilyOptions.MiscOption.report_bg_io_stats, var1);
      }

      @Override
      public boolean reportBgIoStats() {
         return this.getBoolean(MutableColumnFamilyOptions.MiscOption.report_bg_io_stats);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setTtl(long var1) {
         return this.setLong(MutableColumnFamilyOptions.CompactionOption.ttl, var1);
      }

      @Override
      public long ttl() {
         return this.getLong(MutableColumnFamilyOptions.CompactionOption.ttl);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setPeriodicCompactionSeconds(long var1) {
         return this.setLong(MutableColumnFamilyOptions.CompactionOption.periodic_compaction_seconds, var1);
      }

      @Override
      public long periodicCompactionSeconds() {
         return this.getLong(MutableColumnFamilyOptions.CompactionOption.periodic_compaction_seconds);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setEnableBlobFiles(boolean var1) {
         return this.setBoolean(MutableColumnFamilyOptions.BlobOption.enable_blob_files, var1);
      }

      @Override
      public boolean enableBlobFiles() {
         return this.getBoolean(MutableColumnFamilyOptions.BlobOption.enable_blob_files);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setMinBlobSize(long var1) {
         return this.setLong(MutableColumnFamilyOptions.BlobOption.min_blob_size, var1);
      }

      @Override
      public long minBlobSize() {
         return this.getLong(MutableColumnFamilyOptions.BlobOption.min_blob_size);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setBlobFileSize(long var1) {
         return this.setLong(MutableColumnFamilyOptions.BlobOption.blob_file_size, var1);
      }

      @Override
      public long blobFileSize() {
         return this.getLong(MutableColumnFamilyOptions.BlobOption.blob_file_size);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setBlobCompressionType(CompressionType var1) {
         return this.setEnum(MutableColumnFamilyOptions.BlobOption.blob_compression_type, var1);
      }

      @Override
      public CompressionType blobCompressionType() {
         return this.getEnum(MutableColumnFamilyOptions.BlobOption.blob_compression_type);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setEnableBlobGarbageCollection(boolean var1) {
         return this.setBoolean(MutableColumnFamilyOptions.BlobOption.enable_blob_garbage_collection, var1);
      }

      @Override
      public boolean enableBlobGarbageCollection() {
         return this.getBoolean(MutableColumnFamilyOptions.BlobOption.enable_blob_garbage_collection);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setBlobGarbageCollectionAgeCutoff(double var1) {
         return this.setDouble(MutableColumnFamilyOptions.BlobOption.blob_garbage_collection_age_cutoff, var1);
      }

      @Override
      public double blobGarbageCollectionAgeCutoff() {
         return this.getDouble(MutableColumnFamilyOptions.BlobOption.blob_garbage_collection_age_cutoff);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setBlobGarbageCollectionForceThreshold(double var1) {
         return this.setDouble(MutableColumnFamilyOptions.BlobOption.blob_garbage_collection_force_threshold, var1);
      }

      @Override
      public double blobGarbageCollectionForceThreshold() {
         return this.getDouble(MutableColumnFamilyOptions.BlobOption.blob_garbage_collection_force_threshold);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setBlobCompactionReadaheadSize(long var1) {
         return this.setLong(MutableColumnFamilyOptions.BlobOption.blob_compaction_readahead_size, var1);
      }

      @Override
      public long blobCompactionReadaheadSize() {
         return this.getLong(MutableColumnFamilyOptions.BlobOption.blob_compaction_readahead_size);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setBlobFileStartingLevel(int var1) {
         return this.setInt(MutableColumnFamilyOptions.BlobOption.blob_file_starting_level, var1);
      }

      @Override
      public int blobFileStartingLevel() {
         return this.getInt(MutableColumnFamilyOptions.BlobOption.blob_file_starting_level);
      }

      public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder setPrepopulateBlobCache(PrepopulateBlobCache var1) {
         return this.setEnum(MutableColumnFamilyOptions.BlobOption.prepopulate_blob_cache, var1);
      }

      @Override
      public PrepopulateBlobCache prepopulateBlobCache() {
         return this.getEnum(MutableColumnFamilyOptions.BlobOption.prepopulate_blob_cache);
      }

      static {
         for (MutableColumnFamilyOptions.MemtableOption var3 : MutableColumnFamilyOptions.MemtableOption.values()) {
            ALL_KEYS_LOOKUP.put(var3.name(), var3);
         }

         for (MutableColumnFamilyOptions.CompactionOption var13 : MutableColumnFamilyOptions.CompactionOption.values()) {
            ALL_KEYS_LOOKUP.put(var13.name(), var13);
         }

         for (MutableColumnFamilyOptions.MiscOption var14 : MutableColumnFamilyOptions.MiscOption.values()) {
            ALL_KEYS_LOOKUP.put(var14.name(), var14);
         }

         for (MutableColumnFamilyOptions.BlobOption var15 : MutableColumnFamilyOptions.BlobOption.values()) {
            ALL_KEYS_LOOKUP.put(var15.name(), var15);
         }
      }
   }
}
