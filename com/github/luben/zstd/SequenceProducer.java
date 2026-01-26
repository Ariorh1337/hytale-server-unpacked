/*
 * Decompiled with CFR 0.152.
 */
package com.github.luben.zstd;

public interface SequenceProducer {
    public long getFunctionPointer();

    public long createState();

    public void freeState(long var1);
}

