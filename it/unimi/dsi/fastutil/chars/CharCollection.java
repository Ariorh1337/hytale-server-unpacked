/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.CharIterable;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharPredicate;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface CharCollection
extends Collection<Character>,
CharIterable {
    @Override
    public CharIterator iterator();

    @Override
    default public IntIterator intIterator() {
        return CharIterable.super.intIterator();
    }

    @Override
    default public CharSpliterator spliterator() {
        return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(this), 320);
    }

    @Override
    default public IntSpliterator intSpliterator() {
        return CharIterable.super.intSpliterator();
    }

    @Override
    public boolean add(char var1);

    public boolean contains(char var1);

    public boolean rem(char var1);

    @Override
    @Deprecated
    default public boolean add(Character key) {
        return this.add(key.charValue());
    }

    @Override
    @Deprecated
    default public boolean contains(Object key) {
        if (key == null) {
            return false;
        }
        return this.contains(((Character)key).charValue());
    }

    @Override
    @Deprecated
    default public boolean remove(Object key) {
        if (key == null) {
            return false;
        }
        return this.rem(((Character)key).charValue());
    }

    public char[] toCharArray();

    @Deprecated
    default public char[] toCharArray(char[] a) {
        return this.toArray(a);
    }

    public char[] toArray(char[] var1);

    public boolean addAll(CharCollection var1);

    public boolean containsAll(CharCollection var1);

    public boolean removeAll(CharCollection var1);

    @Override
    @Deprecated
    default public boolean removeIf(Predicate<? super Character> filter) {
        return this.removeIf(filter instanceof CharPredicate ? (CharPredicate)filter : key -> filter.test(Character.valueOf(SafeMath.safeIntToChar(key))));
    }

    default public boolean removeIf(CharPredicate filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        CharIterator each = this.iterator();
        while (each.hasNext()) {
            if (!filter.test(each.nextChar())) continue;
            each.remove();
            removed = true;
        }
        return removed;
    }

    default public boolean removeIf(IntPredicate filter) {
        return this.removeIf(filter instanceof CharPredicate ? (CharPredicate)filter : filter::test);
    }

    public boolean retainAll(CharCollection var1);

    @Override
    @Deprecated
    default public Stream<Character> stream() {
        return Collection.super.stream();
    }

    default public IntStream intStream() {
        return StreamSupport.intStream(this.intSpliterator(), false);
    }

    @Override
    @Deprecated
    default public Stream<Character> parallelStream() {
        return Collection.super.parallelStream();
    }

    default public IntStream intParallelStream() {
        return StreamSupport.intStream(this.intSpliterator(), true);
    }
}

