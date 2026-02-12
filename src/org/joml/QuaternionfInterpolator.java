package org.joml;

public class QuaternionfInterpolator {
   private final QuaternionfInterpolator.SvdDecomposition3f svdDecomposition3f = new QuaternionfInterpolator.SvdDecomposition3f();
   private final float[] m = new float[9];
   private final Matrix3f u = new Matrix3f();
   private final Matrix3f v = new Matrix3f();

   public Quaternionf computeWeightedAverage(Quaternionfc[] qs, float[] weights, int maxSvdIterations, Quaternionf dest) {
      float m00 = 0.0F;
      float m01 = 0.0F;
      float m02 = 0.0F;
      float m10 = 0.0F;
      float m11 = 0.0F;
      float m12 = 0.0F;
      float m20 = 0.0F;
      float m21 = 0.0F;
      float m22 = 0.0F;

      for (int i = 0; i < qs.length; i++) {
         Quaternionfc q = qs[i];
         float dx = q.x() + q.x();
         float dy = q.y() + q.y();
         float dz = q.z() + q.z();
         float q00 = dx * q.x();
         float q11 = dy * q.y();
         float q22 = dz * q.z();
         float q01 = dx * q.y();
         float q02 = dx * q.z();
         float q03 = dx * q.w();
         float q12 = dy * q.z();
         float q13 = dy * q.w();
         float q23 = dz * q.w();
         m00 += weights[i] * (1.0F - q11 - q22);
         m01 += weights[i] * (q01 + q23);
         m02 += weights[i] * (q02 - q13);
         m10 += weights[i] * (q01 - q23);
         m11 += weights[i] * (1.0F - q22 - q00);
         m12 += weights[i] * (q12 + q03);
         m20 += weights[i] * (q02 + q13);
         m21 += weights[i] * (q12 - q03);
         m22 += weights[i] * (1.0F - q11 - q00);
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
      this.svdDecomposition3f.svd(this.m, maxSvdIterations, this.u, this.v);
      this.u.mul(this.v.transpose());
      return dest.setFromNormalized(this.u).normalize();
   }

   private static class SvdDecomposition3f {
      private final float[] rv1 = new float[3];
      private final float[] w = new float[3];
      private final float[] v = new float[9];

      SvdDecomposition3f() {
      }

      private float SIGN(float a, float b) {
         return b >= 0.0 ? Math.abs(a) : -Math.abs(a);
      }

      void svd(float[] a, int maxIterations, Matrix3f destU, Matrix3f destV) {
         int l = 0;
         int nm = 0;
         float anorm = 0.0F;
         float g = 0.0F;
         float scale = 0.0F;

         for (int i = 0; i < 3; i++) {
            l = i + 1;
            this.rv1[i] = scale * g;
            scale = 0.0F;
            float s = 0.0F;
            g = 0.0F;

            for (int k = i; k < 3; k++) {
               scale += Math.abs(a[k + 3 * i]);
            }

            if (scale != 0.0F) {
               for (int var39 = i; var39 < 3; var39++) {
                  a[var39 + 3 * i] = a[var39 + 3 * i] / scale;
                  s += a[var39 + 3 * i] * a[var39 + 3 * i];
               }

               float f = a[i + 3 * i];
               g = -this.SIGN(Math.sqrt(s), f);
               float h = f * g - s;
               a[i + 3 * i] = f - g;
               if (i != 2) {
                  for (int j = l; j < 3; j++) {
                     s = 0.0F;

                     for (int var40 = i; var40 < 3; var40++) {
                        s += a[var40 + 3 * i] * a[var40 + 3 * j];
                     }

                     f = s / h;

                     for (int var41 = i; var41 < 3; var41++) {
                        a[var41 + 3 * j] = a[var41 + 3 * j] + f * a[var41 + 3 * i];
                     }
                  }
               }

               for (int var42 = i; var42 < 3; var42++) {
                  a[var42 + 3 * i] = a[var42 + 3 * i] * scale;
               }
            }

            this.w[i] = scale * g;
            scale = 0.0F;
            s = 0.0F;
            g = 0.0F;
            if (i < 3 && i != 2) {
               for (int var43 = l; var43 < 3; var43++) {
                  scale += Math.abs(a[i + 3 * var43]);
               }

               if (scale != 0.0F) {
                  for (int var44 = l; var44 < 3; var44++) {
                     a[i + 3 * var44] = a[i + 3 * var44] / scale;
                     s += a[i + 3 * var44] * a[i + 3 * var44];
                  }

                  float f = a[i + 3 * l];
                  g = -this.SIGN(Math.sqrt(s), f);
                  float h = f * g - s;
                  a[i + 3 * l] = f - g;

                  for (int var45 = l; var45 < 3; var45++) {
                     this.rv1[var45] = a[i + 3 * var45] / h;
                  }

                  if (i != 2) {
                     for (int j = l; j < 3; j++) {
                        s = 0.0F;

                        for (int var46 = l; var46 < 3; var46++) {
                           s += a[j + 3 * var46] * a[i + 3 * var46];
                        }

                        for (int var47 = l; var47 < 3; var47++) {
                           a[j + 3 * var47] = a[j + 3 * var47] + s * this.rv1[var47];
                        }
                     }
                  }

                  for (int var48 = l; var48 < 3; var48++) {
                     a[i + 3 * var48] = a[i + 3 * var48] * scale;
                  }
               }
            }

            anorm = Math.max(anorm, Math.abs(this.w[i]) + Math.abs(this.rv1[i]));
         }

         for (int var23 = 2; var23 >= 0; l = var23--) {
            if (var23 < 2) {
               if (g != 0.0F) {
                  for (int j = l; j < 3; j++) {
                     this.v[j + 3 * var23] = a[var23 + 3 * j] / a[var23 + 3 * l] / g;
                  }

                  for (int var29 = l; var29 < 3; var29++) {
                     float s = 0.0F;

                     for (int k = l; k < 3; k++) {
                        s += a[var23 + 3 * k] * this.v[k + 3 * var29];
                     }

                     for (int var50 = l; var50 < 3; var50++) {
                        this.v[var50 + 3 * var29] = this.v[var50 + 3 * var29] + s * this.v[var50 + 3 * var23];
                     }
                  }
               }

               for (int j = l; j < 3; j++) {
                  this.v[var23 + 3 * j] = this.v[j + 3 * var23] = 0.0F;
               }
            }

            this.v[var23 + 3 * var23] = 1.0F;
            g = this.rv1[var23];
         }

         for (int var24 = 2; var24 >= 0; var24--) {
            l = var24 + 1;
            g = this.w[var24];
            if (var24 < 2) {
               for (int j = l; j < 3; j++) {
                  a[var24 + 3 * j] = 0.0F;
               }
            }

            if (g == 0.0F) {
               for (int j = var24; j < 3; j++) {
                  a[j + 3 * var24] = 0.0F;
               }
            } else {
               g = 1.0F / g;
               if (var24 != 2) {
                  for (int j = l; j < 3; j++) {
                     float s = 0.0F;

                     for (int k = l; k < 3; k++) {
                        s += a[k + 3 * var24] * a[k + 3 * j];
                     }

                     float f = s / a[var24 + 3 * var24] * g;

                     for (int var52 = var24; var52 < 3; var52++) {
                        a[var52 + 3 * j] = a[var52 + 3 * j] + f * a[var52 + 3 * var24];
                     }
                  }
               }

               for (int j = var24; j < 3; j++) {
                  a[j + 3 * var24] = a[j + 3 * var24] * g;
               }
            }

            a[var24 + 3 * var24]++;
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
                  float c = 0.0F;
                  float s = 1.0F;

                  for (int var25 = l; var25 <= k; var25++) {
                     float f = s * this.rv1[var25];
                     if (Math.abs(f) + anorm != anorm) {
                        g = this.w[var25];
                        float h = PYTHAG(f, g);
                        this.w[var25] = h;
                        h = 1.0F / h;
                        c = g * h;
                        s = -f * h;

                        for (int j = 0; j < 3; j++) {
                           float y = a[j + 3 * nm];
                           float z = a[j + 3 * var25];
                           a[j + 3 * nm] = y * c + z * s;
                           a[j + 3 * var25] = z * c - y * s;
                        }
                     }
                  }
               }

               float z = this.w[k];
               if (l == k) {
                  if (!(z < 0.0F)) {
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

               float x = this.w[l];
               nm = k - 1;
               float y = this.w[nm];
               g = this.rv1[nm];
               float h = this.rv1[k];
               float f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2.0F * h * y);
               g = PYTHAG(f, 1.0F);
               f = ((x - z) * (x + z) + h * (y / (f + this.SIGN(g, f)) - h)) / x;
               float s = 1.0F;
               float c = 1.0F;

               for (int j = l; j <= nm; j++) {
                  int var26 = j + 1;
                  g = this.rv1[var26];
                  y = this.w[var26];
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
                     z = this.v[jj + 3 * var26];
                     this.v[jj + 3 * j] = x * c + z * s;
                     this.v[jj + 3 * var26] = z * c - x * s;
                  }

                  z = PYTHAG(f, h);
                  this.w[j] = z;
                  if (z != 0.0F) {
                     z = 1.0F / z;
                     c = f * z;
                     s = h * z;
                  }

                  f = c * g + s * y;
                  x = c * y - s * g;

                  for (int var38 = 0; var38 < 3; var38++) {
                     y = a[var38 + 3 * j];
                     z = a[var38 + 3 * var26];
                     a[var38 + 3 * j] = y * c + z * s;
                     a[var38 + 3 * var26] = z * c - y * s;
                  }
               }

               this.rv1[l] = 0.0F;
               this.rv1[k] = f;
               this.w[k] = x;
            }
         }

         destU.set(a);
         destV.set(this.v);
      }

      private static float PYTHAG(float a, float b) {
         float at = Math.abs(a);
         float bt = Math.abs(b);
         float result;
         if (at > bt) {
            float ct = bt / at;
            result = at * (float)Math.sqrt(1.0 + ct * ct);
         } else if (bt > 0.0F) {
            float ct = at / bt;
            result = bt * (float)Math.sqrt(1.0 + ct * ct);
         } else {
            result = 0.0F;
         }

         return result;
      }
   }
}
