package org.rocksdb;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ColumnFamilyOptions
   extends RocksObject
   implements ColumnFamilyOptionsInterface<ColumnFamilyOptions>,
   MutableColumnFamilyOptionsInterface<ColumnFamilyOptions> {
   private MemTableConfig memTableConfig_;
   private TableFormatConfig tableFormatConfig_;
   private AbstractComparator comparator_;
   private AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter_;
   private AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> compactionFilterFactory_;
   private CompactionOptionsUniversal compactionOptionsUniversal_;
   private CompactionOptionsFIFO compactionOptionsFIFO_;
   private CompressionOptions bottommostCompressionOptions_;
   private CompressionOptions compressionOptions_;
   private SstPartitionerFactory sstPartitionerFactory_;
   private ConcurrentTaskLimiter compactionThreadLimiter_;

   public ColumnFamilyOptions() {
      super(newColumnFamilyOptionsInstance());
   }

   public ColumnFamilyOptions(ColumnFamilyOptions var1) {
      super(copyColumnFamilyOptions(var1.nativeHandle_));
      this.memTableConfig_ = var1.memTableConfig_;
      this.tableFormatConfig_ = var1.tableFormatConfig_;
      this.comparator_ = var1.comparator_;
      this.compactionFilter_ = var1.compactionFilter_;
      this.compactionFilterFactory_ = var1.compactionFilterFactory_;
      this.compactionOptionsUniversal_ = var1.compactionOptionsUniversal_;
      this.compactionOptionsFIFO_ = var1.compactionOptionsFIFO_;
      this.bottommostCompressionOptions_ = var1.bottommostCompressionOptions_;
      this.compressionOptions_ = var1.compressionOptions_;
      this.compactionThreadLimiter_ = var1.compactionThreadLimiter_;
      this.sstPartitionerFactory_ = var1.sstPartitionerFactory_;
   }

   public ColumnFamilyOptions(Options var1) {
      super(newColumnFamilyOptionsFromOptions(var1.nativeHandle_));
   }

   ColumnFamilyOptions(long var1) {
      super(var1);
   }

   public static ColumnFamilyOptions getColumnFamilyOptionsFromProps(Properties var0) {
      ColumnFamilyOptions var1 = null;
      long var2 = getColumnFamilyOptionsFromProps(Options.getOptionStringFromProps(var0));
      if (var2 != 0L) {
         var1 = new ColumnFamilyOptions(var2);
      }

      return var1;
   }

   public static ColumnFamilyOptions getColumnFamilyOptionsFromProps(ConfigOptions var0, Properties var1) {
      ColumnFamilyOptions var2 = null;
      long var3 = getColumnFamilyOptionsFromProps(var0.nativeHandle_, Options.getOptionStringFromProps(var1));
      if (var3 != 0L) {
         var2 = new ColumnFamilyOptions(var3);
      }

      return var2;
   }

   public ColumnFamilyOptions oldDefaults(int var1, int var2) {
      oldDefaults(this.nativeHandle_, var1, var2);
      return this;
   }

   public ColumnFamilyOptions optimizeForSmallDb() {
      optimizeForSmallDb(this.nativeHandle_);
      return this;
   }

   public ColumnFamilyOptions optimizeForSmallDb(Cache var1) {
      optimizeForSmallDb(this.nativeHandle_, var1.getNativeHandle());
      return this;
   }

   public ColumnFamilyOptions optimizeForPointLookup(long var1) {
      optimizeForPointLookup(this.nativeHandle_, var1);
      return this;
   }

   public ColumnFamilyOptions optimizeLevelStyleCompaction() {
      optimizeLevelStyleCompaction(this.nativeHandle_, 536870912L);
      return this;
   }

   public ColumnFamilyOptions optimizeLevelStyleCompaction(long var1) {
      optimizeLevelStyleCompaction(this.nativeHandle_, var1);
      return this;
   }

   public ColumnFamilyOptions optimizeUniversalStyleCompaction() {
      optimizeUniversalStyleCompaction(this.nativeHandle_, 536870912L);
      return this;
   }

   public ColumnFamilyOptions optimizeUniversalStyleCompaction(long var1) {
      optimizeUniversalStyleCompaction(this.nativeHandle_, var1);
      return this;
   }

   public ColumnFamilyOptions setComparator(BuiltinComparator var1) {
      assert this.isOwningHandle();
      setComparatorHandle(this.nativeHandle_, var1.ordinal());
      return this;
   }

   public ColumnFamilyOptions setComparator(AbstractComparator var1) {
      assert this.isOwningHandle();
      setComparatorHandle(this.nativeHandle_, var1.nativeHandle_, var1.getComparatorType().getValue());
      this.comparator_ = var1;
      return this;
   }

   public ColumnFamilyOptions setMergeOperatorName(String var1) {
      assert this.isOwningHandle();
      if (var1 == null) {
         throw new IllegalArgumentException("Merge operator name must not be null.");
      }

      setMergeOperatorName(this.nativeHandle_, var1);
      return this;
   }

   public ColumnFamilyOptions setMergeOperator(MergeOperator var1) {
      setMergeOperator(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public ColumnFamilyOptions setCompactionFilter(AbstractCompactionFilter<? extends AbstractSlice<?>> var1) {
      setCompactionFilterHandle(this.nativeHandle_, var1.nativeHandle_);
      this.compactionFilter_ = var1;
      return this;
   }

   @Override
   public AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter() {
      assert this.isOwningHandle();
      return this.compactionFilter_;
   }

   public ColumnFamilyOptions setCompactionFilterFactory(AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> var1) {
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

   public ColumnFamilyOptions setWriteBufferSize(long var1) {
      assert this.isOwningHandle();
      setWriteBufferSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long writeBufferSize() {
      assert this.isOwningHandle();
      return writeBufferSize(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMaxWriteBufferNumber(int var1) {
      assert this.isOwningHandle();
      setMaxWriteBufferNumber(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int maxWriteBufferNumber() {
      assert this.isOwningHandle();
      return maxWriteBufferNumber(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMinWriteBufferNumberToMerge(int var1) {
      setMinWriteBufferNumberToMerge(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int minWriteBufferNumberToMerge() {
      return minWriteBufferNumberToMerge(this.nativeHandle_);
   }

   public ColumnFamilyOptions useFixedLengthPrefixExtractor(int var1) {
      assert this.isOwningHandle();
      useFixedLengthPrefixExtractor(this.nativeHandle_, var1);
      return this;
   }

   public ColumnFamilyOptions useCappedPrefixExtractor(int var1) {
      assert this.isOwningHandle();
      useCappedPrefixExtractor(this.nativeHandle_, var1);
      return this;
   }

   public ColumnFamilyOptions setCompressionType(CompressionType var1) {
      setCompressionType(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompressionType compressionType() {
      return CompressionType.getCompressionType(compressionType(this.nativeHandle_));
   }

   public ColumnFamilyOptions setCompressionPerLevel(List<CompressionType> var1) {
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

   public ColumnFamilyOptions setBottommostCompressionType(CompressionType var1) {
      setBottommostCompressionType(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompressionType bottommostCompressionType() {
      return CompressionType.getCompressionType(bottommostCompressionType(this.nativeHandle_));
   }

   public ColumnFamilyOptions setBottommostCompressionOptions(CompressionOptions var1) {
      setBottommostCompressionOptions(this.nativeHandle_, var1.nativeHandle_);
      this.bottommostCompressionOptions_ = var1;
      return this;
   }

   @Override
   public CompressionOptions bottommostCompressionOptions() {
      return this.bottommostCompressionOptions_;
   }

   public ColumnFamilyOptions setCompressionOptions(CompressionOptions var1) {
      setCompressionOptions(this.nativeHandle_, var1.nativeHandle_);
      this.compressionOptions_ = var1;
      return this;
   }

   @Override
   public CompressionOptions compressionOptions() {
      return this.compressionOptions_;
   }

   public ColumnFamilyOptions setNumLevels(int var1) {
      setNumLevels(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int numLevels() {
      return numLevels(this.nativeHandle_);
   }

   public ColumnFamilyOptions setLevelZeroFileNumCompactionTrigger(int var1) {
      setLevelZeroFileNumCompactionTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int levelZeroFileNumCompactionTrigger() {
      return levelZeroFileNumCompactionTrigger(this.nativeHandle_);
   }

   public ColumnFamilyOptions setLevelZeroSlowdownWritesTrigger(int var1) {
      setLevelZeroSlowdownWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int levelZeroSlowdownWritesTrigger() {
      return levelZeroSlowdownWritesTrigger(this.nativeHandle_);
   }

   public ColumnFamilyOptions setLevelZeroStopWritesTrigger(int var1) {
      setLevelZeroStopWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int levelZeroStopWritesTrigger() {
      return levelZeroStopWritesTrigger(this.nativeHandle_);
   }

   public ColumnFamilyOptions setTargetFileSizeBase(long var1) {
      setTargetFileSizeBase(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long targetFileSizeBase() {
      return targetFileSizeBase(this.nativeHandle_);
   }

   public ColumnFamilyOptions setTargetFileSizeMultiplier(int var1) {
      setTargetFileSizeMultiplier(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int targetFileSizeMultiplier() {
      return targetFileSizeMultiplier(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMaxBytesForLevelBase(long var1) {
      setMaxBytesForLevelBase(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxBytesForLevelBase() {
      return maxBytesForLevelBase(this.nativeHandle_);
   }

   public ColumnFamilyOptions setLevelCompactionDynamicLevelBytes(boolean var1) {
      setLevelCompactionDynamicLevelBytes(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean levelCompactionDynamicLevelBytes() {
      return levelCompactionDynamicLevelBytes(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMaxBytesForLevelMultiplier(double var1) {
      setMaxBytesForLevelMultiplier(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double maxBytesForLevelMultiplier() {
      return maxBytesForLevelMultiplier(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMaxCompactionBytes(long var1) {
      setMaxCompactionBytes(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxCompactionBytes() {
      return maxCompactionBytes(this.nativeHandle_);
   }

   public ColumnFamilyOptions setArenaBlockSize(long var1) {
      setArenaBlockSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long arenaBlockSize() {
      return arenaBlockSize(this.nativeHandle_);
   }

   public ColumnFamilyOptions setDisableAutoCompactions(boolean var1) {
      setDisableAutoCompactions(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean disableAutoCompactions() {
      return disableAutoCompactions(this.nativeHandle_);
   }

   public ColumnFamilyOptions setCompactionStyle(CompactionStyle var1) {
      setCompactionStyle(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompactionStyle compactionStyle() {
      return CompactionStyle.fromValue(compactionStyle(this.nativeHandle_));
   }

   public ColumnFamilyOptions setMaxTableFilesSizeFIFO(long var1) {
      assert var1 > 0L;
      assert this.isOwningHandle();
      setMaxTableFilesSizeFIFO(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxTableFilesSizeFIFO() {
      return maxTableFilesSizeFIFO(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMaxSequentialSkipInIterations(long var1) {
      setMaxSequentialSkipInIterations(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxSequentialSkipInIterations() {
      return maxSequentialSkipInIterations(this.nativeHandle_);
   }

   @Override
   public MemTableConfig memTableConfig() {
      return this.memTableConfig_;
   }

   public ColumnFamilyOptions setMemTableConfig(MemTableConfig var1) {
      setMemTableFactory(this.nativeHandle_, var1.newMemTableFactoryHandle());
      this.memTableConfig_ = var1;
      return this;
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

   public ColumnFamilyOptions setTableFormatConfig(TableFormatConfig var1) {
      setTableFactory(this.nativeHandle_, var1.newTableFactoryHandle());
      this.tableFormatConfig_ = var1;
      return this;
   }

   void setFetchedTableFormatConfig(TableFormatConfig var1) {
      this.tableFormatConfig_ = var1;
   }

   @Override
   public String tableFactoryName() {
      assert this.isOwningHandle();
      return tableFactoryName(this.nativeHandle_);
   }

   public ColumnFamilyOptions setCfPaths(Collection<DbPath> var1) {
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

   public ColumnFamilyOptions setInplaceUpdateSupport(boolean var1) {
      setInplaceUpdateSupport(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean inplaceUpdateSupport() {
      return inplaceUpdateSupport(this.nativeHandle_);
   }

   public ColumnFamilyOptions setInplaceUpdateNumLocks(long var1) {
      setInplaceUpdateNumLocks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long inplaceUpdateNumLocks() {
      return inplaceUpdateNumLocks(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMemtablePrefixBloomSizeRatio(double var1) {
      setMemtablePrefixBloomSizeRatio(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double memtablePrefixBloomSizeRatio() {
      return memtablePrefixBloomSizeRatio(this.nativeHandle_);
   }

   public ColumnFamilyOptions setExperimentalMempurgeThreshold(double var1) {
      setExperimentalMempurgeThreshold(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double experimentalMempurgeThreshold() {
      return experimentalMempurgeThreshold(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMemtableWholeKeyFiltering(boolean var1) {
      setMemtableWholeKeyFiltering(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean memtableWholeKeyFiltering() {
      return memtableWholeKeyFiltering(this.nativeHandle_);
   }

   public ColumnFamilyOptions setBloomLocality(int var1) {
      setBloomLocality(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int bloomLocality() {
      return bloomLocality(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMaxSuccessiveMerges(long var1) {
      setMaxSuccessiveMerges(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long maxSuccessiveMerges() {
      return maxSuccessiveMerges(this.nativeHandle_);
   }

   public ColumnFamilyOptions setOptimizeFiltersForHits(boolean var1) {
      setOptimizeFiltersForHits(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean optimizeFiltersForHits() {
      return optimizeFiltersForHits(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMemtableHugePageSize(long var1) {
      setMemtableHugePageSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long memtableHugePageSize() {
      return memtableHugePageSize(this.nativeHandle_);
   }

   public ColumnFamilyOptions setSoftPendingCompactionBytesLimit(long var1) {
      setSoftPendingCompactionBytesLimit(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long softPendingCompactionBytesLimit() {
      return softPendingCompactionBytesLimit(this.nativeHandle_);
   }

   public ColumnFamilyOptions setHardPendingCompactionBytesLimit(long var1) {
      setHardPendingCompactionBytesLimit(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long hardPendingCompactionBytesLimit() {
      return hardPendingCompactionBytesLimit(this.nativeHandle_);
   }

   public ColumnFamilyOptions setLevel0FileNumCompactionTrigger(int var1) {
      setLevel0FileNumCompactionTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int level0FileNumCompactionTrigger() {
      return level0FileNumCompactionTrigger(this.nativeHandle_);
   }

   public ColumnFamilyOptions setLevel0SlowdownWritesTrigger(int var1) {
      setLevel0SlowdownWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int level0SlowdownWritesTrigger() {
      return level0SlowdownWritesTrigger(this.nativeHandle_);
   }

   public ColumnFamilyOptions setLevel0StopWritesTrigger(int var1) {
      setLevel0StopWritesTrigger(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int level0StopWritesTrigger() {
      return level0StopWritesTrigger(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMaxBytesForLevelMultiplierAdditional(int[] var1) {
      setMaxBytesForLevelMultiplierAdditional(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int[] maxBytesForLevelMultiplierAdditional() {
      return maxBytesForLevelMultiplierAdditional(this.nativeHandle_);
   }

   public ColumnFamilyOptions setParanoidFileChecks(boolean var1) {
      setParanoidFileChecks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean paranoidFileChecks() {
      return paranoidFileChecks(this.nativeHandle_);
   }

   public ColumnFamilyOptions setCompactionPriority(CompactionPriority var1) {
      setCompactionPriority(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompactionPriority compactionPriority() {
      return CompactionPriority.getCompactionPriority(compactionPriority(this.nativeHandle_));
   }

   public ColumnFamilyOptions setReportBgIoStats(boolean var1) {
      setReportBgIoStats(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean reportBgIoStats() {
      return reportBgIoStats(this.nativeHandle_);
   }

   public ColumnFamilyOptions setTtl(long var1) {
      setTtl(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long ttl() {
      return ttl(this.nativeHandle_);
   }

   public ColumnFamilyOptions setPeriodicCompactionSeconds(long var1) {
      setPeriodicCompactionSeconds(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long periodicCompactionSeconds() {
      return periodicCompactionSeconds(this.nativeHandle_);
   }

   public ColumnFamilyOptions setCompactionOptionsUniversal(CompactionOptionsUniversal var1) {
      setCompactionOptionsUniversal(this.nativeHandle_, var1.nativeHandle_);
      this.compactionOptionsUniversal_ = var1;
      return this;
   }

   @Override
   public CompactionOptionsUniversal compactionOptionsUniversal() {
      return this.compactionOptionsUniversal_;
   }

   public ColumnFamilyOptions setCompactionOptionsFIFO(CompactionOptionsFIFO var1) {
      setCompactionOptionsFIFO(this.nativeHandle_, var1.nativeHandle_);
      this.compactionOptionsFIFO_ = var1;
      return this;
   }

   @Override
   public CompactionOptionsFIFO compactionOptionsFIFO() {
      return this.compactionOptionsFIFO_;
   }

   public ColumnFamilyOptions setForceConsistencyChecks(boolean var1) {
      setForceConsistencyChecks(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean forceConsistencyChecks() {
      return forceConsistencyChecks(this.nativeHandle_);
   }

   public ColumnFamilyOptions setSstPartitionerFactory(SstPartitionerFactory var1) {
      setSstPartitionerFactory(this.nativeHandle_, var1.nativeHandle_);
      this.sstPartitionerFactory_ = var1;
      return this;
   }

   public ColumnFamilyOptions setCompactionThreadLimiter(ConcurrentTaskLimiter var1) {
      setCompactionThreadLimiter(this.nativeHandle_, var1.nativeHandle_);
      this.compactionThreadLimiter_ = var1;
      return this;
   }

   @Override
   public ConcurrentTaskLimiter compactionThreadLimiter() {
      assert this.isOwningHandle();
      return this.compactionThreadLimiter_;
   }

   @Override
   public SstPartitionerFactory sstPartitionerFactory() {
      return this.sstPartitionerFactory_;
   }

   public ColumnFamilyOptions setMemtableMaxRangeDeletions(int var1) {
      setMemtableMaxRangeDeletions(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int memtableMaxRangeDeletions() {
      return memtableMaxRangeDeletions(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMinTombstonesForRangeConversion(int var1) {
      setMinTombstonesForRangeConversion(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int minTombstonesForRangeConversion() {
      return minTombstonesForRangeConversion(this.nativeHandle_);
   }

   public ColumnFamilyOptions setEnableBlobFiles(boolean var1) {
      setEnableBlobFiles(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableBlobFiles() {
      return enableBlobFiles(this.nativeHandle_);
   }

   public ColumnFamilyOptions setMinBlobSize(long var1) {
      setMinBlobSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long minBlobSize() {
      return minBlobSize(this.nativeHandle_);
   }

   public ColumnFamilyOptions setBlobFileSize(long var1) {
      setBlobFileSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long blobFileSize() {
      return blobFileSize(this.nativeHandle_);
   }

   public ColumnFamilyOptions setBlobCompressionType(CompressionType var1) {
      setBlobCompressionType(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public CompressionType blobCompressionType() {
      return CompressionType.values()[blobCompressionType(this.nativeHandle_)];
   }

   public ColumnFamilyOptions setEnableBlobGarbageCollection(boolean var1) {
      setEnableBlobGarbageCollection(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public boolean enableBlobGarbageCollection() {
      return enableBlobGarbageCollection(this.nativeHandle_);
   }

   public ColumnFamilyOptions setBlobGarbageCollectionAgeCutoff(double var1) {
      setBlobGarbageCollectionAgeCutoff(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double blobGarbageCollectionAgeCutoff() {
      return blobGarbageCollectionAgeCutoff(this.nativeHandle_);
   }

   public ColumnFamilyOptions setBlobGarbageCollectionForceThreshold(double var1) {
      setBlobGarbageCollectionForceThreshold(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double blobGarbageCollectionForceThreshold() {
      return blobGarbageCollectionForceThreshold(this.nativeHandle_);
   }

   public ColumnFamilyOptions setReadTriggeredCompactionThreshold(double var1) {
      setReadTriggeredCompactionThreshold(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public double readTriggeredCompactionThreshold() {
      return readTriggeredCompactionThreshold(this.nativeHandle_);
   }

   public ColumnFamilyOptions setBlobCompactionReadaheadSize(long var1) {
      setBlobCompactionReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public long blobCompactionReadaheadSize() {
      return blobCompactionReadaheadSize(this.nativeHandle_);
   }

   public ColumnFamilyOptions setBlobFileStartingLevel(int var1) {
      setBlobFileStartingLevel(this.nativeHandle_, var1);
      return this;
   }

   @Override
   public int blobFileStartingLevel() {
      return blobFileStartingLevel(this.nativeHandle_);
   }

   public ColumnFamilyOptions setPrepopulateBlobCache(PrepopulateBlobCache var1) {
      setPrepopulateBlobCache(this.nativeHandle_, var1.getValue());
      return this;
   }

   @Override
   public PrepopulateBlobCache prepopulateBlobCache() {
      return PrepopulateBlobCache.getPrepopulateBlobCache(prepopulateBlobCache(this.nativeHandle_));
   }

   private static native long getColumnFamilyOptionsFromProps(long var0, String var2);

   private static native long getColumnFamilyOptionsFromProps(String var0);

   private static long newColumnFamilyOptionsInstance() {
      RocksDB.loadLibrary();
      return newColumnFamilyOptions();
   }

   private static native long newColumnFamilyOptions();

   private static native long copyColumnFamilyOptions(long var0);

   private static native long newColumnFamilyOptionsFromOptions(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

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

   private static native void setMaxTableFilesSizeFIFO(long var0, long var2);

   private static native long maxTableFilesSizeFIFO(long var0);

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

   private static native void setSstPartitionerFactory(long var0, long var2);

   private static native void setCompactionThreadLimiter(long var0, long var2);

   private static native void setMemtableMaxRangeDeletions(long var0, int var2);

   private static native int memtableMaxRangeDeletions(long var0);

   private static native void setMinTombstonesForRangeConversion(long var0, int var2);

   private static native int minTombstonesForRangeConversion(long var0);

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

   private static native void setReadTriggeredCompactionThreshold(long var0, double var2);

   private static native double readTriggeredCompactionThreshold(long var0);

   private static native void setBlobCompactionReadaheadSize(long var0, long var2);

   private static native long blobCompactionReadaheadSize(long var0);

   private static native void setBlobFileStartingLevel(long var0, int var2);

   private static native int blobFileStartingLevel(long var0);

   private static native void setPrepopulateBlobCache(long var0, byte var2);

   private static native byte prepopulateBlobCache(long var0);
}
