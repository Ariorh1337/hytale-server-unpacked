/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.jline.utils.InfoCmp;
import org.jline.utils.InputStreamReader;

public class Colors {
    public static final int[] DEFAULT_COLORS_256 = new int[]{0, 0x800000, 32768, 0x808000, 128, 0x800080, 32896, 0xC0C0C0, 0x808080, 0xFF0000, 65280, 0xFFFF00, 255, 0xFF00FF, 65535, 0xFFFFFF, 0, 95, 135, 175, 215, 255, 24320, 24415, 24455, 24495, 24535, 24575, 34560, 34655, 34695, 34735, 34775, 34815, 44800, 44895, 44935, 44975, 45015, 45055, 55040, 55135, 55175, 55215, 55255, 55295, 65280, 65375, 65415, 65455, 65495, 65535, 0x5F0000, 0x5F005F, 6226055, 6226095, 6226135, 0x5F00FF, 0x5F5F00, 0x5F5F5F, 6250375, 0x5F5FAF, 6250455, 0x5F5FFF, 6260480, 6260575, 6260615, 6260655, 6260695, 6260735, 6270720, 0x5FAF5F, 6270855, 0x5FAFAF, 6270935, 0x5FAFFF, 6280960, 6281055, 6281095, 6281135, 6281175, 6281215, 0x5FFF00, 0x5FFF5F, 6291335, 0x5FFFAF, 6291415, 0x5FFFFF, 0x870000, 8847455, 0x870087, 8847535, 8847575, 8847615, 8871680, 8871775, 8871815, 8871855, 8871895, 8871935, 0x878700, 8882015, 0x878787, 8882095, 0x8787D7, 0x8787FF, 8892160, 8892255, 8892295, 8892335, 8892375, 8892415, 8902400, 8902495, 0x87D787, 8902575, 0x87D7D7, 8902655, 8912640, 8912735, 0x87FF87, 8912815, 8912855, 0x87FFFF, 0xAF0000, 11468895, 11468935, 0xAF00AF, 11469015, 0xAF00FF, 11493120, 0xAF5F5F, 11493255, 0xAF5FAF, 11493335, 0xAF5FFF, 11503360, 11503455, 11503495, 11503535, 11503575, 11503615, 0xAFAF00, 0xAFAF5F, 11513735, 0xAFAFAF, 11513815, 0xAFAFFF, 11523840, 11523935, 11523975, 11524015, 11524055, 11524095, 0xAFFF00, 0xAFFF5F, 11534215, 0xAFFFAF, 11534295, 0xAFFFFF, 0xD70000, 14090335, 14090375, 14090415, 0xD700D7, 14090495, 14114560, 14114655, 14114695, 14114735, 14114775, 14114815, 14124800, 14124895, 0xD78787, 14124975, 0xD787D7, 14125055, 14135040, 14135135, 14135175, 14135215, 14135255, 14135295, 0xD7D700, 14145375, 0xD7D787, 14145455, 0xD7D7D7, 0xD7D7FF, 14155520, 14155615, 14155655, 14155695, 0xD7FFD7, 0xD7FFFF, 0xFF0000, 0xFF005F, 16711815, 0xFF00AF, 16711895, 0xFF00FF, 0xFF5F00, 0xFF5F5F, 16736135, 0xFF5FAF, 16736215, 0xFF5FFF, 16746240, 16746335, 0xFF8787, 16746415, 16746455, 0xFF87FF, 0xFFAF00, 0xFFAF5F, 16756615, 0xFFAFAF, 16756695, 0xFFAFFF, 16766720, 16766815, 16766855, 16766895, 0xFFD7D7, 0xFFD7FF, 0xFFFF00, 0xFFFF5F, 0xFFFF87, 0xFFFFAF, 0xFFFFD7, 0xFFFFFF, 526344, 0x121212, 0x1C1C1C, 0x262626, 0x303030, 0x3A3A3A, 0x444444, 0x4E4E4E, 0x585858, 0x626262, 0x6C6C6C, 0x767676, 0x808080, 0x8A8A8A, 0x949494, 0x9E9E9E, 0xA8A8A8, 0xB2B2B2, 0xBCBCBC, 0xC6C6C6, 0xD0D0D0, 0xDADADA, 0xE4E4E4, 0xEEEEEE};
    public static final int[] DEFAULT_COLORS_88 = new int[]{0, 0x800000, 32768, 0x808000, 128, 0x800080, 32896, 0xC0C0C0, 0x808080, 0xFF0000, 65280, 0xFFFF00, 255, 0xFF00FF, 65535, 0xFFFFFF, 0, 139, 205, 255, 35584, 35723, 35789, 35839, 52480, 52619, 52685, 52735, 65280, 65419, 65485, 65535, 0x8B0000, 0x8B008B, 9109709, 9109759, 0x8B8B00, 0x8B8B8B, 9145293, 0x8B8BFF, 9161984, 9162123, 9162189, 9162239, 9174784, 0x8BFF8B, 9174989, 0x8BFFFF, 0xCD0000, 13435019, 0xCD00CD, 13435135, 13470464, 13470603, 13470669, 13470719, 0xCDCD00, 13487499, 0xCDCDCD, 0xCDCDFF, 13500160, 13500299, 0xCDFFCD, 0xCDFFFF, 0xFF0000, 16711819, 16711885, 0xFF00FF, 16747264, 0xFF8B8B, 16747469, 0xFF8BFF, 16764160, 16764299, 0xFFCDCD, 0xFFCDFF, 0xFFFF00, 0xFFFF8B, 0xFFFFCD, 0xFFFFFF, 0x2E2E2E, 0x5C5C5C, 0x737373, 0x8B8B8B, 0xA2A2A2, 0xB9B9B9, 0xD0D0D0, 0xE7E7E7};
    public static final double[] D50 = new double[]{96.422f, 100.0, 82.521f};
    public static final double[] D65 = new double[]{95.047, 100.0, 108.883};
    public static final double[] averageSurrounding = new double[]{1.0, 0.69, 1.0};
    public static final double[] dimSurrounding = new double[]{0.9, 0.59, 0.9};
    public static final double[] darkSurrounding = new double[]{0.8, 0.525, 0.8};
    public static final double[] sRGB_encoding_environment = Colors.vc(D50, 64.0, 12.8, dimSurrounding);
    public static final double[] sRGB_typical_environment = Colors.vc(D50, 200.0, 40.0, averageSurrounding);
    public static final double[] AdobeRGB_environment = Colors.vc(D65, 160.0, 32.0, averageSurrounding);
    private static int[] COLORS_256 = DEFAULT_COLORS_256;
    private static Map<String, Integer> COLOR_NAMES;
    private static final int L = 0;
    private static final int A = 1;
    private static final int B = 2;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    private static final double kl = 2.0;
    private static final double kc = 1.0;
    private static final double kh = 1.0;
    private static final double k1 = 0.045;
    private static final double k2 = 0.015;
    public static final int J = 0;
    public static final int Q = 1;
    public static final int C = 2;
    public static final int M = 3;
    public static final int s = 4;
    public static final int H = 5;
    public static final int h = 6;
    static final int SUR_F = 0;
    static final int SUR_C = 1;
    static final int SUR_N_C = 2;
    static final int VC_X_W = 0;
    static final int VC_Y_W = 1;
    static final int VC_Z_W = 2;
    static final int VC_L_A = 3;
    static final int VC_Y_B = 4;
    static final int VC_F = 5;
    static final int VC_C = 6;
    static final int VC_N_C = 7;
    static final int VC_Z = 8;
    static final int VC_N = 9;
    static final int VC_N_BB = 10;
    static final int VC_N_CB = 11;
    static final int VC_A_W = 12;
    static final int VC_F_L = 13;
    static final int VC_D_RGB_R = 14;
    static final int VC_D_RGB_G = 15;
    static final int VC_D_RGB_B = 16;
    private static final double epsilon = 0.008856451679035631;
    private static final double kappa = 903.2962962962963;

