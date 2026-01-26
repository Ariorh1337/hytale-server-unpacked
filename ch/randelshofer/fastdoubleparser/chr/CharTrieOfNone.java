/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharTrie;

final class CharTrieOfNone
implements CharTrie {
    CharTrieOfNone() {
    }

    @Override
    public int match(CharSequence str) {
        return 0;
    }

    @Override
    public int match(CharSequence str, int startIndex, int endIndex) {
        return 0;
    }

    @Override
    public int match(char[] str) {
        return 0;
    }

    @Override
    public int match(char[] str, int startIndex, int endIndex) {
        return 0;
    }
}

