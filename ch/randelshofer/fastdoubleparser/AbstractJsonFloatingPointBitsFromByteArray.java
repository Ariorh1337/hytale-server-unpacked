/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractFloatValueParser;
import ch.randelshofer.fastdoubleparser.FastDoubleSwar;

abstract class AbstractJsonFloatingPointBitsFromByteArray
extends AbstractFloatValueParser {
    AbstractJsonFloatingPointBitsFromByteArray() {
    }

    public final long parseNumber(byte[] str, int offset, int length) {
        int exponentOfTruncatedSignificand;
        boolean isSignificandTruncated;
        int exponent;
        int digitCount;
        boolean hasLeadingZero;
        boolean isNegative;
        int index = offset;
        int endIndex = AbstractJsonFloatingPointBitsFromByteArray.checkBounds(str.length, offset, length);
        byte ch = AbstractJsonFloatingPointBitsFromByteArray.charAt(str, index, endIndex);
        boolean bl = isNegative = ch == 45;
        if (isNegative && (ch = AbstractJsonFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) == 0) {
            return 9221120237041090561L;
        }
        boolean bl2 = hasLeadingZero = ch == 48;
        if (hasLeadingZero && (ch = AbstractJsonFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) == 48) {
            return 9221120237041090561L;
        }
        long significand = 0L;
        int significandStartIndex = index;
        int integerDigitCount = -1;
        int swarLimit = Math.min(endIndex - 4, 0x40000000);
        boolean illegal = false;
        while (index < endIndex) {
            ch = str[index];
            char digit = (char)(ch - 48);
            if (digit < '\n') {
                significand = 10L * significand + (long)digit;
            } else {
                int digits;
                if (ch != 46) break;
                illegal |= integerDigitCount >= 0;
                integerDigitCount = index - significandStartIndex;
                while (index < swarLimit && (digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1)) >= 0) {
                    significand = 10000L * significand + (long)digits;
                    index += 4;
                }
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
            boolean bl3 = isExponentNegative = (ch = AbstractJsonFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) == 45;
            if (isExponentNegative || ch == 43) {
                ch = AbstractJsonFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex);
            }
            illegal |= (digit = (char)(ch - 48)) >= '\n';
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + digit;
            } while ((digit = (char)((ch = AbstractJsonFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) - 48)) < '\n');
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
                ch = str[index];
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

    abstract long valueOfFloatLiteral(byte[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}

