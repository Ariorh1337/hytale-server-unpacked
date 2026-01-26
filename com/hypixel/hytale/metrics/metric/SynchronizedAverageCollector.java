/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.metrics.metric;

import com.hypixel.hytale.metrics.metric.AverageCollector;

public class SynchronizedAverageCollector
extends AverageCollector {
    @Override
    public synchronized double get() {
        return super.get();
    }

    @Override
    public synchronized long size() {
        return super.size();
    }

    @Override
    public synchronized double addAndGet(double v) {
        return super.addAndGet(v);
    }

    @Override
    public synchronized void add(double v) {
        super.add(v);
    }

    @Override
    public synchronized void remove(double v) {
        super.remove(v);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }
}

