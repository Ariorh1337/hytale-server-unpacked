/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractNumberParser;

abstract class AbstractBigDecimalParser
extends AbstractNumberParser {
    public static final int MANY_DIGITS_THRESHOLD = 32;
    static final int RECURSION_THRESHOLD = 400;
    protected static final long MAX_EXPONENT_NUMBER = Integer.MAX_VALUE;
    protected static final int MAX_DIGITS_WITHOUT_LEADING_ZEROS = 646456993;

    AbstractBigDecimalParser() {
    }

    protected static boolean hasManyDigits(int length) {
        return length >= 32;
    }

    protected static void checkParsedBigDecimalBounds(boolean illegal, int index, int endIndex, int digitCount, long exponent) {
        if (illegal || index < endIndex) {
            throw new NumberFormatException("illegal syntax");
        }
        if (exponent <= Integer.MIN_VALUE || exponent > Integer.MAX_VALUE || digitCount > 646456993) {
            throw new NumberFormatException("value exceeds limits");
        }
    }
}

