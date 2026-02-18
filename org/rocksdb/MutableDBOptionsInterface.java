/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public interface MutableDBOptionsInterface<T extends MutableDBOptionsInterface<T>> {
    public T setMaxBackgroundJobs(int var1);

    public int maxBackgroundJobs();

    @Deprecated
    public T setMaxBackgroundCompactions(int var1);

    @Deprecated
    public int maxBackgroundCompactions();

    public T setAvoidFlushDuringShutdown(boolean var1);

    public boolean avoidFlushDuringShutdown();

    public T setWritableFileMaxBufferSize(long var1);

    public long writableFileMaxBufferSize();

    public T setDelayedWriteRate(long var1);

    public long delayedWriteRate();

    public T setMaxTotalWalSize(long var1);

    public long maxTotalWalSize();

    public T setDeleteObsoleteFilesPeriodMicros(long var1);

    public long deleteObsoleteFilesPeriodMicros();

    public T setStatsDumpPeriodSec(int var1);

    public int statsDumpPeriodSec();

    public T setStatsPersistPeriodSec(int var1);

    public int statsPersistPeriodSec();

    public T setStatsHistoryBufferSize(long var1);

    public long statsHistoryBufferSize();

    public T setMaxOpenFiles(int var1);

    public int maxOpenFiles();

    public T setBytesPerSync(long var1);

    public long bytesPerSync();

    public T setWalBytesPerSync(long var1);

    public long walBytesPerSync();

    public T setStrictBytesPerSync(boolean var1);

    public boolean strictBytesPerSync();

    public T setCompactionReadaheadSize(long var1);

    public long compactionReadaheadSize();

    public T setDailyOffpeakTimeUTC(String var1);

    public String dailyOffpeakTimeUTC();
}

