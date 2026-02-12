package org.rocksdb;

import java.util.Collection;
import java.util.List;

public interface ColumnFamilyOptionsInterface<T extends ColumnFamilyOptionsInterface<T>> extends AdvancedColumnFamilyOptionsInterface<T> {
   long DEFAULT_COMPACTION_MEMTABLE_MEMORY_BUDGET = 536870912L;

   T oldDefaults(int var1, int var2);

   T optimizeForSmallDb();

   T optimizeForSmallDb(Cache var1);

   T optimizeForPointLookup(long var1);

   T optimizeLevelStyleCompaction();

   T optimizeLevelStyleCompaction(long var1);

   T optimizeUniversalStyleCompaction();

   T optimizeUniversalStyleCompaction(long var1);

   T setComparator(BuiltinComparator var1);

   T setComparator(AbstractComparator var1);

   T setMergeOperatorName(String var1);

   T setMergeOperator(MergeOperator var1);

   T setCompactionFilter(AbstractCompactionFilter<? extends AbstractSlice<?>> var1);

   AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter();

   T setCompactionFilterFactory(AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> var1);

   AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> compactionFilterFactory();

   T useFixedLengthPrefixExtractor(int var1);

   T useCappedPrefixExtractor(int var1);

   T setLevelZeroFileNumCompactionTrigger(int var1);

   int levelZeroFileNumCompactionTrigger();

   T setLevelZeroSlowdownWritesTrigger(int var1);

   int levelZeroSlowdownWritesTrigger();

   T setLevelZeroStopWritesTrigger(int var1);

   int levelZeroStopWritesTrigger();

   T setMaxBytesForLevelMultiplier(double var1);

   double maxBytesForLevelMultiplier();

   T setMaxTableFilesSizeFIFO(long var1);

   long maxTableFilesSizeFIFO();

   MemTableConfig memTableConfig();

   T setMemTableConfig(MemTableConfig var1);

   String memTableFactoryName();

   TableFormatConfig tableFormatConfig();

   T setTableFormatConfig(TableFormatConfig var1);

   String tableFactoryName();

   T setCfPaths(Collection<DbPath> var1);

   List<DbPath> cfPaths();

   T setBottommostCompressionType(CompressionType var1);

   CompressionType bottommostCompressionType();

   T setBottommostCompressionOptions(CompressionOptions var1);

   CompressionOptions bottommostCompressionOptions();

   T setCompressionOptions(CompressionOptions var1);

   CompressionOptions compressionOptions();

   T setSstPartitionerFactory(SstPartitionerFactory var1);

   SstPartitionerFactory sstPartitionerFactory();

   T setMemtableMaxRangeDeletions(int var1);

   int memtableMaxRangeDeletions();

   T setCompactionThreadLimiter(ConcurrentTaskLimiter var1);

   ConcurrentTaskLimiter compactionThreadLimiter();
}
