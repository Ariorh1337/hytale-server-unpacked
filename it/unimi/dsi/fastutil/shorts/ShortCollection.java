/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortIterable;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortPredicate;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ShortCollection
extends Collection<Short>,
ShortIterable {
    @Override
    public ShortIterator iterator();

    @Override
    default public IntIterator intIterator() {
        return ShortIterable.super.intIterator();
    }

    @Override
    default public ShortSpliterator spliterator() {
        return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
    }

    @Override
    default public IntSpliterator intSpliterator() {
        return ShortIterable.super.intSpliterator();
    }

    @Override
    public boolean add(short var1);

    public boolean contains(short var1);

    public boolean rem(short var1);

    @Override
    @Deprecated
    default public boolean add(Short key) {
        return this.add((short)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Short)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Short)key);
    }

    public short[] toShortArray();

    @Deprecated
    default public short[] toShortArray(short[] a) {
        return this.toArray(a);
    }

    public short[] toArray(short[] var1);

    public boolean addAll(ShortCollection var1);

    public boolean containsAll(ShortCollection var1);

    public boolean removeAll(ShortCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Short> filter) {
        return this.removeIf(filter instanceof ShortPredicate ? (ShortPredicate)filter : key -> filter.test(SafeMath.safeIntToShort(key)));
    }

    default public boolean removeIf(ShortPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        ShortIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextShort())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    default public boolean removeIf(IntPredicate filter) {
        return this.removeIf(filter instanceof ShortPredicate ? (ShortPredicate)filter : filter::test);
    }

    public boolean retainAll(ShortCollection var1);

    @Override
    @Deprecated
    default public Stream<Short> stream() {
        return Collection.super.stream();
    }

    default public IntStream intStream() {
        return StreamSupport.intStream(this.intSpliterator(), false);
    }

    @Override
    @Deprecated
    default public Stream<Short> parallelStream() {
        return Collection.super.parallelStream();
    }

    default public IntStream intParallelStream() {
        return StreamSupport.intStream(this.intSpliterator(), true);
    }
}

