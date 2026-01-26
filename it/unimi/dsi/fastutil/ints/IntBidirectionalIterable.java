/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntIterable;

public interface IntBidirectionalIterable
extends IntIterable {
    @Override
    public IntBidirectionalIterator iterator();
}

