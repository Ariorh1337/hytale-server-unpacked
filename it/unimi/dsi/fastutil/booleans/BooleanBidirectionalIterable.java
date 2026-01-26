/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import it.unimi.dsi.fastutil.booleans.BooleanIterable;

public interface BooleanBidirectionalIterable
extends BooleanIterable {
    @Override
    public BooleanBidirectionalIterator iterator();
}

