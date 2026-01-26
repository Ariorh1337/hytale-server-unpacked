/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanBidirectionalIterator;
import java.util.ListIterator;

public interface BooleanListIterator
extends BooleanBidirectionalIterator,
ListIterator<Boolean> {
    @Override
    default public void set(boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void add(boolean k) {
        throw new UnsupportedOperationException();
    }

    @Override
    default public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    default public void set(Boolean k) {
        this.set((boolean)k);
    }

    @Override
    @Deprecated
    default public void add(Boolean k) {
        this.add((boolean)k);
    }

    @Override
    @Deprecated
    default public Boolean next() {
        return BooleanBidirectionalIterator.super.next();
    }

    @Override
    @Deprecated
    default public Boolean previous() {
        return BooleanBidirectionalIterator.super.previous();
    }
}

