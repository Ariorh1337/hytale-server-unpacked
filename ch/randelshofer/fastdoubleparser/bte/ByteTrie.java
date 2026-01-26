/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.bte;

import ch.randelshofer.fastdoubleparser.bte.ByteTrieOfFew;
import ch.randelshofer.fastdoubleparser.bte.ByteTrieOfFewIgnoreCase;
import ch.randelshofer.fastdoubleparser.bte.ByteTrieOfNone;
import ch.randelshofer.fastdoubleparser.bte.ByteTrieOfOne;
import ch.randelshofer.fastdoubleparser.bte.ByteTrieOfOneSingleByte;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public interface ByteTrie {
    default public int match(byte[] str) {
        return this.match(str, 0, str.length);
    }

    public int match(byte[] var1, int var2, int var3);

    public static ByteTrie copyOf(Set<String> set, boolean ignoreCase) {
        switch (set.size()) {
            case 0: {
                return new ByteTrieOfNone();
            }
            case 1: {
                String str = set.iterator().next();
                if (ignoreCase) {
                    switch (str.length()) {
                        case 0: {
                            return new ByteTrieOfNone();
                        }
                        case 1: {
                            LinkedHashSet<String> newSet = new LinkedHashSet<String>();
                            newSet.add(str.toLowerCase());
                            newSet.add(str.toUpperCase());
                            if (newSet.size() == 1) {
                                if (((String)newSet.iterator().next()).getBytes(StandardCharsets.UTF_8).length == 1) {
                                    return new ByteTrieOfOneSingleByte(newSet);
                                }
                                return new ByteTrieOfOne(newSet);
                            }
                            return new ByteTrieOfFew(newSet);
                        }
                    }
                    return new ByteTrieOfFewIgnoreCase(set);
                }
                if (set.iterator().next().getBytes(StandardCharsets.UTF_8).length == 1) {
                    return new ByteTrieOfOneSingleByte(set);
                }
                return new ByteTrieOfOne(set);
            }
        }
        if (ignoreCase) {
            return new ByteTrieOfFewIgnoreCase(set);
        }
        return new ByteTrieOfFew(set);
    }

    public static ByteTrie copyOfChars(Set<Character> set, boolean ignoreCase) {
        HashSet<String> strSet = new HashSet<String>(set.size() * 2);
        if (ignoreCase) {
            for (char ch : set) {
                String string = new String(new char[]{ch});
                strSet.add(string.toLowerCase());
                strSet.add(string.toUpperCase());
            }
            return ByteTrie.copyOf(strSet, false);
        }
        for (char ch : set) {
            strSet.add(new String(new char[]{ch}));
        }
        return ByteTrie.copyOf(strSet, ignoreCase);
    }
}

