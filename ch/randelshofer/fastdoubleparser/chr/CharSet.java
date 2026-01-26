/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharSetOfFew;
import ch.randelshofer.fastdoubleparser.chr.CharSetOfNone;
import ch.randelshofer.fastdoubleparser.chr.CharSetOfOne;
import ch.randelshofer.fastdoubleparser.chr.CharToIntMap;
import java.util.LinkedHashSet;
import java.util.Set;

public interface CharSet {
    public boolean containsKey(char var1);

    public static CharSet copyOf(Set<Character> set, boolean ignoreCase) {
        set = CharSet.applyIgnoreCase(set, ignoreCase);
        switch (set.size()) {
            case 0: {
                return new CharSetOfNone();
            }
            case 1: {
                return new CharSetOfOne(set);
            }
        }
        return set.size() < 5 ? new CharSetOfFew(set) : new CharToIntMap(set);
    }

    public static Set<Character> applyIgnoreCase(Set<Character> set, boolean ignoreCase) {
        if (ignoreCase) {
            LinkedHashSet<Character> convertedSet = new LinkedHashSet<Character>();
            for (Character ch : set) {
                convertedSet.add(ch);
                char lc = Character.toLowerCase(ch.charValue());
                char uc = Character.toUpperCase(ch.charValue());
                char uclc = Character.toLowerCase(uc);
                convertedSet.add(Character.valueOf(lc));
                convertedSet.add(Character.valueOf(uc));
                convertedSet.add(Character.valueOf(uclc));
            }
            set = convertedSet;
        }
        return set;
    }
}

