/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.fastutil;

import com.hypixel.hytale.common.fastutil.HLongSet;
import com.hypixel.hytale.function.predicate.LongTriIntBiObjPredicate;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import javax.annotation.Nonnull;

public class HLongOpenHashSet
extends LongOpenHashSet
implements HLongSet {
    @Override
    public <T, V> void removeIf(@Nonnull LongTriIntBiObjPredicate<T, V> predicate, int ia, int ib, int ic, T obj1, V obj2) {
        int pos = this.n;
        int last = -1;
        int c = this.size;
        boolean mustReturnNull = this.containsNull;
        LongArrayList wrapped = null;
        while (c != 0) {
            long value = 0L;
            --c;
            if (mustReturnNull) {
                mustReturnNull = false;
                last = this.n;
                value = this.key[this.n];
            } else {
                long[] key1 = this.key;
                while (--pos >= 0) {
                    if (key1[pos] == 0L) continue;
                    last = pos;
                    value = key1[last];
                    break;
                }
                if (pos < 0) {
                    last = Integer.MIN_VALUE;
                    value = wrapped.getLong(-pos - 1);
                }
            }
            if (!predicate.test(value, ia, ib, ic, obj1, obj2)) continue;
            if (last == this.n) {
                this.containsNull = false;
                this.key[this.n] = 0L;
                --this.size;
                last = -1;
                continue;
            }
            if (pos >= 0) {
                int pos1 = last;
                long[] key1 = this.key;
                block2: while (true) {
                    long curr;
                    int last1 = pos1;
                    pos1 = pos1 + 1 & this.mask;
                    while (true) {
                        if ((curr = key1[pos1]) == 0L) break block2;
                        int slot = (int)HashCommon.mix(curr) & this.mask;
                        if (last1 <= pos1 ? last1 >= slot || slot > pos1 : last1 >= slot && slot > pos1) break;
                        pos1 = pos1 + 1 & this.mask;
                    }
                    if (pos1 < last1) {
                        if (wrapped == null) {
                            wrapped = new LongArrayList(2);
                        }
                        wrapped.add(key1[pos1]);
                    }
                    key1[last1] = curr;
                }
                key1[last1] = 0L;
                --this.size;
                last = -1;
                continue;
            }
            super.remove(wrapped.getLong(-pos - 1));
            last = -1;
        }
    }
}

