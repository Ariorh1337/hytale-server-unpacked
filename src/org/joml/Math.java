package org.joml;

public class Math {
   public static final double PI = java.lang.Math.PI;
   public static final double PI_TIMES_2 = java.lang.Math.PI * 2;
   public static final float PI_f = (float) java.lang.Math.PI;
   public static final float PI_TIMES_2_f = (float) (java.lang.Math.PI * 2);
   public static final double PI_OVER_2 = java.lang.Math.PI / 2;
   public static final float PI_OVER_2_f = (float) (java.lang.Math.PI / 2);
   public static final double PI_OVER_4 = java.lang.Math.PI / 4;
   public static final float PI_OVER_4_f = (float) (java.lang.Math.PI / 4);
   public static final double ONE_OVER_PI = 0.3183098861837907;
   public static final float ONE_OVER_PI_f = 0.31830987F;
   private static final int lookupBits = Options.SIN_LOOKUP_BITS;
   private static final int lookupTableSize = 1 << lookupBits;
   private static final int lookupTableSizeMinus1 = lookupTableSize - 1;
   private static final int lookupTableSizeWithMargin = lookupTableSize + 1;
   private static final float pi2OverLookupSize = (float) (java.lang.Math.PI * 2) / lookupTableSize;
   private static final float lookupSizeOverPi2 = lookupTableSize / (float) (java.lang.Math.PI * 2);
   private static final float[] sinTable;
   private static final double c1;
   private static final double c2;
   private static final double c3;
   private static final double c4;
   private static final double c5;
   private static final double c6;
   private static final double c7;
   private static final double s5;
   private static final double s4;
   private static final double s3;
   private static final double s2;
   private static final double s1;
   private static final double k1;
   private static final double k2;
   private static final double k3;
   private static final double k4;
   private static final double k5;
   private static final double k6;
   private static final double k7;

   static double sin_theagentd_arith(double x) {
      double xi = floor((x + (java.lang.Math.PI / 4)) * 0.3183098861837907);
      double x_ = x - xi * java.lang.Math.PI;
      double sign = ((int)xi & 1) * -2 + 1;
      double x2 = x_ * x_;
      double sin = x_;
      double tx = x_ * x2;
      sin += tx * c1;
      tx *= x2;
      sin += tx * c2;
      tx *= x2;
      sin += tx * c3;
      tx *= x2;
      sin += tx * c4;
      tx *= x2;
      sin += tx * c5;
      tx *= x2;
      sin += tx * c6;
      tx *= x2;
      sin += tx * c7;
      return sign * sin;
   }

