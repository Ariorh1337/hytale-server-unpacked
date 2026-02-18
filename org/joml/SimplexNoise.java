/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

public class SimplexNoise {
    private static final Vector3b[] grad3 = new Vector3b[]{new Vector3b(1, 1, 0), new Vector3b(-1, 1, 0), new Vector3b(1, -1, 0), new Vector3b(-1, -1, 0), new Vector3b(1, 0, 1), new Vector3b(-1, 0, 1), new Vector3b(1, 0, -1), new Vector3b(-1, 0, -1), new Vector3b(0, 1, 1), new Vector3b(0, -1, 1), new Vector3b(0, 1, -1), new Vector3b(0, -1, -1)};
    private static final Vector4b[] grad4 = new Vector4b[]{new Vector4b(0, 1, 1, 1), new Vector4b(0, 1, 1, -1), new Vector4b(0, 1, -1, 1), new Vector4b(0, 1, -1, -1), new Vector4b(0, -1, 1, 1), new Vector4b(0, -1, 1, -1), new Vector4b(0, -1, -1, 1), new Vector4b(0, -1, -1, -1), new Vector4b(1, 0, 1, 1), new Vector4b(1, 0, 1, -1), new Vector4b(1, 0, -1, 1), new Vector4b(1, 0, -1, -1), new Vector4b(-1, 0, 1, 1), new Vector4b(-1, 0, 1, -1), new Vector4b(-1, 0, -1, 1), new Vector4b(-1, 0, -1, -1), new Vector4b(1, 1, 0, 1), new Vector4b(1, 1, 0, -1), new Vector4b(1, -1, 0, 1), new Vector4b(1, -1, 0, -1), new Vector4b(-1, 1, 0, 1), new Vector4b(-1, 1, 0, -1), new Vector4b(-1, -1, 0, 1), new Vector4b(-1, -1, 0, -1), new Vector4b(1, 1, 1, 0), new Vector4b(1, 1, -1, 0), new Vector4b(1, -1, 1, 0), new Vector4b(1, -1, -1, 0), new Vector4b(-1, 1, 1, 0), new Vector4b(-1, 1, -1, 0), new Vector4b(-1, -1, 1, 0), new Vector4b(-1, -1, -1, 0)};
    private static final byte[] p = new byte[]{-105, -96, -119, 91, 90, 15, -125, 13, -55, 95, 96, 53, -62, -23, 7, -31, -116, 36, 103, 30, 69, -114, 8, 99, 37, -16, 21, 10, 23, -66, 6, -108, -9, 120, -22, 75, 0, 26, -59, 62, 94, -4, -37, -53, 117, 35, 11, 32, 57, -79, 33, 88, -19, -107, 56, 87, -82, 20, 125, -120, -85, -88, 68, -81, 74, -91, 71, -122, -117, 48, 27, -90, 77, -110, -98, -25, 83, 111, -27, 122, 60, -45, -123, -26, -36, 105, 92, 41, 55, 46, -11, 40, -12, 102, -113, 54, 65, 25, 63, -95, 1, -40, 80, 73, -47, 76, -124, -69, -48, 89, 18, -87, -56, -60, -121, -126, 116, -68, -97, 86, -92, 100, 109, -58, -83, -70, 3, 64, 52, -39, -30, -6, 124, 123, 5, -54, 38, -109, 118, 126, -1, 82, 85, -44, -49, -50, 59, -29, 47, 16, 58, 17, -74, -67, 28, 42, -33, -73, -86, -43, 119, -8, -104, 2, 44, -102, -93, 70, -35, -103, 101, -101, -89, 43, -84, 9, -127, 22, 39, -3, 19, 98, 108, 110, 79, 113, -32, -24, -78, -71, 112, 104, -38, -10, 97, -28, -5, 34, -14, -63, -18, -46, -112, 12, -65, -77, -94, -15, 81, 51, -111, -21, -7, 14, -17, 107, 49, -64, -42, 31, -75, -57, 106, -99, -72, 84, -52, -80, 115, 121, 50, 45, 127, 4, -106, -2, -118, -20, -51, 93, -34, 114, 67, 29, 24, 72, -13, -115, -128, -61, 78, 66, -41, 61, -100, -76};
    private static final byte[] perm = new byte[512];
    private static final byte[] permMod12 = new byte[512];
    private static final float F2 = 0.36602542f;
    private static final float G2 = 0.21132487f;
    private static final float F3 = 0.33333334f;
    private static final float G3 = 0.16666667f;
    private static final float F4 = 0.309017f;
    private static final float G4 = 0.1381966f;

