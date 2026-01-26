/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.BytePredicate;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface ByteCollection
extends Collection<Byte>,
ByteIterable {
    @Override
    public ByteIterator iterator();

    @Override
    default public IntIterator intIterator() {
        return ByteIterable.super.intIterator();
    }

    @Override
    default public ByteSpliterator spliterator() {
        return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
    }

    @Override
    default public IntSpliterator intSpliterator() {
        return ByteIterable.super.intSpliterator();
    }

    @Override
    public boolean add(byte var1);

    public boolean contains(byte var1);

    public boolean rem(byte var1);

    @Override
    @Deprecated
    default public boolean add(Byte key) {
        return this.add((byte)key);
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains((Byte)key);
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem((Byte)key);
    }

    public byte[] toByteArray();

    @Deprecated
    default public byte[] toByteArray(byte[] a) {
        return this.toArray(a);
    }

    public byte[] toArray(byte[] var1);

    public boolean addAll(ByteCollection var1);

    public boolean containsAll(ByteCollection var1);

    public boolean removeAll(ByteCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Byte> filter) {
        return this.removeIf(filter instanceof BytePredicate ? (BytePredicate)filter : key -> filter.test(SafeMath.safeIntToByte(key)));
    }

    default public boolean removeIf(BytePredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        ByteIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextByte())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    default public boolean removeIf(IntPredicate filter) {
        return this.removeIf(filter instanceof BytePredicate ? (BytePredicate)filter : filter::test);
    }

    public boolean retainAll(ByteCollection var1);

    @Override
    @Deprecated
    default public Stream<Byte> stream() {
        return Collection.super.stream();
    }

    default public IntStream intStream() {
        return StreamSupport.intStream(this.intSpliterator(), false);
    }

    @Override
    @Deprecated
    default public Stream<Byte> parallelStream() {
        return Collection.super.parallelStream();
    }

    default public IntStream intParallelStream() {
        return StreamSupport.intStream(this.intSpliterator(), true);
    }
}

