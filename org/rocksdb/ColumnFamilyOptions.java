/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.rocksdb.AbstractCompactionFilter;
import org.rocksdb.AbstractCompactionFilterFactory;
import org.rocksdb.AbstractComparator;
import org.rocksdb.AbstractSlice;
import org.rocksdb.BuiltinComparator;
import org.rocksdb.Cache;
import org.rocksdb.ColumnFamilyOptionsInterface;
import org.rocksdb.CompactionOptionsFIFO;
import org.rocksdb.CompactionOptionsUniversal;
import org.rocksdb.CompactionPriority;
import org.rocksdb.CompactionStyle;
import org.rocksdb.CompressionOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.ConcurrentTaskLimiter;
import org.rocksdb.ConfigOptions;
import org.rocksdb.DbPath;
import org.rocksdb.MemTableConfig;
import org.rocksdb.MergeOperator;
import org.rocksdb.MutableColumnFamilyOptionsInterface;
import org.rocksdb.Options;
import org.rocksdb.PrepopulateBlobCache;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;
import org.rocksdb.SstPartitionerFactory;
import org.rocksdb.TableFormatConfig;

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
        super(ColumnFamilyOptions.newColumnFamilyOptionsInstance());
    }

    public ColumnFamilyOptions(ColumnFamilyOptions columnFamilyOptions) {
        super(ColumnFamilyOptions.copyColumnFamilyOptions(columnFamilyOptions.nativeHandle_));
        this.memTableConfig_ = columnFamilyOptions.memTableConfig_;
        this.tableFormatConfig_ = columnFamilyOptions.tableFormatConfig_;
        this.comparator_ = columnFamilyOptions.comparator_;
        this.compactionFilter_ = columnFamilyOptions.compactionFilter_;
        this.compactionFilterFactory_ = columnFamilyOptions.compactionFilterFactory_;
        this.compactionOptionsUniversal_ = columnFamilyOptions.compactionOptionsUniversal_;
        this.compactionOptionsFIFO_ = columnFamilyOptions.compactionOptionsFIFO_;
        this.bottommostCompressionOptions_ = columnFamilyOptions.bottommostCompressionOptions_;
        this.compressionOptions_ = columnFamilyOptions.compressionOptions_;
        this.compactionThreadLimiter_ = columnFamilyOptions.compactionThreadLimiter_;
        this.sstPartitionerFactory_ = columnFamilyOptions.sstPartitionerFactory_;
    }

    public ColumnFamilyOptions(Options options) {
        super(ColumnFamilyOptions.newColumnFamilyOptionsFromOptions(options.nativeHandle_));
    }

    ColumnFamilyOptions(long l) {
        super(l);
    }

    public static ColumnFamilyOptions getColumnFamilyOptionsFromProps(Properties properties) {
        ColumnFamilyOptions columnFamilyOptions = null;
        long l = ColumnFamilyOptions.getColumnFamilyOptionsFromProps(Options.getOptionStringFromProps(properties));
        if (l != 0L) {
            columnFamilyOptions = new ColumnFamilyOptions(l);
        }
        return columnFamilyOptions;
    }

    public static ColumnFamilyOptions getColumnFamilyOptionsFromProps(ConfigOptions configOptions, Properties properties) {
        ColumnFamilyOptions columnFamilyOptions = null;
        long l = ColumnFamilyOptions.getColumnFamilyOptionsFromProps(configOptions.nativeHandle_, Options.getOptionStringFromProps(properties));
        if (l != 0L) {
            columnFamilyOptions = new ColumnFamilyOptions(l);
        }
        return columnFamilyOptions;
    }

    @Override
    public ColumnFamilyOptions oldDefaults(int n, int n2) {
        ColumnFamilyOptions.oldDefaults(this.nativeHandle_, n, n2);
        return this;
    }

    @Override
    public ColumnFamilyOptions optimizeForSmallDb() {
        ColumnFamilyOptions.optimizeForSmallDb(this.nativeHandle_);
        return this;
    }

    @Override
    public ColumnFamilyOptions optimizeForSmallDb(Cache cache) {
        ColumnFamilyOptions.optimizeForSmallDb(this.nativeHandle_, cache.getNativeHandle());
        return this;
    }

    @Override
    public ColumnFamilyOptions optimizeForPointLookup(long l) {
        ColumnFamilyOptions.optimizeForPointLookup(this.nativeHandle_, l);
        return this;
    }

    @Override
    public ColumnFamilyOptions optimizeLevelStyleCompaction() {
        ColumnFamilyOptions.optimizeLevelStyleCompaction(this.nativeHandle_, 0x20000000L);
        return this;
    }

    @Override
    public ColumnFamilyOptions optimizeLevelStyleCompaction(long l) {
        ColumnFamilyOptions.optimizeLevelStyleCompaction(this.nativeHandle_, l);
        return this;
    }

    @Override
    public ColumnFamilyOptions optimizeUniversalStyleCompaction() {
        ColumnFamilyOptions.optimizeUniversalStyleCompaction(this.nativeHandle_, 0x20000000L);
        return this;
    }

    @Override
    public ColumnFamilyOptions optimizeUniversalStyleCompaction(long l) {
        ColumnFamilyOptions.optimizeUniversalStyleCompaction(this.nativeHandle_, l);
        return this;
    }

    @Override
    public ColumnFamilyOptions setComparator(BuiltinComparator builtinComparator) {
        assert (this.isOwningHandle());
        ColumnFamilyOptions.setComparatorHandle(this.nativeHandle_, builtinComparator.ordinal());
        return this;
    }

    @Override
    public ColumnFamilyOptions setComparator(AbstractComparator abstractComparator) {
        assert (this.isOwningHandle());
        ColumnFamilyOptions.setComparatorHandle(this.nativeHandle_, abstractComparator.nativeHandle_, abstractComparator.getComparatorType().getValue());
        this.comparator_ = abstractComparator;
        return this;
    }

    @Override
    public ColumnFamilyOptions setMergeOperatorName(String string) {
        assert (this.isOwningHandle());
        if (string == null) {
            throw new IllegalArgumentException("Merge operator name must not be null.");
        }
        ColumnFamilyOptions.setMergeOperatorName(this.nativeHandle_, string);
        return this;
    }

    @Override
    public ColumnFamilyOptions setMergeOperator(MergeOperator mergeOperator) {
        ColumnFamilyOptions.setMergeOperator(this.nativeHandle_, mergeOperator.nativeHandle_);
        return this;
    }

    @Override
    public ColumnFamilyOptions setCompactionFilter(AbstractCompactionFilter<? extends AbstractSlice<?>> abstractCompactionFilter) {
        ColumnFamilyOptions.setCompactionFilterHandle(this.nativeHandle_, abstractCompactionFilter.nativeHandle_);
        this.compactionFilter_ = abstractCompactionFilter;
        return this;
    }

    @Override
    public AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter() {
        assert (this.isOwningHandle());
        return this.compactionFilter_;
    }

    @Override
    public ColumnFamilyOptions setCompactionFilterFactory(AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> abstractCompactionFilterFactory) {
        assert (this.isOwningHandle());
        ColumnFamilyOptions.setCompactionFilterFactoryHandle(this.nativeHandle_, abstractCompactionFilterFactory.nativeHandle_);
        this.compactionFilterFactory_ = abstractCompactionFilterFactory;
        return this;
    }

    @Override
    public AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> compactionFilterFactory() {
        assert (this.isOwningHandle());
        return this.compactionFilterFactory_;
    }

    @Override
    public ColumnFamilyOptions setWriteBufferSize(long l) {
        assert (this.isOwningHandle());
        ColumnFamilyOptions.setWriteBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writeBufferSize() {
        assert (this.isOwningHandle());
        return ColumnFamilyOptions.writeBufferSize(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMaxWriteBufferNumber(int n) {
        assert (this.isOwningHandle());
        ColumnFamilyOptions.setMaxWriteBufferNumber(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxWriteBufferNumber() {
        assert (this.isOwningHandle());
        return ColumnFamilyOptions.maxWriteBufferNumber(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMinWriteBufferNumberToMerge(int n) {
        ColumnFamilyOptions.setMinWriteBufferNumberToMerge(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int minWriteBufferNumberToMerge() {
        return ColumnFamilyOptions.minWriteBufferNumberToMerge(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions useFixedLengthPrefixExtractor(int n) {
        assert (this.isOwningHandle());
        ColumnFamilyOptions.useFixedLengthPrefixExtractor(this.nativeHandle_, n);
        return this;
    }

    @Override
    public ColumnFamilyOptions useCappedPrefixExtractor(int n) {
        assert (this.isOwningHandle());
        ColumnFamilyOptions.useCappedPrefixExtractor(this.nativeHandle_, n);
        return this;
    }

    @Override
    public ColumnFamilyOptions setCompressionType(CompressionType compressionType) {
        ColumnFamilyOptions.setCompressionType(this.nativeHandle_, compressionType.getValue());
        return this;
    }

    @Override
    public CompressionType compressionType() {
        return CompressionType.getCompressionType(ColumnFamilyOptions.compressionType(this.nativeHandle_));
    }

    @Override
    public ColumnFamilyOptions setCompressionPerLevel(List<CompressionType> list) {
        byte[] byArray = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            byArray[i] = list.get(i).getValue();
        }
        ColumnFamilyOptions.setCompressionPerLevel(this.nativeHandle_, byArray);
        return this;
    }

    @Override
    public List<CompressionType> compressionPerLevel() {
        byte[] byArray = ColumnFamilyOptions.compressionPerLevel(this.nativeHandle_);
        ArrayList<CompressionType> arrayList = new ArrayList<CompressionType>();
        for (byte by : byArray) {
            arrayList.add(CompressionType.getCompressionType(by));
        }
        return arrayList;
    }

    @Override
    public ColumnFamilyOptions setBottommostCompressionType(CompressionType compressionType) {
        ColumnFamilyOptions.setBottommostCompressionType(this.nativeHandle_, compressionType.getValue());
        return this;
    }

    @Override
    public CompressionType bottommostCompressionType() {
        return CompressionType.getCompressionType(ColumnFamilyOptions.bottommostCompressionType(this.nativeHandle_));
    }

    @Override
    public ColumnFamilyOptions setBottommostCompressionOptions(CompressionOptions compressionOptions) {
        ColumnFamilyOptions.setBottommostCompressionOptions(this.nativeHandle_, compressionOptions.nativeHandle_);
        this.bottommostCompressionOptions_ = compressionOptions;
        return this;
    }

    @Override
    public CompressionOptions bottommostCompressionOptions() {
        return this.bottommostCompressionOptions_;
    }

    @Override
    public ColumnFamilyOptions setCompressionOptions(CompressionOptions compressionOptions) {
        ColumnFamilyOptions.setCompressionOptions(this.nativeHandle_, compressionOptions.nativeHandle_);
        this.compressionOptions_ = compressionOptions;
        return this;
    }

    @Override
    public CompressionOptions compressionOptions() {
        return this.compressionOptions_;
    }

    @Override
    public ColumnFamilyOptions setNumLevels(int n) {
        ColumnFamilyOptions.setNumLevels(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int numLevels() {
        return ColumnFamilyOptions.numLevels(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setLevelZeroFileNumCompactionTrigger(int n) {
        ColumnFamilyOptions.setLevelZeroFileNumCompactionTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int levelZeroFileNumCompactionTrigger() {
        return ColumnFamilyOptions.levelZeroFileNumCompactionTrigger(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setLevelZeroSlowdownWritesTrigger(int n) {
        ColumnFamilyOptions.setLevelZeroSlowdownWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int levelZeroSlowdownWritesTrigger() {
        return ColumnFamilyOptions.levelZeroSlowdownWritesTrigger(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setLevelZeroStopWritesTrigger(int n) {
        ColumnFamilyOptions.setLevelZeroStopWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int levelZeroStopWritesTrigger() {
        return ColumnFamilyOptions.levelZeroStopWritesTrigger(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setTargetFileSizeBase(long l) {
        ColumnFamilyOptions.setTargetFileSizeBase(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long targetFileSizeBase() {
        return ColumnFamilyOptions.targetFileSizeBase(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setTargetFileSizeMultiplier(int n) {
        ColumnFamilyOptions.setTargetFileSizeMultiplier(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int targetFileSizeMultiplier() {
        return ColumnFamilyOptions.targetFileSizeMultiplier(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMaxBytesForLevelBase(long l) {
        ColumnFamilyOptions.setMaxBytesForLevelBase(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxBytesForLevelBase() {
        return ColumnFamilyOptions.maxBytesForLevelBase(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setLevelCompactionDynamicLevelBytes(boolean bl) {
        ColumnFamilyOptions.setLevelCompactionDynamicLevelBytes(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean levelCompactionDynamicLevelBytes() {
        return ColumnFamilyOptions.levelCompactionDynamicLevelBytes(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMaxBytesForLevelMultiplier(double d) {
        ColumnFamilyOptions.setMaxBytesForLevelMultiplier(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double maxBytesForLevelMultiplier() {
        return ColumnFamilyOptions.maxBytesForLevelMultiplier(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMaxCompactionBytes(long l) {
        ColumnFamilyOptions.setMaxCompactionBytes(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxCompactionBytes() {
        return ColumnFamilyOptions.maxCompactionBytes(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setArenaBlockSize(long l) {
        ColumnFamilyOptions.setArenaBlockSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long arenaBlockSize() {
        return ColumnFamilyOptions.arenaBlockSize(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setDisableAutoCompactions(boolean bl) {
        ColumnFamilyOptions.setDisableAutoCompactions(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean disableAutoCompactions() {
        return ColumnFamilyOptions.disableAutoCompactions(this.nativeHandle_);
    }

    public ColumnFamilyOptions setCompactionStyle(CompactionStyle compactionStyle) {
        ColumnFamilyOptions.setCompactionStyle(this.nativeHandle_, compactionStyle.getValue());
        return this;
    }

    @Override
    public CompactionStyle compactionStyle() {
        return CompactionStyle.fromValue(ColumnFamilyOptions.compactionStyle(this.nativeHandle_));
    }

    @Override
    public ColumnFamilyOptions setMaxTableFilesSizeFIFO(long l) {
        assert (l > 0L);
        assert (this.isOwningHandle());
        ColumnFamilyOptions.setMaxTableFilesSizeFIFO(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxTableFilesSizeFIFO() {
        return ColumnFamilyOptions.maxTableFilesSizeFIFO(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMaxSequentialSkipInIterations(long l) {
        ColumnFamilyOptions.setMaxSequentialSkipInIterations(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxSequentialSkipInIterations() {
        return ColumnFamilyOptions.maxSequentialSkipInIterations(this.nativeHandle_);
    }

    @Override
    public MemTableConfig memTableConfig() {
        return this.memTableConfig_;
    }

    @Override
    public ColumnFamilyOptions setMemTableConfig(MemTableConfig memTableConfig) {
        ColumnFamilyOptions.setMemTableFactory(this.nativeHandle_, memTableConfig.newMemTableFactoryHandle());
        this.memTableConfig_ = memTableConfig;
        return this;
    }

    @Override
    public String memTableFactoryName() {
        assert (this.isOwningHandle());
        return ColumnFamilyOptions.memTableFactoryName(this.nativeHandle_);
    }

    @Override
    public TableFormatConfig tableFormatConfig() {
        return this.tableFormatConfig_;
    }

    @Override
    public ColumnFamilyOptions setTableFormatConfig(TableFormatConfig tableFormatConfig) {
        ColumnFamilyOptions.setTableFactory(this.nativeHandle_, tableFormatConfig.newTableFactoryHandle());
        this.tableFormatConfig_ = tableFormatConfig;
        return this;
    }

    void setFetchedTableFormatConfig(TableFormatConfig tableFormatConfig) {
        this.tableFormatConfig_ = tableFormatConfig;
    }

    @Override
    public String tableFactoryName() {
        assert (this.isOwningHandle());
        return ColumnFamilyOptions.tableFactoryName(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setCfPaths(Collection<DbPath> collection) {
        assert (this.isOwningHandle());
        int n = collection.size();
        String[] stringArray = new String[n];
        long[] lArray = new long[n];
        int n2 = 0;
        for (DbPath dbPath : collection) {
            stringArray[n2] = dbPath.path.toString();
            lArray[n2] = dbPath.targetSize;
            ++n2;
        }
        ColumnFamilyOptions.setCfPaths(this.nativeHandle_, stringArray, lArray);
        return this;
    }

    @Override
    public List<DbPath> cfPaths() {
        int n = (int)ColumnFamilyOptions.cfPathsLen(this.nativeHandle_);
        if (n == 0) {
            return Collections.emptyList();
        }
        String[] stringArray = new String[n];
        long[] lArray = new long[n];
        ColumnFamilyOptions.cfPaths(this.nativeHandle_, stringArray, lArray);
        ArrayList<DbPath> arrayList = new ArrayList<DbPath>();
        for (int i = 0; i < n; ++i) {
            arrayList.add(new DbPath(Paths.get(stringArray[i], new String[0]), lArray[i]));
        }
        return arrayList;
    }

    @Override
    public ColumnFamilyOptions setInplaceUpdateSupport(boolean bl) {
        ColumnFamilyOptions.setInplaceUpdateSupport(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean inplaceUpdateSupport() {
        return ColumnFamilyOptions.inplaceUpdateSupport(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setInplaceUpdateNumLocks(long l) {
        ColumnFamilyOptions.setInplaceUpdateNumLocks(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long inplaceUpdateNumLocks() {
        return ColumnFamilyOptions.inplaceUpdateNumLocks(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMemtablePrefixBloomSizeRatio(double d) {
        ColumnFamilyOptions.setMemtablePrefixBloomSizeRatio(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double memtablePrefixBloomSizeRatio() {
        return ColumnFamilyOptions.memtablePrefixBloomSizeRatio(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setExperimentalMempurgeThreshold(double d) {
        ColumnFamilyOptions.setExperimentalMempurgeThreshold(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double experimentalMempurgeThreshold() {
        return ColumnFamilyOptions.experimentalMempurgeThreshold(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMemtableWholeKeyFiltering(boolean bl) {
        ColumnFamilyOptions.setMemtableWholeKeyFiltering(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean memtableWholeKeyFiltering() {
        return ColumnFamilyOptions.memtableWholeKeyFiltering(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setBloomLocality(int n) {
        ColumnFamilyOptions.setBloomLocality(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int bloomLocality() {
        return ColumnFamilyOptions.bloomLocality(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMaxSuccessiveMerges(long l) {
        ColumnFamilyOptions.setMaxSuccessiveMerges(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxSuccessiveMerges() {
        return ColumnFamilyOptions.maxSuccessiveMerges(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setOptimizeFiltersForHits(boolean bl) {
        ColumnFamilyOptions.setOptimizeFiltersForHits(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean optimizeFiltersForHits() {
        return ColumnFamilyOptions.optimizeFiltersForHits(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMemtableHugePageSize(long l) {
        ColumnFamilyOptions.setMemtableHugePageSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long memtableHugePageSize() {
        return ColumnFamilyOptions.memtableHugePageSize(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setSoftPendingCompactionBytesLimit(long l) {
        ColumnFamilyOptions.setSoftPendingCompactionBytesLimit(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long softPendingCompactionBytesLimit() {
        return ColumnFamilyOptions.softPendingCompactionBytesLimit(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setHardPendingCompactionBytesLimit(long l) {
        ColumnFamilyOptions.setHardPendingCompactionBytesLimit(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long hardPendingCompactionBytesLimit() {
        return ColumnFamilyOptions.hardPendingCompactionBytesLimit(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setLevel0FileNumCompactionTrigger(int n) {
        ColumnFamilyOptions.setLevel0FileNumCompactionTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int level0FileNumCompactionTrigger() {
        return ColumnFamilyOptions.level0FileNumCompactionTrigger(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setLevel0SlowdownWritesTrigger(int n) {
        ColumnFamilyOptions.setLevel0SlowdownWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int level0SlowdownWritesTrigger() {
        return ColumnFamilyOptions.level0SlowdownWritesTrigger(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setLevel0StopWritesTrigger(int n) {
        ColumnFamilyOptions.setLevel0StopWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int level0StopWritesTrigger() {
        return ColumnFamilyOptions.level0StopWritesTrigger(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMaxBytesForLevelMultiplierAdditional(int[] nArray) {
        ColumnFamilyOptions.setMaxBytesForLevelMultiplierAdditional(this.nativeHandle_, nArray);
        return this;
    }

    @Override
    public int[] maxBytesForLevelMultiplierAdditional() {
        return ColumnFamilyOptions.maxBytesForLevelMultiplierAdditional(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setParanoidFileChecks(boolean bl) {
        ColumnFamilyOptions.setParanoidFileChecks(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean paranoidFileChecks() {
        return ColumnFamilyOptions.paranoidFileChecks(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setCompactionPriority(CompactionPriority compactionPriority) {
        ColumnFamilyOptions.setCompactionPriority(this.nativeHandle_, compactionPriority.getValue());
        return this;
    }

    @Override
    public CompactionPriority compactionPriority() {
        return CompactionPriority.getCompactionPriority(ColumnFamilyOptions.compactionPriority(this.nativeHandle_));
    }

    @Override
    public ColumnFamilyOptions setReportBgIoStats(boolean bl) {
        ColumnFamilyOptions.setReportBgIoStats(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean reportBgIoStats() {
        return ColumnFamilyOptions.reportBgIoStats(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setTtl(long l) {
        ColumnFamilyOptions.setTtl(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long ttl() {
        return ColumnFamilyOptions.ttl(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setPeriodicCompactionSeconds(long l) {
        ColumnFamilyOptions.setPeriodicCompactionSeconds(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long periodicCompactionSeconds() {
        return ColumnFamilyOptions.periodicCompactionSeconds(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setCompactionOptionsUniversal(CompactionOptionsUniversal compactionOptionsUniversal) {
        ColumnFamilyOptions.setCompactionOptionsUniversal(this.nativeHandle_, compactionOptionsUniversal.nativeHandle_);
        this.compactionOptionsUniversal_ = compactionOptionsUniversal;
        return this;
    }

    @Override
    public CompactionOptionsUniversal compactionOptionsUniversal() {
        return this.compactionOptionsUniversal_;
    }

    @Override
    public ColumnFamilyOptions setCompactionOptionsFIFO(CompactionOptionsFIFO compactionOptionsFIFO) {
        ColumnFamilyOptions.setCompactionOptionsFIFO(this.nativeHandle_, compactionOptionsFIFO.nativeHandle_);
        this.compactionOptionsFIFO_ = compactionOptionsFIFO;
        return this;
    }

    @Override
    public CompactionOptionsFIFO compactionOptionsFIFO() {
        return this.compactionOptionsFIFO_;
    }

    @Override
    public ColumnFamilyOptions setForceConsistencyChecks(boolean bl) {
        ColumnFamilyOptions.setForceConsistencyChecks(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean forceConsistencyChecks() {
        return ColumnFamilyOptions.forceConsistencyChecks(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setSstPartitionerFactory(SstPartitionerFactory sstPartitionerFactory) {
        ColumnFamilyOptions.setSstPartitionerFactory(this.nativeHandle_, sstPartitionerFactory.nativeHandle_);
        this.sstPartitionerFactory_ = sstPartitionerFactory;
        return this;
    }

    @Override
    public ColumnFamilyOptions setCompactionThreadLimiter(ConcurrentTaskLimiter concurrentTaskLimiter) {
        ColumnFamilyOptions.setCompactionThreadLimiter(this.nativeHandle_, concurrentTaskLimiter.nativeHandle_);
        this.compactionThreadLimiter_ = concurrentTaskLimiter;
        return this;
    }

    @Override
    public ConcurrentTaskLimiter compactionThreadLimiter() {
        assert (this.isOwningHandle());
        return this.compactionThreadLimiter_;
    }

    @Override
    public SstPartitionerFactory sstPartitionerFactory() {
        return this.sstPartitionerFactory_;
    }

    @Override
    public ColumnFamilyOptions setMemtableMaxRangeDeletions(int n) {
        ColumnFamilyOptions.setMemtableMaxRangeDeletions(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int memtableMaxRangeDeletions() {
        return ColumnFamilyOptions.memtableMaxRangeDeletions(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setEnableBlobFiles(boolean bl) {
        ColumnFamilyOptions.setEnableBlobFiles(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableBlobFiles() {
        return ColumnFamilyOptions.enableBlobFiles(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setMinBlobSize(long l) {
        ColumnFamilyOptions.setMinBlobSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long minBlobSize() {
        return ColumnFamilyOptions.minBlobSize(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setBlobFileSize(long l) {
        ColumnFamilyOptions.setBlobFileSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long blobFileSize() {
        return ColumnFamilyOptions.blobFileSize(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setBlobCompressionType(CompressionType compressionType) {
        ColumnFamilyOptions.setBlobCompressionType(this.nativeHandle_, compressionType.getValue());
        return this;
    }

    @Override
    public CompressionType blobCompressionType() {
        return CompressionType.values()[ColumnFamilyOptions.blobCompressionType(this.nativeHandle_)];
    }

    @Override
    public ColumnFamilyOptions setEnableBlobGarbageCollection(boolean bl) {
        ColumnFamilyOptions.setEnableBlobGarbageCollection(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableBlobGarbageCollection() {
        return ColumnFamilyOptions.enableBlobGarbageCollection(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setBlobGarbageCollectionAgeCutoff(double d) {
        ColumnFamilyOptions.setBlobGarbageCollectionAgeCutoff(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double blobGarbageCollectionAgeCutoff() {
        return ColumnFamilyOptions.blobGarbageCollectionAgeCutoff(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setBlobGarbageCollectionForceThreshold(double d) {
        ColumnFamilyOptions.setBlobGarbageCollectionForceThreshold(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double blobGarbageCollectionForceThreshold() {
        return ColumnFamilyOptions.blobGarbageCollectionForceThreshold(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setBlobCompactionReadaheadSize(long l) {
        ColumnFamilyOptions.setBlobCompactionReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long blobCompactionReadaheadSize() {
        return ColumnFamilyOptions.blobCompactionReadaheadSize(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setBlobFileStartingLevel(int n) {
        ColumnFamilyOptions.setBlobFileStartingLevel(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int blobFileStartingLevel() {
        return ColumnFamilyOptions.blobFileStartingLevel(this.nativeHandle_);
    }

    @Override
    public ColumnFamilyOptions setPrepopulateBlobCache(PrepopulateBlobCache prepopulateBlobCache) {
        ColumnFamilyOptions.setPrepopulateBlobCache(this.nativeHandle_, prepopulateBlobCache.getValue());
        return this;
    }

    @Override
    public PrepopulateBlobCache prepopulateBlobCache() {
        return PrepopulateBlobCache.getPrepopulateBlobCache(ColumnFamilyOptions.prepopulateBlobCache(this.nativeHandle_));
    }

    private static native long getColumnFamilyOptionsFromProps(long var0, String var2);

    private static native long getColumnFamilyOptionsFromProps(String var0);

    private static long newColumnFamilyOptionsInstance() {
        RocksDB.loadLibrary();
        return ColumnFamilyOptions.newColumnFamilyOptions();
    }

    private static native long newColumnFamilyOptions();

    private static native long copyColumnFamilyOptions(long var0);

    private static native long newColumnFamilyOptionsFromOptions(long var0);

    @Override
    protected final void disposeInternal(long l) {
        ColumnFamilyOptions.disposeInternalJni(l);
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
}

