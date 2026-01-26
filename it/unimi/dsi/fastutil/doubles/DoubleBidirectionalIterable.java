/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;

public interface DoubleBidirectionalIterable
extends DoubleIterable {
    @Override
    public DoubleBidirectionalIterator iterator();
}

