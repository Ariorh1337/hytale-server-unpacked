/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.benchmark;

import com.hypixel.hytale.common.benchmark.TimeRecorder;
import com.hypixel.hytale.math.util.MathUtil;
import java.util.Formatter;
import javax.annotation.Nonnull;

public class TimeDistributionRecorder
extends TimeRecorder {
    protected int minLogRange;
    protected int maxLogRange;
    protected int logSteps;
    protected long[] valueBins;

    public TimeDistributionRecorder(double maxSecs, double minSecs, int logSteps) {
        if (maxSecs < 1.0E-6 || maxSecs > 0.1) {
            throw new IllegalArgumentException("Max seconds must be between 100 milli secs and 1 micro sec");
        }
        if (minSecs < 1.0E-6 || minSecs > 0.1) {
            throw new IllegalArgumentException("Min seconds must be between 100 milli secs and 1 micro sec");
        }
        if (maxSecs <= minSecs) {
            throw new IllegalArgumentException("Max seconds must be larger than min seconds");
        }
        if (logSteps < 2 || logSteps > 10) {
            throw new IllegalArgumentException("LogSteps must be between 2 and 10");
        }
        this.maxLogRange = MathUtil.ceil(Math.log10(maxSecs));
        this.minLogRange = MathUtil.floor(Math.log10(minSecs));
        this.logSteps = MathUtil.clamp(logSteps, 2, 10);
        this.valueBins = new long[(this.maxLogRange - this.minLogRange) * this.logSteps + 2];
        int length = this.valueBins.length;
        for (int i = 0; i < length; ++i) {
            this.valueBins[i] = 0L;
        }
    }

    public TimeDistributionRecorder(double maxSecs, double minSecs) {
        this(maxSecs, minSecs, 5);
    }

    public TimeDistributionRecorder() {
        this(0.1, 1.0E-5);
    }

    @Override
    public void reset() {
        super.reset();
        int length = this.valueBins.length;
        for (int i = 0; i < length; ++i) {
            this.valueBins[i] = 0L;
        }
    }

    @Override
    public double recordNanos(long nanos) {
        double secs = super.recordNanos(nanos);
        int n = this.timeToIndex(secs);
        this.valueBins[n] = this.valueBins[n] + 1L;
        return secs;
    }

    public int timeToIndex(double secs) {
        double logSecs = Math.log10(secs);
        double indexDbl = ((double)this.maxLogRange - logSecs) * (double)this.logSteps;
        int index = MathUtil.ceil(indexDbl);
        if (index < 0) {
            index = 0;
        } else if (index >= this.valueBins.length) {
            index = this.valueBins.length - 1;
        }
        return index;
    }

    public double indexToTime(int index) {
        if (index < 0) {
            index = 0;
        } else if (index >= this.valueBins.length) {
            index = this.valueBins.length - 1;
        }
        if (index == this.valueBins.length - 1) {
            return 0.0;
        }
        double exp = (double)this.maxLogRange - (double)index / (double)this.logSteps;
        return Math.pow(10.0, exp);
    }

    public int size() {
        return this.valueBins.length;
    }

    public long get(int index) {
        return this.valueBins[index];
    }

    @Override
    @Nonnull
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(12 * this.size());
        stringBuilder.append("Cnt=").append(this.getCount());
        for (int i = 0; i < this.size(); ++i) {
            stringBuilder.append(' ').append(TimeDistributionRecorder.formatTime(this.indexToTime(i))).append('=').append(this.get(i));
        }
        return super.toString() + " " + String.valueOf(stringBuilder);
    }

    @Override
    public void formatHeader(@Nonnull Formatter formatter, @Nonnull String columnFormatHeader) {
        super.formatHeader(formatter, columnFormatHeader);
        for (int i = 0; i < this.size(); ++i) {
            formatter.format(columnFormatHeader, TimeDistributionRecorder.formatTime(this.indexToTime(i)));
        }
    }

    @Override
    public void formatValues(@Nonnull Formatter formatter, @Nonnull String columnFormatValue) {
        this.formatValues(formatter, 0L, columnFormatValue);
    }

    public void formatValues(@Nonnull Formatter formatter, long normalValue) {
        this.formatValues(formatter, normalValue, "|%6.6s");
    }

    public void formatValues(@Nonnull Formatter formatter, long normalValue, @Nonnull String columnFormatValue) {
        super.formatValues(formatter, columnFormatValue);
        double norm = this.count > 0L && normalValue > 1L ? (double)normalValue / (double)this.count : 1.0;
        for (int i = 0; i < this.size(); ++i) {
            formatter.format(columnFormatValue, (int)Math.round((double)this.get(i) * norm));
        }
    }
}

