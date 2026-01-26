/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.AbstractConfigurableFloatingPointBitsFromCharArray;
import ch.randelshofer.fastdoubleparser.FastDoubleMath;
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols;

final class ConfigurableDoubleBitsFromCharArray
extends AbstractConfigurableFloatingPointBitsFromCharArray {
    public ConfigurableDoubleBitsFromCharArray(NumberFormatSymbols symbols, boolean ignoreCase) {
        super(symbols, ignoreCase);
    }

    @Override
    long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    @Override
    long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    @Override
    long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    @Override
    long valueOfFloatLiteral(char[] str, int integerStartIndex, int integerEndIndex, int fractionStartIndex, int fractionEndIndex, boolean isSignificandNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand, int exponentValue, int startIndex, int endIndex) {
        double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isSignificandNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Double.doubleToRawLongBits(Double.isNaN(d) ? this.slowPathToDouble(str, integerStartIndex, integerEndIndex, fractionStartIndex, fractionEndIndex, isSignificandNegative, exponentValue) : d);
    }
}

