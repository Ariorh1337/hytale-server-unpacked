/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteIterable;
import it.unimi.dsi.fastutil.bytes.ByteIterator;

public final class ByteIterables {
    private ByteIterables() {
    }

    public static long size(ByteIterable iterable) {
        long c = 0L;
        ByteIterator byteIterator = iterable.iterator();
        while (byteIterator.hasNext()) {
            byte dummy = (Byte)byteIterator.next();
            ++c;
        }
        return c;
    }
}

