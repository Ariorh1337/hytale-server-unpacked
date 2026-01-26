/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterable;
import it.unimi.dsi.fastutil.doubles.DoubleBidirectionalIterator;
import it.unimi.dsi.fastutil.doubles.DoubleComparator;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import java.util.SortedSet;

public interface DoubleSortedSet
extends DoubleSet,
SortedSet<Double>,
DoubleBidirectionalIterable {
    public DoubleBidirectionalIterator iterator(double var1);

    @Override
    public DoubleBidirectionalIterator iterator();

    @Override
    default public DoubleSpliterator spliterator() {
        return DoubleSpliterators.asSpliteratorFromSorted(this.iterator(), Size64.sizeOf(this), 341, this.comparator());
    }

    public DoubleSortedSet subSet(double var1, double var3);

    public DoubleSortedSet headSet(double var1);

    public DoubleSortedSet tailSet(double var1);

    public DoubleComparator comparator();

    public double firstDouble();

    public double lastDouble();

    @Deprecated
    default public DoubleSortedSet subSet(Double from, Double to) {
        return this.subSet((double)from, (double)to);
    }

    @Deprecated
    default public DoubleSortedSet headSet(Double to) {
        return this.headSet((double)to);
    }

    @Deprecated
    default public DoubleSortedSet tailSet(Double from) {
        return this.tailSet((double)from);
    }

    @Override
    @Deprecated
    default public Double first() {
        return this.firstDouble();
    }

    @Override
    @Deprecated
    default public Double last() {
        return this.lastDouble();
    }
}

