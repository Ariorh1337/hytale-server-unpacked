/*
 * Decompiled with CFR 0.152.
 */
package org.joml.sampling;

class Math
extends org.joml.Math {
    static final double PI = java.lang.Math.PI;
    static final double PI2 = java.lang.Math.PI * 2;
    static final double PIHalf = 1.5707963267948966;
    private static final double ONE_OVER_PI = 0.3183098861837907;
    private static final double s5 = Double.longBitsToDouble(4523227044276562163L);
    private static final double s4 = Double.longBitsToDouble(-4671934770969572232L);
    private static final double s3 = Double.longBitsToDouble(4575957211482072852L);
    private static final double s2 = Double.longBitsToDouble(-4628199223918090387L);
    private static final double s1 = Double.longBitsToDouble(4607182418589157889L);

    Math() {
    }

    static double sin_roquen_9(double v) {
        double i = java.lang.Math.rint(v * 0.3183098861837907);
        double x = v - i * java.lang.Math.PI;
        double qs = 1 - 2 * ((int)i & 1);
        double x2 = x * x;
        x = qs * x;
        double r = s5;
        r = r * x2 + s4;
        r = r * x2 + s3;
        r = r * x2 + s2;
        r = r * x2 + s1;
        return x * r;
    }
}

