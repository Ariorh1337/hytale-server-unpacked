package org.rocksdb;

public interface StatisticsCollectorCallback {
   void tickerCallback(TickerType var1, long var2);

   void histogramCallback(HistogramType var1, HistogramData var2);
}
