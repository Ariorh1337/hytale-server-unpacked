/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.IndirectPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntComparator;

public interface IntIndirectPriorityQueue
extends IndirectPriorityQueue<Integer> {
    public IntComparator comparator();
}

