/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;

public interface ObjectBidirectionalIterable<K>
extends ObjectIterable<K> {
    @Override
    public ObjectBidirectionalIterator<K> iterator();
}

