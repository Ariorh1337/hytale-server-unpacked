/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.ints.AbstractIntSet;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

public abstract class AbstractIntSortedSet
extends AbstractIntSet
implements IntSortedSet {
    protected AbstractIntSortedSet() {
    }

    @Override
    public abstract IntBidirectionalIterator iterator();
}