    private static int fastfloor(float x) {
        int xi = (int)x;
        return x < (float)xi ? xi - 1 : xi;
    }

    private static float dot(Vector3b g, float x, float y) {
        return (float)g.x * x + (float)g.y * y;
    }

    private static float dot(Vector3b g, float x, float y, float z) {
        return (float)g.x * x + (float)g.y * y + (float)g.z * z;
    }

    private static float dot(Vector4b g, float x, float y, float z, float w) {
        return (float)g.x * x + (float)g.y * y + (float)g.z * z + (float)g.w * w;
    }

    public static float noise(float x, float y) {
        float n2;
        float n1;
        float n0;
        int j1;
        int i1;
        float Y0;
        float y0;
        int j;
        float t;
        float s = (x + y) * 0.36602542f;
        int i = SimplexNoise.fastfloor(x + s);
        float X0 = (float)i - (t = (float)(i + (j = SimplexNoise.fastfloor(y + s))) * 0.21132487f);
        float x0 = x - X0;
        if (x0 > (y0 = y - (Y0 = (float)j - t))) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }
        float x1 = x0 - (float)i1 + 0.21132487f;
        float y1 = y0 - (float)j1 + 0.21132487f;
        float x2 = x0 - 1.0f + 0.42264974f;
        float y2 = y0 - 1.0f + 0.42264974f;
        int ii = i & 0xFF;
        int jj = j & 0xFF;
        int gi0 = permMod12[ii + perm[jj] & 0xFF] & 0xFF;
        int gi1 = permMod12[ii + i1 + perm[jj + j1] & 0xFF] & 0xFF;
        int gi2 = permMod12[ii + 1 + perm[jj + 1] & 0xFF] & 0xFF;
        float t0 = 0.5f - x0 * x0 - y0 * y0;
        if (t0 < 0.0f) {
            n0 = 0.0f;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * SimplexNoise.dot(grad3[gi0], x0, y0);
        }
        float t1 = 0.5f - x1 * x1 - y1 * y1;
        if (t1 < 0.0f) {
            n1 = 0.0f;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * SimplexNoise.dot(grad3[gi1], x1, y1);
        }
        float t2 = 0.5f - x2 * x2 - y2 * y2;
        if (t2 < 0.0f) {
            n2 = 0.0f;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * SimplexNoise.dot(grad3[gi2], x2, y2);
        }
        return 70.0f * (n0 + n1 + n2);
    }

    public static float noise(float x, float y, float z) {
        float n3;
        float n2;
        float n1;
        float n0;
        int k2;
        int j2;
        int i2;
        int k1;
        int j1;
        int i1;
        float s = (x + y + z) * 0.33333334f;
        int i = SimplexNoise.fastfloor(x + s);
        int j = SimplexNoise.fastfloor(y + s);
        int k = SimplexNoise.fastfloor(z + s);
        float t = (float)(i + j + k) * 0.16666667f;
        float X0 = (float)i - t;
        float Y0 = (float)j - t;
        float Z0 = (float)k - t;
        float x0 = x - X0;
        float y0 = y - Y0;
        float z0 = z - Z0;
        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else if (y0 < z0) {
            i1 = 0;
            j1 = 0;
            k1 = 1;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else if (x0 < z0) {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 0;
            j2 = 1;
            k2 = 1;
        } else {
            i1 = 0;
            j1 = 1;
            k1 = 0;
            i2 = 1;
            j2 = 1;
            k2 = 0;
        }
        float x1 = x0 - (float)i1 + 0.16666667f;
        float y1 = y0 - (float)j1 + 0.16666667f;
        float z1 = z0 - (float)k1 + 0.16666667f;
        float x2 = x0 - (float)i2 + 0.33333334f;
        float y2 = y0 - (float)j2 + 0.33333334f;
        float z2 = z0 - (float)k2 + 0.33333334f;
        float x3 = x0 - 1.0f + 0.5f;
        float y3 = y0 - 1.0f + 0.5f;
        float z3 = z0 - 1.0f + 0.5f;
        int ii = i & 0xFF;
        int jj = j & 0xFF;
        int kk = k & 0xFF;
        int gi0 = permMod12[ii + perm[jj + perm[kk] & 0xFF] & 0xFF] & 0xFF;
        int gi1 = permMod12[ii + i1 + perm[jj + j1 + perm[kk + k1] & 0xFF] & 0xFF] & 0xFF;
        int gi2 = permMod12[ii + i2 + perm[jj + j2 + perm[kk + k2] & 0xFF] & 0xFF] & 0xFF;
        int gi3 = permMod12[ii + 1 + perm[jj + 1 + perm[kk + 1] & 0xFF] & 0xFF] & 0xFF;
        float t0 = 0.6f - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 < 0.0f) {
            n0 = 0.0f;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * SimplexNoise.dot(grad3[gi0], x0, y0, z0);
        }
        float t1 = 0.6f - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 < 0.0f) {
            n1 = 0.0f;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * SimplexNoise.dot(grad3[gi1], x1, y1, z1);
        }
        float t2 = 0.6f - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 < 0.0f) {
            n2 = 0.0f;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * SimplexNoise.dot(grad3[gi2], x2, y2, z2);
        }
        float t3 = 0.6f - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 < 0.0f) {
            n3 = 0.0f;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * SimplexNoise.dot(grad3[gi3], x3, y3, z3);
        }
        return 32.0f * (n0 + n1 + n2 + n3);
    }

    public static float noise(float x, float y, float z, float w) {
        float n4;
        float n3;
        float n2;
        float n1;
        float n0;
        float s = (x + y + z + w) * 0.309017f;
        int i = SimplexNoise.fastfloor(x + s);
        int j = SimplexNoise.fastfloor(y + s);
        int k = SimplexNoise.fastfloor(z + s);
        int l = SimplexNoise.fastfloor(w + s);
        float t = (float)(i + j + k + l) * 0.1381966f;
        float X0 = (float)i - t;
        float Y0 = (float)j - t;
        float Z0 = (float)k - t;
        float W0 = (float)l - t;
        float x0 = x - X0;
        float y0 = y - Y0;
        float z0 = z - Z0;
        float w0 = w - W0;
        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        if (x0 > y0) {
            ++rankx;
        } else {
            ++ranky;
        }
        if (x0 > z0) {
            ++rankx;
        } else {
            ++rankz;
        }
        if (x0 > w0) {
            ++rankx;
        } else {
            ++rankw;
        }
        if (y0 > z0) {
            ++ranky;
        } else {
            ++rankz;
        }
        if (y0 > w0) {
            ++ranky;
        } else {
            ++rankw;
        }
        if (z0 > w0) {
            ++rankz;
        } else {
            ++rankw;
        }
        int i1 = rankx >= 3 ? 1 : 0;
        int j1 = ranky >= 3 ? 1 : 0;
        int k1 = rankz >= 3 ? 1 : 0;
        int l1 = rankw >= 3 ? 1 : 0;
        int i2 = rankx >= 2 ? 1 : 0;
        int j2 = ranky >= 2 ? 1 : 0;
        int k2 = rankz >= 2 ? 1 : 0;
        int l2 = rankw >= 2 ? 1 : 0;
        int i3 = rankx >= 1 ? 1 : 0;
        int j3 = ranky >= 1 ? 1 : 0;
        int k3 = rankz >= 1 ? 1 : 0;
        int l3 = rankw >= 1 ? 1 : 0;
        float x1 = x0 - (float)i1 + 0.1381966f;
        float y1 = y0 - (float)j1 + 0.1381966f;
        float z1 = z0 - (float)k1 + 0.1381966f;
        float w1 = w0 - (float)l1 + 0.1381966f;
        float x2 = x0 - (float)i2 + 0.2763932f;
        float y2 = y0 - (float)j2 + 0.2763932f;
        float z2 = z0 - (float)k2 + 0.2763932f;
        float w2 = w0 - (float)l2 + 0.2763932f;
        float x3 = x0 - (float)i3 + 0.41458982f;
        float y3 = y0 - (float)j3 + 0.41458982f;
        float z3 = z0 - (float)k3 + 0.41458982f;
        float w3 = w0 - (float)l3 + 0.41458982f;
        float x4 = x0 - 1.0f + 0.5527864f;
        float y4 = y0 - 1.0f + 0.5527864f;
        float z4 = z0 - 1.0f + 0.5527864f;
        float w4 = w0 - 1.0f + 0.5527864f;
        int ii = i & 0xFF;
        int jj = j & 0xFF;
        int kk = k & 0xFF;
        int ll = l & 0xFF;
        int gi0 = (perm[ii + perm[jj + perm[kk + perm[ll] & 0xFF] & 0xFF] & 0xFF] & 0xFF) % 32;
        int gi1 = (perm[ii + i1 + perm[jj + j1 + perm[kk + k1 + perm[ll + l1] & 0xFF] & 0xFF] & 0xFF] & 0xFF) % 32;
        int gi2 = (perm[ii + i2 + perm[jj + j2 + perm[kk + k2 + perm[ll + l2] & 0xFF] & 0xFF] & 0xFF] & 0xFF) % 32;
        int gi3 = (perm[ii + i3 + perm[jj + j3 + perm[kk + k3 + perm[ll + l3] & 0xFF] & 0xFF] & 0xFF] & 0xFF) % 32;
        int gi4 = (perm[ii + 1 + perm[jj + 1 + perm[kk + 1 + perm[ll + 1] & 0xFF] & 0xFF] & 0xFF] & 0xFF) % 32;
        float t0 = 0.6f - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        if (t0 < 0.0f) {
            n0 = 0.0f;
        } else {
            t0 *= t0;
            n0 = t0 * t0 * SimplexNoise.dot(grad4[gi0], x0, y0, z0, w0);
        }
        float t1 = 0.6f - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        if (t1 < 0.0f) {
            n1 = 0.0f;
        } else {
            t1 *= t1;
            n1 = t1 * t1 * SimplexNoise.dot(grad4[gi1], x1, y1, z1, w1);
        }
        float t2 = 0.6f - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        if (t2 < 0.0f) {
            n2 = 0.0f;
        } else {
            t2 *= t2;
            n2 = t2 * t2 * SimplexNoise.dot(grad4[gi2], x2, y2, z2, w2);
        }
        float t3 = 0.6f - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        if (t3 < 0.0f) {
            n3 = 0.0f;
        } else {
            t3 *= t3;
            n3 = t3 * t3 * SimplexNoise.dot(grad4[gi3], x3, y3, z3, w3);
        }
        float t4 = 0.6f - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        if (t4 < 0.0f) {
            n4 = 0.0f;
        } else {
            t4 *= t4;
            n4 = t4 * t4 * SimplexNoise.dot(grad4[gi4], x4, y4, z4, w4);
        }
        return 27.0f * (n0 + n1 + n2 + n3 + n4);
    }

    static {
        for (int i = 0; i < 512; ++i) {
            SimplexNoise.perm[i] = p[i & 0xFF];
            SimplexNoise.permMod12[i] = (byte)((perm[i] & 0xFF) % 12);
        }
    }

    private static class Vector4b {
        byte x;
        byte y;
        byte z;
        byte w;

        Vector4b(int x, int y, int z, int w) {
            this.x = (byte)x;
            this.y = (byte)y;
            this.z = (byte)z;
            this.w = (byte)w;
        }
    }

    private static class Vector3b {
        byte x;
        byte y;
        byte z;

        Vector3b(int x, int y, int z) {
            this.x = (byte)x;
            this.y = (byte)y;
            this.z = (byte)z;
        }
    }
}

