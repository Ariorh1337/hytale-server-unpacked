/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharIterable;
import it.unimi.dsi.fastutil.chars.CharIterator;

public final class CharIterables {
    private CharIterables() {
    }

    public static long size(CharIterable iterable) {
        long c = 0L;
        CharIterator charIterator = iterable.iterator();
        while (charIterator.hasNext()) {
            char dummy = ((Character)charIterator.next()).charValue();
            ++c;
        }
        return c;
    }
}

