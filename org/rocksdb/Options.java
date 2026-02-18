/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.rocksdb.AbstractCompactionFilter;
import org.rocksdb.AbstractCompactionFilterFactory;
import org.rocksdb.AbstractComparator;
import org.rocksdb.AbstractEventListener;
import org.rocksdb.AbstractSlice;
import org.rocksdb.AbstractWalFilter;
import org.rocksdb.BuiltinComparator;
import org.rocksdb.Cache;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.ColumnFamilyOptionsInterface;
import org.rocksdb.CompactionOptionsFIFO;
import org.rocksdb.CompactionOptionsUniversal;
import org.rocksdb.CompactionPriority;
import org.rocksdb.CompactionStyle;
import org.rocksdb.CompressionOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.ConcurrentTaskLimiter;
import org.rocksdb.DBOptions;
import org.rocksdb.DBOptionsInterface;
import org.rocksdb.DbPath;
import org.rocksdb.Env;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerInterface;
import org.rocksdb.MemTableConfig;
import org.rocksdb.MergeOperator;
import org.rocksdb.MutableColumnFamilyOptionsInterface;
import org.rocksdb.MutableDBOptionsInterface;
import org.rocksdb.PrepopulateBlobCache;
import org.rocksdb.RateLimiter;
import org.rocksdb.RocksCallbackObject;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;
import org.rocksdb.SstFileManager;
import org.rocksdb.SstPartitionerFactory;
import org.rocksdb.Statistics;
import org.rocksdb.TableFormatConfig;
import org.rocksdb.TablePropertiesCollectorFactory;
import org.rocksdb.WALRecoveryMode;
import org.rocksdb.WalFilter;
import org.rocksdb.WriteBufferManager;

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

    public static String getOptionStringFromProps(Properties properties) {
        if (properties == null || properties.size() == 0) {
            throw new IllegalArgumentException("Properties value must contain at least one value.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : properties.stringPropertyNames()) {
            stringBuilder.append(string);
            stringBuilder.append("=");
            stringBuilder.append(properties.getProperty(string));
            stringBuilder.append(";");
        }
        return stringBuilder.toString();
    }

    public Options() {
        super(Options.newOptionsInstance());
        this.env_ = Env.getDefault();
    }

    public Options(DBOptions dBOptions, ColumnFamilyOptions columnFamilyOptions) {
        super(Options.newOptions(dBOptions.nativeHandle_, columnFamilyOptions.nativeHandle_));
        this.env_ = dBOptions.getEnv() != null ? dBOptions.getEnv() : Env.getDefault();
    }

    public Options(Options options) {
        super(Options.copyOptions(options.nativeHandle_));
        this.env_ = options.env_;
        this.memTableConfig_ = options.memTableConfig_;
        this.tableFormatConfig_ = options.tableFormatConfig_;
        this.rateLimiter_ = options.rateLimiter_;
        this.comparator_ = options.comparator_;
        this.compactionFilter_ = options.compactionFilter_;
        this.compactionFilterFactory_ = options.compactionFilterFactory_;
        this.compactionOptionsUniversal_ = options.compactionOptionsUniversal_;
        this.compactionOptionsFIFO_ = options.compactionOptionsFIFO_;
        this.compressionOptions_ = options.compressionOptions_;
        this.rowCache_ = options.rowCache_;
        this.writeBufferManager_ = options.writeBufferManager_;
        this.compactionThreadLimiter_ = options.compactionThreadLimiter_;
        this.bottommostCompressionOptions_ = options.bottommostCompressionOptions_;
        this.walFilter_ = options.walFilter_;
        this.sstPartitionerFactory_ = options.sstPartitionerFactory_;
    }

    @Override
    public Options setIncreaseParallelism(int n) {
        assert (this.isOwningHandle());
        Options.setIncreaseParallelism(this.nativeHandle_, n);
        return this;
    }

    @Override
    public Options setCreateIfMissing(boolean bl) {
        assert (this.isOwningHandle());
        Options.setCreateIfMissing(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public Options setCreateMissingColumnFamilies(boolean bl) {
        assert (this.isOwningHandle());
        Options.setCreateMissingColumnFamilies(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public Options setEnv(Env env) {
        assert (this.isOwningHandle());
        Options.setEnv(this.nativeHandle_, env.nativeHandle_);
        this.env_ = env;
        return this;
    }

    @Override
    public Env getEnv() {
        return this.env_;
    }

    public Options prepareForBulkLoad() {
        Options.prepareForBulkLoad(this.nativeHandle_);
        return this;
    }

    @Override
    public boolean createIfMissing() {
        assert (this.isOwningHandle());
        return Options.createIfMissing(this.nativeHandle_);
    }

    @Override
    public boolean createMissingColumnFamilies() {
        assert (this.isOwningHandle());
        return Options.createMissingColumnFamilies(this.nativeHandle_);
    }

    @Override
    public Options oldDefaults(int n, int n2) {
        Options.oldDefaults(this.nativeHandle_, n, n2);
        return this;
    }

    @Override
    public Options optimizeForSmallDb() {
        Options.optimizeForSmallDb(this.nativeHandle_);
        return this;
    }

    @Override
    public Options optimizeForSmallDb(Cache cache) {
        Options.optimizeForSmallDb(this.nativeHandle_, cache.getNativeHandle());
        return this;
    }

    @Override
    public Options optimizeForPointLookup(long l) {
        Options.optimizeForPointLookup(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options optimizeLevelStyleCompaction() {
        Options.optimizeLevelStyleCompaction(this.nativeHandle_, 0x20000000L);
        return this;
    }

    @Override
    public Options optimizeLevelStyleCompaction(long l) {
        Options.optimizeLevelStyleCompaction(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options optimizeUniversalStyleCompaction() {
        Options.optimizeUniversalStyleCompaction(this.nativeHandle_, 0x20000000L);
        return this;
    }

    @Override
    public Options optimizeUniversalStyleCompaction(long l) {
        Options.optimizeUniversalStyleCompaction(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options setComparator(BuiltinComparator builtinComparator) {
        assert (this.isOwningHandle());
        Options.setComparatorHandle(this.nativeHandle_, builtinComparator.ordinal());
        return this;
    }

    @Override
    public Options setComparator(AbstractComparator abstractComparator) {
        assert (this.isOwningHandle());
        Options.setComparatorHandle(this.nativeHandle_, abstractComparator.nativeHandle_, abstractComparator.getComparatorType().getValue());
        this.comparator_ = abstractComparator;
        return this;
    }

    @Override
    public Options setMergeOperatorName(String string) {
        assert (this.isOwningHandle());
        if (string == null) {
            throw new IllegalArgumentException("Merge operator name must not be null.");
        }
        Options.setMergeOperatorName(this.nativeHandle_, string);
        return this;
    }

    @Override
    public Options setMergeOperator(MergeOperator mergeOperator) {
        Options.setMergeOperator(this.nativeHandle_, mergeOperator.nativeHandle_);
        return this;
    }

    @Override
    public Options setCompactionFilter(AbstractCompactionFilter<? extends AbstractSlice<?>> abstractCompactionFilter) {
        Options.setCompactionFilterHandle(this.nativeHandle_, abstractCompactionFilter.nativeHandle_);
        this.compactionFilter_ = abstractCompactionFilter;
        return this;
    }

    @Override
    public AbstractCompactionFilter<? extends AbstractSlice<?>> compactionFilter() {
        assert (this.isOwningHandle());
        return this.compactionFilter_;
    }

    @Override
    public Options setCompactionFilterFactory(AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> abstractCompactionFilterFactory) {
        assert (this.isOwningHandle());
        Options.setCompactionFilterFactoryHandle(this.nativeHandle_, abstractCompactionFilterFactory.nativeHandle_);
        this.compactionFilterFactory_ = abstractCompactionFilterFactory;
        return this;
    }

    @Override
    public AbstractCompactionFilterFactory<? extends AbstractCompactionFilter<?>> compactionFilterFactory() {
        assert (this.isOwningHandle());
        return this.compactionFilterFactory_;
    }

    @Override
    public Options setWriteBufferSize(long l) {
        assert (this.isOwningHandle());
        Options.setWriteBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writeBufferSize() {
        assert (this.isOwningHandle());
        return Options.writeBufferSize(this.nativeHandle_);
    }

    @Override
    public Options setMaxWriteBufferNumber(int n) {
        assert (this.isOwningHandle());
        Options.setMaxWriteBufferNumber(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxWriteBufferNumber() {
        assert (this.isOwningHandle());
        return Options.maxWriteBufferNumber(this.nativeHandle_);
    }

    @Override
    public boolean errorIfExists() {
        assert (this.isOwningHandle());
        return Options.errorIfExists(this.nativeHandle_);
    }

    @Override
    public Options setErrorIfExists(boolean bl) {
        assert (this.isOwningHandle());
        Options.setErrorIfExists(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean paranoidChecks() {
        assert (this.isOwningHandle());
        return Options.paranoidChecks(this.nativeHandle_);
    }

    @Override
    public Options setParanoidChecks(boolean bl) {
        assert (this.isOwningHandle());
        Options.setParanoidChecks(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public int maxOpenFiles() {
        assert (this.isOwningHandle());
        return Options.maxOpenFiles(this.nativeHandle_);
    }

    @Override
    public Options setMaxFileOpeningThreads(int n) {
        assert (this.isOwningHandle());
        Options.setMaxFileOpeningThreads(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxFileOpeningThreads() {
        assert (this.isOwningHandle());
        return Options.maxFileOpeningThreads(this.nativeHandle_);
    }

    @Override
    public Options setMaxTotalWalSize(long l) {
        assert (this.isOwningHandle());
        Options.setMaxTotalWalSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxTotalWalSize() {
        assert (this.isOwningHandle());
        return Options.maxTotalWalSize(this.nativeHandle_);
    }

    @Override
    public Options setMaxOpenFiles(int n) {
        assert (this.isOwningHandle());
        Options.setMaxOpenFiles(this.nativeHandle_, n);
        return this;
    }

    @Override
    public boolean useFsync() {
        assert (this.isOwningHandle());
        return Options.useFsync(this.nativeHandle_);
    }

    @Override
    public Options setUseFsync(boolean bl) {
        assert (this.isOwningHandle());
        Options.setUseFsync(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public Options setDbPaths(Collection<DbPath> collection) {
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
        Options.setDbPaths(this.nativeHandle_, stringArray, lArray);
        return this;
    }

    @Override
    public List<DbPath> dbPaths() {
        int n = (int)Options.dbPathsLen(this.nativeHandle_);
        if (n == 0) {
            return Collections.emptyList();
        }
        String[] stringArray = new String[n];
        long[] lArray = new long[n];
        Options.dbPaths(this.nativeHandle_, stringArray, lArray);
        ArrayList<DbPath> arrayList = new ArrayList<DbPath>();
        for (int i = 0; i < n; ++i) {
            arrayList.add(new DbPath(Paths.get(stringArray[i], new String[0]), lArray[i]));
        }
        return arrayList;
    }

    @Override
    public String dbLogDir() {
        assert (this.isOwningHandle());
        return Options.dbLogDir(this.nativeHandle_);
    }

    @Override
    public Options setDbLogDir(String string) {
        assert (this.isOwningHandle());
        Options.setDbLogDir(this.nativeHandle_, string);
        return this;
    }

    @Override
    public String walDir() {
        assert (this.isOwningHandle());
        return Options.walDir(this.nativeHandle_);
    }

    @Override
    public Options setWalDir(String string) {
        assert (this.isOwningHandle());
        Options.setWalDir(this.nativeHandle_, string);
        return this;
    }

    @Override
    public long deleteObsoleteFilesPeriodMicros() {
        assert (this.isOwningHandle());
        return Options.deleteObsoleteFilesPeriodMicros(this.nativeHandle_);
    }

    @Override
    public Options setDeleteObsoleteFilesPeriodMicros(long l) {
        assert (this.isOwningHandle());
        Options.setDeleteObsoleteFilesPeriodMicros(this.nativeHandle_, l);
        return this;
    }

    @Override
    @Deprecated
    public int maxBackgroundCompactions() {
        assert (this.isOwningHandle());
        return Options.maxBackgroundCompactions(this.nativeHandle_);
    }

    @Override
    public Options setStatistics(Statistics statistics) {
        assert (this.isOwningHandle());
        Options.setStatistics(this.nativeHandle_, statistics.nativeHandle_);
        return this;
    }

    @Override
    public Statistics statistics() {
        assert (this.isOwningHandle());
        long l = Options.statistics(this.nativeHandle_);
        if (l == 0L) {
            return null;
        }
        return new Statistics(l);
    }

    @Override
    @Deprecated
    public Options setMaxBackgroundCompactions(int n) {
        assert (this.isOwningHandle());
        Options.setMaxBackgroundCompactions(this.nativeHandle_, n);
        return this;
    }

    @Override
    public Options setMaxSubcompactions(int n) {
        assert (this.isOwningHandle());
        Options.setMaxSubcompactions(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxSubcompactions() {
        assert (this.isOwningHandle());
        return Options.maxSubcompactions(this.nativeHandle_);
    }

    @Override
    @Deprecated
    public int maxBackgroundFlushes() {
        assert (this.isOwningHandle());
        return Options.maxBackgroundFlushes(this.nativeHandle_);
    }

    @Override
    @Deprecated
    public Options setMaxBackgroundFlushes(int n) {
        assert (this.isOwningHandle());
        Options.setMaxBackgroundFlushes(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxBackgroundJobs() {
        assert (this.isOwningHandle());
        return Options.maxBackgroundJobs(this.nativeHandle_);
    }

    @Override
    public Options setMaxBackgroundJobs(int n) {
        assert (this.isOwningHandle());
        Options.setMaxBackgroundJobs(this.nativeHandle_, n);
        return this;
    }

    @Override
    public long maxLogFileSize() {
        assert (this.isOwningHandle());
        return Options.maxLogFileSize(this.nativeHandle_);
    }

    @Override
    public Options setMaxLogFileSize(long l) {
        assert (this.isOwningHandle());
        Options.setMaxLogFileSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long logFileTimeToRoll() {
        assert (this.isOwningHandle());
        return Options.logFileTimeToRoll(this.nativeHandle_);
    }

    @Override
    public Options setLogFileTimeToRoll(long l) {
        assert (this.isOwningHandle());
        Options.setLogFileTimeToRoll(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long keepLogFileNum() {
        assert (this.isOwningHandle());
        return Options.keepLogFileNum(this.nativeHandle_);
    }

    @Override
    public Options setKeepLogFileNum(long l) {
        assert (this.isOwningHandle());
        Options.setKeepLogFileNum(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options setRecycleLogFileNum(long l) {
        assert (this.isOwningHandle());
        Options.setRecycleLogFileNum(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long recycleLogFileNum() {
        assert (this.isOwningHandle());
        return Options.recycleLogFileNum(this.nativeHandle_);
    }

    @Override
    public long maxManifestFileSize() {
        assert (this.isOwningHandle());
        return Options.maxManifestFileSize(this.nativeHandle_);
    }

    @Override
    public Options setMaxManifestFileSize(long l) {
        assert (this.isOwningHandle());
        Options.setMaxManifestFileSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options setMaxTableFilesSizeFIFO(long l) {
        assert (l > 0L);
        assert (this.isOwningHandle());
        Options.setMaxTableFilesSizeFIFO(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxTableFilesSizeFIFO() {
        return Options.maxTableFilesSizeFIFO(this.nativeHandle_);
    }

    @Override
    public int tableCacheNumshardbits() {
        assert (this.isOwningHandle());
        return Options.tableCacheNumshardbits(this.nativeHandle_);
    }

    @Override
    public Options setTableCacheNumshardbits(int n) {
        assert (this.isOwningHandle());
        Options.setTableCacheNumshardbits(this.nativeHandle_, n);
        return this;
    }

    @Override
    public long walTtlSeconds() {
        assert (this.isOwningHandle());
        return Options.walTtlSeconds(this.nativeHandle_);
    }

    @Override
    public Options setWalTtlSeconds(long l) {
        assert (this.isOwningHandle());
        Options.setWalTtlSeconds(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long walSizeLimitMB() {
        assert (this.isOwningHandle());
        return Options.walSizeLimitMB(this.nativeHandle_);
    }

    @Override
    public Options setMaxWriteBatchGroupSizeBytes(long l) {
        Options.setMaxWriteBatchGroupSizeBytes(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxWriteBatchGroupSizeBytes() {
        assert (this.isOwningHandle());
        return Options.maxWriteBatchGroupSizeBytes(this.nativeHandle_);
    }

    @Override
    public Options setWalSizeLimitMB(long l) {
        assert (this.isOwningHandle());
        Options.setWalSizeLimitMB(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long manifestPreallocationSize() {
        assert (this.isOwningHandle());
        return Options.manifestPreallocationSize(this.nativeHandle_);
    }

    @Override
    public Options setManifestPreallocationSize(long l) {
        assert (this.isOwningHandle());
        Options.setManifestPreallocationSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options setUseDirectReads(boolean bl) {
        assert (this.isOwningHandle());
        Options.setUseDirectReads(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean useDirectReads() {
        assert (this.isOwningHandle());
        return Options.useDirectReads(this.nativeHandle_);
    }

    @Override
    public Options setUseDirectIoForFlushAndCompaction(boolean bl) {
        assert (this.isOwningHandle());
        Options.setUseDirectIoForFlushAndCompaction(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean useDirectIoForFlushAndCompaction() {
        assert (this.isOwningHandle());
        return Options.useDirectIoForFlushAndCompaction(this.nativeHandle_);
    }

    @Override
    public Options setAllowFAllocate(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAllowFAllocate(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowFAllocate() {
        assert (this.isOwningHandle());
        return Options.allowFAllocate(this.nativeHandle_);
    }

    @Override
    public boolean allowMmapReads() {
        assert (this.isOwningHandle());
        return Options.allowMmapReads(this.nativeHandle_);
    }

    @Override
    public Options setAllowMmapReads(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAllowMmapReads(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowMmapWrites() {
        assert (this.isOwningHandle());
        return Options.allowMmapWrites(this.nativeHandle_);
    }

    @Override
    public Options setAllowMmapWrites(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAllowMmapWrites(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean isFdCloseOnExec() {
        assert (this.isOwningHandle());
        return Options.isFdCloseOnExec(this.nativeHandle_);
    }

    @Override
    public Options setIsFdCloseOnExec(boolean bl) {
        assert (this.isOwningHandle());
        Options.setIsFdCloseOnExec(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public int statsDumpPeriodSec() {
        assert (this.isOwningHandle());
        return Options.statsDumpPeriodSec(this.nativeHandle_);
    }

    @Override
    public Options setStatsDumpPeriodSec(int n) {
        assert (this.isOwningHandle());
        Options.setStatsDumpPeriodSec(this.nativeHandle_, n);
        return this;
    }

    @Override
    public Options setStatsPersistPeriodSec(int n) {
        assert (this.isOwningHandle());
        Options.setStatsPersistPeriodSec(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int statsPersistPeriodSec() {
        assert (this.isOwningHandle());
        return Options.statsPersistPeriodSec(this.nativeHandle_);
    }

    @Override
    public Options setStatsHistoryBufferSize(long l) {
        assert (this.isOwningHandle());
        Options.setStatsHistoryBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long statsHistoryBufferSize() {
        assert (this.isOwningHandle());
        return Options.statsHistoryBufferSize(this.nativeHandle_);
    }

    @Override
    public boolean adviseRandomOnOpen() {
        return Options.adviseRandomOnOpen(this.nativeHandle_);
    }

    @Override
    public Options setAdviseRandomOnOpen(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAdviseRandomOnOpen(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public Options setDbWriteBufferSize(long l) {
        assert (this.isOwningHandle());
        Options.setDbWriteBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options setWriteBufferManager(WriteBufferManager writeBufferManager) {
        assert (this.isOwningHandle());
        Options.setWriteBufferManager(this.nativeHandle_, writeBufferManager.nativeHandle_);
        this.writeBufferManager_ = writeBufferManager;
        return this;
    }

    @Override
    public WriteBufferManager writeBufferManager() {
        assert (this.isOwningHandle());
        return this.writeBufferManager_;
    }

    @Override
    public long dbWriteBufferSize() {
        assert (this.isOwningHandle());
        return Options.dbWriteBufferSize(this.nativeHandle_);
    }

    @Override
    public Options setCompactionReadaheadSize(long l) {
        assert (this.isOwningHandle());
        Options.setCompactionReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long compactionReadaheadSize() {
        assert (this.isOwningHandle());
        return Options.compactionReadaheadSize(this.nativeHandle_);
    }

    @Override
    public Options setDailyOffpeakTimeUTC(String string) {
        assert (this.isOwningHandle());
        Options.setDailyOffpeakTimeUTC(this.nativeHandle_, string);
        return this;
    }

    @Override
    public String dailyOffpeakTimeUTC() {
        assert (this.isOwningHandle());
        return Options.dailyOffpeakTimeUTC(this.nativeHandle_);
    }

    @Override
    public Options setWritableFileMaxBufferSize(long l) {
        assert (this.isOwningHandle());
        Options.setWritableFileMaxBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writableFileMaxBufferSize() {
        assert (this.isOwningHandle());
        return Options.writableFileMaxBufferSize(this.nativeHandle_);
    }

    @Override
    public boolean useAdaptiveMutex() {
        assert (this.isOwningHandle());
        return Options.useAdaptiveMutex(this.nativeHandle_);
    }

    @Override
    public Options setUseAdaptiveMutex(boolean bl) {
        assert (this.isOwningHandle());
        Options.setUseAdaptiveMutex(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public long bytesPerSync() {
        return Options.bytesPerSync(this.nativeHandle_);
    }

    @Override
    public Options setBytesPerSync(long l) {
        assert (this.isOwningHandle());
        Options.setBytesPerSync(this.nativeHandle_, l);
        return this;
    }

    @Override
    public Options setWalBytesPerSync(long l) {
        assert (this.isOwningHandle());
        Options.setWalBytesPerSync(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long walBytesPerSync() {
        assert (this.isOwningHandle());
        return Options.walBytesPerSync(this.nativeHandle_);
    }

    @Override
    public Options setStrictBytesPerSync(boolean bl) {
        assert (this.isOwningHandle());
        Options.setStrictBytesPerSync(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean strictBytesPerSync() {
        assert (this.isOwningHandle());
        return Options.strictBytesPerSync(this.nativeHandle_);
    }

    @Override
    public Options setListeners(List<AbstractEventListener> list) {
        assert (this.isOwningHandle());
        Options.setEventListeners(this.nativeHandle_, RocksCallbackObject.toNativeHandleList(list));
        return this;
    }

    @Override
    public List<AbstractEventListener> listeners() {
        assert (this.isOwningHandle());
        return Arrays.asList(Options.eventListeners(this.nativeHandle_));
    }

    @Override
    public Options setEnableThreadTracking(boolean bl) {
        assert (this.isOwningHandle());
        Options.setEnableThreadTracking(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableThreadTracking() {
        assert (this.isOwningHandle());
        return Options.enableThreadTracking(this.nativeHandle_);
    }

    @Override
    public Options setDelayedWriteRate(long l) {
        assert (this.isOwningHandle());
        Options.setDelayedWriteRate(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long delayedWriteRate() {
        return Options.delayedWriteRate(this.nativeHandle_);
    }

    @Override
    public Options setEnablePipelinedWrite(boolean bl) {
        Options.setEnablePipelinedWrite(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enablePipelinedWrite() {
        return Options.enablePipelinedWrite(this.nativeHandle_);
    }

    @Override
    public Options setUnorderedWrite(boolean bl) {
        Options.setUnorderedWrite(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean unorderedWrite() {
        return Options.unorderedWrite(this.nativeHandle_);
    }

    @Override
    public Options setAllowConcurrentMemtableWrite(boolean bl) {
        Options.setAllowConcurrentMemtableWrite(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowConcurrentMemtableWrite() {
        return Options.allowConcurrentMemtableWrite(this.nativeHandle_);
    }

    @Override
    public Options setEnableWriteThreadAdaptiveYield(boolean bl) {
        Options.setEnableWriteThreadAdaptiveYield(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableWriteThreadAdaptiveYield() {
        return Options.enableWriteThreadAdaptiveYield(this.nativeHandle_);
    }

    @Override
    public Options setWriteThreadMaxYieldUsec(long l) {
        Options.setWriteThreadMaxYieldUsec(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writeThreadMaxYieldUsec() {
        return Options.writeThreadMaxYieldUsec(this.nativeHandle_);
    }

    @Override
    public Options setWriteThreadSlowYieldUsec(long l) {
        Options.setWriteThreadSlowYieldUsec(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writeThreadSlowYieldUsec() {
        return Options.writeThreadSlowYieldUsec(this.nativeHandle_);
    }

    @Override
    public Options setSkipStatsUpdateOnDbOpen(boolean bl) {
        assert (this.isOwningHandle());
        Options.setSkipStatsUpdateOnDbOpen(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean skipStatsUpdateOnDbOpen() {
        assert (this.isOwningHandle());
        return Options.skipStatsUpdateOnDbOpen(this.nativeHandle_);
    }

    @Override
    public Options setSkipCheckingSstFileSizesOnDbOpen(boolean bl) {
        Options.setSkipCheckingSstFileSizesOnDbOpen(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean skipCheckingSstFileSizesOnDbOpen() {
        assert (this.isOwningHandle());
        return Options.skipCheckingSstFileSizesOnDbOpen(this.nativeHandle_);
    }

    @Override
    public Options setWalRecoveryMode(WALRecoveryMode wALRecoveryMode) {
        assert (this.isOwningHandle());
        Options.setWalRecoveryMode(this.nativeHandle_, wALRecoveryMode.getValue());
        return this;
    }

    @Override
    public WALRecoveryMode walRecoveryMode() {
        assert (this.isOwningHandle());
        return WALRecoveryMode.getWALRecoveryMode(Options.walRecoveryMode(this.nativeHandle_));
    }

    @Override
    public Options setAllow2pc(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAllow2pc(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allow2pc() {
        assert (this.isOwningHandle());
        return Options.allow2pc(this.nativeHandle_);
    }

    @Override
    public Options setRowCache(Cache cache) {
        assert (this.isOwningHandle());
        Options.setRowCache(this.nativeHandle_, cache.nativeHandle_);
        this.rowCache_ = cache;
        return this;
    }

    @Override
    public Cache rowCache() {
        assert (this.isOwningHandle());
        return this.rowCache_;
    }

    @Override
    public Options setWalFilter(AbstractWalFilter abstractWalFilter) {
        assert (this.isOwningHandle());
        Options.setWalFilter(this.nativeHandle_, abstractWalFilter.nativeHandle_);
        this.walFilter_ = abstractWalFilter;
        return this;
    }

    @Override
    public WalFilter walFilter() {
        assert (this.isOwningHandle());
        return this.walFilter_;
    }

    @Override
    public Options setFailIfOptionsFileError(boolean bl) {
        assert (this.isOwningHandle());
        Options.setFailIfOptionsFileError(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean failIfOptionsFileError() {
        assert (this.isOwningHandle());
        return Options.failIfOptionsFileError(this.nativeHandle_);
    }

    @Override
    public Options setDumpMallocStats(boolean bl) {
        assert (this.isOwningHandle());
        Options.setDumpMallocStats(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean dumpMallocStats() {
        assert (this.isOwningHandle());
        return Options.dumpMallocStats(this.nativeHandle_);
    }

    @Override
    public Options setAvoidFlushDuringRecovery(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAvoidFlushDuringRecovery(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean avoidFlushDuringRecovery() {
        assert (this.isOwningHandle());
        return Options.avoidFlushDuringRecovery(this.nativeHandle_);
    }

    @Override
    public Options setAvoidFlushDuringShutdown(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAvoidFlushDuringShutdown(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean avoidFlushDuringShutdown() {
        assert (this.isOwningHandle());
        return Options.avoidFlushDuringShutdown(this.nativeHandle_);
    }

    @Override
    public Options setAllowIngestBehind(boolean bl) {
        assert (this.isOwningHandle());
        Options.setAllowIngestBehind(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowIngestBehind() {
        assert (this.isOwningHandle());
        return Options.allowIngestBehind(this.nativeHandle_);
    }

    @Override
    public Options setTwoWriteQueues(boolean bl) {
        assert (this.isOwningHandle());
        Options.setTwoWriteQueues(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean twoWriteQueues() {
        assert (this.isOwningHandle());
        return Options.twoWriteQueues(this.nativeHandle_);
    }

    @Override
    public Options setManualWalFlush(boolean bl) {
        assert (this.isOwningHandle());
        Options.setManualWalFlush(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean manualWalFlush() {
        assert (this.isOwningHandle());
        return Options.manualWalFlush(this.nativeHandle_);
    }

    @Override
    public MemTableConfig memTableConfig() {
        return this.memTableConfig_;
    }

    @Override
    public Options setMemTableConfig(MemTableConfig memTableConfig) {
        this.memTableConfig_ = memTableConfig;
        Options.setMemTableFactory(this.nativeHandle_, memTableConfig.newMemTableFactoryHandle());
        return this;
    }

    @Override
    public Options setRateLimiter(RateLimiter rateLimiter) {
        assert (this.isOwningHandle());
        this.rateLimiter_ = rateLimiter;
        Options.setRateLimiter(this.nativeHandle_, rateLimiter.nativeHandle_);
        return this;
    }

    @Override
    public Options setSstFileManager(SstFileManager sstFileManager) {
        assert (this.isOwningHandle());
        Options.setSstFileManager(this.nativeHandle_, sstFileManager.nativeHandle_);
        return this;
    }

    @Override
    public Options setLogger(LoggerInterface loggerInterface) {
        assert (this.isOwningHandle());
        Options.setLogger(this.nativeHandle_, loggerInterface.getNativeHandle(), loggerInterface.getLoggerType().getValue());
        return this;
    }

    @Override
    public Options setInfoLogLevel(InfoLogLevel infoLogLevel) {
        assert (this.isOwningHandle());
        Options.setInfoLogLevel(this.nativeHandle_, infoLogLevel.getValue());
        return this;
    }

    @Override
    public InfoLogLevel infoLogLevel() {
        assert (this.isOwningHandle());
        return InfoLogLevel.getInfoLogLevel(Options.infoLogLevel(this.nativeHandle_));
    }

    @Override
    public String memTableFactoryName() {
        assert (this.isOwningHandle());
        return Options.memTableFactoryName(this.nativeHandle_);
    }

    @Override
    public TableFormatConfig tableFormatConfig() {
        return this.tableFormatConfig_;
    }

    @Override
    public Options setTableFormatConfig(TableFormatConfig tableFormatConfig) {
        this.tableFormatConfig_ = tableFormatConfig;
        Options.setTableFactory(this.nativeHandle_, tableFormatConfig.newTableFactoryHandle());
        return this;
    }

    @Override
    public String tableFactoryName() {
        assert (this.isOwningHandle());
        return Options.tableFactoryName(this.nativeHandle_);
    }

    @Override
    public Options setCfPaths(Collection<DbPath> collection) {
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
        Options.setCfPaths(this.nativeHandle_, stringArray, lArray);
        return this;
    }

    @Override
    public List<DbPath> cfPaths() {
        int n = (int)Options.cfPathsLen(this.nativeHandle_);
        if (n == 0) {
            return Collections.emptyList();
        }
        String[] stringArray = new String[n];
        long[] lArray = new long[n];
        Options.cfPaths(this.nativeHandle_, stringArray, lArray);
        ArrayList<DbPath> arrayList = new ArrayList<DbPath>();
        for (int i = 0; i < n; ++i) {
            arrayList.add(new DbPath(Paths.get(stringArray[i], new String[0]), lArray[i]));
        }
        return arrayList;
    }

    @Override
    public Options useFixedLengthPrefixExtractor(int n) {
        assert (this.isOwningHandle());
        Options.useFixedLengthPrefixExtractor(this.nativeHandle_, n);
        return this;
    }

    @Override
    public Options useCappedPrefixExtractor(int n) {
        assert (this.isOwningHandle());
        Options.useCappedPrefixExtractor(this.nativeHandle_, n);
        return this;
    }

    @Override
    public CompressionType compressionType() {
        return CompressionType.getCompressionType(Options.compressionType(this.nativeHandle_));
    }

    @Override
    public Options setCompressionPerLevel(List<CompressionType> list) {
        byte[] byArray = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            byArray[i] = list.get(i).getValue();
        }
        Options.setCompressionPerLevel(this.nativeHandle_, byArray);
        return this;
    }

    @Override
    public List<CompressionType> compressionPerLevel() {
        byte[] byArray = Options.compressionPerLevel(this.nativeHandle_);
        ArrayList<CompressionType> arrayList = new ArrayList<CompressionType>();
        for (byte by : byArray) {
            arrayList.add(CompressionType.getCompressionType(by));
        }
        return arrayList;
    }

    @Override
    public Options setCompressionType(CompressionType compressionType) {
        Options.setCompressionType(this.nativeHandle_, compressionType.getValue());
        return this;
    }

    @Override
    public Options setBottommostCompressionType(CompressionType compressionType) {
        Options.setBottommostCompressionType(this.nativeHandle_, compressionType.getValue());
        return this;
    }

    @Override
    public CompressionType bottommostCompressionType() {
        return CompressionType.getCompressionType(Options.bottommostCompressionType(this.nativeHandle_));
    }

    @Override
    public Options setBottommostCompressionOptions(CompressionOptions compressionOptions) {
        Options.setBottommostCompressionOptions(this.nativeHandle_, compressionOptions.nativeHandle_);
        this.bottommostCompressionOptions_ = compressionOptions;
        return this;
    }

    @Override
    public CompressionOptions bottommostCompressionOptions() {
        return this.bottommostCompressionOptions_;
    }

    @Override
    public Options setCompressionOptions(CompressionOptions compressionOptions) {
        Options.setCompressionOptions(this.nativeHandle_, compressionOptions.nativeHandle_);
        this.compressionOptions_ = compressionOptions;
        return this;
    }

    @Override
    public CompressionOptions compressionOptions() {
        return this.compressionOptions_;
    }

    @Override
    public CompactionStyle compactionStyle() {
        return CompactionStyle.fromValue(Options.compactionStyle(this.nativeHandle_));
    }

    public Options setCompactionStyle(CompactionStyle compactionStyle) {
        Options.setCompactionStyle(this.nativeHandle_, compactionStyle.getValue());
        return this;
    }

    @Override
    public int numLevels() {
        return Options.numLevels(this.nativeHandle_);
    }

    @Override
    public Options setNumLevels(int n) {
        Options.setNumLevels(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int levelZeroFileNumCompactionTrigger() {
        return Options.levelZeroFileNumCompactionTrigger(this.nativeHandle_);
    }

    @Override
    public Options setLevelZeroFileNumCompactionTrigger(int n) {
        Options.setLevelZeroFileNumCompactionTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int levelZeroSlowdownWritesTrigger() {
        return Options.levelZeroSlowdownWritesTrigger(this.nativeHandle_);
    }

    @Override
    public Options setLevelZeroSlowdownWritesTrigger(int n) {
        Options.setLevelZeroSlowdownWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int levelZeroStopWritesTrigger() {
        return Options.levelZeroStopWritesTrigger(this.nativeHandle_);
    }

    @Override
    public Options setLevelZeroStopWritesTrigger(int n) {
        Options.setLevelZeroStopWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public long targetFileSizeBase() {
        return Options.targetFileSizeBase(this.nativeHandle_);
    }

    @Override
    public Options setTargetFileSizeBase(long l) {
        Options.setTargetFileSizeBase(this.nativeHandle_, l);
        return this;
    }

    @Override
    public int targetFileSizeMultiplier() {
        return Options.targetFileSizeMultiplier(this.nativeHandle_);
    }

    @Override
    public Options setTargetFileSizeMultiplier(int n) {
        Options.setTargetFileSizeMultiplier(this.nativeHandle_, n);
        return this;
    }

    @Override
    public Options setMaxBytesForLevelBase(long l) {
        Options.setMaxBytesForLevelBase(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxBytesForLevelBase() {
        return Options.maxBytesForLevelBase(this.nativeHandle_);
    }

    @Override
    public Options setLevelCompactionDynamicLevelBytes(boolean bl) {
        Options.setLevelCompactionDynamicLevelBytes(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean levelCompactionDynamicLevelBytes() {
        return Options.levelCompactionDynamicLevelBytes(this.nativeHandle_);
    }

    @Override
    public double maxBytesForLevelMultiplier() {
        return Options.maxBytesForLevelMultiplier(this.nativeHandle_);
    }

    @Override
    public Options setMaxBytesForLevelMultiplier(double d) {
        Options.setMaxBytesForLevelMultiplier(this.nativeHandle_, d);
        return this;
    }

    @Override
    public long maxCompactionBytes() {
        return Options.maxCompactionBytes(this.nativeHandle_);
    }

    @Override
    public Options setMaxCompactionBytes(long l) {
        Options.setMaxCompactionBytes(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long arenaBlockSize() {
        return Options.arenaBlockSize(this.nativeHandle_);
    }

    @Override
    public Options setArenaBlockSize(long l) {
        Options.setArenaBlockSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public boolean disableAutoCompactions() {
        return Options.disableAutoCompactions(this.nativeHandle_);
    }

    @Override
    public Options setDisableAutoCompactions(boolean bl) {
        Options.setDisableAutoCompactions(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public long maxSequentialSkipInIterations() {
        return Options.maxSequentialSkipInIterations(this.nativeHandle_);
    }

    @Override
    public Options setMaxSequentialSkipInIterations(long l) {
        Options.setMaxSequentialSkipInIterations(this.nativeHandle_, l);
        return this;
    }

    @Override
    public boolean inplaceUpdateSupport() {
        return Options.inplaceUpdateSupport(this.nativeHandle_);
    }

    @Override
    public Options setInplaceUpdateSupport(boolean bl) {
        Options.setInplaceUpdateSupport(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public long inplaceUpdateNumLocks() {
        return Options.inplaceUpdateNumLocks(this.nativeHandle_);
    }

    @Override
    public Options setInplaceUpdateNumLocks(long l) {
        Options.setInplaceUpdateNumLocks(this.nativeHandle_, l);
        return this;
    }

    @Override
    public double memtablePrefixBloomSizeRatio() {
        return Options.memtablePrefixBloomSizeRatio(this.nativeHandle_);
    }

    @Override
    public Options setMemtablePrefixBloomSizeRatio(double d) {
        Options.setMemtablePrefixBloomSizeRatio(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double experimentalMempurgeThreshold() {
        return Options.experimentalMempurgeThreshold(this.nativeHandle_);
    }

    @Override
    public Options setExperimentalMempurgeThreshold(double d) {
        Options.setExperimentalMempurgeThreshold(this.nativeHandle_, d);
        return this;
    }

    @Override
    public boolean memtableWholeKeyFiltering() {
        return Options.memtableWholeKeyFiltering(this.nativeHandle_);
    }

    @Override
    public Options setMemtableWholeKeyFiltering(boolean bl) {
        Options.setMemtableWholeKeyFiltering(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public int bloomLocality() {
        return Options.bloomLocality(this.nativeHandle_);
    }

    @Override
    public Options setBloomLocality(int n) {
        Options.setBloomLocality(this.nativeHandle_, n);
        return this;
    }

    @Override
    public long maxSuccessiveMerges() {
        return Options.maxSuccessiveMerges(this.nativeHandle_);
    }

    @Override
    public Options setMaxSuccessiveMerges(long l) {
        Options.setMaxSuccessiveMerges(this.nativeHandle_, l);
        return this;
    }

    @Override
    public int minWriteBufferNumberToMerge() {
        return Options.minWriteBufferNumberToMerge(this.nativeHandle_);
    }

    @Override
    public Options setMinWriteBufferNumberToMerge(int n) {
        Options.setMinWriteBufferNumberToMerge(this.nativeHandle_, n);
        return this;
    }

    @Override
    public Options setOptimizeFiltersForHits(boolean bl) {
        Options.setOptimizeFiltersForHits(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean optimizeFiltersForHits() {
        return Options.optimizeFiltersForHits(this.nativeHandle_);
    }

    @Override
    public Options setMemtableHugePageSize(long l) {
        Options.setMemtableHugePageSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long memtableHugePageSize() {
        return Options.memtableHugePageSize(this.nativeHandle_);
    }

    @Override
    public Options setSoftPendingCompactionBytesLimit(long l) {
        Options.setSoftPendingCompactionBytesLimit(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long softPendingCompactionBytesLimit() {
        return Options.softPendingCompactionBytesLimit(this.nativeHandle_);
    }

    @Override
    public Options setHardPendingCompactionBytesLimit(long l) {
        Options.setHardPendingCompactionBytesLimit(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long hardPendingCompactionBytesLimit() {
        return Options.hardPendingCompactionBytesLimit(this.nativeHandle_);
    }

    @Override
    public Options setLevel0FileNumCompactionTrigger(int n) {
        Options.setLevel0FileNumCompactionTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int level0FileNumCompactionTrigger() {
        return Options.level0FileNumCompactionTrigger(this.nativeHandle_);
    }

    @Override
    public Options setLevel0SlowdownWritesTrigger(int n) {
        Options.setLevel0SlowdownWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int level0SlowdownWritesTrigger() {
        return Options.level0SlowdownWritesTrigger(this.nativeHandle_);
    }

    @Override
    public Options setLevel0StopWritesTrigger(int n) {
        Options.setLevel0StopWritesTrigger(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int level0StopWritesTrigger() {
        return Options.level0StopWritesTrigger(this.nativeHandle_);
    }

    @Override
    public Options setMaxBytesForLevelMultiplierAdditional(int[] nArray) {
        Options.setMaxBytesForLevelMultiplierAdditional(this.nativeHandle_, nArray);
        return this;
    }

    @Override
    public int[] maxBytesForLevelMultiplierAdditional() {
        return Options.maxBytesForLevelMultiplierAdditional(this.nativeHandle_);
    }

    @Override
    public Options setParanoidFileChecks(boolean bl) {
        Options.setParanoidFileChecks(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean paranoidFileChecks() {
        return Options.paranoidFileChecks(this.nativeHandle_);
    }

    @Override
    public Options setCompactionPriority(CompactionPriority compactionPriority) {
        Options.setCompactionPriority(this.nativeHandle_, compactionPriority.getValue());
        return this;
    }

    @Override
    public CompactionPriority compactionPriority() {
        return CompactionPriority.getCompactionPriority(Options.compactionPriority(this.nativeHandle_));
    }

    @Override
    public Options setReportBgIoStats(boolean bl) {
        Options.setReportBgIoStats(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean reportBgIoStats() {
        return Options.reportBgIoStats(this.nativeHandle_);
    }

    @Override
    public Options setTtl(long l) {
        Options.setTtl(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long ttl() {
        return Options.ttl(this.nativeHandle_);
    }

    @Override
    public Options setPeriodicCompactionSeconds(long l) {
        Options.setPeriodicCompactionSeconds(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long periodicCompactionSeconds() {
        return Options.periodicCompactionSeconds(this.nativeHandle_);
    }

    @Override
    public Options setCompactionOptionsUniversal(CompactionOptionsUniversal compactionOptionsUniversal) {
        Options.setCompactionOptionsUniversal(this.nativeHandle_, compactionOptionsUniversal.nativeHandle_);
        this.compactionOptionsUniversal_ = compactionOptionsUniversal;
        return this;
    }

    @Override
    public CompactionOptionsUniversal compactionOptionsUniversal() {
        return this.compactionOptionsUniversal_;
    }

    @Override
    public Options setCompactionOptionsFIFO(CompactionOptionsFIFO compactionOptionsFIFO) {
        Options.setCompactionOptionsFIFO(this.nativeHandle_, compactionOptionsFIFO.nativeHandle_);
        this.compactionOptionsFIFO_ = compactionOptionsFIFO;
        return this;
    }

    @Override
    public CompactionOptionsFIFO compactionOptionsFIFO() {
        return this.compactionOptionsFIFO_;
    }

    @Override
    public Options setForceConsistencyChecks(boolean bl) {
        Options.setForceConsistencyChecks(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean forceConsistencyChecks() {
        return Options.forceConsistencyChecks(this.nativeHandle_);
    }

    @Override
    public Options setAtomicFlush(boolean bl) {
        Options.setAtomicFlush(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean atomicFlush() {
        return Options.atomicFlush(this.nativeHandle_);
    }

    @Override
    public Options setAvoidUnnecessaryBlockingIO(boolean bl) {
        Options.setAvoidUnnecessaryBlockingIO(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean avoidUnnecessaryBlockingIO() {
        assert (this.isOwningHandle());
        return Options.avoidUnnecessaryBlockingIO(this.nativeHandle_);
    }

    @Override
    public Options setPersistStatsToDisk(boolean bl) {
        Options.setPersistStatsToDisk(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean persistStatsToDisk() {
        assert (this.isOwningHandle());
        return Options.persistStatsToDisk(this.nativeHandle_);
    }

    @Override
    public Options setWriteDbidToManifest(boolean bl) {
        Options.setWriteDbidToManifest(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean writeDbidToManifest() {
        assert (this.isOwningHandle());
        return Options.writeDbidToManifest(this.nativeHandle_);
    }

    @Override
    public Options setLogReadaheadSize(long l) {
        Options.setLogReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long logReadaheadSize() {
        assert (this.isOwningHandle());
        return Options.logReadaheadSize(this.nativeHandle_);
    }

    @Override
    public Options setBestEffortsRecovery(boolean bl) {
        Options.setBestEffortsRecovery(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean bestEffortsRecovery() {
        assert (this.isOwningHandle());
        return Options.bestEffortsRecovery(this.nativeHandle_);
    }

    @Override
    public Options setMaxBgErrorResumeCount(int n) {
        Options.setMaxBgErrorResumeCount(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxBgerrorResumeCount() {
        assert (this.isOwningHandle());
        return Options.maxBgerrorResumeCount(this.nativeHandle_);
    }

    @Override
    public Options setBgerrorResumeRetryInterval(long l) {
        Options.setBgerrorResumeRetryInterval(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long bgerrorResumeRetryInterval() {
        assert (this.isOwningHandle());
        return Options.bgerrorResumeRetryInterval(this.nativeHandle_);
    }

    @Override
    public Options setSstPartitionerFactory(SstPartitionerFactory sstPartitionerFactory) {
        Options.setSstPartitionerFactory(this.nativeHandle_, sstPartitionerFactory.nativeHandle_);
        this.sstPartitionerFactory_ = sstPartitionerFactory;
        return this;
    }

    @Override
    public SstPartitionerFactory sstPartitionerFactory() {
        return this.sstPartitionerFactory_;
    }

    @Override
    public Options setMemtableMaxRangeDeletions(int n) {
        Options.setMemtableMaxRangeDeletions(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int memtableMaxRangeDeletions() {
        return Options.memtableMaxRangeDeletions(this.nativeHandle_);
    }

    @Override
    public Options setCompactionThreadLimiter(ConcurrentTaskLimiter concurrentTaskLimiter) {
        Options.setCompactionThreadLimiter(this.nativeHandle_, concurrentTaskLimiter.nativeHandle_);
        this.compactionThreadLimiter_ = concurrentTaskLimiter;
        return this;
    }

    @Override
    public ConcurrentTaskLimiter compactionThreadLimiter() {
        assert (this.isOwningHandle());
        return this.compactionThreadLimiter_;
    }

    @Override
    public Options setEnableBlobFiles(boolean bl) {
        Options.setEnableBlobFiles(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableBlobFiles() {
        return Options.enableBlobFiles(this.nativeHandle_);
    }

    @Override
    public Options setMinBlobSize(long l) {
        Options.setMinBlobSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long minBlobSize() {
        return Options.minBlobSize(this.nativeHandle_);
    }

    @Override
    public Options setBlobFileSize(long l) {
        Options.setBlobFileSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long blobFileSize() {
        return Options.blobFileSize(this.nativeHandle_);
    }

    @Override
    public Options setBlobCompressionType(CompressionType compressionType) {
        Options.setBlobCompressionType(this.nativeHandle_, compressionType.getValue());
        return this;
    }

    @Override
    public CompressionType blobCompressionType() {
        return CompressionType.values()[Options.blobCompressionType(this.nativeHandle_)];
    }

    @Override
    public Options setEnableBlobGarbageCollection(boolean bl) {
        Options.setEnableBlobGarbageCollection(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableBlobGarbageCollection() {
        return Options.enableBlobGarbageCollection(this.nativeHandle_);
    }

    @Override
    public Options setBlobGarbageCollectionAgeCutoff(double d) {
        Options.setBlobGarbageCollectionAgeCutoff(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double blobGarbageCollectionAgeCutoff() {
        return Options.blobGarbageCollectionAgeCutoff(this.nativeHandle_);
    }

    @Override
    public Options setBlobGarbageCollectionForceThreshold(double d) {
        Options.setBlobGarbageCollectionForceThreshold(this.nativeHandle_, d);
        return this;
    }

    @Override
    public double blobGarbageCollectionForceThreshold() {
        return Options.blobGarbageCollectionForceThreshold(this.nativeHandle_);
    }

    @Override
    public Options setBlobCompactionReadaheadSize(long l) {
        Options.setBlobCompactionReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long blobCompactionReadaheadSize() {
        return Options.blobCompactionReadaheadSize(this.nativeHandle_);
    }

    @Override
    public Options setBlobFileStartingLevel(int n) {
        Options.setBlobFileStartingLevel(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int blobFileStartingLevel() {
        return Options.blobFileStartingLevel(this.nativeHandle_);
    }

    @Override
    public Options setPrepopulateBlobCache(PrepopulateBlobCache prepopulateBlobCache) {
        Options.setPrepopulateBlobCache(this.nativeHandle_, prepopulateBlobCache.getValue());
        return this;
    }

    @Override
    public PrepopulateBlobCache prepopulateBlobCache() {
        return PrepopulateBlobCache.getPrepopulateBlobCache(Options.prepopulateBlobCache(this.nativeHandle_));
    }

    public List<TablePropertiesCollectorFactory> tablePropertiesCollectorFactory() {
        long[] lArray = Options.tablePropertiesCollectorFactory(this.nativeHandle_);
        return Arrays.stream(lArray).mapToObj(l -> TablePropertiesCollectorFactory.newWrapper(l)).collect(Collectors.toList());
    }

    public void setTablePropertiesCollectorFactory(List<TablePropertiesCollectorFactory> list) {
        long[] lArray = new long[list.size()];
        for (int i = 0; i < lArray.length; ++i) {
            lArray[i] = list.get(i).getNativeHandle();
        }
        Options.setTablePropertiesCollectorFactory(this.nativeHandle_, lArray);
    }

    private static long newOptionsInstance() {
        RocksDB.loadLibrary();
        return Options.newOptions();
    }

    private static native long newOptions();

    private static native long newOptions(long var0, long var2);

    private static native long copyOptions(long var0);

    @Override
    protected final void disposeInternal(long l) {
        Options.disposeInternalJni(l);
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

