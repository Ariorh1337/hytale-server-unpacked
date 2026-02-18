/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.EnumSet;
import org.rocksdb.HistogramData;
import org.rocksdb.HistogramType;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;
import org.rocksdb.StatsLevel;
import org.rocksdb.TickerType;

public class Statistics
extends RocksObject {
    public Statistics() {
        super(Statistics.newStatisticsInstance());
    }

    public Statistics(Statistics statistics) {
        super(Statistics.newStatistics(statistics.nativeHandle_));
    }

    public Statistics(EnumSet<HistogramType> enumSet) {
        super(Statistics.newStatisticsInstance(Statistics.toArrayValues(enumSet)));
    }

    public Statistics(EnumSet<HistogramType> enumSet, Statistics statistics) {
        super(Statistics.newStatistics(Statistics.toArrayValues(enumSet), statistics.nativeHandle_));
    }

    Statistics(long l) {
        super(l);
    }

    private static byte[] toArrayValues(EnumSet<HistogramType> enumSet) {
        byte[] byArray = new byte[enumSet.size()];
        int n = 0;
        for (HistogramType histogramType : enumSet) {
            byArray[n++] = histogramType.getValue();
        }
        return byArray;
    }

    public StatsLevel statsLevel() {
        return StatsLevel.getStatsLevel(Statistics.statsLevel(this.nativeHandle_));
    }

    public void setStatsLevel(StatsLevel statsLevel) {
        Statistics.setStatsLevel(this.nativeHandle_, statsLevel.getValue());
    }

    public long getTickerCount(TickerType tickerType) {
        assert (this.isOwningHandle());
        return Statistics.getTickerCount(this.nativeHandle_, tickerType.getValue());
    }

    public long getAndResetTickerCount(TickerType tickerType) {
        assert (this.isOwningHandle());
        return Statistics.getAndResetTickerCount(this.nativeHandle_, tickerType.getValue());
    }

    public HistogramData getHistogramData(HistogramType histogramType) {
        assert (this.isOwningHandle());
        return Statistics.getHistogramData(this.nativeHandle_, histogramType.getValue());
    }

    public String getHistogramString(HistogramType histogramType) {
        assert (this.isOwningHandle());
        return Statistics.getHistogramString(this.nativeHandle_, histogramType.getValue());
    }

    public void reset() throws RocksDBException {
        assert (this.isOwningHandle());
        Statistics.reset(this.nativeHandle_);
    }

    public String toString() {
        assert (this.isOwningHandle());
        return Statistics.toString(this.nativeHandle_);
    }

    private static long newStatisticsInstance() {
        RocksDB.loadLibrary();
        return Statistics.newStatistics();
    }

    private static native long newStatistics();

    private static native long newStatistics(long var0);

    private static long newStatisticsInstance(byte[] byArray) {
        RocksDB.loadLibrary();
        return Statistics.newStatistics(byArray);
    }

    private static native long newStatistics(byte[] var0);

    private static native long newStatistics(byte[] var0, long var1);

    @Override
    protected final void disposeInternal(long l) {
        Statistics.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native byte statsLevel(long var0);

    private static native void setStatsLevel(long var0, byte var2);

    private static native long getTickerCount(long var0, byte var2);

    private static native long getAndResetTickerCount(long var0, byte var2);

    private static native HistogramData getHistogramData(long var0, byte var2);

    private static native String getHistogramString(long var0, byte var2);

    private static native void reset(long var0) throws RocksDBException;

    private static native String toString(long var0);
}

