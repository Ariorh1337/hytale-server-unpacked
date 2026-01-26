/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteFloatImmutablePair;
import java.util.Comparator;

public interface ByteFloatPair
extends Pair<Byte, Float> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteFloatPair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteFloatPair left(Byte l) {
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

    default public ByteFloatPair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteFloatPair first(Byte l) {
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

    default public ByteFloatPair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteFloatPair key(Byte l) {
        return this.key((byte)l);
    }

    public float rightFloat();

    @Override
    @Deprecated
    default public Float right() {
        return Float.valueOf(this.rightFloat());
    }

    default public ByteFloatPair right(float r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteFloatPair right(Float l) {
        return this.right(l.floatValue());
    }

    default public float secondFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float second() {
        return Float.valueOf(this.secondFloat());
    }

    default public ByteFloatPair second(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteFloatPair second(Float l) {
        return this.second(l.floatValue());
    }

    default public float valueFloat() {
        return this.rightFloat();
    }

    @Override
    @Deprecated
    default public Float value() {
        return Float.valueOf(this.valueFloat());
    }

    default public ByteFloatPair value(float r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteFloatPair value(Float l) {
        return this.value(l.floatValue());
    }

    public static ByteFloatPair of(byte left, float right) {
        return new ByteFloatImmutablePair(left, right);
    }

    public static Comparator<ByteFloatPair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Float.compare(x.rightFloat(), y.rightFloat());
        };
    }
}

