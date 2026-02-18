/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.text.NumberFormat;
import org.joml.Math;
import org.joml.Options;

public final class Runtime {
    public static final boolean HAS_floatToRawIntBits = Runtime.hasFloatToRawIntBits();
    public static final boolean HAS_doubleToRawLongBits = Runtime.hasDoubleToRawLongBits();
    public static final boolean HAS_Long_rotateLeft = Runtime.hasLongRotateLeft();
    public static final boolean HAS_Math_fma = Options.USE_MATH_FMA && Runtime.hasMathFma();

    private static boolean hasMathFma() {
        try {
            java.lang.Math.class.getDeclaredMethod("fma", Float.TYPE, Float.TYPE, Float.TYPE);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    private Runtime() {
    }

    private static boolean hasFloatToRawIntBits() {
        try {
            Float.class.getDeclaredMethod("floatToRawIntBits", Float.TYPE);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean hasDoubleToRawLongBits() {
        try {
            Double.class.getDeclaredMethod("doubleToRawLongBits", Double.TYPE);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static boolean hasLongRotateLeft() {
        try {
            Long.class.getDeclaredMethod("rotateLeft", Long.TYPE, Integer.TYPE);
            return true;
        }
        catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static int floatToIntBits(float flt) {
        if (HAS_floatToRawIntBits) {
            return Runtime.floatToIntBits1_3(flt);
        }
        return Runtime.floatToIntBits1_2(flt);
    }

    private static int floatToIntBits1_3(float flt) {
        return Float.floatToRawIntBits(flt);
    }

    private static int floatToIntBits1_2(float flt) {
        return Float.floatToIntBits(flt);
    }

    public static long doubleToLongBits(double dbl) {
        if (HAS_doubleToRawLongBits) {
            return Runtime.doubleToLongBits1_3(dbl);
        }
        return Runtime.doubleToLongBits1_2(dbl);
    }

    private static long doubleToLongBits1_3(double dbl) {
        return Double.doubleToRawLongBits(dbl);
    }

    private static long doubleToLongBits1_2(double dbl) {
        return Double.doubleToLongBits(dbl);
    }

    public static String formatNumbers(String str) {
        StringBuffer res = new StringBuffer();
        int eIndex = Integer.MIN_VALUE;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c == 'E') {
                eIndex = i;
            } else {
                if (c == ' ' && eIndex == i - 1) {
                    res.append('+');
                    continue;
                }
                if (Character.isDigit(c) && eIndex == i - 1) {
                    res.append('+');
                }
            }
            res.append(c);
        }
        return res.toString();
    }

    public static String format(double number, NumberFormat format) {
        if (Double.isNaN(number)) {
            return Runtime.padLeft(format, " NaN");
        }
        if (Double.isInfinite(number)) {
            return Runtime.padLeft(format, number > 0.0 ? " +Inf" : " -Inf");
        }
        return format.format(number);
    }

    private static String padLeft(NumberFormat format, String str) {
        int len = format.format(0.0).length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len - str.length() + 1; ++i) {
            sb.append(" ");
        }
        return sb.append(str).toString();
    }

    public static boolean equals(float a, float b, float delta) {
        return Float.floatToIntBits(a) == Float.floatToIntBits(b) || Math.abs(a - b) <= delta;
    }

    public static boolean equals(double a, double b, double delta) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b) || Math.abs(a - b) <= delta;
    }
}

