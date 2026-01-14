package io.netty.handler.ssl.util;

import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.Provider;
import java.security.Security;
import javax.net.ssl.SSLEngine;

public final class BouncyCastleUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(BouncyCastleUtil.class);
   private static final String BC_PROVIDER_NAME = "BC";
   private static final String BC_PROVIDER = "org.bouncycastle.jce.provider.BouncyCastleProvider";
   private static final String BC_FIPS_PROVIDER_NAME = "BCFIPS";
   private static final String BC_FIPS_PROVIDER = "org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider";
   private static final String BC_JSSE_PROVIDER_NAME = "BCJSSE";
   private static final String BC_JSSE_PROVIDER = "org.bouncycastle.jsse.provider.BouncyCastleJsseProvider";
   private static final String BC_PEMPARSER = "org.bouncycastle.openssl.PEMParser";
   private static final String BC_JSSE_SSLENGINE = "org.bouncycastle.jsse.BCSSLEngine";
   private static final String BC_JSSE_ALPN_SELECTOR = "org.bouncycastle.jsse.BCApplicationProtocolSelector";
   private static volatile Throwable unavailabilityCauseBcProv;
   private static volatile Throwable unavailabilityCauseBcPkix;
   private static volatile Throwable unavailabilityCauseBcTls;
   private static volatile Provider bcProviderJce;
   private static volatile Provider bcProviderJsse;
   private static volatile Class<? extends SSLEngine> bcSSLEngineClass;
   private static volatile boolean attemptedLoading;

   public static boolean isBcProvAvailable() {
      ensureLoaded();
      return unavailabilityCauseBcProv == null;
   }

   public static boolean isBcPkixAvailable() {
      ensureLoaded();
      return unavailabilityCauseBcPkix == null;
   }

   public static boolean isBcTlsAvailable() {
      ensureLoaded();
      return unavailabilityCauseBcTls == null;
   }

   public static Throwable unavailabilityCauseBcProv() {
      ensureLoaded();
      return unavailabilityCauseBcProv;
   }

   public static Throwable unavailabilityCauseBcPkix() {
      ensureLoaded();
      return unavailabilityCauseBcPkix;
   }

   public static Throwable unavailabilityCauseBcTls() {
      ensureLoaded();
      return unavailabilityCauseBcTls;
   }

   public static boolean isBcJsseInUse(SSLEngine engine) {
      ensureLoaded();
      Class<? extends SSLEngine> bcEngineClass = bcSSLEngineClass;
      return bcEngineClass != null && bcEngineClass.isInstance(engine);
   }

   public static Provider getBcProviderJce() {
      ensureLoaded();
      Throwable cause = unavailabilityCauseBcProv;
      Provider provider = bcProviderJce;
      if (cause == null && provider != null) {
         return provider;
      } else {
         throw new IllegalStateException(cause);
      }
   }

   public static Provider getBcProviderJsse() {
      ensureLoaded();
      Throwable cause = unavailabilityCauseBcTls;
      Provider provider = bcProviderJsse;
      if (cause == null && provider != null) {
         return provider;
      } else {
         throw new IllegalStateException(cause);
      }
   }

   public static Class<? extends SSLEngine> getBcSSLEngineClass() {
      ensureLoaded();
      return bcSSLEngineClass;
   }

   static void reset() {
      attemptedLoading = false;
      unavailabilityCauseBcProv = null;
      unavailabilityCauseBcPkix = null;
      unavailabilityCauseBcTls = null;
      bcProviderJce = null;
      bcProviderJsse = null;
      bcSSLEngineClass = null;
   }

   private static void ensureLoaded() {
      if (!attemptedLoading) {
         tryLoading();
      }
   }

   private static void tryLoading() {
      AccessController.doPrivileged(() -> {
         try {
            Provider provider = Security.getProvider("BC");
            if (provider == null) {
               provider = Security.getProvider("BCFIPS");
            }

            if (provider == null) {
               ClassLoader classLoader = BouncyCastleUtil.class.getClassLoader();

               Class<Provider> bcProviderClass;
               try {
                  bcProviderClass = (Class<Provider>)Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider", true, classLoader);
               } catch (ClassNotFoundException e) {
                  try {
                     bcProviderClass = (Class<Provider>)Class.forName("org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider", true, classLoader);
                  } catch (ClassNotFoundException ex) {
                     ThrowableUtil.addSuppressed(e, ex);
                     throw e;
                  }
               }

               provider = bcProviderClass.getConstructor().newInstance();
            }

            bcProviderJce = provider;
            logger.debug("Bouncy Castle provider available");
         } catch (Throwable e) {
            logger.debug("Cannot load Bouncy Castle provider", e);
            unavailabilityCauseBcProv = e;
         }

         try {
            ClassLoader classLoader = BouncyCastleUtil.class.getClassLoader();
            Provider provider = bcProviderJce;
            if (provider != null) {
               classLoader = provider.getClass().getClassLoader();
            }

            Class.forName("org.bouncycastle.openssl.PEMParser", true, classLoader);
            logger.debug("Bouncy Castle PKIX available");
         } catch (Throwable e) {
            logger.debug("Cannot load Bouncy Castle PKIX", e);
            unavailabilityCauseBcPkix = e;
         }

         try {
            ClassLoader classLoader = BouncyCastleUtil.class.getClassLoader();
            Provider provider = Security.getProvider("BCJSSE");
            if (provider != null) {
               classLoader = provider.getClass().getClassLoader();
            } else {
               Class<?> providerClass = Class.forName("org.bouncycastle.jsse.provider.BouncyCastleJsseProvider", true, classLoader);
               provider = (Provider)providerClass.getConstructor().newInstance();
            }

            bcSSLEngineClass = (Class<? extends SSLEngine>)Class.forName("org.bouncycastle.jsse.BCSSLEngine", true, classLoader);
            Class.forName("org.bouncycastle.jsse.BCApplicationProtocolSelector", true, classLoader);
            bcProviderJsse = provider;
            logger.debug("Bouncy Castle JSSE available");
         } catch (Throwable e) {
            logger.debug("Cannot load Bouncy Castle TLS", e);
            unavailabilityCauseBcTls = e;
         }

         attemptedLoading = true;
         return null;
      });
   }

   private BouncyCastleUtil() {
   }
}
