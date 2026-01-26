/*
 * Decompiled with CFR 0.152.
 */
package org.fusesource.jansi.io;

public class Colors {
    public static final int[] DEFAULT_COLORS_256 = new int[]{0, 0x800000, 32768, 0x808000, 128, 0x800080, 32896, 0xC0C0C0, 0x808080, 0xFF0000, 65280, 0xFFFF00, 255, 0xFF00FF, 65535, 0xFFFFFF, 0, 95, 135, 175, 215, 255, 24320, 24415, 24455, 24495, 24535, 24575, 34560, 34655, 34695, 34735, 34775, 34815, 44800, 44895, 44935, 44975, 45015, 45055, 55040, 55135, 55175, 55215, 55255, 55295, 65280, 65375, 65415, 65455, 65495, 65535, 0x5F0000, 0x5F005F, 6226055, 6226095, 6226135, 0x5F00FF, 0x5F5F00, 0x5F5F5F, 6250375, 0x5F5FAF, 6250455, 0x5F5FFF, 6260480, 6260575, 6260615, 6260655, 6260695, 6260735, 6270720, 0x5FAF5F, 6270855, 0x5FAFAF, 6270935, 0x5FAFFF, 6280960, 6281055, 6281095, 6281135, 6281175, 6281215, 0x5FFF00, 0x5FFF5F, 6291335, 0x5FFFAF, 6291415, 0x5FFFFF, 0x870000, 8847455, 0x870087, 8847535, 8847575, 8847615, 8871680, 8871775, 8871815, 8871855, 8871895, 8871935, 0x878700, 8882015, 0x878787, 8882095, 0x8787D7, 0x8787FF, 8892160, 8892255, 8892295, 8892335, 8892375, 8892415, 8902400, 8902495, 0x87D787, 8902575, 0x87D7D7, 8902655, 8912640, 8912735, 0x87FF87, 8912815, 8912855, 0x87FFFF, 0xAF0000, 11468895, 11468935, 0xAF00AF, 11469015, 0xAF00FF, 11493120, 0xAF5F5F, 11493255, 0xAF5FAF, 11493335, 0xAF5FFF, 11503360, 11503455, 11503495, 11503535, 11503575, 11503615, 0xAFAF00, 0xAFAF5F, 11513735, 0xAFAFAF, 11513815, 0xAFAFFF, 11523840, 11523935, 11523975, 11524015, 11524055, 11524095, 0xAFFF00, 0xAFFF5F, 11534215, 0xAFFFAF, 11534295, 0xAFFFFF, 0xD70000, 14090335, 14090375, 14090415, 0xD700D7, 14090495, 14114560, 14114655, 14114695, 14114735, 14114775, 14114815, 14124800, 14124895, 0xD78787, 14124975, 0xD787D7, 14125055, 14135040, 14135135, 14135175, 14135215, 14135255, 14135295, 0xD7D700, 14145375, 0xD7D787, 14145455, 0xD7D7D7, 0xD7D7FF, 14155520, 14155615, 14155655, 14155695, 0xD7FFD7, 0xD7FFFF, 0xFF0000, 0xFF005F, 16711815, 0xFF00AF, 16711895, 0xFF00FF, 0xFF5F00, 0xFF5F5F, 16736135, 0xFF5FAF, 16736215, 0xFF5FFF, 16746240, 16746335, 0xFF8787, 16746415, 16746455, 0xFF87FF, 0xFFAF00, 0xFFAF5F, 16756615, 0xFFAFAF, 16756695, 0xFFAFFF, 16766720, 16766815, 16766855, 16766895, 0xFFD7D7, 0xFFD7FF, 0xFFFF00, 0xFFFF5F, 0xFFFF87, 0xFFFFAF, 0xFFFFD7, 0xFFFFFF, 526344, 0x121212, 0x1C1C1C, 0x262626, 0x303030, 0x3A3A3A, 0x444444, 0x4E4E4E, 0x585858, 0x626262, 0x6C6C6C, 0x767676, 0x808080, 0x8A8A8A, 0x949494, 0x9E9E9E, 0xA8A8A8, 0xB2B2B2, 0xBCBCBC, 0xC6C6C6, 0xD0D0D0, 0xDADADA, 0xE4E4E4, 0xEEEEEE};
    private static final double epsilon = 0.008856451679035631;
    private static final double kappa = 903.2962962962963;

    public static int roundColor(int col, int max) {
        if (col >= max) {
            int c = DEFAULT_COLORS_256[col];
            col = Colors.roundColor(c, DEFAULT_COLORS_256, max);
        }
        return col;
    }

    public static int roundRgbColor(int r, int g, int b, int max) {
        return Colors.roundColor((r << 16) + (g << 8) + b, DEFAULT_COLORS_256, max);
    }

    private static int roundColor(int color, int[] colors, int max) {
        double best_distance = 2.147483647E9;
        int best_index = Integer.MAX_VALUE;
        for (int idx = 0; idx < max; ++idx) {
            double d = Colors.cie76(color, colors[idx]);
            if (!(d <= best_distance)) continue;
            best_index = idx;
            best_distance = d;
        }
        return best_index;
    }

    private static double cie76(int c1, int c2) {
        return Colors.scalar(Colors.rgb2cielab(c1), Colors.rgb2cielab(c2));
    }

    private static double scalar(double[] c1, double[] c2) {
        return Colors.sqr(c1[0] - c2[0]) + Colors.sqr(c1[1] - c2[1]) + Colors.sqr(c1[2] - c2[2]);
    }

    private static double[] rgb(int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color >> 0 & 0xFF;
        return new double[]{(double)r / 255.0, (double)g / 255.0, (double)b / 255.0};
    }

    private static double[] rgb2cielab(int color) {
        return Colors.rgb2cielab(Colors.rgb(color));
    }

    private static double[] rgb2cielab(double[] rgb) {
        return Colors.xyz2lab(Colors.rgb2xyz(rgb));
    }

    private static double[] rgb2xyz(double[] rgb) {
        double vr = Colors.pivotRgb(rgb[0]);
        double vg = Colors.pivotRgb(rgb[1]);
        double vb = Colors.pivotRgb(rgb[2]);
        double x = vr * 0.4124564 + vg * 0.3575761 + vb * 0.1804375;
        double y = vr * 0.2126729 + vg * 0.7151522 + vb * 0.072175;
        double z = vr * 0.0193339 + vg * 0.119192 + vb * 0.9503041;
        return new double[]{x, y, z};
    }

    private static double pivotRgb(double n) {
        return n > 0.04045 ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92;
    }

    private static double[] xyz2lab(double[] xyz) {
        double fx = Colors.pivotXyz(xyz[0]);
        double fy = Colors.pivotXyz(xyz[1]);
        double fz = Colors.pivotXyz(xyz[2]);
        double l = 116.0 * fy - 16.0;
        double a = 500.0 * (fx - fy);
        double b = 200.0 * (fy - fz);
        return new double[]{l, a, b};
    }

    private static double pivotXyz(double n) {
        return n > 0.008856451679035631 ? Math.cbrt(n) : (903.2962962962963 * n + 16.0) / 116.0;
    }

    private static double sqr(double n) {
        return n * n;
    }
}

