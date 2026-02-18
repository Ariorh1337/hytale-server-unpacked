/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.CompressionType;
import org.rocksdb.PrepopulateBlobCache;

public interface AdvancedMutableColumnFamilyOptionsInterface<T extends AdvancedMutableColumnFamilyOptionsInterface<T>> {
    public T setMaxWriteBufferNumber(int var1);

    public int maxWriteBufferNumber();

    public T setInplaceUpdateNumLocks(long var1);

    public long inplaceUpdateNumLocks();

    public T setMemtablePrefixBloomSizeRatio(double var1);

    public double memtablePrefixBloomSizeRatio();

    public T setExperimentalMempurgeThreshold(double var1);

    public double experimentalMempurgeThreshold();

    public T setMemtableWholeKeyFiltering(boolean var1);

    public boolean memtableWholeKeyFiltering();

    public T setMemtableHugePageSize(long var1);

    public long memtableHugePageSize();

    public T setArenaBlockSize(long var1);

    public long arenaBlockSize();

    public T setLevel0SlowdownWritesTrigger(int var1);

    public int level0SlowdownWritesTrigger();

    public T setLevel0StopWritesTrigger(int var1);

    public int level0StopWritesTrigger();

    public T setTargetFileSizeBase(long var1);

    public long targetFileSizeBase();

    public T setTargetFileSizeMultiplier(int var1);

    public int targetFileSizeMultiplier();

    public T setMaxBytesForLevelMultiplier(double var1);

    public double maxBytesForLevelMultiplier();

    public T setMaxBytesForLevelMultiplierAdditional(int[] var1);

    public int[] maxBytesForLevelMultiplierAdditional();

    public T setSoftPendingCompactionBytesLimit(long var1);

    public long softPendingCompactionBytesLimit();

    public T setHardPendingCompactionBytesLimit(long var1);

    public long hardPendingCompactionBytesLimit();

    public T setMaxSequentialSkipInIterations(long var1);

    public long maxSequentialSkipInIterations();

    public T setMaxSuccessiveMerges(long var1);

    public long maxSuccessiveMerges();

    public T setParanoidFileChecks(boolean var1);

    public boolean paranoidFileChecks();

    public T setReportBgIoStats(boolean var1);

    public boolean reportBgIoStats();

    public T setTtl(long var1);

    public long ttl();

    public T setPeriodicCompactionSeconds(long var1);

    public long periodicCompactionSeconds();

    public T setEnableBlobFiles(boolean var1);

    public boolean enableBlobFiles();

    public T setMinBlobSize(long var1);

    public long minBlobSize();

    public T setBlobFileSize(long var1);

    public long blobFileSize();

    public T setBlobCompressionType(CompressionType var1);

    public CompressionType blobCompressionType();

    public T setEnableBlobGarbageCollection(boolean var1);

    public boolean enableBlobGarbageCollection();

    public T setBlobGarbageCollectionAgeCutoff(double var1);

    public double blobGarbageCollectionAgeCutoff();

    public T setBlobGarbageCollectionForceThreshold(double var1);

    public double blobGarbageCollectionForceThreshold();

    public T setBlobCompactionReadaheadSize(long var1);

    public long blobCompactionReadaheadSize();

    public T setBlobFileStartingLevel(int var1);

    public int blobFileStartingLevel();

    public T setPrepopulateBlobCache(PrepopulateBlobCache var1);

    public PrepopulateBlobCache prepopulateBlobCache();
}

