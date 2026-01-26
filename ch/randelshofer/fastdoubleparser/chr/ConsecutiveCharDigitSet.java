/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser.chr;

import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;

final class ConsecutiveCharDigitSet
implements CharDigitSet {
    private final char zeroDigit;

    public ConsecutiveCharDigitSet(char zeroDigit) {
        this.zeroDigit = zeroDigit;
    }

    @Override
    public int toDigit(char ch) {
        return (char)(ch - this.zeroDigit);
    }
}

