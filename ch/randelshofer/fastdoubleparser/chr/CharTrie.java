/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharTrieOfFew;
import ch.randelshofer.fastdoubleparser.chr.CharTrieOfFewIgnoreCase;
import ch.randelshofer.fastdoubleparser.chr.CharTrieOfNone;
import ch.randelshofer.fastdoubleparser.chr.CharTrieOfOne;
import ch.randelshofer.fastdoubleparser.chr.CharTrieOfOneSingleChar;
import java.util.Set;

public interface CharTrie {
    default public int match(CharSequence str) {
        return this.match(str, 0, str.length());
    }

    default public int match(char[] str) {
        return this.match(str, 0, str.length);
    }

    public int match(CharSequence var1, int var2, int var3);

    public int match(char[] var1, int var2, int var3);

    public static CharTrie copyOf(Set<String> set, boolean ignoreCase) {
        switch (set.size()) {
            case 0: {
                return new CharTrieOfNone();
            }
            case 1: {
                if (set.iterator().next().length() == 1) {
                    return ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfOneSingleChar(set);
                }
                return ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfOne(set);
            }
        }
        return ignoreCase ? new CharTrieOfFewIgnoreCase(set) : new CharTrieOfFew(set);
    }
}

