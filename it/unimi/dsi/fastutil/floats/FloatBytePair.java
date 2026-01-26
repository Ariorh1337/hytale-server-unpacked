/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.FloatByteImmutablePair;
import java.util.Comparator;

public interface FloatBytePair
extends Pair<Float, Byte> {
    public float leftFloat();

    @Override
    @Deprecated
    default public Float left() {
        return Float.valueOf(this.leftFloat());
    }

    default public FloatBytePair left(float l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatBytePair left(Float l) {
        return this.left(l.floatValue());
    }

    default public float firstFloat() {
        return this.leftFloat();
    }

    @Override
    @Deprecated
    default public Float first() {
        return Float.valueOf(this.firstFloat());
    }

    default public FloatBytePair first(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatBytePair first(Float l) {
        return this.first(l.floatValue());
    }

    default public float keyFloat() {
        return this.firstFloat();
    }

    @Override
    @Deprecated
    default public Float key() {
        return Float.valueOf(this.keyFloat());
    }

    default public FloatBytePair key(float l) {
        return this.left(l);
    }

    @Deprecated
    default public FloatBytePair key(Float l) {
        return this.key(l.floatValue());
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public FloatBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public FloatBytePair right(Byte l) {
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

    default public FloatBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatBytePair second(Byte l) {
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

    default public FloatBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public FloatBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static FloatBytePair of(float left, byte right) {
        return new FloatByteImmutablePair(left, right);
    }

    public static Comparator<FloatBytePair> lexComparator() {
        return (x, y) -> {
            int t = Float.compare(x.leftFloat(), y.leftFloat());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

