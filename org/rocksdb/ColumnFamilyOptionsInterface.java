/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Collection;
import java.util.List;
import org.rocksdb.AbstractCompactionFilter;
import org.rocksdb.AbstractCompactionFilterFactory;
import org.rocksdb.AbstractComparator;
import org.rocksdb.AbstractSlice;
import org.rocksdb.AdvancedColumnFamilyOptionsInterface;
import org.rocksdb.BuiltinComparator;
import org.rocksdb.Cache;
import org.rocksdb.CompressionOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.ConcurrentTaskLimiter;
import org.rocksdb.DbPath;
import org.rocksdb.MemTableConfig;
import org.rocksdb.MergeOperator;
import org.rocksdb.SstPartitionerFactory;
import org.rocksdb.TableFormatConfig;

public interface ColumnFamilyOptionsInterface<T extends ColumnFamilyOptionsInterface<T>>
extends AdvancedColumnFamilyOptionsInterface<T> {
    public static final long DEFAULT_COMPACTION_MEMTABLE_MEMORY_BUDGET = 0x20000000L;

    public T oldDefaults(int var1, int var2);

    public T optimizeForSmallDb();

    public T optimizeForSmallDb(Cache var1);

    public T optimizeForPointLookup(long var1);

    public T optimizeLevelStyleCompaction();

    public T optimizeLevelStyleCompaction(long var1);

    public T optimizeUniversalStyleCompaction();

    public T optimizeUniversalStyleCompaction(long var1);

    public T setComparator(BuiltinComparator var1);

    public T setComparator(AbstractComparator var1);

    public T setMergeOperatorName(String var1);

    public T setMergeOperator(MergeOperator var1);

    public T setCompactionFilter(AbstractCompactionFilter<? extends AbstractSlice<?>> var1);

    public AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter();

    public T setCompactionFilterFactory(AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> var1);

    public AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> compactionFilterFactory();

    public T useFixedLengthPrefixExtractor(int var1);

    public T useCappedPrefixExtractor(int var1);

    public T setLevelZeroFileNumCompactionTrigger(int var1);

    public int levelZeroFileNumCompactionTrigger();

    public T setLevelZeroSlowdownWritesTrigger(int var1);

    public int levelZeroSlowdownWritesTrigger();

    public T setLevelZeroStopWritesTrigger(int var1);

    public int levelZeroStopWritesTrigger();

    public T setMaxBytesForLevelMultiplier(double var1);

    public double maxBytesForLevelMultiplier();

    public T setMaxTableFilesSizeFIFO(long var1);

    public long maxTableFilesSizeFIFO();

    public MemTableConfig memTableConfig();

    public T setMemTableConfig(MemTableConfig var1);

    public String memTableFactoryName();

    public TableFormatConfig tableFormatConfig();

    public T setTableFormatConfig(TableFormatConfig var1);

    public String tableFactoryName();

    public T setCfPaths(Collection<DbPath> var1);

    public List<DbPath> cfPaths();

    public T setBottommostCompressionType(CompressionType var1);

    public CompressionType bottommostCompressionType();

    public T setBottommostCompressionOptions(CompressionOptions var1);

    public CompressionOptions bottommostCompressionOptions();

    public T setCompressionOptions(CompressionOptions var1);

    public CompressionOptions compressionOptions();

    public T setSstPartitionerFactory(SstPartitionerFactory var1);

    public SstPartitionerFactory sstPartitionerFactory();

    public T setMemtableMaxRangeDeletions(int var1);

    public int memtableMaxRangeDeletions();

    public T setCompactionThreadLimiter(ConcurrentTaskLimiter var1);

    public ConcurrentTaskLimiter compactionThreadLimiter();
}

