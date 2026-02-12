package org.rocksdb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MutableDBOptions extends AbstractMutableOptions {
   private MutableDBOptions(String[] var1, String[] var2) {
      super(var1, var2);
   }

   public static MutableDBOptions.MutableDBOptionsBuilder builder() {
      return new MutableDBOptions.MutableDBOptionsBuilder();
   }

   public static MutableDBOptions.MutableDBOptionsBuilder parse(String var0, boolean var1) {
      Objects.requireNonNull(var0);
      List var2 = OptionString.Parser.parse(var0);
      return new MutableDBOptions.MutableDBOptionsBuilder().fromParsed(var2, var1);
   }

   public static MutableDBOptions.MutableDBOptionsBuilder parse(String var0) {
      return parse(var0, false);
   }

   public enum DBOption implements MutableDBOptions.MutableDBOptionKey {
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

      DBOption(MutableOptionKey.ValueType var3) {
         this.valueType = var3;
      }

      @Override
      public MutableOptionKey.ValueType getValueType() {
         return this.valueType;
      }
   }

   private interface MutableDBOptionKey extends MutableOptionKey {
   }

   public static class MutableDBOptionsBuilder
      extends AbstractMutableOptions.AbstractMutableOptionsBuilder<MutableDBOptions, MutableDBOptions.MutableDBOptionsBuilder, MutableDBOptions.MutableDBOptionKey>
      implements MutableDBOptionsInterface<MutableDBOptions.MutableDBOptionsBuilder> {
      private static final Map<String, MutableDBOptions.MutableDBOptionKey> ALL_KEYS_LOOKUP = new HashMap<>();

      private MutableDBOptionsBuilder() {
      }

      protected MutableDBOptions.MutableDBOptionsBuilder self() {
         return this;
      }

      @Override
      protected Map<String, MutableDBOptions.MutableDBOptionKey> allKeys() {
         return ALL_KEYS_LOOKUP;
      }

      protected MutableDBOptions build(String[] var1, String[] var2) {
         return new MutableDBOptions(var1, var2);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setMaxBackgroundJobs(int var1) {
         return this.setInt(MutableDBOptions.DBOption.max_background_jobs, var1);
      }

      @Override
      public int maxBackgroundJobs() {
         return this.getInt(MutableDBOptions.DBOption.max_background_jobs);
      }

      @Deprecated
      public MutableDBOptions.MutableDBOptionsBuilder setMaxBackgroundCompactions(int var1) {
         return this.setInt(MutableDBOptions.DBOption.max_background_compactions, var1);
      }

      @Deprecated
      @Override
      public int maxBackgroundCompactions() {
         return this.getInt(MutableDBOptions.DBOption.max_background_compactions);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setAvoidFlushDuringShutdown(boolean var1) {
         return this.setBoolean(MutableDBOptions.DBOption.avoid_flush_during_shutdown, var1);
      }

      @Override
      public boolean avoidFlushDuringShutdown() {
         return this.getBoolean(MutableDBOptions.DBOption.avoid_flush_during_shutdown);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setWritableFileMaxBufferSize(long var1) {
         return this.setLong(MutableDBOptions.DBOption.writable_file_max_buffer_size, var1);
      }

      @Override
      public long writableFileMaxBufferSize() {
         return this.getLong(MutableDBOptions.DBOption.writable_file_max_buffer_size);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setDelayedWriteRate(long var1) {
         return this.setLong(MutableDBOptions.DBOption.delayed_write_rate, var1);
      }

      @Override
      public long delayedWriteRate() {
         return this.getLong(MutableDBOptions.DBOption.delayed_write_rate);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setMaxTotalWalSize(long var1) {
         return this.setLong(MutableDBOptions.DBOption.max_total_wal_size, var1);
      }

      @Override
      public long maxTotalWalSize() {
         return this.getLong(MutableDBOptions.DBOption.max_total_wal_size);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setDeleteObsoleteFilesPeriodMicros(long var1) {
         return this.setLong(MutableDBOptions.DBOption.delete_obsolete_files_period_micros, var1);
      }

      @Override
      public long deleteObsoleteFilesPeriodMicros() {
         return this.getLong(MutableDBOptions.DBOption.delete_obsolete_files_period_micros);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setStatsDumpPeriodSec(int var1) {
         return this.setInt(MutableDBOptions.DBOption.stats_dump_period_sec, var1);
      }

      @Override
      public int statsDumpPeriodSec() {
         return this.getInt(MutableDBOptions.DBOption.stats_dump_period_sec);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setStatsPersistPeriodSec(int var1) {
         return this.setInt(MutableDBOptions.DBOption.stats_persist_period_sec, var1);
      }

      @Override
      public int statsPersistPeriodSec() {
         return this.getInt(MutableDBOptions.DBOption.stats_persist_period_sec);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setStatsHistoryBufferSize(long var1) {
         return this.setLong(MutableDBOptions.DBOption.stats_history_buffer_size, var1);
      }

      @Override
      public long statsHistoryBufferSize() {
         return this.getLong(MutableDBOptions.DBOption.stats_history_buffer_size);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setMaxOpenFiles(int var1) {
         return this.setInt(MutableDBOptions.DBOption.max_open_files, var1);
      }

      @Override
      public int maxOpenFiles() {
         return this.getInt(MutableDBOptions.DBOption.max_open_files);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setBytesPerSync(long var1) {
         return this.setLong(MutableDBOptions.DBOption.bytes_per_sync, var1);
      }

      @Override
      public long bytesPerSync() {
         return this.getLong(MutableDBOptions.DBOption.bytes_per_sync);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setWalBytesPerSync(long var1) {
         return this.setLong(MutableDBOptions.DBOption.wal_bytes_per_sync, var1);
      }

      @Override
      public long walBytesPerSync() {
         return this.getLong(MutableDBOptions.DBOption.wal_bytes_per_sync);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setStrictBytesPerSync(boolean var1) {
         return this.setBoolean(MutableDBOptions.DBOption.strict_bytes_per_sync, var1);
      }

      @Override
      public boolean strictBytesPerSync() {
         return this.getBoolean(MutableDBOptions.DBOption.strict_bytes_per_sync);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setCompactionReadaheadSize(long var1) {
         return this.setLong(MutableDBOptions.DBOption.compaction_readahead_size, var1);
      }

      @Override
      public long compactionReadaheadSize() {
         return this.getLong(MutableDBOptions.DBOption.compaction_readahead_size);
      }

      public MutableDBOptions.MutableDBOptionsBuilder setDailyOffpeakTimeUTC(String var1) {
         return this.setString(MutableDBOptions.DBOption.daily_offpeak_time_utc, var1);
      }

      @Override
      public String dailyOffpeakTimeUTC() {
         return this.getString(MutableDBOptions.DBOption.daily_offpeak_time_utc);
      }

      static {
         for (MutableDBOptions.DBOption var3 : MutableDBOptions.DBOption.values()) {
            ALL_KEYS_LOOKUP.put(var3.name(), var3);
         }
      }
   }
}
