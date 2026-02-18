/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.rocksdb.HistogramData;
import org.rocksdb.HistogramType;
import org.rocksdb.Statistics;
import org.rocksdb.StatisticsCollectorCallback;
import org.rocksdb.StatsCollectorInput;
import org.rocksdb.TickerType;

public class StatisticsCollector {
    private final List<StatsCollectorInput> _statsCollectorInputList;
    private final ExecutorService _executorService;
    private final int _statsCollectionInterval;
    private volatile boolean _isRunning = true;

    public StatisticsCollector(List<StatsCollectorInput> list, int n) {
        this._statsCollectorInputList = list;
        this._statsCollectionInterval = n;
        this._executorService = Executors.newSingleThreadExecutor();
    }

    public void start() {
        this._executorService.submit(this.collectStatistics());
    }

    public void shutDown(int n) throws InterruptedException {
        this._isRunning = false;
        this._executorService.shutdownNow();
        this._executorService.awaitTermination(n, TimeUnit.MILLISECONDS);
    }

    private Runnable collectStatistics() {
        return () -> {
            while (this._isRunning) {
                try {
                    if (Thread.currentThread().isInterrupted()) break;
                    for (StatsCollectorInput statsCollectorInput : this._statsCollectorInputList) {
                        Statistics statistics = statsCollectorInput.getStatistics();
                        StatisticsCollectorCallback statisticsCollectorCallback = statsCollectorInput.getCallback();
                        for (TickerType tickerType : TickerType.values()) {
                            if (tickerType == TickerType.TICKER_ENUM_MAX) continue;
                            long l = statistics.getTickerCount(tickerType);
                            statisticsCollectorCallback.tickerCallback(tickerType, l);
                        }
                        for (Enum enum_ : HistogramType.values()) {
                            if (enum_ == HistogramType.HISTOGRAM_ENUM_MAX) continue;
                            HistogramData histogramData = statistics.getHistogramData((HistogramType)enum_);
                            statisticsCollectorCallback.histogramCallback((HistogramType)enum_, histogramData);
                        }
                    }
                    Thread.sleep(this._statsCollectionInterval);
                }
                catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }
                catch (Exception exception) {
                    throw new RuntimeException("Error while calculating statistics", exception);
                }
            }
        };
    }
}

