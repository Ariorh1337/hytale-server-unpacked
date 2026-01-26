/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleByteImmutablePair;
import java.util.Comparator;

public interface DoubleBytePair
extends Pair<Double, Byte> {
    public double leftDouble();

    @Override
    @Deprecated
    default public Double left() {
        return this.leftDouble();
    }

    default public DoubleBytePair left(double l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleBytePair left(Double l) {
        return this.left((double)l);
    }

    default public double firstDouble() {
        return this.leftDouble();
    }

    @Override
    @Deprecated
    default public Double first() {
        return this.firstDouble();
    }

    default public DoubleBytePair first(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleBytePair first(Double l) {
        return this.first((double)l);
    }

    default public double keyDouble() {
        return this.firstDouble();
    }

    @Override
    @Deprecated
    default public Double key() {
        return this.keyDouble();
    }

    default public DoubleBytePair key(double l) {
        return this.left(l);
    }

    @Deprecated
    default public DoubleBytePair key(Double l) {
        return this.key((double)l);
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public DoubleBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public DoubleBytePair right(Byte l) {
        return this.right((byte)l);
    }

    default public byte secondByte() {
        return this.rightByte();
    }

    @Override
    @Deprecated
    default public Byte second() {
        return this.secondByte();
    }

    default public DoubleBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleBytePair second(Byte l) {
        return this.second((byte)l);
    }

    default public byte valueByte() {
        return this.rightByte();
    }

    @Override
    @Deprecated
    default public Byte value() {
        return this.valueByte();
    }

    default public DoubleBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public DoubleBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static DoubleBytePair of(double left, byte right) {
        return new DoubleByteImmutablePair(left, right);
    }

    public static Comparator<DoubleBytePair> lexComparator() {
        return (x, y) -> {
            int t = Double.compare(x.leftDouble(), y.leftDouble());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

