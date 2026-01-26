/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.shorts.AbstractShortSet;
import it.unimi.dsi.fastutil.shorts.ShortBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;

public abstract class AbstractShortSortedSet
extends AbstractShortSet
implements ShortSortedSet {
    protected AbstractShortSortedSet() {
    }

    @Override
    public abstract ShortBidirectionalIterator iterator();
}

