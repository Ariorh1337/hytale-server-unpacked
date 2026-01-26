/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface ByteBidirectionalIterator
extends ByteIterator,
ObjectBidirectionalIterator<Byte> {
    public byte previousByte();

    @Override
    @Deprecated
    default public Byte previous() {
        return this.previousByte();
    }

    @Override
    default public int back(int n) {
        int i = n;
        while (i-- != 0 && this.hasPrevious()) {
            this.previousByte();
        }
        return n - i - 1;
    }

    @Override
    default public int skip(int n) {
        return ByteIterator.super.skip(n);
    }
}