   static double sin_roquen_arith(double x) {
      double xi = floor((x + (java.lang.Math.PI / 4)) * 0.3183098861837907);
      double x_ = x - xi * java.lang.Math.PI;
      double sign = ((int)xi & 1) * -2 + 1;
      double x2 = x_ * x_;
      x_ = sign * x_;
      double sin = c7;
      sin = sin * x2 + c6;
      sin = sin * x2 + c5;
      sin = sin * x2 + c4;
      sin = sin * x2 + c3;
      sin = sin * x2 + c2;
      sin = sin * x2 + c1;
      return x_ + x_ * x2 * sin;
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

   static double sin_roquen_newk(double v) {
      double i = java.lang.Math.rint(v * 0.3183098861837907);
      double x = v - i * java.lang.Math.PI;
      double qs = 1 - 2 * ((int)i & 1);
      double x2 = x * x;
      x = qs * x;
      double r = k7;
      r = r * x2 + k6;
      r = r * x2 + k5;
      r = r * x2 + k4;
      r = r * x2 + k3;
      r = r * x2 + k2;
      r = r * x2 + k1;
      return x + x * x2 * r;
   }

   static float sin_theagentd_lookup(float rad) {
      float index = rad * lookupSizeOverPi2;
      int ii = (int)java.lang.Math.floor(index);
      float alpha = index - ii;
      int i = ii & lookupTableSizeMinus1;
      float sin1 = sinTable[i];
      float sin2 = sinTable[i + 1];
      return sin1 + (sin2 - sin1) * alpha;
   }

   public static float sin(float rad) {
      if (Options.FASTMATH) {
         return Options.SIN_LOOKUP ? sin_theagentd_lookup(rad) : (float)sin_roquen_newk(rad);
      } else {
         return (float)java.lang.Math.sin(rad);
      }
   }

   public static double sin(double rad) {
      if (Options.FASTMATH) {
         return Options.SIN_LOOKUP ? sin_theagentd_lookup((float)rad) : sin_roquen_newk(rad);
      } else {
         return java.lang.Math.sin(rad);
      }
   }

   public static float cos(float rad) {
      return Options.FASTMATH ? sin(rad + (float) (java.lang.Math.PI / 2)) : (float)java.lang.Math.cos(rad);
   }

   public static double cos(double rad) {
      return Options.FASTMATH ? sin(rad + (java.lang.Math.PI / 2)) : java.lang.Math.cos(rad);
   }

   public static float cosFromSin(float sin, float angle) {
      return Options.FASTMATH ? sin(angle + (float) (java.lang.Math.PI / 2)) : cosFromSinInternal(sin, angle);
   }

   private static float cosFromSinInternal(float sin, float angle) {
      float cos = sqrt(1.0F - sin * sin);
      float a = angle + (float) (java.lang.Math.PI / 2);
      float b = a - (int)(a / (float) (java.lang.Math.PI * 2)) * (float) (java.lang.Math.PI * 2);
      if (b < 0.0) {
         b += (float) (java.lang.Math.PI * 2);
      }

      return b >= (float) java.lang.Math.PI ? -cos : cos;
   }

   public static double cosFromSin(double sin, double angle) {
      if (Options.FASTMATH) {
         return sin(angle + (java.lang.Math.PI / 2));
      }

      double cos = sqrt(1.0 - sin * sin);
      double a = angle + (java.lang.Math.PI / 2);
      double b = a - (int)(a / (java.lang.Math.PI * 2)) * (java.lang.Math.PI * 2);
      if (b < 0.0) {
         b += java.lang.Math.PI * 2;
      }

      return b >= java.lang.Math.PI ? -cos : cos;
   }

   public static float sqrt(float r) {
      return (float)java.lang.Math.sqrt(r);
   }

   public static double sqrt(double r) {
      return java.lang.Math.sqrt(r);
   }

   public static float invsqrt(float r) {
      return 1.0F / (float)java.lang.Math.sqrt(r);
   }

   public static double invsqrt(double r) {
      return 1.0 / java.lang.Math.sqrt(r);
   }

   public static float tan(float r) {
      return (float)java.lang.Math.tan(r);
   }

   public static double tan(double r) {
      return java.lang.Math.tan(r);
   }

   public static float acos(float r) {
      return (float)java.lang.Math.acos(r);
   }

   public static double acos(double r) {
      return java.lang.Math.acos(r);
   }

   public static float safeAcos(float v) {
      if (v < -1.0F) {
         return (float) java.lang.Math.PI;
      } else {
         return v > 1.0F ? 0.0F : acos(v);
      }
   }

   public static double safeAcos(double v) {
      if (v < -1.0) {
         return java.lang.Math.PI;
      } else {
         return v > 1.0 ? 0.0 : acos(v);
      }
   }

   private static double fastAtan2(double y, double x) {
      double ax = x >= 0.0 ? x : -x;
      double ay = y >= 0.0 ? y : -y;
      double a = ay > ax ? ax / ay : ay / ax;
      double s = a * a;
      double r = fma(fma(fma(-0.0464964749, s, 0.15931422), s, -0.327622764) * s, a, a);
      if (ay > ax) {
         r = (java.lang.Math.PI / 2) - r;
      }

      if (x < 0.0) {
         r = java.lang.Math.PI - r;
      }

      return y >= 0.0 ? r : -r;
   }

   public static float atan2(float y, float x) {
      return (float)java.lang.Math.atan2(y, x);
   }

   public static double atan2(double y, double x) {
      return Options.FASTMATH ? fastAtan2(y, x) : java.lang.Math.atan2(y, x);
   }

   public static float asin(float r) {
      return (float)java.lang.Math.asin(r);
   }

   public static double asin(double r) {
      return java.lang.Math.asin(r);
   }

   public static float safeAsin(float r) {
      return r <= -1.0F ? (float) (-java.lang.Math.PI / 2) : (r >= 1.0F ? (float) (java.lang.Math.PI / 2) : asin(r));
   }

   public static double safeAsin(double r) {
      return r <= -1.0 ? -java.lang.Math.PI / 2 : (r >= 1.0 ? java.lang.Math.PI / 2 : asin(r));
   }

   public static float abs(float r) {
      return java.lang.Math.abs(r);
   }

   public static double abs(double r) {
      return java.lang.Math.abs(r);
   }

   static boolean absEqualsOne(float r) {
      return (Float.floatToRawIntBits(r) & 2147483647) == 1065353216;
   }

   static boolean absEqualsOne(double r) {
      return (Double.doubleToRawLongBits(r) & Long.MAX_VALUE) == 4607182418800017408L;
   }

   public static int abs(int r) {
      return java.lang.Math.abs(r);
   }

   public static long abs(long r) {
      return java.lang.Math.abs(r);
   }

   public static int max(int x, int y) {
      return java.lang.Math.max(x, y);
   }

   public static int min(int x, int y) {
      return java.lang.Math.min(x, y);
   }

   public static long max(long x, long y) {
      return java.lang.Math.max(x, y);
   }

   public static long min(long x, long y) {
      return java.lang.Math.min(x, y);
   }

   public static double min(double a, double b) {
      return a < b ? a : b;
   }

   public static float min(float a, float b) {
      return a < b ? a : b;
   }

   public static float max(float a, float b) {
      return a > b ? a : b;
   }

   public static double max(double a, double b) {
      return a > b ? a : b;
   }

   public static float clamp(float a, float b, float val) {
      return max(a, min(b, val));
   }

   public static double clamp(double a, double b, double val) {
      return max(a, min(b, val));
   }

   public static int clamp(int a, int b, int val) {
      return max(a, min(b, val));
   }

   public static long clamp(long a, long b, long val) {
      return max(a, min(b, val));
   }

   public static float toRadians(float angles) {
      return (float)java.lang.Math.toRadians(angles);
   }

   public static double toRadians(double angles) {
      return java.lang.Math.toRadians(angles);
   }

   public static float toDegrees(float angles) {
      return (float)java.lang.Math.toDegrees(angles);
   }

   public static double toDegrees(double angles) {
      return java.lang.Math.toDegrees(angles);
   }

   public static double floor(double v) {
      return java.lang.Math.floor(v);
   }

   public static float floor(float v) {
      return (float)java.lang.Math.floor(v);
   }

   public static double ceil(double v) {
      return java.lang.Math.ceil(v);
   }

   public static float ceil(float v) {
      return (float)java.lang.Math.ceil(v);
   }

   public static long round(double v) {
      return java.lang.Math.round(v);
   }

   public static int round(float v) {
      return java.lang.Math.round(v);
   }

   public static double exp(double a) {
      return java.lang.Math.exp(a);
   }

   public static boolean isFinite(double d) {
      return abs(d) <= Double.MAX_VALUE;
   }

   public static boolean isFinite(float f) {
      return abs(f) <= Float.MAX_VALUE;
   }

   public static float fma(float a, float b, float c) {
      return Runtime.HAS_Math_fma ? java.lang.Math.fma(a, b, c) : a * b + c;
   }

   public static double fma(double a, double b, double c) {
      return Runtime.HAS_Math_fma ? java.lang.Math.fma(a, b, c) : a * b + c;
   }

   public static int roundUsing(float v, int mode) {
      switch (mode) {
         case 0:
            return (int)v;
         case 1:
            return (int)java.lang.Math.ceil(v);
         case 2:
            return (int)java.lang.Math.floor(v);
         case 3:
            return roundHalfEven(v);
         case 4:
            return roundHalfDown(v);
         case 5:
            return roundHalfUp(v);
         default:
            throw new UnsupportedOperationException();
      }
   }

   public static int roundUsing(double v, int mode) {
      switch (mode) {
         case 0:
            return (int)v;
         case 1:
            return (int)java.lang.Math.ceil(v);
         case 2:
            return (int)java.lang.Math.floor(v);
         case 3:
            return roundHalfEven(v);
         case 4:
            return roundHalfDown(v);
         case 5:
            return roundHalfUp(v);
         default:
            throw new UnsupportedOperationException();
      }
   }

   public static long roundLongUsing(double v, int mode) {
      switch (mode) {
         case 0:
            return (long)v;
         case 1:
            return (long)java.lang.Math.ceil(v);
         case 2:
            return (long)java.lang.Math.floor(v);
         case 3:
            return roundHalfEven(v);
         case 4:
            return roundHalfDown(v);
         case 5:
            return roundHalfUp(v);
         default:
            throw new UnsupportedOperationException();
      }
   }

   public static float lerp(float a, float b, float t) {
      return fma(b - a, t, a);
   }

   public static double lerp(double a, double b, double t) {
      return fma(b - a, t, a);
   }

   public static float biLerp(float q00, float q10, float q01, float q11, float tx, float ty) {
      float lerpX1 = lerp(q00, q10, tx);
      float lerpX2 = lerp(q01, q11, tx);
      return lerp(lerpX1, lerpX2, ty);
   }

   public static double biLerp(double q00, double q10, double q01, double q11, double tx, double ty) {
      double lerpX1 = lerp(q00, q10, tx);
      double lerpX2 = lerp(q01, q11, tx);
      return lerp(lerpX1, lerpX2, ty);
   }

   public static float triLerp(float q000, float q100, float q010, float q110, float q001, float q101, float q011, float q111, float tx, float ty, float tz) {
      float x00 = lerp(q000, q100, tx);
      float x10 = lerp(q010, q110, tx);
      float x01 = lerp(q001, q101, tx);
      float x11 = lerp(q011, q111, tx);
      float y0 = lerp(x00, x10, ty);
      float y1 = lerp(x01, x11, ty);
      return lerp(y0, y1, tz);
   }

   public static double triLerp(
      double q000, double q100, double q010, double q110, double q001, double q101, double q011, double q111, double tx, double ty, double tz
   ) {
      double x00 = lerp(q000, q100, tx);
      double x10 = lerp(q010, q110, tx);
      double x01 = lerp(q001, q101, tx);
      double x11 = lerp(q011, q111, tx);
      double y0 = lerp(x00, x10, ty);
      double y1 = lerp(x01, x11, ty);
      return lerp(y0, y1, tz);
   }

   public static int roundHalfEven(float v) {
      return (int)java.lang.Math.rint(v);
   }

   public static int roundHalfDown(float v) {
      return v > 0.0F ? (int)java.lang.Math.ceil(v - 0.5) : (int)java.lang.Math.floor(v + 0.5);
   }

   public static int roundHalfUp(float v) {
      return v > 0.0F ? (int)java.lang.Math.floor(v + 0.5) : (int)java.lang.Math.ceil(v - 0.5);
   }

   public static int roundHalfEven(double v) {
      return (int)java.lang.Math.rint(v);
   }

   public static int roundHalfDown(double v) {
      return v > 0.0 ? (int)java.lang.Math.ceil(v - 0.5) : (int)java.lang.Math.floor(v + 0.5);
   }

   public static int roundHalfUp(double v) {
      return v > 0.0 ? (int)java.lang.Math.floor(v + 0.5) : (int)java.lang.Math.ceil(v - 0.5);
   }

   public static long roundLongHalfEven(double v) {
      return (long)java.lang.Math.rint(v);
   }

   public static long roundLongHalfDown(double v) {
      return v > 0.0 ? (long)java.lang.Math.ceil(v - 0.5) : (long)java.lang.Math.floor(v + 0.5);
   }

   public static long roundLongHalfUp(double v) {
      return v > 0.0 ? (long)java.lang.Math.floor(v + 0.5) : (long)java.lang.Math.ceil(v - 0.5);
   }

   public static double random() {
      return java.lang.Math.random();
   }

   public static double signum(double v) {
      return java.lang.Math.signum(v);
   }

   public static float signum(float v) {
      return java.lang.Math.signum(v);
   }

   public static int signum(int v) {
      return v >> 31 | -v >>> 31;
   }

   public static int signum(long v) {
      return (int)(v >> 63 | -v >>> 63);
   }

   static {
      if (Options.FASTMATH && Options.SIN_LOOKUP) {
         sinTable = new float[lookupTableSizeWithMargin];

         for (int i = 0; i < lookupTableSizeWithMargin; i++) {
            double d = i * pi2OverLookupSize;
            sinTable[i] = (float)java.lang.Math.sin(d);
         }
      } else {
         sinTable = null;
      }

      c1 = Double.longBitsToDouble(-4628199217061079772L);
      c2 = Double.longBitsToDouble(4575957461383582011L);
      c3 = Double.longBitsToDouble(-4671919876300759001L);
      c4 = Double.longBitsToDouble(4523617214285661942L);
      c5 = Double.longBitsToDouble(-4730215272828025532L);
      c6 = Double.longBitsToDouble(4460272573143870633L);
      c7 = Double.longBitsToDouble(-4797767418267846529L);
      s5 = Double.longBitsToDouble(4523227044276562163L);
      s4 = Double.longBitsToDouble(-4671934770969572232L);
      s3 = Double.longBitsToDouble(4575957211482072852L);
      s2 = Double.longBitsToDouble(-4628199223918090387L);
      s1 = Double.longBitsToDouble(4607182418589157889L);
      k1 = Double.longBitsToDouble(-4628199217061079959L);
      k2 = Double.longBitsToDouble(4575957461383549981L);
      k3 = Double.longBitsToDouble(-4671919876307284301L);
      k4 = Double.longBitsToDouble(4523617213632129738L);
      k5 = Double.longBitsToDouble(-4730215344060517252L);
      k6 = Double.longBitsToDouble(4460268259291226124L);
      k7 = Double.longBitsToDouble(-4798040743777455072L);
   }
}
