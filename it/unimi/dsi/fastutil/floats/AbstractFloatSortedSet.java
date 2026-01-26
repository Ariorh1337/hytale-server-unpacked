/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.AbstractFloatSet;
import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatSortedSet;

public abstract class AbstractFloatSortedSet
extends AbstractFloatSet
implements FloatSortedSet {
    protected AbstractFloatSortedSet() {
    }

    @Override
    public abstract FloatBidirectionalIterator iterator();
}