    public static void setRgbColors(int[] colors) {
        if (colors == null || colors.length != 256) {
            throw new IllegalArgumentException();
        }
        COLORS_256 = colors;
    }

    public static int rgbColor(int col) {
        return COLORS_256[col];
    }

    public static Integer rgbColor(String name) {
        if (COLOR_NAMES == null) {
            LinkedHashMap<String, Integer> colors = new LinkedHashMap<String, Integer>();
            try (InputStream is = InfoCmp.class.getResourceAsStream("colors.txt");
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));){
                br.lines().map(String::trim).filter(s -> !s.startsWith("#")).filter(s -> !s.isEmpty()).forEachOrdered(s -> colors.put((String)s, colors.size()));
                COLOR_NAMES = colors;
            }
            catch (IOException e) {
                throw new IOError(e);
            }
        }
        return COLOR_NAMES.get(name);
    }

    public static int roundColor(int col, int max) {
        return Colors.roundColor(col, max, null);
    }

    public static int roundColor(int col, int max, String dist) {
        if (col >= max) {
            int c = COLORS_256[col];
            col = Colors.roundColor(c, COLORS_256, max, dist);
        }
        return col;
    }

    public static int roundRgbColor(int r, int g, int b, int max) {
        return Colors.roundColor((r << 16) + (g << 8) + b, COLORS_256, max, (String)null);
    }

    static int roundColor(int color, int[] colors, int max, String dist) {
        return Colors.roundColor(color, colors, max, Colors.getDistance(dist));
    }

    static int roundColor(int color, int[] colors, int max, Distance distance) {
        double best_distance = 2.147483647E9;
        int best_index = Integer.MAX_VALUE;
        for (int idx = 0; idx < max; ++idx) {
            double d = distance.compute(color, colors[idx]);
            if (!(d <= best_distance)) continue;
            best_index = idx;
            best_distance = d;
        }
        return best_index;
    }

    static Distance getDistance(String dist) {
        if (dist == null) {
            dist = System.getProperty("org.jline.utils.colorDistance", "cie76");
        }
        return new NamedDistance(dist, Colors.doGetDistance(dist));
    }

    private static Distance doGetDistance(String dist) {
        if (dist.equals("rgb")) {
            return (p1, p2) -> {
                double[] c1 = Colors.rgb(p1);
                double[] c2 = Colors.rgb(p2);
                double rmean = (c1[0] + c2[0]) / 2.0;
                double[] w = new double[]{2.0 + rmean, 4.0, 3.0 - rmean};
                return Colors.scalar(c1, c2, w);
            };
        }
        if (dist.matches("rgb\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
            return (p1, p2) -> Colors.scalar(Colors.rgb(p1), Colors.rgb(p2), Colors.getWeights(dist));
        }
        if (dist.equals("lab") || dist.equals("cie76")) {
            return (p1, p2) -> Colors.scalar(Colors.rgb2cielab(p1), Colors.rgb2cielab(p2));
        }
        if (dist.matches("lab\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
            double[] w = Colors.getWeights(dist);
            return (p1, p2) -> Colors.scalar(Colors.rgb2cielab(p1), Colors.rgb2cielab(p2), new double[]{w[0], w[1], w[1]});
        }
        if (dist.equals("cie94")) {
            return (p1, p2) -> Colors.cie94(Colors.rgb2cielab(p1), Colors.rgb2cielab(p2));
        }
        if (dist.equals("cie00") || dist.equals("cie2000")) {
            return (p1, p2) -> Colors.cie00(Colors.rgb2cielab(p1), Colors.rgb2cielab(p2));
        }
        if (dist.equals("cam02")) {
            return (p1, p2) -> Colors.cam02(p1, p2, sRGB_typical_environment);
        }
        if (dist.equals("camlab")) {
            return (p1, p2) -> {
                double[] c1 = Colors.camlab(p1, sRGB_typical_environment);
                double[] c2 = Colors.camlab(p2, sRGB_typical_environment);
                return Colors.scalar(c1, c2);
            };
        }
        if (dist.matches("camlab\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
            return (p1, p2) -> {
                double[] c1 = Colors.camlab(p1, sRGB_typical_environment);
                double[] c2 = Colors.camlab(p2, sRGB_typical_environment);
                double[] w = Colors.getWeights(dist);
                return Colors.scalar(c1, c2, new double[]{w[0], w[1], w[1]});
            };
        }
        if (dist.matches("camlch")) {
            return (p1, p2) -> {
                double[] c1 = Colors.camlch(p1, sRGB_typical_environment);
                double[] c2 = Colors.camlch(p2, sRGB_typical_environment);
                return Colors.camlch(c1, c2);
            };
        }
        if (dist.matches("camlch\\(([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?),([0-9]+(\\.[0-9]+)?)\\)")) {
            return (p1, p2) -> {
                double[] c1 = Colors.camlch(p1, sRGB_typical_environment);
                double[] c2 = Colors.camlch(p2, sRGB_typical_environment);
                double[] w = Colors.getWeights(dist);
                return Colors.camlch(c1, c2, w);
            };
        }
        throw new IllegalArgumentException("Unsupported distance function: " + dist);
    }

    private static double[] getWeights(String dist) {
        String[] weights = dist.substring(dist.indexOf(40) + 1, dist.length() - 1).split(",");
        return Stream.of(weights).mapToDouble(Double::parseDouble).toArray();
    }

    private static double scalar(double[] c1, double[] c2, double[] w) {
        return Colors.sqr((c1[0] - c2[0]) * w[0]) + Colors.sqr((c1[1] - c2[1]) * w[1]) + Colors.sqr((c1[2] - c2[2]) * w[2]);
    }

    private static double scalar(double[] c1, double[] c2) {
        return Colors.sqr(c1[0] - c2[0]) + Colors.sqr(c1[1] - c2[1]) + Colors.sqr(c1[2] - c2[2]);
    }

    private static double cie94(double[] lab1, double[] lab2) {
        double c2;
        double dl = lab1[0] - lab2[0];
        double da = lab1[1] - lab2[1];
        double db = lab1[2] - lab2[2];
        double c1 = Math.sqrt(lab1[1] * lab1[1] + lab1[2] * lab1[2]);
        double dc = c1 - (c2 = Math.sqrt(lab2[1] * lab2[1] + lab2[2] * lab2[2]));
        double dh = da * da + db * db - dc * dc;
        dh = dh < 0.0 ? 0.0 : Math.sqrt(dh);
        double sl = 1.0;
        double sc = 1.0 + 0.045 * c1;
        double sh = 1.0 + 0.015 * c1;
        double dLKlsl = dl / (2.0 * sl);
        double dCkcsc = dc / (1.0 * sc);
        double dHkhsh = dh / (1.0 * sh);
        return dLKlsl * dLKlsl + dCkcsc * dCkcsc + dHkhsh * dHkhsh;
    }

    private static double cie00(double[] lab1, double[] lab2) {
        double c_star_1_ab = Math.sqrt(lab1[1] * lab1[1] + lab1[2] * lab1[2]);
        double c_star_2_ab = Math.sqrt(lab2[1] * lab2[1] + lab2[2] * lab2[2]);
        double c_star_average_ab = (c_star_1_ab + c_star_2_ab) / 2.0;
        double c_star_average_ab_pot_3 = c_star_average_ab * c_star_average_ab * c_star_average_ab;
        double c_star_average_ab_pot_7 = c_star_average_ab_pot_3 * c_star_average_ab_pot_3 * c_star_average_ab;
        double G = 0.5 * (1.0 - Math.sqrt(c_star_average_ab_pot_7 / (c_star_average_ab_pot_7 + 6.103515625E9)));
        double a1_prime = (1.0 + G) * lab1[1];
        double a2_prime = (1.0 + G) * lab2[1];
        double C_prime_1 = Math.sqrt(a1_prime * a1_prime + lab1[2] * lab1[2]);
        double C_prime_2 = Math.sqrt(a2_prime * a2_prime + lab2[2] * lab2[2]);
        double h_prime_1 = (Math.toDegrees(Math.atan2(lab1[2], a1_prime)) + 360.0) % 360.0;
        double h_prime_2 = (Math.toDegrees(Math.atan2(lab2[2], a2_prime)) + 360.0) % 360.0;
        double delta_L_prime = lab2[0] - lab1[0];
        double delta_C_prime = C_prime_2 - C_prime_1;
        double h_bar = Math.abs(h_prime_1 - h_prime_2);
        double delta_h_prime = C_prime_1 * C_prime_2 == 0.0 ? 0.0 : (h_bar <= 180.0 ? h_prime_2 - h_prime_1 : (h_prime_2 <= h_prime_1 ? h_prime_2 - h_prime_1 + 360.0 : h_prime_2 - h_prime_1 - 360.0));
        double delta_H_prime = 2.0 * Math.sqrt(C_prime_1 * C_prime_2) * Math.sin(Math.toRadians(delta_h_prime / 2.0));
        double L_prime_average = (lab1[0] + lab2[0]) / 2.0;
        double C_prime_average = (C_prime_1 + C_prime_2) / 2.0;
        double h_prime_average = C_prime_1 * C_prime_2 == 0.0 ? 0.0 : (h_bar <= 180.0 ? (h_prime_1 + h_prime_2) / 2.0 : (h_prime_1 + h_prime_2 < 360.0 ? (h_prime_1 + h_prime_2 + 360.0) / 2.0 : (h_prime_1 + h_prime_2 - 360.0) / 2.0));
        double L_prime_average_minus_50 = L_prime_average - 50.0;
        double L_prime_average_minus_50_square = L_prime_average_minus_50 * L_prime_average_minus_50;
        double T = 1.0 - 0.17 * Math.cos(Math.toRadians(h_prime_average - 30.0)) + 0.24 * Math.cos(Math.toRadians(h_prime_average * 2.0)) + 0.32 * Math.cos(Math.toRadians(h_prime_average * 3.0 + 6.0)) - 0.2 * Math.cos(Math.toRadians(h_prime_average * 4.0 - 63.0));
        double S_L = 1.0 + 0.015 * L_prime_average_minus_50_square / Math.sqrt(20.0 + L_prime_average_minus_50_square);
        double S_C = 1.0 + 0.045 * C_prime_average;
        double S_H = 1.0 + 0.015 * T * C_prime_average;
        double h_prime_average_minus_275_div_25 = (h_prime_average - 275.0) / 25.0;
        double h_prime_average_minus_275_div_25_square = h_prime_average_minus_275_div_25 * h_prime_average_minus_275_div_25;
        double delta_theta = 30.0 * Math.exp(-h_prime_average_minus_275_div_25_square);
        double C_prime_average_pot_3 = C_prime_average * C_prime_average * C_prime_average;
        double C_prime_average_pot_7 = C_prime_average_pot_3 * C_prime_average_pot_3 * C_prime_average;
        double R_C = 2.0 * Math.sqrt(C_prime_average_pot_7 / (C_prime_average_pot_7 + 6.103515625E9));
        double R_T = -Math.sin(Math.toRadians(2.0 * delta_theta)) * R_C;
        double dLKlsl = delta_L_prime / (2.0 * S_L);
        double dCkcsc = delta_C_prime / (1.0 * S_C);
        double dHkhsh = delta_H_prime / (1.0 * S_H);
        return dLKlsl * dLKlsl + dCkcsc * dCkcsc + dHkhsh * dHkhsh + R_T * dCkcsc * dHkhsh;
    }

    private static double cam02(int p1, int p2, double[] vc) {
        double[] c1 = Colors.jmh2ucs(Colors.camlch(p1, vc));
        double[] c2 = Colors.jmh2ucs(Colors.camlch(p2, vc));
        return Colors.scalar(c1, c2);
    }

    private static double[] jmh2ucs(double[] lch) {
        double sJ = 1.7000000000000002 * lch[0] / (1.0 + 0.007 * lch[0]);
        double sM = 43.859649122807014 * Math.log(1.0 + 0.0228 * lch[1]);
        double a = sM * Math.cos(Math.toRadians(lch[2]));
        double b = sM * Math.sin(Math.toRadians(lch[2]));
        return new double[]{sJ, a, b};
    }

    static double camlch(double[] c1, double[] c2) {
        return Colors.camlch(c1, c2, new double[]{1.0, 1.0, 1.0});
    }

    static double camlch(double[] c1, double[] c2, double[] w) {
        double lightnessWeight = w[0] / 100.0;
        double colorfulnessWeight = w[1] / 120.0;
        double hueWeight = w[2] / 360.0;
        double dl = (c1[0] - c2[0]) * lightnessWeight;
        double dc = (c1[1] - c2[1]) * colorfulnessWeight;
        double dh = Colors.hueDifference(c1[2], c2[2], 360.0) * hueWeight;
        return dl * dl + dc * dc + dh * dh;
    }

    private static double hueDifference(double hue1, double hue2, double c) {
        double difference = (hue2 - hue1) % c;
        double ch = c / 2.0;
        if (difference > ch) {
            difference -= c;
        }
        if (difference < -ch) {
            difference += c;
        }
        return difference;
    }

    private static double[] rgb(int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color >> 0 & 0xFF;
        return new double[]{(double)r / 255.0, (double)g / 255.0, (double)b / 255.0};
    }

    static double[] rgb2xyz(int color) {
        return Colors.rgb2xyz(Colors.rgb(color));
    }

    static double[] rgb2cielab(int color) {
        return Colors.rgb2cielab(Colors.rgb(color));
    }

    static double[] camlch(int color) {
        return Colors.camlch(color, sRGB_typical_environment);
    }

    static double[] camlch(int color, double[] vc) {
        return Colors.xyz2camlch(Colors.rgb2xyz(color), vc);
    }

    static double[] camlab(int color) {
        return Colors.camlab(color, sRGB_typical_environment);
    }

    static double[] camlab(int color, double[] vc) {
        return Colors.lch2lab(Colors.camlch(color, vc));
    }

    static double[] lch2lab(double[] lch) {
        double toRad = Math.PI / 180;
        return new double[]{lch[0], lch[1] * Math.cos(lch[2] * toRad), lch[1] * Math.sin(lch[2] * toRad)};
    }

    private static double[] xyz2camlch(double[] xyz, double[] vc) {
        double[] XYZ2 = new double[]{xyz[0] * 100.0, xyz[1] * 100.0, xyz[2] * 100.0};
        double[] cam = Colors.forwardTransform(XYZ2, vc);
        return new double[]{cam[0], cam[3], cam[6]};
    }

    private static double[] forwardTransform(double[] XYZ2, double[] vc) {
        double[] RGB = Colors.forwardPreAdaptationConeResponse(XYZ2);
        double[] RGB_c = Colors.forwardPostAdaptationConeResponse(RGB, vc);
        double[] RGBPrime = Colors.CAT02toHPE(RGB_c);
        double[] RGBPrime_a = Colors.forwardResponseCompression(RGBPrime, vc);
        double A = (2.0 * RGBPrime_a[0] + RGBPrime_a[1] + RGBPrime_a[2] / 20.0 - 0.305) * vc[10];
        double J = 100.0 * Math.pow(A / vc[12], vc[8] * vc[6]);
        double a = RGBPrime_a[0] + (-12.0 * RGBPrime_a[1] + RGBPrime_a[2]) / 11.0;
        double b = (RGBPrime_a[0] + RGBPrime_a[1] - 2.0 * RGBPrime_a[2]) / 9.0;
        double h = (Math.toDegrees(Math.atan2(b, a)) + 360.0) % 360.0;
        double e = 961.5384615384615 * vc[7] * vc[11] * (Math.cos(Math.toRadians(h) + 2.0) + 3.8);
        double t = e * Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0)) / (RGBPrime_a[0] + RGBPrime_a[1] + 1.05 * RGBPrime_a[2]);
        double Q = 4.0 / vc[6] * Math.sqrt(J / 100.0) * (vc[12] + 4.0) * Math.pow(vc[13], 0.25);
        double C = Math.signum(t) * Math.pow(Math.abs(t), 0.9) * Math.sqrt(J / 100.0) * Math.pow(1.64 - Math.pow(0.29, vc[9]), 0.73);
        double M = C * Math.pow(vc[13], 0.25);
        double s = 100.0 * Math.sqrt(M / Q);
        double H = Colors.calculateH(h);
        return new double[]{J, Q, C, M, s, H, h};
    }

    private static double calculateH(double h) {
        if (h < 20.14) {
            h += 360.0;
        }
        if (h >= 20.14 && h < 90.0) {
            double i = (h - 20.14) / 0.8;
            return 100.0 * i / (i + (90.0 - h) / 0.7);
        }
        if (h < 164.25) {
            double i = (h - 90.0) / 0.7;
            return 100.0 + 100.0 * i / (i + (164.25 - h) / 1.0);
        }
        if (h < 237.53) {
            double i = (h - 164.25) / 1.0;
            return 200.0 + 100.0 * i / (i + (237.53 - h) / 1.2);
        }
        if (h <= 380.14) {
            double i = (h - 237.53) / 1.2;
            double H = 300.0 + 100.0 * i / (i + (380.14 - h) / 0.8);
            if (H <= 400.0 && H >= 399.999) {
                H = 0.0;
            }
            return H;
        }
        throw new IllegalArgumentException("h outside assumed range 0..360: " + h);
    }

    private static double[] forwardResponseCompression(double[] RGB, double[] vc) {
        double[] result = new double[3];
        for (int channel = 0; channel < RGB.length; ++channel) {
            double n;
            if (RGB[channel] >= 0.0) {
                n = Math.pow(vc[13] * RGB[channel] / 100.0, 0.42);
                result[channel] = 400.0 * n / (n + 27.13) + 0.1;
                continue;
            }
            n = Math.pow(-1.0 * vc[13] * RGB[channel] / 100.0, 0.42);
            result[channel] = -400.0 * n / (n + 27.13) + 0.1;
        }
        return result;
    }

    private static double[] forwardPostAdaptationConeResponse(double[] RGB, double[] vc) {
        return new double[]{vc[14] * RGB[0], vc[15] * RGB[1], vc[16] * RGB[2]};
    }

    public static double[] CAT02toHPE(double[] RGB) {
        double[] RGBPrime = new double[]{0.7409792 * RGB[0] + 0.218025 * RGB[1] + 0.0410058 * RGB[2], 0.2853532 * RGB[0] + 0.6242014 * RGB[1] + 0.0904454 * RGB[2], -0.009628 * RGB[0] - 0.005698 * RGB[1] + 1.015326 * RGB[2]};
        return RGBPrime;
    }

    private static double[] forwardPreAdaptationConeResponse(double[] XYZ2) {
        double[] RGB = new double[]{0.7328 * XYZ2[0] + 0.4296 * XYZ2[1] - 0.1624 * XYZ2[2], -0.7036 * XYZ2[0] + 1.6975 * XYZ2[1] + 0.0061 * XYZ2[2], 0.003 * XYZ2[0] + 0.0136 * XYZ2[1] + 0.9834 * XYZ2[2]};
        return RGB;
    }

    static double[] vc(double[] xyz_w, double L_A, double Y_b, double[] surrounding) {
        double[] vc = new double[17];
        vc[0] = xyz_w[0];
        vc[1] = xyz_w[1];
        vc[2] = xyz_w[2];
        vc[3] = L_A;
        vc[4] = Y_b;
        vc[5] = surrounding[0];
        vc[6] = surrounding[1];
        vc[7] = surrounding[2];
        double[] RGB_w = Colors.forwardPreAdaptationConeResponse(xyz_w);
        double D = Math.max(0.0, Math.min(1.0, vc[5] * (1.0 - 0.2777777777777778 * Math.pow(Math.E, (-L_A - 42.0) / 92.0))));
        double Yw = xyz_w[1];
        double[] RGB_c = new double[]{D * Yw / RGB_w[0] + (1.0 - D), D * Yw / RGB_w[1] + (1.0 - D), D * Yw / RGB_w[2] + (1.0 - D)};
        double L_Ax5 = 5.0 * L_A;
        double k = 1.0 / (L_Ax5 + 1.0);
        double kpow4 = Math.pow(k, 4.0);
        vc[13] = 0.2 * kpow4 * L_Ax5 + 0.1 * Math.pow(1.0 - kpow4, 2.0) * Math.pow(L_Ax5, 0.3333333333333333);
        vc[9] = Y_b / Yw;
        vc[8] = 1.48 + Math.sqrt(vc[9]);
        vc[10] = 0.725 * Math.pow(1.0 / vc[9], 0.2);
        vc[11] = vc[10];
        double[] RGB_wc = new double[]{RGB_c[0] * RGB_w[0], RGB_c[1] * RGB_w[1], RGB_c[2] * RGB_w[2]};
        double[] RGBPrime_w = Colors.CAT02toHPE(RGB_wc);
        double[] RGBPrime_aw = new double[3];
        for (int channel = 0; channel < RGBPrime_w.length; ++channel) {
            double n;
            if (RGBPrime_w[channel] >= 0.0) {
                n = Math.pow(vc[13] * RGBPrime_w[channel] / 100.0, 0.42);
                RGBPrime_aw[channel] = 400.0 * n / (n + 27.13) + 0.1;
                continue;
            }
            n = Math.pow(-1.0 * vc[13] * RGBPrime_w[channel] / 100.0, 0.42);
            RGBPrime_aw[channel] = -400.0 * n / (n + 27.13) + 0.1;
        }
        vc[12] = (2.0 * RGBPrime_aw[0] + RGBPrime_aw[1] + RGBPrime_aw[2] / 20.0 - 0.305) * vc[10];
        vc[14] = RGB_c[0];
        vc[15] = RGB_c[1];
        vc[16] = RGB_c[2];
        return vc;
    }

    public static double[] rgb2cielab(double[] rgb) {
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

    @FunctionalInterface
    static interface Distance {
        public double compute(int var1, int var2);
    }

    private static class NamedDistance
    implements Distance {
        private final String name;
        private final Distance delegate;

        public NamedDistance(String name, Distance delegate) {
            this.name = name;
            this.delegate = delegate;
        }

        @Override
        public double compute(int c1, int c2) {
            return this.delegate.compute(c1, c2);
        }

        public String toString() {
            return this.name;
        }
    }
}

