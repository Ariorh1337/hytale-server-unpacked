/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteDoubleImmutablePair;
import java.util.Comparator;

public interface ByteDoublePair
extends Pair<Byte, Double> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteDoublePair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteDoublePair left(Byte l) {
        return this.left((byte)l);
    }

    default public byte firstByte() {
        return this.leftByte();
    }

    @Override
    @Deprecated
    default public Byte first() {
        return this.firstByte();
    }

    default public ByteDoublePair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteDoublePair first(Byte l) {
        return this.first((byte)l);
    }

    default public byte keyByte() {
        return this.firstByte();
    }

    @Override
    @Deprecated
    default public Byte key() {
        return this.keyByte();
    }

    default public ByteDoublePair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteDoublePair key(Byte l) {
        return this.key((byte)l);
    }

    public double rightDouble();

    @Override
    @Deprecated
    default public Double right() {
        return this.rightDouble();
    }

    default public ByteDoublePair right(double r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteDoublePair right(Double l) {
        return this.right((double)l);
    }

    default public double secondDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double second() {
        return this.secondDouble();
    }

    default public ByteDoublePair second(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteDoublePair second(Double l) {
        return this.second((double)l);
    }

    default public double valueDouble() {
        return this.rightDouble();
    }

    @Override
    @Deprecated
    default public Double value() {
        return this.valueDouble();
    }

    default public ByteDoublePair value(double r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteDoublePair value(Double l) {
        return this.value((double)l);
    }

    public static ByteDoublePair of(byte left, double right) {
        return new ByteDoubleImmutablePair(left, right);
    }

    public static Comparator<ByteDoublePair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Double.compare(x.rightDouble(), y.rightDouble());
        };
    }
}

