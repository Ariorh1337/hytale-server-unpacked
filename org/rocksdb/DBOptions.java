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
import org.rocksdb.AbstractEventListener;
import org.rocksdb.AbstractWalFilter;
import org.rocksdb.Cache;
import org.rocksdb.ConfigOptions;
import org.rocksdb.DBOptionsInterface;
import org.rocksdb.DbPath;
import org.rocksdb.Env;
import org.rocksdb.InfoLogLevel;
import org.rocksdb.LoggerInterface;
import org.rocksdb.MutableDBOptionsInterface;
import org.rocksdb.Options;
import org.rocksdb.RateLimiter;
import org.rocksdb.RocksCallbackObject;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksObject;
import org.rocksdb.SstFileManager;
import org.rocksdb.Statistics;
import org.rocksdb.WALRecoveryMode;
import org.rocksdb.WalFilter;
import org.rocksdb.WriteBufferManager;

public class DBOptions
extends RocksObject
implements DBOptionsInterface<DBOptions>,
MutableDBOptionsInterface<DBOptions> {
    static final int DEFAULT_NUM_SHARD_BITS = -1;
    private Env env_;
    private int numShardBits_;
    private RateLimiter rateLimiter_;
    private Cache rowCache_;
    private WalFilter walFilter_;
    private WriteBufferManager writeBufferManager_;

    public DBOptions() {
        super(DBOptions.newDBOptionsInstance());
        this.numShardBits_ = -1;
        this.env_ = Env.getDefault();
    }

    public DBOptions(DBOptions dBOptions) {
        super(DBOptions.copyDBOptions(dBOptions.nativeHandle_));
        this.env_ = dBOptions.env_;
        this.numShardBits_ = dBOptions.numShardBits_;
        this.rateLimiter_ = dBOptions.rateLimiter_;
        this.rowCache_ = dBOptions.rowCache_;
        this.walFilter_ = dBOptions.walFilter_;
        this.writeBufferManager_ = dBOptions.writeBufferManager_;
    }

    public DBOptions(Options options) {
        super(DBOptions.newDBOptionsFromOptions(options.nativeHandle_));
    }

    public static DBOptions getDBOptionsFromProps(ConfigOptions configOptions, Properties properties) {
        DBOptions dBOptions = null;
        String string = Options.getOptionStringFromProps(properties);
        long l = DBOptions.getDBOptionsFromProps(configOptions.nativeHandle_, string);
        if (l != 0L) {
            dBOptions = new DBOptions(l);
        }
        return dBOptions;
    }

    public static DBOptions getDBOptionsFromProps(Properties properties) {
        DBOptions dBOptions = null;
        String string = Options.getOptionStringFromProps(properties);
        long l = DBOptions.getDBOptionsFromProps(string);
        if (l != 0L) {
            dBOptions = new DBOptions(l);
        }
        return dBOptions;
    }

    @Override
    public DBOptions optimizeForSmallDb() {
        DBOptions.optimizeForSmallDb(this.nativeHandle_);
        return this;
    }

    @Override
    public DBOptions setIncreaseParallelism(int n) {
        assert (this.isOwningHandle());
        DBOptions.setIncreaseParallelism(this.nativeHandle_, n);
        return this;
    }

    @Override
    public DBOptions setCreateIfMissing(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setCreateIfMissing(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean createIfMissing() {
        assert (this.isOwningHandle());
        return DBOptions.createIfMissing(this.nativeHandle_);
    }

    @Override
    public DBOptions setCreateMissingColumnFamilies(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setCreateMissingColumnFamilies(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean createMissingColumnFamilies() {
        assert (this.isOwningHandle());
        return DBOptions.createMissingColumnFamilies(this.nativeHandle_);
    }

    @Override
    public DBOptions setErrorIfExists(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setErrorIfExists(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean errorIfExists() {
        assert (this.isOwningHandle());
        return DBOptions.errorIfExists(this.nativeHandle_);
    }

    @Override
    public DBOptions setParanoidChecks(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setParanoidChecks(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean paranoidChecks() {
        assert (this.isOwningHandle());
        return DBOptions.paranoidChecks(this.nativeHandle_);
    }

    @Override
    public DBOptions setEnv(Env env) {
        DBOptions.setEnv(this.nativeHandle_, env.nativeHandle_);
        this.env_ = env;
        return this;
    }

    @Override
    public Env getEnv() {
        return this.env_;
    }

    @Override
    public DBOptions setRateLimiter(RateLimiter rateLimiter) {
        assert (this.isOwningHandle());
        this.rateLimiter_ = rateLimiter;
        DBOptions.setRateLimiter(this.nativeHandle_, rateLimiter.nativeHandle_);
        return this;
    }

    @Override
    public DBOptions setSstFileManager(SstFileManager sstFileManager) {
        assert (this.isOwningHandle());
        DBOptions.setSstFileManager(this.nativeHandle_, sstFileManager.nativeHandle_);
        return this;
    }

    @Override
    public DBOptions setLogger(LoggerInterface loggerInterface) {
        assert (this.isOwningHandle());
        DBOptions.setLogger(this.nativeHandle_, loggerInterface.getNativeHandle(), loggerInterface.getLoggerType().getValue());
        return this;
    }

    @Override
    public DBOptions setInfoLogLevel(InfoLogLevel infoLogLevel) {
        assert (this.isOwningHandle());
        DBOptions.setInfoLogLevel(this.nativeHandle_, infoLogLevel.getValue());
        return this;
    }

    @Override
    public InfoLogLevel infoLogLevel() {
        assert (this.isOwningHandle());
        return InfoLogLevel.getInfoLogLevel(DBOptions.infoLogLevel(this.nativeHandle_));
    }

    @Override
    public DBOptions setMaxOpenFiles(int n) {
        assert (this.isOwningHandle());
        DBOptions.setMaxOpenFiles(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxOpenFiles() {
        assert (this.isOwningHandle());
        return DBOptions.maxOpenFiles(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxFileOpeningThreads(int n) {
        assert (this.isOwningHandle());
        DBOptions.setMaxFileOpeningThreads(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxFileOpeningThreads() {
        assert (this.isOwningHandle());
        return DBOptions.maxFileOpeningThreads(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxTotalWalSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setMaxTotalWalSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxTotalWalSize() {
        assert (this.isOwningHandle());
        return DBOptions.maxTotalWalSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setStatistics(Statistics statistics) {
        assert (this.isOwningHandle());
        DBOptions.setStatistics(this.nativeHandle_, statistics.nativeHandle_);
        return this;
    }

    @Override
    public Statistics statistics() {
        assert (this.isOwningHandle());
        long l = DBOptions.statistics(this.nativeHandle_);
        if (l == 0L) {
            return null;
        }
        return new Statistics(l);
    }

    @Override
    public DBOptions setUseFsync(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setUseFsync(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean useFsync() {
        assert (this.isOwningHandle());
        return DBOptions.useFsync(this.nativeHandle_);
    }

    @Override
    public DBOptions setDbPaths(Collection<DbPath> collection) {
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
        DBOptions.setDbPaths(this.nativeHandle_, stringArray, lArray);
        return this;
    }

    @Override
    public List<DbPath> dbPaths() {
        int n = (int)DBOptions.dbPathsLen(this.nativeHandle_);
        if (n == 0) {
            return Collections.emptyList();
        }
        String[] stringArray = new String[n];
        long[] lArray = new long[n];
        DBOptions.dbPaths(this.nativeHandle_, stringArray, lArray);
        ArrayList<DbPath> arrayList = new ArrayList<DbPath>();
        for (int i = 0; i < n; ++i) {
            arrayList.add(new DbPath(Paths.get(stringArray[i], new String[0]), lArray[i]));
        }
        return arrayList;
    }

    @Override
    public DBOptions setDbLogDir(String string) {
        assert (this.isOwningHandle());
        DBOptions.setDbLogDir(this.nativeHandle_, string);
        return this;
    }

    @Override
    public String dbLogDir() {
        assert (this.isOwningHandle());
        return DBOptions.dbLogDir(this.nativeHandle_);
    }

    @Override
    public DBOptions setWalDir(String string) {
        assert (this.isOwningHandle());
        DBOptions.setWalDir(this.nativeHandle_, string);
        return this;
    }

    @Override
    public String walDir() {
        assert (this.isOwningHandle());
        return DBOptions.walDir(this.nativeHandle_);
    }

    @Override
    public DBOptions setDeleteObsoleteFilesPeriodMicros(long l) {
        assert (this.isOwningHandle());
        DBOptions.setDeleteObsoleteFilesPeriodMicros(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long deleteObsoleteFilesPeriodMicros() {
        assert (this.isOwningHandle());
        return DBOptions.deleteObsoleteFilesPeriodMicros(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxBackgroundJobs(int n) {
        assert (this.isOwningHandle());
        DBOptions.setMaxBackgroundJobs(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxBackgroundJobs() {
        assert (this.isOwningHandle());
        return DBOptions.maxBackgroundJobs(this.nativeHandle_);
    }

    @Override
    @Deprecated
    public DBOptions setMaxBackgroundCompactions(int n) {
        assert (this.isOwningHandle());
        DBOptions.setMaxBackgroundCompactions(this.nativeHandle_, n);
        return this;
    }

    @Override
    @Deprecated
    public int maxBackgroundCompactions() {
        assert (this.isOwningHandle());
        return DBOptions.maxBackgroundCompactions(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxSubcompactions(int n) {
        assert (this.isOwningHandle());
        DBOptions.setMaxSubcompactions(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxSubcompactions() {
        assert (this.isOwningHandle());
        return DBOptions.maxSubcompactions(this.nativeHandle_);
    }

    @Override
    @Deprecated
    public DBOptions setMaxBackgroundFlushes(int n) {
        assert (this.isOwningHandle());
        DBOptions.setMaxBackgroundFlushes(this.nativeHandle_, n);
        return this;
    }

    @Override
    @Deprecated
    public int maxBackgroundFlushes() {
        assert (this.isOwningHandle());
        return DBOptions.maxBackgroundFlushes(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxLogFileSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setMaxLogFileSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxLogFileSize() {
        assert (this.isOwningHandle());
        return DBOptions.maxLogFileSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setLogFileTimeToRoll(long l) {
        assert (this.isOwningHandle());
        DBOptions.setLogFileTimeToRoll(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long logFileTimeToRoll() {
        assert (this.isOwningHandle());
        return DBOptions.logFileTimeToRoll(this.nativeHandle_);
    }

    @Override
    public DBOptions setKeepLogFileNum(long l) {
        assert (this.isOwningHandle());
        DBOptions.setKeepLogFileNum(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long keepLogFileNum() {
        assert (this.isOwningHandle());
        return DBOptions.keepLogFileNum(this.nativeHandle_);
    }

    @Override
    public DBOptions setRecycleLogFileNum(long l) {
        assert (this.isOwningHandle());
        DBOptions.setRecycleLogFileNum(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long recycleLogFileNum() {
        assert (this.isOwningHandle());
        return DBOptions.recycleLogFileNum(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxManifestFileSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setMaxManifestFileSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxManifestFileSize() {
        assert (this.isOwningHandle());
        return DBOptions.maxManifestFileSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setTableCacheNumshardbits(int n) {
        assert (this.isOwningHandle());
        DBOptions.setTableCacheNumshardbits(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int tableCacheNumshardbits() {
        assert (this.isOwningHandle());
        return DBOptions.tableCacheNumshardbits(this.nativeHandle_);
    }

    @Override
    public DBOptions setWalTtlSeconds(long l) {
        assert (this.isOwningHandle());
        DBOptions.setWalTtlSeconds(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long walTtlSeconds() {
        assert (this.isOwningHandle());
        return DBOptions.walTtlSeconds(this.nativeHandle_);
    }

    @Override
    public DBOptions setWalSizeLimitMB(long l) {
        assert (this.isOwningHandle());
        DBOptions.setWalSizeLimitMB(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long walSizeLimitMB() {
        assert (this.isOwningHandle());
        return DBOptions.walSizeLimitMB(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxWriteBatchGroupSizeBytes(long l) {
        DBOptions.setMaxWriteBatchGroupSizeBytes(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long maxWriteBatchGroupSizeBytes() {
        assert (this.isOwningHandle());
        return DBOptions.maxWriteBatchGroupSizeBytes(this.nativeHandle_);
    }

    @Override
    public DBOptions setManifestPreallocationSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setManifestPreallocationSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long manifestPreallocationSize() {
        assert (this.isOwningHandle());
        return DBOptions.manifestPreallocationSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setAllowMmapReads(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAllowMmapReads(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowMmapReads() {
        assert (this.isOwningHandle());
        return DBOptions.allowMmapReads(this.nativeHandle_);
    }

    @Override
    public DBOptions setAllowMmapWrites(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAllowMmapWrites(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowMmapWrites() {
        assert (this.isOwningHandle());
        return DBOptions.allowMmapWrites(this.nativeHandle_);
    }

    @Override
    public DBOptions setUseDirectReads(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setUseDirectReads(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean useDirectReads() {
        assert (this.isOwningHandle());
        return DBOptions.useDirectReads(this.nativeHandle_);
    }

    @Override
    public DBOptions setUseDirectIoForFlushAndCompaction(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setUseDirectIoForFlushAndCompaction(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean useDirectIoForFlushAndCompaction() {
        assert (this.isOwningHandle());
        return DBOptions.useDirectIoForFlushAndCompaction(this.nativeHandle_);
    }

    @Override
    public DBOptions setAllowFAllocate(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAllowFAllocate(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowFAllocate() {
        assert (this.isOwningHandle());
        return DBOptions.allowFAllocate(this.nativeHandle_);
    }

    @Override
    public DBOptions setIsFdCloseOnExec(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setIsFdCloseOnExec(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean isFdCloseOnExec() {
        assert (this.isOwningHandle());
        return DBOptions.isFdCloseOnExec(this.nativeHandle_);
    }

    @Override
    public DBOptions setStatsDumpPeriodSec(int n) {
        assert (this.isOwningHandle());
        DBOptions.setStatsDumpPeriodSec(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int statsDumpPeriodSec() {
        assert (this.isOwningHandle());
        return DBOptions.statsDumpPeriodSec(this.nativeHandle_);
    }

    @Override
    public DBOptions setStatsPersistPeriodSec(int n) {
        assert (this.isOwningHandle());
        DBOptions.setStatsPersistPeriodSec(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int statsPersistPeriodSec() {
        assert (this.isOwningHandle());
        return DBOptions.statsPersistPeriodSec(this.nativeHandle_);
    }

    @Override
    public DBOptions setStatsHistoryBufferSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setStatsHistoryBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long statsHistoryBufferSize() {
        assert (this.isOwningHandle());
        return DBOptions.statsHistoryBufferSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setAdviseRandomOnOpen(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAdviseRandomOnOpen(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean adviseRandomOnOpen() {
        return DBOptions.adviseRandomOnOpen(this.nativeHandle_);
    }

    @Override
    public DBOptions setDbWriteBufferSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setDbWriteBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public DBOptions setWriteBufferManager(WriteBufferManager writeBufferManager) {
        assert (this.isOwningHandle());
        DBOptions.setWriteBufferManager(this.nativeHandle_, writeBufferManager.nativeHandle_);
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
        return DBOptions.dbWriteBufferSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setCompactionReadaheadSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setCompactionReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long compactionReadaheadSize() {
        assert (this.isOwningHandle());
        return DBOptions.compactionReadaheadSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setDailyOffpeakTimeUTC(String string) {
        assert (this.isOwningHandle());
        DBOptions.setDailyOffpeakTimeUTC(this.nativeHandle_, string);
        return this;
    }

    @Override
    public String dailyOffpeakTimeUTC() {
        assert (this.isOwningHandle());
        return DBOptions.dailyOffpeakTimeUTC(this.nativeHandle_);
    }

    @Override
    public DBOptions setWritableFileMaxBufferSize(long l) {
        assert (this.isOwningHandle());
        DBOptions.setWritableFileMaxBufferSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writableFileMaxBufferSize() {
        assert (this.isOwningHandle());
        return DBOptions.writableFileMaxBufferSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setUseAdaptiveMutex(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setUseAdaptiveMutex(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean useAdaptiveMutex() {
        assert (this.isOwningHandle());
        return DBOptions.useAdaptiveMutex(this.nativeHandle_);
    }

    @Override
    public DBOptions setBytesPerSync(long l) {
        assert (this.isOwningHandle());
        DBOptions.setBytesPerSync(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long bytesPerSync() {
        return DBOptions.bytesPerSync(this.nativeHandle_);
    }

    @Override
    public DBOptions setWalBytesPerSync(long l) {
        assert (this.isOwningHandle());
        DBOptions.setWalBytesPerSync(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long walBytesPerSync() {
        assert (this.isOwningHandle());
        return DBOptions.walBytesPerSync(this.nativeHandle_);
    }

    @Override
    public DBOptions setStrictBytesPerSync(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setStrictBytesPerSync(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean strictBytesPerSync() {
        assert (this.isOwningHandle());
        return DBOptions.strictBytesPerSync(this.nativeHandle_);
    }

    @Override
    public DBOptions setListeners(List<AbstractEventListener> list) {
        assert (this.isOwningHandle());
        DBOptions.setEventListeners(this.nativeHandle_, RocksCallbackObject.toNativeHandleList(list));
        return this;
    }

    @Override
    public List<AbstractEventListener> listeners() {
        assert (this.isOwningHandle());
        return Arrays.asList(DBOptions.eventListeners(this.nativeHandle_));
    }

    @Override
    public DBOptions setEnableThreadTracking(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setEnableThreadTracking(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableThreadTracking() {
        assert (this.isOwningHandle());
        return DBOptions.enableThreadTracking(this.nativeHandle_);
    }

    @Override
    public DBOptions setDelayedWriteRate(long l) {
        assert (this.isOwningHandle());
        DBOptions.setDelayedWriteRate(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long delayedWriteRate() {
        return DBOptions.delayedWriteRate(this.nativeHandle_);
    }

    @Override
    public DBOptions setEnablePipelinedWrite(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setEnablePipelinedWrite(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enablePipelinedWrite() {
        assert (this.isOwningHandle());
        return DBOptions.enablePipelinedWrite(this.nativeHandle_);
    }

    @Override
    public DBOptions setUnorderedWrite(boolean bl) {
        DBOptions.setUnorderedWrite(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean unorderedWrite() {
        return DBOptions.unorderedWrite(this.nativeHandle_);
    }

    @Override
    public DBOptions setAllowConcurrentMemtableWrite(boolean bl) {
        DBOptions.setAllowConcurrentMemtableWrite(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowConcurrentMemtableWrite() {
        return DBOptions.allowConcurrentMemtableWrite(this.nativeHandle_);
    }

    @Override
    public DBOptions setEnableWriteThreadAdaptiveYield(boolean bl) {
        DBOptions.setEnableWriteThreadAdaptiveYield(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean enableWriteThreadAdaptiveYield() {
        return DBOptions.enableWriteThreadAdaptiveYield(this.nativeHandle_);
    }

    @Override
    public DBOptions setWriteThreadMaxYieldUsec(long l) {
        DBOptions.setWriteThreadMaxYieldUsec(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writeThreadMaxYieldUsec() {
        return DBOptions.writeThreadMaxYieldUsec(this.nativeHandle_);
    }

    @Override
    public DBOptions setWriteThreadSlowYieldUsec(long l) {
        DBOptions.setWriteThreadSlowYieldUsec(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long writeThreadSlowYieldUsec() {
        return DBOptions.writeThreadSlowYieldUsec(this.nativeHandle_);
    }

    @Override
    public DBOptions setSkipStatsUpdateOnDbOpen(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setSkipStatsUpdateOnDbOpen(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean skipStatsUpdateOnDbOpen() {
        assert (this.isOwningHandle());
        return DBOptions.skipStatsUpdateOnDbOpen(this.nativeHandle_);
    }

    @Override
    public DBOptions setSkipCheckingSstFileSizesOnDbOpen(boolean bl) {
        DBOptions.setSkipCheckingSstFileSizesOnDbOpen(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean skipCheckingSstFileSizesOnDbOpen() {
        assert (this.isOwningHandle());
        return DBOptions.skipCheckingSstFileSizesOnDbOpen(this.nativeHandle_);
    }

    @Override
    public DBOptions setWalRecoveryMode(WALRecoveryMode wALRecoveryMode) {
        assert (this.isOwningHandle());
        DBOptions.setWalRecoveryMode(this.nativeHandle_, wALRecoveryMode.getValue());
        return this;
    }

    @Override
    public WALRecoveryMode walRecoveryMode() {
        assert (this.isOwningHandle());
        return WALRecoveryMode.getWALRecoveryMode(DBOptions.walRecoveryMode(this.nativeHandle_));
    }

    @Override
    public DBOptions setAllow2pc(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAllow2pc(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allow2pc() {
        assert (this.isOwningHandle());
        return DBOptions.allow2pc(this.nativeHandle_);
    }

    @Override
    public DBOptions setRowCache(Cache cache) {
        assert (this.isOwningHandle());
        DBOptions.setRowCache(this.nativeHandle_, cache.nativeHandle_);
        this.rowCache_ = cache;
        return this;
    }

    @Override
    public Cache rowCache() {
        assert (this.isOwningHandle());
        return this.rowCache_;
    }

    @Override
    public DBOptions setWalFilter(AbstractWalFilter abstractWalFilter) {
        assert (this.isOwningHandle());
        DBOptions.setWalFilter(this.nativeHandle_, abstractWalFilter.nativeHandle_);
        this.walFilter_ = abstractWalFilter;
        return this;
    }

    @Override
    public WalFilter walFilter() {
        assert (this.isOwningHandle());
        return this.walFilter_;
    }

    @Override
    public DBOptions setFailIfOptionsFileError(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setFailIfOptionsFileError(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean failIfOptionsFileError() {
        assert (this.isOwningHandle());
        return DBOptions.failIfOptionsFileError(this.nativeHandle_);
    }

    @Override
    public DBOptions setDumpMallocStats(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setDumpMallocStats(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean dumpMallocStats() {
        assert (this.isOwningHandle());
        return DBOptions.dumpMallocStats(this.nativeHandle_);
    }

    @Override
    public DBOptions setAvoidFlushDuringRecovery(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAvoidFlushDuringRecovery(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean avoidFlushDuringRecovery() {
        assert (this.isOwningHandle());
        return DBOptions.avoidFlushDuringRecovery(this.nativeHandle_);
    }

    @Override
    public DBOptions setAvoidFlushDuringShutdown(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAvoidFlushDuringShutdown(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean avoidFlushDuringShutdown() {
        assert (this.isOwningHandle());
        return DBOptions.avoidFlushDuringShutdown(this.nativeHandle_);
    }

    @Override
    public DBOptions setAllowIngestBehind(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setAllowIngestBehind(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean allowIngestBehind() {
        assert (this.isOwningHandle());
        return DBOptions.allowIngestBehind(this.nativeHandle_);
    }

    @Override
    public DBOptions setTwoWriteQueues(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setTwoWriteQueues(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean twoWriteQueues() {
        assert (this.isOwningHandle());
        return DBOptions.twoWriteQueues(this.nativeHandle_);
    }

    @Override
    public DBOptions setManualWalFlush(boolean bl) {
        assert (this.isOwningHandle());
        DBOptions.setManualWalFlush(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean manualWalFlush() {
        assert (this.isOwningHandle());
        return DBOptions.manualWalFlush(this.nativeHandle_);
    }

    @Override
    public DBOptions setAtomicFlush(boolean bl) {
        DBOptions.setAtomicFlush(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean atomicFlush() {
        return DBOptions.atomicFlush(this.nativeHandle_);
    }

    @Override
    public DBOptions setAvoidUnnecessaryBlockingIO(boolean bl) {
        DBOptions.setAvoidUnnecessaryBlockingIO(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean avoidUnnecessaryBlockingIO() {
        assert (this.isOwningHandle());
        return DBOptions.avoidUnnecessaryBlockingIO(this.nativeHandle_);
    }

    @Override
    public DBOptions setPersistStatsToDisk(boolean bl) {
        DBOptions.setPersistStatsToDisk(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean persistStatsToDisk() {
        assert (this.isOwningHandle());
        return DBOptions.persistStatsToDisk(this.nativeHandle_);
    }

    @Override
    public DBOptions setWriteDbidToManifest(boolean bl) {
        DBOptions.setWriteDbidToManifest(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean writeDbidToManifest() {
        assert (this.isOwningHandle());
        return DBOptions.writeDbidToManifest(this.nativeHandle_);
    }

    @Override
    public DBOptions setLogReadaheadSize(long l) {
        DBOptions.setLogReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long logReadaheadSize() {
        assert (this.isOwningHandle());
        return DBOptions.logReadaheadSize(this.nativeHandle_);
    }

    @Override
    public DBOptions setBestEffortsRecovery(boolean bl) {
        DBOptions.setBestEffortsRecovery(this.nativeHandle_, bl);
        return this;
    }

    @Override
    public boolean bestEffortsRecovery() {
        assert (this.isOwningHandle());
        return DBOptions.bestEffortsRecovery(this.nativeHandle_);
    }

    @Override
    public DBOptions setMaxBgErrorResumeCount(int n) {
        DBOptions.setMaxBgErrorResumeCount(this.nativeHandle_, n);
        return this;
    }

    @Override
    public int maxBgerrorResumeCount() {
        assert (this.isOwningHandle());
        return DBOptions.maxBgerrorResumeCount(this.nativeHandle_);
    }

    @Override
    public DBOptions setBgerrorResumeRetryInterval(long l) {
        DBOptions.setBgerrorResumeRetryInterval(this.nativeHandle_, l);
        return this;
    }

    @Override
    public long bgerrorResumeRetryInterval() {
        assert (this.isOwningHandle());
        return DBOptions.bgerrorResumeRetryInterval(this.nativeHandle_);
    }

    private DBOptions(long l) {
        super(l);
    }

    private static native long getDBOptionsFromProps(long var0, String var2);

    private static native long getDBOptionsFromProps(String var0);

    private static long newDBOptionsInstance() {
        RocksDB.loadLibrary();
        return DBOptions.newDBOptions();
    }

    private static native long newDBOptions();

    private static native long copyDBOptions(long var0);

    private static native long newDBOptionsFromOptions(long var0);

    @Override
    protected final void disposeInternal(long l) {
        DBOptions.disposeInternalJni(l);
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

