/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

public class ZstdFrameProgression {
    private long ingested;
    private long consumed;
    private long produced;
    private long flushed;
    private int currentJobID;
    private int nbActiveWorkers;

    public ZstdFrameProgression(long l, long l2, long l3, long l4, int n, int n2) {
        this.ingested = l;
        this.consumed = l2;
        this.produced = l3;
        this.flushed = l4;
        this.currentJobID = n;
        this.nbActiveWorkers = n2;
    }

    public long getIngested() {
        return this.ingested;
    }

    public long getConsumed() {
        return this.consumed;
    }

    public long getProduced() {
        return this.produced;
    }

    public long getFlushed() {
        return this.flushed;
    }

    public int getCurrentJobID() {
        return this.currentJobID;
    }

    public int getNbActiveWorkers() {
        return this.nbActiveWorkers;
    }
}

