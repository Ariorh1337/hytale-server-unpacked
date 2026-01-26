/*
 * Decompiled with CFR 0.152.
 */
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
        BouncyCastleUtil.ensureLoaded();
        return unavailabilityCauseBcProv == null;
    }

    public static boolean isBcPkixAvailable() {
        BouncyCastleUtil.ensureLoaded();
        return unavailabilityCauseBcPkix == null;
    }

    public static boolean isBcTlsAvailable() {
        BouncyCastleUtil.ensureLoaded();
        return unavailabilityCauseBcTls == null;
    }

    public static Throwable unavailabilityCauseBcProv() {
        BouncyCastleUtil.ensureLoaded();
        return unavailabilityCauseBcProv;
    }

    public static Throwable unavailabilityCauseBcPkix() {
        BouncyCastleUtil.ensureLoaded();
        return unavailabilityCauseBcPkix;
    }

    public static Throwable unavailabilityCauseBcTls() {
        BouncyCastleUtil.ensureLoaded();
        return unavailabilityCauseBcTls;
    }

    public static boolean isBcJsseInUse(SSLEngine engine) {
        BouncyCastleUtil.ensureLoaded();
        Class<? extends SSLEngine> bcEngineClass = bcSSLEngineClass;
        return bcEngineClass != null && bcEngineClass.isInstance(engine);
    }

    public static Provider getBcProviderJce() {
        BouncyCastleUtil.ensureLoaded();
        Throwable cause = unavailabilityCauseBcProv;
        Provider provider = bcProviderJce;
        if (cause != null || provider == null) {
            throw new IllegalStateException(cause);
        }
        return provider;
    }

    public static Provider getBcProviderJsse() {
        BouncyCastleUtil.ensureLoaded();
        Throwable cause = unavailabilityCauseBcTls;
        Provider provider = bcProviderJsse;
        if (cause != null || provider == null) {
            throw new IllegalStateException(cause);
        }
        return provider;
    }

    public static Class<? extends SSLEngine> getBcSSLEngineClass() {
        BouncyCastleUtil.ensureLoaded();
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
            BouncyCastleUtil.tryLoading();
        }
    }

    private static void tryLoading() {
        AccessController.doPrivileged(() -> {
            Provider provider;
            ClassLoader classLoader;
            try {
                Provider provider2 = Security.getProvider(BC_PROVIDER_NAME);
                if (provider2 == null) {
                    provider2 = Security.getProvider(BC_FIPS_PROVIDER_NAME);
                }
                if (provider2 == null) {
                    Class<?> bcProviderClass;
                    ClassLoader classLoader2 = BouncyCastleUtil.class.getClassLoader();
                    try {
                        bcProviderClass = Class.forName(BC_PROVIDER, true, classLoader2);
                    }
                    catch (ClassNotFoundException e) {
                        try {
                            bcProviderClass = Class.forName(BC_FIPS_PROVIDER, true, classLoader2);
                        }
                        catch (ClassNotFoundException ex) {
                            ThrowableUtil.addSuppressed((Throwable)e, ex);
                            throw e;
                        }
                    }
                    provider2 = (Provider)bcProviderClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                bcProviderJce = provider2;
                logger.debug("Bouncy Castle provider available");
            }
            catch (Throwable e) {
                logger.debug("Cannot load Bouncy Castle provider", e);
                unavailabilityCauseBcProv = e;
            }
            try {
                classLoader = BouncyCastleUtil.class.getClassLoader();
                provider = bcProviderJce;
                if (provider != null) {
                    classLoader = provider.getClass().getClassLoader();
                }
                Class.forName(BC_PEMPARSER, true, classLoader);
                logger.debug("Bouncy Castle PKIX available");
            }
            catch (Throwable e) {
                logger.debug("Cannot load Bouncy Castle PKIX", e);
                unavailabilityCauseBcPkix = e;
            }
            try {
                classLoader = BouncyCastleUtil.class.getClassLoader();
                provider = Security.getProvider(BC_JSSE_PROVIDER_NAME);
                if (provider != null) {
                    classLoader = provider.getClass().getClassLoader();
                } else {
                    Class<?> providerClass = Class.forName(BC_JSSE_PROVIDER, true, classLoader);
                    provider = (Provider)providerClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                bcSSLEngineClass = Class.forName(BC_JSSE_SSLENGINE, true, classLoader);
                Class.forName(BC_JSSE_ALPN_SELECTOR, true, classLoader);
                bcProviderJsse = provider;
                logger.debug("Bouncy Castle JSSE available");
            }
            catch (Throwable e) {
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

