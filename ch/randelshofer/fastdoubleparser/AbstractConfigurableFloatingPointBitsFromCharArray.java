/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractFloatValueParser;
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols;
import ch.randelshofer.fastdoubleparser.NumberFormatSymbolsInfo;
import ch.randelshofer.fastdoubleparser.SlowDoubleConversionPath;
import ch.randelshofer.fastdoubleparser.chr.CharDigitSet;
import ch.randelshofer.fastdoubleparser.chr.CharSet;
import ch.randelshofer.fastdoubleparser.chr.CharSetOfNone;
import ch.randelshofer.fastdoubleparser.chr.CharTrie;
import ch.randelshofer.fastdoubleparser.chr.FormatCharSet;

abstract class AbstractConfigurableFloatingPointBitsFromCharArray
extends AbstractFloatValueParser {
    private final CharDigitSet digitSet;
    private final CharSet minusSign;
    private final CharSet plusSign;
    private final CharSet decimalSeparator;
    private final CharSet groupingSeparator;
    private final CharTrie nan;
    private final CharTrie infinity;
    private final CharTrie exponentSeparator;
    private final CharSet formatChar;

    public AbstractConfigurableFloatingPointBitsFromCharArray(NumberFormatSymbols symbols, boolean ignoreCase) {
        this.decimalSeparator = CharSet.copyOf(symbols.decimalSeparator(), ignoreCase);
        this.groupingSeparator = CharSet.copyOf(symbols.groupingSeparator(), ignoreCase);
        this.digitSet = CharDigitSet.copyOf(symbols.digits());
        this.minusSign = CharSet.copyOf(symbols.minusSign(), ignoreCase);
        this.exponentSeparator = CharTrie.copyOf(symbols.exponentSeparator(), ignoreCase);
        this.plusSign = CharSet.copyOf(symbols.plusSign(), ignoreCase);
        this.nan = CharTrie.copyOf(symbols.nan(), ignoreCase);
        this.infinity = CharTrie.copyOf(symbols.infinity(), ignoreCase);
        this.formatChar = NumberFormatSymbolsInfo.containsFormatChars(symbols) ? new CharSetOfNone() : new FormatCharSet();
    }

    abstract long nan();

    abstract long negativeInfinity();

    public final long parseFloatingPointLiteral(char[] str, int offset, int length) {
        int exponentOfTruncatedSignificand;
        boolean isSignificandTruncated;
        int count;
        int exponent;
        int digitCount;
        int endIndex = AbstractConfigurableFloatingPointBitsFromCharArray.checkBounds(str.length, offset, length);
        int index = this.skipFormatCharacters(str, offset, endIndex);
        if (index == endIndex) {
            return 9221120237041090561L;
        }
        char ch = str[index];
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
        if (!(illegal |= digitCount == 0 && significandEndIndex > significandStartIndex) && digitCount == 0) {
            return this.parseNaNOrInfinity(str, index, endIndex, isNegative, isSignificandSigned);
        }
        if (index < endIndex && !isSignificandSigned && ((isNegative = this.minusSign.containsKey(ch)) || this.plusSign.containsKey(ch))) {
            ++index;
        }
        int expNumber = 0;
        boolean isExponentSigned = false;
        if (digitCount > 0 && (count = this.exponentSeparator.match(str, index, endIndex)) > 0) {
            int digit;
            index += count;
            ch = AbstractConfigurableFloatingPointBitsFromCharArray.charAt(str, index = this.skipFormatCharacters(str, index, endIndex), endIndex);
            boolean isExponentNegative = this.minusSign.containsKey(ch);
            if (isExponentNegative || this.plusSign.containsKey(ch)) {
                ch = AbstractConfigurableFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex);
                isExponentSigned = true;
            }
            illegal |= (digit = this.digitSet.toDigit(ch)) >= 10;
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + digit;
            } while ((digit = this.digitSet.toDigit(ch = AbstractConfigurableFloatingPointBitsFromCharArray.charAt(str, ++index, endIndex))) < 10);
            if (!isExponentSigned && ((isExponentNegative = this.minusSign.containsKey(ch)) || this.plusSign.containsKey(ch))) {
                ++index;
            }
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
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

    private int skipFormatCharacters(char[] str, int index, int endIndex) {
        while (index < endIndex && this.formatChar.containsKey(str[index])) {
            ++index;
        }
        return index;
    }

    private long parseNaNOrInfinity(char[] str, int index, int endIndex, boolean isNegative, boolean isSignificandSigned) {
        int nanMatch = this.nan.match(str, index, endIndex);
        if (nanMatch > 0) {
            char ch;
            if ((index += nanMatch) < endIndex && !isSignificandSigned && (this.minusSign.containsKey(ch = str[index]) || this.plusSign.containsKey(ch))) {
                ++index;
            }
            return index == endIndex ? this.nan() : 9221120237041090561L;
        }
        int infinityMatch = this.infinity.match(str, index, endIndex);
        if (infinityMatch > 0) {
            char ch;
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

    abstract long valueOfFloatLiteral(char[] var1, int var2, int var3, int var4, int var5, boolean var6, long var7, int var9, boolean var10, int var11, int var12, int var13, int var14);

    protected double slowPathToDouble(char[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, int exponentValue) {
        return SlowDoubleConversionPath.toDouble(str, this.digitSet, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, (long)exponentValue);
    }
}

