/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectByteImmutablePair;
import java.util.Comparator;

public interface ObjectBytePair<K>
extends Pair<K, Byte> {
    public byte rightByte();

    @Override
    @Deprecated
    default public Byte right() {
        return this.rightByte();
    }

    default public ObjectBytePair<K> right(byte r) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default public ObjectBytePair<K> right(Byte l) {
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

    default public ObjectBytePair<K> second(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectBytePair<K> second(Byte l) {
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

    default public ObjectBytePair<K> value(byte r) {
        return this.right(r);
    }

    @Deprecated
    default public ObjectBytePair<K> value(Byte l) {
        return this.value((byte)l);
    }

    public static <K> ObjectBytePair<K> of(K left, byte right) {
        return new ObjectByteImmutablePair<K>(left, right);
    }

    public static <K> Comparator<ObjectBytePair<K>> lexComparator() {
        return (x, y) -> {
            int t = ((Comparable)x.left()).compareTo(y.left());
            if (t != 0) {
                return t;
            }
            return Byte.compare(x.rightByte(), y.rightByte());
        };
    }
}

