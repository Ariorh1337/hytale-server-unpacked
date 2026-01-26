/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.chars.CharComparators;
import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface CharComparator
extends Comparator<Character> {
    @Override
    public int compare(char var1, char var2);

    default public CharComparator reversed() {
        return CharComparators.oppositeComparator(this);
    }

    @Override
    @Deprecated
    default public int compare(Character ok1, Character ok2) {
        return this.compare(ok1.charValue(), ok2.charValue());
    }

    default public CharComparator thenComparing(CharComparator second) {
        return (CharComparator & Serializable)(k1, k2) -> {
            int comp = this.compare(k1, k2);
            return comp == 0 ? second.compare(k1, k2) : comp;
        };
    }

    @Override
    default public Comparator<Character> thenComparing(Comparator<? super Character> second) {
        if (second instanceof CharComparator) {
            return this.thenComparing((CharComparator)second);
        }
        return Comparator.super.thenComparing(second);
    }
}

