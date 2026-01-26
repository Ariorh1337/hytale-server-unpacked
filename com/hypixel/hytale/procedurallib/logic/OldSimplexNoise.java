/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.logic;

import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.logic.DoubleArray;
import javax.annotation.Nonnull;

public class OldSimplexNoise
implements NoiseFunction {
    public static final OldSimplexNoise INSTANCE = new OldSimplexNoise();
    private static final double STRETCH_CONSTANT_2D = -0.211324865405187;
    private static final double SQUISH_CONSTANT_2D = 0.366025403784439;
    private static final double STRETCH_CONSTANT_3D = -0.16666666666666666;
    private static final double SQUISH_CONSTANT_3D = 0.3333333333333333;
    private static final double NORM_CONSTANT_2D = 47.0;
    private static final double NORM_CONSTANT_3D = 103.0;
    @Nonnull
    private static DoubleArray.Double2[] gradients2D = new DoubleArray.Double2[]{new DoubleArray.Double2(5.0, 2.0), new DoubleArray.Double2(2.0, 5.0), new DoubleArray.Double2(-5.0, 2.0), new DoubleArray.Double2(-2.0, 5.0), new DoubleArray.Double2(5.0, -2.0), new DoubleArray.Double2(2.0, -5.0), new DoubleArray.Double2(-5.0, -2.0), new DoubleArray.Double2(-2.0, -5.0)};
    @Nonnull
    private static DoubleArray.Double3[] gradients3D = new DoubleArray.Double3[]{new DoubleArray.Double3(-11.0, 4.0, 4.0), new DoubleArray.Double3(-4.0, 11.0, 4.0), new DoubleArray.Double3(-4.0, 4.0, 11.0), new DoubleArray.Double3(11.0, 4.0, 4.0), new DoubleArray.Double3(4.0, 11.0, 4.0), new DoubleArray.Double3(4.0, 4.0, 11.0), new DoubleArray.Double3(-11.0, -4.0, 4.0), new DoubleArray.Double3(-4.0, -11.0, 4.0), new DoubleArray.Double3(-4.0, -4.0, 11.0), new DoubleArray.Double3(11.0, -4.0, 4.0), new DoubleArray.Double3(4.0, -11.0, 4.0), new DoubleArray.Double3(4.0, -4.0, 11.0), new DoubleArray.Double3(-11.0, 4.0, -4.0), new DoubleArray.Double3(-4.0, 11.0, -4.0), new DoubleArray.Double3(-4.0, 4.0, -11.0), new DoubleArray.Double3(11.0, 4.0, -4.0), new DoubleArray.Double3(4.0, 11.0, -4.0), new DoubleArray.Double3(4.0, 4.0, -11.0), new DoubleArray.Double3(-11.0, -4.0, -4.0), new DoubleArray.Double3(-4.0, -11.0, -4.0), new DoubleArray.Double3(-4.0, -4.0, -11.0), new DoubleArray.Double3(11.0, -4.0, -4.0), new DoubleArray.Double3(4.0, -11.0, -4.0), new DoubleArray.Double3(4.0, -4.0, -11.0)};

    private OldSimplexNoise() {
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y) {
        double attn_ext;
        double dy_ext;
        double dx_ext;
        int ysv_ext;
        int xsv_ext;
        double dy2;
        double dx2;
        double attn2;
        double stretchOffset = (x + y) * -0.211324865405187;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;
        int xsb = OldSimplexNoise.fastFloor(xs);
        int ysb = OldSimplexNoise.fastFloor(ys);
        double squishOffset = (double)(xsb + ysb) * 0.366025403784439;
        double xb = (double)xsb + squishOffset;
        double yb = (double)ysb + squishOffset;
        double xins = xs - (double)xsb;
        double yins = ys - (double)ysb;
        double inSum = xins + yins;
        double dx0 = x - xb;
        double dy0 = y - yb;
        double value = 0.0;
        double dx1 = dx0 - 1.0 - 0.366025403784439;
        double dy1 = dy0 - 0.0 - 0.366025403784439;
        double attn1 = 2.0 - dx1 * dx1 - dy1 * dy1;
        if (attn1 > 0.0) {
            attn1 *= attn1;
            value += attn1 * attn1 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 0, dx1, dy1);
        }
        if ((attn2 = 2.0 - (dx2 = dx0 - 0.0 - 0.366025403784439) * dx2 - (dy2 = dy0 - 1.0 - 0.366025403784439) * dy2) > 0.0) {
            attn2 *= attn2;
            value += attn2 * attn2 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 1, dx2, dy2);
        }
        if (inSum <= 1.0) {
            zins = 1.0 - inSum;
            if (zins > xins || zins > yins) {
                if (xins > yins) {
                    xsv_ext = xsb + 1;
                    ysv_ext = ysb - 1;
                    dx_ext = dx0 - 1.0;
                    dy_ext = dy0 + 1.0;
                } else {
                    xsv_ext = xsb - 1;
                    ysv_ext = ysb + 1;
                    dx_ext = dx0 + 1.0;
                    dy_ext = dy0 - 1.0;
                }
            } else {
                xsv_ext = xsb + 1;
                ysv_ext = ysb + 1;
                dx_ext = dx0 - 1.0 - 0.732050807568878;
                dy_ext = dy0 - 1.0 - 0.732050807568878;
            }
        } else {
            zins = 2.0 - inSum;
            if (zins < xins || zins < yins) {
                if (xins > yins) {
                    xsv_ext = xsb + 2;
                    ysv_ext = ysb + 0;
                    dx_ext = dx0 - 2.0 - 0.732050807568878;
                    dy_ext = dy0 + 0.0 - 0.732050807568878;
                } else {
                    xsv_ext = xsb + 0;
                    ysv_ext = ysb + 2;
                    dx_ext = dx0 + 0.0 - 0.732050807568878;
                    dy_ext = dy0 - 2.0 - 0.732050807568878;
                }
            } else {
                dx_ext = dx0;
                dy_ext = dy0;
                xsv_ext = xsb;
                ysv_ext = ysb;
            }
            ++xsb;
            ++ysb;
            dx0 = dx0 - 1.0 - 0.732050807568878;
            dy0 = dy0 - 1.0 - 0.732050807568878;
        }
        double attn0 = 2.0 - dx0 * dx0 - dy0 * dy0;
        if (attn0 > 0.0) {
            attn0 *= attn0;
            value += attn0 * attn0 * OldSimplexNoise.extrapolate(offsetSeed, xsb, ysb, dx0, dy0);
        }
        if ((attn_ext = 2.0 - dx_ext * dx_ext - dy_ext * dy_ext) > 0.0) {
            attn_ext *= attn_ext;
            value += attn_ext * attn_ext * OldSimplexNoise.extrapolate(offsetSeed, xsv_ext, ysv_ext, dx_ext, dy_ext);
        }
        return value / 47.0;
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y, double z) {
        double attn_ext1;
        double dz_ext1;
        double dz_ext0;
        int zsv_ext1;
        int zsv_ext0;
        double dy_ext0;
        double dy_ext1;
        int ysv_ext0;
        int ysv_ext1;
        double dx_ext1;
        double dx_ext0;
        int xsv_ext1;
        int xsv_ext0;
        double stretchOffset = (x + y + z) * -0.16666666666666666;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;
        double zs = z + stretchOffset;
        int xsb = OldSimplexNoise.fastFloor(xs);
        int ysb = OldSimplexNoise.fastFloor(ys);
        int zsb = OldSimplexNoise.fastFloor(zs);
        double squishOffset = (double)(xsb + ysb + zsb) * 0.3333333333333333;
        double xb = (double)xsb + squishOffset;
        double yb = (double)ysb + squishOffset;
        double zb = (double)zsb + squishOffset;
        double xins = xs - (double)xsb;
        double yins = ys - (double)ysb;
        double zins = zs - (double)zsb;
        double inSum = xins + yins + zins;
        double dx0 = x - xb;
        double dy0 = y - yb;
        double dz0 = z - zb;
        double value = 0.0;
        if (inSum <= 1.0) {
            double dz3;
            double dy3;
            double dx3;
            double attn3;
            double dz2;
            double dy2;
            double dx2;
            double attn2;
            double dz1;
            double dy1;
            double dx1;
            double attn1;
            int aPoint = 1;
            double aScore = xins;
            int bPoint = 2;
            double bScore = yins;
            if (aScore >= bScore && zins > bScore) {
                bScore = zins;
                bPoint = 4;
            } else if (aScore < bScore && zins > aScore) {
                aScore = zins;
                aPoint = 4;
            }
            double wins = 1.0 - inSum;
            if (wins > aScore || wins > bScore) {
                int n = c = bScore > aScore ? bPoint : aPoint;
                if ((c & 1) == 0) {
                    xsv_ext0 = xsb - 1;
                    xsv_ext1 = xsb;
                    dx_ext0 = dx0 + 1.0;
                    dx_ext1 = dx0;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb + 1;
                    dx_ext0 = dx_ext1 = dx0 - 1.0;
                }
                if ((c & 2) == 0) {
                    ysv_ext1 = ysb;
                    ysv_ext0 = ysv_ext1--;
                    dy_ext0 = dy_ext1 = dy0;
                    if ((c & 1) == 0) {
                        dy_ext1 += 1.0;
                    } else {
                        --ysv_ext0;
                        dy_ext0 += 1.0;
                    }
                } else {
                    ysv_ext0 = ysv_ext1 = ysb + 1;
                    dy_ext0 = dy_ext1 = dy0 - 1.0;
                }
                if ((c & 4) == 0) {
                    zsv_ext0 = zsb;
                    zsv_ext1 = zsb - 1;
                    dz_ext0 = dz0;
                    dz_ext1 = dz0 + 1.0;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb + 1;
                    dz_ext0 = dz_ext1 = dz0 - 1.0;
                }
            } else {
                c = (byte)(aPoint | bPoint);
                if ((c & 1) == 0) {
                    xsv_ext0 = xsb;
                    xsv_ext1 = xsb - 1;
                    dx_ext0 = dx0 - 0.6666666666666666;
                    dx_ext1 = dx0 + 1.0 - 0.3333333333333333;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb + 1;
                    dx_ext0 = dx0 - 1.0 - 0.6666666666666666;
                    dx_ext1 = dx0 - 1.0 - 0.3333333333333333;
                }
                if ((c & 2) == 0) {
                    ysv_ext0 = ysb;
                    ysv_ext1 = ysb - 1;
                    dy_ext0 = dy0 - 0.6666666666666666;
                    dy_ext1 = dy0 + 1.0 - 0.3333333333333333;
                } else {
                    ysv_ext0 = ysv_ext1 = ysb + 1;
                    dy_ext0 = dy0 - 1.0 - 0.6666666666666666;
                    dy_ext1 = dy0 - 1.0 - 0.3333333333333333;
                }
                if ((c & 4) == 0) {
                    zsv_ext0 = zsb;
                    zsv_ext1 = zsb - 1;
                    dz_ext0 = dz0 - 0.6666666666666666;
                    dz_ext1 = dz0 + 1.0 - 0.3333333333333333;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb + 1;
                    dz_ext0 = dz0 - 1.0 - 0.6666666666666666;
                    dz_ext1 = dz0 - 1.0 - 0.3333333333333333;
                }
            }
            double attn0 = 2.0 - dx0 * dx0 - dy0 * dy0 - dz0 * dz0;
            if (attn0 > 0.0) {
                attn0 *= attn0;
                value += attn0 * attn0 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 0, zsb + 0, dx0, dy0, dz0);
            }
            if ((attn1 = 2.0 - (dx1 = dx0 - 1.0 - 0.3333333333333333) * dx1 - (dy1 = dy0 - 0.0 - 0.3333333333333333) * dy1 - (dz1 = dz0 - 0.0 - 0.3333333333333333) * dz1) > 0.0) {
                attn1 *= attn1;
                value += attn1 * attn1 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 0, zsb + 0, dx1, dy1, dz1);
            }
            if ((attn2 = 2.0 - (dx2 = dx0 - 0.0 - 0.3333333333333333) * dx2 - (dy2 = dy0 - 1.0 - 0.3333333333333333) * dy2 - (dz2 = dz1) * dz2) > 0.0) {
                attn2 *= attn2;
                value += attn2 * attn2 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 1, zsb + 0, dx2, dy2, dz2);
            }
            if ((attn3 = 2.0 - (dx3 = dx2) * dx3 - (dy3 = dy1) * dy3 - (dz3 = dz0 - 1.0 - 0.3333333333333333) * dz3) > 0.0) {
                attn3 *= attn3;
                value += attn3 * attn3 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 0, zsb + 1, dx3, dy3, dz3);
            }
        } else if (inSum >= 2.0) {
            double attn0;
            double dz1;
            double dy1;
            double dx1;
            double attn1;
            double dz2;
            double dy2;
            double dx2;
            double attn2;
            int aPoint = 6;
            double aScore = xins;
            int bPoint = 5;
            double bScore = yins;
            if (aScore <= bScore && zins < bScore) {
                bScore = zins;
                bPoint = 3;
            } else if (aScore > bScore && zins < aScore) {
                aScore = zins;
                aPoint = 3;
            }
            double wins = 3.0 - inSum;
            if (wins < aScore || wins < bScore) {
                int n = c = bScore < aScore ? bPoint : aPoint;
                if ((c & 1) != 0) {
                    xsv_ext0 = xsb + 2;
                    xsv_ext1 = xsb + 1;
                    dx_ext0 = dx0 - 2.0 - 1.0;
                    dx_ext1 = dx0 - 1.0 - 1.0;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb;
                    dx_ext0 = dx_ext1 = dx0 - 1.0;
                }
                if ((c & 2) != 0) {
                    ysv_ext1 = ysb + 1;
                    ysv_ext0 = ysv_ext1++;
                    dy_ext0 = dy_ext1 = dy0 - 1.0 - 1.0;
                    if ((c & 1) != 0) {
                        dy_ext1 -= 1.0;
                    } else {
                        ++ysv_ext0;
                        dy_ext0 -= 1.0;
                    }
                } else {
                    ysv_ext0 = ysv_ext1 = ysb;
                    dy_ext0 = dy_ext1 = dy0 - 1.0;
                }
                if ((c & 4) != 0) {
                    zsv_ext0 = zsb + 1;
                    zsv_ext1 = zsb + 2;
                    dz_ext0 = dz0 - 1.0 - 1.0;
                    dz_ext1 = dz0 - 2.0 - 1.0;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb;
                    dz_ext0 = dz_ext1 = dz0 - 1.0;
                }
            } else {
                c = (byte)(aPoint & bPoint);
                if ((c & 1) != 0) {
                    xsv_ext0 = xsb + 1;
                    xsv_ext1 = xsb + 2;
                    dx_ext0 = dx0 - 1.0 - 0.3333333333333333;
                    dx_ext1 = dx0 - 2.0 - 0.6666666666666666;
                } else {
                    xsv_ext0 = xsv_ext1 = xsb;
                    dx_ext0 = dx0 - 0.3333333333333333;
                    dx_ext1 = dx0 - 0.6666666666666666;
                }
                if ((c & 2) != 0) {
                    ysv_ext0 = ysb + 1;
                    ysv_ext1 = ysb + 2;
                    dy_ext0 = dy0 - 1.0 - 0.3333333333333333;
                    dy_ext1 = dy0 - 2.0 - 0.6666666666666666;
                } else {
                    ysv_ext0 = ysv_ext1 = ysb;
                    dy_ext0 = dy0 - 0.3333333333333333;
                    dy_ext1 = dy0 - 0.6666666666666666;
                }
                if ((c & 4) != 0) {
                    zsv_ext0 = zsb + 1;
                    zsv_ext1 = zsb + 2;
                    dz_ext0 = dz0 - 1.0 - 0.3333333333333333;
                    dz_ext1 = dz0 - 2.0 - 0.6666666666666666;
                } else {
                    zsv_ext0 = zsv_ext1 = zsb;
                    dz_ext0 = dz0 - 0.3333333333333333;
                    dz_ext1 = dz0 - 0.6666666666666666;
                }
            }
            double dx3 = dx0 - 1.0 - 0.6666666666666666;
            double dy3 = dy0 - 1.0 - 0.6666666666666666;
            double dz3 = dz0 - 0.0 - 0.6666666666666666;
            double attn3 = 2.0 - dx3 * dx3 - dy3 * dy3 - dz3 * dz3;
            if (attn3 > 0.0) {
                attn3 *= attn3;
                value += attn3 * attn3 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 1, zsb + 0, dx3, dy3, dz3);
            }
            if ((attn2 = 2.0 - (dx2 = dx3) * dx2 - (dy2 = dy0 - 0.0 - 0.6666666666666666) * dy2 - (dz2 = dz0 - 1.0 - 0.6666666666666666) * dz2) > 0.0) {
                attn2 *= attn2;
                value += attn2 * attn2 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 0, zsb + 1, dx2, dy2, dz2);
            }
            if ((attn1 = 2.0 - (dx1 = dx0 - 0.0 - 0.6666666666666666) * dx1 - (dy1 = dy3) * dy1 - (dz1 = dz2) * dz1) > 0.0) {
                attn1 *= attn1;
                value += attn1 * attn1 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 1, zsb + 1, dx1, dy1, dz1);
            }
            if ((attn0 = 2.0 - (dx0 = dx0 - 1.0 - 1.0) * dx0 - (dy0 = dy0 - 1.0 - 1.0) * dy0 - (dz0 = dz0 - 1.0 - 1.0) * dz0) > 0.0) {
                attn0 *= attn0;
                value += attn0 * attn0 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 1, zsb + 1, dx0, dy0, dz0);
            }
        } else {
            double dz6;
            double dy6;
            double dx6;
            double attn6;
            double dz5;
            double dy5;
            double dx5;
            double attn5;
            double dz4;
            double dy4;
            double dx4;
            double attn4;
            double dz3;
            double dy3;
            double dx3;
            double attn3;
            double dz2;
            double dy2;
            double dx2;
            double attn2;
            boolean bIsFurtherSide;
            int bPoint;
            double bScore;
            boolean aIsFurtherSide;
            int aPoint;
            double aScore;
            double p1 = xins + yins;
            if (p1 > 1.0) {
                aScore = p1 - 1.0;
                aPoint = 3;
                aIsFurtherSide = true;
            } else {
                aScore = 1.0 - p1;
                aPoint = 4;
                aIsFurtherSide = false;
            }
            double p2 = xins + zins;
            if (p2 > 1.0) {
                bScore = p2 - 1.0;
                bPoint = 5;
                bIsFurtherSide = true;
            } else {
                bScore = 1.0 - p2;
                bPoint = 2;
                bIsFurtherSide = false;
            }
            double p3 = yins + zins;
            if (p3 > 1.0) {
                score = p3 - 1.0;
                if (aScore <= bScore && aScore < score) {
                    aScore = score;
                    aPoint = 6;
                    aIsFurtherSide = true;
                } else if (aScore > bScore && bScore < score) {
                    bScore = score;
                    bPoint = 6;
                    bIsFurtherSide = true;
                }
            } else {
                score = 1.0 - p3;
                if (aScore <= bScore && aScore < score) {
                    aScore = score;
                    aPoint = 1;
                    aIsFurtherSide = false;
                } else if (aScore > bScore && bScore < score) {
                    bScore = score;
                    bPoint = 1;
                    bIsFurtherSide = false;
                }
            }
            if (aIsFurtherSide == bIsFurtherSide) {
                if (aIsFurtherSide) {
                    dx_ext0 = dx0 - 1.0 - 1.0;
                    dy_ext0 = dy0 - 1.0 - 1.0;
                    dz_ext0 = dz0 - 1.0 - 1.0;
                    xsv_ext0 = xsb + 1;
                    ysv_ext0 = ysb + 1;
                    zsv_ext0 = zsb + 1;
                    c = (byte)(aPoint & bPoint);
                    if ((c & 1) != 0) {
                        dx_ext1 = dx0 - 2.0 - 0.6666666666666666;
                        dy_ext1 = dy0 - 0.6666666666666666;
                        dz_ext1 = dz0 - 0.6666666666666666;
                        xsv_ext1 = xsb + 2;
                        ysv_ext1 = ysb;
                        zsv_ext1 = zsb;
                    } else if ((c & 2) != 0) {
                        dx_ext1 = dx0 - 0.6666666666666666;
                        dy_ext1 = dy0 - 2.0 - 0.6666666666666666;
                        dz_ext1 = dz0 - 0.6666666666666666;
                        xsv_ext1 = xsb;
                        ysv_ext1 = ysb + 2;
                        zsv_ext1 = zsb;
                    } else {
                        dx_ext1 = dx0 - 0.6666666666666666;
                        dy_ext1 = dy0 - 0.6666666666666666;
                        dz_ext1 = dz0 - 2.0 - 0.6666666666666666;
                        xsv_ext1 = xsb;
                        ysv_ext1 = ysb;
                        zsv_ext1 = zsb + 2;
                    }
                } else {
                    dx_ext0 = dx0;
                    dy_ext0 = dy0;
                    dz_ext0 = dz0;
                    xsv_ext0 = xsb;
                    ysv_ext0 = ysb;
                    zsv_ext0 = zsb;
                    c = (byte)(aPoint | bPoint);
                    if ((c & 1) == 0) {
                        dx_ext1 = dx0 + 1.0 - 0.3333333333333333;
                        dy_ext1 = dy0 - 1.0 - 0.3333333333333333;
                        dz_ext1 = dz0 - 1.0 - 0.3333333333333333;
                        xsv_ext1 = xsb - 1;
                        ysv_ext1 = ysb + 1;
                        zsv_ext1 = zsb + 1;
                    } else if ((c & 2) == 0) {
                        dx_ext1 = dx0 - 1.0 - 0.3333333333333333;
                        dy_ext1 = dy0 + 1.0 - 0.3333333333333333;
                        dz_ext1 = dz0 - 1.0 - 0.3333333333333333;
                        xsv_ext1 = xsb + 1;
                        ysv_ext1 = ysb - 1;
                        zsv_ext1 = zsb + 1;
                    } else {
                        dx_ext1 = dx0 - 1.0 - 0.3333333333333333;
                        dy_ext1 = dy0 - 1.0 - 0.3333333333333333;
                        dz_ext1 = dz0 + 1.0 - 0.3333333333333333;
                        xsv_ext1 = xsb + 1;
                        ysv_ext1 = ysb + 1;
                        zsv_ext1 = zsb - 1;
                    }
                }
            } else {
                int c2;
                int c1;
                if (aIsFurtherSide) {
                    c1 = aPoint;
                    c2 = bPoint;
                } else {
                    c1 = bPoint;
                    c2 = aPoint;
                }
                if ((c1 & 1) == 0) {
                    dx_ext0 = dx0 + 1.0 - 0.3333333333333333;
                    dy_ext0 = dy0 - 1.0 - 0.3333333333333333;
                    dz_ext0 = dz0 - 1.0 - 0.3333333333333333;
                    xsv_ext0 = xsb - 1;
                    ysv_ext0 = ysb + 1;
                    zsv_ext0 = zsb + 1;
                } else if ((c1 & 2) == 0) {
                    dx_ext0 = dx0 - 1.0 - 0.3333333333333333;
                    dy_ext0 = dy0 + 1.0 - 0.3333333333333333;
                    dz_ext0 = dz0 - 1.0 - 0.3333333333333333;
                    xsv_ext0 = xsb + 1;
                    ysv_ext0 = ysb - 1;
                    zsv_ext0 = zsb + 1;
                } else {
                    dx_ext0 = dx0 - 1.0 - 0.3333333333333333;
                    dy_ext0 = dy0 - 1.0 - 0.3333333333333333;
                    dz_ext0 = dz0 + 1.0 - 0.3333333333333333;
                    xsv_ext0 = xsb + 1;
                    ysv_ext0 = ysb + 1;
                    zsv_ext0 = zsb - 1;
                }
                dx_ext1 = dx0 - 0.6666666666666666;
                dy_ext1 = dy0 - 0.6666666666666666;
                dz_ext1 = dz0 - 0.6666666666666666;
                xsv_ext1 = xsb;
                ysv_ext1 = ysb;
                zsv_ext1 = zsb;
                if ((c2 & 1) != 0) {
                    dx_ext1 -= 2.0;
                    xsv_ext1 += 2;
                } else if ((c2 & 2) != 0) {
                    dy_ext1 -= 2.0;
                    ysv_ext1 += 2;
                } else {
                    dz_ext1 -= 2.0;
                    zsv_ext1 += 2;
                }
            }
            double dx1 = dx0 - 1.0 - 0.3333333333333333;
            double dy1 = dy0 - 0.0 - 0.3333333333333333;
            double dz1 = dz0 - 0.0 - 0.3333333333333333;
            double attn1 = 2.0 - dx1 * dx1 - dy1 * dy1 - dz1 * dz1;
            if (attn1 > 0.0) {
                attn1 *= attn1;
                value += attn1 * attn1 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 0, zsb + 0, dx1, dy1, dz1);
            }
            if ((attn2 = 2.0 - (dx2 = dx0 - 0.0 - 0.3333333333333333) * dx2 - (dy2 = dy0 - 1.0 - 0.3333333333333333) * dy2 - (dz2 = dz1) * dz2) > 0.0) {
                attn2 *= attn2;
                value += attn2 * attn2 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 1, zsb + 0, dx2, dy2, dz2);
            }
            if ((attn3 = 2.0 - (dx3 = dx2) * dx3 - (dy3 = dy1) * dy3 - (dz3 = dz0 - 1.0 - 0.3333333333333333) * dz3) > 0.0) {
                attn3 *= attn3;
                value += attn3 * attn3 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 0, zsb + 1, dx3, dy3, dz3);
            }
            if ((attn4 = 2.0 - (dx4 = dx0 - 1.0 - 0.6666666666666666) * dx4 - (dy4 = dy0 - 1.0 - 0.6666666666666666) * dy4 - (dz4 = dz0 - 0.0 - 0.6666666666666666) * dz4) > 0.0) {
                attn4 *= attn4;
                value += attn4 * attn4 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 1, zsb + 0, dx4, dy4, dz4);
            }
            if ((attn5 = 2.0 - (dx5 = dx4) * dx5 - (dy5 = dy0 - 0.0 - 0.6666666666666666) * dy5 - (dz5 = dz0 - 1.0 - 0.6666666666666666) * dz5) > 0.0) {
                attn5 *= attn5;
                value += attn5 * attn5 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 1, ysb + 0, zsb + 1, dx5, dy5, dz5);
            }
            if ((attn6 = 2.0 - (dx6 = dx0 - 0.0 - 0.6666666666666666) * dx6 - (dy6 = dy4) * dy6 - (dz6 = dz5) * dz6) > 0.0) {
                attn6 *= attn6;
                value += attn6 * attn6 * OldSimplexNoise.extrapolate(offsetSeed, xsb + 0, ysb + 1, zsb + 1, dx6, dy6, dz6);
            }
        }
        double attn_ext0 = 2.0 - dx_ext0 * dx_ext0 - dy_ext0 * dy_ext0 - dz_ext0 * dz_ext0;
        if (attn_ext0 > 0.0) {
            attn_ext0 *= attn_ext0;
            value += attn_ext0 * attn_ext0 * OldSimplexNoise.extrapolate(offsetSeed, xsv_ext0, ysv_ext0, zsv_ext0, dx_ext0, dy_ext0, dz_ext0);
        }
        if ((attn_ext1 = 2.0 - dx_ext1 * dx_ext1 - dy_ext1 * dy_ext1 - dz_ext1 * dz_ext1) > 0.0) {
            attn_ext1 *= attn_ext1;
            value += attn_ext1 * attn_ext1 * OldSimplexNoise.extrapolate(offsetSeed, xsv_ext1, ysv_ext1, zsv_ext1, dx_ext1, dy_ext1, dz_ext1);
        }
        return value / 103.0;
    }

    @Nonnull
    public String toString() {
        return "OldSimplexNoise{}";
    }

    private static double extrapolate(int seed, int x, int y, double xd, double yd) {
        int hash = seed;
        hash ^= 1619 * x;
        hash ^= 31337 * y;
        hash = hash * hash * hash * 60493;
        hash = hash >> 13 ^ hash;
        DoubleArray.Double2 g = gradients2D[hash & 7];
        return xd * g.x + yd * g.y;
    }

    private static double extrapolate(int seed, int x, int y, int z, double xd, double yd, double zd) {
        int hash = seed;
        hash ^= 1619 * x;
        hash ^= 31337 * y;
        hash ^= 6971 * z;
        hash = hash * hash * hash * 60493;
        hash = hash >> 13 ^ hash;
        DoubleArray.Double3 g = gradients3D[hash % gradients3D.length];
        return xd * g.x + yd * g.y + zd * g.z;
    }

    private static int fastFloor(double x) {
        int xi = (int)x;
        return x < (double)xi ? xi - 1 : xi;
    }
}

