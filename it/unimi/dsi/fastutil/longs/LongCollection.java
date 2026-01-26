/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongPredicate;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface LongCollection
extends Collection<Long>,
LongIterable {
    @Override
    public LongIterator iterator();

    @Override
    default public LongIterator longIterator() {
        return this.iterator();
    }

    @Override
    default public LongSpliterator spliterator() {
        return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
    }

    @Override
    default public LongSpliterator longSpliterator() {
        return this.spliterator();
    }

    @Override
    public boolean add(long var1);

    public boolean contains(long var1);

    public boolean rem(long var1);

    @Override
    @Deprecated
    default public boolean add(Long key) {
        return this.add((long)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Long)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Long)key);
    }

    public long[] toLongArray();

    @Deprecated
    default public long[] toLongArray(long[] a) {
        return this.toArray(a);
    }

    public long[] toArray(long[] var1);

    public boolean addAll(LongCollection var1);

    public boolean containsAll(LongCollection var1);

    public boolean removeAll(LongCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Long> filter) {
        return this.removeIf(filter instanceof java.util.function.LongPredicate ? (java.util.function.LongPredicate)((Object)filter) : key -> filter.test(key));
    }

    default public boolean removeIf(java.util.function.LongPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        LongIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextLong())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    default public boolean removeIf(LongPredicate filter) {
        return this.removeIf((java.util.function.LongPredicate)filter);
    }

    public boolean retainAll(LongCollection var1);

    @Override
    @Deprecated
    default public Stream<Long> stream() {
        return Collection.super.stream();
    }

    default public LongStream longStream() {
        return StreamSupport.longStream(this.longSpliterator(), false);
    }

    @Override
    @Deprecated
    default public Stream<Long> parallelStream() {
        return Collection.super.parallelStream();
    }

    default public LongStream longParallelStream() {
        return StreamSupport.longStream(this.longSpliterator(), true);
    }
}

