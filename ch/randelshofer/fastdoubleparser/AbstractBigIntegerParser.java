/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractNumberParser;

abstract class AbstractBigIntegerParser
extends AbstractNumberParser {
    private static final int MAX_DECIMAL_DIGITS = 646456993;
    private static final int MAX_HEX_DIGITS = 0x20000000;
    static final int RECURSION_THRESHOLD = 400;

    AbstractBigIntegerParser() {
    }

    protected static boolean hasManyDigits(int length) {
        return length > 18;
    }

    protected static void checkHexBigIntegerBounds(int numDigits) {
        if (numDigits > 0x20000000) {
            throw new NumberFormatException("value exceeds limits");
        }
    }

    protected static void checkDecBigIntegerBounds(int numDigits) {
        if (numDigits > 646456993) {
            throw new NumberFormatException("value exceeds limits");
        }
    }
}

