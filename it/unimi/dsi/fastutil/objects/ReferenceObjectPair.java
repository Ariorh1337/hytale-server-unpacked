/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceObjectImmutablePair;

public interface ReferenceObjectPair<K, V>
extends Pair<K, V> {
    public static <K, V> ReferenceObjectPair<K, V> of(K left, V right) {
        return new ReferenceObjectImmutablePair<K, V>(left, right);
    }
}

