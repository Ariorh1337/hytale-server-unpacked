/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.rocksdb.AbstractMutableOptions;
import org.rocksdb.MutableDBOptionsInterface;
import org.rocksdb.MutableOptionKey;
import org.rocksdb.OptionString;

public class MutableDBOptions
extends AbstractMutableOptions {
    private MutableDBOptions(String[] stringArray, String[] stringArray2) {
        super(stringArray, stringArray2);
    }

    public static MutableDBOptionsBuilder builder() {
        return new MutableDBOptionsBuilder();
    }

    public static MutableDBOptionsBuilder parse(String string, boolean bl) {
        Objects.requireNonNull(string);
        List<OptionString.Entry> list = OptionString.Parser.parse(string);
        return (MutableDBOptionsBuilder)new MutableDBOptionsBuilder().fromParsed(list, bl);
    }

    public static MutableDBOptionsBuilder parse(String string) {
        return MutableDBOptions.parse(string, false);
    }

    public static class MutableDBOptionsBuilder
    extends AbstractMutableOptions.AbstractMutableOptionsBuilder<MutableDBOptions, MutableDBOptionsBuilder, MutableDBOptionKey>
    implements MutableDBOptionsInterface<MutableDBOptionsBuilder> {
        private static final Map<String, MutableDBOptionKey> ALL_KEYS_LOOKUP = new HashMap<String, MutableDBOptionKey>();

        private MutableDBOptionsBuilder() {
        }

        @Override
        protected MutableDBOptionsBuilder self() {
            return this;
        }

        @Override
        protected Map<String, MutableDBOptionKey> allKeys() {
            return ALL_KEYS_LOOKUP;
        }

        @Override
        protected MutableDBOptions build(String[] stringArray, String[] stringArray2) {
            return new MutableDBOptions(stringArray, stringArray2);
        }

        @Override
        public MutableDBOptionsBuilder setMaxBackgroundJobs(int n) {
            return (MutableDBOptionsBuilder)this.setInt(DBOption.max_background_jobs, n);
        }

        @Override
        public int maxBackgroundJobs() {
            return this.getInt(DBOption.max_background_jobs);
        }

        @Override
        @Deprecated
        public MutableDBOptionsBuilder setMaxBackgroundCompactions(int n) {
            return (MutableDBOptionsBuilder)this.setInt(DBOption.max_background_compactions, n);
        }

        @Override
        @Deprecated
        public int maxBackgroundCompactions() {
            return this.getInt(DBOption.max_background_compactions);
        }

        @Override
        public MutableDBOptionsBuilder setAvoidFlushDuringShutdown(boolean bl) {
            return (MutableDBOptionsBuilder)this.setBoolean(DBOption.avoid_flush_during_shutdown, bl);
        }

        @Override
        public boolean avoidFlushDuringShutdown() {
            return this.getBoolean(DBOption.avoid_flush_during_shutdown);
        }

        @Override
        public MutableDBOptionsBuilder setWritableFileMaxBufferSize(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.writable_file_max_buffer_size, l);
        }

        @Override
        public long writableFileMaxBufferSize() {
            return this.getLong(DBOption.writable_file_max_buffer_size);
        }

        @Override
        public MutableDBOptionsBuilder setDelayedWriteRate(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.delayed_write_rate, l);
        }

        @Override
        public long delayedWriteRate() {
            return this.getLong(DBOption.delayed_write_rate);
        }

        @Override
        public MutableDBOptionsBuilder setMaxTotalWalSize(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.max_total_wal_size, l);
        }

        @Override
        public long maxTotalWalSize() {
            return this.getLong(DBOption.max_total_wal_size);
        }

        @Override
        public MutableDBOptionsBuilder setDeleteObsoleteFilesPeriodMicros(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.delete_obsolete_files_period_micros, l);
        }

        @Override
        public long deleteObsoleteFilesPeriodMicros() {
            return this.getLong(DBOption.delete_obsolete_files_period_micros);
        }

        @Override
        public MutableDBOptionsBuilder setStatsDumpPeriodSec(int n) {
            return (MutableDBOptionsBuilder)this.setInt(DBOption.stats_dump_period_sec, n);
        }

        @Override
        public int statsDumpPeriodSec() {
            return this.getInt(DBOption.stats_dump_period_sec);
        }

        @Override
        public MutableDBOptionsBuilder setStatsPersistPeriodSec(int n) {
            return (MutableDBOptionsBuilder)this.setInt(DBOption.stats_persist_period_sec, n);
        }

        @Override
        public int statsPersistPeriodSec() {
            return this.getInt(DBOption.stats_persist_period_sec);
        }

        @Override
        public MutableDBOptionsBuilder setStatsHistoryBufferSize(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.stats_history_buffer_size, l);
        }

        @Override
        public long statsHistoryBufferSize() {
            return this.getLong(DBOption.stats_history_buffer_size);
        }

        @Override
        public MutableDBOptionsBuilder setMaxOpenFiles(int n) {
            return (MutableDBOptionsBuilder)this.setInt(DBOption.max_open_files, n);
        }

        @Override
        public int maxOpenFiles() {
            return this.getInt(DBOption.max_open_files);
        }

        @Override
        public MutableDBOptionsBuilder setBytesPerSync(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.bytes_per_sync, l);
        }

        @Override
        public long bytesPerSync() {
            return this.getLong(DBOption.bytes_per_sync);
        }

        @Override
        public MutableDBOptionsBuilder setWalBytesPerSync(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.wal_bytes_per_sync, l);
        }

        @Override
        public long walBytesPerSync() {
            return this.getLong(DBOption.wal_bytes_per_sync);
        }

        @Override
        public MutableDBOptionsBuilder setStrictBytesPerSync(boolean bl) {
            return (MutableDBOptionsBuilder)this.setBoolean(DBOption.strict_bytes_per_sync, bl);
        }

        @Override
        public boolean strictBytesPerSync() {
            return this.getBoolean(DBOption.strict_bytes_per_sync);
        }

        @Override
        public MutableDBOptionsBuilder setCompactionReadaheadSize(long l) {
            return (MutableDBOptionsBuilder)this.setLong(DBOption.compaction_readahead_size, l);
        }

        @Override
        public long compactionReadaheadSize() {
            return this.getLong(DBOption.compaction_readahead_size);
        }

        @Override
        public MutableDBOptionsBuilder setDailyOffpeakTimeUTC(String string) {
            return (MutableDBOptionsBuilder)this.setString(DBOption.daily_offpeak_time_utc, string);
        }

        @Override
        public String dailyOffpeakTimeUTC() {
            return this.getString(DBOption.daily_offpeak_time_utc);
        }

        static {
            for (DBOption dBOption : DBOption.values()) {
                ALL_KEYS_LOOKUP.put(dBOption.name(), dBOption);
            }
        }
    }

    public static enum DBOption implements MutableDBOptionKey
    {
        max_background_jobs(MutableOptionKey.ValueType.INT),
        max_background_compactions(MutableOptionKey.ValueType.INT),
        avoid_flush_during_shutdown(MutableOptionKey.ValueType.BOOLEAN),
        writable_file_max_buffer_size(MutableOptionKey.ValueType.LONG),
        delayed_write_rate(MutableOptionKey.ValueType.LONG),
        max_total_wal_size(MutableOptionKey.ValueType.LONG),
        delete_obsolete_files_period_micros(MutableOptionKey.ValueType.LONG),
        stats_dump_period_sec(MutableOptionKey.ValueType.INT),
        stats_persist_period_sec(MutableOptionKey.ValueType.INT),
        stats_history_buffer_size(MutableOptionKey.ValueType.LONG),
        max_open_files(MutableOptionKey.ValueType.INT),
        bytes_per_sync(MutableOptionKey.ValueType.LONG),
        wal_bytes_per_sync(MutableOptionKey.ValueType.LONG),
        strict_bytes_per_sync(MutableOptionKey.ValueType.BOOLEAN),
        compaction_readahead_size(MutableOptionKey.ValueType.LONG),
        daily_offpeak_time_utc(MutableOptionKey.ValueType.STRING);

        private final MutableOptionKey.ValueType valueType;

        private DBOption(MutableOptionKey.ValueType valueType) {
            this.valueType = valueType;
        }

        @Override
        public MutableOptionKey.ValueType getValueType() {
            return this.valueType;
        }
    }

    private static interface MutableDBOptionKey
    extends MutableOptionKey {
    }
}

