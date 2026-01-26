/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractFloatValueParser;

abstract class AbstractJsonFloatingPointBitsFromCharSequence
extends AbstractFloatValueParser {
    AbstractJsonFloatingPointBitsFromCharSequence() {
    }

    public final long parseNumber(CharSequence str, int offset, int length) {
        int exponentOfTruncatedSignificand;
        boolean isSignificandTruncated;
        int exponent;
        int digitCount;
        boolean hasLeadingZero;
        boolean isNegative;
        int index = offset;
        int endIndex = AbstractJsonFloatingPointBitsFromCharSequence.checkBounds(str.length(), offset, length);
        char ch = AbstractJsonFloatingPointBitsFromCharSequence.charAt(str, index, endIndex);
        boolean bl = isNegative = ch == '-';
        if (isNegative && (ch = AbstractJsonFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) == '\u0000') {
            return 9221120237041090561L;
        }
        boolean bl2 = hasLeadingZero = ch == '0';
        if (hasLeadingZero && (ch = AbstractJsonFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) == '0') {
            return 9221120237041090561L;
        }
        long significand = 0L;
        int significandStartIndex = index;
        int integerDigitCount = -1;
        boolean illegal = false;
        while (index < endIndex) {
            ch = str.charAt(index);
            char digit = (char)(ch - 48);
            if (digit < '\n') {
                significand = 10L * significand + (long)digit;
            } else {
                if (ch != '.') break;
                illegal |= integerDigitCount >= 0;
                integerDigitCount = index - significandStartIndex;
            }
            ++index;
        }
        int significandEndIndex = index;
        if (integerDigitCount < 0) {
            integerDigitCount = digitCount = index - significandStartIndex;
            exponent = 0;
        } else {
            digitCount = index - significandStartIndex - 1;
            exponent = integerDigitCount - digitCount;
        }
        int expNumber = 0;
        if ((ch | 0x20) == 101) {
            char digit;
            boolean isExponentNegative;
            boolean bl3 = isExponentNegative = (ch = AbstractJsonFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) == '-';
            if (isExponentNegative || ch == '+') {
                ch = AbstractJsonFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex);
            }
            illegal |= (digit = (char)(ch - 48)) >= '\n';
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + digit;
            } while ((digit = (char)((ch = AbstractJsonFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) - 48)) < '\n');
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (illegal || index < endIndex || !hasLeadingZero && digitCount == 0) {
            return 9221120237041090561L;
        }
        if (digitCount > 19) {
            int truncatedDigitCount = 0;
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                ch = str.charAt(index);
                char digit = (char)(ch - 48);
                if (digit >= '\n') continue;
                if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) break;
                significand = 10L * significand + (long)digit;
                ++truncatedDigitCount;
            }
            isSignificandTruncated = index < significandEndIndex;
            exponentOfTruncatedSignificand = integerDigitCount - truncatedDigitCount + expNumber;
        } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
        }
        return this.valueOfFloatLiteral(str, offset, endIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
    }

    abstract long valueOfFloatLiteral(CharSequence var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}

