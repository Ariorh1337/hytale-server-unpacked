/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.BigSignificand;
import ch.randelshofer.fastdoubleparser.FastDoubleSwar;
import ch.randelshofer.fastdoubleparser.FastIntegerMath;
import ch.randelshofer.fastdoubleparser.FftMultiplier;
import java.math.BigInteger;
import java.util.Map;

final class ParseDigitsTaskCharSequence {
    private ParseDigitsTaskCharSequence() {
    }

    static BigInteger parseDigitsIterative(CharSequence str, int from, int to) {
        assert (str != null) : "str==null";
        int numDigits = to - from;
        BigSignificand bigSignificand = new BigSignificand(FastIntegerMath.estimateNumBits(numDigits));
        int preroll = from + (numDigits & 7);
        int value = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = value >= 0;
        bigSignificand.add(value);
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= addend >= 0;
            bigSignificand.fma(100000000, addend);
        }
        if (!success) {
            throw new NumberFormatException("illegal syntax");
        }
        return bigSignificand.toBigInteger();
    }

    static BigInteger parseDigitsRecursive(CharSequence str, int from, int to, Map<Integer, BigInteger> powersOfTen, int recursionThreshold) {
        assert (str != null) : "str==null";
        assert (powersOfTen != null) : "powersOfTen==null";
        int numDigits = to - from;
        if (numDigits <= recursionThreshold) {
            return ParseDigitsTaskCharSequence.parseDigitsIterative(str, from, to);
        }
        int mid = FastIntegerMath.splitFloor16(from, to);
        BigInteger high = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, from, mid, powersOfTen, recursionThreshold);
        BigInteger low = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, mid, to, powersOfTen, recursionThreshold);
        high = FftMultiplier.multiply(high, powersOfTen.get(to - mid));
        return low.add(high);
    }
}

