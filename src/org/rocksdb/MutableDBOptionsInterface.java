package org.rocksdb;

public interface MutableDBOptionsInterface<T extends MutableDBOptionsInterface<T>> {
   T setMaxBackgroundJobs(int var1);

   int maxBackgroundJobs();

   @Deprecated
   T setMaxBackgroundCompactions(int var1);

   @Deprecated
   int maxBackgroundCompactions();

   T setAvoidFlushDuringShutdown(boolean var1);

   boolean avoidFlushDuringShutdown();

   T setWritableFileMaxBufferSize(long var1);

   long writableFileMaxBufferSize();

   T setDelayedWriteRate(long var1);

   long delayedWriteRate();

   T setMaxTotalWalSize(long var1);

   long maxTotalWalSize();

   T setDeleteObsoleteFilesPeriodMicros(long var1);

   long deleteObsoleteFilesPeriodMicros();

   T setStatsDumpPeriodSec(int var1);

   int statsDumpPeriodSec();

   T setStatsPersistPeriodSec(int var1);

   int statsPersistPeriodSec();

   T setStatsHistoryBufferSize(long var1);

   long statsHistoryBufferSize();

   T setMaxOpenFiles(int var1);

   int maxOpenFiles();

   T setBytesPerSync(long var1);

   long bytesPerSync();

   T setWalBytesPerSync(long var1);

   long walBytesPerSync();

   T setStrictBytesPerSync(boolean var1);

   boolean strictBytesPerSync();

   T setCompactionReadaheadSize(long var1);

   long compactionReadaheadSize();

   T setDailyOffpeakTimeUTC(String var1);

   String dailyOffpeakTimeUTC();
}
