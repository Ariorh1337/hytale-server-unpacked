package org.rocksdb;

public interface AdvancedMutableColumnFamilyOptionsInterface<T extends AdvancedMutableColumnFamilyOptionsInterface<T>> {
   T setMaxWriteBufferNumber(int var1);

   int maxWriteBufferNumber();

   T setInplaceUpdateNumLocks(long var1);

   long inplaceUpdateNumLocks();

   T setMemtablePrefixBloomSizeRatio(double var1);

   double memtablePrefixBloomSizeRatio();

   T setExperimentalMempurgeThreshold(double var1);

   double experimentalMempurgeThreshold();

   T setMinTombstonesForRangeConversion(int var1);

   int minTombstonesForRangeConversion();

   T setMemtableWholeKeyFiltering(boolean var1);

   boolean memtableWholeKeyFiltering();

   T setMemtableHugePageSize(long var1);

   long memtableHugePageSize();

   T setArenaBlockSize(long var1);

   long arenaBlockSize();

   T setLevel0SlowdownWritesTrigger(int var1);

   int level0SlowdownWritesTrigger();

   T setLevel0StopWritesTrigger(int var1);

   int level0StopWritesTrigger();

   T setTargetFileSizeBase(long var1);

   long targetFileSizeBase();

   T setTargetFileSizeMultiplier(int var1);

   int targetFileSizeMultiplier();

   T setMaxBytesForLevelMultiplier(double var1);

   double maxBytesForLevelMultiplier();

   T setMaxBytesForLevelMultiplierAdditional(int[] var1);

   int[] maxBytesForLevelMultiplierAdditional();

   T setSoftPendingCompactionBytesLimit(long var1);

   long softPendingCompactionBytesLimit();

   T setHardPendingCompactionBytesLimit(long var1);

   long hardPendingCompactionBytesLimit();

   T setMaxSequentialSkipInIterations(long var1);

   long maxSequentialSkipInIterations();

   T setMaxSuccessiveMerges(long var1);

   long maxSuccessiveMerges();

   T setParanoidFileChecks(boolean var1);

   boolean paranoidFileChecks();

   T setReportBgIoStats(boolean var1);

   boolean reportBgIoStats();

   T setTtl(long var1);

   long ttl();

   T setPeriodicCompactionSeconds(long var1);

   long periodicCompactionSeconds();

   T setEnableBlobFiles(boolean var1);

   boolean enableBlobFiles();

   T setMinBlobSize(long var1);

   long minBlobSize();

   T setBlobFileSize(long var1);

   long blobFileSize();

   T setBlobCompressionType(CompressionType var1);

   CompressionType blobCompressionType();

   T setEnableBlobGarbageCollection(boolean var1);

   boolean enableBlobGarbageCollection();

   T setBlobGarbageCollectionAgeCutoff(double var1);

   double blobGarbageCollectionAgeCutoff();

   T setBlobGarbageCollectionForceThreshold(double var1);

   double blobGarbageCollectionForceThreshold();

   T setReadTriggeredCompactionThreshold(double var1);

   double readTriggeredCompactionThreshold();

   T setBlobCompactionReadaheadSize(long var1);

   long blobCompactionReadaheadSize();

   T setBlobFileStartingLevel(int var1);

   int blobFileStartingLevel();

   T setPrepopulateBlobCache(PrepopulateBlobCache var1);

   PrepopulateBlobCache prepopulateBlobCache();
}
