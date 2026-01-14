package io.netty.handler.codec.compression;

import com.github.luben.zstd.util.Native;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class Zstd {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Zstd.class);
   private static final Throwable cause;

   public static boolean isAvailable() {
      return cause == null;
   }

   public static void ensureAvailability() throws Throwable {
      if (cause != null) {
         throw cause;
      }
   }

   public static Throwable cause() {
      return cause;
   }

   private Zstd() {
   }

   static {
      Throwable t = null;

      try {
         Class.forName("com.github.luben.zstd.Zstd", false, PlatformDependent.getClassLoader(Zstd.class));
      } catch (ClassNotFoundException e) {
         t = e;
         logger.debug("zstd-jni not in the classpath; Zstd support will be unavailable.");
      }

      if (t == null) {
         try {
            Native.load();
         } catch (Throwable e) {
            t = e;
            logger.debug("Failed to load zstd-jni; Zstd support will be unavailable.", e);
         }
      }

      cause = t;
   }
}
