package org.rocksdb;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Options
   extends RocksObject
   implements DBOptionsInterface<Options>,
   MutableDBOptionsInterface<Options>,
   ColumnFamilyOptionsInterface<Options>,
   MutableColumnFamilyOptionsInterface<Options> {
   private Env env_;
   private MemTableConfig memTableConfig_;
   private TableFormatConfig tableFormatConfig_;
   private RateLimiter rateLimiter_;
   private AbstractComparator comparator_;
   private AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter_;
   private AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> compactionFilterFactory_;
   private CompactionOptionsUniversal compactionOptionsUniversal_;
   private CompactionOptionsFIFO compactionOptionsFIFO_;
   private CompressionOptions bottommostCompressionOptions_;
   private CompressionOptions compressionOptions_;
   private Cache rowCache_;
   private WalFilter walFilter_;
   private WriteBufferManager writeBufferManager_;
   private SstPartitionerFactory sstPartitionerFactory_;
   private ConcurrentTaskLimiter compactionThreadLimiter_;

   public static String getOptionStringFromProps(Properties var0) {
      if (var0 != null && var0.size() != 0) {
         StringBuilder var1 = new StringBuilder();

         for (String var3 : var0.stringPropertyNames()) {
            var1.append(var3);
            var1.append("=");
            var1.append(var0.getProperty(var3));
            var1.append(";");
         }

         return var1.toString();
      } else {
         throw new IllegalArgumentException("Properties value must contain at least one value.");
      }
   }

   public Options() {
      super(newOptionsInstance());
      this.env_ = Env.getDefault();
   }

   public Options(DBOptions var1, ColumnFamilyOptions var2) {
      super(newOptions(var1.nativeHandle_, var2.nativeHandle_));
      this.env_ = var1.getEnv() != null ? var1.getEnv() : Env.getDefault();
   }

   public Options(Options var1) {
      super(copyOptions(var1.nativeHandle_));
      this.env_ = var1.env_;
      this.memTableConfig_ = var1.memTableConfig_;
      this.tableFormatConfig_ = var1.tableFormatConfig_;
      this.rateLimiter_ = var1.rateLimiter_;
      this.comparator_ = var1.comparator_;
      this.compactionFilter_ = var1.compactionFilter_;
      this.compactionFilterFactory_ = var1.compactionFilterFactory_;
      this.compactionOptionsUniversal_ = var1.compactionOptionsUniversal_;
      this.compactionOptionsFIFO_ = var1.compactionOptionsFIFO_;
      this.compressionOptions_ = var1.compressionOptions_;
      this.rowCache_ = var1.rowCache_;
      this.writeBufferManager_ = var1.writeBufferManager_;
      this.compactionThreadLimiter_ = var1.compactionThreadLimiter_;
      this.bottommostCompressionOptions_ = var1.bottommostCompressionOptions_;
      this.walFilter_ = var1.walFilter_;
      this.sstPartitionerFactory_ = var1.sstPartitionerFactory_;
   }

   public Options setIncreaseParallelism(int var1) {
      assert this.isOwningHandle();
      setIncreaseParallelism(this.nativeHandle_, var1);
      return this;
   }

   public Options setCreateIfMissing(boolean var1) {
      assert this.isOwningHandle();
      setCreateIfMissing(this.nativeHandle_, var1);
      return this;
   }

   public Options setCreateMissingColumnFamilies(boolean var1) {
      assert this.isOwningHandle();
      setCreateMissingColumnFamilies(this.nativeHandle_, var1);
      return this;
   }

   public Options setEnv(Env var1) {
      assert this.isOwningHandle();
      setEnv(this.nativeHandle_, var1.nativeHandle_);
      this.env_ = var1;
      return this;
   }

   @Override
   public Env getEnv() {
      return this.env_;
   }

   public Options prepareForBulkLoad() {
      prepareForBulkLoad(this.nativeHandle_);
      return this;
   }

   @Override
   public boolean createIfMissing() {
      assert this.isOwningHandle();
      return createIfMissing(this.nativeHandle_);
   }

   @Override
   public boolean createMissingColumnFamilies() {
      assert this.isOwningHandle();
      return createMissingColumnFamilies(this.nativeHandle_);
   }

   public Options oldDefaults(int var1, int var2) {
      oldDefaults(this.nativeHandle_, var1, var2);
      return this;
   }

   public Options optimizeForSmallDb() {
      optimizeForSmallDb(this.nativeHandle_);
      return this;
   }

   public Options optimizeForSmallDb(Cache var1) {
      optimizeForSmallDb(this.nativeHandle_, var1.getNativeHandle());
      return this;
   }

   public Options optimizeForPointLookup(long var1) {
      optimizeForPointLookup(this.nativeHandle_, var1);
      return this;
   }

   public Options optimizeLevelStyleCompaction() {
      optimizeLevelStyleCompaction(this.nativeHandle_, 536870912L);
      return this;
   }

   public Options optimizeLevelStyleCompaction(long var1) {
      optimizeLevelStyleCompaction(this.nativeHandle_, var1);
      return this;
   }

   public Options optimizeUniversalStyleCompaction() {
      optimizeUniversalStyleCompaction(this.nativeHandle_, 536870912L);
      return this;
   }

   public Options optimizeUniversalStyleCompaction(long var1) {
      optimizeUniversalStyleCompaction(this.nativeHandle_, var1);
      return this;
   }

   public Options setComparator(BuiltinComparator var1) {
      assert this.isOwningHandle();
      setComparatorHandle(this.nativeHandle_, var1.ordinal());
      return this;
   }

   public Options setComparator(AbstractComparator var1) {
      assert this.isOwningHandle();
      setComparatorHandle(this.nativeHandle_, var1.nativeHandle_, var1.getComparatorType().getValue());
      this.comparator_ = var1;
      return this;
   }

   public Options setMergeOperatorName(String var1) {
      assert this.isOwningHandle();
      if (var1 == null) {
         throw new IllegalArgumentException("Merge operator name must not be null.");
      }

      setMergeOperatorName(this.nativeHandle_, var1);
      return this;
   }

   public Options setMergeOperator(MergeOperator var1) {
      setMergeOperator(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public Options setCompactionFilter(AbstractCompactionFilter<? extends AbstractSlice<?>> var1) {
      setCompactionFilterHandle(this.nativeHandle_, var1.nativeHandle_);
      this.compactionFilter_ = var1;
      return this;
   }

   @Override
   public AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter() {
      assert this.isOwningHandle();
      return this.compactionFilter_;
   }

   public Options setCompactionFilterFactory(AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> var1) {
      assert this.isOwningHandle();
      setCompactionFilterFactoryHandle(this.nativeHandle_, var1.nativeHandle_);
      this.compactionFilterFactory_ = var1;
      return this;
   }

   @Override
   public AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> compactionFilterFactory() {
      assert this.isOwningHandle();
      return this.compactionFilterFactory_;
   }

   public Options setWriteBufferSize(long var1) {
      assert this.isOwningHandle();
      setWriteBufferSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writeBufferSize() {
      assert this.isOwningHandle();
      return writeBufferSize(this.nativeHandle_);
   }

   public Options setMaxWriteBufferNumber(int var1) {
      assert this.isOwningHandle();
      setMaxWriteBufferNumber(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxWriteBufferNumber() {
      assert this.isOwningHandle();
      return maxWriteBufferNumber(this.nativeHandle_);
   }

   @Override
   public boolean errorIfExists() {
      assert this.isOwningHandle();
      return errorIfExists(this.nativeHandle_);
   }

   public Options setErrorIfExists(boolean var1) {
      assert this.isOwningHandle();
      setErrorIfExists(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean paranoidChecks() {
      assert this.isOwningHandle();
      return paranoidChecks(this.nativeHandle_);
   }

   public Options setParanoidChecks(boolean var1) {
      assert this.isOwningHandle();
      setParanoidChecks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxOpenFiles() {
      assert this.isOwningHandle();
      return maxOpenFiles(this.nativeHandle_);
   }

   public Options setMaxFileOpeningThreads(int var1) {
      assert this.isOwningHandle();
      setMaxFileOpeningThreads(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxFileOpeningThreads() {
      assert this.isOwningHandle();
      return maxFileOpeningThreads(this.nativeHandle_);
   }

   public Options setMaxTotalWalSize(long var1) {
      assert this.isOwningHandle();
      setMaxTotalWalSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxTotalWalSize() {
      assert this.isOwningHandle();
      return maxTotalWalSize(this.nativeHandle_);
   }

   public Options setMaxOpenFiles(int var1) {
      assert this.isOwningHandle();
      setMaxOpenFiles(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean useFsync() {
      assert this.isOwningHandle();
      return useFsync(this.nativeHandle_);
   }

   public Options setUseFsync(boolean var1) {
      assert this.isOwningHandle();
      setUseFsync(this.nativeHandle_, var1);
      return this;
   }

   public Options setDbPaths(Collection<DbPath> var1) {
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

   @Override
   public String dbLogDir() {
      assert this.isOwningHandle();
      return dbLogDir(this.nativeHandle_);
   }

   public Options setDbLogDir(String var1) {
      assert this.isOwningHandle();
      setDbLogDir(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public String walDir() {
      assert this.isOwningHandle();
      return walDir(this.nativeHandle_);
   }

   public Options setWalDir(String var1) {
      assert this.isOwningHandle();
      setWalDir(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long deleteObsoleteFilesPeriodMicros() {
      assert this.isOwningHandle();
      return deleteObsoleteFilesPeriodMicros(this.nativeHandle_);
   }

   public Options setDeleteObsoleteFilesPeriodMicros(long var1) {
      assert this.isOwningHandle();
      setDeleteObsoleteFilesPeriodMicros(this.nativeHandle_, var1);
      return this;
   }

   @Deprecated
   @Override
   public int maxBackgroundCompactions() {
      assert this.isOwningHandle();
      return maxBackgroundCompactions(this.nativeHandle_);
   }

   public Options setStatistics(Statistics var1) {
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

   @Deprecated
   public Options setMaxBackgroundCompactions(int var1) {
      assert this.isOwningHandle();
      setMaxBackgroundCompactions(this.nativeHandle_, var1);
      return this;
   }

   public Options setMaxSubcompactions(int var1) {
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
   @Override
   public int maxBackgroundFlushes() {
      assert this.isOwningHandle();
      return maxBackgroundFlushes(this.nativeHandle_);
   }

   @Deprecated
   public Options setMaxBackgroundFlushes(int var1) {
      assert this.isOwningHandle();
      setMaxBackgroundFlushes(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxBackgroundJobs() {
      assert this.isOwningHandle();
      return maxBackgroundJobs(this.nativeHandle_);
   }

   public Options setMaxBackgroundJobs(int var1) {
      assert this.isOwningHandle();
      setMaxBackgroundJobs(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxLogFileSize() {
      assert this.isOwningHandle();
      return maxLogFileSize(this.nativeHandle_);
   }

   public Options setMaxLogFileSize(long var1) {
      assert this.isOwningHandle();
      setMaxLogFileSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long logFileTimeToRoll() {
      assert this.isOwningHandle();
      return logFileTimeToRoll(this.nativeHandle_);
   }

   public Options setLogFileTimeToRoll(long var1) {
      assert this.isOwningHandle();
      setLogFileTimeToRoll(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long keepLogFileNum() {
      assert this.isOwningHandle();
      return keepLogFileNum(this.nativeHandle_);
   }

   public Options setKeepLogFileNum(long var1) {
      assert this.isOwningHandle();
      setKeepLogFileNum(this.nativeHandle_, var1);
      return this;
   }

   public Options setRecycleLogFileNum(long var1) {
      assert this.isOwningHandle();
      setRecycleLogFileNum(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long recycleLogFileNum() {
      assert this.isOwningHandle();
      return recycleLogFileNum(this.nativeHandle_);
   }

   @Override
   public long maxManifestFileSize() {
      assert this.isOwningHandle();
      return maxManifestFileSize(this.nativeHandle_);
   }

   public Options setMaxManifestFileSize(long var1) {
      assert this.isOwningHandle();
      setMaxManifestFileSize(this.nativeHandle_, var1);
      return this;
   }

   public Options setMaxTableFilesSizeFIFO(long var1) {
      assert var1 > 0L;
      assert this.isOwningHandle();
      setMaxTableFilesSizeFIFO(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxTableFilesSizeFIFO() {
      return maxTableFilesSizeFIFO(this.nativeHandle_);
   }

   @Override
   public int tableCacheNumshardbits() {
      assert this.isOwningHandle();
      return tableCacheNumshardbits(this.nativeHandle_);
   }

   public Options setTableCacheNumshardbits(int var1) {
      assert this.isOwningHandle();
      setTableCacheNumshardbits(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long walTtlSeconds() {
      assert this.isOwningHandle();
      return walTtlSeconds(this.nativeHandle_);
   }

   public Options setWalTtlSeconds(long var1) {
      assert this.isOwningHandle();
      setWalTtlSeconds(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long walSizeLimitMB() {
      assert this.isOwningHandle();
      return walSizeLimitMB(this.nativeHandle_);
   }

   public Options setMaxWriteBatchGroupSizeBytes(long var1) {
      setMaxWriteBatchGroupSizeBytes(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxWriteBatchGroupSizeBytes() {
      assert this.isOwningHandle();
      return maxWriteBatchGroupSizeBytes(this.nativeHandle_);
   }

   public Options setWalSizeLimitMB(long var1) {
      assert this.isOwningHandle();
      setWalSizeLimitMB(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long manifestPreallocationSize() {
      assert this.isOwningHandle();
      return manifestPreallocationSize(this.nativeHandle_);
   }

   public Options setManifestPreallocationSize(long var1) {
      assert this.isOwningHandle();
      setManifestPreallocationSize(this.nativeHandle_, var1);
      return this;
   }

   public Options setUseDirectReads(boolean var1) {
      assert this.isOwningHandle();
      setUseDirectReads(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean useDirectReads() {
      assert this.isOwningHandle();
      return useDirectReads(this.nativeHandle_);
   }

   public Options setUseDirectIoForFlushAndCompaction(boolean var1) {
      assert this.isOwningHandle();
      setUseDirectIoForFlushAndCompaction(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean useDirectIoForFlushAndCompaction() {
      assert this.isOwningHandle();
      return useDirectIoForFlushAndCompaction(this.nativeHandle_);
   }

   public Options setAllowFAllocate(boolean var1) {
      assert this.isOwningHandle();
      setAllowFAllocate(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowFAllocate() {
      assert this.isOwningHandle();
      return allowFAllocate(this.nativeHandle_);
   }

   @Override
   public boolean allowMmapReads() {
      assert this.isOwningHandle();
      return allowMmapReads(this.nativeHandle_);
   }

   public Options setAllowMmapReads(boolean var1) {
      assert this.isOwningHandle();
      setAllowMmapReads(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowMmapWrites() {
      assert this.isOwningHandle();
      return allowMmapWrites(this.nativeHandle_);
   }

   public Options setAllowMmapWrites(boolean var1) {
      assert this.isOwningHandle();
      setAllowMmapWrites(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean isFdCloseOnExec() {
      assert this.isOwningHandle();
      return isFdCloseOnExec(this.nativeHandle_);
   }

   public Options setIsFdCloseOnExec(boolean var1) {
      assert this.isOwningHandle();
      setIsFdCloseOnExec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int statsDumpPeriodSec() {
      assert this.isOwningHandle();
      return statsDumpPeriodSec(this.nativeHandle_);
   }

   public Options setStatsDumpPeriodSec(int var1) {
      assert this.isOwningHandle();
      setStatsDumpPeriodSec(this.nativeHandle_, var1);
      return this;
   }

   public Options setStatsPersistPeriodSec(int var1) {
      assert this.isOwningHandle();
      setStatsPersistPeriodSec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int statsPersistPeriodSec() {
      assert this.isOwningHandle();
      return statsPersistPeriodSec(this.nativeHandle_);
   }

   public Options setStatsHistoryBufferSize(long var1) {
      assert this.isOwningHandle();
      setStatsHistoryBufferSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long statsHistoryBufferSize() {
      assert this.isOwningHandle();
      return statsHistoryBufferSize(this.nativeHandle_);
   }

   @Override
   public boolean adviseRandomOnOpen() {
      return adviseRandomOnOpen(this.nativeHandle_);
   }

   public Options setAdviseRandomOnOpen(boolean var1) {
      assert this.isOwningHandle();
      setAdviseRandomOnOpen(this.nativeHandle_, var1);
      return this;
   }

   public Options setDbWriteBufferSize(long var1) {
      assert this.isOwningHandle();
      setDbWriteBufferSize(this.nativeHandle_, var1);
      return this;
   }

   public Options setWriteBufferManager(WriteBufferManager var1) {
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

   public Options setCompactionReadaheadSize(long var1) {
      assert this.isOwningHandle();
      setCompactionReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long compactionReadaheadSize() {
      assert this.isOwningHandle();
      return compactionReadaheadSize(this.nativeHandle_);
   }

   public Options setDailyOffpeakTimeUTC(String var1) {
      assert this.isOwningHandle();
      setDailyOffpeakTimeUTC(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public String dailyOffpeakTimeUTC() {
      assert this.isOwningHandle();
      return dailyOffpeakTimeUTC(this.nativeHandle_);
   }

   public Options setWritableFileMaxBufferSize(long var1) {
      assert this.isOwningHandle();
      setWritableFileMaxBufferSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writableFileMaxBufferSize() {
      assert this.isOwningHandle();
      return writableFileMaxBufferSize(this.nativeHandle_);
   }

   @Override
   public boolean useAdaptiveMutex() {
      assert this.isOwningHandle();
      return useAdaptiveMutex(this.nativeHandle_);
   }

   public Options setUseAdaptiveMutex(boolean var1) {
      assert this.isOwningHandle();
      setUseAdaptiveMutex(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long bytesPerSync() {
      return bytesPerSync(this.nativeHandle_);
   }

   public Options setBytesPerSync(long var1) {
      assert this.isOwningHandle();
      setBytesPerSync(this.nativeHandle_, var1);
      return this;
   }

   public Options setWalBytesPerSync(long var1) {
      assert this.isOwningHandle();
      setWalBytesPerSync(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long walBytesPerSync() {
      assert this.isOwningHandle();
      return walBytesPerSync(this.nativeHandle_);
   }

   public Options setStrictBytesPerSync(boolean var1) {
      assert this.isOwningHandle();
      setStrictBytesPerSync(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean strictBytesPerSync() {
      assert this.isOwningHandle();
      return strictBytesPerSync(this.nativeHandle_);
   }

   public Options setListeners(List<AbstractEventListener> var1) {
      assert this.isOwningHandle();
      setEventListeners(this.nativeHandle_, RocksCallbackObject.toNativeHandleList(var1));
      return this;
   }

   @Override
   public List<AbstractEventListener> listeners() {
      assert this.isOwningHandle();
      return Arrays.asList(eventListeners(this.nativeHandle_));
   }

   public Options setEnableThreadTracking(boolean var1) {
      assert this.isOwningHandle();
      setEnableThreadTracking(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableThreadTracking() {
      assert this.isOwningHandle();
      return enableThreadTracking(this.nativeHandle_);
   }

   public Options setDelayedWriteRate(long var1) {
      assert this.isOwningHandle();
      setDelayedWriteRate(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long delayedWriteRate() {
      return delayedWriteRate(this.nativeHandle_);
   }

   public Options setEnablePipelinedWrite(boolean var1) {
      setEnablePipelinedWrite(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enablePipelinedWrite() {
      return enablePipelinedWrite(this.nativeHandle_);
   }

   public Options setUnorderedWrite(boolean var1) {
      setUnorderedWrite(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean unorderedWrite() {
      return unorderedWrite(this.nativeHandle_);
   }

   public Options setAllowConcurrentMemtableWrite(boolean var1) {
      setAllowConcurrentMemtableWrite(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowConcurrentMemtableWrite() {
      return allowConcurrentMemtableWrite(this.nativeHandle_);
   }

   public Options setEnableWriteThreadAdaptiveYield(boolean var1) {
      setEnableWriteThreadAdaptiveYield(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableWriteThreadAdaptiveYield() {
      return enableWriteThreadAdaptiveYield(this.nativeHandle_);
   }

   public Options setWriteThreadMaxYieldUsec(long var1) {
      setWriteThreadMaxYieldUsec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writeThreadMaxYieldUsec() {
      return writeThreadMaxYieldUsec(this.nativeHandle_);
   }

   public Options setWriteThreadSlowYieldUsec(long var1) {
      setWriteThreadSlowYieldUsec(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writeThreadSlowYieldUsec() {
      return writeThreadSlowYieldUsec(this.nativeHandle_);
   }

   public Options setSkipStatsUpdateOnDbOpen(boolean var1) {
      assert this.isOwningHandle();
      setSkipStatsUpdateOnDbOpen(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean skipStatsUpdateOnDbOpen() {
      assert this.isOwningHandle();
      return skipStatsUpdateOnDbOpen(this.nativeHandle_);
   }

   public Options setSkipCheckingSstFileSizesOnDbOpen(boolean var1) {
      setSkipCheckingSstFileSizesOnDbOpen(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean skipCheckingSstFileSizesOnDbOpen() {
      assert this.isOwningHandle();
      return skipCheckingSstFileSizesOnDbOpen(this.nativeHandle_);
   }

   public Options setWalRecoveryMode(WALRecoveryMode var1) {
      assert this.isOwningHandle();
      setWalRecoveryMode(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public WALRecoveryMode walRecoveryMode() {
      assert this.isOwningHandle();
      return WALRecoveryMode.getWALRecoveryMode(walRecoveryMode(this.nativeHandle_));
   }

   public Options setAllow2pc(boolean var1) {
      assert this.isOwningHandle();
      setAllow2pc(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allow2pc() {
      assert this.isOwningHandle();
      return allow2pc(this.nativeHandle_);
   }

   public Options setRowCache(Cache var1) {
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

   public Options setWalFilter(AbstractWalFilter var1) {
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

   public Options setFailIfOptionsFileError(boolean var1) {
      assert this.isOwningHandle();
      setFailIfOptionsFileError(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean failIfOptionsFileError() {
      assert this.isOwningHandle();
      return failIfOptionsFileError(this.nativeHandle_);
   }

   public Options setDumpMallocStats(boolean var1) {
      assert this.isOwningHandle();
      setDumpMallocStats(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean dumpMallocStats() {
      assert this.isOwningHandle();
      return dumpMallocStats(this.nativeHandle_);
   }

   public Options setAvoidFlushDuringRecovery(boolean var1) {
      assert this.isOwningHandle();
      setAvoidFlushDuringRecovery(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean avoidFlushDuringRecovery() {
      assert this.isOwningHandle();
      return avoidFlushDuringRecovery(this.nativeHandle_);
   }

   public Options setAvoidFlushDuringShutdown(boolean var1) {
      assert this.isOwningHandle();
      setAvoidFlushDuringShutdown(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean avoidFlushDuringShutdown() {
      assert this.isOwningHandle();
      return avoidFlushDuringShutdown(this.nativeHandle_);
   }

   public Options setAllowIngestBehind(boolean var1) {
      assert this.isOwningHandle();
      setAllowIngestBehind(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean allowIngestBehind() {
      assert this.isOwningHandle();
      return allowIngestBehind(this.nativeHandle_);
   }

   public Options setTwoWriteQueues(boolean var1) {
      assert this.isOwningHandle();
      setTwoWriteQueues(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean twoWriteQueues() {
      assert this.isOwningHandle();
      return twoWriteQueues(this.nativeHandle_);
   }

   public Options setManualWalFlush(boolean var1) {
      assert this.isOwningHandle();
      setManualWalFlush(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean manualWalFlush() {
      assert this.isOwningHandle();
      return manualWalFlush(this.nativeHandle_);
   }

   @Override
   public MemTableConfig memTableConfig() {
      return this.memTableConfig_;
   }

   public Options setMemTableConfig(MemTableConfig var1) {
      this.memTableConfig_ = var1;
      setMemTableFactory(this.nativeHandle_, var1.newMemTableFactoryHandle());
      return this;
   }

   public Options setRateLimiter(RateLimiter var1) {
      assert this.isOwningHandle();
      this.rateLimiter_ = var1;
      setRateLimiter(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public Options setSstFileManager(SstFileManager var1) {
      assert this.isOwningHandle();
      setSstFileManager(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public Options setLogger(LoggerInterface var1) {
      assert this.isOwningHandle();
      setLogger(this.nativeHandle_, var1.getNativeHandle(), var1.getLoggerType().getValue());
      return this;
   }

   public Options setInfoLogLevel(InfoLogLevel var1) {
      assert this.isOwningHandle();
      setInfoLogLevel(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public InfoLogLevel infoLogLevel() {
      assert this.isOwningHandle();
      return InfoLogLevel.getInfoLogLevel(infoLogLevel(this.nativeHandle_));
   }

   @Override
   public String memTableFactoryName() {
      assert this.isOwningHandle();
      return memTableFactoryName(this.nativeHandle_);
   }

   @Override
   public TableFormatConfig tableFormatConfig() {
      return this.tableFormatConfig_;
   }

   public Options setTableFormatConfig(TableFormatConfig var1) {
      this.tableFormatConfig_ = var1;
      setTableFactory(this.nativeHandle_, var1.newTableFactoryHandle());
      return this;
   }

   @Override
   public String tableFactoryName() {
      assert this.isOwningHandle();
      return tableFactoryName(this.nativeHandle_);
   }

   public Options setCfPaths(Collection<DbPath> var1) {
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

      setCfPaths(this.nativeHandle_, var3, var4);
      return this;
   }

   @Override
   public List<DbPath> cfPaths() {
      int var1 = (int)cfPathsLen(this.nativeHandle_);
      if (var1 == 0) {
         return Collections.emptyList();
      }

      String[] var2 = new String[var1];
      long[] var3 = new long[var1];
      cfPaths(this.nativeHandle_, var2, var3);
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var1; var5++) {
         var4.add(new DbPath(Paths.get(var2[var5]), var3[var5]));
      }

      return var4;
   }

   public Options useFixedLengthPrefixExtractor(int var1) {
      assert this.isOwningHandle();
      useFixedLengthPrefixExtractor(this.nativeHandle_, var1);
      return this;
   }

   public Options useCappedPrefixExtractor(int var1) {
      assert this.isOwningHandle();
      useCappedPrefixExtractor(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public CompressionType compressionType() {
      return CompressionType.getCompressionType(compressionType(this.nativeHandle_));
   }

   public Options setCompressionPerLevel(List<CompressionType> var1) {
      byte[] var2 = new byte[var1.size()];

      for (int var3 = 0; var3 < var1.size(); var3++) {
         var2[var3] = ((CompressionType)var1.get(var3)).getValue();
      }

      setCompressionPerLevel(this.nativeHandle_, var2);
      return this;
   }

   @Override
   public List<CompressionType> compressionPerLevel() {
      byte[] var1 = compressionPerLevel(this.nativeHandle_);
      ArrayList var2 = new ArrayList();

      for (byte var6 : var1) {
         var2.add(CompressionType.getCompressionType(var6));
      }

      return var2;
   }

   public Options setCompressionType(CompressionType var1) {
      setCompressionType(this.nativeHandle_, var1.getValue());
      return this;
   }

   public Options setBottommostCompressionType(CompressionType var1) {
      setBottommostCompressionType(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompressionType bottommostCompressionType() {
      return CompressionType.getCompressionType(bottommostCompressionType(this.nativeHandle_));
   }

   public Options setBottommostCompressionOptions(CompressionOptions var1) {
      setBottommostCompressionOptions(this.nativeHandle_, var1.nativeHandle_);
      this.bottommostCompressionOptions_ = var1;
      return this;
   }

   @Override
   public CompressionOptions bottommostCompressionOptions() {
      return this.bottommostCompressionOptions_;
   }

   public Options setCompressionOptions(CompressionOptions var1) {
      setCompressionOptions(this.nativeHandle_, var1.nativeHandle_);
      this.compressionOptions_ = var1;
      return this;
   }

   @Override
   public CompressionOptions compressionOptions() {
      return this.compressionOptions_;
   }

   @Override
   public CompactionStyle compactionStyle() {
      return CompactionStyle.fromValue(compactionStyle(this.nativeHandle_));
   }

   public Options setCompactionStyle(CompactionStyle var1) {
      setCompactionStyle(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public int numLevels() {
      return numLevels(this.nativeHandle_);
   }

   public Options setNumLevels(int var1) {
      setNumLevels(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int levelZeroFileNumCompactionTrigger() {
      return levelZeroFileNumCompactionTrigger(this.nativeHandle_);
   }

   public Options setLevelZeroFileNumCompactionTrigger(int var1) {
      setLevelZeroFileNumCompactionTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int levelZeroSlowdownWritesTrigger() {
      return levelZeroSlowdownWritesTrigger(this.nativeHandle_);
   }

   public Options setLevelZeroSlowdownWritesTrigger(int var1) {
      setLevelZeroSlowdownWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int levelZeroStopWritesTrigger() {
      return levelZeroStopWritesTrigger(this.nativeHandle_);
   }

   public Options setLevelZeroStopWritesTrigger(int var1) {
      setLevelZeroStopWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long targetFileSizeBase() {
      return targetFileSizeBase(this.nativeHandle_);
   }

   public Options setTargetFileSizeBase(long var1) {
      setTargetFileSizeBase(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int targetFileSizeMultiplier() {
      return targetFileSizeMultiplier(this.nativeHandle_);
   }

   public Options setTargetFileSizeMultiplier(int var1) {
      setTargetFileSizeMultiplier(this.nativeHandle_, var1);
      return this;
   }

   public Options setMaxBytesForLevelBase(long var1) {
      setMaxBytesForLevelBase(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxBytesForLevelBase() {
      return maxBytesForLevelBase(this.nativeHandle_);
   }

   public Options setLevelCompactionDynamicLevelBytes(boolean var1) {
      setLevelCompactionDynamicLevelBytes(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean levelCompactionDynamicLevelBytes() {
      return levelCompactionDynamicLevelBytes(this.nativeHandle_);
   }

   @Override
   public double maxBytesForLevelMultiplier() {
      return maxBytesForLevelMultiplier(this.nativeHandle_);
   }

   public Options setMaxBytesForLevelMultiplier(double var1) {
      setMaxBytesForLevelMultiplier(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxCompactionBytes() {
      return maxCompactionBytes(this.nativeHandle_);
   }

   public Options setMaxCompactionBytes(long var1) {
      setMaxCompactionBytes(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long arenaBlockSize() {
      return arenaBlockSize(this.nativeHandle_);
   }

   public Options setArenaBlockSize(long var1) {
      setArenaBlockSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean disableAutoCompactions() {
      return disableAutoCompactions(this.nativeHandle_);
   }

   public Options setDisableAutoCompactions(boolean var1) {
      setDisableAutoCompactions(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxSequentialSkipInIterations() {
      return maxSequentialSkipInIterations(this.nativeHandle_);
   }

   public Options setMaxSequentialSkipInIterations(long var1) {
      setMaxSequentialSkipInIterations(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean inplaceUpdateSupport() {
      return inplaceUpdateSupport(this.nativeHandle_);
   }

   public Options setInplaceUpdateSupport(boolean var1) {
      setInplaceUpdateSupport(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long inplaceUpdateNumLocks() {
      return inplaceUpdateNumLocks(this.nativeHandle_);
   }

   public Options setInplaceUpdateNumLocks(long var1) {
      setInplaceUpdateNumLocks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double memtablePrefixBloomSizeRatio() {
      return memtablePrefixBloomSizeRatio(this.nativeHandle_);
   }

   public Options setMemtablePrefixBloomSizeRatio(double var1) {
      setMemtablePrefixBloomSizeRatio(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double experimentalMempurgeThreshold() {
      return experimentalMempurgeThreshold(this.nativeHandle_);
   }

   public Options setExperimentalMempurgeThreshold(double var1) {
      setExperimentalMempurgeThreshold(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean memtableWholeKeyFiltering() {
      return memtableWholeKeyFiltering(this.nativeHandle_);
   }

   public Options setMemtableWholeKeyFiltering(boolean var1) {
      setMemtableWholeKeyFiltering(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int bloomLocality() {
      return bloomLocality(this.nativeHandle_);
   }

   public Options setBloomLocality(int var1) {
      setBloomLocality(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxSuccessiveMerges() {
      return maxSuccessiveMerges(this.nativeHandle_);
   }

   public Options setMaxSuccessiveMerges(long var1) {
      setMaxSuccessiveMerges(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int minWriteBufferNumberToMerge() {
      return minWriteBufferNumberToMerge(this.nativeHandle_);
   }

   public Options setMinWriteBufferNumberToMerge(int var1) {
      setMinWriteBufferNumberToMerge(this.nativeHandle_, var1);
      return this;
   }

   public Options setOptimizeFiltersForHits(boolean var1) {
      setOptimizeFiltersForHits(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean optimizeFiltersForHits() {
      return optimizeFiltersForHits(this.nativeHandle_);
   }

   public Options setMemtableHugePageSize(long var1) {
      setMemtableHugePageSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long memtableHugePageSize() {
      return memtableHugePageSize(this.nativeHandle_);
   }

   public Options setSoftPendingCompactionBytesLimit(long var1) {
      setSoftPendingCompactionBytesLimit(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long softPendingCompactionBytesLimit() {
      return softPendingCompactionBytesLimit(this.nativeHandle_);
   }

   public Options setHardPendingCompactionBytesLimit(long var1) {
      setHardPendingCompactionBytesLimit(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long hardPendingCompactionBytesLimit() {
      return hardPendingCompactionBytesLimit(this.nativeHandle_);
   }

   public Options setLevel0FileNumCompactionTrigger(int var1) {
      setLevel0FileNumCompactionTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int level0FileNumCompactionTrigger() {
      return level0FileNumCompactionTrigger(this.nativeHandle_);
   }

   public Options setLevel0SlowdownWritesTrigger(int var1) {
      setLevel0SlowdownWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int level0SlowdownWritesTrigger() {
      return level0SlowdownWritesTrigger(this.nativeHandle_);
   }

   public Options setLevel0StopWritesTrigger(int var1) {
      setLevel0StopWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int level0StopWritesTrigger() {
      return level0StopWritesTrigger(this.nativeHandle_);
   }

   public Options setMaxBytesForLevelMultiplierAdditional(int[] var1) {
      setMaxBytesForLevelMultiplierAdditional(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int[] maxBytesForLevelMultiplierAdditional() {
      return maxBytesForLevelMultiplierAdditional(this.nativeHandle_);
   }

   public Options setParanoidFileChecks(boolean var1) {
      setParanoidFileChecks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean paranoidFileChecks() {
      return paranoidFileChecks(this.nativeHandle_);
   }

   public Options setCompactionPriority(CompactionPriority var1) {
      setCompactionPriority(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompactionPriority compactionPriority() {
      return CompactionPriority.getCompactionPriority(compactionPriority(this.nativeHandle_));
   }

   public Options setReportBgIoStats(boolean var1) {
      setReportBgIoStats(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean reportBgIoStats() {
      return reportBgIoStats(this.nativeHandle_);
   }

   public Options setTtl(long var1) {
      setTtl(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long ttl() {
      return ttl(this.nativeHandle_);
   }

   public Options setPeriodicCompactionSeconds(long var1) {
      setPeriodicCompactionSeconds(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long periodicCompactionSeconds() {
      return periodicCompactionSeconds(this.nativeHandle_);
   }

   public Options setCompactionOptionsUniversal(CompactionOptionsUniversal var1) {
      setCompactionOptionsUniversal(this.nativeHandle_, var1.nativeHandle_);
      this.compactionOptionsUniversal_ = var1;
      return this;
   }

   @Override
   public CompactionOptionsUniversal compactionOptionsUniversal() {
      return this.compactionOptionsUniversal_;
   }

   public Options setCompactionOptionsFIFO(CompactionOptionsFIFO var1) {
      setCompactionOptionsFIFO(this.nativeHandle_, var1.nativeHandle_);
      this.compactionOptionsFIFO_ = var1;
      return this;
   }

   @Override
   public CompactionOptionsFIFO compactionOptionsFIFO() {
      return this.compactionOptionsFIFO_;
   }

   public Options setForceConsistencyChecks(boolean var1) {
      setForceConsistencyChecks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean forceConsistencyChecks() {
      return forceConsistencyChecks(this.nativeHandle_);
   }

   public Options setAtomicFlush(boolean var1) {
      setAtomicFlush(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean atomicFlush() {
      return atomicFlush(this.nativeHandle_);
   }

   public Options setAvoidUnnecessaryBlockingIO(boolean var1) {
      setAvoidUnnecessaryBlockingIO(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean avoidUnnecessaryBlockingIO() {
      assert this.isOwningHandle();
      return avoidUnnecessaryBlockingIO(this.nativeHandle_);
   }

   public Options setPersistStatsToDisk(boolean var1) {
      setPersistStatsToDisk(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean persistStatsToDisk() {
      assert this.isOwningHandle();
      return persistStatsToDisk(this.nativeHandle_);
   }

   public Options setWriteDbidToManifest(boolean var1) {
      setWriteDbidToManifest(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean writeDbidToManifest() {
      assert this.isOwningHandle();
      return writeDbidToManifest(this.nativeHandle_);
   }

   public Options setLogReadaheadSize(long var1) {
      setLogReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long logReadaheadSize() {
      assert this.isOwningHandle();
      return logReadaheadSize(this.nativeHandle_);
   }

   public Options setBestEffortsRecovery(boolean var1) {
      setBestEffortsRecovery(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean bestEffortsRecovery() {
      assert this.isOwningHandle();
      return bestEffortsRecovery(this.nativeHandle_);
   }

   public Options setMaxBgErrorResumeCount(int var1) {
      setMaxBgErrorResumeCount(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxBgerrorResumeCount() {
      assert this.isOwningHandle();
      return maxBgerrorResumeCount(this.nativeHandle_);
   }

   public Options setBgerrorResumeRetryInterval(long var1) {
      setBgerrorResumeRetryInterval(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long bgerrorResumeRetryInterval() {
      assert this.isOwningHandle();
      return bgerrorResumeRetryInterval(this.nativeHandle_);
   }

   public Options setSstPartitionerFactory(SstPartitionerFactory var1) {
      setSstPartitionerFactory(this.nativeHandle_, var1.nativeHandle_);
      this.sstPartitionerFactory_ = var1;
      return this;
   }

   @Override
   public SstPartitionerFactory sstPartitionerFactory() {
      return this.sstPartitionerFactory_;
   }

   public Options setMemtableMaxRangeDeletions(int var1) {
      setMemtableMaxRangeDeletions(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int memtableMaxRangeDeletions() {
      return memtableMaxRangeDeletions(this.nativeHandle_);
   }

   public Options setCompactionThreadLimiter(ConcurrentTaskLimiter var1) {
      setCompactionThreadLimiter(this.nativeHandle_, var1.nativeHandle_);
      this.compactionThreadLimiter_ = var1;
      return this;
   }

   @Override
   public ConcurrentTaskLimiter compactionThreadLimiter() {
      assert this.isOwningHandle();
      return this.compactionThreadLimiter_;
   }

   public Options setEnableBlobFiles(boolean var1) {
      setEnableBlobFiles(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableBlobFiles() {
      return enableBlobFiles(this.nativeHandle_);
   }

   public Options setMinBlobSize(long var1) {
      setMinBlobSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long minBlobSize() {
      return minBlobSize(this.nativeHandle_);
   }

   public Options setBlobFileSize(long var1) {
      setBlobFileSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long blobFileSize() {
      return blobFileSize(this.nativeHandle_);
   }

   public Options setBlobCompressionType(CompressionType var1) {
      setBlobCompressionType(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompressionType blobCompressionType() {
      return CompressionType.values()[blobCompressionType(this.nativeHandle_)];
   }

   public Options setEnableBlobGarbageCollection(boolean var1) {
      setEnableBlobGarbageCollection(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableBlobGarbageCollection() {
      return enableBlobGarbageCollection(this.nativeHandle_);
   }

   public Options setBlobGarbageCollectionAgeCutoff(double var1) {
      setBlobGarbageCollectionAgeCutoff(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double blobGarbageCollectionAgeCutoff() {
      return blobGarbageCollectionAgeCutoff(this.nativeHandle_);
   }

   public Options setBlobGarbageCollectionForceThreshold(double var1) {
      setBlobGarbageCollectionForceThreshold(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double blobGarbageCollectionForceThreshold() {
      return blobGarbageCollectionForceThreshold(this.nativeHandle_);
   }

   public Options setBlobCompactionReadaheadSize(long var1) {
      setBlobCompactionReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long blobCompactionReadaheadSize() {
      return blobCompactionReadaheadSize(this.nativeHandle_);
   }

   public Options setBlobFileStartingLevel(int var1) {
      setBlobFileStartingLevel(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int blobFileStartingLevel() {
      return blobFileStartingLevel(this.nativeHandle_);
   }

   public Options setPrepopulateBlobCache(PrepopulateBlobCache var1) {
      setPrepopulateBlobCache(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public PrepopulateBlobCache prepopulateBlobCache() {
      return PrepopulateBlobCache.getPrepopulateBlobCache(prepopulateBlobCache(this.nativeHandle_));
   }

   public List<TablePropertiesCollectorFactory> tablePropertiesCollectorFactory() {
      long[] var1 = tablePropertiesCollectorFactory(this.nativeHandle_);
      return Arrays.stream(var1).mapToObj(var0 -> TablePropertiesCollectorFactory.newWrapper(var0)).collect(Collectors.toList());
   }

   public void setTablePropertiesCollectorFactory(List<TablePropertiesCollectorFactory> var1) {
      long[] var2 = new long[var1.size()];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = ((TablePropertiesCollectorFactory)var1.get(var3)).getNativeHandle();
      }

      setTablePropertiesCollectorFactory(this.nativeHandle_, var2);
   }

   private static long newOptionsInstance() {
      RocksDB.loadLibrary();
      return newOptions();
   }

   private static native long newOptions();

   private static native long newOptions(long var0, long var2);

   private static native long copyOptions(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setEnv(long var0, long var2);

   private static native void prepareForBulkLoad(long var0);

   private static native void setIncreaseParallelism(long var0, int var2);

   private static native void setCreateIfMissing(long var0, boolean var2);

   private static native boolean createIfMissing(long var0);

   private static native void setCreateMissingColumnFamilies(long var0, boolean var2);

   private static native boolean createMissingColumnFamilies(long var0);

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

   private static native void setMaxTotalWalSize(long var0, long var2);

   private static native void setMaxFileOpeningThreads(long var0, int var2);

   private static native int maxFileOpeningThreads(long var0);

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

   private static native void setMaxTableFilesSizeFIFO(long var0, long var2);

   private static native long maxTableFilesSizeFIFO(long var0);

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

   private static native void oldDefaults(long var0, int var2, int var3);

   private static native void optimizeForSmallDb(long var0);

   private static native void optimizeForSmallDb(long var0, long var2);

   private static native void optimizeForPointLookup(long var0, long var2);

   private static native void optimizeLevelStyleCompaction(long var0, long var2);

   private static native void optimizeUniversalStyleCompaction(long var0, long var2);

   private static native void setComparatorHandle(long var0, int var2);

   private static native void setComparatorHandle(long var0, long var2, byte var4);

   private static native void setMergeOperatorName(long var0, String var2);

   private static native void setMergeOperator(long var0, long var2);

   private static native void setCompactionFilterHandle(long var0, long var2);

   private static native void setCompactionFilterFactoryHandle(long var0, long var2);

   private static native void setWriteBufferSize(long var0, long var2) throws IllegalArgumentException;

   private static native long writeBufferSize(long var0);

   private static native void setMaxWriteBufferNumber(long var0, int var2);

   private static native int maxWriteBufferNumber(long var0);

   private static native void setMinWriteBufferNumberToMerge(long var0, int var2);

   private static native int minWriteBufferNumberToMerge(long var0);

   private static native void setCompressionType(long var0, byte var2);

   private static native byte compressionType(long var0);

   private static native void setCompressionPerLevel(long var0, byte[] var2);

   private static native byte[] compressionPerLevel(long var0);

   private static native void setBottommostCompressionType(long var0, byte var2);

   private static native byte bottommostCompressionType(long var0);

   private static native void setBottommostCompressionOptions(long var0, long var2);

   private static native void setCompressionOptions(long var0, long var2);

   private static native void useFixedLengthPrefixExtractor(long var0, int var2);

   private static native void useCappedPrefixExtractor(long var0, int var2);

   private static native void setNumLevels(long var0, int var2);

   private static native int numLevels(long var0);

   private static native void setLevelZeroFileNumCompactionTrigger(long var0, int var2);

   private static native int levelZeroFileNumCompactionTrigger(long var0);

   private static native void setLevelZeroSlowdownWritesTrigger(long var0, int var2);

   private static native int levelZeroSlowdownWritesTrigger(long var0);

   private static native void setLevelZeroStopWritesTrigger(long var0, int var2);

   private static native int levelZeroStopWritesTrigger(long var0);

   private static native void setTargetFileSizeBase(long var0, long var2);

   private static native long targetFileSizeBase(long var0);

   private static native void setTargetFileSizeMultiplier(long var0, int var2);

   private static native int targetFileSizeMultiplier(long var0);

   private static native void setMaxBytesForLevelBase(long var0, long var2);

   private static native long maxBytesForLevelBase(long var0);

   private static native void setLevelCompactionDynamicLevelBytes(long var0, boolean var2);

   private static native boolean levelCompactionDynamicLevelBytes(long var0);

   private static native void setMaxBytesForLevelMultiplier(long var0, double var2);

   private static native double maxBytesForLevelMultiplier(long var0);

   private static native void setMaxCompactionBytes(long var0, long var2);

   private static native long maxCompactionBytes(long var0);

   private static native void setArenaBlockSize(long var0, long var2) throws IllegalArgumentException;

   private static native long arenaBlockSize(long var0);

   private static native void setDisableAutoCompactions(long var0, boolean var2);

   private static native boolean disableAutoCompactions(long var0);

   private static native void setCompactionStyle(long var0, byte var2);

   private static native byte compactionStyle(long var0);

   private static native void setMaxSequentialSkipInIterations(long var0, long var2);

   private static native long maxSequentialSkipInIterations(long var0);

   private static native void setMemTableFactory(long var0, long var2);

   private static native String memTableFactoryName(long var0);

   private static native void setTableFactory(long var0, long var2);

   private static native String tableFactoryName(long var0);

   private static native void setCfPaths(long var0, String[] var2, long[] var3);

   private static native long cfPathsLen(long var0);

   private static native void cfPaths(long var0, String[] var2, long[] var3);

   private static native void setInplaceUpdateSupport(long var0, boolean var2);

   private static native boolean inplaceUpdateSupport(long var0);

   private static native void setInplaceUpdateNumLocks(long var0, long var2) throws IllegalArgumentException;

   private static native long inplaceUpdateNumLocks(long var0);

   private static native void setMemtablePrefixBloomSizeRatio(long var0, double var2);

   private static native double memtablePrefixBloomSizeRatio(long var0);

   private static native void setExperimentalMempurgeThreshold(long var0, double var2);

   private static native double experimentalMempurgeThreshold(long var0);

   private static native void setMemtableWholeKeyFiltering(long var0, boolean var2);

   private static native boolean memtableWholeKeyFiltering(long var0);

   private static native void setBloomLocality(long var0, int var2);

   private static native int bloomLocality(long var0);

   private static native void setMaxSuccessiveMerges(long var0, long var2) throws IllegalArgumentException;

   private static native long maxSuccessiveMerges(long var0);

   private static native void setOptimizeFiltersForHits(long var0, boolean var2);

   private static native boolean optimizeFiltersForHits(long var0);

   private static native void setMemtableHugePageSize(long var0, long var2);

   private static native long memtableHugePageSize(long var0);

   private static native void setSoftPendingCompactionBytesLimit(long var0, long var2);

   private static native long softPendingCompactionBytesLimit(long var0);

   private static native void setHardPendingCompactionBytesLimit(long var0, long var2);

   private static native long hardPendingCompactionBytesLimit(long var0);

   private static native void setLevel0FileNumCompactionTrigger(long var0, int var2);

   private static native int level0FileNumCompactionTrigger(long var0);

   private static native void setLevel0SlowdownWritesTrigger(long var0, int var2);

   private static native int level0SlowdownWritesTrigger(long var0);

   private static native void setLevel0StopWritesTrigger(long var0, int var2);

   private static native int level0StopWritesTrigger(long var0);

   private static native void setMaxBytesForLevelMultiplierAdditional(long var0, int[] var2);

   private static native int[] maxBytesForLevelMultiplierAdditional(long var0);

   private static native void setParanoidFileChecks(long var0, boolean var2);

   private static native boolean paranoidFileChecks(long var0);

   private static native void setCompactionPriority(long var0, byte var2);

   private static native byte compactionPriority(long var0);

   private static native void setReportBgIoStats(long var0, boolean var2);

   private static native boolean reportBgIoStats(long var0);

   private static native void setTtl(long var0, long var2);

   private static native long ttl(long var0);

   private static native void setPeriodicCompactionSeconds(long var0, long var2);

   private static native long periodicCompactionSeconds(long var0);

   private static native void setCompactionOptionsUniversal(long var0, long var2);

   private static native void setCompactionOptionsFIFO(long var0, long var2);

   private static native void setForceConsistencyChecks(long var0, boolean var2);

   private static native boolean forceConsistencyChecks(long var0);

   private static native void setAtomicFlush(long var0, boolean var2);

   private static native boolean atomicFlush(long var0);

   private static native void setSstPartitionerFactory(long var0, long var2);

   private static native void setMemtableMaxRangeDeletions(long var0, int var2);

   private static native int memtableMaxRangeDeletions(long var0);

   private static native void setCompactionThreadLimiter(long var0, long var2);

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

   private static native void setEnableBlobFiles(long var0, boolean var2);

   private static native boolean enableBlobFiles(long var0);

   private static native void setMinBlobSize(long var0, long var2);

   private static native long minBlobSize(long var0);

   private static native void setBlobFileSize(long var0, long var2);

   private static native long blobFileSize(long var0);

   private static native void setBlobCompressionType(long var0, byte var2);

   private static native byte blobCompressionType(long var0);

   private static native void setEnableBlobGarbageCollection(long var0, boolean var2);

   private static native boolean enableBlobGarbageCollection(long var0);

   private static native void setBlobGarbageCollectionAgeCutoff(long var0, double var2);

   private static native double blobGarbageCollectionAgeCutoff(long var0);

   private static native void setBlobGarbageCollectionForceThreshold(long var0, double var2);

   private static native double blobGarbageCollectionForceThreshold(long var0);

   private static native void setBlobCompactionReadaheadSize(long var0, long var2);

   private static native long blobCompactionReadaheadSize(long var0);

   private static native void setBlobFileStartingLevel(long var0, int var2);

   private static native int blobFileStartingLevel(long var0);

   private static native void setPrepopulateBlobCache(long var0, byte var2);

   private static native byte prepopulateBlobCache(long var0);

   private static native long[] tablePropertiesCollectorFactory(long var0);

   private static native void setTablePropertiesCollectorFactory(long var0, long[] var2);
}
