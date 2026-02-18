/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RateLimiterMode;
import org.rocksdb.RocksObject;

public class RateLimiter
extends RocksObject {
    public static final long DEFAULT_REFILL_PERIOD_MICROS = 100000L;
    public static final int DEFAULT_FAIRNESS = 10;
    public static final RateLimiterMode DEFAULT_MODE = RateLimiterMode.WRITES_ONLY;
    public static final boolean DEFAULT_AUTOTUNE = false;

    public RateLimiter(long l) {
        this(l, 100000L, 10, DEFAULT_MODE, false);
    }

    public RateLimiter(long l, long l2) {
        this(l, l2, 10, DEFAULT_MODE, false);
    }

    public RateLimiter(long l, long l2, int n) {
        this(l, l2, n, DEFAULT_MODE, false);
    }

    public RateLimiter(long l, long l2, int n, RateLimiterMode rateLimiterMode) {
        this(l, l2, n, rateLimiterMode, false);
    }

    public RateLimiter(long l, long l2, int n, RateLimiterMode rateLimiterMode, boolean bl) {
        super(RateLimiter.newRateLimiterHandle(l, l2, n, rateLimiterMode.getValue(), bl));
    }

    public void setBytesPerSecond(long l) {
        assert (this.isOwningHandle());
        RateLimiter.setBytesPerSecond(this.nativeHandle_, l);
    }

    public long getBytesPerSecond() {
        assert (this.isOwningHandle());
        return RateLimiter.getBytesPerSecond(this.nativeHandle_);
    }

    public void request(long l) {
        assert (this.isOwningHandle());
        RateLimiter.request(this.nativeHandle_, l);
    }

    public long getSingleBurstBytes() {
        assert (this.isOwningHandle());
        return RateLimiter.getSingleBurstBytes(this.nativeHandle_);
    }

    public long getTotalBytesThrough() {
        assert (this.isOwningHandle());
        return RateLimiter.getTotalBytesThrough(this.nativeHandle_);
    }

    public long getTotalRequests() {
        assert (this.isOwningHandle());
        return RateLimiter.getTotalRequests(this.nativeHandle_);
    }

    private static native long newRateLimiterHandle(long var0, long var2, int var4, byte var5, boolean var6);

    @Override
    protected final void disposeInternal(long l) {
        RateLimiter.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native void setBytesPerSecond(long var0, long var2);

    private static native long getBytesPerSecond(long var0);

    private static native void request(long var0, long var2);

    private static native long getSingleBurstBytes(long var0);

    private static native long getTotalBytesThrough(long var0);

    private static native long getTotalRequests(long var0);
}

