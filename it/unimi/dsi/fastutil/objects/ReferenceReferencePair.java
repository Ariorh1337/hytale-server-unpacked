/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceReferenceImmutablePair;

public interface ReferenceReferencePair<K, V>
extends Pair<K, V> {
    public static <K, V> ReferenceReferencePair<K, V> of(K left, V right) {
        return new ReferenceReferenceImmutablePair<K, V>(left, right);
    }
}

