/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharSet;
import java.util.Set;

final class CharSetOfOne
implements CharSet {
    private final char ch;

    CharSetOfOne(Set<Character> set) {
        if (set.size() != 1) {
            throw new IllegalArgumentException("set size must be 1, size=" + set.size());
        }
        this.ch = set.iterator().next().charValue();
    }

    @Override
    public boolean containsKey(char ch) {
        return this.ch == ch;
    }
}

