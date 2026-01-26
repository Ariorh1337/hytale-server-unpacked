/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongCharImmutablePair;
import java.util.Comparator;

public interface LongCharPair
extends Pair<Long, Character> {
    public long leftLong();

    @Override
    @Deprecated
    default public Long left() {
        return this.leftLong();
    }

    default public LongCharPair left(long l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongCharPair left(Long l) {
        return this.left((long)l);
    }

    default public long firstLong() {
        return this.leftLong();
    }

    @Override
    @Deprecated
    default public Long first() {
        return this.firstLong();
    }

    default public LongCharPair first(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongCharPair first(Long l) {
        return this.first((long)l);
    }

    default public long keyLong() {
        return this.firstLong();
    }

    @Override
    @Deprecated
    default public Long key() {
        return this.keyLong();
    }

    default public LongCharPair key(long l) {
        return this.left(l);
    }

    @Deprecated
    default public LongCharPair key(Long l) {
        return this.key((long)l);
    }

    public char rightChar();

    @Override
    @Deprecated
    default public Character right() {
        return Character.valueOf(this.rightChar());
    }

    default public LongCharPair right(char r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public LongCharPair right(Character l) {
        return this.right(l.charValue());
    }

    default public char secondChar() {
        return this.rightChar();
    }

    @Override
    @Deprecated
    default public Character second() {
        return Character.valueOf(this.secondChar());
    }

    default public LongCharPair second(char r) {
        return this.right(r);
    }

    @Deprecated
    default public LongCharPair second(Character l) {
        return this.second(l.charValue());
    }

    default public char valueChar() {
        return this.rightChar();
    }

    @Override
    @Deprecated
    default public Character value() {
        return Character.valueOf(this.valueChar());
    }

    default public LongCharPair value(char r) {
        return this.right(r);
    }

    @Deprecated
    default public LongCharPair value(Character l) {
        return this.value(l.charValue());
    }

    public static LongCharPair of(long left, char right) {
        return new LongCharImmutablePair(left, right);
    }

    public static Comparator<LongCharPair> lexComparator() {
        return (x, y) -> {
            int t = Long.compare(x.leftLong(), y.leftLong());
            if (t != 0) {
                return t;
            }
            return Character.compare(x.rightChar(), y.rightChar());
        };
    }
}

