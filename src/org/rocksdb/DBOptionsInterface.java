package org.rocksdb;

import java.util.Collection;
import java.util.List;

public interface DBOptionsInterface<T extends DBOptionsInterface<T>> {
   T optimizeForSmallDb();

   T setEnv(Env var1);

   Env getEnv();

   T setIncreaseParallelism(int var1);

   T setCreateIfMissing(boolean var1);

   boolean createIfMissing();

   T setCreateMissingColumnFamilies(boolean var1);

   boolean createMissingColumnFamilies();

   T setErrorIfExists(boolean var1);

   boolean errorIfExists();

   T setParanoidChecks(boolean var1);

   boolean paranoidChecks();

   T setRateLimiter(RateLimiter var1);

   T setSstFileManager(SstFileManager var1);

   T setLogger(LoggerInterface var1);

   T setInfoLogLevel(InfoLogLevel var1);

   InfoLogLevel infoLogLevel();

   T setMaxFileOpeningThreads(int var1);

   int maxFileOpeningThreads();

   T setStatistics(Statistics var1);

   Statistics statistics();

   T setUseFsync(boolean var1);

   boolean useFsync();

   T setDbPaths(Collection<DbPath> var1);

   List<DbPath> dbPaths();

   T setDbLogDir(String var1);

   String dbLogDir();

   T setWalDir(String var1);

   String walDir();

   T setDeleteObsoleteFilesPeriodMicros(long var1);

   long deleteObsoleteFilesPeriodMicros();

   T setMaxSubcompactions(int var1);

   int maxSubcompactions();

   @Deprecated
   T setMaxBackgroundFlushes(int var1);

   @Deprecated
   int maxBackgroundFlushes();

   T setMaxLogFileSize(long var1);

   long maxLogFileSize();

   T setLogFileTimeToRoll(long var1);

   long logFileTimeToRoll();

   T setKeepLogFileNum(long var1);

   long keepLogFileNum();

   T setRecycleLogFileNum(long var1);

   long recycleLogFileNum();

   T setMaxManifestFileSize(long var1);

   long maxManifestFileSize();

   T setTableCacheNumshardbits(int var1);

   int tableCacheNumshardbits();

   T setWalTtlSeconds(long var1);

   long walTtlSeconds();

   T setWalSizeLimitMB(long var1);

   long walSizeLimitMB();

   T setMaxWriteBatchGroupSizeBytes(long var1);

   long maxWriteBatchGroupSizeBytes();

   T setManifestPreallocationSize(long var1);

   long manifestPreallocationSize();

   T setUseDirectReads(boolean var1);

   boolean useDirectReads();

   T setUseDirectIoForFlushAndCompaction(boolean var1);

   boolean useDirectIoForFlushAndCompaction();

   T setAllowFAllocate(boolean var1);

   boolean allowFAllocate();

   T setAllowMmapReads(boolean var1);

   boolean allowMmapReads();

   T setAllowMmapWrites(boolean var1);

   boolean allowMmapWrites();

   T setIsFdCloseOnExec(boolean var1);

   boolean isFdCloseOnExec();

   T setAdviseRandomOnOpen(boolean var1);

   boolean adviseRandomOnOpen();

   T setDbWriteBufferSize(long var1);

   T setWriteBufferManager(WriteBufferManager var1);

   WriteBufferManager writeBufferManager();

   long dbWriteBufferSize();

   T setUseAdaptiveMutex(boolean var1);

   boolean useAdaptiveMutex();

   T setListeners(List<AbstractEventListener> var1);

   List<AbstractEventListener> listeners();

   T setEnableThreadTracking(boolean var1);

   boolean enableThreadTracking();

   T setEnablePipelinedWrite(boolean var1);

   boolean enablePipelinedWrite();

   T setUnorderedWrite(boolean var1);

   boolean unorderedWrite();

   T setAllowConcurrentMemtableWrite(boolean var1);

   boolean allowConcurrentMemtableWrite();

   T setEnableWriteThreadAdaptiveYield(boolean var1);

   boolean enableWriteThreadAdaptiveYield();

   T setWriteThreadMaxYieldUsec(long var1);

   long writeThreadMaxYieldUsec();

   T setWriteThreadSlowYieldUsec(long var1);

   long writeThreadSlowYieldUsec();

   T setSkipStatsUpdateOnDbOpen(boolean var1);

   boolean skipStatsUpdateOnDbOpen();

   T setSkipCheckingSstFileSizesOnDbOpen(boolean var1);

   boolean skipCheckingSstFileSizesOnDbOpen();

   T setWalRecoveryMode(WALRecoveryMode var1);

   WALRecoveryMode walRecoveryMode();

   T setAllow2pc(boolean var1);

   boolean allow2pc();

   T setRowCache(Cache var1);

   Cache rowCache();

   T setWalFilter(AbstractWalFilter var1);

   WalFilter walFilter();

   T setFailIfOptionsFileError(boolean var1);

   boolean failIfOptionsFileError();

   T setDumpMallocStats(boolean var1);

   boolean dumpMallocStats();

   T setAvoidFlushDuringRecovery(boolean var1);

   boolean avoidFlushDuringRecovery();

   T setAllowIngestBehind(boolean var1);

   boolean allowIngestBehind();

   T setTwoWriteQueues(boolean var1);

   boolean twoWriteQueues();

   T setManualWalFlush(boolean var1);

   boolean manualWalFlush();

   T setAtomicFlush(boolean var1);

   boolean atomicFlush();

   T setAvoidUnnecessaryBlockingIO(boolean var1);

   boolean avoidUnnecessaryBlockingIO();

   T setPersistStatsToDisk(boolean var1);

   boolean persistStatsToDisk();

   T setWriteDbidToManifest(boolean var1);

   boolean writeDbidToManifest();

   T setLogReadaheadSize(long var1);

   long logReadaheadSize();

   T setBestEffortsRecovery(boolean var1);

   boolean bestEffortsRecovery();

   T setMaxBgErrorResumeCount(int var1);

   int maxBgerrorResumeCount();

   T setBgerrorResumeRetryInterval(long var1);

   long bgerrorResumeRetryInterval();

   T setDailyOffpeakTimeUTC(String var1);

   String dailyOffpeakTimeUTC();
}
