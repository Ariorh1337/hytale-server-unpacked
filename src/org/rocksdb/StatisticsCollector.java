package org.rocksdb;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StatisticsCollector {
   private final List<StatsCollectorInput> _statsCollectorInputList;
   private final ExecutorService _executorService;
   private final int _statsCollectionInterval;
   private volatile boolean _isRunning = true;

   public StatisticsCollector(List<StatsCollectorInput> var1, int var2) {
      this._statsCollectorInputList = var1;
      this._statsCollectionInterval = var2;
      this._executorService = Executors.newSingleThreadExecutor();
   }

   public void start() {
      this._executorService.submit(this.collectStatistics());
   }

   public void shutDown(int var1) throws InterruptedException {
      this._isRunning = false;
      this._executorService.shutdownNow();
      this._executorService.awaitTermination(var1, TimeUnit.MILLISECONDS);
   }

   private Runnable collectStatistics() {
      return () -> {
         while (this._isRunning) {
            try {
               if (!Thread.currentThread().isInterrupted()) {
                  for (StatsCollectorInput var2 : this._statsCollectorInputList) {
                     Statistics var3 = var2.getStatistics();
                     StatisticsCollectorCallback var4 = var2.getCallback();

                     for (TickerType var8 : TickerType.values()) {
                        if (var8 != TickerType.TICKER_ENUM_MAX) {
                           long var9 = var3.getTickerCount(var8);
                           var4.tickerCallback(var8, var9);
                        }
                     }

                     for (HistogramType var16 : HistogramType.values()) {
                        if (var16 != HistogramType.HISTOGRAM_ENUM_MAX) {
                           HistogramData var17 = var3.getHistogramData(var16);
                           var4.histogramCallback(var16, var17);
                        }
                     }
                  }

                  Thread.sleep(this._statsCollectionInterval);
                  continue;
               }
            } catch (InterruptedException var11) {
               Thread.currentThread().interrupt();
            } catch (Exception var12) {
               throw new RuntimeException("Error while calculating statistics", var12);
            }
            break;
         }
      };
   }
}
