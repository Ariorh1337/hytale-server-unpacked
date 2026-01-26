/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteIterable;

public interface ByteBidirectionalIterable
extends ByteIterable {
    @Override
    public ByteBidirectionalIterator iterator();
}

