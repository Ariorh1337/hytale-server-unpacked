/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectReferenceImmutablePair;

public interface ObjectReferencePair<K, V>
extends Pair<K, V> {
    public static <K, V> ObjectReferencePair<K, V> of(K left, V right) {
        return new ObjectReferenceImmutablePair<K, V>(left, right);
    }
}

