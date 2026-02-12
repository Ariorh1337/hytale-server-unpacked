package org.joml;

import java.text.NumberFormat;

public final class Runtime {
   public static final boolean HAS_floatToRawIntBits = hasFloatToRawIntBits();
   public static final boolean HAS_doubleToRawLongBits = hasDoubleToRawLongBits();
   public static final boolean HAS_Long_rotateLeft = hasLongRotateLeft();
   public static final boolean HAS_Math_fma = Options.USE_MATH_FMA && hasMathFma();

   private static boolean hasMathFma() {
      try {
         java.lang.Math.class.getDeclaredMethod("fma", float.class, float.class, float.class);
         return true;
      } catch (NoSuchMethodException e) {
         return false;
      }
   }

   private Runtime() {
   }

   private static boolean hasFloatToRawIntBits() {
      try {
         Float.class.getDeclaredMethod("floatToRawIntBits", float.class);
         return true;
      } catch (NoSuchMethodException e) {
         return false;
      }
   }

   private static boolean hasDoubleToRawLongBits() {
      try {
         Double.class.getDeclaredMethod("doubleToRawLongBits", double.class);
         return true;
      } catch (NoSuchMethodException e) {
         return false;
      }
   }

   private static boolean hasLongRotateLeft() {
      try {
         Long.class.getDeclaredMethod("rotateLeft", long.class, int.class);
         return true;
      } catch (NoSuchMethodException e) {
         return false;
      }
   }

   public static int floatToIntBits(float flt) {
      return HAS_floatToRawIntBits ? floatToIntBits1_3(flt) : floatToIntBits1_2(flt);
   }

   private static int floatToIntBits1_3(float flt) {
      return Float.floatToRawIntBits(flt);
   }

   private static int floatToIntBits1_2(float flt) {
      return Float.floatToIntBits(flt);
   }

   public static long doubleToLongBits(double dbl) {
      return HAS_doubleToRawLongBits ? doubleToLongBits1_3(dbl) : doubleToLongBits1_2(dbl);
   }

   private static long doubleToLongBits1_3(double dbl) {
      return Double.doubleToRawLongBits(dbl);
   }

   private static long doubleToLongBits1_2(double dbl) {
      return Double.doubleToLongBits(dbl);
   }

   public static String formatNumbers(String str) {
      StringBuffer res = new StringBuffer();
      int eIndex = Integer.MIN_VALUE;

      for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c == 'E') {
            eIndex = i;
         } else {
            if (c == ' ' && eIndex == i - 1) {
               res.append('+');
               continue;
            }

            if (Character.isDigit(c) && eIndex == i - 1) {
               res.append('+');
            }
         }

         res.append(c);
      }

      return res.toString();
   }

   public static String format(double number, NumberFormat format) {
      if (Double.isNaN(number)) {
         return padLeft(format, " NaN");
      } else {
         return Double.isInfinite(number) ? padLeft(format, number > 0.0 ? " +Inf" : " -Inf") : format.format(number);
      }
   }

   private static String padLeft(NumberFormat format, String str) {
      int len = format.format(0.0).length();
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < len - str.length() + 1; i++) {
         sb.append(" ");
      }

      return sb.append(str).toString();
   }

   public static boolean equals(float a, float b, float delta) {
      return Float.floatToIntBits(a) == Float.floatToIntBits(b) || Math.abs(a - b) <= delta;
   }

   public static boolean equals(double a, double b, double delta) {
      return Double.doubleToLongBits(a) == Double.doubleToLongBits(b) || Math.abs(a - b) <= delta;
   }
}
