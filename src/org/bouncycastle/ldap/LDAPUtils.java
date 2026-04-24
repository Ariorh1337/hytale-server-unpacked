package org.bouncycastle.ldap;

import org.bouncycastle.util.Strings;

public class LDAPUtils {
   private static String[] FILTER_ESCAPE_TABLE = new String[93];

   public static String parseDN(String var0, String var1) {
      String var2 = var0;
      int var3 = Strings.toLowerCase(var2).indexOf(Strings.toLowerCase(var1));
      if (var3 == -1) {
         return "";
      }

      var2 = var2.substring(var3 + var1.length());
      int var4 = var2.indexOf(44);
      if (var4 == -1) {
         var4 = var2.length();
      }

      while (var2.charAt(var4 - 1) == '\\') {
         var4 = var2.indexOf(44, var4 + 1);
         if (var4 == -1) {
            var4 = var2.length();
         }
      }

      var2 = var2.substring(0, var4);
      var3 = var2.indexOf(61);
      var2 = var2.substring(var3 + 1);
      if (var2.charAt(0) == ' ') {
         var2 = var2.substring(1);
      }

      if (var2.startsWith("\"")) {
         var2 = var2.substring(1);
      }

      if (var2.endsWith("\"")) {
         var2 = var2.substring(0, var2.length() - 1);
      }

      return filterEncode(var2);
   }

   private static String filterEncode(String var0) {
      if (var0 == null) {
         return null;
      }

      StringBuilder var1 = new StringBuilder(var0.length() * 2);
      int var2 = var0.length();

      for (int var3 = 0; var3 < var2; var3++) {
         char var4 = var0.charAt(var3);
         if (var4 < FILTER_ESCAPE_TABLE.length) {
            var1.append(FILTER_ESCAPE_TABLE[var4]);
         } else {
            var1.append(var4);
         }
      }

      return var1.toString();
   }

   static {
      for (char var0 = 0; var0 < FILTER_ESCAPE_TABLE.length; var0++) {
         FILTER_ESCAPE_TABLE[var0] = String.valueOf(var0);
      }

      FILTER_ESCAPE_TABLE[42] = "\\2a";
      FILTER_ESCAPE_TABLE[40] = "\\28";
      FILTER_ESCAPE_TABLE[41] = "\\29";
      FILTER_ESCAPE_TABLE[92] = "\\5c";
      FILTER_ESCAPE_TABLE[0] = "\\00";
   }
}
