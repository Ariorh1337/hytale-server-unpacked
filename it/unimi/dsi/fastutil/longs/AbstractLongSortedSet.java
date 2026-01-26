/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.longs.AbstractLongSet;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

public abstract class AbstractLongSortedSet
extends AbstractLongSet
implements LongSortedSet {
    protected AbstractLongSortedSet() {
    }

    @Override
    public abstract LongBidirectionalIterator iterator();
}

