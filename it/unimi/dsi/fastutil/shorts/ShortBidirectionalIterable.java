/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterable;

public interface ShortBidirectionalIterable
extends ShortIterable {
    @Override
    public ShortBidirectionalIterator iterator();
}

