/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractFloatValueParser;
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols;
import ch.randelshofer.fastdoubleparser.SlowDoubleConversionPath;
import ch.randelshofer.fastdoubleparser.bte.ByteDigitSet;
import ch.randelshofer.fastdoubleparser.bte.ByteSet;
import ch.randelshofer.fastdoubleparser.bte.ByteTrie;

abstract class AbstractConfigurableFloatingPointBitsFromByteArrayAscii
extends AbstractFloatValueParser {
    private final ByteDigitSet digitSet;
    private final ByteSet minusSign;
    private final ByteSet plusSign;
    private final ByteSet decimalSeparator;
    private final ByteSet groupingSeparator;
    private final ByteTrie nan;
    private final ByteTrie infinity;
    private final ByteTrie exponentSeparator;

    public AbstractConfigurableFloatingPointBitsFromByteArrayAscii(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = ByteSet.copyOf(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = ByteSet.copyOf(symbols.groupingSeparator(), ignoreCase);
        this.digitSet = ByteDigitSet.copyOf(symbols.digits());
        this.minusSign = ByteSet.copyOf(symbols.minusSign(), ignoreCase);
        this.exponentSeparator = ByteTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
        this.plusSign = ByteSet.copyOf(symbols.plusSign(), ignoreCase);
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
        int endIndex = AbstractConfigurableFloatingPointBitsFromByteArrayAscii.checkBounds(str.length, offset, length);
        int index = offset;
        byte ch = str[index];
        boolean isNegative = this.minusSign.containsKey(ch);
        boolean isSignificandSigned = false;
        if (isNegative || this.plusSign.containsKey(ch)) {
            isSignificandSigned = true;
            if (++index == endIndex) {
                return 9221120237041090561L;
            }
        }
        long significand = 0L;
        int significandStartIndex = index;
        int decimalSeparatorIndex = -1;
        int integerDigitCount = -1;
        int groupingCount = 0;
        boolean illegal = false;
        while (index < endIndex) {
            ch = str[index];
            int digit = this.digitSet.toDigit(ch);
            if (digit < 10) {
                significand = 10L * significand + (long)digit;
            } else if (this.decimalSeparator.containsKey(ch)) {
                illegal |= integerDigitCount >= 0;
                decimalSeparatorIndex = index;
                integerDigitCount = index - significandStartIndex - groupingCount;
            } else {
                if (!this.groupingSeparator.containsKey(ch)) break;
                illegal |= decimalSeparatorIndex != -1;
                ++groupingCount;
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
        if (index < endIndex && !isSignificandSigned && ((isNegative = this.minusSign.containsKey(ch)) || this.plusSign.containsKey(ch))) {
            ++index;
        }
        int expNumber = 0;
        boolean isExponentSigned = false;
        if (digitCount > 0 && (count = this.exponentSeparator.match(str, index, endIndex)) > 0) {
            int digit;
            ch = AbstractConfigurableFloatingPointBitsFromByteArrayAscii.charAt(str, index += count, endIndex);
            boolean isExponentNegative = this.minusSign.containsKey(ch);
            if (isExponentNegative || this.plusSign.containsKey(ch)) {
                ch = AbstractConfigurableFloatingPointBitsFromByteArrayAscii.charAt(str, ++index, endIndex);
                isExponentSigned = true;
            }
            illegal |= (digit = this.digitSet.toDigit(ch)) >= 10;
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + digit;
            } while ((digit = this.digitSet.toDigit(ch = AbstractConfigurableFloatingPointBitsFromByteArrayAscii.charAt(str, ++index, endIndex))) < 10);
            if (!isExponentSigned && ((isExponentNegative = this.minusSign.containsKey(ch)) || this.plusSign.containsKey(ch))) {
                ++index;
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
                ch = str[index];
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
            byte ch;
            if ((index += nanMatch) < endIndex && !isSignificandSigned && (this.minusSign.containsKey(ch = str[index]) || this.plusSign.containsKey(ch))) {
                ++index;
            }
            return index == endIndex ? this.nan() : 9221120237041090561L;
        }
        int infinityMatch = this.infinity.match(str, index, endIndex);
        if (infinityMatch > 0) {
            byte ch;
            if ((index += infinityMatch) < endIndex && !isSignificandSigned && ((isNegative = this.minusSign.containsKey(ch = str[index])) || this.plusSign.containsKey(ch))) {
                ++index;
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

