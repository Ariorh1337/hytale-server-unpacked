/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteBooleanImmutablePair;
import java.util.Comparator;

public interface ByteBooleanPair
extends Pair<Byte, Boolean> {
    public byte leftByte();

    @Override
    @Deprecated
    default public Byte left() {
        return this.leftByte();
    }

    default public ByteBooleanPair left(byte l) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteBooleanPair left(Byte l) {
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

    default public ByteBooleanPair first(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteBooleanPair first(Byte l) {
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

    default public ByteBooleanPair key(byte l) {
        return this.left(l);
    }

    @Deprecated
    default public ByteBooleanPair key(Byte l) {
        return this.key((byte)l);
    }

    public boolean rightBoolean();

    @Override
    @Deprecated
    default public Boolean right() {
        return this.rightBoolean();
    }

    default public ByteBooleanPair right(boolean r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ByteBooleanPair right(Boolean l) {
        return this.right((boolean)l);
    }

    default public boolean secondBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean second() {
        return this.secondBoolean();
    }

    default public ByteBooleanPair second(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteBooleanPair second(Boolean l) {
        return this.second((boolean)l);
    }

    default public boolean valueBoolean() {
        return this.rightBoolean();
    }

    @Override
    @Deprecated
    default public Boolean value() {
        return this.valueBoolean();
    }

    default public ByteBooleanPair value(boolean r) {
        return this.right(r);
    }

    @Deprecated
    default public ByteBooleanPair value(Boolean l) {
        return this.value((boolean)l);
    }

    public static ByteBooleanPair of(byte left, boolean right) {
        return new ByteBooleanImmutablePair(left, right);
    }

    public static Comparator<ByteBooleanPair> lexComparator() {
        return (x, y) -> {
            int t = Byte.compare(x.leftByte(), y.leftByte());
            if (t != 0) {
                return t;
            }
            return Boolean.compare(x.rightBoolean(), y.rightBoolean());
        };
    }
}

