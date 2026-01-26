/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractBigIntegerParser;
import ch.randelshofer.fastdoubleparser.AbstractNumberParser;
import ch.randelshofer.fastdoubleparser.FastDoubleSwar;
import ch.randelshofer.fastdoubleparser.FastIntegerMath;
import ch.randelshofer.fastdoubleparser.ParseDigitsTaskCharArray;
import java.math.BigInteger;
import java.util.NavigableMap;

final class JavaBigIntegerFromCharArray
extends AbstractBigIntegerParser {
    JavaBigIntegerFromCharArray() {
    }

    public BigInteger parseBigIntegerString(char[] str, int offset, int length, int radix) throws NumberFormatException {
        try {
            boolean isNegative;
            int endIndex = AbstractNumberParser.checkBounds(str.length, offset, length);
            int index = offset;
            char ch = str[index];
            boolean bl = isNegative = ch == '-';
            if ((isNegative || ch == '+') && (ch = JavaBigIntegerFromCharArray.charAt(str, ++index, endIndex)) == '\u0000') {
                throw new NumberFormatException("illegal syntax");
            }
            switch (radix) {
                case 10: {
                    return this.parseDecDigits(str, index, endIndex, isNegative);
                }
                case 16: {
                    return this.parseHexDigits(str, index, endIndex, isNegative);
                }
            }
            return new BigInteger(new String(str, offset, length), radix);
        }
        catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException("value exceeds limits");
            nfe.initCause(e);
            throw nfe;
        }
    }

    private BigInteger parseDecDigits(char[] str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (JavaBigIntegerFromCharArray.hasManyDigits(numDigits)) {
            return this.parseManyDecDigits(str, from, to, isNegative);
        }
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0L;
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= addend >= 0;
            significand = significand * 100000000L + (long)addend;
        }
        if (!success) {
            throw new NumberFormatException("illegal syntax");
        }
        return BigInteger.valueOf(isNegative ? -significand : significand);
    }

    private BigInteger parseHexDigits(char[] str, int from, int to, boolean isNegative) {
        int numDigits = to - (from = this.skipZeroes(str, from, to));
        if (numDigits <= 0) {
            return BigInteger.ZERO;
        }
        JavaBigIntegerFromCharArray.checkHexBigIntegerBounds(numDigits);
        byte[] bytes = new byte[(numDigits + 1 >> 1) + 1];
        int index = 1;
        boolean illegalDigits = false;
        if ((numDigits & 1) != 0) {
            char chLow = str[from++];
            int valueLow = JavaBigIntegerFromCharArray.lookupHex(chLow);
            bytes[index++] = (byte)valueLow;
            illegalDigits = valueLow < 0;
        }
        int prerollLimit = from + (to - from & 7);
        while (from < prerollLimit) {
            char chHigh = str[from];
            char chLow = str[from + 1];
            int valueHigh = JavaBigIntegerFromCharArray.lookupHex(chHigh);
            int valueLow = JavaBigIntegerFromCharArray.lookupHex(chLow);
            bytes[index++] = (byte)(valueHigh << 4 | valueLow);
            illegalDigits |= valueHigh < 0 || valueLow < 0;
            from += 2;
        }
        while (from < to) {
            long value = FastDoubleSwar.tryToParseEightHexDigits(str, from);
            FastDoubleSwar.writeIntBE(bytes, index, (int)value);
            illegalDigits |= value < 0L;
            from += 8;
            index += 4;
        }
        if (illegalDigits) {
            throw new NumberFormatException("illegal syntax");
        }
        BigInteger result = new BigInteger(bytes);
        return isNegative ? result.negate() : result;
    }

    private BigInteger parseManyDecDigits(char[] str, int from, int to, boolean isNegative) {
        from = this.skipZeroes(str, from, to);
        int numDigits = to - from;
        JavaBigIntegerFromCharArray.checkDecBigIntegerBounds(numDigits);
        NavigableMap<Integer, BigInteger> powersOfTen = FastIntegerMath.fillPowersOf10Floor16(from, to);
        BigInteger result = ParseDigitsTaskCharArray.parseDigitsRecursive(str, from, to, powersOfTen, 400);
        return isNegative ? result.negate() : result;
    }

    private int skipZeroes(char[] str, int from, int to) {
        while (from < to - 8 && FastDoubleSwar.isEightZeroes(str, from)) {
            from += 8;
        }
        while (from < to && str[from] == '0') {
            ++from;
        }
        return from;
    }
}

