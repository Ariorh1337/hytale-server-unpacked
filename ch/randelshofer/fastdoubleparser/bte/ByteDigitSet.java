/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.bte;

import ch.randelshofer.fastdoubleparser.bte.ByteToIntMap;
import ch.randelshofer.fastdoubleparser.bte.ConsecutiveByteDigitSet;
import java.util.List;

public interface ByteDigitSet {
    public int toDigit(byte var1);

    public static ByteDigitSet copyOf(List<Character> digits) {
        boolean consecutive = true;
        char zeroDigit = digits.get(0).charValue();
        for (int i = 1; i < 10; ++i) {
            char current = digits.get(i).charValue();
            consecutive &= current == zeroDigit + i;
        }
        return consecutive ? new ConsecutiveByteDigitSet(digits.get(0).charValue()) : new ByteToIntMap(digits);
    }
}

