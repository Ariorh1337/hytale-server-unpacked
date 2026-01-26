/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContextOption;
import io.netty.handler.ssl.SslUtils;
import java.security.Provider;

public enum SslProvider {
    JDK,
    OPENSSL,
    OPENSSL_REFCNT;


    public static boolean isAlpnSupported(SslProvider provider) {
        switch (provider) {
            case JDK: {
                return JdkAlpnApplicationProtocolNegotiator.isAlpnSupported();
            }
            case OPENSSL: 
            case OPENSSL_REFCNT: {
                return OpenSsl.isAlpnSupported();
            }
        }
        throw new Error("Unexpected SslProvider: " + (Object)((Object)provider));
    }

    public static boolean isTlsv13Supported(SslProvider sslProvider) {
        return SslProvider.isTlsv13Supported(sslProvider, null);
    }

    public static boolean isTlsv13Supported(SslProvider sslProvider, Provider provider) {
        switch (sslProvider) {
            case JDK: {
                return SslUtils.isTLSv13SupportedByJDK(provider);
            }
            case OPENSSL: 
            case OPENSSL_REFCNT: {
                return OpenSsl.isTlsv13Supported();
            }
        }
        throw new Error("Unexpected SslProvider: " + (Object)((Object)sslProvider));
    }

    public static boolean isOptionSupported(SslProvider sslProvider, SslContextOption<?> option) {
        switch (sslProvider) {
            case JDK: {
                return false;
            }
            case OPENSSL: 
            case OPENSSL_REFCNT: {
                return OpenSsl.isOptionSupported(option);
            }
        }
        throw new Error("Unexpected SslProvider: " + (Object)((Object)sslProvider));
    }

    static boolean isTlsv13EnabledByDefault(SslProvider sslProvider, Provider provider) {
        switch (sslProvider) {
            case JDK: {
                return SslUtils.isTLSv13EnabledByJDK(provider);
            }
            case OPENSSL: 
            case OPENSSL_REFCNT: {
                return OpenSsl.isTlsv13Supported();
            }
        }
        throw new Error("Unexpected SslProvider: " + (Object)((Object)sslProvider));
    }
}

