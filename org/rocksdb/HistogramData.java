/*
 * Decompiled with CFR 0.152.
 */
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

    public HistogramData(double d, double d2, double d3, double d4, double d5) {
        this(d, d2, d3, d4, d5, 0.0, 0L, 0L, 0.0);
    }

    public HistogramData(double d, double d2, double d3, double d4, double d5, double d6, long l, long l2, double d7) {
        this.median_ = d;
        this.percentile95_ = d2;
        this.percentile99_ = d3;
        this.average_ = d4;
        this.standardDeviation_ = d5;
        this.min_ = d7;
        this.max_ = d6;
        this.count_ = l;
        this.sum_ = l2;
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

