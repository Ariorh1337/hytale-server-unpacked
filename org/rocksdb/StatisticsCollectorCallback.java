/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.HistogramData;
import org.rocksdb.HistogramType;
import org.rocksdb.TickerType;

public interface StatisticsCollectorCallback {
    public void tickerCallback(TickerType var1, long var2);

    public void histogramCallback(HistogramType var1, HistogramData var2);
}

