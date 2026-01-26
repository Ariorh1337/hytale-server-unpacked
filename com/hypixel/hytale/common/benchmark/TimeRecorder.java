/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.benchmark;

import com.hypixel.hytale.common.benchmark.ContinuousValueRecorder;
import com.hypixel.hytale.common.benchmark.DiscreteValueRecorder;
import com.hypixel.hytale.common.util.FormatUtil;
import java.util.Formatter;
import javax.annotation.Nonnull;

public class TimeRecorder
extends ContinuousValueRecorder {
    public static final String DEFAULT_COLUMN_SEPARATOR = "|";
    public static final String DEFAULT_COLUMN_FORMAT_HEADER = "|%-6.6s";
    public static final String DEFAULT_COLUMN_FORMAT_VALUE = "|%6.6s";
    public static final String[] DEFAULT_COLUMNS = DiscreteValueRecorder.DEFAULT_COLUMNS;
    public static final double NANOS_TO_SECONDS = 1.0E-9;

    public long start() {
        return System.nanoTime();
    }

    public double end(long start) {
        return this.recordNanos(System.nanoTime() - start);
    }

    public double recordNanos(long nanos) {
        return super.record((double)nanos * 1.0E-9);
    }

    @Nonnull
    public String toString() {
        return String.format("Avg=%s Min=%s Max=%s", TimeRecorder.formatTime(this.getAverage(0.0)), TimeRecorder.formatTime(this.getMinValue(0.0)), TimeRecorder.formatTime(this.getMaxValue(0.0)));
    }

    @Nonnull
    public static String formatTime(double secs) {
        if (secs <= 0.0) {
            return "0s";
        }
        if (secs >= 10.0) {
            return TimeRecorder.format(secs, "s");
        }
        if ((secs *= 1000.0) >= 10.0) {
            return TimeRecorder.format(secs, "ms");
        }
        if ((secs *= 1000.0) >= 10.0) {
            return TimeRecorder.format(secs, "us");
        }
        return TimeRecorder.format(secs *= 1000.0, "ns");
    }

    @Nonnull
    protected static String format(double val, String suffix) {
        return (int)Math.round(val) + suffix;
    }

    public void formatHeader(@Nonnull Formatter formatter) {
        this.formatHeader(formatter, DEFAULT_COLUMN_FORMAT_HEADER);
    }

    public void formatHeader(@Nonnull Formatter formatter, @Nonnull String columnFormatHeader) {
        FormatUtil.formatArray(formatter, columnFormatHeader, DEFAULT_COLUMNS);
    }

    public void formatValues(@Nonnull Formatter formatter) {
        this.formatValues(formatter, DEFAULT_COLUMN_FORMAT_VALUE);
    }

    public void formatValues(@Nonnull Formatter formatter, @Nonnull String columnFormatValue) {
        FormatUtil.formatArgs(formatter, columnFormatValue, TimeRecorder.formatTime(this.getAverage()), TimeRecorder.formatTime(this.getMinValue()), TimeRecorder.formatTime(this.getMaxValue()), this.count);
    }
}

