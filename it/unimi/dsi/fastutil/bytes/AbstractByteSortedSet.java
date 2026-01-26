/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.bytes.AbstractByteSet;
import it.unimi.dsi.fastutil.bytes.ByteBidirectionalIterator;
import it.unimi.dsi.fastutil.bytes.ByteSortedSet;

public abstract class AbstractByteSortedSet
extends AbstractByteSet
implements ByteSortedSet {
    protected AbstractByteSortedSet() {
    }

    @Override
    public abstract ByteBidirectionalIterator iterator();
}

