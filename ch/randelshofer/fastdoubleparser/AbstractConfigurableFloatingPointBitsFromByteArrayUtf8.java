/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractFloatValueParser;
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols;
import ch.randelshofer.fastdoubleparser.SlowDoubleConversionPath;
import ch.randelshofer.fastdoubleparser.bte.ByteDigitSet;
import ch.randelshofer.fastdoubleparser.bte.ByteTrie;

abstract class AbstractConfigurableFloatingPointBitsFromByteArrayUtf8
extends AbstractFloatValueParser {
    private final ByteDigitSet digitSet;
    private final ByteTrie minusSign;
    private final ByteTrie plusSign;
    private final ByteTrie decimalSeparator;
    private final ByteTrie groupingSeparator;
    private final ByteTrie nan;
    private final ByteTrie infinity;
    private final ByteTrie exponentSeparator;

    public AbstractConfigurableFloatingPointBitsFromByteArrayUtf8(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = ByteTrie.copyOfChars(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = ByteTrie.copyOfChars(symbols.groupingSeparator(), ignoreCase);
        this.digitSet = ByteDigitSet.copyOf(symbols.digits());
        this.minusSign = ByteTrie.copyOfChars(symbols.minusSign(), ignoreCase);
        this.exponentSeparator = ByteTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
        this.plusSign = ByteTrie.copyOfChars(symbols.plusSign(), ignoreCase);
        this.nan = ByteTrie.copyOf(symbols.nan(), ignoreCase);
        this.infinity = ByteTrie.copyOf(symbols.infinity(), ignoreCase);
    }

    abstract long nan();

    abstract long negativeInfinity();

    public final long parseFloatingPointLiteral(byte[] str, int offset, int length) {
        int exponentOfTruncatedSignificand;
        boolean isSignificandTruncated;
        int count;
        int exponent;
        int digitCount;
        boolean isSignificandSigned;
        boolean isNegative;
        int index = offset;
        int endIndex = AbstractConfigurableFloatingPointBitsFromByteArrayUtf8.checkBounds(str.length, offset, length);
        int matchCount = this.minusSign.match(str, index, endIndex);
        boolean bl = isNegative = matchCount > 0;
        if (isNegative) {
            index += matchCount;
        } else {
            matchCount = this.plusSign.match(str, index, endIndex);
            index += matchCount;
        }
        boolean bl2 = isSignificandSigned = matchCount > 0;
        if (index == endIndex) {
            return 9221120237041090561L;
        }
        long significand = 0L;
        int significandStartIndex = index;
        int decimalSeparatorIndex = -1;
        int integerDigitCount = -1;
        int groupingCount = 0;
        boolean illegal = false;
        while (index < endIndex) {
            byte ch = str[index];
            int digit = this.digitSet.toDigit(ch);
            if (digit < 10) {
                significand = 10L * significand + (long)digit;
            } else {
                matchCount = this.decimalSeparator.match(str, index, endIndex);
                if (matchCount > 0) {
                    illegal |= integerDigitCount >= 0;
                    decimalSeparatorIndex = index;
                    integerDigitCount = index - significandStartIndex - groupingCount;
                    index += matchCount - 1;
                } else {
                    matchCount = this.groupingSeparator.match(str, index, endIndex);
                    if (matchCount <= 0) break;
                    illegal |= decimalSeparatorIndex != -1;
                    groupingCount += matchCount;
                    index += matchCount - 1;
                }
            }
            ++index;
        }
        int significandEndIndex = index;
        if (integerDigitCount < 0) {
            integerDigitCount = digitCount = significandEndIndex - significandStartIndex - groupingCount;
            decimalSeparatorIndex = significandEndIndex;
            exponent = 0;
        } else {
            digitCount = significandEndIndex - significandStartIndex - 1 - groupingCount;
            exponent = integerDigitCount - digitCount;
        }
        illegal |= digitCount == 0 && significandEndIndex > significandStartIndex;
        if (index < endIndex && !isSignificandSigned) {
            matchCount = this.minusSign.match(str, index, endIndex);
            if (matchCount > 0) {
                isNegative = true;
                index += matchCount;
            } else {
                index += this.plusSign.match(str, index, endIndex);
            }
        }
        int expNumber = 0;
        boolean isExponentSigned = false;
        if (digitCount > 0 && (count = this.exponentSeparator.match(str, index, endIndex)) > 0) {
            boolean isExponentNegative;
            byte ch = AbstractConfigurableFloatingPointBitsFromByteArrayUtf8.charAt(str, index += count, endIndex);
            matchCount = this.minusSign.match(str, index, endIndex);
            boolean bl3 = isExponentNegative = matchCount > 0;
            index = isExponentNegative ? (index += matchCount) : (index += this.plusSign.match(str, index, endIndex));
            ch = AbstractConfigurableFloatingPointBitsFromByteArrayUtf8.charAt(str, index, endIndex);
            int digit = this.digitSet.toDigit(ch);
            illegal |= digit >= 10;
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + digit;
            } while ((digit = this.digitSet.toDigit(ch = AbstractConfigurableFloatingPointBitsFromByteArrayUtf8.charAt(str, ++index, endIndex))) < 10);
            if (!isExponentSigned) {
                boolean isExponentNegative2;
                matchCount = this.minusSign.match(str, index, endIndex);
                boolean bl4 = isExponentNegative2 = matchCount > 0;
                if (isExponentNegative2 || (matchCount = this.plusSign.match(str, index, endIndex)) > 0) {
                    isExponentNegative |= isExponentNegative2;
                    index += matchCount;
                }
            }
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (!illegal && digitCount == 0) {
            return this.parseNaNOrInfinity(str, index, endIndex, isNegative, isSignificandSigned);
        }
        if (illegal || index < endIndex) {
            return 9221120237041090561L;
        }
        if (digitCount > 19) {
            int truncatedDigitCount = 0;
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                byte ch = str[index];
                int digit = this.digitSet.toDigit(ch);
                if (digit >= 10) continue;
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
        return this.valueOfFloatLiteral(str, significandStartIndex, decimalSeparatorIndex, decimalSeparatorIndex + 1, significandEndIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand, expNumber, offset, endIndex);
    }

    private long parseNaNOrInfinity(byte[] str, int index, int endIndex, boolean isNegative, boolean isSignificandSigned) {
        int nanMatch = this.nan.match(str, index, endIndex);
        if (nanMatch > 0) {
            int matchCount;
            if (!((index += nanMatch) >= endIndex || isSignificandSigned || (matchCount = this.minusSign.match(str, index, endIndex)) <= 0 && (matchCount = this.plusSign.match(str, index, endIndex)) <= 0)) {
                index += matchCount;
            }
            return index == endIndex ? this.nan() : 9221120237041090561L;
        }
        int infinityMatch = this.infinity.match(str, index, endIndex);
        if (infinityMatch > 0) {
            if ((index += infinityMatch) < endIndex && !isSignificandSigned) {
                int matchCount = this.minusSign.match(str, index, endIndex);
                boolean bl = isNegative = matchCount > 0;
                if (isNegative || (matchCount = this.plusSign.match(str, index, endIndex)) > 0) {
                    index += matchCount;
                }
            }
            if (index == endIndex) {
                return isNegative ? this.negativeInfinity() : this.positiveInfinity();
            }
        }
        return 9221120237041090561L;
    }

    abstract long positiveInfinity();

    abstract long valueOfFloatLiteral(byte[] var1, int var2, int var3, int var4, int var5, boolean var6, long var7, int var9, boolean var10, int var11, int var12, int var13, int var14);

    protected double slowPathToDouble(byte[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, int exponentValue) {
        return SlowDoubleConversionPath.toDouble(str, this.digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, (long)exponentValue);
    }
}

