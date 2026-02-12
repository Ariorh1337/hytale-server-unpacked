package org.rocksdb;

public class HistogramData {
   private final double median_;
   private final double percentile95_;
   private final double percentile99_;
   private final double average_;
   private final double standardDeviation_;
   private final double max_;
   private final long count_;
   private final long sum_;
   private final double min_;

   public HistogramData(double var1, double var3, double var5, double var7, double var9) {
      this(var1, var3, var5, var7, var9, 0.0, 0L, 0L, 0.0);
   }

   public HistogramData(double var1, double var3, double var5, double var7, double var9, double var11, long var13, long var15, double var17) {
      this.median_ = var1;
      this.percentile95_ = var3;
      this.percentile99_ = var5;
      this.average_ = var7;
      this.standardDeviation_ = var9;
      this.min_ = var17;
      this.max_ = var11;
      this.count_ = var13;
      this.sum_ = var15;
   }

   public double getMedian() {
      return this.median_;
   }

   public double getPercentile95() {
      return this.percentile95_;
   }

   public double getPercentile99() {
      return this.percentile99_;
   }

   public double getAverage() {
      return this.average_;
   }

   public double getStandardDeviation() {
      return this.standardDeviation_;
   }

   public double getMax() {
      return this.max_;
   }

   public long getCount() {
      return this.count_;
   }

   public long getSum() {
      return this.sum_;
   }

   public double getMin() {
      return this.min_;
   }
}
