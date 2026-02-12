package org.rocksdb;

import java.util.EnumSet;

public class Statistics extends RocksObject {
   public Statistics() {
      super(newStatisticsInstance());
   }

   public Statistics(Statistics var1) {
      super(newStatistics(var1.nativeHandle_));
   }

   public Statistics(EnumSet<HistogramType> var1) {
      super(newStatisticsInstance(toArrayValues(var1)));
   }

   public Statistics(EnumSet<HistogramType> var1, Statistics var2) {
      super(newStatistics(toArrayValues(var1), var2.nativeHandle_));
   }

   Statistics(long var1) {
      super(var1);
   }

   private static byte[] toArrayValues(EnumSet<HistogramType> var0) {
      byte[] var1 = new byte[var0.size()];
      int var2 = 0;

      for (HistogramType var4 : var0) {
         var1[var2++] = var4.getValue();
      }

      return var1;
   }

   public StatsLevel statsLevel() {
      return StatsLevel.getStatsLevel(statsLevel(this.nativeHandle_));
   }

   public void setStatsLevel(StatsLevel var1) {
      setStatsLevel(this.nativeHandle_, var1.getValue());
   }

   public long getTickerCount(TickerType var1) {
      assert this.isOwningHandle();
      return getTickerCount(this.nativeHandle_, var1.getValue());
   }

   public long getAndResetTickerCount(TickerType var1) {
      assert this.isOwningHandle();
      return getAndResetTickerCount(this.nativeHandle_, var1.getValue());
   }

   public HistogramData getHistogramData(HistogramType var1) {
      assert this.isOwningHandle();
      return getHistogramData(this.nativeHandle_, var1.getValue());
   }

   public String getHistogramString(HistogramType var1) {
      assert this.isOwningHandle();
      return getHistogramString(this.nativeHandle_, var1.getValue());
   }

   public void reset() throws RocksDBException {
      assert this.isOwningHandle();
      reset(this.nativeHandle_);
   }

   @Override
   public String toString() {
      assert this.isOwningHandle();
      return toString(this.nativeHandle_);
   }

   private static long newStatisticsInstance() {
      RocksDB.loadLibrary();
      return newStatistics();
   }

   private static native long newStatistics();

   private static native long newStatistics(long var0);

   private static long newStatisticsInstance(byte[] var0) {
      RocksDB.loadLibrary();
      return newStatistics(var0);
   }

   private static native long newStatistics(byte[] var0);

   private static native long newStatistics(byte[] var0, long var1);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
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
