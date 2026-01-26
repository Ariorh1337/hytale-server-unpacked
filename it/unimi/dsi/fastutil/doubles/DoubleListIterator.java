/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import java.util.ListIterator;

public interface DoubleListIterator
extends DoubleBidirectionalIterator,
ListIterator<Double> {
    @Override
    default public void set(double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(double k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public void set(Double k) {
        this.set((double)k);
    }

    @Override
    @Deprecated
    default public void add(Double k) {
        this.add((double)k);
    }

    @Override
    @Deprecated
    default public Double next() {
        return DoubleBidirectionalIterator.super.next();
    }

    @Override
    @Deprecated
    default public Double previous() {
        return DoubleBidirectionalIterator.super.previous();
    }
}

