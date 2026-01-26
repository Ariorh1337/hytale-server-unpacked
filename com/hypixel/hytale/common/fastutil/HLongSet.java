/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.fastutil;

import com.hypixel.hytale.function.predicate.LongTriIntBiObjPredicate;
import it.unimi.dsi.fastutil.longs.LongSet;

public interface HLongSet
extends LongSet {
    public <T, V> void removeIf(LongTriIntBiObjPredicate<T, V> var1, int var2, int var3, int var4, T var5, V var6);
}

