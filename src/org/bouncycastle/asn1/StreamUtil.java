package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import org.bouncycastle.util.Properties;

class StreamUtil {
   static final String MAX_LIMIT = "org.bouncycastle.asn1.max_limit";

   static int findLimit(InputStream var0) {
      if (var0 instanceof LimitedInputStream) {
         return ((LimitedInputStream)var0).getLimit();
      }

      if (var0 instanceof ASN1InputStream) {
         return ((ASN1InputStream)var0).getLimit();
      }

      if (var0 instanceof ByteArrayInputStream) {
         return ((ByteArrayInputStream)var0).available();
      }

      if (var0 instanceof FileInputStream) {
         try {
            FileChannel var1 = ((FileInputStream)var0).getChannel();
            long var2 = var1 != null ? var1.size() : 2147483647L;
            if (var2 < 2147483647L) {
               return (int)var2;
            }
         } catch (IOException var4) {
         }
      }

      String var5 = Properties.getPropertyValue("org.bouncycastle.asn1.max_limit");
      if (var5 != null) {
         switch (var5.charAt(var5.length() - 1)) {
            case 'g':
               return Integer.parseInt(var5.substring(0, var5.length() - 1)) * 1024 * 1024 * 1024;
            case 'k':
               return Integer.parseInt(var5.substring(0, var5.length() - 1)) * 1024;
            case 'm':
               return Integer.parseInt(var5.substring(0, var5.length() - 1)) * 1024 * 1024;
            default:
               return Integer.parseInt(var5);
         }
      } else {
         return getMaxMemory();
      }
   }

   private static int getMaxMemory() {
      long var0 = Runtime.getRuntime().maxMemory();
      return var0 > 2147483647L ? Integer.MAX_VALUE : (int)var0;
   }
}
