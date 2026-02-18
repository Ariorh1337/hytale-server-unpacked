/*
 * Decompiled with CFR 0.152.
 */
package org.joml;

import org.joml.Math;
import org.joml.Matrix3d;
import org.joml.Quaterniond;

public class QuaterniondInterpolator {
    private final SvdDecomposition3d svdDecomposition3d = new SvdDecomposition3d();
    private final double[] m = new double[9];
    private final Matrix3d u = new Matrix3d();
    private final Matrix3d v = new Matrix3d();

    public Quaterniond computeWeightedAverage(Quaterniond[] qs, double[] weights, int maxSvdIterations, Quaterniond dest) {
        double m00 = 0.0;
        double m01 = 0.0;
        double m02 = 0.0;
        double m10 = 0.0;
        double m11 = 0.0;
        double m12 = 0.0;
        double m20 = 0.0;
        double m21 = 0.0;
        double m22 = 0.0;
        for (int i = 0; i < qs.length; ++i) {
            Quaterniond q = qs[i];
            double dx = q.x + q.x;
            double dy = q.y + q.y;
            double dz = q.z + q.z;
            double q00 = dx * q.x;
            double q11 = dy * q.y;
            double q22 = dz * q.z;
            double q01 = dx * q.y;
            double q02 = dx * q.z;
            double q03 = dx * q.w;
            double q12 = dy * q.z;
            double q13 = dy * q.w;
            double q23 = dz * q.w;
            m00 += weights[i] * (1.0 - q11 - q22);
            m01 += weights[i] * (q01 + q23);
            m02 += weights[i] * (q02 - q13);
            m10 += weights[i] * (q01 - q23);
            m11 += weights[i] * (1.0 - q22 - q00);
            m12 += weights[i] * (q12 + q03);
            m20 += weights[i] * (q02 + q13);
            m21 += weights[i] * (q12 - q03);
            m22 += weights[i] * (1.0 - q11 - q00);
        }
        this.m[0] = m00;
        this.m[1] = m01;
        this.m[2] = m02;
        this.m[3] = m10;
        this.m[4] = m11;
        this.m[5] = m12;
        this.m[6] = m20;
        this.m[7] = m21;
        this.m[8] = m22;
        this.svdDecomposition3d.svd(this.m, maxSvdIterations, this.u, this.v);
        this.u.mul(this.v.transpose());
        return dest.setFromNormalized(this.u).normalize();
    }

    private static class SvdDecomposition3d {
        private final double[] rv1 = new double[3];
        private final double[] w = new double[3];
        private final double[] v = new double[9];

        SvdDecomposition3d() {
        }

        private double SIGN(double a, double b) {
            return b >= 0.0 ? Math.abs(a) : -Math.abs(a);
        }

