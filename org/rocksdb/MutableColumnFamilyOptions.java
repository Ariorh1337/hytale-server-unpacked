/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.rocksdb.AbstractMutableOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.MutableColumnFamilyOptionsInterface;
import org.rocksdb.MutableOptionKey;
import org.rocksdb.OptionString;
import org.rocksdb.PrepopulateBlobCache;

public class MutableColumnFamilyOptions
extends AbstractMutableOptions {
    private MutableColumnFamilyOptions(String[] stringArray, String[] stringArray2) {
        super(stringArray, stringArray2);
    }

    public static MutableColumnFamilyOptionsBuilder builder() {
        return new MutableColumnFamilyOptionsBuilder();
    }

    public static MutableColumnFamilyOptionsBuilder parse(String string, boolean bl) {
        Objects.requireNonNull(string);
        List<OptionString.Entry> list = OptionString.Parser.parse(string);
        return (MutableColumnFamilyOptionsBuilder)new MutableColumnFamilyOptionsBuilder().fromParsed(list, bl);
    }

    public static MutableColumnFamilyOptionsBuilder parse(String string) {
        return MutableColumnFamilyOptions.parse(string, false);
    }

    public static class MutableColumnFamilyOptionsBuilder
    extends AbstractMutableOptions.AbstractMutableOptionsBuilder<MutableColumnFamilyOptions, MutableColumnFamilyOptionsBuilder, MutableColumnFamilyOptionKey>
    implements MutableColumnFamilyOptionsInterface<MutableColumnFamilyOptionsBuilder> {
        private static final Map<String, MutableColumnFamilyOptionKey> ALL_KEYS_LOOKUP = new HashMap<String, MutableColumnFamilyOptionKey>();

        private MutableColumnFamilyOptionsBuilder() {
        }

        @Override
        protected MutableColumnFamilyOptionsBuilder self() {
            return this;
        }

        @Override
        protected Map<String, MutableColumnFamilyOptionKey> allKeys() {
            return ALL_KEYS_LOOKUP;
        }

        @Override
        protected MutableColumnFamilyOptions build(String[] stringArray, String[] stringArray2) {
            return new MutableColumnFamilyOptions(stringArray, stringArray2);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setWriteBufferSize(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(MemtableOption.write_buffer_size, l);
        }

        @Override
        public long writeBufferSize() {
            return this.getLong(MemtableOption.write_buffer_size);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setArenaBlockSize(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(MemtableOption.arena_block_size, l);
        }

        @Override
        public long arenaBlockSize() {
            return this.getLong(MemtableOption.arena_block_size);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMemtablePrefixBloomSizeRatio(double d) {
            return (MutableColumnFamilyOptionsBuilder)this.setDouble(MemtableOption.memtable_prefix_bloom_size_ratio, d);
        }

        @Override
        public double memtablePrefixBloomSizeRatio() {
            return this.getDouble(MemtableOption.memtable_prefix_bloom_size_ratio);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMemtableWholeKeyFiltering(boolean bl) {
            return (MutableColumnFamilyOptionsBuilder)this.setBoolean(MemtableOption.memtable_whole_key_filtering, bl);
        }

        @Override
        public boolean memtableWholeKeyFiltering() {
            return this.getBoolean(MemtableOption.memtable_whole_key_filtering);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMemtableHugePageSize(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(MemtableOption.memtable_huge_page_size, l);
        }

        @Override
        public long memtableHugePageSize() {
            return this.getLong(MemtableOption.memtable_huge_page_size);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMaxSuccessiveMerges(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(MemtableOption.max_successive_merges, l);
        }

        @Override
        public long maxSuccessiveMerges() {
            return this.getLong(MemtableOption.max_successive_merges);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMaxWriteBufferNumber(int n) {
            return (MutableColumnFamilyOptionsBuilder)this.setInt(MemtableOption.max_write_buffer_number, n);
        }

        @Override
        public int maxWriteBufferNumber() {
            return this.getInt(MemtableOption.max_write_buffer_number);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setInplaceUpdateNumLocks(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(MemtableOption.inplace_update_num_locks, l);
        }

        @Override
        public long inplaceUpdateNumLocks() {
            return this.getLong(MemtableOption.inplace_update_num_locks);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setExperimentalMempurgeThreshold(double d) {
            return (MutableColumnFamilyOptionsBuilder)this.setDouble(MemtableOption.experimental_mempurge_threshold, d);
        }

        @Override
        public double experimentalMempurgeThreshold() {
            return this.getDouble(MemtableOption.experimental_mempurge_threshold);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setDisableAutoCompactions(boolean bl) {
            return (MutableColumnFamilyOptionsBuilder)this.setBoolean(CompactionOption.disable_auto_compactions, bl);
        }

        @Override
        public boolean disableAutoCompactions() {
            return this.getBoolean(CompactionOption.disable_auto_compactions);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setSoftPendingCompactionBytesLimit(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(CompactionOption.soft_pending_compaction_bytes_limit, l);
        }

        @Override
        public long softPendingCompactionBytesLimit() {
            return this.getLong(CompactionOption.soft_pending_compaction_bytes_limit);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setHardPendingCompactionBytesLimit(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(CompactionOption.hard_pending_compaction_bytes_limit, l);
        }

        @Override
        public long hardPendingCompactionBytesLimit() {
            return this.getLong(CompactionOption.hard_pending_compaction_bytes_limit);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setLevel0FileNumCompactionTrigger(int n) {
            return (MutableColumnFamilyOptionsBuilder)this.setInt(CompactionOption.level0_file_num_compaction_trigger, n);
        }

        @Override
        public int level0FileNumCompactionTrigger() {
            return this.getInt(CompactionOption.level0_file_num_compaction_trigger);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setLevel0SlowdownWritesTrigger(int n) {
            return (MutableColumnFamilyOptionsBuilder)this.setInt(CompactionOption.level0_slowdown_writes_trigger, n);
        }

        @Override
        public int level0SlowdownWritesTrigger() {
            return this.getInt(CompactionOption.level0_slowdown_writes_trigger);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setLevel0StopWritesTrigger(int n) {
            return (MutableColumnFamilyOptionsBuilder)this.setInt(CompactionOption.level0_stop_writes_trigger, n);
        }

        @Override
        public int level0StopWritesTrigger() {
            return this.getInt(CompactionOption.level0_stop_writes_trigger);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMaxCompactionBytes(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(CompactionOption.max_compaction_bytes, l);
        }

        @Override
        public long maxCompactionBytes() {
            return this.getLong(CompactionOption.max_compaction_bytes);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setTargetFileSizeBase(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(CompactionOption.target_file_size_base, l);
        }

        @Override
        public long targetFileSizeBase() {
            return this.getLong(CompactionOption.target_file_size_base);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setTargetFileSizeMultiplier(int n) {
            return (MutableColumnFamilyOptionsBuilder)this.setInt(CompactionOption.target_file_size_multiplier, n);
        }

        @Override
        public int targetFileSizeMultiplier() {
            return this.getInt(CompactionOption.target_file_size_multiplier);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMaxBytesForLevelBase(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(CompactionOption.max_bytes_for_level_base, l);
        }

        @Override
        public long maxBytesForLevelBase() {
            return this.getLong(CompactionOption.max_bytes_for_level_base);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMaxBytesForLevelMultiplier(double d) {
            return (MutableColumnFamilyOptionsBuilder)this.setDouble(CompactionOption.max_bytes_for_level_multiplier, d);
        }

        @Override
        public double maxBytesForLevelMultiplier() {
            return this.getDouble(CompactionOption.max_bytes_for_level_multiplier);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMaxBytesForLevelMultiplierAdditional(int[] nArray) {
            return (MutableColumnFamilyOptionsBuilder)this.setIntArray(CompactionOption.max_bytes_for_level_multiplier_additional, nArray);
        }

        @Override
        public int[] maxBytesForLevelMultiplierAdditional() {
            return this.getIntArray(CompactionOption.max_bytes_for_level_multiplier_additional);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMaxSequentialSkipInIterations(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(MiscOption.max_sequential_skip_in_iterations, l);
        }

        @Override
        public long maxSequentialSkipInIterations() {
            return this.getLong(MiscOption.max_sequential_skip_in_iterations);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setParanoidFileChecks(boolean bl) {
            return (MutableColumnFamilyOptionsBuilder)this.setBoolean(MiscOption.paranoid_file_checks, bl);
        }

        @Override
        public boolean paranoidFileChecks() {
            return this.getBoolean(MiscOption.paranoid_file_checks);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setCompressionType(CompressionType compressionType) {
            return (MutableColumnFamilyOptionsBuilder)this.setEnum(MiscOption.compression, compressionType);
        }

        @Override
        public CompressionType compressionType() {
            return (CompressionType)((Object)this.getEnum(MiscOption.compression));
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setReportBgIoStats(boolean bl) {
            return (MutableColumnFamilyOptionsBuilder)this.setBoolean(MiscOption.report_bg_io_stats, bl);
        }

        @Override
        public boolean reportBgIoStats() {
            return this.getBoolean(MiscOption.report_bg_io_stats);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setTtl(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(CompactionOption.ttl, l);
        }

        @Override
        public long ttl() {
            return this.getLong(CompactionOption.ttl);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setPeriodicCompactionSeconds(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(CompactionOption.periodic_compaction_seconds, l);
        }

        @Override
        public long periodicCompactionSeconds() {
            return this.getLong(CompactionOption.periodic_compaction_seconds);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setEnableBlobFiles(boolean bl) {
            return (MutableColumnFamilyOptionsBuilder)this.setBoolean(BlobOption.enable_blob_files, bl);
        }

        @Override
        public boolean enableBlobFiles() {
            return this.getBoolean(BlobOption.enable_blob_files);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setMinBlobSize(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(BlobOption.min_blob_size, l);
        }

        @Override
        public long minBlobSize() {
            return this.getLong(BlobOption.min_blob_size);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setBlobFileSize(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(BlobOption.blob_file_size, l);
        }

        @Override
        public long blobFileSize() {
            return this.getLong(BlobOption.blob_file_size);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setBlobCompressionType(CompressionType compressionType) {
            return (MutableColumnFamilyOptionsBuilder)this.setEnum(BlobOption.blob_compression_type, compressionType);
        }

        @Override
        public CompressionType blobCompressionType() {
            return (CompressionType)((Object)this.getEnum(BlobOption.blob_compression_type));
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setEnableBlobGarbageCollection(boolean bl) {
            return (MutableColumnFamilyOptionsBuilder)this.setBoolean(BlobOption.enable_blob_garbage_collection, bl);
        }

        @Override
        public boolean enableBlobGarbageCollection() {
            return this.getBoolean(BlobOption.enable_blob_garbage_collection);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setBlobGarbageCollectionAgeCutoff(double d) {
            return (MutableColumnFamilyOptionsBuilder)this.setDouble(BlobOption.blob_garbage_collection_age_cutoff, d);
        }

        @Override
        public double blobGarbageCollectionAgeCutoff() {
            return this.getDouble(BlobOption.blob_garbage_collection_age_cutoff);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setBlobGarbageCollectionForceThreshold(double d) {
            return (MutableColumnFamilyOptionsBuilder)this.setDouble(BlobOption.blob_garbage_collection_force_threshold, d);
        }

        @Override
        public double blobGarbageCollectionForceThreshold() {
            return this.getDouble(BlobOption.blob_garbage_collection_force_threshold);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setBlobCompactionReadaheadSize(long l) {
            return (MutableColumnFamilyOptionsBuilder)this.setLong(BlobOption.blob_compaction_readahead_size, l);
        }

        @Override
        public long blobCompactionReadaheadSize() {
            return this.getLong(BlobOption.blob_compaction_readahead_size);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setBlobFileStartingLevel(int n) {
            return (MutableColumnFamilyOptionsBuilder)this.setInt(BlobOption.blob_file_starting_level, n);
        }

        @Override
        public int blobFileStartingLevel() {
            return this.getInt(BlobOption.blob_file_starting_level);
        }

        @Override
        public MutableColumnFamilyOptionsBuilder setPrepopulateBlobCache(PrepopulateBlobCache prepopulateBlobCache) {
            return (MutableColumnFamilyOptionsBuilder)this.setEnum(BlobOption.prepopulate_blob_cache, prepopulateBlobCache);
        }

        @Override
        public PrepopulateBlobCache prepopulateBlobCache() {
            return (PrepopulateBlobCache)((Object)this.getEnum(BlobOption.prepopulate_blob_cache));
        }

        static {
            for (MemtableOption enum_ : MemtableOption.values()) {
                ALL_KEYS_LOOKUP.put(enum_.name(), enum_);
            }
            for (Enum enum_ : CompactionOption.values()) {
                ALL_KEYS_LOOKUP.put(enum_.name(), (MutableColumnFamilyOptionKey)((Object)enum_));
            }
            for (Enum enum_ : MiscOption.values()) {
                ALL_KEYS_LOOKUP.put(enum_.name(), (MutableColumnFamilyOptionKey)((Object)enum_));
            }
            for (Enum enum_ : BlobOption.values()) {
                ALL_KEYS_LOOKUP.put(enum_.name(), (MutableColumnFamilyOptionKey)((Object)enum_));
            }
        }
    }

    public static enum MiscOption implements MutableColumnFamilyOptionKey
    {
        max_sequential_skip_in_iterations(MutableOptionKey.ValueType.LONG),
        paranoid_file_checks(MutableOptionKey.ValueType.BOOLEAN),
        report_bg_io_stats(MutableOptionKey.ValueType.BOOLEAN),
        compression(MutableOptionKey.ValueType.ENUM);

        private final MutableOptionKey.ValueType valueType;

        private MiscOption(MutableOptionKey.ValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public MutableOptionKey.ValueType getValueType() {
            return this.valueType;
        }
    }

    public static enum BlobOption implements MutableColumnFamilyOptionKey
    {
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

        private BlobOption(MutableOptionKey.ValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public MutableOptionKey.ValueType getValueType() {
            return this.valueType;
        }
    }

    public static enum CompactionOption implements MutableColumnFamilyOptionKey
    {
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

        private CompactionOption(MutableOptionKey.ValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public MutableOptionKey.ValueType getValueType() {
            return this.valueType;
        }
    }

    public static enum MemtableOption implements MutableColumnFamilyOptionKey
    {
        write_buffer_size(MutableOptionKey.ValueType.LONG),
        arena_block_size(MutableOptionKey.ValueType.LONG),
        memtable_prefix_bloom_size_ratio(MutableOptionKey.ValueType.DOUBLE),
        memtable_whole_key_filtering(MutableOptionKey.ValueType.BOOLEAN),
        memtable_prefix_bloom_bits(MutableOptionKey.ValueType.INT),
        memtable_prefix_bloom_probes(MutableOptionKey.ValueType.INT),
        memtable_huge_page_size(MutableOptionKey.ValueType.LONG),
        max_successive_merges(MutableOptionKey.ValueType.LONG),
        filter_deletes(MutableOptionKey.ValueType.BOOLEAN),
        max_write_buffer_number(MutableOptionKey.ValueType.INT),
        inplace_update_num_locks(MutableOptionKey.ValueType.LONG),
        experimental_mempurge_threshold(MutableOptionKey.ValueType.DOUBLE);

        private final MutableOptionKey.ValueType valueType;

        private MemtableOption(MutableOptionKey.ValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public MutableOptionKey.ValueType getValueType() {
            return this.valueType;
        }
    }

    private static interface MutableColumnFamilyOptionKey
    extends MutableOptionKey {
    }
}

