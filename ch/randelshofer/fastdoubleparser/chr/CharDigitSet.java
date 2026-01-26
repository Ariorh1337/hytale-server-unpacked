/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharToIntMap;
import ch.randelshofer.fastdoubleparser.chr.ConsecutiveCharDigitSet;
import java.util.List;

public interface CharDigitSet {
    public int toDigit(char var1);

    public static CharDigitSet copyOf(List<Character> digits) {
        boolean consecutive = true;
        char zeroDigit = digits.get(0).charValue();
        for (int i = 1; i < 10; ++i) {
            char current = digits.get(i).charValue();
            consecutive &= current == zeroDigit + i;
        }
        return consecutive ? new ConsecutiveCharDigitSet(digits.get(0).charValue()) : new CharToIntMap(digits);
    }
}

