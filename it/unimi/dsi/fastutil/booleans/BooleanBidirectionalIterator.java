/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface BooleanBidirectionalIterator
extends BooleanIterator,
ObjectBidirectionalIterator<Boolean> {
    public boolean previousBoolean();

    @Override
    @Deprecated
    default public Boolean previous() {
        return this.previousBoolean();
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousBoolean();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return BooleanIterator.super.skip(n);
    }
}

