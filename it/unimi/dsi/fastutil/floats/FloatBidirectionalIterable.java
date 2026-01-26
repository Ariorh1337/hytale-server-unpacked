/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.floats.FloatBidirectionalIterator;
import it.unimi.dsi.fastutil.floats.FloatIterable;

public interface FloatBidirectionalIterable
extends FloatIterable {
    @Override
    public FloatBidirectionalIterator iterator();
}

