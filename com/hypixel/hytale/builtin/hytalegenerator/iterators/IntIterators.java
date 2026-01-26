/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.iterators;

import com.hypixel.hytale.builtin.hytalegenerator.iterators.BackwardIntIterator;
import com.hypixel.hytale.builtin.hytalegenerator.iterators.ForwardIntIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import javax.annotation.Nonnull;

public class IntIterators {
    @Nonnull
    public static IntIterator range(int start, int end) {
        if (start <= end) {
            return new ForwardIntIterator(start, end);
        }
        return new BackwardIntIterator(end, start);
    }
}

