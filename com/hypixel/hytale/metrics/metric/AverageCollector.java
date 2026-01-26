/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.metrics.metric;

public class AverageCollector {
    private double val = 0.0;
    private long n = 0L;

    public double get() {
        return this.val;
    }

    public long size() {
        return this.n;
    }

    public double addAndGet(double v) {
        this.add(v);
        return this.get();
    }

    public void add(double v) {
        ++this.n;
        this.val = this.val - this.val / (double)this.n + v / (double)this.n;
    }

    public void remove(double v) {
        if (this.n == 1L) {
            this.n = 0L;
            this.val = 0.0;
        } else if (this.n > 1L) {
            this.val = (this.val - v / (double)this.n) / (1.0 - 1.0 / (double)this.n);
            --this.n;
        }
    }

    public void clear() {
        this.val = 0.0;
        this.n = 0L;
    }

    public static double add(double val, double v, int n) {
        return val - val / (double)n + v / (double)n;
    }

    public void set(double v) {
        this.n = 1L;
        this.val = v;
    }
}

