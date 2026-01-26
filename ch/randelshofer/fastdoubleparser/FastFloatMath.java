/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.FastDoubleMath;
import ch.randelshofer.fastdoubleparser.FastIntegerMath;

final class FastFloatMath {
    private static final int FLOAT_EXPONENT_BIAS = 127;
    private static final int FLOAT_SIGNIFICAND_WIDTH = 24;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TEN = -45;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TEN = 38;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TWO = -126;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TWO = 127;
    private static final float[] FLOAT_POWER_OF_TEN = new float[]{1.0f, 10.0f, 100.0f, 1000.0f, 10000.0f, 100000.0f, 1000000.0f, 1.0E7f, 1.0E8f, 1.0E9f, 1.0E10f};

    private FastFloatMath() {
    }

    static float tryDecFloatToFloatTruncated(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float result;
        if (significand == 0L) {
            return isNegative ? -0.0f : 0.0f;
        }
        if (isSignificandTruncated) {
            if (-45 <= exponentOfTruncatedSignificand && exponentOfTruncatedSignificand <= 38) {
                float withoutRounding = FastFloatMath.tryDecToFloatWithFastAlgorithm(isNegative, significand, exponentOfTruncatedSignificand);
                float roundedUp = FastFloatMath.tryDecToFloatWithFastAlgorithm(isNegative, significand + 1L, exponentOfTruncatedSignificand);
                if (roundedUp == withoutRounding) {
                    return withoutRounding;
                }
            }
            result = Float.NaN;
        } else {
            result = -45 <= exponent && exponent <= 38 ? FastFloatMath.tryDecToFloatWithFastAlgorithm(isNegative, significand, exponent) : Float.NaN;
        }
        return result;
    }

    static float tryHexFloatToFloatTruncated(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        int power;
        int n = power = isSignificandTruncated ? exponentOfTruncatedSignificand : exponent;
        if (-126 <= power && power <= 127) {
            float d = (float)significand + (significand < 0L ? 1.8446744E19f : 0.0f);
            d = FastFloatMath.fastScalb(d, power);
            return isNegative ? -d : d;
        }
        return Float.NaN;
    }

    static float tryDecToFloatWithFastAlgorithm(boolean isNegative, long significand, int power) {
        if (-10 <= power && power <= 10 && Long.compareUnsigned(significand, 0xFFFFFFL) <= 0) {
            float d = significand;
            d = power < 0 ? (d /= FLOAT_POWER_OF_TEN[-power]) : (d *= FLOAT_POWER_OF_TEN[power]);
            return isNegative ? -d : d;
        }
        long factorMantissa = FastDoubleMath.MANTISSA_64[power - -325];
        long exponent = (217706L * (long)power >> 16) + 127L + 64L;
        int lz = Long.numberOfLeadingZeros(significand);
        long shiftedSignificand = significand << lz;
        long upper = FastIntegerMath.unsignedMultiplyHigh(shiftedSignificand, factorMantissa);
        long upperbit = upper >>> 63;
        long mantissa = upper >>> (int)(upperbit + 38L);
        lz += (int)(1L ^ upperbit);
        if ((upper & 0x3FFFFFFFFFL) == 0x3FFFFFFFFFL || (upper & 0x3FFFFFFFFFL) == 0L && (mantissa & 3L) == 1L) {
            return Float.NaN;
        }
        ++mantissa;
        if ((mantissa >>>= 1) >= 0x1000000L) {
            mantissa = 0x800000L;
            --lz;
        }
        mantissa &= 0xFFFFFFFFFF7FFFFFL;
        long real_exponent = exponent - (long)lz;
        if (real_exponent < 1L || real_exponent > 254L) {
            return Float.NaN;
        }
        int bits = (int)(mantissa | real_exponent << 23 | (isNegative ? 0x80000000L : 0L));
        return Float.intBitsToFloat(bits);
    }

    static float fastScalb(float number, int scaleFactor) {
        return number * Float.intBitsToFloat(scaleFactor + 127 << 23);
    }
}

