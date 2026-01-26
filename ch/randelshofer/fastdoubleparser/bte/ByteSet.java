/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.bte;

import ch.randelshofer.fastdoubleparser.bte.ByteSetOfFew;
import ch.randelshofer.fastdoubleparser.bte.ByteSetOfNone;
import ch.randelshofer.fastdoubleparser.bte.ByteSetOfOne;
import ch.randelshofer.fastdoubleparser.bte.ByteToIntMap;
import java.util.LinkedHashSet;
import java.util.Set;

public interface ByteSet {
    public boolean containsKey(byte var1);

    public static ByteSet copyOf(Set<Character> set, boolean ignoreCase) {
        set = ByteSet.applyIgnoreCase(set, ignoreCase);
        switch (set.size()) {
            case 0: {
                return new ByteSetOfNone();
            }
            case 1: {
                return new ByteSetOfOne(set);
            }
        }
        return set.size() < 5 ? new ByteSetOfFew(set) : new ByteToIntMap(set);
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

