/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractFloatValueParser;
import ch.randelshofer.fastdoubleparser.FastDoubleSwar;

abstract class AbstractJavaFloatingPointBitsFromCharArray
extends AbstractFloatValueParser {
    private static final boolean CONDITIONAL_COMPILATION_PARSE_EIGHT_HEX_DIGITS = true;

    AbstractJavaFloatingPointBitsFromCharArray() {
    }

    private static int skipWhitespace(char[] str, int index, int endIndex) {
        while (index < endIndex && str[index] <= ' ') {
            ++index;
        }
        return index;
    }

    abstract long nan();

    abstract long negativeInfinity();

    private long parseDecFloatLiteral(char[] str, int index, int startIndex, int endIndex, boolean isNegative) {
        int exponentOfTruncatedSignificand;
        boolean isSignificandTruncated;
        int exponent;
        int digitCount;
        long significand = 0L;
        int significandStartIndex = index;
        int integerDigitCount = -1;
        boolean illegal = false;
        char ch = '\u0000';
        int swarLimit = Math.min(endIndex - 4, 0x40000000);
        while (index < endIndex) {
            ch = str[index];
            char digit = (char)(ch - 48);
            if (digit < '\n') {
                significand = 10L * significand + (long)digit;
            } else {
                int digits;
                if (ch != '.') break;
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
        illegal |= digitCount == 0 && significandEndIndex > significandStartIndex;
        int expNumber = 0;
        if ((ch | 0x20) == 101) {
            char digit;
            boolean isExponentNegative;
            boolean bl = isExponentNegative = (ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex)) == '-';
            if (isExponentNegative || ch == '+') {
                ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex);
            }
            illegal |= (digit = (char)(ch - 48)) >= '\n';
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + digit;
            } while ((digit = (char)((ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex)) - 48)) < '\n');
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (!illegal && digitCount == 0) {
            return this.parseNaNOrInfinity(str, index, endIndex, isNegative);
        }
        if ((ch | 0x22) == 102) {
            ++index;
        }
        index = AbstractJavaFloatingPointBitsFromCharArray.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex) {
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
        return this.valueOfFloatLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
    }

    public long parseFloatingPointLiteral(char[] str, int offset, int length) {
        boolean hasLeadingZero;
        boolean isNegative;
        int endIndex = AbstractJavaFloatingPointBitsFromCharArray.checkBounds(str.length, offset, length);
        int index = AbstractJavaFloatingPointBitsFromCharArray.skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            return 9221120237041090561L;
        }
        char ch = str[index];
        boolean bl = isNegative = ch == '-';
        if ((isNegative || ch == '+') && (ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex)) == '\u0000') {
            return 9221120237041090561L;
        }
        boolean bl2 = hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            if (((ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex)) | 0x20) == 120) {
                return this.parseHexFloatLiteral(str, index + 1, offset, endIndex, isNegative);
            }
            --index;
        }
        return this.parseDecFloatLiteral(str, index, offset, endIndex, isNegative);
    }

    private long parseHexFloatLiteral(char[] str, int index, int startIndex, int endIndex, boolean isNegative) {
        boolean isSignificandTruncated;
        boolean hasExponent;
        int digitCount;
        long significand = 0L;
        int exponent = 0;
        int significandStartIndex = index;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        char ch = '\u0000';
        while (index < endIndex) {
            ch = str[index];
            int hexValue = AbstractJavaFloatingPointBitsFromCharArray.lookupHex(ch);
            if (hexValue >= 0) {
                significand = significand << 4 | (long)hexValue;
            } else {
                long parsed;
                if (hexValue != -4) break;
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                while (index < endIndex - 8 && (parsed = this.tryToParseEightHexDigits(str, index + 1)) >= 0L) {
                    significand = (significand << 32) + parsed;
                    index += 8;
                }
            }
            ++index;
        }
        int significandEndIndex = index;
        if (virtualIndexOfPoint < 0) {
            digitCount = significandEndIndex - significandStartIndex;
            virtualIndexOfPoint = significandEndIndex;
        } else {
            digitCount = significandEndIndex - significandStartIndex - 1;
            exponent = Math.min(virtualIndexOfPoint - index + 1, 1024) * 4;
        }
        int expNumber = 0;
        boolean bl = hasExponent = (ch | 0x20) == 112;
        if (hasExponent) {
            char digit;
            boolean isExponentNegative;
            boolean bl2 = isExponentNegative = (ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex)) == '-';
            if (isExponentNegative || ch == '+') {
                ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex);
            }
            illegal |= (digit = (char)(ch - 48)) >= '\n';
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + digit;
            } while ((digit = (char)((ch = AbstractJavaFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex)) - 48)) < '\n');
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if ((ch | 0x22) == 102) {
            ++index;
        }
        index = AbstractJavaFloatingPointBitsFromCharArray.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex || digitCount == 0 || !hasExponent) {
            return 9221120237041090561L;
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 16) {
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                ch = str[index];
                int hexValue = AbstractJavaFloatingPointBitsFromCharArray.lookupHex(ch);
                if (hexValue >= 0) {
                    if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) break;
                    significand = significand << 4 | (long)hexValue;
                    continue;
                }
                ++skipCountInTruncatedDigits;
            }
            isSignificandTruncated = index < significandEndIndex;
        } else {
            isSignificandTruncated = false;
        }
        return this.valueOfHexLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, (virtualIndexOfPoint - index + skipCountInTruncatedDigits) * 4 + expNumber);
    }

    private long parseNaNOrInfinity(char[] str, int index, int endIndex, boolean isNegative) {
        if (index < endIndex) {
            if (str[index] == 'N') {
                if (index + 2 < endIndex && str[index + 1] == 'a' && str[index + 2] == 'N' && (index = AbstractJavaFloatingPointBitsFromCharArray.skipWhitespace(str, index + 3, endIndex)) == endIndex) {
                    return this.nan();
                }
            } else if (index + 7 < endIndex && str[index] == 'I' && str[index + 1] == 'n' && str[index + 2] == 'f' && str[index + 3] == 'i' && str[index + 4] == 'n' && str[index + 5] == 'i' && str[index + 6] == 't' && str[index + 7] == 'y' && (index = AbstractJavaFloatingPointBitsFromCharArray.skipWhitespace(str, index + 8, endIndex)) == endIndex) {
                return isNegative ? this.negativeInfinity() : this.positiveInfinity();
            }
        }
        return 9221120237041090561L;
    }

    abstract long positiveInfinity();

    private long tryToParseEightHexDigits(char[] str, int offset) {
        return FastDoubleSwar.tryToParseEightHexDigits(str, offset);
    }

    abstract long valueOfFloatLiteral(char[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);

    abstract long valueOfHexLiteral(char[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}

