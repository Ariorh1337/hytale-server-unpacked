package org.rocksdb;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DBOptions extends RocksObject implements DBOptionsInterface<DBOptions>, MutableDBOptionsInterface<DBOptions> {
   static final int DEFAULT_NUM_SHARD_BITS = -1;
   private Env env_;
   private int numShardBits_;
   private RateLimiter rateLimiter_;
   private Cache rowCache_;
   private WalFilter walFilter_;
   private WriteBufferManager writeBufferManager_;

   public DBOptions() {
      super(newDBOptionsInstance());
      this.numShardBits_ = -1;
      this.env_ = Env.getDefault();
   }

   public DBOptions(DBOptions var1) {
      super(copyDBOptions(var1.nativeHandle_));
      this.env_ = var1.env_;
      this.numShardBits_ = var1.numShardBits_;
      this.rateLimiter_ = var1.rateLimiter_;
      this.rowCache_ = var1.rowCache_;
      this.walFilter_ = var1.walFilter_;
      this.writeBufferManager_ = var1.writeBufferManager_;
   }

   public DBOptions(Options var1) {
      super(newDBOptionsFromOptions(var1.nativeHandle_));
   }

   public static DBOptions getDBOptionsFromProps(ConfigOptions var0, Properties var1) {
      DBOptions var2 = null;
      String var3 = Options.getOptionStringFromProps(var1);
      long var4 = getDBOptionsFromProps(var0.nativeHandle_, var3);
      if (var4 != 0L) {
         var2 = new DBOptions(var4);
      }

      return var2;
   }

   public static DBOptions getDBOptionsFromProps(Properties var0) {
      DBOptions var1 = null;
      String var2 = Options.getOptionStringFromProps(var0);
      long var3 = getDBOptionsFromProps(var2);
      if (var3 != 0L) {
         var1 = new DBOptions(var3);
      }

      return var1;
   }

   public DBOptions optimizeForSmallDb() {
      optimizeForSmallDb(this.nativeHandle_);
      return this;
   }

   public DBOptions setIncreaseParallelism(int var1) {
      assert this.isOwningHandle();
      setIncreaseParallelism(this.nativeHandle_, var1);
      return this;
   }

   public DBOptions setCreateIfMissing(boolean var1) {
      assert this.isOwningHandle();
      setCreateIfMissing(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean createIfMissing() {
      assert this.isOwningHandle();
      return createIfMissing(this.nativeHandle_);
   }

   public DBOptions setCreateMissingColumnFamilies(boolean var1) {
      assert this.isOwningHandle();
      setCreateMissingColumnFamilies(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean createMissingColumnFamilies() {
      assert this.isOwningHandle();
      return createMissingColumnFamilies(this.nativeHandle_);
   }

   public DBOptions setErrorIfExists(boolean var1) {
      assert this.isOwningHandle();
      setErrorIfExists(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean errorIfExists() {
      assert this.isOwningHandle();
      return errorIfExists(this.nativeHandle_);
   }

   public DBOptions setParanoidChecks(boolean var1) {
      assert this.isOwningHandle();
      setParanoidChecks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean paranoidChecks() {
      assert this.isOwningHandle();
      return paranoidChecks(this.nativeHandle_);
   }

   public DBOptions setEnv(Env var1) {
      setEnv(this.nativeHandle_, var1.nativeHandle_);
      this.env_ = var1;
      return this;
   }

   @Override
   public Env getEnv() {
      return this.env_;
   }

   public DBOptions setRateLimiter(RateLimiter var1) {
      assert this.isOwningHandle();
      this.rateLimiter_ = var1;
      setRateLimiter(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public DBOptions setSstFileManager(SstFileManager var1) {
      assert this.isOwningHandle();
      setSstFileManager(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public DBOptions setLogger(LoggerInterface var1) {
      assert this.isOwningHandle();
      setLogger(this.nativeHandle_, var1.getNativeHandle(), var1.getLoggerType().getValue());
      return this;
   }

   public DBOptions setInfoLogLevel(InfoLogLevel var1) {
      assert this.isOwningHandle();
      setInfoLogLevel(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public InfoLogLevel infoLogLevel() {
      assert this.isOwningHandle();
      return InfoLogLevel.getInfoLogLevel(infoLogLevel(this.nativeHandle_));
   }

   public DBOptions setMaxOpenFiles(int var1) {
      assert this.isOwningHandle();
      setMaxOpenFiles(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxOpenFiles() {
      assert this.isOwningHandle();
      return maxOpenFiles(this.nativeHandle_);
   }

   public DBOptions setMaxFileOpeningThreads(int var1) {
      assert this.isOwningHandle();
      setMaxFileOpeningThreads(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxFileOpeningThreads() {
      assert this.isOwningHandle();
      return maxFileOpeningThreads(this.nativeHandle_);
   }

   public DBOptions setMaxTotalWalSize(long var1) {
      assert this.isOwningHandle();
      setMaxTotalWalSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxTotalWalSize() {
      assert this.isOwningHandle();
      return maxTotalWalSize(this.nativeHandle_);
   }

   public DBOptions setStatistics(Statistics var1) {
      assert this.isOwningHandle();
      setStatistics(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   @Override
   public Statistics statistics() {
      assert this.isOwningHandle();
      long var1 = statistics(this.nativeHandle_);
      return var1 == 0L ? null : new Statistics(var1);
   }

   public DBOptions setUseFsync(boolean var1) {
      assert this.isOwningHandle();
      setUseFsync(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean useFsync() {
      assert this.isOwningHandle();
      return useFsync(this.nativeHandle_);
   }

   public DBOptions setDbPaths(Collection<DbPath> var1) {
      assert this.isOwningHandle();
      int var2 = var1.size();
      String[] var3 = new String[var2];
      long[] var4 = new long[var2];
      int var5 = 0;

      for (DbPath var7 : var1) {
         var3[var5] = var7.path.toString();
         var4[var5] = var7.targetSize;
         var5++;
      }

      setDbPaths(this.nativeHandle_, var3, var4);
      return this;
   }

   @Override
   public List<DbPath> dbPaths() {
      int var1 = (int)dbPathsLen(this.nativeHandle_);
      if (var1 == 0) {
         return Collections.emptyList();
      }

      String[] var2 = new String[var1];
      long[] var3 = new long[var1];
      dbPaths(this.nativeHandle_, var2, var3);
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var1; var5++) {
         var4.add(new DbPath(Paths.get(var2[var5]), var3[var5]));
      }

      return var4;
   }

   public DBOptions setDbLogDir(String var1) {
      assert this.isOwningHandle();
      setDbLogDir(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public String dbLogDir() {
      assert this.isOwningHandle();
      return dbLogDir(this.nativeHandle_);
   }

   public DBOptions setWalDir(String var1) {
      assert this.isOwningHandle();
      setWalDir(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public String walDir() {
      assert this.isOwningHandle();
      return walDir(this.nativeHandle_);
   }

   public DBOptions setDeleteObsoleteFilesPeriodMicros(long var1) {
      assert this.isOwningHandle();
      setDeleteObsoleteFilesPeriodMicros(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long deleteObsoleteFilesPeriodMicros() {
      assert this.isOwningHandle();
      return deleteObsoleteFilesPeriodMicros(this.nativeHandle_);
   }

   public DBOptions setMaxBackgroundJobs(int var1) {
      assert this.isOwningHandle();
      setMaxBackgroundJobs(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxBackgroundJobs() {
      assert this.isOwningHandle();
      return maxBackgroundJobs(this.nativeHandle_);
   }

   @Deprecated
   public DBOptions setMaxBackgroundCompactions(int var1) {
      assert this.isOwningHandle();
      setMaxBackgroundCompactions(this.nativeHandle_, var1);
      return this;
   }

   @Deprecated
   @Override
   public int maxBackgroundCompactions() {
      assert this.isOwningHandle();
      return maxBackgroundCompactions(this.nativeHandle_);
   }

   public DBOptions setMaxSubcompactions(int var1) {
      assert this.isOwningHandle();
      setMaxSubcompactions(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxSubcompactions() {
      assert this.isOwningHandle();
      return maxSubcompactions(this.nativeHandle_);
   }

   @Deprecated
   public DBOptions setMaxBackgroundFlushes(int var1) {
      assert this.isOwningHandle();
      setMaxBackgroundFlushes(this.nativeHandle_, var1);
      return this;
   }

   @Deprecated
   @Override
   public int maxBackgroundFlushes() {
      assert this.isOwningHandle();
      return maxBackgroundFlushes(this.nativeHandle_);
   }

   public DBOptions setMaxLogFileSize(long var1) {
      assert this.isOwningHandle();
      setMaxLogFileSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxLogFileSize() {
      assert this.isOwningHandle();
      return maxLogFileSize(this.nativeHandle_);
   }

   public DBOptions setLogFileTimeToRoll(long var1) {
      assert this.isOwningHandle();
      setLogFileTimeToRoll(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long logFileTimeToRoll() {
      assert this.isOwningHandle();
      return logFileTimeToRoll(this.nativeHandle_);
   }

   public DBOptions setKeepLogFileNum(long var1) {
      assert this.isOwningHandle();
      setKeepLogFileNum(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long keepLogFileNum() {
      assert this.isOwningHandle();
      return keepLogFileNum(this.nativeHandle_);
   }

   public DBOptions setRecycleLogFileNum(long var1) {
      assert this.isOwningHandle();
      setRecycleLogFileNum(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long recycleLogFileNum() {
      assert this.isOwningHandle();
      return recycleLogFileNum(this.nativeHandle_);
   }

   public DBOptions setMaxManifestFileSize(long var1) {
      assert this.isOwningHandle();
      setMaxManifestFileSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxManifestFileSize() {
      assert this.isOwningHandle();
      return maxManifestFileSize(this.nativeHandle_);
   }

   public DBOptions setTableCacheNumshardbits(int var1) {
      assert this.isOwningHandle();
      setTableCacheNumshardbits(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int tableCacheNumshardbits() {
      assert this.isOwningHandle();
      return tableCacheNumshardbits(this.nativeHandle_);
   }

   public DBOptions setWalTtlSeconds(long var1) {
      assert this.isOwningHandle();
      setWalTtlSeconds(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long walTtlSeconds() {
      assert this.isOwningHandle();
      return walTtlSeconds(this.nativeHandle_);
   }

   public DBOptions setWalSizeLimitMB(long var1) {
      assert this.isOwningHandle();
      setWalSizeLimitMB(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long walSizeLimitMB() {
      assert this.isOwningHandle();
      return walSizeLimitMB(this.nativeHandle_);
   }

   public DBOptions setMaxWriteBatchGroupSizeBytes(long var1) {
      setMaxWriteBatchGroupSizeBytes(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxWriteBatchGroupSizeBytes() {
      assert this.isOwningHandle();
      return maxWriteBatchGroupSizeBytes(this.nativeHandle_);
   }

   public DBOptions setManifestPreallocationSize(long var1) {
      assert this.isOwningHandle();
      setManifestPreallocationSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long manifestPreallocationSize() {
      assert this.isOwningHandle();
      return manifestPreallocationSize(this.nativeHandle_);
   }

   public DBOptions setAllowMmapReads(boolean var1) {
      assert this.isOwningHandle();
      setAllowMmapReads(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowMmapReads() {
      assert this.isOwningHandle();
      return allowMmapReads(this.nativeHandle_);
   }

   public DBOptions setAllowMmapWrites(boolean var1) {
      assert this.isOwningHandle();
      setAllowMmapWrites(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowMmapWrites() {
      assert this.isOwningHandle();
      return allowMmapWrites(this.nativeHandle_);
   }

   public DBOptions setUseDirectReads(boolean var1) {
      assert this.isOwningHandle();
      setUseDirectReads(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean useDirectReads() {
      assert this.isOwningHandle();
      return useDirectReads(this.nativeHandle_);
   }

   public DBOptions setUseDirectIoForFlushAndCompaction(boolean var1) {
      assert this.isOwningHandle();
      setUseDirectIoForFlushAndCompaction(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean useDirectIoForFlushAndCompaction() {
      assert this.isOwningHandle();
      return useDirectIoForFlushAndCompaction(this.nativeHandle_);
   }

   public DBOptions setAllowFAllocate(boolean var1) {
      assert this.isOwningHandle();
      setAllowFAllocate(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowFAllocate() {
      assert this.isOwningHandle();
      return allowFAllocate(this.nativeHandle_);
   }

   public DBOptions setIsFdCloseOnExec(boolean var1) {
      assert this.isOwningHandle();
      setIsFdCloseOnExec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean isFdCloseOnExec() {
      assert this.isOwningHandle();
      return isFdCloseOnExec(this.nativeHandle_);
   }

   public DBOptions setStatsDumpPeriodSec(int var1) {
      assert this.isOwningHandle();
      setStatsDumpPeriodSec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int statsDumpPeriodSec() {
      assert this.isOwningHandle();
      return statsDumpPeriodSec(this.nativeHandle_);
   }

   public DBOptions setStatsPersistPeriodSec(int var1) {
      assert this.isOwningHandle();
      setStatsPersistPeriodSec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int statsPersistPeriodSec() {
      assert this.isOwningHandle();
      return statsPersistPeriodSec(this.nativeHandle_);
   }

   public DBOptions setStatsHistoryBufferSize(long var1) {
      assert this.isOwningHandle();
      setStatsHistoryBufferSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long statsHistoryBufferSize() {
      assert this.isOwningHandle();
      return statsHistoryBufferSize(this.nativeHandle_);
   }

   public DBOptions setAdviseRandomOnOpen(boolean var1) {
      assert this.isOwningHandle();
      setAdviseRandomOnOpen(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean adviseRandomOnOpen() {
      return adviseRandomOnOpen(this.nativeHandle_);
   }

   public DBOptions setDbWriteBufferSize(long var1) {
      assert this.isOwningHandle();
      setDbWriteBufferSize(this.nativeHandle_, var1);
      return this;
   }

   public DBOptions setWriteBufferManager(WriteBufferManager var1) {
      assert this.isOwningHandle();
      setWriteBufferManager(this.nativeHandle_, var1.nativeHandle_);
      this.writeBufferManager_ = var1;
      return this;
   }

   @Override
   public WriteBufferManager writeBufferManager() {
      assert this.isOwningHandle();
      return this.writeBufferManager_;
   }

   @Override
   public long dbWriteBufferSize() {
      assert this.isOwningHandle();
      return dbWriteBufferSize(this.nativeHandle_);
   }

   public DBOptions setCompactionReadaheadSize(long var1) {
      assert this.isOwningHandle();
      setCompactionReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long compactionReadaheadSize() {
      assert this.isOwningHandle();
      return compactionReadaheadSize(this.nativeHandle_);
   }

   public DBOptions setDailyOffpeakTimeUTC(String var1) {
      assert this.isOwningHandle();
      setDailyOffpeakTimeUTC(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public String dailyOffpeakTimeUTC() {
      assert this.isOwningHandle();
      return dailyOffpeakTimeUTC(this.nativeHandle_);
   }

   public DBOptions setWritableFileMaxBufferSize(long var1) {
      assert this.isOwningHandle();
      setWritableFileMaxBufferSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writableFileMaxBufferSize() {
      assert this.isOwningHandle();
      return writableFileMaxBufferSize(this.nativeHandle_);
   }

   public DBOptions setUseAdaptiveMutex(boolean var1) {
      assert this.isOwningHandle();
      setUseAdaptiveMutex(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean useAdaptiveMutex() {
      assert this.isOwningHandle();
      return useAdaptiveMutex(this.nativeHandle_);
   }

   public DBOptions setBytesPerSync(long var1) {
      assert this.isOwningHandle();
      setBytesPerSync(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long bytesPerSync() {
      return bytesPerSync(this.nativeHandle_);
   }

   public DBOptions setWalBytesPerSync(long var1) {
      assert this.isOwningHandle();
      setWalBytesPerSync(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long walBytesPerSync() {
      assert this.isOwningHandle();
      return walBytesPerSync(this.nativeHandle_);
   }

   public DBOptions setStrictBytesPerSync(boolean var1) {
      assert this.isOwningHandle();
      setStrictBytesPerSync(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean strictBytesPerSync() {
      assert this.isOwningHandle();
      return strictBytesPerSync(this.nativeHandle_);
   }

   public DBOptions setListeners(List<AbstractEventListener> var1) {
      assert this.isOwningHandle();
      setEventListeners(this.nativeHandle_, RocksCallbackObject.toNativeHandleList(var1));
      return this;
   }

   @Override
   public List<AbstractEventListener> listeners() {
      assert this.isOwningHandle();
      return Arrays.asList(eventListeners(this.nativeHandle_));
   }

   public DBOptions setEnableThreadTracking(boolean var1) {
      assert this.isOwningHandle();
      setEnableThreadTracking(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableThreadTracking() {
      assert this.isOwningHandle();
      return enableThreadTracking(this.nativeHandle_);
   }

   public DBOptions setDelayedWriteRate(long var1) {
      assert this.isOwningHandle();
      setDelayedWriteRate(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long delayedWriteRate() {
      return delayedWriteRate(this.nativeHandle_);
   }

   public DBOptions setEnablePipelinedWrite(boolean var1) {
      assert this.isOwningHandle();
      setEnablePipelinedWrite(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enablePipelinedWrite() {
      assert this.isOwningHandle();
      return enablePipelinedWrite(this.nativeHandle_);
   }

   public DBOptions setUnorderedWrite(boolean var1) {
      setUnorderedWrite(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean unorderedWrite() {
      return unorderedWrite(this.nativeHandle_);
   }

   public DBOptions setAllowConcurrentMemtableWrite(boolean var1) {
      setAllowConcurrentMemtableWrite(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowConcurrentMemtableWrite() {
      return allowConcurrentMemtableWrite(this.nativeHandle_);
   }

   public DBOptions setEnableWriteThreadAdaptiveYield(boolean var1) {
      setEnableWriteThreadAdaptiveYield(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableWriteThreadAdaptiveYield() {
      return enableWriteThreadAdaptiveYield(this.nativeHandle_);
   }

   public DBOptions setWriteThreadMaxYieldUsec(long var1) {
      setWriteThreadMaxYieldUsec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writeThreadMaxYieldUsec() {
      return writeThreadMaxYieldUsec(this.nativeHandle_);
   }

   public DBOptions setWriteThreadSlowYieldUsec(long var1) {
      setWriteThreadSlowYieldUsec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writeThreadSlowYieldUsec() {
      return writeThreadSlowYieldUsec(this.nativeHandle_);
   }

   public DBOptions setSkipStatsUpdateOnDbOpen(boolean var1) {
      assert this.isOwningHandle();
      setSkipStatsUpdateOnDbOpen(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean skipStatsUpdateOnDbOpen() {
      assert this.isOwningHandle();
      return skipStatsUpdateOnDbOpen(this.nativeHandle_);
   }

   public DBOptions setSkipCheckingSstFileSizesOnDbOpen(boolean var1) {
      setSkipCheckingSstFileSizesOnDbOpen(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean skipCheckingSstFileSizesOnDbOpen() {
      assert this.isOwningHandle();
      return skipCheckingSstFileSizesOnDbOpen(this.nativeHandle_);
   }

   public DBOptions setWalRecoveryMode(WALRecoveryMode var1) {
      assert this.isOwningHandle();
      setWalRecoveryMode(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public WALRecoveryMode walRecoveryMode() {
      assert this.isOwningHandle();
      return WALRecoveryMode.getWALRecoveryMode(walRecoveryMode(this.nativeHandle_));
   }

   public DBOptions setAllow2pc(boolean var1) {
      assert this.isOwningHandle();
      setAllow2pc(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allow2pc() {
      assert this.isOwningHandle();
      return allow2pc(this.nativeHandle_);
   }

   public DBOptions setRowCache(Cache var1) {
      assert this.isOwningHandle();
      setRowCache(this.nativeHandle_, var1.nativeHandle_);
      this.rowCache_ = var1;
      return this;
   }

   @Override
   public Cache rowCache() {
      assert this.isOwningHandle();
      return this.rowCache_;
   }

   public DBOptions setWalFilter(AbstractWalFilter var1) {
      assert this.isOwningHandle();
      setWalFilter(this.nativeHandle_, var1.nativeHandle_);
      this.walFilter_ = var1;
      return this;
   }

   @Override
   public WalFilter walFilter() {
      assert this.isOwningHandle();
      return this.walFilter_;
   }

   public DBOptions setFailIfOptionsFileError(boolean var1) {
      assert this.isOwningHandle();
      setFailIfOptionsFileError(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean failIfOptionsFileError() {
      assert this.isOwningHandle();
      return failIfOptionsFileError(this.nativeHandle_);
   }

   public DBOptions setDumpMallocStats(boolean var1) {
      assert this.isOwningHandle();
      setDumpMallocStats(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean dumpMallocStats() {
      assert this.isOwningHandle();
      return dumpMallocStats(this.nativeHandle_);
   }

   public DBOptions setAvoidFlushDuringRecovery(boolean var1) {
      assert this.isOwningHandle();
      setAvoidFlushDuringRecovery(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean avoidFlushDuringRecovery() {
      assert this.isOwningHandle();
      return avoidFlushDuringRecovery(this.nativeHandle_);
   }

   public DBOptions setAvoidFlushDuringShutdown(boolean var1) {
      assert this.isOwningHandle();
      setAvoidFlushDuringShutdown(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean avoidFlushDuringShutdown() {
      assert this.isOwningHandle();
      return avoidFlushDuringShutdown(this.nativeHandle_);
   }

   public DBOptions setAllowIngestBehind(boolean var1) {
      assert this.isOwningHandle();
      setAllowIngestBehind(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowIngestBehind() {
      assert this.isOwningHandle();
      return allowIngestBehind(this.nativeHandle_);
   }

   public DBOptions setTwoWriteQueues(boolean var1) {
      assert this.isOwningHandle();
      setTwoWriteQueues(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean twoWriteQueues() {
      assert this.isOwningHandle();
      return twoWriteQueues(this.nativeHandle_);
   }

   public DBOptions setManualWalFlush(boolean var1) {
      assert this.isOwningHandle();
      setManualWalFlush(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean manualWalFlush() {
      assert this.isOwningHandle();
      return manualWalFlush(this.nativeHandle_);
   }

   public DBOptions setAtomicFlush(boolean var1) {
      setAtomicFlush(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean atomicFlush() {
      return atomicFlush(this.nativeHandle_);
   }

   public DBOptions setAvoidUnnecessaryBlockingIO(boolean var1) {
      setAvoidUnnecessaryBlockingIO(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean avoidUnnecessaryBlockingIO() {
      assert this.isOwningHandle();
      return avoidUnnecessaryBlockingIO(this.nativeHandle_);
   }

   public DBOptions setPersistStatsToDisk(boolean var1) {
      setPersistStatsToDisk(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean persistStatsToDisk() {
      assert this.isOwningHandle();
      return persistStatsToDisk(this.nativeHandle_);
   }

   public DBOptions setWriteDbidToManifest(boolean var1) {
      setWriteDbidToManifest(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean writeDbidToManifest() {
      assert this.isOwningHandle();
      return writeDbidToManifest(this.nativeHandle_);
   }

   public DBOptions setLogReadaheadSize(long var1) {
      setLogReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long logReadaheadSize() {
      assert this.isOwningHandle();
      return logReadaheadSize(this.nativeHandle_);
   }

   public DBOptions setBestEffortsRecovery(boolean var1) {
      setBestEffortsRecovery(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean bestEffortsRecovery() {
      assert this.isOwningHandle();
      return bestEffortsRecovery(this.nativeHandle_);
   }

   public DBOptions setMaxBgErrorResumeCount(int var1) {
      setMaxBgErrorResumeCount(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxBgerrorResumeCount() {
      assert this.isOwningHandle();
      return maxBgerrorResumeCount(this.nativeHandle_);
   }

   public DBOptions setBgerrorResumeRetryInterval(long var1) {
      setBgerrorResumeRetryInterval(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long bgerrorResumeRetryInterval() {
      assert this.isOwningHandle();
      return bgerrorResumeRetryInterval(this.nativeHandle_);
   }

   private DBOptions(long var1) {
      super(var1);
   }

   private static native long getDBOptionsFromProps(long var0, String var2);

   private static native long getDBOptionsFromProps(String var0);

   private static long newDBOptionsInstance() {
      RocksDB.loadLibrary();
      return newDBOptions();
   }

   private static native long newDBOptions();

   private static native long copyDBOptions(long var0);

   private static native long newDBOptionsFromOptions(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void optimizeForSmallDb(long var0);

   private static native void setIncreaseParallelism(long var0, int var2);

   private static native void setCreateIfMissing(long var0, boolean var2);

   private static native boolean createIfMissing(long var0);

   private static native void setCreateMissingColumnFamilies(long var0, boolean var2);

   private static native boolean createMissingColumnFamilies(long var0);

   private static native void setEnv(long var0, long var2);

   private static native void setErrorIfExists(long var0, boolean var2);

   private static native boolean errorIfExists(long var0);

   private static native void setParanoidChecks(long var0, boolean var2);

   private static native boolean paranoidChecks(long var0);

   private static native void setRateLimiter(long var0, long var2);

   private static native void setSstFileManager(long var0, long var2);

   private static native void setLogger(long var0, long var2, byte var4);

   private static native void setInfoLogLevel(long var0, byte var2);

   private static native byte infoLogLevel(long var0);

   private static native void setMaxOpenFiles(long var0, int var2);

   private static native int maxOpenFiles(long var0);

   private static native void setMaxFileOpeningThreads(long var0, int var2);

   private static native int maxFileOpeningThreads(long var0);

   private static native void setMaxTotalWalSize(long var0, long var2);

   private static native long maxTotalWalSize(long var0);

   private static native void setStatistics(long var0, long var2);

   private static native long statistics(long var0);

   private static native boolean useFsync(long var0);

   private static native void setUseFsync(long var0, boolean var2);

   private static native void setDbPaths(long var0, String[] var2, long[] var3);

   private static native long dbPathsLen(long var0);

   private static native void dbPaths(long var0, String[] var2, long[] var3);

   private static native void setDbLogDir(long var0, String var2);

   private static native String dbLogDir(long var0);

   private static native void setWalDir(long var0, String var2);

   private static native String walDir(long var0);

   private static native void setDeleteObsoleteFilesPeriodMicros(long var0, long var2);

   private static native long deleteObsoleteFilesPeriodMicros(long var0);

   private static native void setMaxBackgroundCompactions(long var0, int var2);

   private static native int maxBackgroundCompactions(long var0);

   private static native void setMaxSubcompactions(long var0, int var2);

   private static native int maxSubcompactions(long var0);

   private static native void setMaxBackgroundFlushes(long var0, int var2);

   private static native int maxBackgroundFlushes(long var0);

   private static native void setMaxBackgroundJobs(long var0, int var2);

   private static native int maxBackgroundJobs(long var0);

   private static native void setMaxLogFileSize(long var0, long var2) throws IllegalArgumentException;

   private static native long maxLogFileSize(long var0);

   private static native void setLogFileTimeToRoll(long var0, long var2) throws IllegalArgumentException;

   private static native long logFileTimeToRoll(long var0);

   private static native void setKeepLogFileNum(long var0, long var2) throws IllegalArgumentException;

   private static native long keepLogFileNum(long var0);

   private static native void setRecycleLogFileNum(long var0, long var2);

   private static native long recycleLogFileNum(long var0);

   private static native void setMaxManifestFileSize(long var0, long var2);

   private static native long maxManifestFileSize(long var0);

   private static native void setTableCacheNumshardbits(long var0, int var2);

   private static native int tableCacheNumshardbits(long var0);

   private static native void setWalTtlSeconds(long var0, long var2);

   private static native long walTtlSeconds(long var0);

   private static native void setWalSizeLimitMB(long var0, long var2);

   private static native long walSizeLimitMB(long var0);

   private static native void setMaxWriteBatchGroupSizeBytes(long var0, long var2);

   private static native long maxWriteBatchGroupSizeBytes(long var0);

   private static native void setManifestPreallocationSize(long var0, long var2) throws IllegalArgumentException;

   private static native long manifestPreallocationSize(long var0);

   private static native void setUseDirectReads(long var0, boolean var2);

   private static native boolean useDirectReads(long var0);

   private static native void setUseDirectIoForFlushAndCompaction(long var0, boolean var2);

   private static native boolean useDirectIoForFlushAndCompaction(long var0);

   private static native void setAllowFAllocate(long var0, boolean var2);

   private static native boolean allowFAllocate(long var0);

   private static native void setAllowMmapReads(long var0, boolean var2);

   private static native boolean allowMmapReads(long var0);

   private static native void setAllowMmapWrites(long var0, boolean var2);

   private static native boolean allowMmapWrites(long var0);

   private static native void setIsFdCloseOnExec(long var0, boolean var2);

   private static native boolean isFdCloseOnExec(long var0);

   private static native void setStatsDumpPeriodSec(long var0, int var2);

   private static native int statsDumpPeriodSec(long var0);

   private static native void setStatsPersistPeriodSec(long var0, int var2);

   private static native int statsPersistPeriodSec(long var0);

   private static native void setStatsHistoryBufferSize(long var0, long var2);

   private static native long statsHistoryBufferSize(long var0);

   private static native void setAdviseRandomOnOpen(long var0, boolean var2);

   private static native boolean adviseRandomOnOpen(long var0);

   private static native void setDbWriteBufferSize(long var0, long var2);

   private static native void setWriteBufferManager(long var0, long var2);

   private static native long dbWriteBufferSize(long var0);

   private static native void setCompactionReadaheadSize(long var0, long var2);

   private static native long compactionReadaheadSize(long var0);

   private static native void setDailyOffpeakTimeUTC(long var0, String var2);

   private static native String dailyOffpeakTimeUTC(long var0);

   private static native void setWritableFileMaxBufferSize(long var0, long var2);

   private static native long writableFileMaxBufferSize(long var0);

   private static native void setUseAdaptiveMutex(long var0, boolean var2);

   private static native boolean useAdaptiveMutex(long var0);

   private static native void setBytesPerSync(long var0, long var2);

   private static native long bytesPerSync(long var0);

   private static native void setWalBytesPerSync(long var0, long var2);

   private static native long walBytesPerSync(long var0);

   private static native void setStrictBytesPerSync(long var0, boolean var2);

   private static native boolean strictBytesPerSync(long var0);

   private static native void setEventListeners(long var0, long[] var2);

   private static native AbstractEventListener[] eventListeners(long var0);

   private static native void setEnableThreadTracking(long var0, boolean var2);

   private static native boolean enableThreadTracking(long var0);

   private static native void setDelayedWriteRate(long var0, long var2);

   private static native long delayedWriteRate(long var0);

   private static native void setEnablePipelinedWrite(long var0, boolean var2);

   private static native boolean enablePipelinedWrite(long var0);

   private static native void setUnorderedWrite(long var0, boolean var2);

   private static native boolean unorderedWrite(long var0);

   private static native void setAllowConcurrentMemtableWrite(long var0, boolean var2);

   private static native boolean allowConcurrentMemtableWrite(long var0);

   private static native void setEnableWriteThreadAdaptiveYield(long var0, boolean var2);

   private static native boolean enableWriteThreadAdaptiveYield(long var0);

   private static native void setWriteThreadMaxYieldUsec(long var0, long var2);

   private static native long writeThreadMaxYieldUsec(long var0);

   private static native void setWriteThreadSlowYieldUsec(long var0, long var2);

   private static native long writeThreadSlowYieldUsec(long var0);

   private static native void setSkipStatsUpdateOnDbOpen(long var0, boolean var2);

   private static native boolean skipStatsUpdateOnDbOpen(long var0);

   private static native void setSkipCheckingSstFileSizesOnDbOpen(long var0, boolean var2);

   private static native boolean skipCheckingSstFileSizesOnDbOpen(long var0);

   private static native void setWalRecoveryMode(long var0, byte var2);

   private static native byte walRecoveryMode(long var0);

   private static native void setAllow2pc(long var0, boolean var2);

   private static native boolean allow2pc(long var0);

   private static native void setRowCache(long var0, long var2);

   private static native void setWalFilter(long var0, long var2);

   private static native void setFailIfOptionsFileError(long var0, boolean var2);

   private static native boolean failIfOptionsFileError(long var0);

   private static native void setDumpMallocStats(long var0, boolean var2);

   private static native boolean dumpMallocStats(long var0);

   private static native void setAvoidFlushDuringRecovery(long var0, boolean var2);

   private static native boolean avoidFlushDuringRecovery(long var0);

   private static native void setAvoidFlushDuringShutdown(long var0, boolean var2);

   private static native boolean avoidFlushDuringShutdown(long var0);

   private static native void setAllowIngestBehind(long var0, boolean var2);

   private static native boolean allowIngestBehind(long var0);

   private static native void setTwoWriteQueues(long var0, boolean var2);

   private static native boolean twoWriteQueues(long var0);

   private static native void setManualWalFlush(long var0, boolean var2);

   private static native boolean manualWalFlush(long var0);

   private static native void setAtomicFlush(long var0, boolean var2);

   private static native boolean atomicFlush(long var0);

   private static native void setAvoidUnnecessaryBlockingIO(long var0, boolean var2);

   private static native boolean avoidUnnecessaryBlockingIO(long var0);

   private static native void setPersistStatsToDisk(long var0, boolean var2);

   private static native boolean persistStatsToDisk(long var0);

   private static native void setWriteDbidToManifest(long var0, boolean var2);

   private static native boolean writeDbidToManifest(long var0);

   private static native void setLogReadaheadSize(long var0, long var2);

   private static native long logReadaheadSize(long var0);

   private static native void setBestEffortsRecovery(long var0, boolean var2);

   private static native boolean bestEffortsRecovery(long var0);

   private static native void setMaxBgErrorResumeCount(long var0, int var2);

   private static native int maxBgerrorResumeCount(long var0);

   private static native void setBgerrorResumeRetryInterval(long var0, long var2);

   private static native long bgerrorResumeRetryInterval(long var0);
}
