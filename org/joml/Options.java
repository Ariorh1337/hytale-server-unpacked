/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public final class Options {
    public static final boolean DEBUG = Options.hasOption(System.getProperty("joml.debug", "false"));
    public static final boolean NO_UNSAFE = Options.hasOption(System.getProperty("joml.nounsafe", "false"));
    public static final boolean FORCE_UNSAFE = Options.hasOption(System.getProperty("joml.forceUnsafe", "false"));
    public static final boolean FASTMATH = Options.hasOption(System.getProperty("joml.fastmath", "false"));
    public static final boolean SIN_LOOKUP = Options.hasOption(System.getProperty("joml.sinLookup", "false"));
    public static final int SIN_LOOKUP_BITS = Integer.parseInt(System.getProperty("joml.sinLookup.bits", "14"));
    public static final boolean useNumberFormat = Options.hasOption(System.getProperty("joml.format", "true"));
    public static final boolean USE_MATH_FMA = Options.hasOption(System.getProperty("joml.useMathFma", "false"));
    public static final int numberFormatDecimals = Integer.parseInt(System.getProperty("joml.format.decimals", "3"));
    public static final NumberFormat NUMBER_FORMAT = Options.decimalFormat();

    private Options() {
    }

    private static NumberFormat decimalFormat() {
        NumberFormat df;
        if (useNumberFormat) {
            char[] prec = new char[numberFormatDecimals];
            Arrays.fill(prec, '0');
            df = new DecimalFormat(" 0." + new String(prec) + "E0;-");
        } else {
            df = NumberFormat.getNumberInstance(Locale.ENGLISH);
            df.setGroupingUsed(false);
        }
        return df;
    }

    private static boolean hasOption(String v) {
        if (v == null) {
            return false;
        }
        if (v.trim().length() == 0) {
            return true;
        }
        return Boolean.valueOf(v);
    }
}

