/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.Statistics;
import org.rocksdb.StatisticsCollectorCallback;

public class StatsCollectorInput {
    private final Statistics _statistics;
    private final StatisticsCollectorCallback _statsCallback;

    public StatsCollectorInput(Statistics statistics, StatisticsCollectorCallback statisticsCollectorCallback) {
        this._statistics = statistics;
        this._statsCallback = statisticsCollectorCallback;
    }

    public Statistics getStatistics() {
        return this._statistics;
    }

    public StatisticsCollectorCallback getCallback() {
        return this._statsCallback;
    }
}

