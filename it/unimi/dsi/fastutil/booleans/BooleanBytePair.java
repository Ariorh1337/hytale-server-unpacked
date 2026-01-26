/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanByteImmutablePair;
import java.util.Comparator;

public interface BooleanBytePair
extends Pair<Boolean, Byte> {
    public boolean leftBoolean();

    @Override
    @Deprecated
    default public Boolean left() {
        return this.leftBoolean();
    }

    default public BooleanBytePair left(boolean l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanBytePair left(Boolean l) {
        return this.left((boolean)l);
    }

    default public boolean firstBoolean() {
        return this.leftBoolean();
    }

    @Override
    @Deprecated
    default public Boolean first() {
        return this.firstBoolean();
    }

    default public BooleanBytePair first(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanBytePair first(Boolean l) {
        return this.first((boolean)l);
    }

    default public boolean keyBoolean() {
        return this.firstBoolean();
    }

    @Override
    @Deprecated
    default public Boolean key() {
        return this.keyBoolean();
    }

    default public BooleanBytePair key(boolean l) {
        return this.left(l);
    }

    @Deprecated
    default public BooleanBytePair key(Boolean l) {
        return this.key((boolean)l);
    }

    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public BooleanBytePair right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public BooleanBytePair right(Byte l) {
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

    default public BooleanBytePair second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanBytePair second(Byte l) {
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

    default public BooleanBytePair value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public BooleanBytePair value(Byte l) {
        return this.value((byte)l);
    }

    public static BooleanBytePair of(boolean left, byte right) {
        return new BooleanByteImmutablePair(left, right);
    }

    public static Comparator<BooleanBytePair> lexComparator() {
        return (x, y) -> {
            int t = Boolean.compare(x.leftBoolean(), y.leftBoolean());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

