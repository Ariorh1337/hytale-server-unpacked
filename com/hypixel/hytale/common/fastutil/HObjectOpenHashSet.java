/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.fastutil;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HObjectOpenHashSet<K>
extends ObjectOpenHashSet<K> {
    @Nullable
    public K first() {
        if (this.containsNull) {
            return (K)this.key[this.n];
        }
        Object[] key = this.key;
        int pos = this.n;
        while (pos-- != 0) {
            if (key[pos] == null) continue;
            return (K)key[pos];
        }
        return null;
    }

    public void pushInto(@Nonnull Collection<K> c) {
        if (this.containsNull) {
            c.add(this.key[this.n]);
        }
        Object[] key = this.key;
        int pos = this.n;
        while (pos-- != 0) {
            if (key[pos] == null) continue;
            c.add(key[pos]);
        }
    }
}

