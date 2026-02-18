/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Collection;
import java.util.List;
import org.rocksdb.AbstractEventListener;
import org.rocksdb.AbstractWalFilter;
import org.rocksdb.Cache;
import org.rocksdb.DbPath;
import org.rocksdb.Env;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerInterface;
import org.rocksdb.RateLimiter;
import org.rocksdb.SstFileManager;
import org.rocksdb.Statistics;
import org.rocksdb.WALRecoveryMode;
import org.rocksdb.WalFilter;
import org.rocksdb.WriteBufferManager;

public interface DBOptionsInterface<T extends DBOptionsInterface<T>> {
    public T optimizeForSmallDb();

    public T setEnv(Env var1);

    public Env getEnv();

    public T setIncreaseParallelism(int var1);

    public T setCreateIfMissing(boolean var1);

    public boolean createIfMissing();

    public T setCreateMissingColumnFamilies(boolean var1);

    public boolean createMissingColumnFamilies();

    public T setErrorIfExists(boolean var1);

    public boolean errorIfExists();

    public T setParanoidChecks(boolean var1);

    public boolean paranoidChecks();

    public T setRateLimiter(RateLimiter var1);

    public T setSstFileManager(SstFileManager var1);

    public T setLogger(LoggerInterface var1);

    public T setInfoLogLevel(InfoLogLevel var1);

    public InfoLogLevel infoLogLevel();

    public T setMaxFileOpeningThreads(int var1);

    public int maxFileOpeningThreads();

    public T setStatistics(Statistics var1);

    public Statistics statistics();

    public T setUseFsync(boolean var1);

    public boolean useFsync();

    public T setDbPaths(Collection<DbPath> var1);

    public List<DbPath> dbPaths();

    public T setDbLogDir(String var1);

    public String dbLogDir();

    public T setWalDir(String var1);

    public String walDir();

    public T setDeleteObsoleteFilesPeriodMicros(long var1);

    public long deleteObsoleteFilesPeriodMicros();

    public T setMaxSubcompactions(int var1);

    public int maxSubcompactions();

    @Deprecated
    public T setMaxBackgroundFlushes(int var1);

    @Deprecated
    public int maxBackgroundFlushes();

    public T setMaxLogFileSize(long var1);

    public long maxLogFileSize();

    public T setLogFileTimeToRoll(long var1);

    public long logFileTimeToRoll();

    public T setKeepLogFileNum(long var1);

    public long keepLogFileNum();

    public T setRecycleLogFileNum(long var1);

    public long recycleLogFileNum();

    public T setMaxManifestFileSize(long var1);

    public long maxManifestFileSize();

    public T setTableCacheNumshardbits(int var1);

    public int tableCacheNumshardbits();

    public T setWalTtlSeconds(long var1);

    public long walTtlSeconds();

    public T setWalSizeLimitMB(long var1);

    public long walSizeLimitMB();

    public T setMaxWriteBatchGroupSizeBytes(long var1);

    public long maxWriteBatchGroupSizeBytes();

    public T setManifestPreallocationSize(long var1);

    public long manifestPreallocationSize();

    public T setUseDirectReads(boolean var1);

    public boolean useDirectReads();

    public T setUseDirectIoForFlushAndCompaction(boolean var1);

    public boolean useDirectIoForFlushAndCompaction();

    public T setAllowFAllocate(boolean var1);

    public boolean allowFAllocate();

    public T setAllowMmapReads(boolean var1);

    public boolean allowMmapReads();

    public T setAllowMmapWrites(boolean var1);

    public boolean allowMmapWrites();

    public T setIsFdCloseOnExec(boolean var1);

    public boolean isFdCloseOnExec();

    public T setAdviseRandomOnOpen(boolean var1);

    public boolean adviseRandomOnOpen();

    public T setDbWriteBufferSize(long var1);

    public T setWriteBufferManager(WriteBufferManager var1);

    public WriteBufferManager writeBufferManager();

    public long dbWriteBufferSize();

    public T setUseAdaptiveMutex(boolean var1);

    public boolean useAdaptiveMutex();

    public T setListeners(List<AbstractEventListener> var1);

    public List<AbstractEventListener> listeners();

    public T setEnableThreadTracking(boolean var1);

    public boolean enableThreadTracking();

    public T setEnablePipelinedWrite(boolean var1);

    public boolean enablePipelinedWrite();

    public T setUnorderedWrite(boolean var1);

    public boolean unorderedWrite();

    public T setAllowConcurrentMemtableWrite(boolean var1);

    public boolean allowConcurrentMemtableWrite();

    public T setEnableWriteThreadAdaptiveYield(boolean var1);

    public boolean enableWriteThreadAdaptiveYield();

    public T setWriteThreadMaxYieldUsec(long var1);

    public long writeThreadMaxYieldUsec();

    public T setWriteThreadSlowYieldUsec(long var1);

    public long writeThreadSlowYieldUsec();

    public T setSkipStatsUpdateOnDbOpen(boolean var1);

    public boolean skipStatsUpdateOnDbOpen();

    public T setSkipCheckingSstFileSizesOnDbOpen(boolean var1);

    public boolean skipCheckingSstFileSizesOnDbOpen();

    public T setWalRecoveryMode(WALRecoveryMode var1);

    public WALRecoveryMode walRecoveryMode();

    public T setAllow2pc(boolean var1);

    public boolean allow2pc();

    public T setRowCache(Cache var1);

    public Cache rowCache();

    public T setWalFilter(AbstractWalFilter var1);

    public WalFilter walFilter();

    public T setFailIfOptionsFileError(boolean var1);

    public boolean failIfOptionsFileError();

    public T setDumpMallocStats(boolean var1);

    public boolean dumpMallocStats();

    public T setAvoidFlushDuringRecovery(boolean var1);

    public boolean avoidFlushDuringRecovery();

    public T setAllowIngestBehind(boolean var1);

    public boolean allowIngestBehind();

    public T setTwoWriteQueues(boolean var1);

    public boolean twoWriteQueues();

    public T setManualWalFlush(boolean var1);

    public boolean manualWalFlush();

    public T setAtomicFlush(boolean var1);

    public boolean atomicFlush();

    public T setAvoidUnnecessaryBlockingIO(boolean var1);

    public boolean avoidUnnecessaryBlockingIO();

    public T setPersistStatsToDisk(boolean var1);

    public boolean persistStatsToDisk();

    public T setWriteDbidToManifest(boolean var1);

    public boolean writeDbidToManifest();

    public T setLogReadaheadSize(long var1);

    public long logReadaheadSize();

    public T setBestEffortsRecovery(boolean var1);

    public boolean bestEffortsRecovery();

    public T setMaxBgErrorResumeCount(int var1);

    public int maxBgerrorResumeCount();

    public T setBgerrorResumeRetryInterval(long var1);

    public long bgerrorResumeRetryInterval();

    public T setDailyOffpeakTimeUTC(String var1);

    public String dailyOffpeakTimeUTC();
}

