/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractBigIntegerParser;
import ch.randelshofer.fastdoubleparser.AbstractNumberParser;
import ch.randelshofer.fastdoubleparser.FastDoubleSwar;
import ch.randelshofer.fastdoubleparser.FastIntegerMath;
import ch.randelshofer.fastdoubleparser.ParseDigitsTaskCharSequence;
import java.math.BigInteger;
import java.util.NavigableMap;

final class JavaBigIntegerFromCharSequence
extends AbstractBigIntegerParser {
    JavaBigIntegerFromCharSequence() {
    }

    public BigInteger parseBigIntegerString(CharSequence str, int offset, int length, int radix) throws NumberFormatException {
        try {
            boolean isNegative;
            int size = str.length();
            int endIndex = AbstractNumberParser.checkBounds(size, offset, length);
            int index = offset;
            char ch = str.charAt(index);
            boolean bl = isNegative = ch == '-';
            if ((isNegative || ch == '+') && (ch = JavaBigIntegerFromCharSequence.charAt(str, ++index, endIndex)) == '\u0000') {
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
            return new BigInteger(str.subSequence(offset, length).toString(), radix);
        }
        catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException("value exceeds limits");
            nfe.initCause(e);
            throw nfe;
        }
    }

    private BigInteger parseDecDigits(CharSequence str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (JavaBigIntegerFromCharSequence.hasManyDigits(numDigits)) {
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

    private BigInteger parseHexDigits(CharSequence str, int from, int to, boolean isNegative) {
        int numDigits = to - (from = this.skipZeroes(str, from, to));
        if (numDigits <= 0) {
            return BigInteger.ZERO;
        }
        JavaBigIntegerFromCharSequence.checkHexBigIntegerBounds(numDigits);
        byte[] bytes = new byte[(numDigits + 1 >> 1) + 1];
        int index = 1;
        boolean illegalDigits = false;
        if ((numDigits & 1) != 0) {
            char chLow = str.charAt(from++);
            int valueLow = JavaBigIntegerFromCharSequence.lookupHex(chLow);
            bytes[index++] = (byte)valueLow;
            illegalDigits = valueLow < 0;
        }
        int prerollLimit = from + (to - from & 7);
        while (from < prerollLimit) {
            char chHigh = str.charAt(from);
            char chLow = str.charAt(from + 1);
            int valueHigh = JavaBigIntegerFromCharSequence.lookupHex(chHigh);
            int valueLow = JavaBigIntegerFromCharSequence.lookupHex(chLow);
            bytes[index++] = (byte)(valueHigh << 4 | valueLow);
            illegalDigits |= valueLow < 0 || valueHigh < 0;
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

    private BigInteger parseManyDecDigits(CharSequence str, int from, int to, boolean isNegative) {
        from = this.skipZeroes(str, from, to);
        int numDigits = to - from;
        JavaBigIntegerFromCharSequence.checkDecBigIntegerBounds(numDigits);
        NavigableMap<Integer, BigInteger> powersOfTen = FastIntegerMath.fillPowersOf10Floor16(from, to);
        BigInteger result = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, from, to, powersOfTen, 400);
        return isNegative ? result.negate() : result;
    }

    private int skipZeroes(CharSequence str, int from, int to) {
        while (from < to && str.charAt(from) == '0') {
            ++from;
        }
        return from;
    }
}

