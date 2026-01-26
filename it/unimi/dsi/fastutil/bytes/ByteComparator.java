/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface ByteComparator
extends Comparator<Byte> {
    @Override
    public int compare(byte var1, byte var2);

    default public ByteComparator reversed() {
        return ByteComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Byte ok1, Byte ok2) {
        return this.compare((byte)ok1, (byte)ok2);
    }

    default public ByteComparator thenComparing(ByteComparator second) {
        return (ByteComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Byte> thenComparing(Comparator<? super Byte> second) {
        if (second instanceof ByteComparator) {
            return this.thenComparing((ByteComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