        void svd(double[] a, int maxIterations, Matrix3d destU, Matrix3d destV) {
            int j;
            double h;
            double f;
            int k;
            double s;
            int i;
            int l = 0;
            int nm = 0;
            double anorm = 0.0;
            double g = 0.0;
            double scale = 0.0;
            for (i = 0; i < 3; ++i) {
                l = i + 1;
                this.rv1[i] = scale * g;
                scale = 0.0;
                s = 0.0;
                g = 0.0;
                for (k = i; k < 3; ++k) {
                    scale += Math.abs(a[k + 3 * i]);
                }
                if (scale != 0.0) {
                    for (k = i; k < 3; ++k) {
                        a[k + 3 * i] = a[k + 3 * i] / scale;
                        s += a[k + 3 * i] * a[k + 3 * i];
                    }
                    f = a[i + 3 * i];
                    g = -this.SIGN(Math.sqrt(s), f);
                    h = f * g - s;
                    a[i + 3 * i] = f - g;
                    if (i != 2) {
                        for (j = l; j < 3; ++j) {
                            s = 0.0;
                            for (k = i; k < 3; ++k) {
                                s += a[k + 3 * i] * a[k + 3 * j];
                            }
                            f = s / h;
                            for (k = i; k < 3; ++k) {
                                int n = k + 3 * j;
                                a[n] = a[n] + f * a[k + 3 * i];
                            }
                        }
                    }
                    for (k = i; k < 3; ++k) {
                        a[k + 3 * i] = a[k + 3 * i] * scale;
                    }
                }
                this.w[i] = scale * g;
                scale = 0.0;
                s = 0.0;
                g = 0.0;
                if (i < 3 && i != 2) {
                    for (k = l; k < 3; ++k) {
                        scale += Math.abs(a[i + 3 * k]);
                    }
                    if (scale != 0.0) {
                        for (k = l; k < 3; ++k) {
                            a[i + 3 * k] = a[i + 3 * k] / scale;
                            s += a[i + 3 * k] * a[i + 3 * k];
                        }
                        f = a[i + 3 * l];
                        g = -this.SIGN(Math.sqrt(s), f);
                        h = f * g - s;
                        a[i + 3 * l] = f - g;
                        for (k = l; k < 3; ++k) {
                            this.rv1[k] = a[i + 3 * k] / h;
                        }
                        if (i != 2) {
                            for (j = l; j < 3; ++j) {
                                s = 0.0;
                                for (k = l; k < 3; ++k) {
                                    s += a[j + 3 * k] * a[i + 3 * k];
                                }
                                for (k = l; k < 3; ++k) {
                                    int n = j + 3 * k;
                                    a[n] = a[n] + s * this.rv1[k];
                                }
                            }
                        }
                        for (k = l; k < 3; ++k) {
                            a[i + 3 * k] = a[i + 3 * k] * scale;
                        }
                    }
                }
                anorm = Math.max(anorm, Math.abs(this.w[i]) + Math.abs(this.rv1[i]));
            }
            i = 2;
            while (i >= 0) {
                if (i < 2) {
                    if (g != 0.0) {
                        for (j = l; j < 3; ++j) {
                            this.v[j + 3 * i] = a[i + 3 * j] / a[i + 3 * l] / g;
                        }
                        for (j = l; j < 3; ++j) {
                            s = 0.0;
                            for (k = l; k < 3; ++k) {
                                s += a[i + 3 * k] * this.v[k + 3 * j];
                            }
                            for (k = l; k < 3; ++k) {
                                int n = k + 3 * j;
                                this.v[n] = this.v[n] + s * this.v[k + 3 * i];
                            }
                        }
                    }
                    for (j = l; j < 3; ++j) {
                        this.v[j + 3 * i] = 0.0;
                        this.v[i + 3 * j] = 0.0;
                    }
                }
                this.v[i + 3 * i] = 1.0;
                g = this.rv1[i];
                l = i--;
            }
            for (i = 2; i >= 0; --i) {
                l = i + 1;
                g = this.w[i];
                if (i < 2) {
                    for (j = l; j < 3; ++j) {
                        a[i + 3 * j] = 0.0;
                    }
                }
                if (g != 0.0) {
                    g = 1.0 / g;
                    if (i != 2) {
                        for (j = l; j < 3; ++j) {
                            s = 0.0;
                            for (k = l; k < 3; ++k) {
                                s += a[k + 3 * i] * a[k + 3 * j];
                            }
                            f = s / a[i + 3 * i] * g;
                            for (k = i; k < 3; ++k) {
                                int n = k + 3 * j;
                                a[n] = a[n] + f * a[k + 3 * i];
                            }
                        }
                    }
                    for (j = i; j < 3; ++j) {
                        a[j + 3 * i] = a[j + 3 * i] * g;
                    }
                } else {
                    for (j = i; j < 3; ++j) {
                        a[j + 3 * i] = 0.0;
                    }
                }
                int n = i + 3 * i;
                a[n] = a[n] + 1.0;
            }
            block27: for (k = 2; k >= 0; --k) {
                for (int its = 0; its < maxIterations; ++its) {
                    double z;
                    double y;
                    double c;
                    boolean flag = true;
                    for (l = k; l >= 0; --l) {
                        nm = l - 1;
                        if (Math.abs(this.rv1[l]) + anorm == anorm) {
                            flag = false;
                            break;
                        }
                        if (Math.abs(this.w[nm]) + anorm == anorm) break;
                    }
                    if (flag) {
                        c = 0.0;
                        s = 1.0;
                        for (i = l; i <= k; ++i) {
                            f = s * this.rv1[i];
                            if (Math.abs(f) + anorm == anorm) continue;
                            g = this.w[i];
                            this.w[i] = h = SvdDecomposition3d.PYTHAG(f, g);
                            h = 1.0 / h;
                            c = g * h;
                            s = -f * h;
                            for (j = 0; j < 3; ++j) {
                                y = a[j + 3 * nm];
                                z = a[j + 3 * i];
                                a[j + 3 * nm] = y * c + z * s;
                                a[j + 3 * i] = z * c - y * s;
                            }
                        }
                    }
                    z = this.w[k];
                    if (l == k) {
                        if (!(z < 0.0)) continue block27;
                        this.w[k] = -z;
                        for (j = 0; j < 3; ++j) {
                            this.v[j + 3 * k] = -this.v[j + 3 * k];
                        }
                        continue block27;
                    }
                    if (its == maxIterations - 1) {
                        throw new RuntimeException("No convergence after " + maxIterations + " iterations");
                    }
                    double x = this.w[l];
                    nm = k - 1;
                    y = this.w[nm];
                    g = this.rv1[nm];
                    h = this.rv1[k];
                    f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2.0 * h * y);
                    g = SvdDecomposition3d.PYTHAG(f, 1.0);
                    f = ((x - z) * (x + z) + h * (y / (f + this.SIGN(g, f)) - h)) / x;
                    s = 1.0;
                    c = 1.0;
                    for (j = l; j <= nm; ++j) {
                        int jj;
                        i = j + 1;
                        g = this.rv1[i];
                        y = this.w[i];
                        h = s * g;
                        g = c * g;
                        this.rv1[j] = z = SvdDecomposition3d.PYTHAG(f, h);
                        c = f / z;
                        s = h / z;
                        f = x * c + g * s;
                        g = g * c - x * s;
                        h = y * s;
                        y *= c;
                        for (jj = 0; jj < 3; ++jj) {
                            x = this.v[jj + 3 * j];
                            z = this.v[jj + 3 * i];
                            this.v[jj + 3 * j] = x * c + z * s;
                            this.v[jj + 3 * i] = z * c - x * s;
                        }
                        this.w[j] = z = SvdDecomposition3d.PYTHAG(f, h);
                        if (z != 0.0) {
                            z = 1.0 / z;
                            c = f * z;
                            s = h * z;
                        }
                        f = c * g + s * y;
                        x = c * y - s * g;
                        for (jj = 0; jj < 3; ++jj) {
                            y = a[jj + 3 * j];
                            z = a[jj + 3 * i];
                            a[jj + 3 * j] = y * c + z * s;
                            a[jj + 3 * i] = z * c - y * s;
                        }
                    }
                    this.rv1[l] = 0.0;
                    this.rv1[k] = f;
                    this.w[k] = x;
                }
            }
            destU.set(a);
            destV.set(this.v);
        }

        private static double PYTHAG(double a, double b) {
            double result;
            double bt;
            double at = Math.abs(a);
            if (at > (bt = Math.abs(b))) {
                double ct = bt / at;
                result = at * Math.sqrt(1.0 + ct * ct);
            } else if (bt > 0.0) {
                double ct = at / bt;
                result = bt * Math.sqrt(1.0 + ct * ct);
            } else {
                result = 0.0;
            }
            return result;
        }
    }
}

