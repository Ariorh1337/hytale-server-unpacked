/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongIterable;

public interface LongBidirectionalIterable
extends LongIterable {
    @Override
    public LongBidirectionalIterator iterator();
}

