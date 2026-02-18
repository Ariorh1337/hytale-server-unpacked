/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Cache;
import org.rocksdb.ChecksumType;
import org.rocksdb.DataBlockIndexType;
import org.rocksdb.Filter;
import org.rocksdb.FilterPolicyType;
import org.rocksdb.IndexShorteningMode;
import org.rocksdb.IndexType;
import org.rocksdb.PersistentCache;
import org.rocksdb.TableFormatConfig;

public class BlockBasedTableConfig
extends TableFormatConfig {
    private boolean cacheIndexAndFilterBlocks;
    private boolean cacheIndexAndFilterBlocksWithHighPriority;
    private boolean pinL0FilterAndIndexBlocksInCache;
    private boolean pinTopLevelIndexAndFilter;
    private IndexType indexType;
    private DataBlockIndexType dataBlockIndexType;
    private double dataBlockHashTableUtilRatio;
    private ChecksumType checksumType;
    private boolean noBlockCache;
    private Cache blockCache;
    private PersistentCache persistentCache;
    private long blockSize;
    private int blockSizeDeviation;
    private int blockRestartInterval;
    private int indexBlockRestartInterval;
    private long metadataBlockSize;
    private boolean partitionFilters;
    private boolean optimizeFiltersForMemory;
    private boolean useDeltaEncoding;
    private Filter filterPolicy;
    private boolean wholeKeyFiltering;
    private boolean verifyCompression;
    private int readAmpBytesPerBit;
    private int formatVersion;
    private boolean enableIndexCompression;
    private boolean blockAlign;
    private IndexShorteningMode indexShortening;
    @Deprecated
    private long blockCacheSize;
    @Deprecated
    private int blockCacheNumShardBits;

    public BlockBasedTableConfig() {
        this.cacheIndexAndFilterBlocks = false;
        this.cacheIndexAndFilterBlocksWithHighPriority = true;
        this.pinL0FilterAndIndexBlocksInCache = false;
        this.pinTopLevelIndexAndFilter = true;
        this.indexType = IndexType.kBinarySearch;
        this.dataBlockIndexType = DataBlockIndexType.kDataBlockBinarySearch;
        this.dataBlockHashTableUtilRatio = 0.75;
        this.checksumType = ChecksumType.kXXH3;
        this.noBlockCache = false;
        this.blockCache = null;
        this.persistentCache = null;
        this.blockSize = 4096L;
        this.blockSizeDeviation = 10;
        this.blockRestartInterval = 16;
        this.indexBlockRestartInterval = 1;
        this.metadataBlockSize = 4096L;
        this.partitionFilters = false;
        this.optimizeFiltersForMemory = true;
        this.useDeltaEncoding = true;
        this.filterPolicy = null;
        this.wholeKeyFiltering = true;
        this.verifyCompression = false;
        this.readAmpBytesPerBit = 0;
        this.formatVersion = 6;
        this.enableIndexCompression = true;
        this.blockAlign = false;
        this.indexShortening = IndexShorteningMode.kShortenSeparators;
        this.blockCacheSize = 0x800000L;
        this.blockCacheNumShardBits = 0;
    }

    private BlockBasedTableConfig(boolean bl, boolean bl2, boolean bl3, boolean bl4, byte by, byte by2, double d, byte by3, boolean bl5, long l, int n, int n2, int n3, long l2, boolean bl6, boolean bl7, boolean bl8, boolean bl9, boolean bl10, int n4, int n5, boolean bl11, boolean bl12, byte by4, byte by5, long l3, double d2) {
        this.cacheIndexAndFilterBlocks = bl;
        this.cacheIndexAndFilterBlocksWithHighPriority = bl2;
        this.pinL0FilterAndIndexBlocksInCache = bl3;
        this.pinTopLevelIndexAndFilter = bl4;
        this.indexType = IndexType.values()[by];
        this.dataBlockIndexType = DataBlockIndexType.values()[by2];
        this.dataBlockHashTableUtilRatio = d;
        this.checksumType = ChecksumType.values()[by3];
        this.noBlockCache = bl5;
        this.blockSize = l;
        this.blockSizeDeviation = n;
        this.blockRestartInterval = n2;
        this.indexBlockRestartInterval = n3;
        this.metadataBlockSize = l2;
        this.partitionFilters = bl6;
        this.optimizeFiltersForMemory = bl7;
        this.useDeltaEncoding = bl8;
        this.wholeKeyFiltering = bl9;
        this.verifyCompression = bl10;
        this.readAmpBytesPerBit = n4;
        this.formatVersion = n5;
        this.enableIndexCompression = bl11;
        this.blockAlign = bl12;
        this.indexShortening = IndexShorteningMode.values()[by4];
        try (Filter filter = FilterPolicyType.values()[by5].createFilter(l3, d2);){
            if (filter != null) {
                filter.disOwnNativeHandle();
                this.setFilterPolicy(filter);
            }
        }
    }

    public boolean cacheIndexAndFilterBlocks() {
        return this.cacheIndexAndFilterBlocks;
    }

    public BlockBasedTableConfig setCacheIndexAndFilterBlocks(boolean bl) {
        this.cacheIndexAndFilterBlocks = bl;
        return this;
    }

    public boolean cacheIndexAndFilterBlocksWithHighPriority() {
        return this.cacheIndexAndFilterBlocksWithHighPriority;
    }

    public BlockBasedTableConfig setCacheIndexAndFilterBlocksWithHighPriority(boolean bl) {
        this.cacheIndexAndFilterBlocksWithHighPriority = bl;
        return this;
    }

    public boolean pinL0FilterAndIndexBlocksInCache() {
        return this.pinL0FilterAndIndexBlocksInCache;
    }

    public BlockBasedTableConfig setPinL0FilterAndIndexBlocksInCache(boolean bl) {
        this.pinL0FilterAndIndexBlocksInCache = bl;
        return this;
    }

    public boolean pinTopLevelIndexAndFilter() {
        return this.pinTopLevelIndexAndFilter;
    }

    public BlockBasedTableConfig setPinTopLevelIndexAndFilter(boolean bl) {
        this.pinTopLevelIndexAndFilter = bl;
        return this;
    }

    public IndexType indexType() {
        return this.indexType;
    }

    public BlockBasedTableConfig setIndexType(IndexType indexType) {
        this.indexType = indexType;
        return this;
    }

    public DataBlockIndexType dataBlockIndexType() {
        return this.dataBlockIndexType;
    }

    public BlockBasedTableConfig setDataBlockIndexType(DataBlockIndexType dataBlockIndexType) {
        this.dataBlockIndexType = dataBlockIndexType;
        return this;
    }

    public double dataBlockHashTableUtilRatio() {
        return this.dataBlockHashTableUtilRatio;
    }

    public BlockBasedTableConfig setDataBlockHashTableUtilRatio(double d) {
        this.dataBlockHashTableUtilRatio = d;
        return this;
    }

    public ChecksumType checksumType() {
        return this.checksumType;
    }

    public BlockBasedTableConfig setChecksumType(ChecksumType checksumType) {
        this.checksumType = checksumType;
        return this;
    }

    public boolean noBlockCache() {
        return this.noBlockCache;
    }

    public BlockBasedTableConfig setNoBlockCache(boolean bl) {
        this.noBlockCache = bl;
        return this;
    }

    public BlockBasedTableConfig setBlockCache(Cache cache) {
        this.blockCache = cache;
        return this;
    }

    public BlockBasedTableConfig setPersistentCache(PersistentCache persistentCache) {
        this.persistentCache = persistentCache;
        return this;
    }

    public long blockSize() {
        return this.blockSize;
    }

    public BlockBasedTableConfig setBlockSize(long l) {
        this.blockSize = l;
        return this;
    }

    public int blockSizeDeviation() {
        return this.blockSizeDeviation;
    }

    public BlockBasedTableConfig setBlockSizeDeviation(int n) {
        this.blockSizeDeviation = n;
        return this;
    }

    public int blockRestartInterval() {
        return this.blockRestartInterval;
    }

    public BlockBasedTableConfig setBlockRestartInterval(int n) {
        this.blockRestartInterval = n;
        return this;
    }

    public int indexBlockRestartInterval() {
        return this.indexBlockRestartInterval;
    }

    public BlockBasedTableConfig setIndexBlockRestartInterval(int n) {
        this.indexBlockRestartInterval = n;
        return this;
    }

    public long metadataBlockSize() {
        return this.metadataBlockSize;
    }

    public BlockBasedTableConfig setMetadataBlockSize(long l) {
        this.metadataBlockSize = l;
        return this;
    }

    public boolean partitionFilters() {
        return this.partitionFilters;
    }

    public BlockBasedTableConfig setPartitionFilters(boolean bl) {
        this.partitionFilters = bl;
        return this;
    }

    public boolean optimizeFiltersForMemory() {
        return this.optimizeFiltersForMemory;
    }

    public BlockBasedTableConfig setOptimizeFiltersForMemory(boolean bl) {
        this.optimizeFiltersForMemory = bl;
        return this;
    }

    public boolean useDeltaEncoding() {
        return this.useDeltaEncoding;
    }

    public BlockBasedTableConfig setUseDeltaEncoding(boolean bl) {
        this.useDeltaEncoding = bl;
        return this;
    }

    public Filter filterPolicy() {
        return this.filterPolicy;
    }

    public BlockBasedTableConfig setFilterPolicy(Filter filter) {
        this.filterPolicy = filter;
        return this;
    }

    @Deprecated
    public BlockBasedTableConfig setFilter(Filter filter) {
        return this.setFilterPolicy(filter);
    }

    public boolean wholeKeyFiltering() {
        return this.wholeKeyFiltering;
    }

    public BlockBasedTableConfig setWholeKeyFiltering(boolean bl) {
        this.wholeKeyFiltering = bl;
        return this;
    }

    public boolean verifyCompression() {
        return this.verifyCompression;
    }

    public BlockBasedTableConfig setVerifyCompression(boolean bl) {
        this.verifyCompression = bl;
        return this;
    }

    public int readAmpBytesPerBit() {
        return this.readAmpBytesPerBit;
    }

    public BlockBasedTableConfig setReadAmpBytesPerBit(int n) {
        this.readAmpBytesPerBit = n;
        return this;
    }

    public int formatVersion() {
        return this.formatVersion;
    }

    public BlockBasedTableConfig setFormatVersion(int n) {
        assert (n >= 0);
        this.formatVersion = n;
        return this;
    }

    public boolean enableIndexCompression() {
        return this.enableIndexCompression;
    }

    public BlockBasedTableConfig setEnableIndexCompression(boolean bl) {
        this.enableIndexCompression = bl;
        return this;
    }

    public boolean blockAlign() {
        return this.blockAlign;
    }

    public BlockBasedTableConfig setBlockAlign(boolean bl) {
        this.blockAlign = bl;
        return this;
    }

    public IndexShorteningMode indexShortening() {
        return this.indexShortening;
    }

    public BlockBasedTableConfig setIndexShortening(IndexShorteningMode indexShorteningMode) {
        this.indexShortening = indexShorteningMode;
        return this;
    }

    @Deprecated
    public long blockCacheSize() {
        return this.blockCacheSize;
    }

    @Deprecated
    public BlockBasedTableConfig setBlockCacheSize(long l) {
        this.blockCacheSize = l;
        return this;
    }

    @Deprecated
    public int cacheNumShardBits() {
        return this.blockCacheNumShardBits;
    }

    @Deprecated
    public BlockBasedTableConfig setCacheNumShardBits(int n) {
        this.blockCacheNumShardBits = n;
        return this;
    }

    @Deprecated
    public boolean hashIndexAllowCollision() {
        return true;
    }

    @Deprecated
    public BlockBasedTableConfig setHashIndexAllowCollision(boolean bl) {
        return this;
    }

    @Override
    protected long newTableFactoryHandle() {
        long l = this.filterPolicy != null ? this.filterPolicy.nativeHandle_ : 0L;
        long l2 = this.blockCache != null ? this.blockCache.nativeHandle_ : 0L;
        long l3 = this.persistentCache != null ? this.persistentCache.nativeHandle_ : 0L;
        return BlockBasedTableConfig.newTableFactoryHandle(this.cacheIndexAndFilterBlocks, this.cacheIndexAndFilterBlocksWithHighPriority, this.pinL0FilterAndIndexBlocksInCache, this.pinTopLevelIndexAndFilter, this.indexType.getValue(), this.dataBlockIndexType.getValue(), this.dataBlockHashTableUtilRatio, this.checksumType.getValue(), this.noBlockCache, l2, l3, this.blockSize, this.blockSizeDeviation, this.blockRestartInterval, this.indexBlockRestartInterval, this.metadataBlockSize, this.partitionFilters, this.optimizeFiltersForMemory, this.useDeltaEncoding, l, this.wholeKeyFiltering, this.verifyCompression, this.readAmpBytesPerBit, this.formatVersion, this.enableIndexCompression, this.blockAlign, this.indexShortening.getValue(), this.blockCacheSize, this.blockCacheNumShardBits);
    }

    private static native long newTableFactoryHandle(boolean var0, boolean var1, boolean var2, boolean var3, byte var4, byte var5, double var6, byte var8, boolean var9, long var10, long var12, long var14, int var16, int var17, int var18, long var19, boolean var21, boolean var22, boolean var23, long var24, boolean var26, boolean var27, int var28, int var29, boolean var30, boolean var31, byte var32, @Deprecated long var33, @Deprecated int var35);
}

