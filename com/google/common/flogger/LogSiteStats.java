/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger;

import com.google.common.flogger.LogSiteKey;
import com.google.common.flogger.LogSiteMap;
import com.google.common.flogger.backend.Metadata;
import com.google.common.flogger.util.Checks;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class LogSiteStats {
    private static final LogSiteMap<LogSiteStats> map = new LogSiteMap<LogSiteStats>(){

        @Override
        protected LogSiteStats initialValue() {
            return new LogSiteStats();
        }
    };
    private final AtomicLong invocationCount = new AtomicLong();
    private final AtomicLong lastTimestampNanos = new AtomicLong();
    private final AtomicInteger skippedLogStatements = new AtomicInteger();

    LogSiteStats() {
    }

    static RateLimitPeriod newRateLimitPeriod(int n, TimeUnit unit) {
        return new RateLimitPeriod(n, unit);
    }

    static LogSiteStats getStatsForKey(LogSiteKey logSiteKey, Metadata metadata) {
        return map.get(logSiteKey, metadata);
    }

    boolean incrementAndCheckInvocationCount(int rateLimitCount) {
        return this.invocationCount.getAndIncrement() % (long)rateLimitCount == 0L;
    }

    boolean checkLastTimestamp(long timestampNanos, RateLimitPeriod period) {
        long lastNanos = this.lastTimestampNanos.get();
        long deadlineNanos = lastNanos + period.toNanos();
        if (deadlineNanos >= 0L && (timestampNanos >= deadlineNanos || lastNanos == 0L) && this.lastTimestampNanos.compareAndSet(lastNanos, timestampNanos)) {
            period.setSkipCount(this.skippedLogStatements.getAndSet(0));
            return true;
        }
        this.skippedLogStatements.incrementAndGet();
        return false;
    }

    static final class RateLimitPeriod {
        private final int n;
        private final TimeUnit unit;
        private int skipCount = -1;

        private RateLimitPeriod(int n, TimeUnit unit) {
            if (n <= 0) {
                throw new IllegalArgumentException("time period must be positive: " + n);
            }
            this.n = n;
            this.unit = Checks.checkNotNull(unit, "time unit");
        }

        private long toNanos() {
            return this.unit.toNanos(this.n);
        }

        private void setSkipCount(int skipCount) {
            this.skipCount = skipCount;
        }

        public String toString() {
            StringBuilder out = new StringBuilder().append(this.n).append(' ').append((Object)this.unit);
            if (this.skipCount > 0) {
                out.append(" [skipped: ").append(this.skipCount).append(']');
            }
            return out.toString();
        }

        public int hashCode() {
            return this.n * 37 ^ this.unit.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof RateLimitPeriod) {
                RateLimitPeriod that = (RateLimitPeriod)obj;
                return this.n == that.n && this.unit == that.unit;
            }
            return false;
        }
    }
}

