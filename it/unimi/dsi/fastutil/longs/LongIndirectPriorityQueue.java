/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.longs.LongComparator;

public interface LongIndirectPriorityQueue
extends IndirectPriorityQueue<Long> {
    public LongComparator comparator();
}

