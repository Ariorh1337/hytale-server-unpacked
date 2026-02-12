package org.rocksdb;

public class StatsCollectorInput {
   private final Statistics _statistics;
   private final StatisticsCollectorCallback _statsCallback;

   public StatsCollectorInput(Statistics var1, StatisticsCollectorCallback var2) {
      this._statistics = var1;
      this._statsCallback = var2;
   }

   public Statistics getStatistics() {
      return this._statistics;
   }

   public StatisticsCollectorCallback getCallback() {
      return this._statsCallback;
   }
}
