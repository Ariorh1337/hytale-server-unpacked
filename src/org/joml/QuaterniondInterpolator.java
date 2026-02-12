package org.joml;

public class QuaterniondInterpolator {
   private final QuaterniondInterpolator.SvdDecomposition3d svdDecomposition3d = new QuaterniondInterpolator.SvdDecomposition3d();
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

      for (int i = 0; i < qs.length; i++) {
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
         int l = 0;
         int nm = 0;
         double anorm = 0.0;
         double g = 0.0;
         double scale = 0.0;

         for (int i = 0; i < 3; i++) {
            l = i + 1;
            this.rv1[i] = scale * g;
            scale = 0.0;
            double s = 0.0;
            g = 0.0;

            for (int k = i; k < 3; k++) {
               scale += Math.abs(a[k + 3 * i]);
            }

            if (scale != 0.0) {
               for (int var49 = i; var49 < 3; var49++) {
                  a[var49 + 3 * i] = a[var49 + 3 * i] / scale;
                  s += a[var49 + 3 * i] * a[var49 + 3 * i];
               }

               double f = a[i + 3 * i];
               g = -this.SIGN(Math.sqrt(s), f);
               double h = f * g - s;
               a[i + 3 * i] = f - g;
               if (i != 2) {
                  for (int j = l; j < 3; j++) {
                     s = 0.0;

                     for (int var50 = i; var50 < 3; var50++) {
                        s += a[var50 + 3 * i] * a[var50 + 3 * j];
                     }

                     f = s / h;

                     for (int var51 = i; var51 < 3; var51++) {
                        a[var51 + 3 * j] = a[var51 + 3 * j] + f * a[var51 + 3 * i];
                     }
                  }
               }

               for (int var52 = i; var52 < 3; var52++) {
                  a[var52 + 3 * i] = a[var52 + 3 * i] * scale;
               }
            }

            this.w[i] = scale * g;
            scale = 0.0;
            s = 0.0;
            g = 0.0;
            if (i < 3 && i != 2) {
               for (int var53 = l; var53 < 3; var53++) {
                  scale += Math.abs(a[i + 3 * var53]);
               }

               if (scale != 0.0) {
                  for (int var54 = l; var54 < 3; var54++) {
                     a[i + 3 * var54] = a[i + 3 * var54] / scale;
                     s += a[i + 3 * var54] * a[i + 3 * var54];
                  }

                  double f = a[i + 3 * l];
                  g = -this.SIGN(Math.sqrt(s), f);
                  double h = f * g - s;
                  a[i + 3 * l] = f - g;

                  for (int var55 = l; var55 < 3; var55++) {
                     this.rv1[var55] = a[i + 3 * var55] / h;
                  }

                  if (i != 2) {
                     for (int j = l; j < 3; j++) {
                        s = 0.0;

                        for (int var56 = l; var56 < 3; var56++) {
                           s += a[j + 3 * var56] * a[i + 3 * var56];
                        }

                        for (int var57 = l; var57 < 3; var57++) {
                           a[j + 3 * var57] = a[j + 3 * var57] + s * this.rv1[var57];
                        }
                     }
                  }

                  for (int var58 = l; var58 < 3; var58++) {
                     a[i + 3 * var58] = a[i + 3 * var58] * scale;
                  }
               }
            }

            anorm = Math.max(anorm, Math.abs(this.w[i]) + Math.abs(this.rv1[i]));
         }

         for (int var33 = 2; var33 >= 0; l = var33--) {
            if (var33 < 2) {
               if (g != 0.0) {
                  for (int j = l; j < 3; j++) {
                     this.v[j + 3 * var33] = a[var33 + 3 * j] / a[var33 + 3 * l] / g;
                  }

                  for (int var39 = l; var39 < 3; var39++) {
                     double s = 0.0;

                     for (int k = l; k < 3; k++) {
                        s += a[var33 + 3 * k] * this.v[k + 3 * var39];
                     }

                     for (int var60 = l; var60 < 3; var60++) {
                        this.v[var60 + 3 * var39] = this.v[var60 + 3 * var39] + s * this.v[var60 + 3 * var33];
                     }
                  }
               }

               for (int j = l; j < 3; j++) {
                  this.v[var33 + 3 * j] = this.v[j + 3 * var33] = 0.0;
               }
            }

            this.v[var33 + 3 * var33] = 1.0;
            g = this.rv1[var33];
         }

         for (int var34 = 2; var34 >= 0; var34--) {
            l = var34 + 1;
            g = this.w[var34];
            if (var34 < 2) {
               for (int j = l; j < 3; j++) {
                  a[var34 + 3 * j] = 0.0;
               }
            }

            if (g == 0.0) {
               for (int j = var34; j < 3; j++) {
                  a[j + 3 * var34] = 0.0;
               }
            } else {
               g = 1.0 / g;
               if (var34 != 2) {
                  for (int j = l; j < 3; j++) {
                     double s = 0.0;

                     for (int k = l; k < 3; k++) {
                        s += a[k + 3 * var34] * a[k + 3 * j];
                     }

                     double f = s / a[var34 + 3 * var34] * g;

                     for (int var62 = var34; var62 < 3; var62++) {
                        a[var62 + 3 * j] = a[var62 + 3 * j] + f * a[var62 + 3 * var34];
                     }
                  }
               }

               for (int j = var34; j < 3; j++) {
                  a[j + 3 * var34] = a[j + 3 * var34] * g;
               }
            }

            a[var34 + 3 * var34]++;
         }

         for (int k = 2; k >= 0; k--) {
            for (int its = 0; its < maxIterations; its++) {
               int flag = 1;

               for (l = k; l >= 0; l--) {
                  nm = l - 1;
                  if (Math.abs(this.rv1[l]) + anorm == anorm) {
                     flag = 0;
                     break;
                  }

                  if (Math.abs(this.w[nm]) + anorm == anorm) {
                     break;
                  }
               }

               if (flag != 0) {
                  double c = 0.0;
                  double s = 1.0;

                  for (int var35 = l; var35 <= k; var35++) {
                     double f = s * this.rv1[var35];
                     if (Math.abs(f) + anorm != anorm) {
                        g = this.w[var35];
                        double h = PYTHAG(f, g);
                        this.w[var35] = h;
                        h = 1.0 / h;
                        c = g * h;
                        s = -f * h;

                        for (int j = 0; j < 3; j++) {
                           double y = a[j + 3 * nm];
                           double z = a[j + 3 * var35];
                           a[j + 3 * nm] = y * c + z * s;
                           a[j + 3 * var35] = z * c - y * s;
                        }
                     }
                  }
               }

               double z = this.w[k];
               if (l == k) {
                  if (!(z < 0.0)) {
                     break;
                  }

                  this.w[k] = -z;

                  for (int j = 0; j < 3; j++) {
                     this.v[j + 3 * k] = -this.v[j + 3 * k];
                  }
                  break;
               }

               if (its == maxIterations - 1) {
                  throw new RuntimeException("No convergence after " + maxIterations + " iterations");
               }

               double x = this.w[l];
               nm = k - 1;
               double y = this.w[nm];
               g = this.rv1[nm];
               double h = this.rv1[k];
               double f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2.0 * h * y);
               g = PYTHAG(f, 1.0);
               f = ((x - z) * (x + z) + h * (y / (f + this.SIGN(g, f)) - h)) / x;
               double s = 1.0;
               double c = 1.0;

               for (int j = l; j <= nm; j++) {
                  int var36 = j + 1;
                  g = this.rv1[var36];
                  y = this.w[var36];
                  h = s * g;
                  g = c * g;
                  z = PYTHAG(f, h);
                  this.rv1[j] = z;
                  c = f / z;
                  s = h / z;
                  f = x * c + g * s;
                  g = g * c - x * s;
                  h = y * s;
                  y *= c;

                  for (int jj = 0; jj < 3; jj++) {
                     x = this.v[jj + 3 * j];
                     z = this.v[jj + 3 * var36];
                     this.v[jj + 3 * j] = x * c + z * s;
                     this.v[jj + 3 * var36] = z * c - x * s;
                  }

                  z = PYTHAG(f, h);
                  this.w[j] = z;
                  if (z != 0.0) {
                     z = 1.0 / z;
                     c = f * z;
                     s = h * z;
                  }

                  f = c * g + s * y;
                  x = c * y - s * g;

                  for (int var48 = 0; var48 < 3; var48++) {
                     y = a[var48 + 3 * j];
                     z = a[var48 + 3 * var36];
                     a[var48 + 3 * j] = y * c + z * s;
                     a[var48 + 3 * var36] = z * c - y * s;
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
         double at = Math.abs(a);
         double bt = Math.abs(b);
         double result;
         if (at > bt) {
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
